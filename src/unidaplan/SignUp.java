package unidaplan;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

	

public class SignUp extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	
@Override
  public void doPost(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {		
    request.setCharacterEncoding("utf-8");    
    JSONObject  jsonIn = null;	
    JSONObject answer=new JSONObject();
    String fullname="";
    String username="";
    String token="";
    String email="";
    String hash="";
    int id=0;
    String status="ok";
    String password = "";
	DBconnection DBconn=new DBconnection();

    try {
        String in = request.getReader().readLine();
		jsonIn = new JSONObject(in);
	    // get User details
		id=jsonIn.getInt("id");
		
		fullname=jsonIn.getString("fullname");
		username=jsonIn.getString("username");
		email=jsonIn.getString("email");
		token=jsonIn.getString("token");
		password=jsonIn.getString("password");  //password should already be a hash
	} catch (JSONException e){
		e.printStackTrace();
		System.err.println("SignUp: Problems reading JSON. Missing token?");
		status="JSON Error: SignUp";
	} try {

   		
   		DBconn.startDB();	 
   		PreparedStatement pstmt1 = DBconn.conn.prepareStatement( 			
			"SELECT token, token_valid_to FROM users WHERE id=?");
	   	pstmt1.setInt(1, id);
	   	JSONObject jsToken = DBconn.jsonObjectFromPreparedStmt(pstmt1);
	   	String dbtoken = jsToken.getString("token");
	   	String validToString = jsToken.optString("token_valid_to");
	   	Timestamp validToDate = Timestamp.valueOf(validToString); 
	   	pstmt1.close();
	   	if (validToDate.getTime()>System.currentTimeMillis() && token.equals(dbtoken)) {
	   	// create a salted hash (salt is part of the hashstring)
	   		hash=PasswordHash.createHash(password);   	
	   		// store stuff in database
	   		PreparedStatement pstmt2 = DBconn.conn.prepareStatement( 			
					"UPDATE users SET fullname=(?), username=(?),email=(?),pw_hash=(?),token=(Null) "
					+"WHERE id=?");
		   	pstmt2.setString(1, fullname);
		   	pstmt2.setString(2, username);
		   	pstmt2.setString(3, email);
		   	pstmt2.setString(4, hash);
		   	pstmt2.setInt(5, id);
		    pstmt2.executeUpdate();
			pstmt2.close(); 
			HttpSession session = request.getSession();		
			session.setAttribute("userID",id);
	   	}
		
		DBconn.closeDB();
   	
	} catch (NoSuchAlgorithmException e) {
		System.err.println("SignUp: NoSuchAlgorithmException");
	} catch (InvalidKeySpecException e) {
		status="SignUp: InvalidKeySpecException";
	} catch (SQLException e) {
		System.err.println("SignUp: Problems with SQL query");
		status="SQL Error; AddUser";
	} catch (JSONException e){
		System.err.println("SignUp: Problems reading JSON");
		status="JSON Error: SignUp";
	} catch (Exception e) {
		System.err.println("SignUp: Strange Problems");
		status="Error SignUp";
	} 
	
    // tell client that everything is fine
	response.setContentType("application/json");
    response.setCharacterEncoding("utf-8");
    try {
		answer.put("status",status);
		
	} catch (JSONException e) {
		System.err.println("SignUp: Problems generating JSON answer");
		e.printStackTrace();
	}
    PrintWriter out = response.getWriter();
	out.println(answer.toString());
	
}	
}
