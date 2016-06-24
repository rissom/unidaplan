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
		PreparedStatement pStmt;
	    response.setContentType("application/json");
	    request.setCharacterEncoding("utf-8");
	    response.setCharacterEncoding("utf-8");
	    PrintWriter out = response.getWriter();
	 	DBconnection dBconn=new DBconnection();
	    try {  
		    dBconn.startDB();
		    if (Unidatoolkit.userHasAdminRights(userID, dBconn)){ // admins are allowed to do everything
				pStmt= dBconn.conn.prepareStatement("WITH "
						+"members AS ( "
						+"SELECT groupid, array_to_json(array_agg(userid)) AS members "
						+"FROM groupmemberships " 
						+"GROUP BY groupid " 
						+"), "
						+"sampletypes AS ( "
						+"SELECT groupid, array_to_json(array_agg(json_build_object('id',sampletype,'permission',permission))) " 
						+"AS sampletypes "
						+"FROM rightssampletypegroup " 
						+"GROUP BY groupid "
						+"), "
						+"processtypes AS ( "
						+"SELECT groupid, array_to_json(array_agg(json_build_object('id',processtype,'permission',permission))) "
						+"AS processtypes "
						+"FROM rightsprocesstypegroup "
						+"GROUP BY groupid "
						+") "
						+"SELECT groups.id, groups.name, members, sampletypes, processtypes "
						+"FROM groups "
						+"LEFT JOIN members ON members.groupid=groups.id "
						+"LEFT JOIN sampletypes st ON st.groupid=groups.id "
						+"LEFT JOIN processtypes pt ON pt.groupid=groups.id ");
				JSONArray groups = dBconn.jsonArrayFromPreparedStmt(pStmt);
				pStmt.close();
				dBconn.closeDB();
				out.println(groups.toString());

		    } else {
		    	response.setStatus(401);
//				out.println("{status:\"Error: access restricted\"}");
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