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

public class Experiments extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	  public void doGet(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
		PreparedStatement pStmt;
		ArrayList<String> stringkeys = new ArrayList<String>(); 
		JSONArray experiments = null;
	    response.setContentType("application/json");
	    request.setCharacterEncoding("utf-8");
	    response.setCharacterEncoding("utf-8");
	    PrintWriter out = response.getWriter();
	 	DBconnection dBconn=new DBconnection();
	    JSONObject expPlans = new JSONObject();
	    try {  
		    dBconn.startDB();
			pStmt = dBconn.conn.prepareStatement( 	
					  "SELECT "
					+ "  experiments.id, " 
					+ "  experiments.creator, "
					+ "  users.fullname as creatorname, " 
					+ "  name, "
					+ "  status, " 
					+ "  number, "
					+ "  getExperimentRights(1,experiments.id) AS rights "
					+ "FROM experiments "
					+ "JOIN users ON users.id = experiments.creator "
					+ "WHERE getExperimentRights(?,experiments.id)>'n'");
			pStmt.setInt( 1, userID );
			experiments = dBconn.jsonArrayFromPreparedStmt(pStmt);
			for (int i = 0; i < experiments.length(); i++) {
				JSONObject tempObj = (JSONObject) experiments.get(i);
	      		stringkeys.add(Integer.toString(tempObj.getInt("name")));
	      	}
    	} catch (SQLException e) {
    		System.err.println("Experiments: Problems with SQL query for Stringkeys");
    		response.setStatus(404);
    	} catch (JSONException e) {
			System.err.println("Experiments: JSON Problem while getting Stringkeys");
			response.setStatus(404);
    	} catch (Exception e2) {
			System.err.println("Experiments: Strange Problem while getting Stringkeys");
			response.setStatus(404);
    	} try {
	        expPlans.put("strings", dBconn.getStrings(stringkeys));
	        expPlans.put("experiments", experiments);
			out.println(expPlans.toString());
			dBconn.closeDB();
    	} catch (JSONException e) {
			System.err.println("Experiments: JSON Problem while getting Stringkeys");
			response.setStatus(404);
    	} catch (Exception e2) {
			System.err.println("Experiments: Strange Problem while getting Stringkeys");
			response.setStatus(404);
    	}
	}
}	