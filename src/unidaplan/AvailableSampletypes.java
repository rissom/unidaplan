package unidaplan;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

	public class AvailableSampletypes extends HttpServlet {
		private static final long serialVersionUID = 1L;


	  @Override
	  public void doGet(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {

		request.setCharacterEncoding("utf-8");
	    response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    PrintWriter out = response.getWriter();
	    
	    PreparedStatement pstmt = null;		// Declare variables
		JSONArray objectList = null;
	    
	    DBconnection DBconn=new DBconnection();   // New connection to the database
	    DBconn.startDB();
	    
	 	ArrayList<String> stringkeys = new ArrayList<String>(); 

	 	try {	
	 		pstmt= DBconn.conn.prepareStatement("SELECT id, string_key FROM objecttypes");
		    objectList=DBconn.jsonArrayFromPreparedStmt(pstmt); // get ResultSet from the database using the query

	 		  if (objectList.length()>0) {
	           	  for (int i=0; i<objectList.length();i++) {
	           		  JSONObject dings=(JSONObject) objectList.get(i);
	           		  stringkeys.add(Integer.toString(dings.getInt("string_key")));
	           	  }
		          String query="SELECT id,string_key,language,value FROM Stringtable WHERE string_key=ANY('{";
		           	
		          StringBuilder buff = new StringBuilder(); // join numbers with commas
		          String sep = "";
		          for (String str : stringkeys) {
		           	    buff.append(sep);
		           	    buff.append(str);
		           	    sep = ",";
		          }
		          query+= buff.toString() + "}'::int[])";
		          JSONArray theStrings=DBconn.jsonfromquery(query);
		          JSONObject jsAvailable=new JSONObject();
		          jsAvailable.put("sampletypes", objectList);
		          jsAvailable.put("strings", theStrings);
		          out.println(jsAvailable.toString());
	  	        }
	  	        else {					
	  	    	  out.println("[]");			// return empty array
	  	        }       

	 		
		} catch (SQLException e) {
			System.err.println("Available objecttypes: Problems with SQL query for sample name");
			e.printStackTrace();	
		} catch (JSONException e) {
			System.err.println("Available objecttypes: JSON Problem while getting sample name");
			e.printStackTrace();
		} catch (Exception e2) {
			System.err.println("Available objecttypes: Strange Problem while getting sample name");
			e2.printStackTrace();
		}   
		DBconn.closeDB();
	}
}	