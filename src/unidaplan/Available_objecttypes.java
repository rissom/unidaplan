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

	public class Available_objecttypes extends HttpServlet {
		private static final long serialVersionUID = 1L;
		private static JSONArray result;
		private static PreparedStatement pstmt;



	@Override
	  public void doGet(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {

		request.setCharacterEncoding("utf-8");
	    response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    PrintWriter out = response.getWriter();
	    DBconnection DBconn=new DBconnection();
	    DBconn.startDB();
	    String plang="de"; // primary language = Deutsch
		String slang="en"; // secondary language = english
		try {	
			pstmt= DBconn.conn.prepareStatement( 	
			"SELECT COALESCE (a.value,b.value) as type \n"
		    + "FROM objecttypes \n"
		    + "JOIN stringtable a ON (objecttypes.string_key=a.string_key AND a.language=? \n)"
		    + "JOIN stringtable b ON (objecttypes.string_key=b.string_key AND b.language=? \n)");
			pstmt.setString(1, plang);
			pstmt.setString(2, slang);
		result=DBconn.jsonFromPreparedStmt(pstmt);
		} catch (SQLException e) {
			System.out.println("Problems with SQL query for sample name");
			e.printStackTrace();	
		} catch (JSONException e) {
			System.out.println("JSON Problem while getting sample name");
			e.printStackTrace();
		} catch (Exception e2) {
			System.out.println("Strange Problem while getting sample name");
			e2.printStackTrace();
		}   
		out.println(result.toString());
		DBconn.closeDB();
	}
}	