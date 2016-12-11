package unidaplan;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



	public class DeletePTSRParameter extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doDelete(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {		
	    
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
		
		DBconnection dBconn = null;
	    PreparedStatement pStmt = null;

		
		request.setCharacterEncoding("utf-8");
	    int id=0;
	  	  try  {
	   		 id=Integer.parseInt(request.getParameter("id")); 
	       }
	   	  catch (Exception e1) {
	   		System.err.print("DeletePTSRParameter: no parameter ID given!");
	   	  }
	    String status = "ok";

	    try {
		    // Delete the parameter
		 	dBconn = new DBconnection();
		    dBconn.startDB();	   
		    
			if (dBconn.isAdmin(userID)){
				pStmt = dBconn.conn.prepareStatement(
						 "DELETE FROM po_parameters WHERE id=? ");
			   	pStmt.setInt(1, id);
			   	pStmt.executeUpdate();
				pStmt.close();
			} else {
				response.setStatus(401);
			}
		
		} catch (SQLException e) {
			System.err.println("DeletePTSRParameter: Problems with SQL query");
			status = "SQL Error; DeletePTSRParameter";
		} catch (Exception e) {
			System.err.println("DeletePTSRParameter: Strange Problems");
			status = "Error DeletePTSRParameter";
		}	
		dBconn.closeDB();

		
	    // tell client that everything is fine
		Unidatoolkit.sendStandardAnswer(status, response);   
	}
}	