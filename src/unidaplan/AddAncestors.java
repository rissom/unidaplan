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

	public class AddAncestors extends HttpServlet {
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
			System.err.println("AddAncestors: Input is not valid JSON");
		}

		response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    
	    // get the sample id
	    int sampleID=-1;
	    try {
			 sampleID=jsonIn.getInt("sampleid");
		} catch (JSONException e) {
			System.err.println("AddAncestors: Error parsing process ID-Field");
			response.setStatus(404);
		}

	 	DBconnection DBconn=new DBconnection();
	    DBconn.startDB();	   
	    PreparedStatement pstmt = null;
	    
		
		try {	
			if (jsonIn.has("ancestors")){
				JSONArray newAncestors=(JSONArray) jsonIn.get("ancestors");
				pstmt= DBconn.conn.prepareStatement( 
					"SELECT parent FROM originates_from WHERE child=?");
				pstmt.setInt(1, sampleID);
				JSONArray oldAncestors=DBconn.jsonArrayFromPreparedStmt(pstmt);
				ArrayList<Integer> assignedAncestorList = new ArrayList<Integer>();
			 	ArrayList<Integer> newlyCreatedAncestorsList = new ArrayList<Integer>();
			 	ArrayList<Integer> newAncestorsList = new ArrayList<Integer>();
			 	ArrayList<Integer> AncestorsToDeleteList = new ArrayList<Integer>();

		 	// create a List of already assigned Samples
			for (int i=0;i<oldAncestors.length();i++){ 
				assignedAncestorList.add((Integer)((JSONObject)oldAncestors.get(i)).getInt("parent"));
			}

		 	
		 	// insert database entries for not already assigned samples
			pstmt= DBconn.conn.prepareStatement( 			
					 "INSERT INTO originates_from VALUES(default,?,?,NOW(),?)");
			for (int i=0;i<newAncestors.length();i++){
				int ancestor=newAncestors.getInt(i);
				newAncestorsList.add(ancestor);
				if (!assignedAncestorList.contains(ancestor)){
//					newlyCreatedAncestors.put(ancestor);
					newlyCreatedAncestorsList.add(ancestor);
					pstmt.setInt(1, ancestor);
					pstmt.setInt(2, sampleID);					
					pstmt.setInt(3, userID);
					pstmt.addBatch();
				}

			}
			pstmt.executeBatch();
			pstmt.close();

			// make a list of samples to delete
			for (int i=0;i<oldAncestors.length();i++){
				if (!newAncestorsList.contains(assignedAncestorList.get(i))){
					AncestorsToDeleteList.add((Integer) assignedAncestorList.get(i));
				}
			}

			// Delete the samples
			pstmt= DBconn.conn.prepareStatement( 			
					 "DELETE FROM originates_from WHERE child=? AND parent=?");
			for(Integer ancestor: AncestorsToDeleteList){
				pstmt.setInt(1, sampleID);
				pstmt.setInt(2, ancestor);
				pstmt.addBatch();
			} 
			pstmt.executeBatch();
			pstmt.close();}
			

		} catch (SQLException e) {
			System.err.println("AddAncestors: Problems with SQL query");
			status="SQL error";
		} catch (JSONException e){
			System.err.println("AddAncestors: Problems creating JSON");
			status="JSON error";
		} catch (Exception e) {
			System.err.println("AddAncestors: Strange Problems");
			e.getStackTrace();
			status="error";
		}	
		
		DBconn.closeDB();

		
    // tell client that everything is fine
    PrintWriter out = response.getWriter();
    out.print("{\"processid\":"+sampleID+",");
	out.println("\"status\":\""+status+"\"}");
	}
}	