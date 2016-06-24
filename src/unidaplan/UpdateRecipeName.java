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

	public class UpdateRecipeName extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doPut(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {		
		Authentificator authentificator = new Authentificator();
		int userID = authentificator.GetUserID(request,response);
	    request.setCharacterEncoding("utf-8");
	    String in = request.getReader().readLine();
	    String privilege = "n";
		String query = "";
	    String status = "ok";

	    JSONObject  jsonIn = null;	
	    int recipeID = -1;
	    String language ="";
	    
    
	    try {
			  jsonIn = new JSONObject(in);
		} catch (JSONException e) {
			System.err.println("UpdateRecipeName: Input is not valid JSON");
		}
		

	    String newName=""; // get parameters
	    
	    try {
	         recipeID = jsonIn.getInt("id");
			 language = jsonIn.getString("language");
			 newName = jsonIn.getString("name");
		} catch (JSONException e) {
			e.printStackTrace();
			System.err.println("UpdateRecipeName: Error parsing ID-Field or comment");
			response.setStatus(404);
		}
	    
	 	DBconnection dBconn = new DBconnection(); // initialize database
	    PreparedStatement pStmt = null;
	    
		
		try {
		    dBconn.startDB();
		    
		    // Check privileges
		    if (jsonIn.getString("type").equals("process")){
		    	query =	"SELECT getProcessRecipeRights(vuserid := ?, vprocessrecipe := ?)";
		    } else {
		    	query = "SELECT getSampleRecipeRights(vuserid := ?, vsamplerecipe := ?)";
		    }
		    pStmt = dBconn.conn.prepareStatement(query);
		    pStmt.setInt(1, userID);
			pStmt.setInt(2, recipeID);
			privilege = dBconn.getSingleStringValue(pStmt);		    	
			pStmt.close();

						
			if (privilege.equals("w")){

				// find the stringkey

			    if (jsonIn.getString("type").equals("process")){
			    	
			    }
			    if (jsonIn.getString("type").equals("process")){
			    	query = "SELECT name FROM processrecipes WHERE id=?";
			    }else{
			    	query = "SELECT name FROM samplerecipes WHERE id=?";
			    }
				pStmt = dBconn.conn.prepareStatement(query);
				pStmt.setInt(1,recipeID);
				int stringKey=dBconn.getSingleIntValue(pStmt);
				pStmt.close();
				
				// delete old entries in the same language
				pStmt=dBconn.conn.prepareStatement(
						"DELETE FROM stringtable WHERE language=? AND string_key=?");
				pStmt.setString(1,language);
				pStmt.setInt(2,stringKey);
				pStmt.executeUpdate();
				pStmt.close();
				
			
				// create database entry for the new name
				pStmt= dBconn.conn.prepareStatement( 			
						 "INSERT INTO stringtable VALUES(default,?,?,?,NOW(),?)");
				pStmt.setInt(1,stringKey);
				pStmt.setString(2, language);
				pStmt.setString(3, newName);
				pStmt.setInt(4,userID);
				pStmt.executeUpdate();
				pStmt.close();
			} else {
				response.setStatus(401);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("UpdateRecipeName: Problems with SQL query");
			status="SQL error";
		} catch (Exception e) {
			System.err.println("UpdateRecipeName: some error occured");
			status="misc error";
		}
		
		dBconn.closeDB();

		
    // tell client that everything is fine
    Unidatoolkit.sendStandardAnswer(status,response);
	}
}	