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

	public class ExpStepChangeRecipe extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doPost(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {		
	    
//		Authentificator authentificator = new Authentificator();
//		int userID=authentificator.GetUserID(request,response);
		request.setCharacterEncoding("utf-8");
	    int processStepID=0;
	    int newRecipe=0;
	  	  	try{
	  	  		processStepID=Integer.parseInt(request.getParameter("processstepid"));
	  	  		newRecipe=Integer.parseInt(request.getParameter("recipe")); 
	  	  	}
	  	  	catch (Exception e1) {
	  	  		System.err.print("ExpStepChangeRecipe: no processstep ID given!");
	  	  	}
	    String status="ok";

	    try {
		    // Delete the user to the database	    
		 	DBconnection DBconn=new DBconnection();
		    DBconn.startDB();	   
		    // decrease positions behind the sample to delete
		    PreparedStatement pstmt = DBconn.conn.prepareStatement(    	
				"UPDATE exp_plan_steps SET recipe=? WHERE id=?");
		    pstmt.setInt(1, newRecipe);
		    pstmt.setInt(2, processStepID);
		   	pstmt.executeUpdate();
			pstmt.close();
			DBconn.closeDB();
		} catch (SQLException e) {
			System.err.println("ExpStepChangeRecipe: Problems with SQL query");
			status="SQL Error; ExpStepChangeRecipe";
		} catch (Exception e) {
			System.err.println("ExpStepChangeRecipe: Strange Problems");
			status="Error ExpStepChangeRecipe";
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
			System.err.println("ExpStepChangeRecipe: Problems creating JSON answer");
		}    
	}
}	 