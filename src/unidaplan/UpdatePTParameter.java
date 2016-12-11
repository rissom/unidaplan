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

	public class UpdatePTParameter extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@SuppressWarnings("resource")
	@Override
	  public void doPut(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {		
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
	    request.setCharacterEncoding("utf-8");
	    String in = request.getReader().readLine();
	    String status = "ok";
	    String language = "";
	    String value = "";

	    JSONObject  jsonIn = null;	
	    int parameterID = -1;
	    
    
	    try {
			  jsonIn = new JSONObject(in);
		} catch (JSONException e) {
			System.err.println("UpdatePTParameter: Input is not valid JSON");
		}
		
	    
	    try {
			 parameterID = jsonIn.getInt("parameterid");
		} catch (JSONException e) {
			System.err.println("UpdatePTParameter: Error parsing ID-Field or comment");
			response.setStatus(404);
		}
	    
	 	DBconnection dBconn = new DBconnection(); // initialize database
	    PreparedStatement pStmt = null;
	    
		if (jsonIn.has("name")){
			String[] names = null;
			JSONObject newNames = null;
		    try{
		    	newNames = jsonIn.getJSONObject("name");
				names = JSONObject.getNames(newNames);
			} catch (JSONException e) {
				System.err.println("UpdatePTParameter: Error parsing ID-Field or comment");
				response.setStatus(404);
			}
		   
			try {
			    dBconn.startDB();
			    
			    if (dBconn.isAdmin(userID)) {

					// find the stringkey
					pStmt = dBconn.conn.prepareStatement(
							"SELECT stringkeyname FROM p_parameters WHERE id = ?");
					pStmt.setInt(1,parameterID);
					int stringKey = dBconn.getSingleIntValue(pStmt);
					pStmt.close();
					
					// if no stringkey defined: create a new one and update database
					if (stringKey == 0){
						stringKey = dBconn.createNewStringKey(newNames.getString(names[0]));
						pStmt = dBconn.conn.prepareStatement(
								"UPDATE p_parameters SET stringkeyname = ? WHERE id = ?");
						pStmt.setInt(1,stringKey);
						pStmt.setInt(2,parameterID);
						pStmt.executeUpdate();
						pStmt.close();
					}
					
					
					for (int i = 0; i < names.length; i++){

						// delete old entries in the same language
						dBconn.deleteString(stringKey, names[i]);
						
						// create database entries for the new names
						dBconn.addString(stringKey, names[i],  newNames.getString(names[i]));
					}
					
					
			    } else {
			    	response.setStatus(401);
			    }
				
				
			} catch (SQLException e) {
				System.err.println("UpdatePTParameter: Problems with SQL query");
				status="SQL error";
			} catch (Exception e) {
				System.err.println("UpdatePTParameter: some error occured");
				status="misc error";
			}
			dBconn.closeDB();
		}
		
		
		
		if (jsonIn.has("compulsory")){
			try {
			    dBconn.startDB();	
			    if (dBconn.isAdmin(userID)){

					boolean compulsory = jsonIn.getBoolean("compulsory");
					pStmt=dBconn.conn.prepareStatement(
							"UPDATE p_parameters SET compulsory = ? WHERE id = ?");
					pStmt.setBoolean(1, compulsory);
					pStmt.setInt(2,parameterID);
					pStmt.executeUpdate();
					pStmt.close();	
			    } else {
			    	response.setStatus(401);
			    }
			} catch (SQLException e){
				System.err.println("UpdatePTParameter: SQL error reading compulsory field");
				status="SQL error, compulsory field";
			}catch(JSONException e) {
				System.err.println("UpdatePTParameter: JSON error reading compulsory field");
				status="JSON error, compulsory field";
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			dBconn.closeDB();
		}
		
		
		
		if (jsonIn.has("description")){
			JSONObject newDescription=null;
		    try{
				 newDescription=jsonIn.getJSONObject("description");
				 if (newDescription.length()>0){
					 language=JSONObject.getNames(newDescription)[0];
					 value=newDescription.getString(language);
				 }
			} catch (JSONException e) {
				System.err.println("UpdateSTParameter: Error parsing Description-Field");
				response.setStatus(404);
			}

			try {
			    dBconn.startDB();
			    if (dBconn.isAdmin(userID)){

				   	// find the stringkey
					pStmt=dBconn.conn.prepareStatement(
							"SELECT description FROM p_parameters WHERE id = ?");
					pStmt.setInt(1,parameterID);
					int stringKey = dBconn.getSingleIntValue(pStmt);
					pStmt.close();
					
					if (stringKey<1) { // no string key in database
						if (newDescription.length()>0){	 //  and new value is not empty
							pStmt=dBconn.conn.prepareStatement( // copy strings from parent type
									  "SELECT description "
									+ "FROM paramdef "
									+ "WHERE id = "
									+ "  (SELECT definition FROM p_parameters WHERE id = ?)");
							pStmt.setInt(1,parameterID);
							int key=dBconn.getSingleIntValue(pStmt);
							stringKey=dBconn.copyStringKey(key,userID,value); // new Stringkey with value as description, old entries are copyied
							pStmt=dBconn.conn.prepareStatement(
									"UPDATE p_parameters SET description = ? WHERE id = ?");
							pStmt.setInt(1,stringKey);
							pStmt.setInt(2,parameterID);
							pStmt.executeUpdate();
							dBconn.addStringSet(stringKey,newDescription);
						}
					} else { // there is a stringkey
						if (newDescription.length()>0){
							dBconn.addStringSet(stringKey,newDescription);
						} else {
							dBconn.removeStringKey(stringKey);
							pStmt = dBconn.conn.prepareStatement(
									"UPDATE p_parameters SET description = ? WHERE id = ?");
							pStmt.setNull(1,java.sql.Types.INTEGER);
							pStmt.setInt(2,parameterID);
							pStmt.executeUpdate();
							pStmt.close();
						}
					}		
				} else {
			    	response.setStatus(401);
			    }
				
			} catch (SQLException e) {
				System.err.println("UpdatePTParameter: Problems with SQL query");
				e.printStackTrace();
				status="SQL error";
			} catch (Exception e) {
				System.err.println("UpdatePTParameter: some error occured");
				status="misc error";
			}
			dBconn.closeDB();
		}
		

		if (jsonIn.has("formula")){
			try {
			    dBconn.startDB();	
				dBconn.conn.setAutoCommit(false);

			    if (dBconn.isAdmin(userID)){

			    	// set new formula
					String formula = jsonIn.getString("formula");
					pStmt = dBconn.conn.prepareStatement(
							  "UPDATE p_parameters "
							+ "SET (formula,lastchange,lastuser) = (?, NOW(), ? ) "
							+ "WHERE id = ?");
					if (formula.equals("")){
						pStmt.setNull(1,java.sql.Types.VARCHAR);
					} else {
						pStmt.setString(1, formula);
					}
					pStmt.setInt(2,userID);
					pStmt.setInt(3,parameterID);				
					pStmt.executeUpdate();
					pStmt.close();	
					
					// check for circular references
					pStmt = dBconn.conn.prepareStatement(
							  "WITH RECURSIVE dependencyrank(parameterid, rank, path, cycle) AS ( "
							+ "SELECT 5 , 1, ARRAY[5], false "
							+ "UNION " 
							+ "SELECT "
							+ "pp.id AS parameterid, "
							+ "rank + 1 AS rank, "
							+ "path || pp.id, "
							+ "pp.id = ANY (path) "
							+ "FROM dependencyrank dr, p_parameters pp "
							+ "WHERE pp.formula LIKE '%p' || dr.parameterid || '%' AND NOT cycle "
							+ ") "
							+ "SELECT "
							+ " bool_or(cycle) "
							+ "FROM dependencyrank "
							+ "WHERE rank > 1");
					Boolean circularReferencesExist = dBconn.getSingleBooleanValue(pStmt);
					pStmt.close();
					if (circularReferencesExist){
						status = "circular references exist";
						response.setStatus(420);
						// replace new formula again with old formula
						dBconn.conn.rollback();
					} 
					dBconn.conn.setAutoCommit(true);
				} else {
			    	response.setStatus(401);
			    }
			} catch (SQLException e){
				System.err.println("UpdatePTParameter: SQL error reading compulsory field");
				status="SQL error, compulsory field";
			}catch(JSONException e) {
				System.err.println("UpdatePTParameter: JSON error reading compulsory field");
				status="JSON error, compulsory field";
			} catch (Exception e) {
				status = "some error occured";
				e.printStackTrace();
			}
		}
		
		
		
		
		if (jsonIn.has("hidden")){
			try {
			    dBconn.startDB();	
			    if (dBconn.isAdmin(userID)){

					boolean hidden=jsonIn.getBoolean("hidden");
					pStmt=dBconn.conn.prepareStatement(
							"UPDATE p_parameters SET hidden=? WHERE id=?");
					pStmt.setBoolean(1, hidden);
					pStmt.setInt(2,parameterID);
					pStmt.executeUpdate();
					pStmt.close();	
			    } else {
			    	response.setStatus(401);
			    }
			} catch (SQLException e){
				System.err.println("UpdatePTParameter: SQL error reading hidden field");
				status="SQL error, hidden field";
			}catch(JSONException e) {
				System.err.println("UpdatePTParameter: JSON error reading hidden field");
				status="JSON error, hidden field";
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			dBconn.closeDB();
		}
		

	    // tell client that everything is fine
	    Unidatoolkit.sendStandardAnswer(status,response);
	}
}	