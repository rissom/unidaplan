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

	public class ReplaceSampleInExperiment extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doPost(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {		
	    
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
	    int sampleInExpID=0;
	    int experimentID=0;
	    int newSampleId=0;
	    PreparedStatement pStmt = null;
	    String privilege = "n";

	    
		request.setCharacterEncoding("utf-8");
	  	  	try{
	  	  		sampleInExpID=Integer.parseInt(request.getParameter("id")); 
	  	  		newSampleId=Integer.parseInt(request.getParameter("sampleid"));
	  	  	}
	  	  	catch (Exception e1) {
	  	  		System.err.print("ReplaceSampleInExperiment: no sampleID or no id given!");
	  	  	}
	    String status="ok";

	    try {
		    // Connect to database	    
		 	DBconnection dBconn=new DBconnection();
		    dBconn.startDB();
		    
		    
		    // get Experiment ID
		    pStmt = dBconn.conn.prepareStatement( 	
					"SELECT expp_id FROM expp_samples WHERE id=?");
			pStmt.setInt(1,sampleInExpID);
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
				pStmt= dBconn.conn.prepareStatement( 			
						"UPDATE expp_samples SET sample=? WHERE id=?");
			   	pStmt.setInt(1, newSampleId);
			   	pStmt.setInt(2, sampleInExpID);
			   	pStmt.executeUpdate();
				pStmt.close();
			} else{
				response.setStatus(401);
			}
			dBconn.closeDB();

		} catch (SQLException e) {
			System.err.println("ReplaceSampleInExperiment: Problems with SQL query");
			status="SQL Error; ReplaceSampleInExperiment";
		} catch (Exception e) {
			System.err.println("ReplaceSampleInExperiment: Strange Problems");
			status="Error DeleteSampleFromExperiment";
		}	
		
	    // tell client that everything is fine
		response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    PrintWriter out = response.getWriter();
	    try {
	        JSONObject answer = new JSONObject();
			answer.put("status", status);
			answer.put("id", sampleInExpID);
			out.println(answer.toString());
		} catch (JSONException e) {
			System.err.println("ReplaceSampleInExperiment: Problems creating JSON answer");
		}    
	}
}	