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

//WebServlet("/change-experiment-status")
public class UpdateUserData extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
		@Override
		  public void doPost(HttpServletRequest request, HttpServletResponse response)
			      throws ServletException, IOException {
			
			Authentificator authentificator = new Authentificator();
			int userID=authentificator.GetUserID(request,response);
			String status="ok";
		    request.setCharacterEncoding("utf-8");
		    // look up the datatype in Database	    
		    int dataUserID=-1;
		    String in = request.getReader().readLine();
		    JSONObject jsonIn = null;
		    try {
				  jsonIn = new JSONObject(in);
			} catch (JSONException e) {
				response.setStatus(404);
				System.err.println("Result: Input is not valid JSON");
				status="no parameters for performing search";
			}
		    
		    
		  	try {
		   		 dataUserID=jsonIn.getInt("userid"); 
		    } catch (Exception e1) {
		   		System.err.println("no userID given!");
				response.setStatus(404);
		   	}
		    
		  	
		    

			try {	
				
				// initialize database
			 	DBconnection dBconn=new DBconnection();
			    PreparedStatement pstmt = null;
			    dBconn.startDB();
			    
			  	if (dataUserID==userID || Unidatoolkit.isMemberOfGroup(userID, 1, dBconn)){

				    
				    // update preferred language
				    if (jsonIn.has("preferredlanguage")){
				    	pstmt= dBconn.conn.prepareStatement( 			
								 "UPDATE users SET preferredlanguage=? WHERE id=?");
				    	pstmt.setString(1, jsonIn.getString("preferredlanguage"));
					   	pstmt.setInt(2, dataUserID);
					   	pstmt.executeUpdate();
				    }
				    
				    // update fullname
				    if (jsonIn.has("fullname")){
				    	pstmt= dBconn.conn.prepareStatement( 			
								 "UPDATE users SET fullname=? WHERE id=?");
				    	pstmt.setString(1, jsonIn.getString("fullname"));
					   	pstmt.setInt(2, dataUserID);
					   	pstmt.executeUpdate();
				    }
				    
				    // update username
				    if (jsonIn.has("username")){
				    	pstmt= dBconn.conn.prepareStatement(
								 "UPDATE users SET username=? WHERE id=?");
				    	pstmt.setString(1, jsonIn.getString("username"));
					   	pstmt.setInt(2, dataUserID);
					   	pstmt.executeUpdate();
				    }
				    
				    // update email
				    if (jsonIn.has("email")){
				    	pstmt= dBconn.conn.prepareStatement(
								 "UPDATE users SET email=? WHERE id=?");
				    	pstmt.setString(1, jsonIn.getString("email"));
					   	pstmt.setInt(2, dataUserID);
					   	pstmt.executeUpdate();
				    }
				  
			  	}
			    dBconn.closeDB();
				
			} catch (SQLException e) {
				System.err.println("SaveSampleParameter: Problems with SQL query");
				status="SaveSampleParameter: Problems with SQL query";
				response.setStatus(404);
			} catch (Exception e) {
				System.err.println("SaveSampleParameter: Strange Problems");
				status="SaveSampleParameter: Strange Problems";
				response.setStatus(404);
			}
			
	    // tell client that everything is fine
	    Unidatoolkit.sendStandardAnswer(status, response);
	    
	}	
}