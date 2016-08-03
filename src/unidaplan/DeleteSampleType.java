package unidaplan;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

public class DeleteSampleType extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DeleteSampleType() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Authentificator authentificator = new Authentificator();
		int userID = authentificator.GetUserID(request,response);
		int name = 0;
		request.setCharacterEncoding("utf-8");
	    String status = "ok";
	    
		PreparedStatement pStmt = null; 	// Declare variables
		int sampleTypeID = 0;
		int description = 0;
	 	DBconnection dBconn = new DBconnection(); // New connection to the database
	 	
		// get Parameter for id
		try{
			dBconn.startDB();
			sampleTypeID=Integer.parseInt(request.getParameter("id")); }
		catch (Exception e1) {
			sampleTypeID = -1;
			System.err.print("DeleteSampleType: no sampleType ID given!");
			status="error: no process ID";
		}
	 	

	 	if (sampleTypeID>0){	
	 		if (Unidatoolkit.userHasAdminRights(userID, dBconn)){
	 			try {
			 		
					// get string_key_table references for later deletion
			        pStmt = dBconn.conn.prepareStatement(	
			        	"SELECT string_key,description FROM objecttypes WHERE id = ?");
					pStmt.setInt(1,sampleTypeID);
					JSONObject ot=dBconn.jsonObjectFromPreparedStmt(pStmt);
					pStmt.close();
					name = ot.getInt("string_key");
			        if (ot.has("description")){
			        	description = ot.getInt("description");
			        } else description=0;
					pStmt.close();
					dBconn.conn.setAutoCommit(false);
			    } catch (SQLException eS) {
					System.err.println("DeleteSampleType: SQL Error");
					status="error: SQL error";
					eS.printStackTrace();
				} catch (Exception e) {
					System.err.println("DeleteSampleType: Some Error, probably JSON");
					status="error: JSON error";
				}
		 
	 			try {
				 	if (sampleTypeID>0){
				 		// delete the parameters
				        pStmt = dBconn.conn.prepareStatement(	
				        	"DELETE FROM ot_parameters WHERE objecttypesid=?");
						pStmt.setInt(1,sampleTypeID);
						pStmt.executeUpdate();
						pStmt.close();
						
						// delete the sampletype
				        pStmt = dBconn.conn.prepareStatement(	
				        	"DELETE FROM objecttypes WHERE id=?");
						pStmt.setInt(1,sampleTypeID);
						pStmt.executeUpdate();
						pStmt.close();
					}
	 			} catch (SQLException eS) {
	 				System.err.println("Delete Process: SQL Error");
	 				status="error: SQL error";
	 				eS.printStackTrace();
				} catch (Exception e) {
					System.err.println("Delete Sample Type: Some Error, probably JSON");
					status="error: JSON error";
				}
		 
			    try {
				 	if (sampleTypeID>0){			
						// delete the stringkeys
				        pStmt = dBconn.conn.prepareStatement(
				        	"DELETE FROM string_key_table WHERE id IN (?,?)");
						pStmt.setInt(1,name);
						pStmt.setInt(2,description);
						pStmt.executeUpdate();
						pStmt.close();
					}
			    } catch (SQLException eS) {
					System.err.println("DeleteSampleType: SQL Error");
					eS.printStackTrace();
					status="error: SQL error";
					response.setStatus(404);
					try {
						dBconn.conn.rollback();
					} catch (SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (Exception e) {
					System.err.println("DeleteSampleType: Some Error occurred");
					status="error";
					response.setStatus(404);
					try {
						dBconn.conn.rollback();
					} catch (SQLException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
		
				} 
		    } else {
		    	response.setStatus(401);
		    }
		    
			try{	
				if (dBconn.conn != null) { 
					dBconn.conn.setAutoCommit(true);
	    		    dBconn.closeDB();  // close the database 
	    	    }
	        }catch (Exception e) {
				status="error: error closing the database";
				System.err.println("DeleteSampleType: Some Error closing the database");
				e.printStackTrace();
		   	}
	    }
	Unidatoolkit.sendStandardAnswer(status, response);
}
}