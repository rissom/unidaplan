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

	public class GetActiveSessionUser extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doGet(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {
		Authentificator authentificator = new Authentificator();
		int userID = authentificator.GetUserID(request,response);
		PreparedStatement pStmt;
		JSONObject user = null;
	    response.setContentType("application/json");
	    request.setCharacterEncoding("utf-8");
	    response.setCharacterEncoding("utf-8");
	    PrintWriter out = response.getWriter();
	    if (userID > 0){
		 	DBconnection dBconn = new DBconnection();
		    try {  
			    dBconn.startDB();
				pStmt = dBconn.conn.prepareStatement(
						"SELECT "
					  + "	users.id,"
					  + "	users.fullname, " 
					  + "	users.username, "
					  + "	users.preferredlanguage, "	
					  + "   NOT gm IS NULL AS admin "
					  + "FROM users "
					  + "LEFT JOIN groupmemberships gm ON (gm.userid = users.id AND gm.groupid = 1) " // member of Admin group? 
					  + "WHERE users.id = ?");
				pStmt.setInt(1, userID);
				user = dBconn.jsonObjectFromPreparedStmt(pStmt);					
			    
				dBconn.closeDB();
				out.println(user.toString());
			    } catch (SQLException e) {
		    		System.err.println("GetUser: Problems with SQL query");
		    	} catch (JSONException e) {
					System.err.println("GetUser: JSON Problem");
		    	} catch (Exception e2) {
					System.err.println("GetUser: Strange Problem");
					e2.printStackTrace();
		    	}
		    }else{
				out.println("{\"status\":\"no session\"}");
		    }
    	
	}}	