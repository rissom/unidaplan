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
import org.json.JSONObject;

public class AvailableProcesstypes extends HttpServlet {
	private static final long serialVersionUID = 1L;
   
	
    public AvailableProcesstypes() {
        super();
    }

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
		throws ServletException, IOException {
		  
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
		userID=userID+1;
		userID=userID-1;
		request.setCharacterEncoding("utf-8");
	    response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    PrintWriter out = response.getWriter(); 
	    
		PreparedStatement pstmt = null; 	// Declare variables
	    JSONArray processList= null;
	    JSONArray recipes= null;		
	 	DBconnection DBconn=new DBconnection(); // New connection to the database
	 			 	
	 	ArrayList<String> stringkeys = new ArrayList<String>(); 
		 	
		 	
	 		try{
	 		 	DBconn.startDB();
	 			pstmt = DBconn.conn.prepareStatement(	
				  "SELECT id, name, description  FROM processtypes");
	 			processList=DBconn.jsonArrayFromPreparedStmt(pstmt); // get ResultSet from the database using the query
	 			pstmt.close();
	           	if (processList.length()>0) {
	           		for (int i=0; i<processList.length();i++) {
	           			JSONObject tempObj=processList.getJSONObject(i);
	           			stringkeys.add(Integer.toString(tempObj.getInt("name")));
	           			stringkeys.add(Integer.toString(tempObj.getInt("description")));
	           			pstmt = DBconn.conn.prepareStatement(
	           					"SELECT id, name FROM p_recipes WHERE ot_id=?");
	           			pstmt.setInt(1, tempObj.getInt("id"));
	           			recipes=DBconn.jsonArrayFromPreparedStmt(pstmt); // get ResultSet from the database using the query
	           			processList.getJSONObject(i).put("recipes", recipes);
	    	           	if (recipes.length()>0) {
	    	           		for (int j=0; j<recipes.length();j++) {
	    	           			stringkeys.add(Integer.toString(recipes.getJSONObject(j).getInt("name")));
	    	           		}
	    	           	}
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
           		}else {					
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
