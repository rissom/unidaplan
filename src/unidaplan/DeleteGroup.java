package unidaplan;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DeleteGroup extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public DeleteGroup() {
        super();
        // TODO Auto-generated constructor stub
    }

	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
		request.setCharacterEncoding("utf-8");
	    response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    String status="ok";
		int groupID;

	 	
		// get Parameter for id
		try{
			 groupID=Integer.parseInt(request.getParameter("id")); }
		catch (Exception e1) {
			groupID=-1;
			System.err.print("DeleteGroup: no group ID given!");
			status="error: no group ID";
		}
	 	
		
	    try {
	    	PreparedStatement pstmt = null; 	
		 	DBconnection dBconn=new DBconnection(); // New connection to the database
		 	dBconn.startDB();
			if (Unidatoolkit.userHasAdminRights(userID, dBconn)){
			 	if (groupID>0){			
					// delete the process
			        pstmt = dBconn.conn.prepareStatement(	
			        	"DELETE FROM groups WHERE id=?");
					pstmt.setInt(1,groupID);
					pstmt.executeUpdate();
					pstmt.close();
				}
			} else {
				response.setStatus(401);
			}
		 	dBconn.closeDB();  // close the database }
	    } catch (SQLException eS) {
			System.err.println("DeleteGroup: SQL Error");
			status="error: SQL error";
			response.setStatus(404);
		} catch (Exception e) {
			System.err.println("DeleteGroup: Some Error, probably JSON");
			status="error: JSON error";
			response.setStatus(404);
		}
  
	    // tell client that everything is fine
	    Unidatoolkit.sendStandardAnswer(status,response);
	}
}
