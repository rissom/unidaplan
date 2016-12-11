package unidaplan;
import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

	public class HelloWorld extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doGet(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {
//		Authentificator authentificator = new Authentificator();
//		int userID=authentificator.GetUserID(request,response);
	    response.setContentType("application/json");
	    request.setCharacterEncoding("utf-8");
	    response.setCharacterEncoding("utf-8");
//	    Authentificator authentificator = new Authentificator();
//		int userID=authentificator.GetUserID(request,response);
	    PrintWriter out = response.getWriter();
	    		
		out.println("Hello World!");
		
	}}	