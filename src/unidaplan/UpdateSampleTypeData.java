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

	public class UpdateSampleTypeData extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doPost(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {
		
		Authentificator authentificator = new Authentificator();
		String status="ok";
		int userID=authentificator.GetUserID(request,response);
	    request.setCharacterEncoding("utf-8");
	    String in = request.getReader().readLine();
	    JSONObject  jsonIn = null;
	    try {
			  jsonIn = new JSONObject(in);
		} catch (JSONException e) {
			System.err.println("UpdateSampleTypeData: Input is not valid JSON");
		}
		response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    
	    
	    
	    // get id of corresponding stringkey
	    int objectTypeID=0;
	    int id=0;
	    int stringkey=0;
	    String newValue = null;
	    String lang = null;
		DBconnection dBconn=new DBconnection();
	    PreparedStatement pStmt = null;

	    try {
		    dBconn.startDB();	   
			objectTypeID=jsonIn.getInt("sampletypeid");
			String field=jsonIn.getString("field");
			lang=jsonIn.getString("lang");
			newValue=jsonIn.getString("newvalue");
			
			pStmt= dBconn.conn.prepareStatement( 			
					 "SELECT string_key,description FROM objecttypes WHERE objecttypes.id=?");
			pStmt.setInt(1,  objectTypeID);
			JSONObject sampleType=dBconn.jsonObjectFromPreparedStmt(pStmt);
//			System.out.println("st: "+sampleType);
			
			if (field.equals("name")) { 
				stringkey=sampleType.getInt("string_key");
			} else{ // field is description
				if (sampleType.has("description")){
					stringkey=sampleType.getInt("description");
				}
			}
			
			Boolean newKey=false; // could change.
			
			// if a stringkey exists: try to get id of stringtable field.
			if (stringkey==0){ // We have no stringkey
				stringkey=dBconn.createNewStringKey(newValue);
				newKey=true;
			} 	    
			
		   	dBconn.addString(stringkey, lang, newValue);
		   	
		   	
			if (newKey && field.equals("description")){ // We have no stringkey
				pStmt= dBconn.conn.prepareStatement( 			
						 "UPDATE objecttypes SET description=? WHERE id=?");
				pStmt.setInt(1, stringkey);
				pStmt.setInt(2, objectTypeID);
				pStmt.executeUpdate();
				pStmt.close();
			} 	
			dBconn.closeDB();
			
			
		} catch (SQLException e) {
			System.err.println("UpdateSampleTypeData: Even More Problems with SQL query");
			status = "SQL Error";
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("UpdateSampleTypeData: More Strange Problems");
			status = "Misc. Error";
		}
		
	    
	    // tell client that everything is fine
	    Unidatoolkit.sendStandardAnswer(status, response);
	}
}	