package unidaplan;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

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
		  public void doPut(HttpServletRequest request, HttpServletResponse response)
			      throws ServletException, IOException {
			
			Authentificator authentificator = new Authentificator();
			int userID = authentificator.GetUserID(request,response);
			String status="ok";
		    request.setCharacterEncoding("utf-8");
		    // look up the datatype in Database	    
		    int dataUserID = -1;
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
			    PreparedStatement pStmt = null;
			    dBconn.startDB();
			    
			  	if (dataUserID==userID || dBconn.isAdmin(userID)){

				    
				    // update preferred language
				    if (jsonIn.has("preferredlanguage")){
				    	pStmt= dBconn.conn.prepareStatement( 			
								 "UPDATE users SET preferredlanguage=? WHERE id=?");
				    	pStmt.setString(1, jsonIn.getString("preferredlanguage"));
					   	pStmt.setInt(2, dataUserID);
					   	pStmt.executeUpdate();
				    }
				    
				    // update fullname
				    if (jsonIn.has("fullname")){
				    	pStmt= dBconn.conn.prepareStatement( 			
								 "UPDATE users SET fullname=? WHERE id=?");
				    	pStmt.setString(1, jsonIn.getString("fullname"));
					   	pStmt.setInt(2, dataUserID);
					   	pStmt.executeUpdate();
				    }
				    
				    // update TokenValidTo
				    if (jsonIn.has("tokenvalidto")){
				    	pStmt= dBconn.conn.prepareStatement(
								 "UPDATE users SET token_valid_to=? WHERE id=?");
				    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");
						SimpleDateFormat sqldf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
						java.sql.Timestamp ts = java.sql.Timestamp.valueOf(sqldf.format(sdf.parse(jsonIn.getString("tokenvalidto"))));		   
						pStmt.setTimestamp(1, (Timestamp) ts);
					   	pStmt.setInt(2, dataUserID);
					   	pStmt.executeUpdate();
				    }
				    
				    // update username
				    if (jsonIn.has("username")){
				    	pStmt= dBconn.conn.prepareStatement(
								 "UPDATE users SET username=? WHERE id=?");
				    	pStmt.setString(1, jsonIn.getString("username"));
					   	pStmt.setInt(2, dataUserID);
					   	pStmt.executeUpdate();
				    }
				    
				    // update email
				    if (jsonIn.has("email")){
				    	pStmt= dBconn.conn.prepareStatement(
								 "UPDATE users SET email=? WHERE id=?");
				    	pStmt.setString(1, jsonIn.getString("email"));
					   	pStmt.setInt(2, dataUserID);
					   	pStmt.executeUpdate();
				    }
				    
				    // update email
				    if (jsonIn.has("blocked")){
				    	pStmt= dBconn.conn.prepareStatement(
								 "UPDATE users SET blocked=? WHERE id=?");
				    	pStmt.setBoolean(1, jsonIn.getBoolean("blocked"));
					   	pStmt.setInt(2, dataUserID);
					   	pStmt.executeUpdate();
				    }
				  
			  	} else {
			  	    response.setStatus(401);
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
				e.printStackTrace();
			}
			
	    // tell client that everything is fine
	    Unidatoolkit.sendStandardAnswer(status, response);
	    
	}	
}