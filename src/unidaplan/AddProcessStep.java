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

	public class AddProcessStep extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doPost(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {		
		// Add a processstep to an experiment
	    
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
		request.setCharacterEncoding("utf-8");
	    int experimentID=0;
	    int expProcessID=0;


	  	  	try{
	  	  		experimentID=Integer.parseInt(request.getParameter("expsample")); 
	  	  		expProcessID=Integer.parseInt(request.getParameter("pprocess")); 
	  	  	}
	  	  	catch (Exception e1) {
	  	  		System.err.print("AddProcessStep: Parameters missing!");
	  	  	}
	    String status="ok";

	    try {
	    // Delete the user to the database	    
	 	DBconnection DBconn=new DBconnection();
	    DBconn.startDB();	   
	    PreparedStatement pstmt = null;
			pstmt= DBconn.conn.prepareStatement( 			
					"INSERT INTO exp_plan_steps VALUES(default,?,?,"+
					"(SELECT recipe FROM exp_plan_processes WHERE exp_plan_processes.id=?),NULL,NOW(),?);");
		   	pstmt.setInt(1, expProcessID);
		   	pstmt.setInt(2, experimentID);
		   	pstmt.setInt(3, experimentID);
		   	pstmt.setInt(4, userID);
		   	pstmt.executeUpdate();
			pstmt.close();
			DBconn.closeDB();
		} catch (SQLException e) {
			System.err.println("AddProcessStep: Problems with SQL query");
			status="SQL Error; AddProcessStep";
		} catch (Exception e) {
			System.err.println("AddProcessStep: Strange Problems");
			status="Error AddProcessStep";
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
			System.err.println("AddProcessStep: Problems creating JSON answer");
		}    
	}
}	