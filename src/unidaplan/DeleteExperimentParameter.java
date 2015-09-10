package unidaplan;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DeleteExperimentParameter extends HttpServlet {
	private static final long serialVersionUID = 1L;

	

    @Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
		userID=userID+1; // REMOVE ME!!!
		userID=userID-1; // REMOVE ME!!!
		request.setCharacterEncoding("utf-8");
	    response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    PrintWriter out = response.getWriter(); 
	    String status="ok";
	    
		PreparedStatement pstmt = null; 	// Declare variables
		int experimentID;
	 	DBconnection DBconn=new DBconnection(); // New connection to the database
	 	DBconn.startDB();
	 	
		// get Parameter for id
		try{
			 experimentID=Integer.parseInt(request.getParameter("id")); }
		catch (Exception e1) {
			experimentID=-1;
			System.err.print("DeleteExperimentParameter: no experiment Parameter ID given!");
			status="error: no experiment ID";
		}
	 	
		
	    try {
		 	if (experimentID>0){			
				// delete the experiment
		        pstmt = DBconn.conn.prepareStatement(	
		        	"DELETE FROM expp_param WHERE id=?");
				pstmt.setInt(1,experimentID);
				pstmt.executeUpdate();
				pstmt.close();
			}
	    } catch (SQLException eS) {
			System.err.println("DeleteExperimentParameter: SQL Error");
			status="error: SQL error";
			eS.printStackTrace();
		} catch (Exception e) {
			System.err.println("DeleteExperimentParameter: Some Error, probably JSON");
			status="error: JSON error";
			e.printStackTrace();
		} finally {
		try{	
	         
	    	   if (DBconn.conn != null) { 
	    		   DBconn.closeDB();  // close the database 
	    	   }
	        } catch (Exception e) {
				status="error: error closing the database";
				System.err.println("DeleteExperimentParameter: Some Error closing the database");
				e.printStackTrace();
		   	}
        }
	    out.println("{\"status:\":\""+status+"\"}");

		
	}


}
