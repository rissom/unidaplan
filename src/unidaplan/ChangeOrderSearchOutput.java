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
	    int searchID = 0;
	   	String privilege="n";
	    String table="";
	    String type = "";
	    PreparedStatement pStmt = null;
	    JSONObject  jsonIn = null;
	    JSONArray output = null;
	    
	    try {
			  jsonIn = new JSONObject(request.getReader().readLine());
			  output = jsonIn.getJSONArray("output");
			  type   = jsonIn.getString("type");
		} catch (JSONException e) {
			System.err.println("ChangeOrderSearchOutput: Input is not valid JSON");
		}

	    
	    
	   
	    try {
		    // Initialize Database
			DBconnection dBconn=new DBconnection();
		    dBconn.startDB();	
		    
		    switch (type) {
			    case "o"  : table="osearchoutput";  break;
			    case "p"  : table="psearchoutput";  break;
			    case "po" : table="posearchoutput"; break;
		    }		    

		    // Check privileges
		    int outputid = output.getJSONObject(0).getInt("outputid");
		    pStmt = dBconn.conn.prepareStatement( 	
					"SELECT search FROM "+table+" WHERE id=?");
			pStmt.setInt(1,outputid);
			searchID = dBconn.getSingleIntValue(pStmt);
			pStmt.close();
		    
		    pStmt = dBconn.conn.prepareStatement( 	
					"SELECT getExperimentRights(vuserid:=?,vexperimentid:=?)");
			pStmt.setInt(1,userID);
			pStmt.setInt(2,searchID);
			privilege = dBconn.getSingleStringValue(pStmt);
			pStmt.close();
						
			if (privilege.equals("w")){
		    
		   
			   
		    	for (int i=0;i<output.length();i++){
		    		JSONObject parameter = output.getJSONObject(i);
		    		pStmt= dBconn.conn.prepareStatement( 			
							 "UPDATE "+table+" SET (position,lastuser)=(?,?) WHERE id=?");
				   	pStmt.setInt(1, parameter.getInt("position"));
				   	pStmt.setInt(2, userID);
				   	pStmt.setInt(3, parameter.getInt("outputid"));
	//				pStmt.addBatch();  // Does not work. I don't know why.
					pStmt.executeUpdate();				
		    	}
				pStmt.close();
			} else{
				response.setStatus(401);
			}
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