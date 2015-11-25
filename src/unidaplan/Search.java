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

	public class Search extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doGet(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
		userID=userID+1;
		userID=userID-1;
		String status="ok";
		PreparedStatement pstmt;
		ArrayList<String> stringkeys = new ArrayList<String>(); 
		JSONObject search = null;
	    response.setContentType("application/json");
	    request.setCharacterEncoding("utf-8");
	    response.setCharacterEncoding("utf-8");
	    PrintWriter out = response.getWriter();
	 	DBconnection dBconn=new DBconnection();
	    JSONObject answer = new JSONObject();
	    int id=-1;
	    
	  	try {
	   		 id=Integer.parseInt(request.getParameter("id")); 
	    } catch (Exception e1) {
	   		System.err.println("no search ID given!");
			response.setStatus(404);
	   	}
	    try {  
		    dBconn.startDB();
	    	// get basic search data (id,name,owner,operation)
			pstmt= dBconn.conn.prepareStatement( 	
			    "SELECT id,name,owner,operation FROM searches "
			   +"WHERE id=?");
			pstmt.setInt(1, id);
			search=dBconn.jsonObjectFromPreparedStmt(pstmt);
			stringkeys.add(Integer.toString(search.getInt("name")));
			pstmt.close();
    	} catch (SQLException e) {
    		System.err.println("Search: Problems with SQL query for search");
    		response.setStatus(404);
			status="SQL Problem while getting experiment";
    	} catch (JSONException e) {
			System.err.println("Search: JSON Problem while getting experiment");
    		response.setStatus(404);
			status="JSON Problem while getting experiment";
    	} catch (Exception e2) {
			System.err.println("Search: Strange Problem while getting experiment");
			status="Problem while getting experiment";
    		response.setStatus(404);
    	} 
	    
	   try {
		   answer.put("search", search);
		   answer.put("strings", dBconn.getStrings(stringkeys));
		   out.println(answer.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    
	    		
	    
		dBconn.closeDB();
	}}	