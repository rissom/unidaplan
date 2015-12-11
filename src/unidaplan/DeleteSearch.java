package unidaplan;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DeleteSearch extends HttpServlet {
	private static final long serialVersionUID = 1L;

	

    @Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
		userID=userID+1; // REMOVE ME!!!
		userID=userID-1; // REMOVE ME!!!
		request.setCharacterEncoding("utf-8");
	    String status="ok";
		int searchID=-1;

	 	
		// get the id
		try{
			 searchID=Integer.parseInt(request.getParameter("searchid")); }
		catch (Exception e1) {
			searchID=-1;
			System.err.print("DeleteSearch: no search ID given!");
			status="error: no search ID";
			response.setStatus(404);
		}
	 	
		
	    try {
			PreparedStatement pstmt = null; 	// Declare variables
		 	DBconnection DBconn=new DBconnection(); // New connection to the database
		 	DBconn.startDB();
		 	if (searchID>0){			
				// delete the search
		        pstmt = DBconn.conn.prepareStatement(	
		        	"DELETE FROM searches WHERE id=?");
				pstmt.setInt(1,searchID);
				pstmt.executeUpdate();
				pstmt.close();
			}
 		   DBconn.closeDB();  // close the database 

	    } catch (SQLException eS) {
			System.err.println("DeleteSearch: SQL Error");
			status="error: SQL error";
			response.setStatus(404);
		} catch (Exception e) {
			System.err.println("DeleteSearch: Some Error, probably JSON");
			status="error: JSON error";
			response.setStatus(404);
		}
	    
	    
	    Unidatoolkit.sendStandardAnswer(status, response);

	}


}
