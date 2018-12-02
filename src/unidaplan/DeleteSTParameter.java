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
	            PreparedStatement pStmt = null;

		        // Check if parameter is a formula
		        pStmt = dBconn.conn.prepareStatement(             
                          "SELECT  " 
                        + "     ((blabla.count) IS NULL OR NOT formula IS NULL) as deletable " 
                        + "FROM ot_parameters " 
                        + "LEFT JOIN ( " 
                        + "    SELECT " 
                        + "        count(a.id), " 
                        + "        ot_parameter_id "  
                        + "    FROM sampledata a " 
                        + "    GROUP BY ot_parameter_id ) AS blabla ON blabla.ot_parameter_id = ot_parameters.id "  
                        + "WHERE ot_parameters.ID = ?");
		        pStmt.setInt(1, id);
		        Boolean deletable = dBconn.getSingleBooleanValue(pStmt);
		        
		        if (deletable) {   // Delete the parameter and all of its data (should only do this when the parameter is a formula)
		            pStmt = dBconn.conn.prepareStatement(           
                            "DELETE FROM sampledata WHERE ot_parameter_id=? ");
                    pStmt.setInt(1, id);
                    pStmt.executeUpdate();
		            pStmt.close();
		            
	                pStmt = dBconn.conn.prepareStatement(           
	                        "DELETE FROM ot_parameters WHERE id=? ");
	                pStmt.setInt(1, id);
	                pStmt.executeUpdate();
	                pStmt.close();
		        }
			    
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