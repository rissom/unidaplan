package unidaplan;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DeleteProcess extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DeleteProcess() {
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
	    String status="ok";
	    
		PreparedStatement pstmt = null; 	// Declare variables
		int processID;
	 	DBconnection DBconn=new DBconnection(); // New connection to the database
	 	
		// get Parameter for id
		try{
			 processID=Integer.parseInt(request.getParameter("id")); }
		catch (Exception e1) {
			processID=-1;
			System.err.print("Delete Process: no process ID given!");
			status="error: no process ID";
		}
	 	
		
	    try {
		 	DBconn.startDB();
		 	if (processID>0){			
				// delete the process
		        pstmt = DBconn.conn.prepareStatement(	
		        	"DELETE FROM processes WHERE id=?");
				pstmt.setInt(1,processID);
				pstmt.executeUpdate();
				pstmt.close();
			}
	    } catch (SQLException eS) {
			System.err.println("Delete Process: SQL Error");
			status="error: SQL error";
			eS.printStackTrace();
		} catch (Exception e) {
			System.err.println("Delete Process: Some Error, probably JSON");
			status="error: JSON error";
			e.printStackTrace();
		} finally {
		try{	
	         
	    	   if (DBconn.conn != null) { 
	    		   DBconn.closeDB();  // close the database 
	    	   }
	        } catch (Exception e) {
				status="error: error closing the database";
				System.err.println("Delete Process: Some Error closing the database");
				e.printStackTrace();
		   	}
        }
	    Unidatoolkit.sendStandardAnswer(status, response);

		
	}


}
