package unidaplan;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;

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
	   	String privilege="n";
		request.setCharacterEncoding("utf-8");
	    String status="ok";
	    Boolean deletable;
	    
		PreparedStatement pStmt = null; 	// Declare variables
		int processID;
	 	DBconnection dBconn=new DBconnection(); // New connection to the database
	 	
		// get Parameter for id
		try{
			 processID=Integer.parseInt(request.getParameter("id")); }
		catch (Exception e1) {
			processID=-1;
			System.err.print("Delete Process: no process ID given!");
			status="error: no process ID";
		}
	 	
		
	    try {
		 	dBconn.startDB();
		 	if (processID>0){		
		 		 // Check privileges
			    pStmt = dBconn.conn.prepareStatement( 	
						"SELECT getProcessRights(vuserid:=?,vprocess:=?)");
				pStmt.setInt(1,userID);
				pStmt.setInt(2,processID);
				privilege = dBconn.getSingleStringValue(pStmt);
				pStmt.close();
				
				if (privilege.equals("w")){
					
					// check if files are attached to process
				    pStmt =  dBconn.conn.prepareStatement( 	
					"SELECT true "+
					"FROM files "+
					"WHERE files.process = ?");
					pStmt.setInt(1,processID);
					deletable = dBconn.getSingleBooleanValue(pStmt);
					pStmt.close();
			 		
					// delete the process
					if (deletable){
				        pStmt = dBconn.conn.prepareStatement(	
				        	"DELETE FROM processes WHERE id = ?");
						pStmt.setInt(1,processID);
						pStmt.executeUpdate();
						pStmt.close();
					} else {
						response.setStatus(403);
						status = "not deleted, files are attached";
					}
					
				} else {
					response.setStatus(401);
				}
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
	         
	    	   if (dBconn.conn != null) { 
	    		   dBconn.closeDB();  // close the database 
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
