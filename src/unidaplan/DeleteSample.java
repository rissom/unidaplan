package unidaplan;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


public class DeleteSample extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
 

	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
	   	String privilege = "n";
		request.setCharacterEncoding("utf-8");
	    response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    PrintWriter out = response.getWriter(); 
	    
	    
		PreparedStatement pStmt = null; 	// Declare variables
		int sampleID;
	 	DBconnection dBconn=new DBconnection(); // New connection to the database
	 	
		// get Parameter for id
		try{
			 sampleID=Integer.parseInt(request.getParameter("id")); }
		catch (Exception e1) {
			sampleID=-1;
			System.err.print("Delete Sample: no object ID given!");
		}
	 	
		
	    try {
		 	dBconn.startDB();

		 	if (sampleID>0){
		 		
		 		// Check privileges
			    pStmt = dBconn.conn.prepareStatement( 	
						"SELECT getSampleRights(vuserid:=?,vsample:=?)");
				pStmt.setInt(1,userID);
				pStmt.setInt(2,sampleID);
				privilege = dBconn.getSingleStringValue(pStmt);
				pStmt.close();
				
				if (privilege.equals("w")){
		 		
			 		
			 		Boolean DeletionPossible=true;			
	
			 		// Check if processes with this sample exist
			        pStmt = dBconn.conn.prepareStatement(	
			    	"SELECT processid, sampleid FROM samplesinprocess "
			 		+"WHERE sampleid=?");
					pStmt.setInt(1,sampleID);
					ResultSet resultset=pStmt.executeQuery();
					if (resultset.next()) {DeletionPossible=false;}
					pStmt.close();
					
					// Check if experiments with this sample exist
			        pStmt = dBconn.conn.prepareStatement(	
			        	"SELECT id FROM expp_samples WHERE sample=?");
					pStmt.setInt(1,sampleID);
					resultset=pStmt.executeQuery();
					if (resultset.next()) {DeletionPossible=false;}
					pStmt.close();
					
					if (DeletionPossible){  // Really deleting the sample (OMG!)
				        pStmt = dBconn.conn.prepareStatement(	
						"DELETE FROM o_float_data WHERE objectid=?"); 
						pStmt.setInt(1,sampleID);
						pStmt.executeUpdate();
						pStmt.close();	    
						
						pStmt = dBconn.conn.prepareStatement(	
						"DELETE FROM o_measurement_data WHERE objectid=?"); 
						pStmt.setInt(1,sampleID);
						pStmt.executeUpdate();
						pStmt.close();
						
				        pStmt = dBconn.conn.prepareStatement(	
						"DELETE FROM o_string_data WHERE objectid=?"); 
						pStmt.setInt(1,sampleID);
						pStmt.executeUpdate();
						pStmt.close();
						
				        pStmt = dBconn.conn.prepareStatement(	
						"DELETE FROM o_integer_data WHERE objectid=?"); 
						pStmt.setInt(1,sampleID);
						pStmt.executeUpdate();
						pStmt.close();
						
				        pStmt = dBconn.conn.prepareStatement(	
						"DELETE FROM originates_from WHERE parent=? OR child=?");
						pStmt.setInt(1,sampleID);
						pStmt.setInt(2,sampleID);
						pStmt.executeUpdate();
						pStmt.close();
						
						// objectinprocess
				        pStmt = dBconn.conn.prepareStatement(	
						"DELETE FROM samples WHERE id=?"); 
						pStmt.setInt(1,sampleID);
						pStmt.executeUpdate();
						pStmt.close();					
					} else {
						out.println("{\"error\":\"processes or experiments with this sample exist!}\"");
					}
		 		} else {
		 			response.setStatus(401);
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
	           if (pStmt != null) { 
	        	  try {
	        	  	pStmt.close();
	        	  } catch (SQLException e) {
	        	  } 
	           }
	    	   if (dBconn.conn != null) { 
	    		   dBconn.closeDB();  // close the database 
	    	   }
	        } catch (Exception e) {
				System.err.println("DeleteSample: Some Error closing the database");
				e.printStackTrace();
		   	}
        }

		
	}


}
