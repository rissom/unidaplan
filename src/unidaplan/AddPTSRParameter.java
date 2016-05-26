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

	public class AddPTSRParameter extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doPost(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {
		
		Authentificator authentificator;
		int userID;
		String status="ok";
		Boolean compulsory = false;
		int processTypeID=0;
	    JSONArray ids = null;
		PreparedStatement pStmt = null;
		String in;
		
		authentificator = new Authentificator();
		userID=authentificator.GetUserID(request,response);

	    request.setCharacterEncoding("utf-8");
	    in = request.getReader().readLine();
	    JSONObject  jsonIn = null;
	    
	    try {
			jsonIn = new JSONObject(in);
			processTypeID = jsonIn.getInt("processtypeid");
    		ids = jsonIn.getJSONArray("parameterids");
		} catch (JSONException e) {
			System.err.println("AddPTSRParameter: Input is not valid JSON");
		}
	    
	    
		response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    
	    

	    int stringKeyName=0;
	    
	    // generate strings for the name
	    try {			 	
	    	DBconnection dBconn=new DBconnection();
	    	dBconn.startDB();	   
	    	
	    	//check if admin
	    	int admins=1;
			if (userID>0 && Unidatoolkit.isMemberOfGroup(userID,admins, dBconn)){
			   	
				for (int i=0; i<ids.length();i++){

				   	// add entry to database
					pStmt= dBconn.conn.prepareStatement( 			
							"INSERT INTO po_parameters ("
							+ "processtypeid,"
							+ "compulsory,"
							+ "definition, "
							+ "position, "
							+ "lastuser) "
							+ "VALUES (?,?,?, "
							+ "(SELECT COALESCE ((SELECT max(po2.position)+1 FROM po_parameters po2 WHERE po2.processtypeid=?),1)), "
							+ "?)");
				   	pStmt.setInt(1, processTypeID);
				   	pStmt.setBoolean (2,compulsory);
				   	pStmt.setInt(3, ids.getInt(i));
				   	pStmt.setInt(4, processTypeID);
				   	pStmt.setInt(5, userID);
				   	pStmt.executeUpdate();
				   	pStmt.close();
				}
			} else {
				response.setStatus(401);
			}
			dBconn.closeDB();
		} catch (SQLException e) {
			System.err.println("AddPTSRParameter: Problems with SQL query");
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println("AddPTSRParameter: Strange Problems");
		}	
		
	
		
    // tell client that everything is fine
	   Unidatoolkit.sendStandardAnswer(status, response);
	}
}	