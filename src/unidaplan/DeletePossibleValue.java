package unidaplan;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DeletePossibleValue extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
    @Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Authentificator authentificator = new Authentificator();
		int userID = authentificator.GetUserID(request,response);
		request.setCharacterEncoding("utf-8");
	    String status = "ok";
		int id = -1;
	 	
		// get the id
		try{
			 id = Integer.parseInt(request.getParameter("id"));
		}
		catch (Exception e1) {
			System.err.print("DeletePossibleValue: no search ID given!");
			status = "error: no search ID";
			response.setStatus(404);
		}
	 	
	 	DBconnection dBconn = new DBconnection(); // initialize database
	    PreparedStatement pStmt = null;
	    
	    try {
		    dBconn.startDB();
			if (dBconn.isAdmin(userID)){
		    	// get basic search data (id,name,owner,operation)
				pStmt = dBconn.conn.prepareStatement( 	
				    "DELETE FROM possible_values WHERE id=?");
				pStmt.setInt(1, id);
				pStmt.executeUpdate();
				pStmt.close();   
			} else{
				response.setStatus(401);
			}
 		    dBconn.closeDB();  // close the database 
	    } catch (SQLException eS) {
			System.err.println("DeletePossibleValue: SQL Error");
			status = "error: SQL error";
			response.setStatus(404);
		} catch (Exception e) {
			System.err.println("DeletePossibleValue: Some Error, probably JSON");
			status = "error: JSON error";
			response.setStatus(404);
		}
	    
	    Unidatoolkit.sendStandardAnswer(status, response);
	}
}
