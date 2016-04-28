package unidaplan;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


	public class DeactivateProcessStep extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doPost(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {		
		
		Authentificator authentificator = new Authentificator();
		int userID = authentificator.GetUserID(request,response);
		int experimentID = 0;
	    int processStepID = 0;
		PreparedStatement pStmt = null;
		String privilege = "n";
		String status="ok";
		
		request.setCharacterEncoding("utf-8");
		
	  	 try{
	  	  		processStepID=Integer.parseInt(request.getParameter("processstepid")); 
	  	  	}
	  	  	catch (Exception e1) {
	  	  		System.err.print("DeactivateProcessStep: no processstep ID given!");
	  	  	}

	    try {
		    // Delete the user to the database	    
		 	DBconnection dBconn=new DBconnection();
		    dBconn.startDB();
		    
		    // get experiment ID
		    pStmt = dBconn.conn.prepareStatement( 	
					"SELECT expp_s_id FROM exp_plan_steps WHERE id=?");
			pStmt.setInt(1,processStepID);
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
			    // TODO: decrease positions behind the sample to delete
			    pStmt = dBconn.conn.prepareStatement(    	
					"DELETE FROM exp_plan_steps WHERE id=?");
			   	pStmt.setInt(1, processStepID);
			   	pStmt.executeUpdate();
				pStmt.close();
			}
			
			dBconn.closeDB();
		} catch (SQLException e) {
			System.err.println("DeactivateProcessStep: Problems with SQL query");
			status="SQL Error; DeactivateProcessStep";
		} catch (Exception e) {
			System.err.println("DeactivateProcessStep: Strange Problems");
			status="Error DeactivateProcessStep";
		}	
		
	    // tell client that everything is fine
		Unidatoolkit.sendStandardAnswer(status, response); 
	}
}	 