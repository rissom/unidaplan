package unidaplan;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


	public class DeleteSTParameterGrp extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doDelete(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {		
	    
//		Authentificator authentificator = new Authentificator();
//		int userID=authentificator.GetUserID(request,response);
		String status="ok";
		request.setCharacterEncoding("utf-8");
	    int id=0;
	  	  try  {
	   		 id=Integer.parseInt(request.getParameter("id")); 
	       }
	   	  catch (Exception e1) {
	   		response.setStatus(404);
	   		status="no parametergroup ID given!";
	   		System.err.print("DeleteSTParameterGrp: no parametergroup ID given!");
	   	  }

	    try {
	    // Delete the user to the database	    
	 	DBconnection DBconn=new DBconnection();
	    DBconn.startDB();	   
	    PreparedStatement pstmt = null;
			pstmt= DBconn.conn.prepareStatement( 			
					"DELETE FROM ot_parametergrps WHERE id=? \n");
		   	pstmt.setInt(1, id);
		   	pstmt.executeUpdate();
			pstmt.close();
			DBconn.closeDB();
		} catch (SQLException e) {
			System.err.println("DeleteSTParameterGrp: Problems with SQL query");
			status="SQL Error; DeleteSTParameterGrp";
			response.setStatus(403);
		} catch (Exception e) {
			System.err.println("DeleteSTParameterGrp: Strange Problems");
			status="Error DeleteSTParameterGrp";
			response.setStatus(403);
		}	
		
	    // tell client that everything is fine
	    Unidatoolkit.sendStandardAnswer(status, response);
	}
}	