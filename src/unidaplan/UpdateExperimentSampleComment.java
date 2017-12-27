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

	public class UpdateExperimentSampleComment extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doPost(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {		
		Authentificator authentificator = new Authentificator();
		int userID = authentificator.GetUserID(request,response);
	    request.setCharacterEncoding("utf-8");
	    String in = request.getReader().readLine();
	    String status = "ok";
	    String privilege = "n";
	    
	    JSONObject  jsonIn = null;	
	    int newKeyID = -1;
	    
	    try {
			  jsonIn = new JSONObject(in);
		} catch (JSONException e) {
			System.err.println("UpdateExperimentProcessComment: Input is not valid JSON");
		}
		
		response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    
	    // get the sample id, the new comment. Language is set to 'none'.
	    int sampleID=-1;
	    String newComment="";
	    try {
			 sampleID=jsonIn.getInt("id");
			 newComment=jsonIn.getString("comment");

		} catch (JSONException e) {
			System.err.println("UpdateExperimentProcessComment: Error parsing ID-Field or comment");
			response.setStatus(404);
		}
	    
	 	DBconnection dBconn=new DBconnection();
	    PreparedStatement pStmt = null;
	    
		
		try {
		    dBconn.startDB();	   
		    
		    // Check privileges
		    // get corresponding experiment ID
 			pStmt = dBconn.conn.prepareStatement(
 					"SELECT expp_id FROM expp_samples WHERE id=?");
 			pStmt.setInt(1,sampleID);
 			int experimentID = dBconn.getSingleIntValue(pStmt);
 			pStmt.close();
		 			
		    pStmt = dBconn.conn.prepareStatement( 	
					"SELECT getExperimentRights(vuserid:=?,vexperimentid:=?)");
			pStmt.setInt(1,userID);
			pStmt.setInt(2,experimentID);
			privilege = dBconn.getSingleStringValue(pStmt);
			pStmt.close();
			
			
			if (privilege.equals("w")){
		    
				// get the old string key.
				pStmt = dBconn.conn.prepareStatement(
						"SELECT note FROM expp_samples WHERE id=?");
				pStmt.setInt(1,sampleID);
				int oldKeyID = dBconn.getSingleIntValue(pStmt);
				
				// create a new stringkey
				pStmt = dBconn.conn.prepareStatement( 			
						 "INSERT INTO string_key_table VALUES (default,?,NOW(),?) RETURNING id");
				pStmt.setString(1, newComment);
				pStmt.setInt(2, userID);
				newKeyID = dBconn.getSingleIntValue(pStmt);
				pStmt.close();
				
				pStmt = dBconn.conn.prepareStatement( 			
						 "INSERT INTO stringtable VALUES(default,?,?,?,NOW())");
				pStmt.setInt(1,newKeyID);
				pStmt.setString(2, "none");
				pStmt.setString(3, newComment);
				pStmt.executeUpdate();
				pStmt.close();
				
				pStmt = dBconn.conn.prepareStatement(
						 "UPDATE expp_samples SET note=?, lastUser=? WHERE id=?");
				pStmt.setInt(1,newKeyID);
				pStmt.setInt(2,userID);
				pStmt.setInt(3,sampleID);
				pStmt.executeUpdate();
				pStmt.close();
				
				if (oldKeyID>0){
					pStmt = dBconn.conn.prepareStatement(
							 "DELETE FROM string_key_table WHERE id=?");
					pStmt.setInt(1,oldKeyID);
					pStmt.executeUpdate();
					pStmt.close();
				}		
			} else {
		    	response.setStatus(401);
		    }
			
		} catch (SQLException e) {
			System.err.println("UpdateExperimentProcessComment: Problems with SQL query");
			status="SQL error";
			response.setStatus(404);
		} catch (JSONException e){
			System.err.println("UpdateExperimentProcessComment: Problems creating JSON");
			status="JSON error";
			response.setStatus(404);
		} catch (Exception e) {
			System.err.println("UpdateExperimentProcessComment: Strange Problems");
			status="error";
			response.setStatus(404);
		}	
		
		dBconn.closeDB();
		
    // tell client that everything is fine
    PrintWriter out = response.getWriter();
    JSONObject answer = new JSONObject();
	try {
		answer.put("processid",sampleID);
		answer.put("status",status);
		out.println(answer.toString());
	} catch (JSONException e) {	
		e.printStackTrace();
	}
	
	
	}
}	