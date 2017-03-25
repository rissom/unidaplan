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

	public class SampletypesForNewSample extends HttpServlet {
		private static final long serialVersionUID = 1L;


	  @Override
	  public void doGet(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {
		  
		Authentificator authentificator = new Authentificator();
		int userID = authentificator.GetUserID(request,response);
	    JSONArray recipes = null;		

		if (userID > 0){
			request.setCharacterEncoding("utf-8");
		    response.setContentType("application/json");
		    response.setCharacterEncoding("utf-8");
		    PrintWriter out = response.getWriter();
		        
		    PreparedStatement pStmt = null;		// Declare variables
			JSONArray sampletypes = null;
		    
		    DBconnection dBconn = new DBconnection();   // New connection to the database
		    
		 	ArrayList<String> stringkeys = new ArrayList<String>(); 
	
		 	try {	
			    dBconn.startDB();
		 		pStmt = dBconn.conn.prepareStatement(
		 			  "WITH " 
					+ "deletable AS ( "
					+ "	SELECT "  
					+ "		objecttypes.id AS sampletype, "
					+ "		count(s.id) = 0 AS deletable "
					+ "	FROM objecttypes "
					+ "	LEFT JOIN samples s ON s.objecttypesid = objecttypes.id "
					+ "	GROUP BY objecttypes.id "
					+ "	), "
					+ ""
					+ "maxNames AS ( "
					+ "	SELECT "
					+ " 	id,"
					+ "		name,"
					+ "		typeid"
					+ "FROM samplenames WHERE (name,typeid) IN "
					+ "	( "
					+ "		SELECT max(name), typeid"
					+ " 	FROM samplenames "
					+ " 	GROUP BY typeid"
					+ "	) "
					+ "	recipes AS ( "
					+ "	SELECT "
					+ "		sampletype, "
					+ "		array_to_json ( array_agg( json_build_object( "
					+ "     	'id', id, "
					+ "			'name', name, "
					+ "			'position', position))) AS recipes "
					+ "	FROM samplerecipes "
					+ "	GROUP BY sampletype "
					+ "	) " 
					+ ""
					+ "	SELECT "
					+ "		st.id, "
					+ "		st.string_key, "
					+ "		description,"
					+ "		deletable, " 
					+ "		recipes "
					+ "	FROM objecttypes st "
					+ "	LEFT JOIN deletable ON deletable.sampletype = st.id " 
					+ "	LEFT JOIN recipes ON recipes.sampletype = st.id");
			    sampletypes = dBconn.jsonArrayFromPreparedStmt(pStmt); // get ResultSet from the database using the query
	
		 		  if (sampletypes.length() > 0) {
		           	  for (int i = 0; i < sampletypes.length(); i++) {
		           		  JSONObject tempObj = sampletypes.getJSONObject(i);
		           		  stringkeys.add(Integer.toString(tempObj.getInt("string_key")));
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
			          JSONObject answer = new JSONObject();
			          answer.put("sampletypes", sampletypes);
			          answer.put("strings", dBconn.getStrings(stringkeys));
			          out.println(answer.toString());
		  	        }
		  	        else {					
		  	    	  out.println("[]");			// return empty array
		  	        }       
			} catch (SQLException e) {
				System.err.println("Sampletypes: Problems with SQL query for sample name");
				e.printStackTrace();
			} catch (JSONException e) {
				System.err.println("Sampletypes: JSON Problem while getting sample name");
				e.printStackTrace();
			} catch (Exception e2) {
				System.err.println("Sampletypes: Strange Problem while getting sample name");
				e2.printStackTrace();
			}   
			dBconn.closeDB();
		}
	}
}	