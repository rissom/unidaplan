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

	public class AddPTParameterGrp extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doPost(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {		
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
		String status="ok";
		int processTypeID=0;
		int position=0;
	    request.setCharacterEncoding("utf-8");
	    String in = request.getReader().readLine();
	    JSONObject  jsonIn = null;
	    
	    try {
			jsonIn = new JSONObject(in);
			processTypeID=jsonIn.getInt("processtypeid");
			position=jsonIn.getInt("position");
		} catch (JSONException e) {
			System.err.println("AddPTParameterGrp: Input is not valid JSON");
		}
	    
	    
		response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    
	 	DBconnection dBconn=new DBconnection();
	    dBconn.startDB();	   
	    
	    int stringKeyName=0;
	    
	    // generate strings for the name
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
				 status="error: no name given";
			 }

		} catch (JSONException e) {
			System.err.println("AddPTParameterGrp: Error creating Strings");
			response.setStatus(404);
		} catch (Exception e) {
			System.err.println("AddPTParameterGrp: Error creating Strings");
		}	
  
	    // get current max position and add 1
	    PreparedStatement pStmt = null;
	    try {	
			pStmt= dBconn.conn.prepareStatement( 			
					"SELECT max(pos) FROM p_parametergrps WHERE processtype=?");
		   	pStmt.setInt(1, processTypeID);
		   	position=dBconn.getSingleIntValue(pStmt)+1;
		} catch (SQLException e) {
			System.err.println("AddPTParameterGrp: Problems with SQL query");
		} catch (Exception e) {
			System.err.println("AddPTParameterGrp: Strange Problems");
		}	
		
	    
	    
	    // add entry to database
		try {	
			pStmt= dBconn.conn.prepareStatement( 			
					"INSERT INTO p_parametergrps values(default,?,?,?, NOW(),?)");
		   	pStmt.setInt(1, processTypeID);
		   	pStmt.setInt(2, stringKeyName);
		   	pStmt.setInt(3, position);
		   	pStmt.setInt(4, userID);
		   	pStmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("AddPTParameterGrp: Problems with SQL query");
		} catch (Exception e) {
			System.err.println("AddPTParameterGrp: Strange Problems");
		}	
		
	
		
    // tell client that everything is fine
    PrintWriter out = response.getWriter();
	    try {
	        JSONObject answer = new JSONObject();
			answer.put("status", status);
			out.println(answer.toString());
		} catch (JSONException e) {
			System.err.println("AddPTParameterGrp: Problems creating JSON answer");
		}    
	}
}	