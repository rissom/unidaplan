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
	String pw="";
	String user="";
	  try  {
		 System.out.print("pw: ");
		 pw=request.getParameter("pw"); 
		 System.out.println(pw);
		 System.out.print("user: ");
		 user=request.getParameter("user");
		 System.out.println(user);
	  }
	  catch (Exception e1) {
		 System.out.print("no password or userID given!");
//		 e1.printStackTrace();
	  }
	  try{    
 	DBconnection DBconn=new DBconnection();
    DBconn.startDB();
    try {  
		pstmt= DBconn.conn.prepareStatement( 	
		"SELECT pw_hash, id FROM users WHERE username=?");
		pstmt.setString(1,user);
		hashjs=DBconn.jsonObjectFromPreparedStmt(pstmt);
		pstmt.close();
    }catch (SQLException e) {
		System.err.println("Login: Problems with SQL query1");
	}try{
		

		String hash= hashjs.getString("pw_hash");
		int id = hashjs.getInt("id");
		System.out.println(hash);		
		if (PasswordHash.validatePassword(pw,hash)){
			out.println("{\"status\":\"Password correct\"}");
		session.setAttribute("userID",id);
		}else{
			out.println("{\"status\":\"Password incorrect\"}");
		}
		DBconn.closeDB();
		
	} catch (NoSuchAlgorithmException | InvalidKeySpecException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	}		
	} catch (SQLException e) {
		System.err.println("Login: Problems with SQL quer2");
	} catch (JSONException e) {
		System.err.println("Login: JSON Problem while getting Stringkeys");
	} catch (Exception e2) {
		System.err.println("Login: Strange Problem while getting Stringkeys");
	}
}}	
