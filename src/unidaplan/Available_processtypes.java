package unidaplan;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

@WebServlet("/available_processtypes.json")
public class Available_processtypes extends HttpServlet {
	private static final long serialVersionUID = 1L;
   
	
    public Available_processtypes() {
        super();
    }

	  @Override
	  protected void doGet(HttpServletRequest request, HttpServletResponse response) 
			  throws ServletException, IOException {
		  
			Authentificator authentificator = new Authentificator();
			int userID=authentificator.GetUserID(request,response);
			request.setCharacterEncoding("utf-8");
		    response.setContentType("application/json");
		    response.setCharacterEncoding("utf-8");
		    PrintWriter out = response.getWriter(); 
		    
			PreparedStatement pstmt = null; 	// Declare variables
		    JSONArray processList= null;		
		 	DBconnection DBconn=new DBconnection(); // New connection to the database
		 	DBconn.startDB();
		 			 	
		 	ArrayList<String> stringkeys = new ArrayList<String>(); 
		 	
		 	
		    try {
		       pstmt = DBconn.conn.prepareStatement(	
				"SELECT pt.id, pt.name FROM processtypes pt");
		       processList=DBconn.jsonArrayFromPreparedStmt(pstmt); // get ResultSet from the database using the query
	            if (processList.length()>0) {
	           	  for (int i=0; i<processList.length();i++) {
	           		  JSONObject dings=(JSONObject) processList.get(i);
	           		  stringkeys.add(Integer.toString(dings.getInt("name")));
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
		          jsAvailable.put("processes", processList);
		          jsAvailable.put("strings", theStrings);
		          out.println(jsAvailable.toString());

	  	        }
	  	        else {					
	  	    	  out.println("[]");			// return empty array
	  	        }       
	            
		    } catch (SQLException eS) {
				System.err.println("Available_processtypes: SQL Error");
				eS.printStackTrace();
			} catch (Exception e) {
				System.err.println("Available_processtypes: Some Error, probably JSON");
				e.printStackTrace();
			} finally {
			try{	
		           if (pstmt != null) { 
		        	  try {
		        	  	pstmt.close();
		        	  } catch (SQLException e) {
		        	  } 
		           }
		    	   if (DBconn.conn != null) { 
		    		   DBconn.closeDB();  // close the database 
		    	   }
		        } catch (Exception e) {
					System.err.println("Available_processtypes: Some Error closing the database");
					e.printStackTrace();
			   	}
	        }       
	}
}
