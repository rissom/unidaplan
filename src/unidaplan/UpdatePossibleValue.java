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

	public class UpdatePossibleValue extends HttpServlet {
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
    
	    try {
			  jsonIn = new JSONObject(in);
		} catch (JSONException e) {
			System.err.println("UpdateSearchName: Input is not valid JSON");
		}
    
	 	DBconnection dBconn=new DBconnection(); // initialize database
	    PreparedStatement pStmt = null;
	    
		
		try {
		    dBconn.startDB();
		    
		    if (dBconn.isAdmin(userID)){

		
				// create database entry for the new name
				pStmt= dBconn.conn.prepareStatement( 			
						 "UPDATE possible_values "
						+"SET (string, lastchange, lastuser) = (?,NOW(),?) "
						+"WHERE id=?");
				pStmt.setString(1, jsonIn.getString("newvalue"));
				pStmt.setInt(2,userID);
				pStmt.setInt(3, jsonIn.getInt("id"));
				pStmt.executeUpdate();
				pStmt.close();
		    } else {
		    	response.setStatus(401);
		    }
			
		} catch (SQLException e) {
			System.err.println("UpdateSearchName: Problems with SQL query");
			status="SQL error";
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("UpdateSearchName: some error occured");
			status="misc error";
		}
		
		dBconn.closeDB();

		
    // tell client that everything is fine
    Unidatoolkit.sendStandardAnswer(status,response);
	}
}	