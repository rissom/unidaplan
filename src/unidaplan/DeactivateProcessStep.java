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

	public class DeactivateProcessStep extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doPost(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {		
	    
//		Authentificator authentificator = new Authentificator();
//		int userID=authentificator.GetUserID(request,response);
		request.setCharacterEncoding("utf-8");
	    int processStepID=0;
	  	  	try{
	  	  		processStepID=Integer.parseInt(request.getParameter("processstepid")); 
	  	  	}
	  	  	catch (Exception e1) {
	  	  		System.err.print("DeactivateProcessStep: no user ID given!");
	  	  	}
	    String status="ok";

	    try {
		    // Delete the user to the database	    
		 	DBconnection DBconn=new DBconnection();
		    DBconn.startDB();	   
		    // decrease positions behind the sample to delete
		    PreparedStatement pstmt = DBconn.conn.prepareStatement(    	
				"DELETE FROM exp_plan_steps WHERE id=?");
		   	pstmt.setInt(1, processStepID);
		   	pstmt.executeUpdate();
			pstmt.close();
			DBconn.closeDB();
		} catch (SQLException e) {
			System.err.println("DeactivateProcessStep: Problems with SQL query");
			status="SQL Error; DeactivateProcessStep";
		} catch (Exception e) {
			System.err.println("DeactivateProcessStep: Strange Problems");
			status="Error DeactivateProcessStep";
		}	
		
	    // tell client that everything is fine
		response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    PrintWriter out = response.getWriter();
	    try {
	        JSONObject answer = new JSONObject();
			answer.put("status", status);
			out.println(answer.toString());
		} catch (JSONException e) {
			System.err.println("DeactivateProcessStep: Problems creating JSON answer");
		}    
	}
}	 