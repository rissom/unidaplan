package unidaplan;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

	public class UpdateExperimentParameter extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@SuppressWarnings("resource")
	@Override
	  public void doPost(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {
		
		Authentificator authentificator = new Authentificator();
		String status="ok";
		int userID=authentificator.GetUserID(request,response);
	    request.setCharacterEncoding("utf-8");
	    String in = request.getReader().readLine();
	    JSONObject  jsonIn = null;	    
	    try {
			  jsonIn = new JSONObject(in);
		} catch (JSONException e) {
			System.err.println("UpdateExperimentParameter: Input is not valid JSON");
		}
		response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    
	    // get the id
	    int id=0;
	    int pid=-1;
	    try {
			 id=jsonIn.getInt("id");	
     		pid=jsonIn.getInt("pid");
		} catch (JSONException e) {
			System.err.println("UpdateExperimentParameter: Error parsing ID-Field");
			status = "Error parsing ID-Field";
			response.setStatus(404);
		}

	    
	    
	    
	    // delete any previous entries
	 	DBconnection DBconn=new DBconnection();
	    DBconn.startDB();	   
	    PreparedStatement pstmt = null;
	    try {	
			pstmt= DBconn.conn.prepareStatement( 			
					 "DELETE FROM Expp_integer_data WHERE expp_id=? AND expp_param_id=?");
		   	pstmt.setInt(1, id);
		   	pstmt.setInt(2, pid);
		   	pstmt.executeUpdate();
			pstmt= DBconn.conn.prepareStatement( 			
					 "DELETE FROM Expp_float_data WHERE expp_id=? AND expp_param_id=?");
		   	pstmt.setInt(1, id);
		   	pstmt.setInt(2, pid);
		   	pstmt.executeUpdate();
			pstmt= DBconn.conn.prepareStatement( 			
					 "DELETE FROM Expp_string_data WHERE expp_id=? AND expp_param_id=?");
		   	pstmt.setInt(1, id);
		   	pstmt.setInt(2, pid);
		   	pstmt.executeUpdate();
			pstmt= DBconn.conn.prepareStatement( 			
					 "DELETE FROM Expp_measurement_data WHERE expp_id=? AND expp_param_id=?");
		   	pstmt.setInt(1, id);
		   	pstmt.setInt(2, pid);
		   	pstmt.executeUpdate();
			pstmt= DBconn.conn.prepareStatement( 			
					 "DELETE FROM Expp_timestamp_data WHERE expp_id=? AND expp_param_id=?");
		   	pstmt.setInt(1, id);
		   	pstmt.setInt(2, pid);
		   	pstmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("UpdateExperimentParameter: Problems with SQL query");
			status = "SQL Error";
		} catch (Exception e) {
			System.err.println("UpdateExperimentParameter: Strange Problems");
			status = "Misc Error (line67)";
		}
	    
	    
	    // look up the datatype in Database	    
	    int type=-1;
		try {	
			pstmt= DBconn.conn.prepareStatement( 			
					 "SELECT paramdef.datatype FROM Expp_param ep \n"
					+"JOIN paramdef ON ep.definition=paramdef.id \n"
					+"WHERE ep.id=?");
		   	pstmt.setInt(1, pid);
		   	JSONObject answer=DBconn.jsonObjectFromPreparedStmt(pstmt);
			type= answer.getInt("datatype");
		} catch (SQLException e) {
			System.err.println("UpdateExperimentParameter: Problems with SQL query");
			status = "SQL Error";
		} catch (JSONException e){
			System.err.println("UpdateExperimentParameter: Problems creating JSON");
			status = "JSON Error";
		} catch (Exception e) {
			System.err.println("UpdateExperimentParameter: Strange Problems");
			status = "Misc Error (line70)";
		}
		
		// differentiate according to type, insert new value in a table
		try {	

			switch (type) {
	        case 1: {   pstmt= DBconn.conn.prepareStatement( 			// Integer values
			   					 "INSERT INTO expp_integer_data VALUES (default,?,?,?,NOW(),?)");
						pstmt.setInt(1, id); //experiment ID
   						pstmt.setInt(2, pid); // Parameter ID
			   			pstmt.setInt(3, jsonIn.getInt("value")); // Value
	   					pstmt.setInt(4, userID); // UserID
			   			break;
			        }
	        case 2: {   pstmt= DBconn.conn.prepareStatement( 			// Double values
	   					 		"INSERT INTO expp_float_data VALUES (default,?,?,?,NOW(),?)");
						pstmt.setInt(1, id); //experiment ID
   						pstmt.setInt(2, pid); // Parameter ID
	   					pstmt.setDouble(3, jsonIn.getDouble("value"));
	   					pstmt.setInt(4, userID); // UserID
	   					break;
        			}
	        case 3: {   pstmt= DBconn.conn.prepareStatement( 			// Measurement data
						 		"INSERT INTO expp_measurement_data VALUES (default,?,?,?,?,NOW(),?)");
						pstmt.setInt(1, id); //experiment ID
						pstmt.setInt(2, pid); // Parameter ID
						pstmt.setDouble(3, Double.parseDouble(jsonIn.getString("value").split("±")[0]));
						pstmt.setDouble(4, Double.parseDouble(jsonIn.getString("value").split("±")[1]));
	   					pstmt.setInt(5, userID);
						break;
			        }
	        case 4:  { pstmt= DBconn.conn.prepareStatement( 			// String data	
				 		"INSERT INTO expp_string_data VALUES (default,?,?,?,NOW(),?)");
					   pstmt.setInt(1, id); //experiment ID
				       pstmt.setInt(2, pid); // Parameter ID
					   pstmt.setString(3, jsonIn.getString("value"));
	   				   pstmt.setInt(4, userID);
					   break;
			        }
	        case 5: {  pstmt= DBconn.conn.prepareStatement( 			// String data	
			 			"INSERT INTO expp_string_data VALUES (default,?,?,?,NOW(),?)");
					   pstmt.setInt(1, id); //experiment ID
				       pstmt.setInt(2, pid); // Parameter ID
					   pstmt.setString(3, jsonIn.getString("value"));
					   pstmt.setInt(4, userID);
					   break;
				   }
	        case 7: {  pstmt= DBconn.conn.prepareStatement( 			// String data	
			 			"INSERT INTO expp_timestamp_data VALUES (default,?,?,?,NOW(),?)");
					   pstmt.setInt(1, id); //experiment ID
				       pstmt.setInt(2, pid); // Parameter ID
				       java.sql.Timestamp ts= Timestamp.valueOf(jsonIn.getString("value"));
					   pstmt.setTimestamp(3, ts);
					   pstmt.setInt(4, userID);
					   break;
				   }
			}
		
		pstmt.executeUpdate();
		pstmt.close();
		DBconn.closeDB();
	} catch (SQLException e) {
		System.err.println("UpdateExperimentParameter: More Problems with SQL query");
		status = "SQL Error";
	} catch (JSONException e){
		System.err.println("UpdateExperimentParameter: More Problems creating JSON");
		status = "JSON Error";
	} catch (Exception e) {
		System.err.println("UpdateExperimentParameter: More Strange Problems");
		status = "Misc. Error";
	}
		
    // tell client that everything is fine
    PrintWriter out = response.getWriter();
	out.println("{\"status\":\""+status+"\"}");
	}
}	