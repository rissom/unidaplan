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
import org.json.JSONException;
import org.json.JSONObject;

public class AllSampleTypeParams extends HttpServlet {
	private static final long serialVersionUID = 1L;
   
	
    public AllSampleTypeParams() {
        super();
    }

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
		throws ServletException, IOException {
		  
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
		userID=userID+1;
		userID=userID-1;
		request.setCharacterEncoding("utf-8");
	    response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    PrintWriter out = response.getWriter(); 
	    int sampleTypeId=-1;
	    try {
	    	JSONObject jsonIn = new JSONObject(request.getReader().readLine());
	    	sampleTypeId = jsonIn.getInt("sampletypeid");
		} catch (JSONException e) {
			System.err.println("UpdateProcessTypeData: Input is not valid JSON");
		}  	
		PreparedStatement pStmt = null; 	// Declare variables
	    JSONArray parameterGrps= null;		
	    JSONArray processTypeGrps= null;
	 	DBconnection dBconn=new DBconnection(); // New connection to the database
	 	ArrayList<String> stringkeys = new ArrayList<String>(); 
		 	
	    try{
		 	dBconn.startDB();
//	 		stringkeys.add(Integer.toString(paramGrp.getInt("name"))); 
           	pStmt = dBconn.conn.prepareStatement(
     		  	   "SELECT ot_parameters.id, compulsory, formula, hidden, pos, definition, ot_parameters.stringkeyname as name, "
     		  	  + "(blabla.count) IS NULL as deletable, stringkeyunit " 
     		  	  +"FROM ot_parameters " 
     		  	  +"JOIN paramdef ON (definition=paramdef.id)"
				  +"WHERE objecttypesid=?"); // status, processnumber and date cannot be edited
	 		pStmt.setInt(1, sampleTypeId);
			processTypeGrps=dBconn.jsonArrayFromPreparedStmt(pStmt); // get ResultSet from the database using the query
			if (processTypeGrps.length()>0) {
           		for (int j=0; j<processTypeGrps.length();j++) {
           			stringkeys.add(Integer.toString(processTypeGrps.getJSONObject(j).getInt("name")));
           			if (processTypeGrps.getJSONObject(j).has("stringkeyunit")){
           				stringkeys.add(Integer.toString(processTypeGrps.getJSONObject(j).getInt("stringkeyunit")));
           			}
           		}
           	}		
		
	        JSONObject answer=new JSONObject();
	        answer.put("parameters", processTypeGrps);
	        answer.put("parametergrps",parameterGrps);
	        answer.put("strings", dBconn.getStrings(stringkeys));
	        out.println(answer.toString());
	    } catch (SQLException eS) {
			System.err.println("AllSampleTypeParameters: SQL Error");
			response.setStatus(404);
	    } catch (Exception e) {
			System.err.println("AllSampleTypeParameters: Some Error, probably JSON");
			response.setStatus(404);
	    } finally {
			try{
				if (pStmt != null) { 
					try {
		        	  	pStmt.close();
		        	} catch (SQLException e) {
						System.err.println("AllSampleTypeParameters: SQL Error ");
		        	} 
				}
		    	if (dBconn.conn != null) { 
		    		dBconn.closeDB();  // close the database 
		    	}
	        } catch (Exception e) {
				System.err.println("AllSampleTypeParameters: Some Error closing the database");
				response.setStatus(404);
	        }
        }       
	}
}
