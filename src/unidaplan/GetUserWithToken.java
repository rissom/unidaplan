package unidaplan;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

	public class GetUserWithToken extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doGet(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {
		PreparedStatement pStmt;
	    response.setContentType("application/json");
	    request.setCharacterEncoding("utf-8");
	    response.setCharacterEncoding("utf-8");
	    int userID = -1;
	    String token = "";
		// get Parameter for id
		try{
			 userID = Integer.parseInt(request.getParameter("id"));
			 token = request.getParameter("token");
		}
		catch (Exception e1) {
			System.err.print("User: no user ID given!");
		}
	    PrintWriter out = response.getWriter();
	 	DBconnection dBconn = new DBconnection();
	    try {  
		    dBconn.startDB();
			pStmt = dBconn.conn.prepareStatement(
					   "SELECT "
					 + "  id,"
					 + "  fullname,"
					 + "  username,"
					 + "  email,"
					 + "  lastchange,"
					 + "  token,"
					 + "  token_valid_to " 
				     + "FROM users "
				     + "WHERE id = ?");
			pStmt.setInt(1, userID);
			JSONObject user = dBconn.jsonObjectFromPreparedStmt(pStmt);
		   	String validToString = user.optString("token_valid_to");
		   	Timestamp validToDate = Timestamp.valueOf(validToString);
			if (user.getString("token").equals(token) &&
					validToDate.getTime()>System.currentTimeMillis()){
				out.println(user.toString());
			} else {
				System.out.println("wrong or timedout token");
				out.println("{\"error\":\"invalid token\"}");
			}
			dBconn.closeDB();
    	} catch (SQLException e) {
    		System.err.println("GetUser: Problems with SQL query");
    	} catch (JSONException e) {
			System.err.println("GetUser: JSON Problem while getting Stringkeys");
    	} catch (Exception e2) {
			System.err.println("GetUser: Strange Problem while getting Stringkeys");
			e2.printStackTrace();
    	}
	}
}	