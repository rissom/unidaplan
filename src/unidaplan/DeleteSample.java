package unidaplan;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

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
		        pstmt = DBconn.conn.prepareStatement(	
		    	"SELECT processid, objectid FROM objectinprocess "
		 		+"WHERE objectid=?");
				pstmt.setInt(1,objID);
				ResultSet resultset=pstmt.executeQuery();
				if (!(resultset.next())) {
					pstmt.close();
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
					pstmt.close();
					out.println("{\"error\":\"processes exist}\"");
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
