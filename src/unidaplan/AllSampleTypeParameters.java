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

	public class AllSampleTypeParameters extends HttpServlet {
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
		JSONArray AllSampleTypeParameters = null;
	    response.setContentType("application/json");
	    request.setCharacterEncoding("utf-8");
	    response.setCharacterEncoding("utf-8");
	    PrintWriter out = response.getWriter();
	 	DBconnection dBconn=new DBconnection();
	    JSONObject result = new JSONObject();
	    try {  
		    dBconn.startDB();
			pstmt= dBconn.conn.prepareStatement( 	
			"SELECT otp.id,otp.stringkeyname, paramdef.stringkeyunit, datatype,paramdef.id_description " 
			+"FROM ot_AllSampleTypeParameters otp "
			+"JOIN paramdef ON otp.definition=paramdef.id "
			+"WHERE objecttypesid=1");
			AllSampleTypeParameters=dBconn.jsonArrayFromPreparedStmt(pstmt);
			pstmt.close();
			  for (int i=0; i<AllSampleTypeParameters.length();i++) {
	      		  JSONObject tempObj=AllSampleTypeParameters.getJSONObject(i);
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
    		System.err.println("AllSampleTypeParameters: Problems with SQL query AllSampleTypeParameters");
    	} catch (JSONException e) {
			System.err.println("AllSampleTypeParameters: JSON Problem while getting Stringkeys");
    	} catch (Exception e2) {
			System.err.println("AllSampleTypeParameters: Strange Problem while getting Stringkeys");
    	} try {
			  
	        result.put("strings", dBconn.getStrings(stringkeys));
	        result.put("AllSampleTypeParameters", AllSampleTypeParameters);
			out.println(result.toString());
			dBconn.closeDB();
    	} catch (JSONException e) {
			System.err.println("AllSampleTypeParameters: JSON Problem ");
    	} catch (Exception e2) {
			System.err.println("AllSampleTypeParameters: Strange Problem while getting Stringkeys");
    	}
	}}	