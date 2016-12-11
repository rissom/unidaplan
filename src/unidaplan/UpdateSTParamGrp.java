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

	public class UpdateSTParamGrp extends HttpServlet {
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
	    int paramGrpID = -1;
	    String language ="";
	    
    
	    try {
			  jsonIn = new JSONObject(in);
		} catch (JSONException e) {
			System.err.println("UpdateSTParamGrp: Input is not valid JSON");
		}
		

	    String newName=""; // get parameters
	    
	    try {
			 paramGrpID=jsonIn.getInt("paramgrpid");
			 language=jsonIn.getString("language");
			 newName=jsonIn.getString("newname");
		} catch (JSONException e) {
			System.err.println("UpdateSTParamGrp: Error parsing ID-Field or comment");
			response.setStatus(404);
		}
	    
	 	DBconnection dBconn=new DBconnection(); // initialize database
	    PreparedStatement pStmt = null;
	    
		
		try {
		    dBconn.startDB();
		    
		    if (dBconn.isAdmin(userID)){


				// find the stringkey
				pStmt=dBconn.conn.prepareStatement(
						"SELECT stringkey FROM ot_parametergrps WHERE id=?");
				pStmt.setInt(1,paramGrpID);
				int stringKey=dBconn.getSingleIntValue(pStmt);
				pStmt.close();
				
				// delete old entries in the same language
				pStmt=dBconn.conn.prepareStatement(
						"DELETE FROM stringtable WHERE language=? AND string_key=?");
				pStmt.setString(1,language);
				pStmt.setInt(2,stringKey);
				pStmt.executeUpdate();
				pStmt.close();
				
			
				// create database entry for the new name
				pStmt= dBconn.conn.prepareStatement( 			
						 "INSERT INTO stringtable VALUES(default,?,?,?,NOW(),?)");
				pStmt.setInt(1,stringKey);
				pStmt.setString(2, language);
				pStmt.setString(3, newName);
				pStmt.setInt(4,userID);
				pStmt.executeUpdate();
				pStmt.close();
		    } else {
		    	response.setStatus(401);
		    }
			
		} catch (SQLException e) {
			System.err.println("UpdateSTParamGrp: Problems with SQL query");
			status="SQL error";
		} catch (Exception e) {
			System.err.println("UpdateSTParamGrp: some error occured");
			status="misc error";
		}
		
		dBconn.closeDB();

		
    // tell client that everything is fine
    Unidatoolkit.sendStandardAnswer(status,response);
	}
}	