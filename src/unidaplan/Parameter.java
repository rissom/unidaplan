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

	public class Parameter extends HttpServlet {
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
		JSONArray parameters = null;
	    response.setContentType("application/json");
	    request.setCharacterEncoding("utf-8");
	    response.setCharacterEncoding("utf-8");
	    PrintWriter out = response.getWriter();
	 	DBconnection dBconn=new DBconnection();
	    JSONObject result = new JSONObject();
	    try {  
		    dBconn.startDB();
			pstmt= dBconn.conn.prepareStatement( 	
			"SELECT paramdef.id,stringkeyname,stringkeyunit,datatype,maxdigits,id_description, "
			+"(blabla.count) IS NULL as deletable "
			+"FROM paramdef "
			+"LEFT JOIN "
			+" (SELECT count(a.id),definition FROM p_parameters a GROUP BY definition "
			+"  UNION ALL "
			+"  SELECT count(b.id),definition FROM ot_parameters b GROUP BY definition "
			+"  UNION ALL "
			+"  SELECT count(c.id),definition FROM expp_param c GROUP BY definition "
			+" ) AS blabla ON definition=paramdef.id "
			+"WHERE paramdef.id>2");
			parameters=dBconn.jsonArrayFromPreparedStmt(pstmt);
			pstmt.close();
			  for (int i=0; i<parameters.length();i++) {
	      		  JSONObject tempObj=parameters.getJSONObject(i);
	      		  if (tempObj.has("stringkeyname")){
	      			  stringkeys.add(Integer.toString(tempObj.getInt("stringkeyname")));
	      		  }
	      		  if (tempObj.has("stringkeyunit")){
	      			  stringkeys.add(Integer.toString(tempObj.getInt("stringkeyunit")));
	      		  }
	      		  if (tempObj.has("id_description")){
	      			  stringkeys.add(Integer.toString(tempObj.getInt("id_description")));
	      		  }
	      	  }
    	} catch (SQLException e) {
    		System.err.println("Parameters: Problems with SQL query parameters");
    	} catch (JSONException e) {
			System.err.println("Parameters: JSON Problem while getting Stringkeys");
    	} catch (Exception e2) {
			System.err.println("Parameters: Strange Problem while getting Stringkeys");
    	} try {
			  
	        result.put("strings", dBconn.getStrings(stringkeys));
	        result.put("parameters", parameters);
			out.println(result.toString());
			dBconn.closeDB();
    	} catch (JSONException e) {
			System.err.println("Parameters: JSON Problem ");
    	} catch (Exception e2) {
			System.err.println("Parameters: Strange Problem while getting Stringkeys");
    	}
	}}	