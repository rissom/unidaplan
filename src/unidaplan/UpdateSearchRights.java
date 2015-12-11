package unidaplan;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

	public class UpdateSearchRights extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doPost(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {
		
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
	    request.setCharacterEncoding("utf-8");
	    String status="ok";
	    String in = request.getReader().readLine();
	    JSONObject  jsonIn = null;	    
	    try {
			  jsonIn = new JSONObject(in);
		} catch (JSONException e) {
			System.err.println("UpdateSearchRights: Input is not valid JSON");
			status="error";
		}
		response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    PreparedStatement pStmt;

	    int searchID=0;
	    boolean isPublic=false;
	    try {
			searchID=jsonIn.getInt("searchid");	
			isPublic=jsonIn.getBoolean("ispublic");
		} catch (JSONException e) {
			System.err.println("UpdateSearchRights: Error parsing ID-Field");
			status="error parsing ID-Field";
			response.setStatus(404);
		}

	 	DBconnection dBconn=new DBconnection();
	    
	 	// delete existing rights
	 	
	 	
	    // Insert new rights
	    try{
		    dBconn.startDB();
		    pStmt= dBconn.conn.prepareStatement("");
		    pStmt.setInt(1, searchID);
		    pStmt.setBoolean(2, isPublic);
		    pStmt.executeUpdate();
		    pStmt.close();
	    } catch (SQLException e) {
			System.err.println("UpdateSearchRights: Problems with SQL query for deletion");
			status="error";
		} catch (Exception e) {
			System.err.println("UpdateSearchRights: Strange Problems deleting old parameter");
			status="error";
		}
	    
	try{
		dBconn.closeDB();
	    // tell client that everything is fine
	    PrintWriter out = response.getWriter();
	    JSONObject myResponse= new JSONObject();
	    myResponse.put("status", status);
		out.println(myResponse.toString());
	} catch (JSONException e){
		System.err.println("UpdateSearchRights: More Problems creating JSON");
		status="error";
	} catch (Exception e) {
		System.err.println("UpdateSearchRights: More Strange Problems");
		status="error";
	}
		
	}
}	