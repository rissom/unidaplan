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
	    
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
		
		DBconnection dBconn = null;
	    PreparedStatement pStmt = null;

		
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
		 	dBconn=new DBconnection();
		    dBconn.startDB();	   
		    if (Unidatoolkit.userHasAdminRights(userID, dBconn)){
				pStmt= dBconn.conn.prepareStatement( 			
						"DELETE FROM p_parametergrps WHERE id=? \n");
			   	pStmt.setInt(1, id);
			   	pStmt.executeUpdate();
				pStmt.close();
		    } else{
		    	response.setStatus(401);
		    }
			dBconn.closeDB();
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