package unidaplan;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

	public class DeleteUser extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doDelete(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {		
	    
		Authentificator authentificator = new Authentificator();
		int userID = authentificator.GetUserID(request,response);
		request.setCharacterEncoding("utf-8");
	    int id = 0;
	  	  try  {
	   		 id = Integer.parseInt(request.getParameter("id")); 
	       }
	   	  catch (Exception e1) {
	   		System.err.print("DeleteUser: no user ID given!");
	   	  }
	    String status = "ok";

	    try {
			// connect to database
		 	DBconnection dBconn = new DBconnection();
		    dBconn.startDB();	   
		    
		    if (dBconn.isAdmin(userID)){
		    	
			    // Delete the user from the database	    
			    PreparedStatement pstmt = null;
			    
			    pstmt = dBconn.conn.prepareStatement( 			
						"DELETE FROM language_preferences WHERE user_id = ?");
			   	pstmt.setInt(1, id);
			   	pstmt.executeUpdate();
				pstmt.close();
			    
				pstmt= dBconn.conn.prepareStatement( 			
						"DELETE FROM users WHERE id = ?");
			   	pstmt.setInt(1, id);
			   	pstmt.executeUpdate();
				pstmt.close();
		    }
			dBconn.closeDB();
		} catch (SQLException e) {
			System.err.println("DeleteUser: Problems with SQL query");
			status = "SQL Error; DeleteUser";
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("DeleteUser: Strange Problems");
			status = "Error DeleteUser";
		}	
		
	    // tell client that everything is fine
		response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    PrintWriter out = response.getWriter();
	    try {
	        JSONObject answer = new JSONObject();
			answer.put("status", status);
			answer.put("id", id);
			out.println(answer.toString());
		} catch (JSONException e) {
			System.err.println("DeleteUser: Problems creating JSON answer");
		}    
	}
}	