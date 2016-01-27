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

	public class AddSampleType extends HttpServlet {
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
			System.err.println("AddSampleType: Input is not valid JSON");
			status="input error";
		}
		response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    
	 	DBconnection dBconn=new DBconnection();

	    int stringKeyName=0;
	    int stringKeyDesc=0; 
	    int position=0;
	    int otgroup=1;


	    
	    // generate strings for the name and the unit
	    try {	
		    dBconn.startDB();	   
			if (jsonIn.has("name")){
				 JSONObject name=jsonIn.getJSONObject("name");
				 String [] names = JSONObject.getNames(name);
				 stringKeyName=dBconn.createNewStringKey(name.getString(names[0]));
				 for (int i=0; i<names.length; i++){
					 dBconn.addString(stringKeyName,names[i],name.getString(names[i]));
				 }
			 }else{
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
			 if (jsonIn.has("otgroup")){
				 otgroup=jsonIn.getInt("ptgroup");
			 }  
	    PreparedStatement pStmt = null;
			pStmt= dBconn.conn.prepareStatement( 			
				"INSERT INTO objecttypes (position,otgrp,string_key,description,lastchange,lastuser) "
				+"VALUES(?,?,?,?,NOW(),?)");
			pStmt.setInt(1, position);
			pStmt.setInt(2, otgroup);
		   	pStmt.setInt(3, stringKeyName);
		   	pStmt.setInt(4, stringKeyDesc);
		   	pStmt.setInt(5, userID);
		   	pStmt.executeUpdate();
		} catch (SQLException e) {
			status="SQL error";
			e.printStackTrace();
			System.err.println("AddSampleType: Problems with SQL query");
		} catch (Exception e) {
			System.err.println("AddSampleType: Strange Problems");
			e.printStackTrace();
		}	
		
		
	
		
    // tell client that everything is fine
    PrintWriter out = response.getWriter();
	    try {
	        JSONObject answer = new JSONObject();
			answer.put("status", status);
			out.println(answer.toString());
		} catch (JSONException e) {
			status="JSON error";
			System.err.println("AddSampleType: Problems creating JSON answer");
		}    
	}
}	