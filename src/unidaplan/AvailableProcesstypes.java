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
		int userID = authentificator.GetUserID(request,response);
		request.setCharacterEncoding("utf-8");
	    response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    PrintWriter out = response.getWriter(); 
	    
		PreparedStatement pstmt = null; 	// Declare variables
	    JSONArray processList = null;
	    JSONArray recipes = null;		
	 	DBconnection DBconn = new DBconnection(); // New connection to the database
	 			 	
	 	ArrayList<String> stringkeys = new ArrayList<String>(); 
		 	
		 	
	 		try{
	 		 	DBconn.startDB();
	 		 	
	 		 	if (userID > 0) {
	 		 	
		 			pstmt = DBconn.conn.prepareStatement(	
						  "WITH "
					 	+ "  deletable AS ( "
						+ "SELECT "
						+ "  processtypeid, "
						+ "  count(processtypeid) <= 3 AS deletable " // only the 3 basic Parameters exist 
						+ "FROM p_parameters "
						+ "GROUP BY processtypeid "
						+ "), "
						+ ""
						+ "recipes AS ( "
						+ "SELECT "
						+ "	processtype, "
						+ "	array_to_json ( array_agg( json_build_object( "
						+ "     'id',id, "
						+ "		'name',name, "
						+ "		'position',position))) AS recipes "
						+ "FROM processrecipes "
						+ "GROUP BY processtype "
						+ ") "
						+ ""
						+ ""
						+ "SELECT "
						+ "	pt.id, "
						+ " pt.name, "
						+ " pt.description, "
						+ "	deletable, " 
						+ "	recipes "
						+ "FROM processtypes pt "
						+ "LEFT JOIN deletable ON deletable.processtypeid = pt.id " 
						+ "LEFT JOIN recipes ON recipes.processtype = pt.id");
		 			processList = DBconn.jsonArrayFromPreparedStmt(pstmt); // get ResultSet from the database using the query
		 			pstmt.close();
		           	if (processList.length() > 0) {
		           		for (int i = 0; i < processList.length(); i++) {
		           			JSONObject tempObj = processList.getJSONObject(i);
		           			stringkeys.add(Integer.toString(tempObj.getInt("name")));
		           			if (tempObj.has("description")){
		           				stringkeys.add(Integer.toString(tempObj.getInt("description")));
		           			}
		           			if (tempObj.has("recipes")){
			           			recipes = tempObj.getJSONArray("recipes");
			           		 	if (recipes.length() > 0) {
			    	           		for (int j = 0; j < recipes.length(); j++) {
			    	           			stringkeys.add(Integer.toString(recipes.getJSONObject(j).getInt("name")));
			    	           		}
			    	           	}
		           			}
		           		}
				        JSONArray theStrings=DBconn.getStrings(stringkeys);
				        JSONObject jsAvailable=new JSONObject();
				        jsAvailable.put("processes", processList);
				        jsAvailable.put("strings", theStrings);
				        out.println(jsAvailable.toString());
	           		}else {					
	           			out.println("[]");			// return empty array
		  	        }       
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
