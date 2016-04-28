package unidaplan;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

	public class ExpStepChangeRecipe extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doPost(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {		
	    
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
		int experimentID = 0;
		PreparedStatement pStmt;
	    int processStepID=0;
	    int newRecipe=0;
	    String privilege="n";
	    String status="ok";
	    
		request.setCharacterEncoding("utf-8");


  	  	try{
  	  		processStepID=Integer.parseInt(request.getParameter("processstepid"));
  	  		newRecipe=Integer.parseInt(request.getParameter("recipe")); 
  	  	}
  	  	catch (Exception e1) {
  	  		System.err.print("ExpStepChangeRecipe: no processstep ID given!");
  	  	}

	    try {
	    	// Connect to database
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
		    
			    pStmt = dBconn.conn.prepareStatement(    	
					"UPDATE exp_plan_steps SET recipe=? WHERE id=?");
			    pStmt.setInt(1, newRecipe);
			    pStmt.setInt(2, processStepID);
			   	pStmt.executeUpdate();
				pStmt.close();
				dBconn.closeDB();
			}
		} catch (SQLException e) {
			System.err.println("ExpStepChangeRecipe: Problems with SQL query");
			status="SQL Error; ExpStepChangeRecipe";
		} catch (Exception e) {
			System.err.println("ExpStepChangeRecipe: Strange Problems");
			status="Error ExpStepChangeRecipe";
		}	
		
	    // tell client that everything is fine
		Unidatoolkit.sendStandardAnswer(status, response);
	}
}	 