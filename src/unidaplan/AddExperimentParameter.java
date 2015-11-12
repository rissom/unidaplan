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
	int userID=authentificator.GetUserID(request,response);
	String status="ok";
    request.setCharacterEncoding("utf-8");
    String in = request.getReader().readLine();
    JSONObject  jsonIn = null;
    PreparedStatement pStmt = null;

    try {
		jsonIn = new JSONObject(in);
	 	DBconnection dBconn=new DBconnection();
	    dBconn.startDB();	   
		pStmt=dBconn.conn.prepareStatement(
				"INSERT INTO expp_param (exp_plan_id,hidden,pos,definition,StringKeyName,lastUser) "
			   +"VALUES (?,false,( "
			   +"SELECT max(pos)+1 FROM expp_param WHERE exp_plan_id=?),?,("
			   +"SELECT stringkeyname FROM paramdef WHERE ID=?),?)");
		JSONArray parameters=jsonIn.getJSONArray("parameters");
		for (int i=0;i<parameters.length();i++){
			pStmt.setInt(1, jsonIn.getInt("experimentid"));  // exp_plan:_id
			pStmt.setInt(2, jsonIn.getInt("experimentid"));  // exp_plan:_id
			pStmt.setInt(3, parameters.getInt(i));  // definition
			pStmt.setInt(4, parameters.getInt(i));  // StringKeyName from definition
			pStmt.setInt(5, userID);						 // lastUser
			pStmt.addBatch();
		}
		pStmt.executeBatch();
	} catch (SQLException e) {
		System.err.println("AddParameter: Problems with SQL query");
	} catch (Exception e) {
		System.err.println("AddParameter: Strange Problems");
	}	
			
	// tell client that everything is fine
    Unidatoolkit.sendStandardAnswer(status,response);
   	}	
}