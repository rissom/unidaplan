package unidaplan;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

	public class AddSamplesToProcess extends HttpServlet {
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
	    try {
			  jsonIn = new JSONObject(in);
		} catch (JSONException e) {
			System.err.println("AddSamplesToProcess: Input is not valid JSON");
		}
		
		response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    
	    // get the process id
	    int processid=-1;
	    try {
			 processid=jsonIn.getInt("id");
		} catch (JSONException e) {
			System.err.println("AddSamplesToProcess: Error parsing process ID-Field");
			response.setStatus(404);
		}
	    
	 	DBconnection DBconn=new DBconnection();
	    DBconn.startDB();	   
	    PreparedStatement pstmt = null;
	    
		
		try {	
			JSONArray samples=(JSONArray) jsonIn.get("samples");
			pstmt= DBconn.conn.prepareStatement( 
					"SELECT sampleid FROM samplesinprocess WHERE processid=?");
			pstmt.setInt(1, processid);
			JSONArray assignedSamples=DBconn.jsonArrayFromPreparedStmt(pstmt);
		 	JSONArray newlyCreatedSamples= new JSONArray();
			ArrayList<Integer> assignedSamplesList = new ArrayList<Integer>();
		 	ArrayList<Integer> newlyCreatedSamplesList = new ArrayList<Integer>();
		 	ArrayList<Integer> newSamplesList = new ArrayList<Integer>();
		 	ArrayList<Integer> samplesToDeleteList = new ArrayList<Integer>();

		 	// create a List of already assigned Samples
			for (int i=0;i<assignedSamples.length();i++){ 
			 	assignedSamplesList.add((Integer)((JSONObject)assignedSamples.get(i)).getInt("sampleid"));
			}
		 	
		 	// insert database entries for not already assigned samples
			pstmt= DBconn.conn.prepareStatement( 			
					 "INSERT INTO samplesinprocess values(default,?,?,NOW(),?)");
			for (int i=0;i<samples.length();i++){
				JSONObject sample=(JSONObject)samples.get(i);
				newSamplesList.add(sample.getInt("sampleid"));
				if (!assignedSamplesList.contains(sample.getInt("sampleid"))){
					newlyCreatedSamples.put(sample);
					newlyCreatedSamplesList.add(sample.getInt("sampleid"));
					pstmt.setInt(1, processid);
					pstmt.setInt(2, sample.getInt("sampleid"));
					pstmt.setInt(3, userID);
					pstmt.addBatch();
				}
			}
			pstmt.executeBatch();
			pstmt.close();

			// make a list of samples to delete
			for (int i=0;i<assignedSamples.length();i++){
				if (!newSamplesList.contains(assignedSamplesList.get(i))){
					samplesToDeleteList.add((Integer) assignedSamplesList.get(i));
				}
			}
			
			// Delete the samples
			pstmt= DBconn.conn.prepareStatement( 			
					 "DELETE FROM samplesinprocess WHERE sampleid=? AND processid=?");
			for(Integer sample: samplesToDeleteList){
				pstmt.setInt(1, sample);
				pstmt.setInt(2, processid);
				pstmt.addBatch();
				} 
			pstmt.executeBatch();
			pstmt.close();
			

		} catch (SQLException e) {
			System.err.println("AddSamplesToProcess: Problems with SQL query");
			status="SQL error";
		} catch (JSONException e){
			System.err.println("AddSamplesToProcess: Problems creating JSON");
			status="JSON error";
		} catch (Exception e) {
			System.err.println("AddSamplesToProcess: Strange Problems");
			status="error";
		}	
		
		DBconn.closeDB();

		
    // tell client that everything is fine
    PrintWriter out = response.getWriter();
    out.print("{\"processid\":"+processid+",");
	out.println("\"status\":\""+status+"\"}");
	}
}	