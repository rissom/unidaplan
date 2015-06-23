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
 * Servlet implementation class Samples_by_name
 * 
 * gets up to 20 Samples with a name close to the String given in the argument "name"
 * of the type given in the argument "type"
 */
@WebServlet("/Samples_by_name")
public class Samples_by_name extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
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
				"SELECT  objectnames.id, objectnames.name, objectnames.type "
				+"FROM objectnames "
				+"WHERE (objectnames.name LIKE ?) "
				+"AND objectnames.type=? "
				+"ORDER BY objectnames.name "
				+"LIMIT 20");
		       stmt.setString(1, name+"%");
		       stmt.setString(2, type);
		       result=stmt.executeQuery(); // get ResultSet from the database using the query
//		       while (result.next()) {
//		            String coffeeName = result.getString("name");
//		            System.out.println(coffeeName); 
//		            String coffeetype = result.getString("type");
//		            System.out.println(coffeetype); 
//		            String id = result.getString("id");
//		            System.out.println(id); 
//		        }

		    } catch (SQLException eS) {
				// TODO Auto-generated catch block
				eS.printStackTrace();
			} finally {
		       try {
		          JSONArray samplelist = DBconnection.table2json(result);
		          if (samplelist.length()>0) {
		        	  out.println(samplelist.toString());
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