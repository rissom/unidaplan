package unidaplan;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

public class ProcessTypeParameters extends HttpServlet {
	private static final long serialVersionUID = 1L;
   
	
    public ProcessTypeParameters() {
        super();
    }

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
		throws ServletException, IOException {
		  
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
		userID=userID+1;
		userID=userID-1;
		int processTypeID=0;
		request.setCharacterEncoding("utf-8");
	    response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    PrintWriter out = response.getWriter(); 
	  	  	try  {
	  	  		processTypeID=Integer.parseInt(request.getParameter("processtypeid")); 
	  	  	}
	  	  	catch (Exception e1) {
	  	  		System.err.print("ProcessTypeParameters: no processTypeID given!");
	  	  		e1.printStackTrace();
	  	  	}
		PreparedStatement pStmt = null; 	// Declare variables
	    JSONObject processType= null;
	    JSONArray parameterGrps= null;		
	 	DBconnection dBconn=new DBconnection(); // New connection to the database
	 	dBconn.startDB();
	 	ArrayList<String> stringkeys = new ArrayList<String>(); 
		 	
	    try{
 			pStmt = dBconn.conn.prepareStatement(	
			  "SELECT * FROM processtypes WHERE id=?");
 			pStmt.setInt(1, processTypeID);
 			processType=dBconn.jsonObjectFromPreparedStmt(pStmt); // get ResultSet from the database using the query
 			pStmt.close();
           	stringkeys.add(Integer.toString(processType.getInt("name")));
           	stringkeys.add(Integer.toString(processType.getInt("description")));
   			pStmt = dBconn.conn.prepareStatement(
   					"SELECT pos,stringkey FROM p_parametergrps "
					+"WHERE (p_parametergrps.pt_id=?) "
					+"ORDER BY pos");
   			pStmt.setInt(1, processTypeID);
   			parameterGrps=dBconn.jsonArrayFromPreparedStmt(pStmt); // get ResultSet from the database using the query

           	if (parameterGrps.length()>0) {
           		for (int j=0; j<parameterGrps.length();j++) {
           			stringkeys.add(Integer.toString(parameterGrps.getJSONObject(j).getInt("stringkey")));
           		}
           	}
       		String query="SELECT id,string_key,language,value FROM Stringtable WHERE string_key=ANY('{";
           	
   			StringBuilder buff = new StringBuilder(); // join numbers with commas
	        String sep = "";
	        for (String str : stringkeys) {
	        	buff.append(sep);
	           	buff.append(str);
	           	sep = ",";
		    }
	        query+= buff.toString() + "}'::int[])";
	        JSONArray theStrings=dBconn.jsonfromquery(query);
	        JSONObject jsAvailable=new JSONObject();
	        jsAvailable.put("strings", theStrings);
	        out.println(jsAvailable.toString());
	    } catch (SQLException eS) {
			System.err.println("ProcessTypeParameters: SQL Error");
			eS.printStackTrace();
		} catch (Exception e) {
			System.err.println("ProcessTypeParameters: Some Error, probably JSON");
			e.printStackTrace();
		} finally {
		try{
			if (pStmt != null) { 
				try {
	        	  	pStmt.close();
	        	} catch (SQLException e) {
	        	} 
			}
	    	if (dBconn.conn != null) { 
	    		dBconn.closeDB();  // close the database 
	    	}
	        } catch (Exception e) {
				System.err.println("ProcessTypeParameters: Some Error closing the database");
				e.printStackTrace();
		   	}
        }       
	}
}