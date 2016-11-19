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

public class SampleTypeParamGrps extends HttpServlet {
	private static final long serialVersionUID = 1L;
   
	
    public SampleTypeParamGrps() {
        super();
    }

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
		throws ServletException, IOException {
		  
		Authentificator authentificator = new Authentificator();
		int userID = authentificator.GetUserID(request,response);
		int sampleTypeID = 0;
		request.setCharacterEncoding("utf-8");
	    response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    PrintWriter out = response.getWriter(); 
	  	  	try  {
	  	  		sampleTypeID = Integer.parseInt(request.getParameter("sampletypeid")); 
	  	  	}
	  	  	catch (Exception e1) {
	  	  		System.err.print("SampleTypeParameters: no sampleTypeID given!");
	  	  		e1.printStackTrace();
	  	  	}
		PreparedStatement pStmt = null; 	// Declare variables
	    JSONObject sampleType= null;
	    JSONArray parameterGrps= null;		
	    JSONArray titleParameters=null;
	    JSONArray sampleTypeGrps= null;
	 	DBconnection dBconn=new DBconnection(); // New connection to the database
	 	ArrayList<String> stringkeys = new ArrayList<String>(); 
		 	
	    try{
		 	dBconn.startDB();
		 	
		 	// check if admin
		 	if (Unidatoolkit.userHasAdminRights(userID, dBconn)){
		 	
	 			pStmt = dBconn.conn.prepareStatement(	
				   "SELECT "
				   + "  objecttypes.id, "
				   + "  objecttypes.position, "
				   + "  otgrp, "
				   + "  objecttypes.string_key, "
	 			   + "  description, "
	 			   + "  objecttypes.lastuser "
				   + "FROM objecttypes "
				   + "WHERE objecttypes.id = ?");
		 		pStmt.setInt(1, sampleTypeID);
	 			sampleType = dBconn.jsonObjectFromPreparedStmt(pStmt); 
	 			// get ResultSet from the database using the query
	 			pStmt.close();
	 			if (sampleType.has("string_key")){
	 				stringkeys.add(Integer.toString(sampleType.getInt("string_key")));
	 			}
	 			if (sampleType.has("description")){
	 				stringkeys.add(Integer.toString(sampleType.getInt("description")));
	 			}
	           	
	 			// get all objecttypesgrps
	           	pStmt = dBconn.conn.prepareStatement(
	     		  	   "SELECT id, position, name FROM objecttypesgrp");
				sampleTypeGrps = dBconn.jsonArrayFromPreparedStmt(pStmt); 
				// get ResultSet from the database using the query
				if (sampleTypeGrps.length() > 0) {
	           		for (int j = 0; j < sampleTypeGrps.length(); j++) {
	           			stringkeys.add(Integer.toString(sampleTypeGrps.getJSONObject(j).getInt("name")));
	           		}
	           	}		
	           	
				// get all parametergroups for this sampletype
	   			pStmt = dBconn.conn.prepareStatement(
	   					"SELECT "
	   				  + "  ot_parametergrps.id,"
	   				  + "  ot_parametergrps.pos,"
	   				  + "  ot_parametergrps.stringkey, "
	   				  + "  count(ot_parameters.id) = 0 AS deletable "
	   				  + "FROM ot_parametergrps "
	   				  +	"LEFT JOIN ot_parameters ON ot_parameters.parametergroup = ot_parametergrps.id "
	   				  + "WHERE (ot_parametergrps.ot_id = ?) "
	   				  + "GROUP BY ot_parametergrps.id"); 
	   			pStmt.setInt(1, sampleTypeID);
	   			parameterGrps=dBconn.jsonArrayFromPreparedStmt(pStmt); 
	
	           	if (parameterGrps.length() > 0) {
	           		for (int j = 0; j < parameterGrps.length(); j++) {
	           			stringkeys.add(Integer.toString(parameterGrps.getJSONObject(j).getInt("stringkey")));
	           		}
	           	}
	           	
	        	
				// get the titleparameters
	   			pStmt = dBconn.conn.prepareStatement(
	   					"SELECT "
	   				  + "  otp.id, "
					  + "  max( COALESCE (otp.stringkeyname, pd.stringkeyname)) AS name, "
					  + "  max( COALESCE (otp.description, pd.description)) AS description, "
					  + "  format, "
					  + "  pos, "
					  + "  count(sampledata) = 0 AS deletable "
					  + "FROM  ot_parameters otp "
					  + "JOIN paramdef pd ON (otp.definition = pd.ID) "
					  + "LEFT JOIN sampledata ON ot_parameter_id = otp.id "
					  + "WHERE otp.ObjecttypesID = ? AND otp.ID_FIELD = true "
					  + "GROUP by otp.id, format, pos"); 
	   			pStmt.setInt(1, sampleTypeID);
	   			titleParameters = dBconn.jsonArrayFromPreparedStmt(pStmt); 
	
	           	if (titleParameters.length() > 0) {
	           		for (int j=0; j<titleParameters.length();j++) {
	           			stringkeys.add(Integer.toString(titleParameters.getJSONObject(j).getInt("name")));
	           			stringkeys.add(Integer.toString(titleParameters.getJSONObject(j).getInt("description")));
	           		}
	           	}
	     
		        JSONObject answer = new JSONObject();
		        answer = sampleType;
		        answer.put("titleparameters",titleParameters);
		        answer.put("sampletypegrps", sampleTypeGrps);
		        answer.put("parametergrps",parameterGrps);
		        answer.put("strings", dBconn.getStrings(stringkeys));
		        out.println(answer.toString());
		 	}else{
		 		response.setStatus(401);
		 	}
	    } catch (SQLException eS) {
			System.err.println("SampleTypeParameters: SQL Error");
		} catch (Exception e) {
			System.err.println("SampleTypeParameters: Some Error, probably JSON");
			e.printStackTrace();
		} finally {
		try{
			if (pStmt != null) { 
				try {
	        	  	pStmt.close();
	        	} catch (SQLException e) {
					System.err.println("SampleTypeParameters: SQL Error ");
	        	} 
			}
	    	if (dBconn.conn != null) { 
	    		dBconn.closeDB();  // close the database 
	    	}
	        } catch (Exception e) {
				System.err.println("SampleTypeParameters: Some Error closing the database");
				e.printStackTrace();
		   	}
        }       
	}
}
