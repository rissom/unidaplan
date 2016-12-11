package unidaplan;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//WebServlet("/change-experiment-status")
public class ChangeExperimentStatus extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
		@Override
		  public void doGet(HttpServletRequest request, HttpServletResponse response)
			      throws ServletException, IOException {
			
			Authentificator authentificator = new Authentificator();
		    PreparedStatement pStmt = null;
		   	String privilege = "n";
			int userID = authentificator.GetUserID(request,response);

			String status = "ok";
		    request.setCharacterEncoding("utf-8");
		    // look up the datatype in Database	    
		    int experimentID = -1;
		    int processstatus = -1;
		  	try {
		   		 experimentID = Integer.parseInt(request.getParameter("id")); 
		    } catch (Exception e1) {
		   		System.err.println("no experiment ID given!");
				response.setStatus(404);
		   	}
		    try {
		    	processstatus = Integer.parseInt(request.getParameter("status")); 
			} catch (Exception e1) {
		   		System.err.println("no status given!");
		   		status="no status given!";
				response.setStatus(404);
			}
		    

			try {	
				// Connect to database
			 	DBconnection dBconn = new DBconnection();
			    dBconn.startDB();	   
			    
			    // Check privileges
			    pStmt = dBconn.conn.prepareStatement( 	
						"SELECT getExperimentRights(vuserid:=?,vexperimentid:=?)");
				pStmt.setInt(1,userID);
				pStmt.setInt(2,experimentID);
				privilege = dBconn.getSingleStringValue(pStmt);
				
				if (privilege.equals("w")){
			    
					pStmt = dBconn.conn.prepareStatement( 			
							 "UPDATE experiments SET status=? WHERE id=?");
				   	pStmt.setInt(2, experimentID);
				   	pStmt.setInt(1, processstatus);
				   	pStmt.executeUpdate();
				   	pStmt.close();
				} else {
					response.setStatus(401);
				}
				dBconn.closeDB();
			} catch (SQLException e) {
				System.err.println("SaveSampleParameter: Problems with SQL query");
				status = "SaveSampleParameter: Problems with SQL query";
				response.setStatus(404);
			} catch (Exception e) {
				System.err.println("SaveSampleParameter: Strange Problems");
				status = "SaveSampleParameter: Strange Problems";
				response.setStatus(404);
			}
			
	    // tell client that everything is fine
	    Unidatoolkit.sendStandardAnswer(status, response);
	    
	}	
}