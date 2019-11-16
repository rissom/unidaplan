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

	public class UpdatePOParameter extends HttpServlet {
		private static final long serialVersionUID = 1L;

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
			System.err.println("UpdatePOParameter: Input is not valid JSON");
		}
		

	    JSONObject newName=null; // get parameters
	    
	    try {
			 parameterID=jsonIn.getInt("parameterid");
		} catch (JSONException e) {
			System.err.println("UpdatePOParameter: Error parsing ID-Field or comment");
			response.setStatus(404);
		}
	    
	 	DBconnection dBconn = new DBconnection(); // initialize database
	    PreparedStatement pStmt = null;
	    
		if (jsonIn.has("name")){
		    try{
				 newName = jsonIn.getJSONObject("name");
				 language = JSONObject.getNames(newName)[0];
				 value = newName.getString(language);
			} catch (JSONException e) {
				System.err.println("UpdatePOParameter: Error parsing ID-Field or comment");
				response.setStatus(404);
			}
			
			try {
			    dBconn.startDB();	   
			    
			    if (dBconn.isAdmin(userID)){

					// find the stringkey
					pStmt = dBconn.conn.prepareStatement(
							"SELECT stringkeyname FROM po_parameters WHERE id = ?");
					pStmt.setInt(1,parameterID);
					int stringKey = dBconn.getSingleIntValue(pStmt);
					pStmt.close();
					
					// if no stringkey exists: create a copy of the parent parameter stringkey
					if (stringKey < 1){
                        pStmt = dBconn.conn.prepareStatement(
                                  "SELECT stringkeyname FROM paramdef WHERE id = "
                                + "(SELECT definition FROM po_parameters WHERE id = ?)");
                        pStmt.setInt(1, parameterID);
                        int key = dBconn.getSingleIntValue(pStmt);
                        
                        stringKey = dBconn.copyStringKey(key,userID,value); // new Stringkey with value as description, old entries are copyied
                        pStmt = dBconn.conn.prepareStatement(
								"UPDATE po_parameters SET stringkeyname = ?  WHERE id = ?");
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
				System.err.println("UpdatePOParameter: Problems with SQL query");
				e.printStackTrace();
				status="SQL error";
			} catch (Exception e) {
				System.err.println("UpdatePOParameter: some error occured");
				status="misc error";
			}
		}
		
		
		
		if (jsonIn.has("compulsory")){
			try {
			    dBconn.startDB();	
			    if (dBconn.isAdmin(userID)){

					boolean compulsory=jsonIn.getBoolean("compulsory");
					pStmt=dBconn.conn.prepareStatement(
							"UPDATE po_parameters SET compulsory=? WHERE id=?");
					pStmt.setBoolean(1, compulsory);
					pStmt.setInt(2,parameterID);
					pStmt.executeUpdate();
					pStmt.close();	
			    } else {
			    	response.setStatus(401);
			    }
			} catch (SQLException e){
				System.err.println("UpdatePOParameter: SQL error reading compulsory field");
				status="SQL error, compulsory field";
			}catch(JSONException e) {
				System.err.println("UpdatePOParameter: JSON error reading compulsory field");
				status="JSON error, compulsory field";
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
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
							"SELECT description FROM po_parameters WHERE id=?");
					pStmt.setInt(1,parameterID);
					int stringKey=dBconn.getSingleIntValue(pStmt);
					pStmt.close();
					
					if (stringKey<1) { // no string key in database
						if (newDescription.length()>0){	 //  and new value is not empty
							pStmt=dBconn.conn.prepareStatement( // copy strings from parent type
									"SELECT description FROM paramdef WHERE id="
									+ "(SELECT definition FROM po_parameters WHERE id=?)");
							pStmt.setInt(1,parameterID);
							int key=dBconn.getSingleIntValue(pStmt);
							stringKey=dBconn.copyStringKey(key,userID,value); // new Stringkey with value as description, old entries are copyied
							pStmt=dBconn.conn.prepareStatement(
									"UPDATE po_parameters SET description = ? WHERE id=?");
							pStmt.setInt(1,stringKey);
							pStmt.setInt(2,parameterID);
							pStmt.executeUpdate();
							pStmt.close();
							dBconn.addStringSet(stringKey,newDescription);
						}
					} else { // there is a stringkey
						if (newDescription.length()>0){
							dBconn.addStringSet(stringKey,newDescription);
						} else {
							dBconn.removeStringKey(stringKey);
							pStmt = dBconn.conn.prepareStatement(
									"UPDATE po_parameters SET description = ? WHERE id=?");
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
		
		
		
		
		if (jsonIn.has("hidden")){
			try {
			    dBconn.startDB();	   
			    if (dBconn.isAdmin(userID)){
	
					boolean hidden=jsonIn.getBoolean("hidden");
					pStmt=dBconn.conn.prepareStatement(
							"UPDATE po_parameters SET hidden=? WHERE id=?");
					pStmt.setBoolean(1, hidden);
					pStmt.setInt(2,parameterID);
					pStmt.executeUpdate();
					pStmt.close();	
			    } else {
			    	response.setStatus(401);
			    }
			} catch (SQLException e){
				System.err.println("UpdatePOParameter: SQL error reading hidden field");
				status="SQL error, hidden field";
			}catch(JSONException e) {
				System.err.println("UpdatePOParameter: JSON error reading hidden field");
				status="JSON error, hidden field";
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		dBconn.closeDB();

	    // tell client that everything is fine
	    Unidatoolkit.sendStandardAnswer(status,response);
	}
}	