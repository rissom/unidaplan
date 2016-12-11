package unidaplan;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DeleteProcessType extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DeleteProcessType() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Authentificator authentificator = new Authentificator();
		int userID = authentificator.GetUserID(request,response);

		request.setCharacterEncoding("utf-8");
	    response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    String status = "ok";
	    
		PreparedStatement pStmt = null; 	// Declare variables
		int processTypeID = 0;
		int name = 0;
		int description = 0;
	 	DBconnection dBconn = new DBconnection(); // New connection to the database
	 	
		// get Parameter for id
		try{
			 processTypeID = Integer.parseInt(request.getParameter("id")); }
		catch (Exception e1) {
			processTypeID = -1;
			System.err.print("Delete Processtype: no process ID given!");
			status = "error: no process ID";
		}
	 	
		 
		try {
			dBconn.startDB();
		} catch (Exception e1) {
			System.err.println("Delete Process: Error starting Database");
			e1.printStackTrace();
		}
			 	
		try{
			if (dBconn.isAdmin(userID)){
		
			
			 	if (processTypeID>0){			
					// get string_key_table references for later deletion
			        pStmt = dBconn.conn.prepareStatement(	
			        	"SELECT name FROM processtypes WHERE id = ?");
					pStmt.setInt(1,processTypeID);
					name = dBconn.getSingleIntValue(pStmt);
			        pStmt = dBconn.conn.prepareStatement(	
				        	"SELECT description FROM processtypes WHERE id = ?");
					pStmt.setInt(1,processTypeID);
					description = dBconn.getSingleIntValue(pStmt);
				}
		   
			 	if ( processTypeID > 0 ){	
			 			 		
					// delete all associatied parameters
			        pStmt = dBconn.conn.prepareStatement(	
			        	"DELETE FROM p_parameters WHERE processtypeid=?");
					pStmt.setInt(1, processTypeID);
					pStmt.executeUpdate();
					pStmt.close();
			 		
					// delete the processtype
			        pStmt = dBconn.conn.prepareStatement(	
			        	"DELETE FROM processtypes WHERE id=?");
					pStmt.setInt(1, processTypeID);
					pStmt.executeUpdate();
					pStmt.close();	
		
					// delete the stringkeys
			        pStmt = dBconn.conn.prepareStatement(	
			        	"DELETE FROM string_key_table WHERE id IN (?,?)");
					pStmt.setInt(1,name);
					pStmt.setInt(2,description);
					pStmt.executeUpdate();
					pStmt.close();
				}
			
			} else {
				response.setStatus(401);
			} 
		} catch (SQLException eS) {
			System.err.println("Delete Processtype: SQL Error");
			status = "error: SQL error";
		} catch (Exception e) {
			System.err.println("Delete Processtype: Some Error, probably JSON");
			status = "error: JSON error";
		} 
	   
	    dBconn.closeDB();  // close the database 
	    Unidatoolkit.sendStandardAnswer(status, response);
	}
}
