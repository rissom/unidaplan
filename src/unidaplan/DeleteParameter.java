package unidaplan;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DeleteParameter extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DeleteParameter() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
		userID=userID+1;
		userID=userID-1;
		request.setCharacterEncoding("utf-8");
	    response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    String status="ok";
		int processID;

	
	 	
		// get Parameter for id
		try{
			 processID=Integer.parseInt(request.getParameter("id")); }
		catch (Exception e1) {
			processID=-1;
			System.err.print("DeleteParameter: no process ID given!");
			status="error: no process ID";
		}
	 	
		
	    try {
	    	PreparedStatement pstmt = null; 	
		 	DBconnection DBconn=new DBconnection(); // New connection to the database
		 	DBconn.startDB();
		 	
		 	if (processID>0){			
				// delete the process
		        pstmt = DBconn.conn.prepareStatement(	
		        	"DELETE FROM paramdef WHERE id=?");
				pstmt.setInt(1,processID);
				pstmt.executeUpdate();
				pstmt.close();
			}
		 	DBconn.closeDB();  // close the database 
	    } catch (SQLException eS) {
			System.err.println("Delete Process: SQL Error");
			status="error: SQL error";
			response.setStatus(404);
		} catch (Exception e) {
			System.err.println("DeleteParameter: Some Error, probably JSON");
			status="error: JSON error";
			response.setStatus(404);
 		} 
  
	    // tell client that everything is fine
	    Unidatoolkit.sendStandardAnswer(status,response);
		
	}


}
