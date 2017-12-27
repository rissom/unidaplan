package unidaplan;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

public class AddExperiment extends HttpServlet {
	private static final long serialVersionUID = 1L;

@Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {		
	Authentificator authentificator = new Authentificator();
	int userID=authentificator.GetUserID(request,response);
	String status = "ok";
    request.setCharacterEncoding("utf-8");
    String in = request.getReader().readLine();
    JSONObject jsonIn = null;
    PreparedStatement pStmt = null;
    int stringKey = 0;
    int experimentID = 0;
//	String privilege = "n";

	
    try {
    	
		jsonIn = new JSONObject(in);
	 	DBconnection dBconn = new DBconnection();
	    dBconn.startDB();
	    
    	if ( jsonIn.has("name") ){
    		JSONObject name = jsonIn.getJSONObject("name");
    		String [] names = JSONObject.getNames(name);
    		stringKey = dBconn.createNewStringKey(name.getString(names[0]));
    		for (int i = 0; i < names.length; i++){
    			dBconn.addString(stringKey,names[i],name.getString(names[i]));
    		}
    	}else{
    		response.setStatus(422);
    	}
	    
		pStmt = dBconn.conn.prepareStatement(
				 "INSERT INTO experiments (name,number,creator,status,lastUser) "
			   + "VALUES ("
			   + "	?,"
			   + "	COALESCE ( (SELECT max(number) + 1 FROM experiments), 1),"
			   + "	?,"
			   + "	1,"
			   + "	?) "
			   + "RETURNING id");
		pStmt.setInt(1, stringKey);  // name
		pStmt.setInt(2, userID);	 // creator
		pStmt.setInt(3, userID);	 // lastUser
		experimentID = dBconn.getSingleIntValue(pStmt);

		dBconn.closeDB();

	} catch (SQLException e) {
		System.err.println("AddExperiment: Problems with SQL query");
	} catch (Exception e) {
		System.err.println("AddExperiment: Strange Problems");
		e.printStackTrace();
	}	
			
	// tell client that everything is fine
	Unidatoolkit.returnID(experimentID, status, response);
   	}	
}