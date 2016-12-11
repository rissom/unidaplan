package unidaplan;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ExpStepChangeRecipe extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	  public void doPost(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {		
	    
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
		int experimentID = 0;
		PreparedStatement pStmt;
	    int processStepID = 0;
	    int newRecipe = 0;
	    String privilege = "n";
	    String status = "ok";
	    
		request.setCharacterEncoding("utf-8");


  	  	try{
  	  		processStepID = Integer.parseInt(request.getParameter("processstepid"));
  	  		if (request.getParameter("recipe").equals("null")){
  	  			newRecipe = 0;
  	  		}else{
  	  			newRecipe = Integer.parseInt(request.getParameter("recipe"));
  	  		}
  	  	}
  	  	catch (Exception e1) {
  	  		System.err.print("ExpStepChangeRecipe: no processstep ID given!");
  	  	}

	    try {
	    	// Connect to database
		 	DBconnection dBconn = new DBconnection();
		    dBconn.startDB();	   
		    
		    // get experiment ID
		    pStmt = dBconn.conn.prepareStatement( 	
					"SELECT expp_s_id FROM exp_plan_steps WHERE id=?");
			pStmt.setInt(1,processStepID);
			experimentID = dBconn.getSingleIntValue(pStmt);
	
	 		// check privileges
		    pStmt = dBconn.conn.prepareStatement( 	
					"SELECT getExperimentRights(vuserid:=?,vexperimentid:=?)");
			pStmt.setInt(1,userID);
			pStmt.setInt(2,experimentID);
			privilege = dBconn.getSingleStringValue(pStmt);
		    
			if (privilege.equals("w")){
				
			    pStmt = dBconn.conn.prepareStatement(    	
			    		"UPDATE exp_plan_steps SET (recipe,lastuser) = (?,?) WHERE id = ?");
				if (newRecipe > 0){
					pStmt.setInt(1, newRecipe);
				} else{
					pStmt.setNull(1, java.sql.Types.INTEGER);
				}
			    pStmt.setInt(2, userID);
			    pStmt.setInt(3, processStepID);
			   	pStmt.executeUpdate();
				pStmt.close();
				dBconn.closeDB();
			}
		} catch (SQLException e) {
			System.err.println("ExpStepChangeRecipe: Problems with SQL query");
			e.printStackTrace();
			status="SQL Error; ExpStepChangeRecipe";
		} catch (Exception e) {
			System.err.println("ExpStepChangeRecipe: Strange Problems");
			status="Error ExpStepChangeRecipe";
		}
		
	    // tell client that everything is fine
		Unidatoolkit.sendStandardAnswer(status, response);
	}
}	 