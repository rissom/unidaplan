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


	public class AddUser extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doPost(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {
		Authentificator authentificator = new Authentificator();
		int userID = authentificator.GetUserID(request,response);
	    request.setCharacterEncoding("utf-8");
	    String in = request.getReader().readLine();
	    JSONObject jsonIn = null;	   
	    String fullname = "";
	    String username = "";
	    String email = "";
	    Timestamp expirationDate = null;
	    Boolean blocked = false;
	    byte[] token = null;
	    int id = 0;
	    String status = "ok";

	    try {
			jsonIn = new JSONObject(in);
		    // get the Name
			fullname = jsonIn.getString("fullname");
			username = jsonIn.getString("username");
			email = jsonIn.getString("email"); 
			if (jsonIn.has("blocked")){
				blocked = jsonIn.getBoolean("blocked");
			}
		   	expirationDate = new Timestamp(System.currentTimeMillis()+4*24*3600*1000);
		    // Add the user to the database	    
		    SecureRandom random = new SecureRandom();
	        token = new byte[32];
	        random.nextBytes(token);
		 	DBconnection dBconn = new DBconnection();
		    dBconn.startDB();
			if (dBconn.isAdmin(userID)){
			    PreparedStatement pStmt = null;
				pStmt = dBconn.conn.prepareStatement( 			
						  "INSERT INTO users ("
						+ "  fullname,"
						+ "  username,"
						+ "  email,"
						+ "  blocked,"
						+ "  preferredlanguage,"
						+ "  token,"
						+ "  token_valid_to)"
						+ "VALUES (?,?,?,?,default,?,?) "
						+ "RETURNING id");
			   	pStmt.setString(1, fullname);
			   	pStmt.setString(2, username);
			   	pStmt.setString(3, email);
			   	pStmt.setBoolean(4, blocked);
			   	pStmt.setString(5, PasswordHash.toHex(token));
			   	pStmt.setTimestamp(6, expirationDate);
			   	id = dBconn.getSingleIntValue(pStmt);
				dBconn.closeDB();
				if (id > 0){
					
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

				    // send an E-Mail with the token
				    int port = request.getLocalPort();
			 		String pname = "unidaplan/";
			 	    String link = "http://" + ip + ":" + port + "/" + pname + 
			 	    			  "index.html#signup/" + id + "/" + PasswordHash.toHex(token);
			 	    // TODO: internationalisation!
			 	    SendMail.sendEmail(email, "Your Unidaplan Login", link);
				}
			} else {
				response.setStatus(401);
				status = "unauthorized";
			}
		} catch (SQLException e) {
			System.err.println("AddUser: Problems with SQL query");
			status="SQL Error; AddUser";
		} catch (JSONException e){
			System.err.println("AddUser: Problems creating JSON");
			status="JSON Error: AddUser";
		} catch (Exception e) {
			System.err.println("AddUser: Strange Problems");
			status="Error AddUser";
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
			System.err.println("AddUser: Problems creating JSON answer");
		}
	}
}	