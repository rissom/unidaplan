package unidaplan;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
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
public class SamplesByName extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
		userID=userID+1; // TODO remove me!
		userID=userID-1;
		request.setCharacterEncoding("utf-8");
	    String in = request.getReader().readLine();
	    JSONObject  jsonIn = null;	 
	    JSONArray samplelist =new JSONArray();
	    try {
			  jsonIn = new JSONObject(in);
		} catch (JSONException e) {
			System.err.println("Samples by name: Input is not valid JSON");
		}
		response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
	 	DBconnection DBconn=new DBconnection();
	 	String name="";
		try{
			name=request.getParameter("name");
		} catch (Exception e1) {
			System.err.print("no type ID or name given!");
		} 
	    PrintWriter out = response.getWriter();
	    PreparedStatement pstmt = null;
		
	    try {
		 	DBconn.startDB();
		 	JSONArray typeArray=jsonIn.getJSONArray("sampletypes");
		 	if (typeArray.length()>0){
		 		String query="SELECT  samplenames.id AS sampleid, samplenames.name, samplenames.typeid \n"
		    		   		+"FROM samplenames \n" 
		    		   		+"WHERE samplenames.name LIKE '%"+name+"%' AND "
		    		   		+"samplenames.typeID = ANY ('{";
		 		String sep="";
		 		for (int i=0; i<typeArray.length(); i++){
		    	   query += sep+typeArray.getInt(i);
		    	   sep=",";
		 		}
		 		query+= "}'::int[]) \n"
		    		   	+"ORDER BY samplenames.name \n" 
		    		   	+"LIMIT 20 \n";
		 		samplelist=DBconn.jsonfromquery(query); 
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