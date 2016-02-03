package unidaplan;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONException;
import org.json.JSONObject;

	public class AddGroup extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doPost(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {		
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
	    request.setCharacterEncoding("utf-8");
	    String in = request.getReader().readLine();
	    String status = "ok";
	    int newGroupID=-1;
	    

	    JSONObject  jsonIn = null;	    
	    try { 
	    	  if (in!=null && in.length()>2){
	    		  jsonIn = new JSONObject(in);
			  }
		} catch (JSONException e) {
			System.err.println("AddGroup: Input is not valid JSON");
		}

		response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    
	    // get the sample id
	    String groupName="new Group";
	    try {
	    	if (jsonIn !=null && jsonIn.has("groupname")){
	    		groupName=jsonIn.getString("groupname");
	    	}
		} catch (JSONException e) {
			System.err.println("AddGroup: Error parsing process ID-Field");
			response.setStatus(404);
		}


	    
		
		try {	
			int admins=1;
		 	DBconnection dBconn=new DBconnection();
		    dBconn.startDB();	   
			if (userID>0 && Unidatoolkit.isMemberOfGroup(userID,admins, dBconn)){
			    PreparedStatement pStmt = null;
			    pStmt= dBconn.conn.prepareStatement( 
						"INSERT INTO groups (name,lastchange,lastuser) VALUES (?,NOW(),?) RETURNING id");
				pStmt.setString(1, groupName);
				pStmt.setInt(2, userID);
				newGroupID=dBconn.getSingleIntValue(pStmt);
			 	pStmt.close();
			}
			dBconn.closeDB();

		} catch (SQLException e) {
			System.err.println("AddGroup: Problems with SQL query");
			status="SQL error";
		} catch (JSONException e){
			System.err.println("AddGroup: Problems creating JSON");
			status="JSON error";
		} catch (Exception e) {
			System.err.println("AddGroup: Strange Problems");
			e.getStackTrace();
			status="error";
		}	

    // tell client that everything is fine
    Unidatoolkit.returnID(newGroupID, status, response);
	}
}	