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
	    String language = "";
	    String value ="";
	    int paramDatatype;

	    JSONObject  jsonIn = null;	
	    int parameterID = -1;
	    
    
	    try {
			  jsonIn = new JSONObject(in);
		} catch (JSONException e) {
			System.err.println("UpdateParameter: Input is not valid JSON");
		}
		

	    JSONObject newName=null; // Object contains the new names, language as keys
	    JSONObject newDesc=null; // Object contains the new descriptions, language as keys
	    JSONObject newUnit=null; // Object contains the new units, language as keys

	    
	    try {
			 parameterID=jsonIn.getInt("parameterid");
		} catch (JSONException e) {
			System.err.println("UpdateParameter: Error parsing ID-Field or comment");
			e.printStackTrace();
			response.setStatus(404);
		}
	    
	 	DBconnection dBconn=new DBconnection(); // initialize database
	    PreparedStatement pStmt = null;
	    
	    try{ 
		    dBconn.startDB();	   
	    } catch (SQLException e) {
			System.err.println("UpdateParameter: Problems initializing Database");
			status="SQL error";
		} catch (Exception e) {
			System.err.println("UpdateParameter: Problems initializing Database");
			status="SQL error";
			e.printStackTrace();
		}
	    
	    
		if (jsonIn.has("datatype")){
			try {
				String datatype=jsonIn.getString("datatype");
				int dt=0;
				for (int i=0; i<Unidatoolkit.Datatypes.length; i++){
					if (Unidatoolkit.Datatypes[i].equalsIgnoreCase(datatype)){
						dt=i;
					}
				}
				pStmt=dBconn.conn.prepareStatement(
						"UPDATE paramdef SET datatype=? WHERE id=?");
				pStmt.setInt(1,dt);
				pStmt.setInt(2,parameterID);				
				pStmt.executeUpdate();
				pStmt.close();	
			} catch (SQLException e){
				System.err.println("UpdateParameter: SQL error reading format field");
				status="SQL error, format field";
			}catch(JSONException e) {
				System.err.println("UpdateParameter: JSON error reading format field");
				status="JSON error, format field";
			}
		}
	    

		if (jsonIn.has("name")){
		    try{
				 newName=jsonIn.getJSONObject("name");
				 language=JSONObject.getNames(newName)[0];
				 value=newName.getString(language);
			} catch (JSONException e) {
				System.err.println("UpdateParameter: Error parsing ID-Field or comment");
				e.printStackTrace();
				response.setStatus(404);
			}
		   
			try {
				// find the stringkey
				pStmt=dBconn.conn.prepareStatement(
						"SELECT stringkeyname FROM paramdef WHERE id=?");
				pStmt.setInt(1,parameterID);
				int stringKey=dBconn.getSingleIntValue(pStmt);
				pStmt.close();
				
				// delete old entries in the same language
				pStmt=dBconn.conn.prepareStatement(
						"DELETE FROM stringtable WHERE string_key=?");
				pStmt.setInt(1,stringKey);
				pStmt.executeUpdate();
				pStmt.close();
				
				
				// create database entries for the new name
				String[] names=JSONObject.getNames(newName);
				for (int i=0; i<names.length; i++){
					pStmt= dBconn.conn.prepareStatement( 			
							 "INSERT INTO stringtable VALUES(default,?,?,?,NOW(),?)");
					pStmt.setInt(1,stringKey);
					pStmt.setString(2,names[i]);
					pStmt.setString(3,newName.getString(names[i]));
					pStmt.setInt(4,userID);
					pStmt.executeUpdate();
					pStmt.close();
				}
				
			} catch (SQLException e) {
				System.err.println("UpdateParameter: Problems with SQL query");
				status="SQL error";
			} catch (Exception e) {
				System.err.println("UpdateParameter: some error occured");
				e.printStackTrace();
				status="misc error";
			}
		}
		
		
		if (jsonIn.has("description")){
		    try{
				newDesc=jsonIn.getJSONObject("description");
				String[] descriptions=JSONObject.getNames(newDesc);

				// find the stringkey
				pStmt=dBconn.conn.prepareStatement(
						"SELECT description FROM paramdef WHERE id=?");
				pStmt.setInt(1,parameterID);
				int descriptionKey=dBconn.getSingleIntValue(pStmt);
				if (descriptionKey<1){
					pStmt=dBconn.conn.prepareStatement(
							"INSERT INTO string_key_table (description, lastchange, lastuser) VALUES (?,NOW(),?) RETURNING id");
					if (newDesc.getString(descriptions[0]).equals("")){
						pStmt.setString(1,newDesc.getString(descriptions[1]));
					}else{
						pStmt.setString(1,newDesc.getString(descriptions[0]));
					}
					pStmt.setInt(2,userID);
					descriptionKey=dBconn.getSingleIntValue(pStmt);
					pStmt=dBconn.conn.prepareStatement(
							"UPDATE paramdef SET description=? WHERE id=?");
					pStmt.setInt(1,descriptionKey);
					pStmt.setInt(2,parameterID);
					pStmt.executeUpdate();
				}

				pStmt.close();
				
				// delete old entries in the same language
				pStmt=dBconn.conn.prepareStatement(
						"DELETE FROM stringtable WHERE string_key=?");
				pStmt.setInt(1,descriptionKey);
				pStmt.executeUpdate();
				pStmt.close();
				
				
				// create database entries for the new descriptions
				for (int i=0; i<descriptions.length; i++){
					pStmt= dBconn.conn.prepareStatement( 			
							 "INSERT INTO stringtable (string_key,language,value,lastchange,lastuser) "
							+"VALUES (?,?,?,NOW(),?)");
					pStmt.setInt(1,descriptionKey);
					pStmt.setString(2,descriptions[i]);
					pStmt.setString(3,newDesc.getString(descriptions[i]));
					pStmt.setInt(4,userID);
					pStmt.executeUpdate();
					pStmt.close();
				}
			} catch (SQLException e) {
				System.err.println("UpdateParameter: Problems with SQL query");
				status="SQL error";
			} catch (Exception e) {
				System.err.println("UpdateParameter: some error occured");
				e.printStackTrace();
				status="misc error";
			}
		}
		
		
		if (jsonIn.has("unit")){
		    try{
				newUnit=jsonIn.getJSONObject("unit");
				String[] units=JSONObject.getNames(newUnit);

				// find the stringkey
				pStmt=dBconn.conn.prepareStatement(
						"SELECT stringkeyunit FROM paramdef WHERE id=?");
				pStmt.setInt(1,parameterID);
				int unitKey=dBconn.getSingleIntValue(pStmt);
				if (unitKey<1){
					pStmt=dBconn.conn.prepareStatement(
							"INSERT INTO string_key_table (description, lastchange, lastuser) VALUES (?,NOW(),?) RETURNING id");
					pStmt.setString(1,"unit: "+newUnit.getString(units[0]));
					pStmt.setInt(2,userID);
					unitKey=dBconn.getSingleIntValue(pStmt);
					pStmt=dBconn.conn.prepareStatement(
							"UPDATE paramdef SET stringkeyunit=? WHERE id=?");
					pStmt.setInt(1,unitKey);
					pStmt.setInt(2,parameterID);
					pStmt.executeUpdate();
				}

				pStmt.close();
				
				// delete old entries in the same language
				pStmt=dBconn.conn.prepareStatement(
						"DELETE FROM stringtable WHERE string_key=?");
				pStmt.setInt(1,unitKey);
				pStmt.executeUpdate();
				pStmt.close();
				
				
				// create database entries for the new units
				for (int i=0; i<units.length; i++){
					pStmt= dBconn.conn.prepareStatement( 			
							 "INSERT INTO stringtable (string_key,language,value,lastchange,lastuser) "
							+"VALUES (?,?,?,NOW(),?)");
					pStmt.setInt(1,unitKey);
					pStmt.setString(2,units[i]);
					pStmt.setString(3,newUnit.getString(units[i]));
					pStmt.setInt(4,userID);
					pStmt.executeUpdate();
					pStmt.close();
				}
			} catch (SQLException e) {
				System.err.println("UpdateParameter: Problems with SQL query");
				status="SQL error";
			} catch (Exception e) {
				System.err.println("UpdateParameter: some error occured");
				e.printStackTrace();
				status="misc error";
			}
		}
		
		
		if (jsonIn.has("regex")){
			try {
				pStmt=dBconn.conn.prepareStatement(
						"UPDATE paramdef SET regex=? WHERE id=?");
				pStmt.setString(1, jsonIn.getString("regex"));
				pStmt.setInt(2,parameterID);				
				pStmt.executeUpdate();
				pStmt.close();	
			} catch (SQLException e){
				System.err.println("UpdateParameter: SQL error reading regex field");
				status="SQL error, compulsory field";
			}catch(JSONException e) {
				System.err.println("UpdateParameter: JSON error reading regex field");
				status="JSON error, regex field";
			}
		}
		
		
		// determine datatype
		try {
			pStmt=dBconn.conn.prepareStatement(
					"SELECT datatype FROM paramdef WHERE id=?");
			pStmt.setInt(1,parameterID);				
			paramDatatype = dBconn.getSingleIntValue(pStmt);
			pStmt.close();
		} catch (SQLException e){
			System.err.println("UpdateParameter: SQL error determining datafield");
			status="SQL error, determining datafield";
		}catch(Exception e) {
			System.err.println("UpdateParameter: error determining datafield");
			status="error determining datafield";
			e.printStackTrace();
		}
		
		if (jsonIn.has("min")){
			try {
				pStmt=dBconn.conn.prepareStatement(
						"UPDATE paramdef SET min=? WHERE id=?");
				if (jsonIn.optString("min").equals("")){
					pStmt.setNull(1, java.sql.Types.DOUBLE);
				} else {
					pStmt.setDouble(1, jsonIn.getDouble("min"));
				}
				pStmt.setInt(2,parameterID);				
				pStmt.executeUpdate();
				pStmt.close();	
			} catch (SQLException e){
				System.err.println("UpdateParameter: SQL error reading min field");
				status="SQL error, min field";
			}catch(JSONException e) {
				System.err.println("UpdateParameter: JSON error reading min field");
				status="JSON error, min field";
				e.printStackTrace();
			}
		}

		if (jsonIn.has("max")){
			try {
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
			} catch (SQLException e){
				System.err.println("UpdateParameter: SQL error reading max field");
				status="SQL error, max field";
			}catch(JSONException e) {
				System.err.println("UpdateParameter: JSON error reading max field");
				status="JSON error, max field";
			}
		}
	
		if (jsonIn.has("format")){
			try {
				pStmt=dBconn.conn.prepareStatement(
						"UPDATE paramdef SET format=? WHERE id=?");
				pStmt.setString(1, jsonIn.getString("format"));
				pStmt.setInt(2,parameterID);				
				pStmt.executeUpdate();
				pStmt.close();	
			} catch (SQLException e){
				System.err.println("UpdateParameter: SQL error reading format field");
				status="SQL error, format field";
			}catch(JSONException e) {
				System.err.println("UpdateParameter: JSON error reading format field");
				status="JSON error, format field";
			}
		}

	
	
			dBconn.closeDB();

	    // tell client that everything is fine
	    Unidatoolkit.sendStandardAnswer(status,response);
	}
}	