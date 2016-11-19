package unidaplan;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class Logout extends HttpServlet {
	private static final long serialVersionUID = 1L;

@Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
	
	try{
	    response.setContentType("application/json");
	    request.setCharacterEncoding("utf-8");
	    response.setCharacterEncoding("utf-8");
	    PrintWriter out = response.getWriter();
		HttpSession session = request.getSession();
		session.removeAttribute("userID");
		session.invalidate();
		out.println("{\"status\":\"Logged out\"}");
	} catch (Exception e2) {
		System.err.println("Logout: Strange Problem while trying to log out");
		response.setStatus(401);
	}
}}	
