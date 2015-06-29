package unidaplan;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;

/**
 * Servlet implementation class Available_processtypes
 */
@WebServlet("/available_processtypes.json")
public class Available_processtypes extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Available_processtypes() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		 response.setContentType("text/html");

		 	DBconnection.startDB();
		 	String name="";
		 	String type="";
			try{
				 name=request.getParameter("name");
				 type=request.getParameter("type"); 
			}
			catch (Exception e1) {
//				e1.printStackTrace();
			}
		 
		    PrintWriter out = response.getWriter();
		    PreparedStatement stmt = null;
		    ResultSet result = null;
		
		    try {
		       stmt = DBconnection.conn.prepareStatement(	
				"SELECT pt.id, string FROM processtypes pt JOIN localized ON (localized.id=pt.name)");
		       result=stmt.executeQuery(); // get ResultSet from the database using the query

		    } catch (SQLException eS) {
				// TODO Auto-generated catch block
				eS.printStackTrace();
			} finally {
		       try {
		          JSONArray processlist = DBconnection.table2json(result);
		          if (processlist.length()>0) {
		        	  out.println(processlist.toString());
			      }
			      else {	
			    	  out.println("[]");}   
		          if (stmt != null) { stmt.close(); }
		       }
		       catch (Exception e2) {
		    	   e2.printStackTrace();
		          // log this error
		       }
		       finally{		       
		    	   if (DBconnection.conn != null) { DBconnection.closeDB(); } // close the database
		       }
		   }    
	}

}
