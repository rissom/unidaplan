package unidaplan;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.security.SecureRandom;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;


	public class ResendToken extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doPut(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {
//		Authentificator authentificator = new Authentificator();
//		int userID=authentificator.GetUserID(request,response);
	    byte[] token = null;
	    int id=0;
	    String status="ok";
	    String email="";
	    
	    
	    
	    // get the id
	    try {
		   	id=Integer.parseInt(request.getParameter("id")); 
	    } catch (Exception e1) {
	   		System.err.println("ResendToken: no user ID given!");
			response.setStatus(404);
	    }
	    
	    
	    // get the email
	 	DBconnection DBconn=new DBconnection();
	    PreparedStatement pStmt = null;
	    
	    try {
	    	DBconn.startDB();	
	    	pStmt= DBconn.conn.prepareStatement( 			
				"SELECT email FROM users WHERE ID=?");
	    	pStmt.setInt(1, id);
		   	email=DBconn.getSingleStringValue(pStmt);
	    } catch (Exception e1) {
	   		System.err.println("ResendToken: no user ID given!");
			response.setStatus(404);
	    }
	    
	    try{
		    // Add the token to the database	    
		    SecureRandom random = new SecureRandom();
	        token = new byte[32];
	        random.nextBytes(token);
			pStmt= DBconn.conn.prepareStatement( 			
					"UPDATE users SET token=?, token_valid_to=? WHERE ID=?");
		   	pStmt.setString(1, PasswordHash.toHex(token));
		   	Timestamp expirationDate= new Timestamp(System.currentTimeMillis()+4*24*3600*1000);
		   	pStmt.setTimestamp(2, expirationDate);
		   	pStmt.setInt(3, id);
		   	pStmt.executeUpdate();
			pStmt.close();
			DBconn.closeDB();
		} catch (SQLException e) {
			System.err.println("ResendToken: Problems with SQL query");
			status="SQL Error; ResendToken:";
		} catch (Exception e) {
			System.err.println("ResendToken: Strange Problems");
			status="Error resending token";
		}	
		
	    // tell client that everything is fine
		response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    PrintWriter out = response.getWriter();
	     
	    try {
	        JSONObject answer = new JSONObject();
			answer.put("status", status);
			answer.put("id", id);
			out.println(answer.toString());
		} catch (JSONException e) {
			System.err.println("ResendToken:: Problems creating JSON answer");
		}

	    // send an E-Mail with the token
 		String pname = "unidaplan/";
 		String ip = "";
		InitialContext initialContext;
		try {
			initialContext = new InitialContext();
			Context environmentContext = (Context) initialContext.lookup("java:/comp/env");
			ip = (String) environmentContext.lookup("IPAdress");
		} catch (NamingException e1) {
			e1.printStackTrace();
		}
		
		if (ip.equals("automatic")){
			InetAddress inetAddress = InetAddress.getLocalHost();
			ip = inetAddress.getHostAddress();
		}
	    int port = request.getLocalPort();

 	    String link = "http://" + ip + ":" + port + "/" + pname + "index.html#signup/" +
 	    				id + "/" + PasswordHash.toHex(token);
 	    SendMail.sendEmail(email, "Your Unidaplan Login", link);
	    
	}
}	