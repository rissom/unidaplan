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
		userID=userID+1;
		userID=userID-1;
		PreparedStatement pstmt;
		ArrayList<String> stringkeys = new ArrayList<String>(); 
		JSONArray experiments = null;
	    response.setContentType("application/json");
	    request.setCharacterEncoding("utf-8");
	    response.setCharacterEncoding("utf-8");
	    PrintWriter out = response.getWriter();
	 	DBconnection dBconn=new DBconnection();
	    dBconn.startDB();
	    JSONObject expPlans = new JSONObject();
	    try {  
			pstmt= dBconn.conn.prepareStatement( 	
			"SELECT exp_plan.ID AS ID, intd.value AS number, users.fullname as creator, exp_plan.name ,status " 
			+"FROM exp_plan " 
			+"JOIN users ON (users.id=exp_plan.Creator) " 
			+"JOIN expp_integer_data intd ON (intd.expp_id=exp_plan.ID) "
			+"JOIN expp_param ON (intd.expp_param_id=expp_param.id AND expp_param.definition=2)");
			experiments=dBconn.jsonArrayFromPreparedStmt(pstmt);
			pstmt.close();
			  for (int i=0; i<experiments.length();i++) {
	      		  JSONObject tempObj=(JSONObject) experiments.get(i);
	      		  stringkeys.add(Integer.toString(tempObj.getInt("name")));
	      	  }
    	} catch (SQLException e) {
    		System.err.println("Experiments: Problems with SQL query for Stringkeys");
    	} catch (JSONException e) {
			System.err.println("Experiments: JSON Problem while getting Stringkeys");
    	} catch (Exception e2) {
			System.err.println("Experiments: Strange Problem while getting Stringkeys");
    	} try {
			  
	        expPlans.put("strings", dBconn.getStrings(stringkeys));
	        expPlans.put("experiments", experiments);
			out.println(expPlans.toString());
			dBconn.closeDB();
    	} catch (JSONException e) {
			System.err.println("Experiments: JSON Problem while getting Stringkeys");
    	} catch (Exception e2) {
			System.err.println("Experiments: Strange Problem while getting Stringkeys");
    	}
	}}	