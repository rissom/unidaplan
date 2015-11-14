package unidaplan;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

	public class DeletePTParameterGrp extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doDelete(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {		
	    
//		Authentificator authentificator = new Authentificator();
//		int userID=authentificator.GetUserID(request,response);
		request.setCharacterEncoding("utf-8");
	    String status="ok";
	    int id=0;
	  	  try  {
	   		 id=Integer.parseInt(request.getParameter("id")); 
	       }
	   	  catch (Exception e1) {
	   		System.err.print("DeletePTParameterGrp: no parametergroup ID given!");
	   		status="no parametergroup";
	   	  }


	    try {
	    // Delete the user to the database	    
	 	DBconnection DBconn=new DBconnection();
	    DBconn.startDB();	   
	    PreparedStatement pstmt = null;
			pstmt= DBconn.conn.prepareStatement( 			
					"DELETE FROM p_parametergrps WHERE id=? \n");
		   	pstmt.setInt(1, id);
		   	pstmt.executeUpdate();
			pstmt.close();
			DBconn.closeDB();
		} catch (SQLException e) {
			System.err.println("DeletePTParameterGrp: Problems with SQL query");
			status="SQL Error; DeletePTParameterGrp";
		} catch (Exception e) {
			System.err.println("DeletePTParameterGrp: Strange Problems");
			status="Error DeletePTParameterGrp";
		}	
		
	    // tell client that everything is fine
	    Unidatoolkit.sendStandardAnswer(status, response);  
	}
}	