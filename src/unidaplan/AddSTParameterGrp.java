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

	public class AddSTParameterGrp extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doPost(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {		
		Authentificator authentificator = new Authentificator();
		int userID = authentificator.GetUserID(request,response);
		String status = "ok";
		int sampleTypeID = 0;
		int parameterGroupID = -1;
		int position = 0;
	    int stringKeyName = 0;
	    request.setCharacterEncoding("utf-8");
	    PreparedStatement pStmt = null;
	    JSONObject  jsonIn = null;
	    
	    try {
			jsonIn = new JSONObject(request.getReader().readLine());
			sampleTypeID = jsonIn.getInt("sampletypeid");
			position = jsonIn.getInt("position");
		} catch (JSONException e) {
			System.err.println("AddSTParameterGrp: Input is not valid JSON");
		}
	    
	    
		response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    
	 	DBconnection dBconn = new DBconnection();
	    
	    
	    // generate strings for the name
	    try {	
		    dBconn.startDB();	 
		} catch (Exception e) {
			System.err.println("AddSTParameterGrp: Error creating Strings");
			response.setStatus(404);
		}	
		
	    
	    //check if admin
	    int admins = 1;
		if (userID>0 && Unidatoolkit.isMemberOfGroup(userID, admins, dBconn)){
		
			try{
			
				if (jsonIn.has("name")){
					 JSONObject name = jsonIn.getJSONObject("name");
					 String [] names = JSONObject.getNames(name);
					 stringKeyName=dBconn.createNewStringKey(name.getString(names[0]));
					 for (int i = 0; i < names.length; i++){
						 dBconn.addString(stringKeyName,names[i],name.getString(names[i]));
					 }
				}else{
				 status = "error: no name given";
				}
			} catch (JSONException e) {
				System.err.println("AddSTParameterGrp: Error creating Strings");
				response.setStatus(404);
			} catch (Exception e) {
				System.err.println("AddSTParameterGrp: Error creating Strings");
				response.setStatus(404);
			}	
  
		    // get current max position and add 1
		    try {	
				pStmt = dBconn.conn.prepareStatement( 			
						"SELECT max(pos) FROM ot_parametergrps WHERE ot_id=?");
			   	pStmt.setInt(1, sampleTypeID);
			   	position = dBconn.getSingleIntValue(pStmt) + 1;
			} catch (SQLException e) {
				System.err.println("AddSTParameterGrp: Problems with SQL query");
				response.setStatus(404);
			} catch (Exception e) {
				System.err.println("AddSTParameterGrp: Strange Problems");
				response.setStatus(404);
			}	
		
	    
	    
		    // add entry to database
			try {	
				pStmt = dBconn.conn.prepareStatement( 			
						  "INSERT INTO ot_parametergrps values(default,?,?,?, NOW(),?)"
						+ "RETURNING id ");
			   	pStmt.setInt(1, sampleTypeID);
			   	pStmt.setInt(2, stringKeyName);
			   	pStmt.setInt(3, position);
			   	pStmt.setInt(4, userID);
			   	parameterGroupID = dBconn.getSingleIntValue(pStmt);
			} catch (SQLException e) {
				System.err.println("AddSTParameterGrp: Problems with SQL query");
				response.setStatus(404);
			} catch (Exception e) {
				System.err.println("AddSTParameterGrp: Strange Problems");
				response.setStatus(404);
			}	
	    } else {
	    	    response.setStatus(401);
	    }
	    dBconn.closeDB();

		
	    // tell client that everything is fine
	    Unidatoolkit.returnID(parameterGroupID, status, response);
	}
}	