package unidaplan;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;

	public class GetUsers extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doGet(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {
//		Authentificator authentificator = new Authentificator();
//		int userID=authentificator.GetUserID(request,response);
		PreparedStatement pstmt;
	    response.setContentType("application/json");
	    request.setCharacterEncoding("utf-8");
	    response.setCharacterEncoding("utf-8");
//	    Authentificator authentificator = new Authentificator();
//		int userID=authentificator.GetUserID(request,response);
	    PrintWriter out = response.getWriter();
	 	DBconnection DBconn=new DBconnection();
	    try {  
		    DBconn.startDB();
			pstmt= DBconn.conn.prepareStatement( 	
			"SELECT users.id, "
			+ "users.fullname, "
			+ "users.username, "
			+ "users.email, "
		    + "users.blocked, "
		    + "users.lastchange, "
		   +"CASE Coalesce((SELECT count(ep.creator) from experiments ep WHERE users.id=ep.creator " 
		   +"GROUP BY ep.creator),0) WHEN 0 THEN true ELSE false END AS deletable "
		   +"FROM users");
			JSONArray users=DBconn.jsonArrayFromPreparedStmt(pstmt);
			pstmt.close();			
			out.println(users.toString());
			DBconn.closeDB();
    	} catch (SQLException e) {
    		System.err.println("GetUsers: Problems with SQL query");
    	} catch (JSONException e) {
			System.err.println("GetUsers: JSON Problem while getting Stringkeys");
    	} catch (Exception e2) {
			System.err.println("GetUsers: Strange Problem getting Stringkeys");
			e2.printStackTrace();
    	}
	}}	