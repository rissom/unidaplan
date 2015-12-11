	package unidaplan;
	import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

	public class Searches extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doGet(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
		userID=userID+1;
		userID=userID-1;
		PreparedStatement pstmt;
		ArrayList<String> stringkeys = new ArrayList<String>(); 
		JSONArray searches = null;
	    response.setContentType("application/json");
	    request.setCharacterEncoding("utf-8");
	    response.setCharacterEncoding("utf-8");
	    PrintWriter out = response.getWriter();
	 	DBconnection dBconn=new DBconnection();
	    JSONObject expPlans = new JSONObject();
	    try {  
		    dBconn.startDB();
			pstmt= dBconn.conn.prepareStatement( 	
			"SELECT searches.id, searches.name, users.fullname AS owner " 
			+"FROM searches " 
			+"JOIN users ON (users.id=searches.owner) ");
			searches=dBconn.jsonArrayFromPreparedStmt(pstmt);
			pstmt.close();
			  for (int i=0; i<searches.length();i++) {
	      		  JSONObject tempObj=(JSONObject) searches.get(i);
	      		  stringkeys.add(Integer.toString(tempObj.getInt("name")));
	      	  }
    	} catch (SQLException e) {
    		System.err.println("Searches: Problems with SQL query for Stringkeys");
    		response.setStatus(404);
    	} catch (JSONException e) {
			System.err.println("Searches: JSON Problem while getting Stringkeys");
			response.setStatus(404);
    	} catch (Exception e2) {
			System.err.println("Searches: Strange Problem while getting Stringkeys");
			response.setStatus(404);
    	} try {
			  
	        expPlans.put("strings", dBconn.getStrings(stringkeys));
	        expPlans.put("searches", searches);
			out.println(expPlans.toString());
			dBconn.closeDB();
    	} catch (JSONException e) {
			System.err.println("Searches: JSON Problem while getting Stringkeys");
			response.setStatus(404);
    	} catch (Exception e2) {
			System.err.println("Searches: Strange Problem while getting Stringkeys");
			response.setStatus(404);
    	}
	}}	