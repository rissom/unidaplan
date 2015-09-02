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

	public class DeleteProcessFromExperiment extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doPost(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {		
	    
//		Authentificator authentificator = new Authentificator();
//		int userID=authentificator.GetUserID(request,response);
		request.setCharacterEncoding("utf-8");
	    int id=0;
	  	  	try{
	  	  		id=Integer.parseInt(request.getParameter("id")); 
	  	  	}
	  	  	catch (Exception e1) {
	  	  		System.err.print("DeleteProcessFromExperiment: no user ID given!");
	  	  	}
	    String status="ok";

	    try {
		    // Delete the user to the database	    
		 	DBconnection DBconn=new DBconnection();
		    DBconn.startDB();	   
		    // decrease positions behind the sample to delete
		    PreparedStatement pstmt = DBconn.conn.prepareStatement(    		
	    		"UPDATE exp_plan_processes SET position = position - 1 "
		    	+"WHERE expp_id=(SELECT expp_id FROM exp_plan_processes WHERE id=?) " 
		    	+"AND position>(SELECT position FROM exp_plan_processes WHERE id=?)");
	    	pstmt.setInt(1,id);
	    	pstmt.setInt(2,id);
	    	pstmt.executeUpdate();
			pstmt= DBconn.conn.prepareStatement( 			
					"DELETE FROM exp_plan_processes WHERE id=?");
		   	pstmt.setInt(1, id);
		   	pstmt.executeUpdate();
			pstmt.close();
			DBconn.closeDB();
		} catch (SQLException e) {
			System.err.println("DeleteProcessFromExperiment: Problems with SQL query");
			status="SQL Error; DeleteProcessFromExperiment";
		} catch (Exception e) {
			System.err.println("DeleteProcessFromExperiment: Strange Problems");
			status="Error DeleteProcessFromExperiment";
		}	
		
	    // tell client that everything is fine
		response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    PrintWriter out = response.getWriter();
	    try {
	        JSONObject answer = new JSONObject();
			answer.put("status", status);
			answer.put("id", id);
			out.println(answer.toString());
		} catch (JSONException e) {
			System.err.println("DeleteProcessFromExperiment: Problems creating JSON answer");
		}    
	}
}	 