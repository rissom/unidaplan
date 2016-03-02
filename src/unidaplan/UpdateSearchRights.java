package unidaplan;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

	public class UpdateSearchRights extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doPut(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {
		
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
	    request.setCharacterEncoding("utf-8");
	    String status="ok";
	    String in = request.getReader().readLine();
	    JSONObject  jsonIn = null;
	    JSONArray groups = null;
	    JSONArray users = null;
	    
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
	    try {
			searchID=jsonIn.getInt("searchid");	
			if (jsonIn.has("groups")){
				groups=jsonIn.getJSONArray("groups");
			}
			if (jsonIn.has("users")){
				users=jsonIn.getJSONArray("users");
			}
		} catch (JSONException e) {
			System.err.println("UpdateSearchRights: Error parsing ID-Field");
			status="error parsing ID-Field";
			response.setStatus(404);
		}

	 	DBconnection dBconn=new DBconnection();
	    try{   
		    dBconn.startDB();

		    // delete existing rights
		    pStmt= dBconn.conn.prepareStatement("DELETE FROM rightssearchgroups WHERE searchid=?");
		    pStmt.setInt(1, searchID);
		    pStmt.executeUpdate();
		    pStmt.close();
		    
		    pStmt= dBconn.conn.prepareStatement("DELETE FROM rightssearchuser WHERE searchid=?");
		    pStmt.setInt(1, searchID);
		    pStmt.executeUpdate();
		    pStmt.close();
	 	
		    // Insert new rights
		    
		    // for groups
		    if (groups!=null){
			    pStmt= dBconn.conn.prepareStatement("INSERT INTO rightssearchgroups (groupid,searchid,permission,lastuser) "
			    		+ "VALUES  (?,?,'r',?)");
			    for (int i=0; i<groups.length(); i++){
			    	 pStmt.setInt(1, groups.getInt(i));
			    	 pStmt.setInt(2, searchID);
			    	 pStmt.setInt(3, userID);
			    	 pStmt.addBatch();
			    }
			    pStmt.executeBatch();
			    pStmt.close();
		    }
		    
		    if (users!=null){
			    // for users
			    pStmt= dBconn.conn.prepareStatement("INSERT INTO rightssearchuser (userid,searchid,permission,lastuser) "
			    		+ "VALUES  (?,?,'r',?)");
			    for (int i=0; i<users.length(); i++){
			    	 pStmt.setInt(1, users.getInt(i));
			    	 pStmt.setInt(2, searchID);
			    	 pStmt.setInt(3, userID);
			    	 pStmt.addBatch();
			    }
			    pStmt.executeBatch();
		    }
		   
	    } catch (SQLException e) {
			System.err.println("UpdateSearchRights: Problems with SQL query");
			status="error";
			e.printStackTrace();
			e.getNextException().printStackTrace();
		} catch (Exception e) {
			System.err.println("UpdateSearchRights: Strange Problems");
			status="error";
			e.printStackTrace();
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