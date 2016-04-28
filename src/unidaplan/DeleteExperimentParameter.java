package unidaplan;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DeleteExperimentParameter extends HttpServlet {
	private static final long serialVersionUID = 1L;

	

    @Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
		String privilege = "n";
		request.setCharacterEncoding("utf-8");
	    String status="ok";
	    
		PreparedStatement pStmt = null; 	// Declare variables
		int experimentID;
		int parameterID = 0;
	 	DBconnection dBconn=new DBconnection(); // New connection to the database
	 	
		// get Parameter for id
		try{
			parameterID=Integer.parseInt(request.getParameter("id")); }
		catch (Exception e1) {
			experimentID=-1;
			System.err.print("DeleteExperimentParameter: no experiment Parameter ID given!");
			status="error: no experiment ID";
		}
	 	
		
	    try {
		 	dBconn.startDB();
		 	if (parameterID>0){
		 		
		 		// get Experiment ID
			    pStmt = dBconn.conn.prepareStatement( 	
						"SELECT exp_plan_id FROM expp_param WHERE id=?");
				pStmt.setInt(1,parameterID);
				experimentID = dBconn.getSingleIntValue(pStmt);
				pStmt.close();
		 		
		 		// check privileges
			    pStmt = dBconn.conn.prepareStatement( 	
						"SELECT getExperimentRights(vuserid:=?,vexperimentid:=?)");
				pStmt.setInt(1,userID);
				pStmt.setInt(2,experimentID);
				privilege = dBconn.getSingleStringValue(pStmt);
				pStmt.close();
			    
				if (privilege.equals("w")){
			 		
					// delete the experimentparameter
			        pStmt = dBconn.conn.prepareStatement(	
			        	"DELETE FROM expp_param WHERE id=?");
					pStmt.setInt(1,parameterID);
					pStmt.executeUpdate();
					pStmt.close();
				}
			}
	    } catch (SQLException eS) {
			System.err.println("DeleteExperimentParameter: SQL Error");
			status="error: SQL error";
			eS.printStackTrace();
		} catch (Exception e) {
			System.err.println("DeleteExperimentParameter: Some Error, probably JSON");
			status="error: JSON error";
			e.printStackTrace();
		} finally {
		try{	
	         
	    	   if (dBconn.conn != null) { 
	    		   dBconn.closeDB();  // close the database 
	    	   }
	        } catch (Exception e) {
				status="error: error closing the database";
				System.err.println("DeleteExperimentParameter: Some Error closing the database");
				e.printStackTrace();
		   	}
        }
	    Unidatoolkit.sendStandardAnswer(status, response);

		
	}


}
