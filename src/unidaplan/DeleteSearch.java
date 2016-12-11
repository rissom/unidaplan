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
		String privilege = "n";
		request.setCharacterEncoding("utf-8");
	    String status = "ok";
		int searchID = -1;

	 	
		// get the id
		try{
			 searchID = Integer.parseInt(request.getParameter("searchid")); }
		catch (Exception e1) {
			searchID = -1;
			System.err.print("DeleteSearch: no search ID given!");
			status = "error: no search ID";
			response.setStatus(404);
		}
	 	
		
	    try {
			PreparedStatement pStmt = null; 	// Declare variables
		 	DBconnection dBconn=new DBconnection(); // New connection to the database
		 	dBconn.startDB();
		 	
		 	
		 	
		 	if (searchID>0){			
		 		
		 		// Check privileges
			    pStmt = dBconn.conn.prepareStatement( 	
						"SELECT getExperimentRights(vuserid:=?,vexperimentid:=?)");
				pStmt.setInt(1,userID);
				pStmt.setInt(2,searchID);
				privilege = dBconn.getSingleStringValue(pStmt);
				pStmt.close();
							
				if (privilege.equals("w")){
		 		
					// delete the search
			        pStmt = dBconn.conn.prepareStatement(	
			        	"DELETE FROM searches WHERE id = ?");
					pStmt.setInt(1,searchID);
					pStmt.executeUpdate();
					pStmt.close();
				} else {
					response.setStatus(401);
				}
			}
 		   dBconn.closeDB();  // close the database 

	    } catch (SQLException eS) {
			System.err.println("DeleteSearch: SQL Error");
			status = "error: SQL error";
			response.setStatus(404);
		} catch (Exception e) {
			System.err.println("DeleteSearch: Some Error, probably JSON");
			status = "error: JSON error";
			response.setStatus(404);
		}
	    Unidatoolkit.sendStandardAnswer(status, response);
	}
}
