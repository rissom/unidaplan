package unidaplan;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

	public class AddSamplesToProcess extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doPost(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {		
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
	    request.setCharacterEncoding("utf-8");
	    String in = request.getReader().readLine();
	    int pid=-1;
	    JSONObject  jsonIn = null;	    
	    try {
			  jsonIn = new JSONObject(in);
		} catch (JSONException e) {
			System.err.println("AddSamplesToProcess: Input is not valid JSON");
		}
		response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    
	    // get the process id
	    int id=0;
	    int processid=-1;
	    try {
//			 processid=Integer.parseInt(request.getParameter("processid")); 
			 processid=jsonIn.getInt("processid");			 
		} catch (JSONException e) {
			System.err.println("AddSampleParameter: Error parsing process ID-Field");
			response.setStatus(404);
		}
	      ArrayList<Integer> samples = new ArrayList<Integer>(); // Array for translation strings

	    
	    // look up the datatype in Database	    
	 	DBconnection DBconn=new DBconnection();
	    DBconn.startDB();	   
	    PreparedStatement pstmt = null;
		try {	
			pstmt= DBconn.conn.prepareStatement( 			
					 "SELECT paramdef.datatype FROM Ot_parameters otp \n"
					+"JOIN paramdef ON otp.definition=paramdef.id \n"
					+"WHERE otp.id=? \n");
		   	pstmt.setInt(1, id);
		   	JSONObject answer=DBconn.jsonObjectFromPreparedStmt(pstmt);

		} catch (SQLException e) {
			System.err.println("AddSamplesToProcess: Problems with SQL query");
		} catch (JSONException e){
			System.err.println("AddSamplesToProcess: Problems creating JSON");
		} catch (Exception e) {
			System.err.println("AddSamplesToProcess: Strange Problems");
		}	
		
		DBconn.closeDB();

		
    // tell client that everything is fine
    PrintWriter out = response.getWriter();
    out.print("{\"pid\":"+pid+",");
	out.println("\"status\":\"ok\"}");
	}
}	