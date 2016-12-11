package unidaplan;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

public class AddExperimentParameter extends HttpServlet {
	private static final long serialVersionUID = 1L;

@Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {		
	Authentificator authentificator = new Authentificator();
	int userID = authentificator.GetUserID(request,response);
	String status = "ok";
    request.setCharacterEncoding("utf-8");
    String in = request.getReader().readLine();
    JSONObject jsonIn = null;
    PreparedStatement pStmt = null;
	String privilege = "n";

	
    try {
    	
		jsonIn = new JSONObject(in);
	 	DBconnection dBconn=new DBconnection();
	    dBconn.startDB();
	    
	    
		// check privileges
	    pStmt = dBconn.conn.prepareStatement( 	
				"SELECT getExperimentRights(vuserid := ?,vexperimentid := ?)");
		pStmt.setInt(1,userID);
		pStmt.setInt(2,jsonIn.getInt("experimentid"));
		privilege = dBconn.getSingleStringValue(pStmt);	    
		
		if (privilege.equals("w")){
	   
			pStmt = dBconn.conn.prepareStatement(
					 "INSERT INTO expp_param (exp_plan_id,hidden,pos,definition,lastUser) "
				   + "VALUES ("
				   + "	?,"
				   + "	false,"
				   + "	COALESCE ((SELECT max(pos) + 1 FROM expp_param WHERE exp_plan_id = ?), 1),"
				   + "	?,"
				   + "	?)");
			JSONArray parameters = jsonIn.getJSONArray("parameters");
			for (int i = 0; i < parameters.length(); i++){
				pStmt.setInt(1, jsonIn.getInt("experimentid"));  // exp_plan:_id
				pStmt.setInt(2, jsonIn.getInt("experimentid"));  // exp_plan:_id
				pStmt.setInt(3, parameters.getInt(i));  // definition
				pStmt.setInt(4, userID);						 // lastUser
				pStmt.addBatch();
			}
			pStmt.executeBatch();
			pStmt.close();
		} else{
			response.setStatus(401);
		}
	} catch (SQLException e) {
		System.err.println("AddExperimentParameter: Problems with SQL query");
		e.printStackTrace();
	} catch (Exception e) {
		System.err.println("AddExperimentParameter: Strange Problems");
	}	
			
	// tell client that everything is fine
    Unidatoolkit.sendStandardAnswer(status,response);
   	}	
}