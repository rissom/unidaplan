package unidaplan;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

//WebServlet("/change-experiment-status")
public class ChangeExperimentStatus extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
		@Override
		  public void doGet(HttpServletRequest request, HttpServletResponse response)
			      throws ServletException, IOException {
			
			Authentificator authentificator = new Authentificator();
			int userID=authentificator.GetUserID(request,response);
			userID=userID+1; // Remove me!!
			userID=userID-1;
		    request.setCharacterEncoding("utf-8");
		    // look up the datatype in Database	    
		    int id=-1;
		    int status=-1;
		  	try {
		   		 id=Integer.parseInt(request.getParameter("id")); 
		    } catch (Exception e1) {
		   		System.err.println("no experiment ID given!");
				response.setStatus(404);
		   	}
		    try {
		    	status=Integer.parseInt(request.getParameter("status")); 
			} catch (Exception e1) {
		   		System.err.println("no status given!");
				response.setStatus(404);
			}
		    
		 	DBconnection DBconn=new DBconnection();
		    DBconn.startDB();	   
		    PreparedStatement pstmt = null;
			try {	
				pstmt= DBconn.conn.prepareStatement( 			
						 "UPDATE exp_plan SET status=? WHERE id=?");
			   	pstmt.setInt(2, id);
			   	pstmt.setInt(1, status);
			   	pstmt.executeUpdate();
			} catch (SQLException e) {
				System.err.println("SaveSampleParameter: Problems with SQL query");
			} catch (Exception e) {
				System.err.println("SaveSampleParameter: Strange Problems");
			}
			
	    // tell client that everything is fine
	    PrintWriter out = response.getWriter();
	    if (status==-1 || id==-1) {
		    out.print("{\"id\":"+id+",");
			out.println("\"status\":"+status+"}");	
	    } else {
	    out.print("{\"id\":"+id+",");
		out.println("\"status\":\"ok\"}");
		}
	}	
}