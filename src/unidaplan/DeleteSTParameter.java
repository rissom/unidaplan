package unidaplan;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

	public class DeleteSTParameter extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doDelete(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {		
	    
		Authentificator authentificator = new Authentificator();
		int userID = authentificator.GetUserID(request,response);
		request.setCharacterEncoding("utf-8");
	    int id = 0;
	  	  try  {
	   		 id = Integer.parseInt(request.getParameter("id")); 
	       }
	   	  catch (Exception e1) {
	   		System.err.print("DeleteSTParameter: no parameter ID given!");
	   	  }
	    String status = "ok";

	    try {
	    	// connect to database
		 	DBconnection dBconn = new DBconnection();
		    dBconn.startDB();
		    
		    if (dBconn.isAdmin(userID)){
		    	
			    // Delete the parameter
			    PreparedStatement pStmt = null;
				pStmt = dBconn.conn.prepareStatement( 			
						"DELETE FROM ot_parameters WHERE id=? ");
			   	pStmt.setInt(1, id);
			   	pStmt.executeUpdate();
				pStmt.close();
		    } else { // no admin rights
		    	response.setStatus(401);
		    }
			dBconn.closeDB();
		} catch (SQLException e) {
			System.err.println("DeleteSTParameter: Problems with SQL query");
			status = "SQL Error; DeleteSTParameter";
			response.setStatus(403);
		} catch (Exception e) {
			System.err.println("DeleteSTParameter: Strange Problems");
			status = "Error DeleteSTParameter";
			response.setStatus(403);
		}	
		
	    // tell client that everything is fine
	    Unidatoolkit.sendStandardAnswer(status, response);
	}
}	