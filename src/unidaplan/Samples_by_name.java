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
import org.json.JSONException;
import org.json.JSONObject;

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
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	    request.setCharacterEncoding("utf-8");
	    String in = request.getReader().readLine();
	    JSONObject  jsonIn = null;	    
	    try {
			  jsonIn = new JSONObject(in);
		} catch (JSONException e) {
			System.err.println("SaveSampleParameter: Input is not valid JSON");
		}
	    System.out.println(jsonIn);
		response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
	 	DBconnection DBconn=new DBconnection();
	 	DBconn.startDB();
	 	String name="";
	 	int typeID=1;
	 	ResultSet result = null;
		try{
			name=request.getParameter("name");
			typeID=Integer.parseInt(request.getParameter("type")); 
		} catch (Exception e1) {
			System.out.print("no type ID or name given!");
		} 
	    PrintWriter out = response.getWriter();
	    PreparedStatement pstmt = null;
		
	    try {
	       pstmt = DBconn.conn.prepareStatement(	
			"SELECT  samplenames.id, samplenames.name, samplenames.typeid " 
			+"FROM samplenames "
			+"WHERE samplenames.name LIKE ? "
			+"AND samplenames.typeID=? "
			+"ORDER BY samplenames.name "
			+"LIMIT 20 ");
	       pstmt.setString(1, "%"+name+"%");
	       pstmt.setInt(2, typeID);
	       result=pstmt.executeQuery(); // get ResultSet from the database using the query
		} catch (SQLException eS) {
			System.out.println("SQL Error in Sample by name");
			eS.printStackTrace();
		} finally {
        try {
          JSONArray samplelist = DBconn.table2json(result);
          if (samplelist.length()>0) {
        	  out.println(samplelist.toString());
	      }
	      else {	
	    	  out.println("[]");}   
          if (pstmt != null) { pstmt.close(); }
        }
        catch (Exception e2) {
    	   e2.printStackTrace();
          // log this error
        }
        finally{		       
    	   if (DBconn.conn != null) { DBconn.closeDB(); } // close the database
        }
		}    
	}
}