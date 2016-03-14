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
	    int sampleTypeID=0;
	  	try {
	  		sampleTypeID=Integer.parseInt(request.getParameter("sampletypeid")); 
	    } catch (Exception e1) {
	   		// return parameters of the first sampletype
	   	}
	  	
		PreparedStatement pStmt = null; 	// Declare variables
	    JSONArray parameterGrps= null;		
	    JSONArray parameters= null;
	 	DBconnection dBconn=new DBconnection(); // New connection to the database
	 	ArrayList<String> stringkeys = new ArrayList<String>(); 
        JSONObject answer=new JSONObject();

		 	
	    try{
		 	dBconn.startDB();
		 	
		 	// if the sampletypeid is 0, set it to the first existing sample type 
		 	if (sampleTypeID==0){
		 		pStmt = dBconn.conn.prepareStatement(
					  	   "SELECT id FROM objecttypes "
						  +"ORDER BY position "
						  +"LIMIT 1");
		 		sampleTypeID = dBconn.getSingleIntValue(pStmt);
		 		if (sampleTypeID<1) {
		 			System.err.println("No sampletypes in database!");
					response.setStatus(404);
		 			throw new Exception();
		 		} else {
		 			answer.put("sampletype", sampleTypeID);
		 		}
		 		
		 	}
		 	
		 	// get the parametergroups
		 	pStmt = dBconn.conn.prepareStatement(
				  	   "SELECT id,pos,stringkey FROM ot_parametergrps "
					  +"WHERE (ot_parametergrps.ot_id=?) ");
   			pStmt.setInt(1, sampleTypeID);
   			parameterGrps=dBconn.jsonArrayFromPreparedStmt(pStmt); 
   			if (parameterGrps.length()>0) {
           		for (int j=0; j<parameterGrps.length();j++) {
           			if (parameterGrps.getJSONObject(j).has("stringkey")){
           				stringkeys.add(Integer.toString(parameterGrps.getJSONObject(j).getInt("stringkey")));
           			}
           		}
           	}	
   			
   			
   			// get the parameters
           	pStmt = dBconn.conn.prepareStatement(
     		  	   "SELECT ot_parameters.id, compulsory, formula, hidden, pos, definition, ot_parameters.stringkeyname as name, "
     		  	  + "stringkeyunit, parametergroup " 
     		  	  +"FROM ot_parameters " 
     		  	  +"JOIN paramdef ON (definition=paramdef.id)"
				  +"WHERE objecttypesid=?"); // status, processnumber and date cannot be edited
	 		pStmt.setInt(1, sampleTypeID);
			parameters=dBconn.jsonArrayFromPreparedStmt(pStmt); // get ResultSet from the database using the query
			
			if (parameters.length()>0) {
				// get all the stringkeys from the parameters
           		for (int j=0; j<parameters.length();j++) {
           			JSONObject parameter=parameters.getJSONObject(j);
           			stringkeys.add(Integer.toString(parameter.getInt("name")));
           			if (parameter.has("stringkeyunit")){
           				stringkeys.add(Integer.toString(parameters.getJSONObject(j).getInt("stringkeyunit")));
           			}
           		}
           		
//    			// assign parameters to parametergroups (not needed any more)
//           		for (int k=0; k<parameterGrps.length();k++){
//           			JSONObject parameterGrp=parameterGrps.getJSONObject(k);
//           			JSONArray grpParameters = new JSONArray();
//               		for (int j=0; j<parameters.length();j++) {
//               			JSONObject parameter=parameters.getJSONObject(j);
//	           			if (parameterGrp.getInt("id")==parameter.getInt("parametergroup")){
//	           				grpParameters.put(parameter);
//	           			}
//               		}
//           			parameterGrp.put("parameters", grpParameters);
//           		}
           	}
					
	        answer.put("parametergrps",parameterGrps);
	        answer.put("parameters",parameters);
	        answer.put("strings", dBconn.getStrings(stringkeys));
	        out.println(answer.toString());
	    } catch (SQLException e) {
			System.err.println("AllSampleTypeParameters: SQL Error");
			response.setStatus(404);
	    } catch (JSONException e) {
			System.err.println("AllSampleTypeParameters: JSON Error");
			e.printStackTrace();
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
