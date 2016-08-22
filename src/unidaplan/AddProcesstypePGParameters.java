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

	public class AddProcesstypePGParameters extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doPost(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {
		
		Authentificator authentificator;
		String status = "ok";
	    JSONObject  jsonIn = null;	    
	    PreparedStatement pStmt = null;
	    int userID = 0;
	    
	    authentificator = new Authentificator();
	    request.setCharacterEncoding("utf-8");
		userID = authentificator.GetUserID(request,response);
	    String in = request.getReader().readLine();
	    
	    try {
			  jsonIn = new JSONObject(in);
		} catch (JSONException e) {
			System.err.println("AddProcesstypePGParameters: Input is not valid JSON");
		}
		response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    
	    // get the id
	    int parameterGrpID = 0;
	    int processTypeID = 0;
	    JSONArray ids = null;

	    try {
			 parameterGrpID=jsonIn.getInt("parametergroupid");
			 processTypeID=jsonIn.getInt("processtypeid");	
     		 ids=jsonIn.getJSONArray("parameterids");
		} catch (JSONException e) {
			System.err.println("AddProcesstypePGParameters: Error parsing ID-Field");
			status = "Error parsing ID-Field";
			response.setStatus(404);
		}

	    

	    
	    // add Parameters to the parametergroup
		try {	
		    // Initialize Database
			DBconnection dBconn=new DBconnection();
		    dBconn.startDB();
		    
		    int admins=1;
		    if (userID>0 && Unidatoolkit.isMemberOfGroup(userID,admins, dBconn)){
		    
				for (int i=0; i<ids.length();i++){
					pStmt= dBconn.conn.prepareStatement( 			
							   "INSERT INTO p_parameters ("
							 + "	ProcesstypeID,"
							 + "	Parametergroup,"
							 + "	compulsory,"
							 + "	ID_Field,"
							 + "	Hidden,"
							 + "	pos,"
							 + "	definition,"
							 + "	lastUser) "
							 + "VALUES(?,?,False,False,False,"
							 + "(SELECT COALESCE ((SELECT max(p2.pos)+1 FROM p_parameters p2 WHERE p2.parametergroup=?),1)),"
							 + "?,?)");
				   	pStmt.setInt(1, processTypeID);
				   	pStmt.setInt(2, parameterGrpID);
				   	pStmt.setInt(3, parameterGrpID);
				   	pStmt.setInt(4, ids.getInt(i));
				   	pStmt.setInt(5, userID);
					pStmt.executeUpdate();
				}
				pStmt.close();
		    } else {
		    	response.setStatus(401);
		    }
			dBconn.closeDB();	
			
			
			
		} catch (SQLException e) {
			System.err.println("AddProcesstypePGParameters: Problems with SQL query");
			status = "SQL Error";
			response.setStatus(404);
		} catch (Exception e) {
			System.err.println("AddProcesstypePGParameters: Strange Problems");
			status = "Misc Error (line70)";
			response.setStatus(404);
		}

	
    // tell client that everything is fine
	Unidatoolkit.sendStandardAnswer(status, response);
	}
}	