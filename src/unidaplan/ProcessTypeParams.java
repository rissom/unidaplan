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

public class ProcessTypeParams extends HttpServlet {
	private static final long serialVersionUID = 1L;
   
	
    public ProcessTypeParams() {
        super();
    }

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
		throws ServletException, IOException {
		  
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
		
		int paramgroupid=0;
		request.setCharacterEncoding("utf-8");
	    response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    PrintWriter out = response.getWriter(); 
	  	  	try  {
	  	  		paramgroupid=Integer.parseInt(request.getParameter("paramgrpid")); 
	  	  	}
	  	  	catch (Exception e1) {
	  	  		System.err.print("ProcessTypeParameters: no paramgroupID given!");
	  	  		e1.printStackTrace();
	  	  	}
		PreparedStatement pStmt = null; 	// Declare variables
	    JSONObject paramGrp= null;
	    JSONArray parameterGrps= null;		
	    JSONArray processTypeGrps= null;
	 	DBconnection dBconn=new DBconnection(); // New connection to the database
	 	ArrayList<String> stringkeys = new ArrayList<String>(); 
		 	
	    try{
		 	dBconn.startDB();

		 	// check if admin
		 	if (Unidatoolkit.userHasAdminRights(userID, dBconn)){
		 	
	 			pStmt = dBconn.conn.prepareStatement(	
				     "SELECT "
				   + "  id,"
				   + "  processtype,"
				   + "  stringkey AS name "
				   + "FROM p_parametergrps "
				   + "WHERE p_parametergrps.id = ?");
		 		pStmt.setInt(1, paramgroupid);
	 			paramGrp = dBconn.jsonObjectFromPreparedStmt(pStmt); // get ResultSet from the database using the query
	 			pStmt.close();
	 			if (paramGrp.length() > 0){
		           	stringkeys.add(Integer.toString(paramGrp.getInt("name")));  // check if the parameter can be deleted. (No, if corresponding data exists).
		           	pStmt = dBconn.conn.prepareStatement(
		     		  	    "SELECT "
		     		  	  + "  p_parameters.id,"
		     		  	  + "  compulsory,"
		     		  	  + "  formula,"
		     		  	  + "  hidden,"
		     		  	  + "  pos,"
		     		  	  + "  definition,"
		     		  	  + "  COALESCE (p_parameters.stringkeyname,paramdef.stringkeyname) AS name, "
		     		  	  + "  (blabla.count) IS NULL as deletable,"
		     		  	  + "  stringkeyunit " 
		     		  	  +"FROM p_parameters " 
		     		  	  +"JOIN paramdef ON (definition = paramdef.id)"
		     		  	  +"LEFT JOIN "
					  	  +"( "
						  +"  SELECT "
						  + "   count(a.id),"
						  + "   parameterid "
						  +"  FROM processdata a GROUP BY parameterid "
						  +") AS blabla ON blabla.parameterid = p_parameters.id "
						  +"WHERE parametergroup=? AND NOT definition IN (1,8,10)"); // status, processnumber and date cannot be edited
			 		pStmt.setInt(1, paramgroupid);
					processTypeGrps = dBconn.jsonArrayFromPreparedStmt(pStmt); // get ResultSet from the database using the query
					if (processTypeGrps.length() > 0) {
		           		for (int j = 0; j < processTypeGrps.length(); j++) {
		           			stringkeys.add(Integer.toString(processTypeGrps.getJSONObject(j).getInt("name")));
		           			if (processTypeGrps.getJSONObject(j).has("stringkeyunit")){
		           				stringkeys.add(Integer.toString(processTypeGrps.getJSONObject(j).getInt("stringkeyunit")));
		           			}
		           		}
		           	}		
	 			} else {
	 				System.err.println("not found");
	 			}
	   		
			
		        JSONObject answer=new JSONObject();
		        answer=paramGrp;
		        answer.put("parameters", processTypeGrps);
		        answer.put("parametergrps",parameterGrps);
		        answer.put("strings", dBconn.getStrings(stringkeys));
		        out.println(answer.toString());
		 	}else{
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
