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

	public class UpdateParameter extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doPut(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {		
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
	    request.setCharacterEncoding("utf-8");
	    String in = request.getReader().readLine();
	    String status = "ok";

	    JSONObject jsonIn = null;	
	    int parameterID = -1;
	    
    
	    try {
			  jsonIn = new JSONObject(in);
		} catch (JSONException e) {
			System.err.println("UpdateParameter: Input is not valid JSON");
		}
		

	    JSONObject newName = null; // Object contains the new names, language as keys
	    JSONObject newDesc = null; // Object contains the new descriptions, language as keys
	    JSONObject newUnit = null; // Object contains the new units, language as keys

	    
	    try {
			 parameterID = jsonIn.getInt("parameterid");
		} catch (JSONException e) {
			System.err.println("UpdateParameter: Error parsing ID-Field or comment");
			e.printStackTrace();
			response.setStatus(404);
		}
	    
	 	DBconnection dBconn = new DBconnection(); // initialize database
	    PreparedStatement pStmt = null;
	    
	    try{ 
		    dBconn.startDB();	   
	    
		    if (dBconn.isAdmin(userID)){
		    
				if (jsonIn.has("datatype")){
				
					String datatype = jsonIn.getString("datatype");
					int dt = 0;
					for (int i = 0; i < Unidatoolkit.Datatypes.length; i++){
						if (Unidatoolkit.Datatypes[i].equalsIgnoreCase(datatype)){
							dt=i;
						}
					}
					pStmt = dBconn.conn.prepareStatement(
							"UPDATE paramdef SET datatype=? WHERE id=?");
					pStmt.setInt(1,dt);
					pStmt.setInt(2,parameterID);				
					pStmt.executeUpdate();
					pStmt.close();	
				
				}
			    
		
				if (jsonIn.has("name")){
					 newName = jsonIn.getJSONObject("name");

					// find the stringkey
					pStmt = dBconn.conn.prepareStatement(
							"SELECT stringkeyname FROM paramdef WHERE id = ?");
					pStmt.setInt(1,parameterID);
					int stringKey = dBconn.getSingleIntValue(pStmt);
					pStmt.close();
					
					// create database entries for the new name
					String[] names = JSONObject.getNames(newName);
					for (int i = 0; i < names.length; i++){
						
						// delete old entries in the same language
						pStmt = dBconn.conn.prepareStatement(
								  "DELETE FROM stringtable "
								+ "WHERE string_key = ? AND language = ?");
						pStmt.setInt(1,stringKey);
						pStmt.setString(2,names[i]);
						pStmt.executeUpdate();
						pStmt.close();
						
						pStmt = dBconn.conn.prepareStatement( 			
								   "INSERT INTO stringtable (string_key,language,value,lastuser)"
								 + "VALUES(?,?,?,?)");
						pStmt.setInt(1,stringKey);
						pStmt.setString(2,names[i]);
						pStmt.setString(3,newName.getString(names[i]));
						pStmt.setInt(4,userID);
						pStmt.executeUpdate();
						pStmt.close();
					}
				}
				
				
				if (jsonIn.has("description")){
					newDesc = jsonIn.getJSONObject("description");
					String[] descriptions = JSONObject.getNames(newDesc);
	
					// find the stringkey
					pStmt = dBconn.conn.prepareStatement(
							"SELECT description FROM paramdef WHERE id=?");
					pStmt.setInt(1,parameterID);
					int descriptionKey = dBconn.getSingleIntValue(pStmt);
					
					// if it does not exist: create a new one
					if (descriptionKey < 1){ 
						descriptionKey = dBconn.createNewStringKey(newUnit.getString(descriptions[0]));
						pStmt = dBconn.conn.prepareStatement(
								"UPDATE paramdef SET description = ? WHERE id = ?");
						pStmt.setInt(1,descriptionKey);
						pStmt.setInt(2,parameterID);
						pStmt.executeUpdate();
						pStmt.close();
					}
					
					// create database entries for the new descriptions
					for (int i = 0; i < descriptions.length; i++){
						
						// delete old entries in the same language
						pStmt = dBconn.conn.prepareStatement(
								  "DELETE FROM stringtable "
								+ "WHERE string_key = ? AND language = ?");
						pStmt.setInt(1,descriptionKey);
						pStmt.setString(2,descriptions[i]);
						pStmt.executeUpdate();
						pStmt.close();
						
						// insert new value
						pStmt = dBconn.conn.prepareStatement( 			
								 "INSERT INTO stringtable (string_key,language,value,lastuser) "
							   + "VALUES (?,?,?,?)");
						pStmt.setInt(1,descriptionKey);
						pStmt.setString(2,descriptions[i]);
						pStmt.setString(3,newDesc.getString(descriptions[i]));
						pStmt.setInt(4,userID);
						pStmt.executeUpdate();
						pStmt.close();
					}
				}
			
				
			
				if (jsonIn.has("unit")){
					newUnit = jsonIn.getJSONObject("unit");
					String[] units = JSONObject.getNames(newUnit);
	
					// find the stringkey
					pStmt = dBconn.conn.prepareStatement(
							"SELECT stringkeyunit FROM paramdef WHERE id = ?");
					pStmt.setInt(1,parameterID);
					int unitKey = dBconn.getSingleIntValue(pStmt);
					pStmt.close();
					
					// if it does not exist: create a new one
					if (unitKey < 1){ 
						unitKey = dBconn.createNewStringKey(newUnit.getString(units[0]));
						pStmt = dBconn.conn.prepareStatement(
								"UPDATE paramdef SET stringkeyunit = ? WHERE id = ?");
						pStmt.setInt(1,unitKey);
						pStmt.setInt(2,parameterID);
						pStmt.executeUpdate();
						pStmt.close();
					}
				
					// create database entries for the new units
					for (int i = 0; i < units.length; i++){
						
						// delete old entries in the same language
						pStmt = dBconn.conn.prepareStatement(
								"DELETE FROM stringtable WHERE string_key = ? AND language = ?");
						pStmt.setInt(1,unitKey);
						pStmt.setString(2,units[i]);
						pStmt.executeUpdate();
						pStmt.close();
						
						pStmt = dBconn.conn.prepareStatement( 			
								 "INSERT INTO stringtable (string_key,language,value,lastuser) "
								+"VALUES (?,?,?,?)");
						pStmt.setInt(1,unitKey);
						pStmt.setString(2,units[i]);
						pStmt.setString(3,newUnit.getString(units[i]));
						pStmt.setInt(4,userID);
						pStmt.executeUpdate();
						pStmt.close();
					}
				}
					
				
				
					
				if (jsonIn.has("regex")){
					pStmt = dBconn.conn.prepareStatement(
							"UPDATE paramdef SET regex=? WHERE id=?");
					pStmt.setString(1, jsonIn.getString("regex"));
					pStmt.setInt(2,parameterID);				
					pStmt.executeUpdate();
					pStmt.close();	
				}
				
				
				if (jsonIn.has("min")){
					pStmt = dBconn.conn.prepareStatement(
							"UPDATE paramdef SET min=? WHERE id=?");
					if (jsonIn.optString("min").equals("")){
						pStmt.setNull(1, java.sql.Types.DOUBLE);
					} else {
						pStmt.setDouble(1, jsonIn.getDouble("min"));
					}
					pStmt.setInt(2,parameterID);				
					pStmt.executeUpdate();
					pStmt.close();	
				}
		
				if (jsonIn.has("max")){
					pStmt=dBconn.conn.prepareStatement(
							"UPDATE paramdef SET max=? WHERE id=?");
					if (jsonIn.optString("max").equals("")){
						pStmt.setNull(1, java.sql.Types.DOUBLE);
					} else {
						pStmt.setDouble(1, jsonIn.getDouble("max"));
					}	
					pStmt.setInt(2,parameterID);				
					pStmt.executeUpdate();
					pStmt.close();	
				}
			
				if (jsonIn.has("format")){
					pStmt = dBconn.conn.prepareStatement(
							"UPDATE paramdef SET format = ? WHERE id = ?");
					pStmt.setString(1, jsonIn.getString("format"));
					pStmt.setInt(2, parameterID);	
					pStmt.executeUpdate();
					pStmt.close();	
				}
					
			} else {
				response.setStatus(401);
			} 
	    }catch (SQLException e) {
			System.err.println("UpdateParameter: Problems initializing Database");
			status="SQL error";
		} catch (Exception e) {
			System.err.println("UpdateParameter: Problems initializing Database");
			status="SQL error";
			e.printStackTrace();
		}
	
		dBconn.closeDB();

	    // tell client that everything is fine
	    Unidatoolkit.sendStandardAnswer(status,response);
	}
}	