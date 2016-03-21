package unidaplan;

import java.io.IOException;
import java.io.PrintWriter;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.json.JSONException;
import org.json.JSONObject;



public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;

@Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
	PreparedStatement pstmt;
    response.setContentType("application/json");
    request.setCharacterEncoding("utf-8");
    response.setCharacterEncoding("utf-8");
    PrintWriter out = response.getWriter();
	HttpSession session = request.getSession();
	JSONObject hashjs = null;
	String preferredLanguage="";
	String pw="";
	String user="";
	  try  {
		 pw=request.getParameter("pw"); 
		 user=request.getParameter("user");
	  }
	  catch (Exception e1) {
		 System.err.print("no password or userID given!");
//		 e1.printStackTrace();
	  }
	  try{    
 	DBconnection dBconn=new DBconnection();
    dBconn.startDB();
    try {  
		pstmt= dBconn.conn.prepareStatement( 	
		"SELECT "
		+ "pw_hash, "
		+ "users.id, "
		+ "fullname, "
		+ "groupmemberships.groupid AS admin, "
		+ "preferredlanguage "
		+ "FROM users " 
		+ "LEFT JOIN groupmemberships ON (groupid=1 AND userid=users.id) "
		+ "WHERE username=?");
		pstmt.setString(1,user);
		hashjs=dBconn.jsonObjectFromPreparedStmt(pstmt);
		pstmt.close();
		
    }catch (SQLException e) {
		System.err.println("Login: Problems with SQL query1");
	}try{
		

		String hash= hashjs.getString("pw_hash");
		int id = hashjs.getInt("id");
		if (PasswordHash.validatePassword(pw,hash)){
			JSONObject answer=new JSONObject();
			answer.put("status","Password correct");
			answer.put("fullname",hashjs.getString("fullname"));
			answer.put("id",id);
			if (hashjs.has("preferredlanguage")){
				answer.put("preferredlanguage",hashjs.getString("preferredlanguage"));
			}
			if (hashjs.has("admin")){
				answer.put("admin",true);
			}
			out.println(answer.toString());
		session.setAttribute("userID",id);
		}else{
			response.setStatus(401);
			out.println("{\"status\":\"Password incorrect\"}");
		}
		dBconn.closeDB();
		
	} catch (NoSuchAlgorithmException | InvalidKeySpecException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}		
	} catch (SQLException e) {
		System.err.println("Login: Problems with SQL query");
		response.setStatus(401);
	} catch (JSONException e) {
		System.err.println("Login: User not found or JSON error");
		e.printStackTrace();
		response.setStatus(401);
	} catch (Exception e2) {
		System.err.println("Login: Strange Problem while trying to log in");
		response.setStatus(401);
	}
}}	
