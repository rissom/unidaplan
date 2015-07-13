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

	public class Experiment extends HttpServlet {
		private static final long serialVersionUID = 1L;
		private static JSONArray result;

	@Override
	  public void doGet(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {
		PreparedStatement pstmt;
		ArrayList<String> stringkeys = new ArrayList<String>(); 
	    response.setContentType("application/json");
	    request.setCharacterEncoding("utf-8");
	    response.setCharacterEncoding("utf-8");
	    PrintWriter out = response.getWriter();
	 	DBconnection DBconn=new DBconnection();
	    DBconn.startDB();
	    JSONObject expPlans = new JSONObject();
	    try {  
			pstmt= DBconn.conn.prepareStatement( 	
			"SELECT exp_plan.ID AS ID,users.name as creator, exp_plan.name ,status "
		    + "FROM  exp_plan \n"
		    + "JOIN users ON (users.id=exp_plan.Creator)");
			JSONArray experiments=DBconn.jsonArrayFromPreparedStmt(pstmt);
			pstmt.close();
			  for (int i=0; i<experiments.length();i++) {
	      		  JSONObject tempObj=(JSONObject) experiments.get(i);
	      		  stringkeys.add(Integer.toString(tempObj.getInt("name")));
	      	  }
			  
			// get the strings
	        String query="SELECT id,string_key,language,value FROM Stringtable WHERE string_key=ANY('{";
	      	
	        StringBuilder buff = new StringBuilder(); // join numbers with commas
	        String sep = "";
	        for (String str : stringkeys) {
         	    buff.append(sep);
         	    buff.append(str);
         	    sep = ",";
	        }
	        query+= buff.toString() + "}'::int[])";
	        JSONArray theStrings=DBconn.jsonfromquery(query);
	        expPlans.put("experiments", experiments);
	        expPlans.put("strings", theStrings);
			out.println(expPlans.toString());
			DBconn.closeDB();
    	} catch (SQLException e) {
    		System.err.println("Experiments: Problems with SQL query for Stringkeys");
    	} catch (JSONException e) {
			System.err.println("Experiments: JSON Problem while getting Stringkeys");
    	} catch (Exception e2) {
			System.err.println("Experiments: Strange Problem while getting Stringkeys");
    	}
	}}	