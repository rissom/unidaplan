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
		PreparedStatement pstmt;
	    response.setContentType("application/json");
	    request.setCharacterEncoding("utf-8");
	    response.setCharacterEncoding("utf-8");
	    PrintWriter out = response.getWriter();
	 	DBconnection DBconn=new DBconnection();
	    DBconn.startDB();
	    try {  
			pstmt= DBconn.conn.prepareStatement( 	
			"SELECT max(users.id) AS id, max(users.name) AS name, max(users.email) AS email, " 
		   +"max(users.lastchange) AS lastchange, "
		   +"CASE count(ep.id) WHEN 0 THEN true ELSE false END AS deletable "
		   +"FROM users "
		   +"LEFT JOIN exp_plan ep ON users.id=ep.creator "
		   +"GROUP BY users.name ");
			JSONArray users=DBconn.jsonArrayFromPreparedStmt(pstmt);
			pstmt.close();			
			out.println(users.toString());
			DBconn.closeDB();
    	} catch (SQLException e) {
    		System.err.println("GetUsers: Problems with SQL query");
    	} catch (JSONException e) {
			System.err.println("GetUsers: JSON Problem while getting Stringkeys");
    	} catch (Exception e2) {
			System.err.println("GetUsers: Strange Problem while getting Stringkeys");
    	}
	}}	