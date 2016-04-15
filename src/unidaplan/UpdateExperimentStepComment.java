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

	public class UpdateExperimentStepComment extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doPost(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {		
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
	    request.setCharacterEncoding("utf-8");
	    String in = request.getReader().readLine();
	    String privilege = "n";
	    String status = "ok";

	    JSONObject  jsonIn = null;	
	    int newKeyID = -1;
	    
	    try {
	    	jsonIn = new JSONObject(in);
		} catch (JSONException e) {
			System.err.println("UpdateExperimentStepComment: Input is not valid JSON");
		}
		
		response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    
	    // get the sample id, the new comment. Language is set to 'none'.
	    int stepID=-1;
	    String newComment="";
	    try {
			 stepID=jsonIn.getInt("id");
			 newComment=jsonIn.getString("comment");

		} catch (JSONException e) {
			System.err.println("UpdateExperimentStepComment: Error parsing ID-Field or comment");
			response.setStatus(404);
		}
	    
	 	DBconnection dBconn=new DBconnection();
	    PreparedStatement pStmt = null;
	    
		
		try {
		    dBconn.startDB();	   
		    
		    // check privileges
		    
	        dBconn.startDB();
	        pStmt= dBconn.conn.prepareStatement( 	
					"  SELECT exp_plan_processes.expp_id FROM exp_plan_steps "
					+ "JOIN exp_plan_processes ON exp_plan_steps.exp_plan_pr=exp_plan_steps.id "
					+ "WHERE exp_plan_steps.id=? ");
			pStmt.setInt(1,stepID);
			int expID=dBconn.getSingleIntValue(pStmt);
			pStmt.close();
	        
	        pStmt= dBconn.conn.prepareStatement( 	
					"SELECT getExperimentRights(vuserid:=?,vexperimentid:=?)");
			pStmt.setInt(1,userID);
			pStmt.setInt(2,expID);
			
			privilege=dBconn.getSingleStringValue(pStmt);
			pStmt.close();
	        	        
		} catch (SQLException e) {
			System.err.println("Showsample: Problems with SQL query for sample name");
		} catch (JSONException e) {
			System.err.println("Showsample: JSON Problem while getting sample name");
		} catch (Exception e2) {
			System.err.println("Showsample: Strange Problem while getting sample name");
			e2.printStackTrace();
		} 
		  
		
		if (privilege.equals("w")){
			
			try{
				// get the old string key.
				pStmt=dBconn.conn.prepareStatement(
						"SELECT note FROM exp_plan_steps WHERE id=?");
				pStmt.setInt(1,stepID);
				int oldKeyID = dBconn.getSingleIntValue(pStmt);
				
				// create a new stringkey
				pStmt= dBconn.conn.prepareStatement( 			
						 "INSERT INTO string_key_table VALUES (default,?,NOW(),?) RETURNING id");
				pStmt.setString(1, newComment);
				pStmt.setInt(2, userID);
				newKeyID=dBconn.getSingleIntValue(pStmt);
				pStmt.close();
				
				pStmt= dBconn.conn.prepareStatement( 			
						 "INSERT INTO stringtable VALUES(default,?,?,?,NOW())");
				pStmt.setInt(1,newKeyID);
				pStmt.setString(2, "none");
				pStmt.setString(3, newComment);
				pStmt.executeUpdate();
				pStmt.close();
				
				pStmt= dBconn.conn.prepareStatement(
						 "UPDATE exp_plan_steps SET note=?, lastUser=? WHERE id=?");
				pStmt.setInt(1,newKeyID);
				pStmt.setInt(2,userID);
				pStmt.setInt(3,stepID);
				pStmt.executeUpdate();
				pStmt.close();
				
				if (oldKeyID>0){
					pStmt= dBconn.conn.prepareStatement(
							 "DELETE FROM string_key_table WHERE id=?");
					pStmt.setInt(1,oldKeyID);
					pStmt.executeUpdate();
					pStmt.close();
				}
				
			} catch (SQLException e) {
				System.err.println("UpdateExperimentStepComment: Problems with SQL query");
				status="SQL error";
			} catch (JSONException e){
				System.err.println("UpdateExperimentStepComment: Problems creating JSON");
				status="JSON error";
			} catch (Exception e) {
				System.err.println("UpdateExperimentStepComment: Strange Problems");
				status="error";
			}	
		} else{
			response.setStatus(401);
			status="restricted";
		}
		
		dBconn.closeDB();

		
    // tell client that everything is fine
    PrintWriter out = response.getWriter();
    out.print("{\"stepid\":"+stepID+",");
	out.println("\"status\":\""+status+"\"}");
	}
}	