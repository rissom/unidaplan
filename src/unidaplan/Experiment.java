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

	@Override
	  public void doGet(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {
		PreparedStatement pstmt;
		ArrayList<String> stringkeys = new ArrayList<String>(); 
		JSONObject experiment = null;
	    response.setContentType("application/json");
	    request.setCharacterEncoding("utf-8");
	    response.setCharacterEncoding("utf-8");
//	    HttpSession session = request.getSession();
	    PrintWriter out = response.getWriter();
	 	DBconnection DBconn=new DBconnection();
	    DBconn.startDB();
	    JSONObject expPlans = new JSONObject();
	    int id=-1;
	    
	 // Print the current Session's ID
//	    out.println("Session ID:" + " " + session.getId());
//	    out.println("<br>");
//
//	    // Print the current Session's Creation Time
//	    out.println("Session Created:" + " " + new Date(session.getCreationTime()) + "<br>");
//
//	    // Print the current Session's Last Access Time
//	    out.println("Session Last Accessed" + " " + new Date(session.getLastAccessedTime()));
//	    
	  	try {
	   		 id=Integer.parseInt(request.getParameter("id")); 
	    } catch (Exception e1) {
	   		System.err.println("no experiment ID given!");
			response.setStatus(404);
	   	}
	    try {  
			pstmt= DBconn.conn.prepareStatement( 	
			"SELECT exp_plan.ID AS ID,users.name as creator, exp_plan.name ,status "
		    + "FROM  exp_plan \n"
		    + "JOIN users ON (users.id=exp_plan.Creator) "
		    + "WHERE exp_plan.ID=?");
			pstmt.setInt(1, id);
			experiment=DBconn.jsonObjectFromPreparedStmt(pstmt);
			pstmt.close();
    	} catch (SQLException e) {
    		System.err.println("Experiments: Problems with SQL query for experiment");
    	} catch (JSONException e) {
			System.err.println("Experiments: JSON Problem while getting experiment");
    	} catch (Exception e2) {
			System.err.println("Experiments: Strange Problem while getting experiment");
    	} 
	    if (experiment.length()>0) {
		    try {
		    	stringkeys.add(Integer.toString(experiment.getInt("name")));
		    	
	     		// Output the Parameters
				pstmt= DBconn.conn.prepareStatement( 	
	     		"SELECT expp_param.id, compulsory, expp_param.pos, "
				+"expp_param.stringkeyname,  pid, value, " 
				+"st.description, paramdef.datatype "
				+"FROM expp_param "
				+"JOIN paramdef ON (paramdef.id=expp_param.definition) " 
				+"LEFT JOIN acc_expp_parameters a ON  "
				+"(a.expp_id=expp_param.exp_plan_id AND a.id=expp_param.id ) " 
				+"JOIN String_key_table st ON st.id=expp_param.stringkeyname "
				+"WHERE expp_param.exp_plan_id=? "
				+"ORDER BY pos ");
				pstmt.setInt(1, id);
				JSONArray parameters=DBconn.jsonArrayFromPreparedStmt(pstmt);
				pstmt.close();
				if (parameters.length()>0) {
					experiment.put("parameters", parameters);
			      	for (int i=0; i<parameters.length();i++) {
			      		JSONObject tempObj=(JSONObject) parameters.get(i);
			      		stringkeys.add(Integer.toString(tempObj.getInt("stringkeyname")));
			      	}
				}
	    	} catch (SQLException e) {
	    		System.err.println("Experiments: Problems with SQL query for Parameters");
	    	} catch (JSONException e) {
				System.err.println("Experiments: JSON Problem while getting Parameters");
	    	} catch (Exception e2) {
				System.err.println("Experiments: Strange Problem while getting Parameters");
	    	} try {
			
						  
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
		        expPlans.put("experiment", experiment);
		        expPlans.put("strings", theStrings);
				out.println(expPlans.toString());
	    	} catch (SQLException e) {
	    		System.err.println("Experiments: Problems with SQL query for Stringkeys");
	    	} catch (JSONException e) {
				System.err.println("Experiments: JSON Problem while getting Stringkeys");
	    	} catch (Exception e2) {
				System.err.println("Experiments: Strange Problem while getting Stringkeys");
	    	}}else{
	    		response.setStatus(404);
	    		out.println("{\"error\":\"not found\"}");
	    	}
		DBconn.closeDB();
	}}	