package unidaplan;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

	public class AddSample extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@SuppressWarnings("resource")
	@Override
	  public void doPost(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {
		
	
		Authentificator authentificator = new Authentificator();
		int userID = authentificator.GetUserID(request,response);
		int id = -1;
	    PreparedStatement pStmt = null;
		String status = "ok";
	   	String privilege = "n";
	   	int recipe = 0;
	    JSONObject jsonIn = null;		
	    request.setCharacterEncoding("utf-8");
		response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    String in = request.getReader().readLine();

	    
	    
	    try {
			jsonIn = new JSONObject(in);
		} catch (JSONException e) {
			System.err.println("AddProcessType: Input is not valid JSON");
			status = "input error";
		}
	    
	    // get the sampletypeID
	    int sampletypeID = -1;
	    try {
			 sampletypeID = jsonIn.getInt("sampletypeid"); 
			 if (jsonIn.has("recipe")){
				 recipe = jsonIn.getInt("recipe");
			 }
			 
		} catch (Exception e) {
			System.err.println("AddSample: Error parsing type ID");
			response.setStatus(404);
		}

	    
	    // create entry in the database	    
	 	DBconnection dBconn = new DBconnection();
	 	
		try {
		    dBconn.startDB();	 
	 	
		 	 // check privilege
		    pStmt = dBconn.conn.prepareStatement( 	
					   "SELECT getSampleTypeRights(vuserid := ?, vsampletype := ?)");
			pStmt.setInt(1,userID);
			pStmt.setInt(2,sampletypeID);
			privilege = dBconn.getSingleStringValue(pStmt);

			if (privilege.equals("w")){	 	
  
				pStmt = dBconn.conn.prepareStatement( 			
						   "INSERT INTO samples (ObjecttypesID, Creator, lastUser) "
						 + "VALUES (?, ?, ?) "
						 + "RETURNING id");
			   	pStmt.setInt(1, sampletypeID);
			   	pStmt.setInt(2, userID);
			   	pStmt.setInt(3, userID);
				id = dBconn.getSingleIntValue(pStmt);
			
				// find the current maximum of sample name parameters
				pStmt = dBconn.conn.prepareStatement( 	
						   "SELECT id "
						 + "FROM samplenames "
						 + "WHERE typeid = ? "
						 + "ORDER BY UPPER(name) "
						 + "DESC LIMIT 1");
			   	pStmt.setInt(1, sampletypeID);
			   	JSONObject answer = dBconn.jsonObjectFromPreparedStmt(pStmt);
			   	int lastSampleID = 0;
			   	if (answer.length() > 0){
			   		lastSampleID = answer.getInt("id");
			   	} 
				
				JSONArray lastTitleIntParameters = null;
				
				if (lastSampleID > 0){
				
					// List of titleparameters of type Integer with values of last object:
					pStmt = dBconn.conn.prepareStatement( 	
							  "SELECT "
							+ "  ot_parameters.id, "
							+ "  sd.data "
							+ "FROM ot_parameters " 
							+ "JOIN paramdef ON ot_parameters.definition = paramdef.id "
							+ "LEFT JOIN sampledata sd ON sd.ot_parameter_id = ot_parameters.id "
							+ "WHERE ID_Field = true AND paramdef.datatype = 1 AND sd.objectid = ? "
							+ "ORDER BY pos DESC");
				   	pStmt.setInt(1, lastSampleID);
				   	lastTitleIntParameters = dBconn.jsonArrayFromPreparedStmt(pStmt);

				} else {
					// List of titleparameters without any values.
					pStmt = dBconn.conn.prepareStatement( 	
							  "SELECT "
							+ "  ot_parameters.id "
							+ "FROM ot_parameters " 
							+ "JOIN paramdef ON ot_parameters.definition = paramdef.id "
							+ "WHERE ID_Field = true AND paramdef.datatype = 1 AND ot_parameters.objecttypesid = ? "
							+ "ORDER BY pos DESC");
					pStmt.setInt(1, sampletypeID);
				   	lastTitleIntParameters = dBconn.jsonArrayFromPreparedStmt(pStmt);
				}
				Boolean intParameterExists = false;


				
				// check if there is any
				if (lastTitleIntParameters.length() > 0){				
			   	
					// copy old integer parameters and increase last integer parameter
					intParameterExists = true;
					int increment = 1;
			        for (int i = 0; i < lastTitleIntParameters.length(); i++){   
			        	JSONObject parameter=(JSONObject) lastTitleIntParameters.get(i);
			        	pStmt = dBconn.conn.prepareStatement(
			        			  "INSERT INTO sampledata (objectid, ot_parameter_id, data, lastuser)"
			        			+ "VALUES(?, ?, json_build_object('value',?)::jsonb, ?)" );
			        	pStmt.setInt(1, id);
			        	pStmt.setInt(2, parameter.getInt("id"));
			        		if (parameter.has("data")){
			        			pStmt.setInt(3, parameter.getJSONObject("data").getInt("value") + increment);
			        		} else {
			        			pStmt.setInt(3, 1);
			        		}
			        	pStmt.setInt(4, userID);
			        	pStmt.executeUpdate();
			        	pStmt.close();
			        	increment = 0;
			        }
				} 
				
				// List of titleparameters of type String:
				pStmt = dBconn.conn.prepareStatement( 	
				      "SELECT "
					+ "  ot_parameters.id, "
					+ "  sd.data "
					+ "FROM ot_parameters " 
					+ "JOIN paramdef ON ot_parameters.definition = paramdef.id "
					+ "JOIN sampledata sd ON sd.ot_parameter_id = ot_parameters.id "
					+ "WHERE ID_Field = true AND paramdef.datatype = 4 AND sd.objectid = ? "
					+ "ORDER BY pos DESC");
			   	pStmt.setInt(1, lastSampleID);
			   	JSONArray lastTitleStrParameters = dBconn.jsonArrayFromPreparedStmt(pStmt);
				
				
				// copy old string parameters and increase replace the last string parameter
		        for (int i=0; i<lastTitleStrParameters.length(); i++){   
		        	JSONObject parameter=(JSONObject) lastTitleStrParameters.get(i);
		        	pStmt = dBconn.conn.prepareStatement(
		        			  "INSERT INTO sampledata (objectid, ot_parameter_id, data, lastuser)"
		        			+ " VALUES(?,?,?,?);");
		        	pStmt.setInt(1, id);
		        	pStmt.setInt(2, parameter.getInt("id"));
		        	JSONObject data = null;
		        	if (!intParameterExists && i+1 == lastTitleStrParameters.length()){
		        		data = new JSONObject();
		        		data.put("value", "new");
		        	}else{
		        		data = parameter.getJSONObject("data");
		        	}
		        	pStmt.setObject(3, data, java.sql.Types.OTHER);
		        	pStmt.setInt(4, userID);
		        	pStmt.executeUpdate();
		        	pStmt.close();
		        }
		        
		        
		        
		    	// fill in default parameter values from recipe
	   	   		if (recipe > 0){
			   	   	pStmt = dBconn.conn.prepareStatement(	
				   	   	  "INSERT INTO sampledata (objectid, ot_parameter_id, data, lastuser) "
			   	   		+ "SELECT "
				   	 	+ "? AS objectid, "
				   	 	+ "parameter, "
				   	 	+ "data, "
				   	 	+ "? AS lastuser " 
			   	   		+ "FROM samplerecipedata " 
			   	   		+ "WHERE recipe = ? ");
			   	   	pStmt.setInt(1, id);
			   	   	pStmt.setInt(2, userID);
			   	   	pStmt.setInt(3, recipe);
		   	   		pStmt.executeUpdate();
		   	   		pStmt.close();
	   	   		}
		        
		        
			} else {
				response.setStatus(401);
			}
		} catch (SQLException e) {
			System.err.println("AddSample: Problems with SQL query");
			e.printStackTrace();
			status = "SQL error";
			response.setStatus(404);
		} catch (JSONException e){
			e.printStackTrace();
			System.err.println("AddSample: Problems creating JSON");
			status = "JSON error";
			response.setStatus(404);
		} catch (Exception e) {
			System.err.println("AddSample: Strange Problems");
			status = "error";
			response.setStatus(404);
		} 
        
		dBconn.closeDB();
		
		
    // tell client that everything is fine
	Unidatoolkit.returnID(id, status, response);
	}
}	