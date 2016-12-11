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

	public class AddRecipe extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doPost(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {		
		Authentificator authentificator = new Authentificator();
		int userID = authentificator.GetUserID(request,response);
	    request.setCharacterEncoding("utf-8");
	    String in = request.getReader().readLine();
	    String status = "ok";
	    int newRecipeID = -1;
	    int stringKeyName = 0;
	    JSONObject name = null;

	    JSONObject  jsonIn = null;	    
	    try { 
	    	  if (in != null && in.length() > 1){
	    		  jsonIn = new JSONObject(in);
			  }
		} catch (JSONException e) {
			System.err.println("AddReceipe: Input is not valid JSON");
		}

		response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    	    
	    
	 	DBconnection dBconn = new DBconnection();
	    try {
			dBconn.startDB();

		    // generate strings for the name and the unit
			if (jsonIn.has("name")){
				name = jsonIn.getJSONObject("name");
				String [] names = JSONObject.getNames(name);
				stringKeyName = dBconn.createNewStringKey(name.getString(names[0]));
				for (int i=0; i<names.length; i++){
					dBconn.addString(stringKeyName,names[i],name.getString(names[i]));
				}
			 } else {
				 name = new JSONObject();
				 name.put("de", "neues Rezept");
				 name.put("en", "new recipe");
			 }
	
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    
		
		try {	
	   
			if (userID > 0 && dBconn.isAdmin(userID)){
			    PreparedStatement pStmt = null;
			    if (jsonIn.getString("type").equals("process")){
				    pStmt = dBconn.conn.prepareStatement( 
				    		"INSERT INTO processrecipes (name,processtype,position,owner) "
				    		+ "VALUES (?,?,"
				    		+ "(SELECT max(b.position)+1 FROM processrecipes b WHERE processtype = ?), ?)"
				    		+ "RETURNING id");
					pStmt.setInt(1, stringKeyName);
					pStmt.setInt(2, jsonIn.getInt("processtype"));
					pStmt.setInt(3, jsonIn.getInt("processtype"));
					pStmt.setInt(4, userID);
			    } else {
			    	pStmt = dBconn.conn.prepareStatement( 
					    		"INSERT INTO samplerecipes (name,sampletype,position,owner) "
					    		+ "VALUES (?,?,"
					    		+ "(SELECT max(b.position)+1 FROM samplerecipes b WHERE sampletype = ?), ?) "
					    		+ "RETURNING id");
					pStmt.setInt(1, stringKeyName);
					pStmt.setInt(2, jsonIn.getInt("sampletype"));
					pStmt.setInt(3, jsonIn.getInt("sampletype"));
					pStmt.setInt(4, userID);
			    }
				pStmt.setInt(3, userID);
				newRecipeID = dBconn.getSingleIntValue(pStmt);
			}
			dBconn.closeDB();

		} catch (SQLException e) {
			System.err.println("AddReceipe: Problems with SQL query");
			status="SQL error";
		} catch (JSONException e){
			System.err.println("AddReceipe: Problems creating JSON");
			status="JSON error";
		} catch (Exception e) {
			System.err.println("AddReceipe: Strange Problems");
			e.getStackTrace();
			status="error";
		}	

    // tell client that everything is fine
    Unidatoolkit.returnID(newRecipeID, status, response);
	}
}	