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

	public class ChangeOrderPTParameters extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doPut(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {
		
		Authentificator authentificator = new Authentificator();
		String status="ok";
		int userID=authentificator.GetUserID(request,response);
	    request.setCharacterEncoding("utf-8");
	    String in = request.getReader().readLine();
	    JSONArray  jsonIn = null;	    
	    try {
			  jsonIn = new JSONArray(in);
		} catch (JSONException e) {
			System.err.println("ChangeOrderPTParameters: Input is not valid JSON");
		}
		response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    
	    
	    // Initialize Database
		DBconnection dBconn=new DBconnection();
	    dBconn.startDB();	
	    PreparedStatement pStmt = null;
	    
	   
	    try {
	    	for (int i=0;i<jsonIn.length();i++){
	    		JSONObject parameter=jsonIn.getJSONObject(i);
	    		pStmt= dBconn.conn.prepareStatement( 			
						 "UPDATE p_parameters SET (pos,lastuser)=(?,?) WHERE id=?");
			   	pStmt.setInt(1, parameter.getInt("position"));
			   	pStmt.setInt(2, userID);
			   	pStmt.setInt(3, parameter.getInt("id"));
//				pStmt.addBatch();  // Does not work. I don't know why.
				pStmt.executeUpdate();
	    	}
			pStmt.close();
		} catch (JSONException e) {
			System.err.println("ChangeOrderPTParameters: Error parsing ID-Field");
			status = "Error parsing ID-Field";
			response.setStatus(404);
		} catch (SQLException e) {
			System.err.println("ChangeOrderPTParameters: Problems with SQL query");
			status = "SQL Error";
			response.setStatus(404);
		} catch (Exception e) {
			System.err.println("ChangeOrderPTParameters: Strange Problems");
			status = "Misc Error";
			response.setStatus(404);
		}

		dBconn.closeDB();
		
    // tell client that everything is fine
    PrintWriter out = response.getWriter();
	out.println("{\"status\":\""+status+"\"}");
	}
}	