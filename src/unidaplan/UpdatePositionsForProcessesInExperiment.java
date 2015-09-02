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

	public class UpdatePositionsForProcessesInExperiment extends HttpServlet {
		/* Updates the positions for the default processes of an experiment */
		private static final long serialVersionUID = 1L;

	@Override
	  public void doPost(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {		
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
		userID=userID+1;
		userID=userID-1;
	    request.setCharacterEncoding("utf-8");
	    String in = request.getReader().readLine();
	    String status = "ok";

	    JSONArray  jsonIn = null;	    
	    try {
	    		
			  jsonIn = new JSONArray(in);
		} catch (JSONException e) {
			System.err.println("UpdatePositionsForProcessesInExperiment: Input is not valid JSON");
		}
		
		response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");

	 	DBconnection DBconn=new DBconnection();
	    DBconn.startDB();	   
	    PreparedStatement pstmt = null;
	    
		
		try {	
			pstmt= DBconn.conn.prepareStatement(  
					"UPDATE exp_plan_processes SET position=? WHERE id=?");
				for (int i=0; i<jsonIn.length();i++){
					pstmt.setInt(1, jsonIn.getJSONObject(i).getInt("position"));
					pstmt.setInt(2, jsonIn.getJSONObject(i).getInt("id"));
					pstmt.addBatch();
				}
			pstmt.executeBatch();


		} catch (SQLException e) {
			System.err.println("UpdatePositionsForProcessesInExperiment: Problems with SQL query");
			status="SQL error";
		} catch (JSONException e){
			System.err.println("UpdatePositionsForProcessesInExperiment: Problems creating JSON");
			status="JSON error";
		} catch (Exception e) {
			System.err.println("UpdatePositionsForProcessesInExperiment: Strange Problems");
			status="error";
		}	
		
		DBconn.closeDB();

		
    // tell client that everything is fine
    PrintWriter out = response.getWriter();
	out.println("{\"status\":\""+status+"\"}");
	}
}	