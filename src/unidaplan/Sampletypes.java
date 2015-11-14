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

	public class Sampletypes extends HttpServlet {
		private static final long serialVersionUID = 1L;


	  @Override
	  public void doGet(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {
		  
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
		userID=userID+1;
		userID=userID-1;

		request.setCharacterEncoding("utf-8");
	    response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    PrintWriter out = response.getWriter();
	    
	    PreparedStatement pstmt = null;		// Declare variables
		JSONArray sampletypes = null;
	    
	    DBconnection DBconn=new DBconnection();   // New connection to the database
	    DBconn.startDB();
	    
	 	ArrayList<String> stringkeys = new ArrayList<String>(); 

	 	try {	
	 		pstmt= DBconn.conn.prepareStatement("SELECT ot.id,ot.string_key,ot.description, count(otg.id) "
			+"FROM objecttypes ot "
			+"LEFT JOIN ot_parametergrps otg ON otg.ot_id=ot.id "
			+"GROUP BY ot.id");
		    sampletypes=DBconn.jsonArrayFromPreparedStmt(pstmt); // get ResultSet from the database using the query

	 		  if (sampletypes.length()>0) {
	           	  for (int i=0; i<sampletypes.length();i++) {
	           		  JSONObject dings=sampletypes.getJSONObject(i);
	           		  if (dings.getInt("count")==0) dings.put("deletable",true);
	           		  dings.remove("count");
	           		  stringkeys.add(Integer.toString(dings.getInt("string_key")));
	           		  stringkeys.add(Integer.toString(dings.getInt("description")));
	           	  }
		          JSONObject answer=new JSONObject();
		          answer.put("sampletypes", sampletypes);
		          answer.put("strings", DBconn.getStrings(stringkeys));
		          out.println(answer.toString());
	  	        }
	  	        else {					
	  	    	  out.println("[]");			// return empty array
	  	        }       
		} catch (SQLException e) {
			System.err.println("Sampletypes: Problems with SQL query for sample name");
		} catch (JSONException e) {
			System.err.println("Sampletypes: JSON Problem while getting sample name");
		} catch (Exception e2) {
			System.err.println("Sampletypes: Strange Problem while getting sample name");
		}   
		DBconn.closeDB();
	}
}	