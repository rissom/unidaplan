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
	    int expID=0;
	    int expParamID=-1;
	    try {
			 expID=jsonIn.getInt("experimentid");	
     		 expParamID=jsonIn.getInt("id");
		} catch (JSONException e) {
			System.err.println("UpdateExperimentParameter: Error parsing ID-Field");
			status = "Error parsing ID-Field";
			response.setStatus(404);
		}

	    
	    
	    
	    // delete any previous entries
	 	DBconnection dBconn=new DBconnection();
	    PreparedStatement pStmt = null;
	    try {	
		    dBconn.startDB();	   

			pStmt= dBconn.conn.prepareStatement( 			
					 "DELETE FROM Expp_integer_data WHERE expp_id=? AND expp_param_id=?");
		   	pStmt.setInt(1, expID);
		   	pStmt.setInt(2, expParamID);
		   	pStmt.executeUpdate();
			pStmt= dBconn.conn.prepareStatement( 			
					 "DELETE FROM Expp_float_data WHERE expp_id=? AND expp_param_id=?");
		   	pStmt.setInt(1, expID);
		   	pStmt.setInt(2, expParamID);
		   	pStmt.executeUpdate();
			pStmt= dBconn.conn.prepareStatement( 			
					 "DELETE FROM Expp_string_data WHERE expp_id=? AND expp_param_id=?");
		   	pStmt.setInt(1, expID);
		   	pStmt.setInt(2, expParamID);
		   	pStmt.executeUpdate();
			pStmt= dBconn.conn.prepareStatement( 			
					 "DELETE FROM Expp_measurement_data WHERE expp_id=? AND expp_param_id=?");
		   	pStmt.setInt(1, expID);
		   	pStmt.setInt(2, expParamID);
		   	pStmt.executeUpdate();
			pStmt= dBconn.conn.prepareStatement( 			
					 "DELETE FROM Expp_timestamp_data WHERE expp_id=? AND expp_param_id=?");
		   	pStmt.setInt(1, expID);
		   	pStmt.setInt(2, expParamID);
		   	pStmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("UpdateExperimentParameter: Problems with SQL query");
			status = "SQL Error";
		} catch (Exception e) {
			System.err.println("UpdateExperimentParameter: Strange Problems");
			status = "Misc Error (line67)";
		}
	    
	    
	    // look up the datatype in Database	    
	    int dataType=-1;
		try {	

			pStmt= dBconn.conn.prepareStatement( 			
					 "SELECT paramdef.datatype FROM Expp_param ep \n"
					+"JOIN paramdef ON ep.definition=paramdef.id \n"
					+"WHERE ep.id=?");
		   	pStmt.setInt(1, expParamID);
			dataType= dBconn.getSingleIntValue(pStmt);
		} catch (SQLException e) {
			System.err.println("UpdateExperimentParameter: Problems with SQL query");
			status = "SQL Error";
		} catch (JSONException e){
			System.err.println("UpdateExperimentParameter: Problems creating JSON");
			status = "JSON Error";
		} catch (Exception e) {
			System.err.println("UpdateExperimentParameter: Strange Problems");
			status = "Misc Error";
		}
		
		// differentiate according to type, insert new value in a table
		try {	
			switch (dataType) {
	        case 1: {   pStmt= dBconn.conn.prepareStatement( 			// Integer values
			   					 "INSERT INTO expp_integer_data VALUES (default,?,?,?,NOW(),?)");
						pStmt.setInt(1, expID); //experiment ID
   						pStmt.setInt(2, expParamID); // Parameter ID
			   			pStmt.setInt(3, jsonIn.getInt("value")); // Value
	   					pStmt.setInt(4, userID); // UserID
			   			break;
			        }
	        case 2: {   pStmt= dBconn.conn.prepareStatement( 			// Double values
	   					 		"INSERT INTO expp_float_data VALUES (default,?,?,?,NOW(),?)");
						pStmt.setInt(1, expID); //experiment ID
   						pStmt.setInt(2, expParamID); // Parameter ID
	   					pStmt.setDouble(3, jsonIn.getDouble("value"));
	   					pStmt.setInt(4, userID); // UserID
	   					break;
        			}
	        case 3: {   pStmt= dBconn.conn.prepareStatement( 			// Measurement data
						 		"INSERT INTO expp_measurement_data VALUES (default,?,?,?,?,NOW(),?)");
						pStmt.setInt(1, expID); //experiment ID
						pStmt.setInt(2, expParamID); // Parameter ID
						pStmt.setDouble(3, Double.parseDouble(jsonIn.getString("value").split("±")[0]));
						pStmt.setDouble(4, Double.parseDouble(jsonIn.getString("value").split("±")[1]));
	   					pStmt.setInt(5, userID);
						break;
			        }
	        case 4: { pStmt= dBconn.conn.prepareStatement( 			// String data	
				 		"INSERT INTO expp_string_data VALUES (default,?,?,?,NOW(),?)");
					   pStmt.setInt(1, expID); //experiment ID
				       pStmt.setInt(2, expParamID); // Parameter ID
					   pStmt.setString(3, jsonIn.getString("value"));
	   				   pStmt.setInt(4, userID);
					   break;
			        }
	        case 5: {  pStmt= dBconn.conn.prepareStatement( 			// String data	
			 			"INSERT INTO expp_string_data VALUES (default,?,?,?,NOW(),?)");
					   pStmt.setInt(1, expID); //experiment ID
				       pStmt.setInt(2, expParamID); // Parameter ID
					   pStmt.setString(3, jsonIn.getString("value"));
					   pStmt.setInt(4, userID);
					   break;
				    }
	        case 7: {  pStmt= dBconn.conn.prepareStatement( 			// Timestamp data	
			 			"INSERT INTO expp_timestamp_data VALUES (default,?,?,?,?,NOW(),?)");
					   pStmt.setInt(1, expID); //experiment ID
				       pStmt.setInt(2, expParamID); // Parameter ID
					   SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
					   SimpleDateFormat sqldf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
					   java.sql.Timestamp ts = java.sql.Timestamp.valueOf(sqldf.format(sdf.parse(jsonIn.getString("date"))));		   
					   pStmt.setTimestamp(3, (Timestamp) ts);
					   pStmt.setInt(4, jsonIn.getInt("tz")); //Timezone in Minutes
					   pStmt.setInt(5, userID);
					   break;
				   }
			}
		
			pStmt.executeUpdate();
			pStmt.close();
			dBconn.closeDB();
		} catch (SQLException e) {
			System.err.println("UpdateExperimentParameter: More Problems with SQL query");
			status = "SQL Error";
		} catch (JSONException e){
			System.err.println("UpdateExperimentParameter: More Problems creating JSON");
			status = "JSON Error";
		} catch (Exception e) {
			System.err.println("UpdateExperimentParameter: More Strange Problems");
			e.printStackTrace();
			status = "Misc. Error";
		}
			
	    // tell client that everything is fine
	    Unidatoolkit.sendStandardAnswer(status, response);
	}
}	