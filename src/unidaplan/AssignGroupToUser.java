package unidaplan;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

	public class AssignGroupToUser extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doPut(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {		
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
	    request.setCharacterEncoding("utf-8");
	    String in = request.getReader().readLine();
	    String status = "ok";
	    JSONObject  jsonIn = null;	
	    int id = -1;
	    
    
	    try {
			  jsonIn = new JSONObject(in);
		} catch (JSONException e) {
			System.err.println("AssignGroupToUser: Input is not valid JSON");
		}
		

	    JSONArray groups=null;// get parameters
	    
	    try {
	         id = jsonIn.getInt("userid");
	         groups = jsonIn.getJSONArray("groups");
		} catch (JSONException e) {
			System.err.println("AssignGroupToUser: Error parsing user ID or groups");
			response.setStatus(404);
		}
	    
	 	DBconnection dBconn = new DBconnection(); // initialize database
	    PreparedStatement pStmt = null;
	    
		
		try {
		    dBconn.startDB();
		    int admins = 1;
		 	if (Unidatoolkit.isMemberOfGroup(userID, admins, dBconn)){


				// delete old entries
				pStmt=dBconn.conn.prepareStatement(
						"DELETE FROM groupmemberships WHERE userid=?");
				pStmt.setInt(1,id);
				pStmt.executeUpdate();
				pStmt.close();
				
			
				// Insert new members into database
				if (groups.length()>0){
					pStmt= dBconn.conn.prepareStatement( 			
							 "INSERT INTO groupmemberships (groupid,userid,lastuser) VALUES (?,?,?)");
					for (int i=0; i < groups.length(); i++){
						pStmt.setInt(1,groups.getInt(i));
						pStmt.setInt(2,id);
						pStmt.setInt(3,userID);
						pStmt.addBatch();
					}
					pStmt.executeBatch();
					pStmt.close();
				}
		 	} else {
		 		response.setStatus(401);
		 	}
			
			
		} catch (SQLException e) {
			System.err.println("AssignGroupToUser: Problems with SQL query");
			status = "SQL error";
		} catch (Exception e) {
			System.err.println("AssignGroupToUser: some error occured");
			status = "misc error";
		}
		
		dBconn.closeDB();

		
    // tell client that everything is fine
    Unidatoolkit.sendStandardAnswer(status,response);
	}
}	