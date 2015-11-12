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
	    
//		Authentificator authentificator = new Authentificator();
//		int userID=authentificator.GetUserID(request,response);
		System.out.println("halloooooo!");
		request.setCharacterEncoding("utf-8");
	    int id=0;
	  	  try  {
	   		 id=Integer.parseInt(request.getParameter("id")); 
	       }
	   	  catch (Exception e1) {
	   		System.err.print("DeleteSTParameter: no parameter ID given!");
	   	  }
	    String status="ok";

	    try {
	    // Delete the parameter
	 	DBconnection DBconn=new DBconnection();
	    DBconn.startDB();	   
	    PreparedStatement pstmt = null;
			pstmt= DBconn.conn.prepareStatement( 			
					"DELETE FROM ot_parameters WHERE id=? \n");
		   	pstmt.setInt(1, id);
		   	pstmt.executeUpdate();
			pstmt.close();
			DBconn.closeDB();
		} catch (SQLException e) {
			System.err.println("DeleteSTParameter: Problems with SQL query");
			status="SQL Error; DeleteSTParameter";
		} catch (Exception e) {
			System.err.println("DeleteSTParameter: Strange Problems");
			status="Error DeleteSTParameter";
		}	
		
	    // tell client that everything is fine
		response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    Unidatoolkit.sendStandardAnswer(status, response);
	}
}	