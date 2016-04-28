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

public class ProcessTypeParamGrps extends HttpServlet {
	private static final long serialVersionUID = 1L;
   
	
    public ProcessTypeParamGrps() {
        super();
    }

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
		throws ServletException, IOException {
		  
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
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
	    JSONArray processTypeGrps= null;
	 	DBconnection dBconn=new DBconnection(); // New connection to the database
	 	ArrayList<String> stringkeys = new ArrayList<String>(); 
		 	
	    try{
		 	dBconn.startDB();
		 	
		 	// check if admin
		 	if (Unidatoolkit.userHasAdminRights(userID, dBconn)){
		 	
	 			pStmt = dBconn.conn.prepareStatement(	
				   "SELECT processtypes.id, processtypes.position, ptgroup, processtypes.name, "
	 			  +"description, processtypes.lastuser "
				  +"FROM processtypes "
				  +"WHERE processtypes.id=?");
		 		pStmt.setInt(1, processTypeID);
	 			processType=dBconn.jsonObjectFromPreparedStmt(pStmt);
	 			pStmt.close();
	           	stringkeys.add(Integer.toString(processType.getInt("name")));
	           	stringkeys.add(Integer.toString(processType.getInt("description")));
	           	
	           	pStmt = dBconn.conn.prepareStatement( // not implemented in frontend yet
	     		  	   "SELECT id, position, name FROM processtypegroups");
				processTypeGrps=dBconn.jsonArrayFromPreparedStmt(pStmt);
				if (processTypeGrps.length()>0) {
	           		for (int j=0; j<processTypeGrps.length();j++) {
	           			stringkeys.add(Integer.toString(processTypeGrps.getJSONObject(j).getInt("name")));
	           		}
	           	}		
	           	
	   			pStmt = dBconn.conn.prepareStatement(
	   				"SELECT pgs.id,pgs.pos,pgs.stringkey, count(pp.id)=0 AS deletable "
					+"FROM p_parametergrps pgs "
					+"LEFT JOIN p_parameters pp ON pgs.id=pp.parametergroup "
					+"WHERE pgs.processtype=? "
					+"GROUP BY pgs.id ");
	   			pStmt.setInt(1, processTypeID);
	   			parameterGrps=dBconn.jsonArrayFromPreparedStmt(pStmt); // get ResultSet from the database using the query
	
	           	if (parameterGrps.length()>0) {
	           		for (int j=0; j<parameterGrps.length();j++) {
	           			stringkeys.add(Integer.toString(parameterGrps.getJSONObject(j).getInt("stringkey")));
	           		}
	           	}
	     
		        JSONObject answer=new JSONObject();
		        answer=processType;
		        answer.put("processtypegrps", processTypeGrps);
		        answer.put("parametergrps",parameterGrps);
		        answer.put("strings", dBconn.getStrings(stringkeys));
		        out.println(answer.toString());
		 	} else {
		 		response.setStatus(401);
		 	}
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
					System.err.println("ProcessTypeParameters: SQL Error ");
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
