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

	public class ProcessByNumber extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doGet(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {
		PreparedStatement pstmt;
	    response.setContentType("application/json");
	    request.setCharacterEncoding("utf-8");
	    response.setCharacterEncoding("utf-8");
	    int processNumber=-1;
	    int processType=-1;
		// get Parameter for number and processType
		try{
			 processNumber=Integer.parseInt(request.getParameter("number"));
			 processType=Integer.parseInt(request.getParameter("type"));
		}
		catch (Exception e1) {
			System.err.print("ProcessByNumber: no processnumber given!");
		}
	    PrintWriter out = response.getWriter();
	 	DBconnection DBconn=new DBconnection();
	    try {  
		    DBconn.startDB();
			pstmt= DBconn.conn.prepareStatement( 	
			"SELECT p_integer_data.processid FROM p_integer_data "
			+"JOIN p_parameters pp ON (p_integer_data.p_parameter_id=pp.id AND id_field=true) "
			// TODO Parameter 8 (oder war es 10??)
			+"WHERE processtypeid=? AND p_integer_data.value=?"); 
			pstmt.setInt(1, processType);
			pstmt.setInt(2, processNumber);
			JSONObject processIDObj=DBconn.jsonObjectFromPreparedStmt(pstmt);
			pstmt.close();
			out.println(processIDObj);
    	} catch (SQLException e) {
    		System.err.println("ProcessByNumber: Problems with SQL query");
    	} catch (JSONException e) {
			System.err.println("ProcessByNumber: JSON Problem while getting Stringkeys");
    	} catch (Exception e2) {
			System.err.println("ProcessByNumber: Strange Problem while getting Stringkeys");
    	}
		DBconn.closeDB();
	}}	