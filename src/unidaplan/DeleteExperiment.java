package unidaplan;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DeleteExperiment extends HttpServlet {
	private static final long serialVersionUID = 1L;

	

    @Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Authentificator authentificator = new Authentificator();
		int userID = authentificator.GetUserID(request,response);
		request.setCharacterEncoding("utf-8");
	    String status = "ok";
		int experimentID = -1;
		String privilege = "n";


	 	
		// get Parameter for id
		try{
			 experimentID = Integer.parseInt(request.getParameter("id")); }
		catch (Exception e1) {
			experimentID = -1;
			System.err.print("Delete Experiment: no experiment ID given!");
			status = "error: no experiment ID";
		}
	 	
		
	    try {
			PreparedStatement pStmt = null; 	// Declare variables
		 	DBconnection dBconn = new DBconnection(); // New connection to the database
		 	dBconn.startDB();
		 	
		 	
		 	
		 	if ( experimentID > 0 ){	
		 		
		 		// check privileges
			    pStmt = dBconn.conn.prepareStatement( 	
						"SELECT getExperimentRights(vuserid:=?,vexperimentid:=?)");
				pStmt.setInt(1, userID);
				pStmt.setInt(2, experimentID);
				privilege = dBconn.getSingleStringValue(pStmt);
			    
				if (privilege.equals("w")){
				  
					// delete the experiment
			        pStmt = dBconn.conn.prepareStatement(	
			        	"DELETE FROM experiments WHERE id = ?");
					pStmt.setInt(1,experimentID);
					pStmt.executeUpdate();
					pStmt.close();
				}
			}
 		   dBconn.closeDB();  // close the database 

	    } catch (SQLException eS) {
			System.err.println("Delete Experiment: SQL Error");
			status = "error: SQL error";
			response.setStatus(404);
		} catch (Exception e) {
			System.err.println("Delete Experiment: Some Error, probably JSON");
			status = "error: JSON error";
			response.setStatus(404);
		}
	    Unidatoolkit.sendStandardAnswer(status, response);
	}
}
