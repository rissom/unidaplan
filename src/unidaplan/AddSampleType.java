package unidaplan;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

	public class AddSampleType extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doPost(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {		
		Authentificator authentificator = new Authentificator();
		int userID = authentificator.GetUserID(request,response);
		String status = "ok";
	    int stringKeyName = 0;
	    int stringKeyDesc = 0; 
	    int sampleTypeID = 0;
	    int parameterID = -1;
	    int position = 0;
	    int otgroup = 1;
	    PreparedStatement pStmt = null;
	    JSONObject  jsonIn = null;
	    
	    request.setCharacterEncoding("utf-8");
	    String in = request.getReader().readLine();

	    
	    try {
			jsonIn = new JSONObject(in);
		} catch (JSONException e) {
			System.err.println("AddSampleType: Input is not valid JSON");
			status = "input error";
		}
		response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    
	 	DBconnection dBconn = new DBconnection();

	    try {	
		    dBconn.startDB();	   
		    
			//check if admin
		    int admins = 1;
			if (userID>0 && Unidatoolkit.isMemberOfGroup(userID, admins, dBconn)){
				
				 // generate strings for the name and the unit
				if (jsonIn.has("name")){
					 JSONObject name = jsonIn.getJSONObject("name");
					 String [] names = JSONObject.getNames(name);
					 stringKeyName = dBconn.createNewStringKey(name.getString(names[0]));
					 for (int i = 0; i < names.length; i++){
						 dBconn.addString(stringKeyName,names[i],name.getString(names[i]));
					 }
				 } else {
					 System.out.println("no name exists");
				 }
				 if (jsonIn.has("description")){
					 JSONObject description = jsonIn.getJSONObject("description");
					 String[] descriptions = JSONObject.getNames(description);
					 if (descriptions != null && descriptions.length > 0){
						 stringKeyDesc = dBconn.createNewStringKey(description.getString(descriptions[0]));
						 for (int i = 0; i < descriptions.length; i++){
							 dBconn.addString(stringKeyDesc,descriptions[i],description.getString(descriptions[i]));
						 }	 
					 }
				 }
				 if (jsonIn.has("position")){
					 position = jsonIn.getInt("position");
				 }
				 if (jsonIn.has("otgroup")){
					 otgroup = jsonIn.getInt("ptgroup");
				 }  
				pStmt = dBconn.conn.prepareStatement( 			
						  "INSERT INTO objecttypes ("
						+ "  position,"
						+ "  otgrp,"
						+ "  string_key,"
						+ "  description,"
						+ "  lastuser) "
						+ "VALUES(?,?,?,?,?) "
						+ "RETURNING id");
				pStmt.setInt(1, position);
				pStmt.setInt(2, otgroup);
			   	pStmt.setInt(3, stringKeyName);
			   	if (stringKeyDesc > 0){
				   	pStmt.setInt(4, stringKeyDesc);
			   	}else{
			   		pStmt.setNull(4, java.sql.Types.INTEGER);
			   	}
			   	pStmt.setInt(5, userID);
			   	sampleTypeID = dBconn.getSingleIntValue(pStmt);			   	
			   	
			   	
			   	
			   	// create new Parametertype for title
			   	
			   	int stringKeySampleNumber = 0;
			   	int stringKeyDescSampleNumber = 0;
			   	
			   	if (jsonIn.has("name")){
			   		
					 JSONObject name = jsonIn.getJSONObject("name");
					 String [] names = JSONObject.getNames(name);
					 stringKeySampleNumber = dBconn.createNewStringKey(name.getString(names[0]));
					 stringKeyDescSampleNumber = dBconn.createNewStringKey(name.getString(names[0]));
					 for (int i = 0; i < names.length; i++){
						 dBconn.addString(stringKeySampleNumber, names[i],"no. of " + name.getString(names[i]));
						 dBconn.addString(stringKeyDescSampleNumber, names[i], "samplenumber of " + name.getString(names[i]));
					 }
			   	}
			   	
			
			   	
			   	pStmt = dBconn.conn.prepareStatement( 			
						  "INSERT INTO paramdef ("
						+ "  StringKeyName,"
						+ "  Datatype,"
						+ "  description,"
						+ "  lastUser) "
						+ "VALUES (?,1,?,?) "
						+ "RETURNING id");
			   	pStmt.setInt(1, stringKeySampleNumber);
			   	pStmt.setInt(2, stringKeyDescSampleNumber);
				pStmt.setInt(3, userID);  	// lastUser 
				parameterID = dBconn.getSingleIntValue(pStmt); 
				
			   	pStmt = dBconn.conn.prepareStatement(
				  		  "INSERT INTO ot_parameters ("
				  		+ "  ObjecttypesID,  "
					    + "  compulsory, "
					    + "  ID_Field, "
					    + "  Hidden, "
					    + "  pos, "
					    + "  definition, "
					    + "  lastUser ) "
					    + "VALUES(?,?,?,?,1,?,?)");
				pStmt.setInt(1, sampleTypeID);
				pStmt.setBoolean(2, true);
				pStmt.setBoolean(3, true);
				pStmt.setBoolean(4, false);  	//hidden
				pStmt.setInt(5, parameterID);  	// parameterID 
				pStmt.setInt(6, userID);  		// lastUser 
				pStmt.executeUpdate();
				pStmt.close();
                dBconn.closeDB();
		    } else {
		        response.setStatus(401);
		    }
			
		} catch (SQLException e) {
			status = "SQL error";
			e.printStackTrace();
			System.err.println("AddSampleType: Problems with SQL query");
		} catch (Exception e) {
			System.err.println("AddSampleType: Strange Problems");
			e.printStackTrace();
		}	
		
	
		
    // tell client that everything is fine
    Unidatoolkit.returnID(sampleTypeID, status, response);
	}
}	