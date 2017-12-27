package unidaplan;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

	public class ReorderPossibleValues extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doPut(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {
		
		Authentificator authentificator = new Authentificator();
		String status = "ok";
		int userID = authentificator.GetUserID(request,response);
	    request.setCharacterEncoding("utf-8");
	    String in = request.getReader().readLine();
	    JSONObject  jsonIn = null;	    
	    try {
			  jsonIn = new JSONObject(in);
		} catch (JSONException e) {
			System.err.println("ReorderPossibleValues: Input is not valid JSON");
		}

	   
	    try {
		    // Initialize Database
			DBconnection dBconn = new DBconnection();
		    dBconn.startDB();
		    
		 	// check if admin
		 	if (dBconn.isAdmin(userID)){
		    
			    PreparedStatement pStmt = null;
	    		
	    		JSONArray newOrder = jsonIn.getJSONArray("neworder");
	
	    		pStmt= dBconn.conn.prepareStatement( 			
						 "UPDATE possible_values SET (position,lastchange,lastuser)=(?,now(),?) WHERE id=?");
		    	for (int i=0; i<newOrder.length(); i++){
				   	pStmt.setInt(1, i+1);
				   	pStmt.setInt(2, userID);
				   	pStmt.setInt(3, newOrder.getInt(i));
					pStmt.addBatch();  // Does not work. I don't know why.
		    	}
				pStmt.executeBatch();
				pStmt.close();
		 	}else{
		 		response.setStatus(401);
		 	}
			dBconn.closeDB();
		} catch (JSONException e) {
			System.err.println("ReorderPossibleValues: Error parsing ID-Field");
			status = "Error parsing ID-Field";
			response.setStatus(404);
		} catch (SQLException e) {
			System.err.println("ReorderPossibleValues: Problems with SQL query");
			status = "SQL Error";
			response.setStatus(404);
		} catch (Exception e) {
			System.err.println("ReorderPossibleValues: Strange Problems");
			status = "Misc Error";
			response.setStatus(404);
		}

		
    // tell client that everything is fine
    Unidatoolkit.sendStandardAnswer(status, response);
	}
}	