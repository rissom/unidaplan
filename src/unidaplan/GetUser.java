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

	public class GetUser extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doGet(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {
		Authentificator authentificator = new Authentificator();
		int userID = authentificator.GetUserID(request,response);
		PreparedStatement pStmt;
		JSONObject user = null;
		JSONArray experiments = null;
		JSONArray groups = null;
		JSONArray ptypes = null;
		JSONArray sampletypes = null;
	    response.setContentType("application/json");
	    request.setCharacterEncoding("utf-8");
	    response.setCharacterEncoding("utf-8");
	    int id = -1;
	    
		// get Parameter for id
		try{
			 id = Integer.parseInt(request.getParameter("id"));
		}
		catch (Exception e1) {
			System.err.print("User: no user ID given!");
		}
	    PrintWriter out = response.getWriter();
	 	DBconnection dBconn = new DBconnection();
	    try {  
		    dBconn.startDB();
		    if (dBconn.isAdmin(userID)){
				pStmt = dBconn.conn.prepareStatement(
						"SELECT "
					  + "	fullname, " 
					  + "	username, " 
					  + "	email, "
					  + "	token_valid_to > NOW() AS validtoken, " 
					  + "	to_char(token_valid_to, 'YYYY-MM-DD\"T\"HH24:MI:SS\"Z\"') AS tokenvalidto "
					  + "FROM users " 
					  + "WHERE users.id = ?");
				pStmt.setInt(1, id);
				user = dBconn.jsonObjectFromPreparedStmt(pStmt);
				
				// Get groupmemberships
				pStmt = dBconn.conn.prepareStatement(
						"SELECT "
					  + "	groupid AS id, "
					  + "	name "
					  + "FROM groupmemberships gm "
					  + "LEFT JOIN groups ON groups.id = gm.groupid "
					  + "WHERE gm.userid = ?");
				pStmt.setInt(1, id);
				groups = dBconn.jsonArrayFromPreparedStmt(pStmt);
				user.put("groups", groups);

				
				// Get Experiments
				pStmt = dBconn.conn.prepareStatement(
						"SELECT "
					  + "	experiment AS id, "
					  + "	permission "
					  + "FROM rightsexperimentuser "
					  + "WHERE userid = ?");
				pStmt.setInt(1, id);
				experiments = dBconn.jsonArrayFromPreparedStmt(pStmt);
				user.put("experiments", experiments);
				
				// Get processtypes
				pStmt = dBconn.conn.prepareStatement(
						"SELECT "
					  + "	processtype AS id, "
					  + "	permission "
					  + "FROM rightsprocesstypeuser "
					  + "WHERE userid = ?");
				pStmt.setInt(1, id);
				ptypes = dBconn.jsonArrayFromPreparedStmt(pStmt);
				pStmt.close();
				user.put("processtypes", ptypes);

				// Get sampletypes
				pStmt = dBconn.conn.prepareStatement(
						"SELECT "
					  + "	sampletype AS id, "
					  + "	permission "
					  + "FROM rightssampletypeuser "
					  + "WHERE userid = ?");
				pStmt.setInt(1, id);
				sampletypes = dBconn.jsonArrayFromPreparedStmt(pStmt);
				user.put("sampletypes", sampletypes);
				
		    }
			dBconn.closeDB();
			out.println(user.toString());
    	} catch (SQLException e) {
    		System.err.println("GetUser: Problems with SQL query");
    	} catch (JSONException e) {
			System.err.println("GetUser: JSON Problem");
    	} catch (Exception e2) {
			System.err.println("GetUser: Strange Problem");
			e2.printStackTrace();
    	}
	}
}	