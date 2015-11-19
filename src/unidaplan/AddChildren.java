package unidaplan;
import java.io.IOException;
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

	public class AddChildren extends HttpServlet {
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
			System.err.println("AddChildren: Input is not valid JSON");
		}

		response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    
	    // get the sample id
	    int sampleID=-1;
	    try {
			 sampleID=jsonIn.getInt("sampleid");
		} catch (JSONException e) {
			System.err.println("AddChildren: Error parsing process ID-Field");
			response.setStatus(404);
		}


	    
		
		try {	
		 	DBconnection DBconn=new DBconnection();
		    DBconn.startDB();	   
		    PreparedStatement pstmt = null;
		    
			if (jsonIn.has("children")){
				JSONArray newChildren=(JSONArray) jsonIn.get("children");
				pstmt= DBconn.conn.prepareStatement( 
					"SELECT child FROM originates_from WHERE parent=?");
				pstmt.setInt(1, sampleID);
				JSONArray oldChildren=DBconn.jsonArrayFromPreparedStmt(pstmt);
				ArrayList<Integer> assignedChildrenList = new ArrayList<Integer>();
			 	ArrayList<Integer> newlyCreatedChildrenList = new ArrayList<Integer>();
			 	ArrayList<Integer> newChildrenList = new ArrayList<Integer>();
			 	ArrayList<Integer> childrenToDeleteList = new ArrayList<Integer>();

		 	// create a List of already assigned Samples
			for (int i=0;i<oldChildren.length();i++){ 
				assignedChildrenList.add((Integer)((JSONObject)oldChildren.get(i)).getInt("child"));
			}

		 	
		 	// insert database entries for not already assigned samples
			pstmt= DBconn.conn.prepareStatement( 			
					 "INSERT INTO originates_from VALUES(default,?,?,NOW(),?)");
			for (int i=0;i<newChildren.length();i++){
				int child=newChildren.getInt(i);
				newChildrenList.add(child);
				if (!assignedChildrenList.contains(child)){
					newlyCreatedChildrenList.add(child);
					pstmt.setInt(1, sampleID);
					pstmt.setInt(2, child);					
					pstmt.setInt(3, userID);
					pstmt.addBatch();
				}

			}
			pstmt.executeBatch();
			pstmt.close();

			// make a list of samples to delete
			for (int i=0;i<oldChildren.length();i++){
				if (!newChildrenList.contains(assignedChildrenList.get(i))){
					childrenToDeleteList.add((Integer) assignedChildrenList.get(i));
				}
			}

			// Delete the samples
			pstmt= DBconn.conn.prepareStatement( 			
					 "DELETE FROM originates_from WHERE child=? AND parent=?");
			for(Integer child: childrenToDeleteList){
				pstmt.setInt(1, child);
				pstmt.setInt(2, sampleID);				
				pstmt.addBatch();
			} 
			pstmt.executeBatch();
			pstmt.close();}
			
			
			DBconn.closeDB();


		} catch (SQLException e) {
			System.err.println("AddChildren: Problems with SQL query");
			status="SQL error";
		} catch (JSONException e){
			System.err.println("AddChildren: Problems creating JSON");
			status="JSON error";
		} catch (Exception e) {
			System.err.println("AddChildren: Strange Problems");
			e.getStackTrace();
			status="error";
		}	
		

		
    // tell client that everything is fine
    Unidatoolkit.sendStandardAnswer(status, response);
	}
}	