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

	public class GetUser extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doGet(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {
		PreparedStatement pstmt;
	    response.setContentType("application/json");
	    request.setCharacterEncoding("utf-8");
	    response.setCharacterEncoding("utf-8");
	    int userID=-1;
		// get Parameter for id
		try{
			 userID=Integer.parseInt(request.getParameter("id")); }
		catch (Exception e1) {
			System.err.print("User: no user ID given!");
		}
	    PrintWriter out = response.getWriter();
	 	DBconnection DBconn=new DBconnection();
	    DBconn.startDB();
	    try {  
			pstmt= DBconn.conn.prepareStatement( 	
			"SELECT users.id, users.name, users.email, " 
		   +"users.lastchange "
		   +"FROM users WHERE id=?");
			pstmt.setInt(1, userID);
			JSONObject user=DBconn.jsonObjectFromPreparedStmt(pstmt);
			pstmt.close();			
			out.println(user.toString());
			DBconn.closeDB();
    	} catch (SQLException e) {
    		System.err.println("GetUsers: Problems with SQL query");
    	} catch (JSONException e) {
			System.err.println("GetUsers: JSON Problem while getting Stringkeys");
    	} catch (Exception e2) {
			System.err.println("GetUsers: Strange Problem while getting Stringkeys");
    	}
	}}	