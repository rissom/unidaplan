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

	public class UpdateProcessRecipeOwner extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doPut(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {		
		Authentificator authentificator = new Authentificator();
		int userID = authentificator.GetUserID(request,response);
	    request.setCharacterEncoding("utf-8");
	    String in = request.getReader().readLine();
	    String status = "ok";

	    JSONObject  jsonIn = null;	
	    int recipeID = -1;
	       
	    try {
	    	jsonIn = new JSONObject(in);
		} catch (JSONException e) {
			System.err.println("UpdateSearchOwner: Input is not valid JSON");
		}
		

	    int newOwner = -1; // get parameters
	    
	    try {
	         recipeID = jsonIn.getInt("recipeid");
			 newOwner = jsonIn.getInt("newowner");
		} catch (JSONException e) {
			System.err.println("UpdateSearchOwner: Error parsing ID-Field or comment");
			response.setStatus(404);
		}
	    
	 	DBconnection dBconn = new DBconnection(); // initialize database
	    PreparedStatement pStmt = null;
	    
		
		try {
		    dBconn.startDB();	   

		    // check privileges
		    pStmt = dBconn.conn.prepareStatement("SELECT getProcessRecipeRights(vuserid := ?, vprocessrecipe := ?)");
		    pStmt.setInt(1,userID);
		    pStmt.setInt(2,recipeID);
		    String privilege = dBconn.getSingleStringValue(pStmt);
		    
		    if (privilege.equals("w")){
		    
			    // find the stringkey
				pStmt=dBconn.conn.prepareStatement(
					"UPDATE processrecipes SET owner = ? WHERE processrecipes.id = ?");
				pStmt.setInt(1,newOwner);
				pStmt.setInt(2,recipeID);
				pStmt.executeUpdate();
				pStmt.close();			
			} else {
				response.setStatus(401);
			}
		} catch (SQLException e) {
			System.err.println("UpdateSearchOwner: Problems with SQL query");
			response.setStatus(404);
			e.printStackTrace();
			status = "SQL error";
		} catch (Exception e) {
			System.err.println("UpdateSearchOwner: some error occured");
			e.printStackTrace();
			response.setStatus(404);
			status = "misc error";
		}
		
	dBconn.closeDB();
		
    // tell client that everything is fine
    Unidatoolkit.sendStandardAnswer(status,response);
	}
}	