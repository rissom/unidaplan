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

	public class MarkAllProcessesInExperiment extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doPost(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {		
	    
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
	    PreparedStatement pStmt = null;
	   	String privilege="n";
		
		request.setCharacterEncoding("utf-8");
	    int processID=0;
	    int experimentID=0;
	  	  	try{
	  	  		experimentID=Integer.parseInt(request.getParameter("experimentid"));
	  	  		processID=Integer.parseInt(request.getParameter("processid"));
	  	  	}
	  	  	catch (Exception e1) {
	  	  		System.err.print("MarkAllProcessesInExperiment: no sampleID or no id given!");
	  	  	}
	    String status="ok";

	    try {
		 	DBconnection dBconn=new DBconnection();
		    dBconn.startDB();
		    
		    // Check privileges
		    pStmt = dBconn.conn.prepareStatement( 	
					"SELECT getExperimentRights(vuserid:=?,vexperimentid:=?)");
			pStmt.setInt(1,userID);
			pStmt.setInt(2,experimentID);
			privilege = dBconn.getSingleStringValue(pStmt);
			pStmt.close();
			
			if (privilege.equals("w")){	    
		    
				pStmt= dBconn.conn.prepareStatement( 			
					"INSERT INTO exp_plan_steps (exp_plan_pr, recipe, expp_s_id, note, lastuser) "
					+"SELECT exp_plan_processes.id AS exp_plan_pr, "
					+" exp_plan_processes.recipe, expp_samples.id AS expp_s_id, exp_plan_processes.note, ? "
					+"FROM expp_samples "
					+"JOIN exp_plan_processes ON (expp_samples.expp_id=exp_plan_processes.expp_id) " 
					+"LEFT JOIN exp_plan_steps ON (exp_plan_steps.expp_s_id=expp_samples.id ) " 
					+" AND (exp_plan_steps.exp_plan_pr=exp_plan_processes.id) "
					+"WHERE expp_samples.expp_id=? AND exp_plan_processes.id=? AND exp_plan_steps.id IS NULL");
			   	pStmt.setInt(1, userID);
			   	pStmt.setInt(2, experimentID);
			   	pStmt.setInt(3, processID);
			   	pStmt.executeUpdate();
				pStmt.close();
				
			}else{
				response.setStatus(401);
			}
			dBconn.closeDB();
		} catch (SQLException e) {
			System.err.println("MarkAllProcessesInExperiment: Problems with SQL query");
			status="SQL Error; MarkAllProcessesInExperiment";
		} catch (Exception e) {
			System.err.println("MarkAllProcessesInExperiment: Strange Problems");
			status="Error DeleteSampleFromExperiment";
		}	
		
	    // tell client that everything is fine
		response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    PrintWriter out = response.getWriter();
	    try {
	        JSONObject answer = new JSONObject();
			answer.put("status", status);
			out.println(answer.toString());
		} catch (JSONException e) {
			System.err.println("MarkAllProcessesInExperiment: Problems creating JSON answer");
		}    
	}
}	