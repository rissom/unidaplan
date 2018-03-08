package unidaplan;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
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

public class SamplesByName extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Authentificator authentificator = new Authentificator();
		int userID = authentificator.GetUserID(request,response);
		request.setCharacterEncoding("utf-8");
	    String in = request.getReader().readLine();
	    JSONObject jsonIn = null;	 
	    JSONArray samplelist = new JSONArray();
	    try {
			jsonIn = new JSONObject(in);
		} catch (JSONException e) {
			System.err.println("Samples by name: Input is not valid JSON");
		}
		response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
	 	DBconnection dBconn = new DBconnection();
	 	String name = "";
	    PrintWriter out = response.getWriter();
	    PreparedStatement pStmt = null;
		
	    try {
		 	dBconn.startDB();
		 	JSONArray typeArray = jsonIn.getJSONArray("sampletypes");
            JSONArray experimentArray = jsonIn.getJSONArray("experiments");
		 	name = jsonIn.getString("name");
		 	if (typeArray.length() > 0){
		 		String query = "SELECT  "
		 		             + "  samplenames.id AS sampleid, "
		 		             + "  samplenames.name, "
		 		             + "  samplenames.typeid, "
		 		             + "  samplenames.experiments "
            		    		     + "FROM samplenames " 
            		    		   	 + "WHERE UPPER(samplenames.name) LIKE UPPER('%" + name + "%') AND "
            		    		   	 + "samplenames.typeID = ANY ('{";
		 		String sep = "";
		 		for (int i=0; i<typeArray.length(); i++){
		 		    query += sep + typeArray.getInt(i);
		 		    sep = ",";
		 		}
		 		query += "}'::int[]) ";
		 		
		 		
		 		query += "AND ( ";
		 		for (int i=0; i<experimentArray.length(); i++) {
		 		        if (i>0) { query += "OR "; } 
		 		        query += "samplenames.experiments @>'";
		 		        query += experimentArray.getInt(i);
		 		        query += "' ";
		 		}
		 		query += ") ";
		 		if (jsonIn.getString("privilege").equals("r")){
			 		query += " AND (   getSampleRights(?, samplenames.id) = 'w'  "
			 			   + " OR getSampleRights(?, samplenames.id) = 'r') ";
			 	}
		 		
		 		if (jsonIn.getString("privilege").equals("w")){
			 		query += " AND getSampleRights(?, samplenames.id) = 'w' ";
			 	}
		 			
		 		query += "ORDER BY samplenames.name " 
		    		       + "LIMIT 20 ";
		 		pStmt = dBconn.conn.prepareStatement(query);
		 		if (jsonIn.getString("privilege").equals("r")){
			 		pStmt.setInt(1,userID);
		 			pStmt.setInt(2,userID);
		 		}
		 		if (jsonIn.getString("privilege").equals("w")){
			 		pStmt.setInt(1,userID);
		 		}
		 		samplelist = dBconn.jsonArrayFromPreparedStmt(pStmt);
		 		pStmt.close();
		 	}
		} catch (SQLException  eS) {
			System.err.println("SQL Error in Sample by name");
		} catch (JSONException js){
			System.err.println("JSON Error in Sample by name");
		} catch (Exception e) {
			System.err.println("Misc Error in Sample by name");
		} finally {
            try {
                	if (samplelist.length()>0) {
                		out.println(samplelist.toString());
                	}
                	else {	
                		out.println("[]");
                	}   
            }
            catch (Exception e2) {
                e2.printStackTrace(); // log this error
            }
            finally{		       
                if (dBconn.conn != null) { dBconn.closeDB(); } // close the database
            }
		}    
	}
}