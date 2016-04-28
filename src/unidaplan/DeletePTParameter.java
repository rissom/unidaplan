package unidaplan;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;



	public class DeletePTParameter extends HttpServlet {
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
	   		System.err.print("DeletePTParameter: no parameter ID given!");
	   	  }
	    String status="ok";

	    try {
		    // Delete the parameter
		 	dBconn=new DBconnection();
		    dBconn.startDB();	   
		    
			if (Unidatoolkit.userHasAdminRights(userID, dBconn)){
				pStmt= dBconn.conn.prepareStatement( 			
						"DELETE FROM p_parameters WHERE id=? \n");
			   	pStmt.setInt(1, id);
			   	pStmt.executeUpdate();
				pStmt.close();
			} else {
				response.setStatus(401);
			}
		
		} catch (SQLException e) {
			System.err.println("DeletePTParameter: Problems with SQL query");
			status="SQL Error; DeletePTParameter";
		} catch (Exception e) {
			System.err.println("DeletePTParameter: Strange Problems");
			status="Error DeletePTParameter";
		}	
		dBconn.closeDB();

		
	    // tell client that everything is fine
		Unidatoolkit.sendStandardAnswer(status, response);   
	}
}	