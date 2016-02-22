package unidaplan;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

	public class ChangeOrderSearchOutput extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doPut(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {
		
		Authentificator authentificator = new Authentificator();
		String status="ok";
		int userID=authentificator.GetUserID(request,response);
	    request.setCharacterEncoding("utf-8");
	    String in = request.getReader().readLine();
	    String table="";
	    JSONObject  jsonIn = null;
	    JSONArray output = null;
	    
	    try {
			  jsonIn = new JSONObject(in);
			  output = jsonIn.getJSONArray("output");
		} catch (JSONException e) {
			System.err.println("ChangeOrderSearchOutput: Input is not valid JSON");
		}

	    
	    
	   
	    try {
		    // Initialize Database
			DBconnection dBconn=new DBconnection();
		    dBconn.startDB();	
		    PreparedStatement pStmt = null;
		    
		    pStmt = dBconn.conn.prepareStatement(
		    		"SELECT type FROM searches WHERE id=?");
		    pStmt.setInt(1, jsonIn.getInt("searchid"));
		    int type=dBconn.getSingleIntValue(pStmt);
		    switch (type) {
			    case 1 : table="osearchoutput"; break;
			    case 2 : table="psearchoutput"; break;
			    case 3 : table="posearchoutput"; break;
		    }		    

	    	for (int i=0;i<output.length();i++){
	    		JSONObject parameter=output.getJSONObject(i);
	    		pStmt= dBconn.conn.prepareStatement( 			
						 "UPDATE "+table+" SET (position,lastuser)=(?,?) WHERE id=?");
//	    		System.out.println("parameter: "+parameter.toString());
			   	pStmt.setInt(1, parameter.getInt("position"));
			   	pStmt.setInt(2, userID);
			   	pStmt.setInt(3, parameter.getInt("outputid"));
//				pStmt.addBatch();  // Does not work. I don't know why.
				pStmt.executeUpdate();				
	    	}
			pStmt.close();
			dBconn.closeDB();
		} catch (JSONException e) {
			System.err.println("ChangeOrderSearchOutput: Error");
			status = "Error";
			e.printStackTrace();
			response.setStatus(404);
		} catch (SQLException e) {
			System.err.println("ChangeOrderSearchOutput: Problems with SQL query");
			status = "SQL Error";
			e.printStackTrace();
			response.setStatus(404);
		} catch (Exception e) {
			System.err.println("ChangeOrderSearchOutput: Strange Problems");
			status = "Misc Error";
			e.printStackTrace();
			response.setStatus(404);
		}

		
    // tell client that everything is fine
    Unidatoolkit.sendStandardAnswer(status, response);
	}
}	