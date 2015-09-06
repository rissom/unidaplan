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
		int userID=authentificator.GetUserID(request,response);
	    request.setCharacterEncoding("utf-8");
	    String in = request.getReader().readLine();
	    String status = "ok";

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
	    dBconn.startDB();	   
	    PreparedStatement pstmt = null;
	    
		
		try {
			// get the old string key.
			pstmt=dBconn.conn.prepareStatement(
					"SELECT note FROM expp_samples WHERE id=?");
			pstmt.setInt(1,sampleID);
			int oldKeyID = dBconn.getSingleIntValue(pstmt);
			
			// create a new stringkey
			pstmt= dBconn.conn.prepareStatement( 			
					 "INSERT INTO string_key_table VALUES (default,?,NOW(),?) RETURNING id");
			pstmt.setString(1, newComment);
			pstmt.setInt(2, userID);
			newKeyID=dBconn.getSingleIntValue(pstmt);
			pstmt.close();
			
			pstmt= dBconn.conn.prepareStatement( 			
					 "INSERT INTO stringtable VALUES(default,?,?,?,NOW())");
			pstmt.setInt(1,newKeyID);
			pstmt.setString(2, "none");
			pstmt.setString(3, newComment);
			pstmt.executeUpdate();
			pstmt.close();
			
			pstmt= dBconn.conn.prepareStatement(
					 "UPDATE expp_samples SET note=?, lastUser=? WHERE id=?");
			pstmt.setInt(1,newKeyID);
			pstmt.setInt(2,userID);
			pstmt.setInt(3,sampleID);
			pstmt.executeUpdate();
			pstmt.close();
			
			if (oldKeyID>0){
				pstmt= dBconn.conn.prepareStatement(
						 "DELETE FROM string_key_table WHERE id=?");
				pstmt.setInt(1,oldKeyID);
				pstmt.executeUpdate();
				pstmt.close();
			}
			
		} catch (SQLException e) {
			System.err.println("UpdateExperimentProcessComment: Problems with SQL query");
			status="SQL error";
		} catch (JSONException e){
			System.err.println("UpdateExperimentProcessComment: Problems creating JSON");
			status="JSON error";
		} catch (Exception e) {
			System.err.println("UpdateExperimentProcessComment: Strange Problems");
			status="error";
		}	
		
		dBconn.closeDB();

		
    // tell client that everything is fine
    PrintWriter out = response.getWriter();
    out.print("{\"processid\":"+sampleID+",");
	out.println("\"status\":\""+status+"\"}");
	}
}	