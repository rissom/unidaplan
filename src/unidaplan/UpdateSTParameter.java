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

	public class UpdateSTParameter extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@SuppressWarnings("resource")
	@Override
	  public void doPut(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {		
		Authentificator authentificator = new Authentificator();
		int userID = authentificator.GetUserID(request,response);
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
			System.err.println("UpdateSTParameter: Input is not valid JSON");
		}
		

	    
	    try {
	        parameterID = jsonIn.getInt("parameterid");
		} catch (JSONException e) {
			System.err.println("UpdateSTParameter: Error parsing ID-Field or comment");
			response.setStatus(404);
		}
	    
	 	DBconnection dBconn = new DBconnection(); // initialize database
	 	try {
	 		dBconn.startDB();	  
	 	
		    PreparedStatement pStmt = null;
		    
			if (jsonIn.has("name")){
			    JSONObject newName = null; // get parameters
			    try{
					 newName = jsonIn.getJSONObject("name");
					 language = JSONObject.getNames(newName)[0];
					 value = newName.getString(language);
				} catch (JSONException e) {
					System.err.println("UpdateSTParameter: Error parsing ID-Field or comment");
					response.setStatus(404);
				}
	
				try {			    
				    if (dBconn.isAdmin(userID)){
				    
						// find the stringkey
						pStmt = dBconn.conn.prepareStatement(
								"SELECT stringkeyname FROM ot_parameters WHERE id = ?");
						pStmt.setInt(1,parameterID);
						int stringKey = dBconn.getSingleIntValue(pStmt);
						pStmt.close();
						
		                 // if no stringkey exists: create a copy of the parent parameter stringkey
						if (stringKey < 1){
							pStmt = dBconn.conn.prepareStatement(
									  "SELECT stringkeyname FROM paramdef WHERE id = "
									+ "(SELECT definition FROM ot_parameters WHERE id = ?)");
							pStmt.setInt(1, parameterID);
							int key = dBconn.getSingleIntValue(pStmt);
							
							stringKey = dBconn.copyStringKey(key,userID,value); // new Stringkey with value as description, old entries are copyied
							pStmt = dBconn.conn.prepareStatement(
									"UPDATE ot_parameters SET stringkeyname = ? WHERE id = ?");
							pStmt.setInt(1, stringKey);
							pStmt.setInt(2, parameterID);
							pStmt.executeUpdate();
							pStmt.close();
						}
						dBconn.addString(stringKey, language, value);
				    } else {
				    	    response.setStatus(401);
				    }
	
					
				} catch (SQLException e) {
					System.err.println("UpdateSTParameter: Problems with SQL query");
					e.printStackTrace();
					status="SQL error";
				} catch (Exception e) {
					System.err.println("UpdateSTParameter: some error occured");
					status="misc error";
				}
			}
			
			
			if (jsonIn.has("description")){
				JSONObject newDescription = null;
			    try{
					 newDescription = jsonIn.getJSONObject("description");
					 if (newDescription.length()>0){
						 language=JSONObject.getNames(newDescription)[0];
						 value=newDescription.getString(language);
					 }
				} catch (JSONException e) {
					System.err.println("UpdateSTParameter: Error parsing Description-Field");
					response.setStatus(404);
				}
	
				try {
				    if (dBconn.isAdmin(userID)){
		
						// find the stringkey
						pStmt = dBconn.conn.prepareStatement(
								"SELECT description FROM ot_parameters WHERE id=?");
						pStmt.setInt(1,parameterID);
						int stringKey=dBconn.getSingleIntValue(pStmt);
						pStmt.close();
						
						if (stringKey < 1) { // no string key in database
							if (newDescription.length()>0){	 //  and new value is not empty
								pStmt = dBconn.conn.prepareStatement( // copy strings from parent type
										"SELECT description FROM paramdef WHERE id="
										+ "(SELECT definition FROM ot_parameters WHERE id=?)");
								pStmt.setInt(1,parameterID);
								int key = dBconn.getSingleIntValue(pStmt);
								stringKey = dBconn.copyStringKey(key,userID,value); // new Stringkey with value as description, old entries are copyied
								pStmt = dBconn.conn.prepareStatement(
										"UPDATE ot_parameters SET description = ? WHERE id = ?");
								pStmt.setInt(1,stringKey);
								pStmt.setInt(2,parameterID);
								pStmt.executeUpdate();
								dBconn.addStringSet(stringKey,newDescription);
							}
						} else { // there is a stringkey
							if (newDescription.length() > 0){
								dBconn.addStringSet(stringKey,newDescription);
							} else {
								dBconn.removeStringKey(stringKey);
								pStmt = dBconn.conn.prepareStatement(
										"UPDATE ot_parameters SET description = ? WHERE id = ?");
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
					System.err.println("UpdateSTParameter: Problems with SQL query");
					e.printStackTrace();
					status="SQL error";
				} catch (Exception e) {
					System.err.println("UpdateSTParameter: some error occured");
					status="misc error";
				}
			}
			
			
			
			if (jsonIn.has("formula")){
				try {
				    dBconn.startDB();	
					dBconn.conn.setAutoCommit(false);
	
				    if (dBconn.isAdmin(userID)){
	
				    	// set new formula
						String formula = jsonIn.getString("formula");
						pStmt = dBconn.conn.prepareStatement(
								  "UPDATE ot_parameters "
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
								+ "otp.id AS parameterid, "
								+ "rank + 1 AS rank, "
								+ "path || otp.id, "
								+ "otp.id = ANY (path) "
								+ "FROM dependencyrank dr, ot_parameters otp "
								+ "WHERE otp.formula LIKE '%p' || dr.parameterid || '%' AND NOT cycle "
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
					System.err.println("UpdateSTParameter: SQL error reading compulsory field");
					status="SQL error, compulsory field";
				}catch(JSONException e) {
					System.err.println("UpdateSTParameter: JSON error reading compulsory field");
					status="JSON error, compulsory field";
				} catch (Exception e) {
					status = "some error occured";
					e.printStackTrace();
				}
			}
			
			
			
			if (jsonIn.has("compulsory")){
				try {
				    if (dBconn.isAdmin(userID)){
						Boolean compulsory=jsonIn.getBoolean("compulsory");
						pStmt=dBconn.conn.prepareStatement(
								"UPDATE ot_parameters SET (compulsory,lastchange,lastuser) = (?,NOW(),?) WHERE id=?");
						pStmt.setBoolean(1, compulsory);
						pStmt.setInt(2,userID);
						pStmt.setInt(3,parameterID);				
						pStmt.executeUpdate();
						pStmt.close();	
					} else {
				    	response.setStatus(401);
				    }
				} catch (SQLException e){
					System.err.println("UpdateSTParameter: SQL error reading compulsory field");
					status="SQL error, compulsory field";
				}catch(JSONException e) {
					System.err.println("UpdateSTParameter: JSON error reading compulsory field");
					status="JSON error, compulsory field";
				} catch (Exception e) {
					status = "some error occured";
					e.printStackTrace();
				}
			}
			
			
			
			if (jsonIn.has("hidden")){
				try {
				    if (dBconn.isAdmin(userID)){
						Boolean hidden = jsonIn.getBoolean("hidden");
						pStmt = dBconn.conn.prepareStatement(
								"UPDATE ot_parameters SET (hidden,lastchange,lastuser)=(?,NOW(),?) WHERE id=?");
						pStmt.setBoolean(1, hidden);
						pStmt.setInt(2,userID);
						pStmt.setInt(3,parameterID);
						pStmt.executeUpdate();
						pStmt.close();
				    } else {
				    	response.setStatus(401);
				    }
				} catch (SQLException e){
					System.err.println("UpdateSTParameter: SQL error reading hidden field");
					status = "SQL error, hidden field";
				}catch(JSONException e) {
					System.err.println("UpdateSTParameter: JSON error reading hidden field");
					status = "JSON error, hidden field";
				} catch (Exception e) {
					e.printStackTrace();
					status = "some error occured";
				}
			}
			
			
			
			if (jsonIn.has("id_field")){
				try {
				    if (dBconn.isAdmin(userID)){
	
						Boolean idField=jsonIn.getBoolean("id_field");
						if (idField){
							pStmt=dBconn.conn.prepareStatement(
									"UPDATE ot_parameters SET (id_field,parametergroup,lastuser)=(TRUE,NULL,?) WHERE id=?");
							pStmt.setInt(1,userID);
							pStmt.setInt(2,parameterID);	
						} 
						pStmt.executeUpdate();
						pStmt.close();	
				    } else {
				    	response.setStatus(401);
				    }
				} catch (SQLException e){
					System.err.println("UpdateSTParameter: SQL error reading id_field");
					status = "SQL error, id_field";
				}catch(JSONException e) {
					System.err.println("UpdateSTParameter: JSON error reading id_field");
					status = "JSON error, id_field";
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
	 	} catch (Exception e) {
			System.err.println("UpdateSTParameter: Error starting database");
			response.setStatus(404);
		} finally{
			dBconn.closeDB();
		}
		

	    // tell client that everything is fine
	    Unidatoolkit.sendStandardAnswer(status,response);
	}
}	