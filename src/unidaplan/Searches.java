package unidaplan;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.CallableStatement;
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
		ArrayList<String> stringkeys = new ArrayList<String>(); 
		JSONArray searches = null;
	    response.setContentType("application/json");
	    request.setCharacterEncoding("utf-8");
	    response.setCharacterEncoding("utf-8");
	    PrintWriter out = response.getWriter();
	 	DBconnection dBconn=new DBconnection();
	    JSONObject answer = new JSONObject();
	    try {  
		    dBconn.startDB();
		    
		    if (dBconn.isAdmin(userID)) {
		    	PreparedStatement pStmt;
		    	pStmt=dBconn.conn.prepareStatement(
			    	"SELECT "
			    	+"searches.id, "
			    	+"searches.name, "
			    	+"users.fullname AS owner, "
			    	+"users.id AS ownerid, "
			    	+"'w'::VARCHAR AS permission "
			    	+"FROM searches "
			    	+"JOIN users ON (users.id=searches.owner) ");
		    	searches=dBconn.jsonArrayFromPreparedStmt(pStmt);
		    }else{
			    CallableStatement cs= dBconn.conn.prepareCall("{ ? = call getSearchesForUser( ? )}");
			    cs.registerOutParameter(1, java.sql.Types.OTHER);
			    cs.setInt(2, userID);
			    cs.execute();
			    searches = new JSONArray(cs.getObject(1).toString());
			    cs.close();
			}
		    
		    // get the strings 
			for (int i=0; i<searches.length();i++) {
				JSONObject tempObj=(JSONObject) searches.get(i);
	      		stringkeys.add(Integer.toString(tempObj.getInt("name")));
	      	}
			
    	} catch (SQLException e) {
    		System.err.println("Searches: Problems with SQL query");
    		e.printStackTrace();
    		response.setStatus(404);
    	} catch (JSONException e) {
			System.err.println("Searches: JSON Problem");
			response.setStatus(404);
    	} catch (Exception e2) {
			System.err.println("Searches: Strange Problem");
			response.setStatus(404);
    	} 
	    
	    
	    try {
	        answer.put("strings", dBconn.getStrings(stringkeys));
	        answer.put("searches", searches);
			out.println(answer.toString());
			dBconn.closeDB();
    	} catch (JSONException e) {
			System.err.println("Searches: JSON Problem while getting Stringkeys");
			response.setStatus(404);
    	} catch (Exception e2) {
			System.err.println("Searches: Strange Problem while getting Stringkeys");
			response.setStatus(404);
    	}
	}}	