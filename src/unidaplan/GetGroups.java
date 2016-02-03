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

public class GetGroups extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	  public void doGet(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
		int admins=1;
		PreparedStatement pstmt;
	    response.setContentType("application/json");
	    request.setCharacterEncoding("utf-8");
	    response.setCharacterEncoding("utf-8");
	    PrintWriter out = response.getWriter();
	 	DBconnection dBconn=new DBconnection();
	    try {  
		    dBconn.startDB();
		    if (Unidatoolkit.isMemberOfGroup(userID, admins, dBconn)){ // admins are allowed to do everything
				pstmt= dBconn.conn.prepareStatement( 	
						 "SELECT "
						+" groups.id, "
						+" groups.name, "
						+" ''||array_to_json(array_agg(userid)) AS members "
						+"FROM groups " 
						+"LEFT JOIN groupmemberships ON (groups.id=groupid) " 
						+"GROUP BY groups.id");
				JSONArray groups=dBconn.jsonArrayFromPreparedStmt(pstmt);
				pstmt.close();			
				dBconn.closeDB();
				for (int i=0; i<groups.length();i++){
					String members=groups.getJSONObject(i).getString("members");
					groups.getJSONObject(i).remove(members);
					JSONArray membersArray= new JSONArray(members);
					groups.getJSONObject(i).put("members",membersArray);
				}
				out.println(groups.toString());

		    }
    	} catch (SQLException e) {
    		System.err.println("GetGroups: Problems with SQL query");
    	} catch (JSONException e) {
			System.err.println("GetGroups: JSON Problem while getting Stringkeys");
			e.printStackTrace();
    	} catch (Exception e2) {
			System.err.println("GetGroups: Strange Problem while getting Stringkeys");
			e2.printStackTrace();
    	}
		
	}
}	