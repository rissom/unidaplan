package unidaplan;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

	public class AddProcessType extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doPost(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {		
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
		String status="ok";
	    request.setCharacterEncoding("utf-8");
	    String in = request.getReader().readLine();
	    JSONObject  jsonIn = null;
	    
	    try {
			jsonIn = new JSONObject(in);
		} catch (JSONException e) {
			System.err.println("AddProcessType: Input is not valid JSON");
			status="input error";
		}
		response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    
	 	DBconnection dBconn=new DBconnection();
	    dBconn.startDB();	   
	    
	    int stringKeyName=0;
	    int stringKeyDesc=0; 
	    int position=0;
	    int ptgroup=1;


	    
	    // generate strings for the name and the unit
	    try {	
			 if (jsonIn.has("name")){
				 JSONObject name=jsonIn.getJSONObject("name");
				 String [] names = JSONObject.getNames(name);
				 stringKeyName=dBconn.createNewStringKey(name.getString(names[0]));
				 for (int i=0; i<names.length; i++){
					 dBconn.addString(stringKeyName,names[i],name.getString(names[i]));
				 }
			 }else
			 {
				 System.out.println("no name exists");
			 }
			 if (jsonIn.has("description")){
				 JSONObject description=jsonIn.getJSONObject("description");
				 String [] descriptions = JSONObject.getNames(description);
				 stringKeyDesc=dBconn.createNewStringKey(description.getString(descriptions[0]));
				 for (int i=0; i<descriptions.length; i++){
					 dBconn.addString(stringKeyDesc,descriptions[i],description.getString(descriptions[i]));
				 }	 
			 }
			 if (jsonIn.has("position")){
				 position=jsonIn.getInt("position");
			 }
			 if (jsonIn.has("ptgroup")){
				 ptgroup=jsonIn.getInt("ptgroup");
			 }
		} catch (JSONException e) {
			System.err.println("AddProcessType: Error creating Strings");
			response.setStatus(404);
			status="JSON String error";
		} catch (Exception e) {
			System.err.println("AddProcessType: Error creating Strings");
			status="String error";
		}	
  
	    PreparedStatement pstmt = null;
		try {	
			pstmt= dBconn.conn.prepareStatement( 			
					"INSERT INTO processtypes values(default,?,?,?,?,NOW(),?)");
			pstmt.setInt(1, position);
			pstmt.setInt(2, ptgroup);
		   	pstmt.setInt(3, stringKeyName);
		   	pstmt.setInt(4, stringKeyDesc);
		   	pstmt.setInt(5, userID);
		   	pstmt.executeUpdate();
		} catch (SQLException e) {
			status="SQL error";
			System.err.println("AddProcessType: Problems with SQL query");
		} catch (Exception e) {
			System.err.println("AddProcessType: Strange Problems");
		}	
		
	
		
    // tell client that everything is fine
    PrintWriter out = response.getWriter();
	    try {
	        JSONObject answer = new JSONObject();
			answer.put("status", status);
			out.println(answer.toString());
		} catch (JSONException e) {
			status="JSON error";
			System.err.println("AddProcessType: Problems creating JSON answer");
		}    
	}
}	