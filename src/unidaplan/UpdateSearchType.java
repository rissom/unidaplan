package unidaplan;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

	public class UpdateSearchType extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doPut(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {
		
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
		
	    request.setCharacterEncoding("utf-8");
	    String status="ok";
	    String in = request.getReader().readLine();
	    JSONObject  jsonIn = null;	    
	    try {
			  jsonIn = new JSONObject(in);
		} catch (JSONException e) {
			System.err.println("UpdateSearchType: Input is not valid JSON");
			status="error";
		}
		response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    PreparedStatement pStmt;

	    int searchID=0;
	    int newType=0;
	    try {
			searchID=jsonIn.getInt("searchid");	
			newType=jsonIn.getInt("type");
		} catch (JSONException e) {
			System.err.println("UpdateSearchType: Error parsing ID-Field");
			status="error parsing ID-Field";
			response.setStatus(404);
		}

	 	DBconnection dBconn=new DBconnection();
	 	
	    try{
		    dBconn.startDB();
	 	
		 	 // check privileges
		    pStmt = dBconn.conn.prepareStatement("SELECT getSearchRights(vuserid:=?,vsearchid:=?)");
		    pStmt.setInt(1,userID);
		    pStmt.setInt(2,searchID);
		    String privilege = dBconn.getSingleStringValue(pStmt);
		    
		    if (privilege.equals("w")){	 	
		 	
			    // update type
		 
			    
			    pStmt= dBconn.conn.prepareStatement(
			    		"SELECT owner FROM searches WHERE id=?");
			    pStmt.setInt(1, searchID);
			    int owner= dBconn.getSingleIntValue(pStmt);
			    pStmt.close();
			    int admins=1;
			    if (userID==owner || Unidatoolkit.isMemberOfGroup(userID,admins,dBconn)){
				    pStmt= dBconn.conn.prepareStatement(
				    		"UPDATE searches SET (type,lastchange,lastuser)=(?,NOW(),?) WHERE id=?");
				    pStmt.setInt(1, newType);
				    pStmt.setInt(2, userID);
				    pStmt.setInt(3, searchID);
				    pStmt.executeUpdate();
				    pStmt.close();
				    
				    //TODO: Delete all criteria and output parameters
			    }
		    } else {
				response.setStatus(401);
			}
	    } catch (SQLException e) {
			System.err.println("UpdateSearchType: Problems with SQL query for deletion");
			status="error";
		} catch (Exception e) {
			System.err.println("UpdateSearchType: Strange Problems deleting old parameter");
			status="error";
		}
	   
		
			
		try{
			dBconn.closeDB();
		    // tell client that everything is fine
			Unidatoolkit.sendStandardAnswer(status, response);
		} catch (Exception e) {
			System.err.println("UpdateSearchType: More Strange Problems");
			status="error";
		}
		
	}
}	