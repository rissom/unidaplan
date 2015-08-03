package unidaplan;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class DeleteSample
 */
@WebServlet("/delete-sample")
public class DeleteSample extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DeleteSample() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
		request.setCharacterEncoding("utf-8");
	    response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    PrintWriter out = response.getWriter(); 
	    
	    
		PreparedStatement pstmt = null; 	// Declare variables
		int objID;
	 	DBconnection DBconn=new DBconnection(); // New connection to the database
	 	DBconn.startDB();
	 	
		// get Parameter for id
		try{
			 objID=Integer.parseInt(request.getParameter("id")); }
		catch (Exception e1) {
			objID=-1;
			System.err.print("Delete Sample: no object ID given!");
		}
	 	
		
	    try {
		 	if (objID>0){
		 		Boolean DeletionPossible=true;			

		 		// Check if processes with this sample exist
		        pstmt = DBconn.conn.prepareStatement(	
		    	"SELECT processid, sampleid FROM samplesinprocess "
		 		+"WHERE objectid=?");
				pstmt.setInt(1,objID);
				ResultSet resultset=pstmt.executeQuery();
				if (resultset.next()) {DeletionPossible=false;}
				pstmt.close();
				
				// Check if experiments with this sample exist
		        pstmt = DBconn.conn.prepareStatement(	
		        	"SELECT id FROM expp_samples WHERE sample=?");
				pstmt.setInt(1,objID);
				resultset=pstmt.executeQuery();
				if (resultset.next()) {DeletionPossible=false;}
				pstmt.close();
				
				if (DeletionPossible){  // Really deleting the sample (OMG!)
			        pstmt = DBconn.conn.prepareStatement(	
					"DELETE FROM o_float_data WHERE objectid=?"); 
					pstmt.setInt(1,objID);
					pstmt.executeUpdate();
					pstmt.close();	    
					
					pstmt = DBconn.conn.prepareStatement(	
					"DELETE FROM o_measurement_data WHERE objectid=?"); 
					pstmt.setInt(1,objID);
					pstmt.executeUpdate();
					pstmt.close();
					
			        pstmt = DBconn.conn.prepareStatement(	
					"DELETE FROM o_string_data WHERE objectid=?"); 
					pstmt.setInt(1,objID);
					pstmt.executeUpdate();
					pstmt.close();
					
			        pstmt = DBconn.conn.prepareStatement(	
					"DELETE FROM o_integer_data WHERE objectid=?"); 
					pstmt.setInt(1,objID);
					pstmt.executeUpdate();
					pstmt.close();
					
			        pstmt = DBconn.conn.prepareStatement(	
					"DELETE FROM originates_from WHERE parent=? OR child=?");
					pstmt.setInt(1,objID);
					pstmt.setInt(2,objID);
					pstmt.executeUpdate();
					pstmt.close();
					
					// objectinprocess
			        pstmt = DBconn.conn.prepareStatement(	
					"DELETE FROM samples WHERE id=?"); 
					pstmt.setInt(1,objID);
					pstmt.executeUpdate();
					pstmt.close();					
				} else {
					out.println("{\"error\":\"processes or experiments with this sample exist!}\"");
					}
			}
	    } catch (SQLException eS) {
			System.err.println("DeleteSample: SQL Error");
			eS.printStackTrace();
		} catch (Exception e) {
			System.err.println("DeleteSample: Some Error, probably JSON");
			e.printStackTrace();
		} finally {
		try{	
	           if (pstmt != null) { 
	        	  try {
	        	  	pstmt.close();
	        	  } catch (SQLException e) {
	        	  } 
	           }
	    	   if (DBconn.conn != null) { 
	    		   DBconn.closeDB();  // close the database 
	    	   }
	        } catch (Exception e) {
				System.err.println("DeleteSample: Some Error closing the database");
				e.printStackTrace();
		   	}
        }

		
	}


}
