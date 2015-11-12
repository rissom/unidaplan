package unidaplan;
import java.io.IOException;
import java.io.PrintWriter;
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

	public class UpdateProcessParameter extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doPost(HttpServletRequest request, HttpServletResponse response)
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
			System.err.println("UpdateProcessParameter: Input is not valid JSON");
			status="error";
		}
		response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    PreparedStatement pStmt;

	    // get the id
	    int processID=0;
	    int parameterID=-1;
	    try {
			processID=jsonIn.getInt("processid");	
     		parameterID=jsonIn.getInt("parameterid");
		} catch (JSONException e) {
			System.err.println("UpdateProcessParameter: Error parsing ID-Field");
			status="error parsing ID-Field";
			response.setStatus(404);
		}

	 	DBconnection dBconn=new DBconnection();
	    dBconn.startDB();
	    
	    // Delete the old parameter.
	    try{
		    pStmt= dBconn.conn.prepareStatement( "DELETE FROM p_string_data WHERE ProcessID=? AND P_Parameter_ID=?");
		    pStmt.setInt(1, processID);
		    pStmt.setInt(2, parameterID);
		    pStmt.executeUpdate();
		    pStmt= dBconn.conn.prepareStatement( "DELETE FROM p_float_data WHERE ProcessID=? AND P_Parameter_ID=?");
		    pStmt.setInt(1, processID);
		    pStmt.setInt(2, parameterID);
		    pStmt.executeUpdate();
		    pStmt= dBconn.conn.prepareStatement( "DELETE FROM p_integer_data WHERE ProcessID=? AND P_Parameter_ID=?");
		    pStmt.setInt(1, processID);
		    pStmt.setInt(2, parameterID);
		    pStmt.executeUpdate();
		    pStmt= dBconn.conn.prepareStatement( "DELETE FROM p_measurement_data WHERE ProcessID=? AND P_Parameter_ID=?");
		    pStmt.setInt(1, processID);
		    pStmt.setInt(2, parameterID);
		    pStmt.executeUpdate();
		    pStmt.close();
	    } catch (SQLException e) {
			System.err.println("UpdateProcessParameter: Problems with SQL query for deletion");
			status="error";
		} catch (Exception e) {
			System.err.println("UpdateProcessParameter: Strange Problems deleting old parameter");
			status="error";
		}
	    
	    
	    // look up the datatype in Database	    
	    int type=-1;
		try {	
			pStmt= dBconn.conn.prepareStatement( 			
					 "SELECT paramdef.datatype FROM p_parameters p "
					+"JOIN paramdef ON p.definition=paramdef.id "
					+"WHERE p.id=?");
		   	pStmt.setInt(1, parameterID);
		   	JSONObject answer=dBconn.jsonObjectFromPreparedStmt(pStmt);
			type= answer.getInt("datatype");
		} catch (SQLException e) {
			System.err.println("UpdateProcessParameter: Problems with SQL query");
			status="error";
		} catch (JSONException e){
			System.err.println("UpdateProcessParameter: Problems creating JSON");
			status="error";
		} catch (Exception e) {
			System.err.println("UpdateProcessParameter: Strange Problems");
			status="error";
		}
		
		pStmt=null; // fooling eclipse to not show warnings
		
		// differentiate according to type
		try {	
			switch (type) {
	        case 1: {   pStmt= dBconn.conn.prepareStatement( 			// Integer values
				         "INSERT INTO p_integer_data VALUES(DEFAULT,?,?,?,NOW(),?) RETURNING ID");
			   			pStmt.setInt(1, processID);
				        pStmt.setInt(2, parameterID);
			   			pStmt.setInt(3, jsonIn.getInt("value"));
			   			pStmt.setInt(4, userID);
			   			break;
			        }
	        case 2: {   pStmt= dBconn.conn.prepareStatement( 			// Double values
				         "INSERT INTO p_float_data VALUES(DEFAULT,?,?,?,NOW(),?) RETURNING ID");
				        pStmt.setInt(1, processID);
			   			pStmt.setInt(2, parameterID);				        
				        pStmt.setDouble(3, jsonIn.getDouble("value"));
				        pStmt.setInt(4, userID);
	   					break;
        			}
	        case 3: {   pStmt= dBconn.conn.prepareStatement( 			// Measurement data
				         "INSERT INTO p_measurement_data VALUES(DEFAULT,?,?,?,?,NOW(),?) RETURNING ID");
						pStmt.setInt(1, processID);
						pStmt.setInt(2, parameterID);
						pStmt.setDouble(3, Double.parseDouble(jsonIn.getString("value").split("±")[0]));
						pStmt.setDouble(4, Double.parseDouble(jsonIn.getString("value").split("±")[1]));
						pStmt.setInt(5, userID);
						break;
			        }
	        case 4:  { pStmt= dBconn.conn.prepareStatement( 			// String data	
			        	"INSERT INTO p_string_data VALUES(DEFAULT,?,?,?,NOW(),?) RETURNING ID");
				       pStmt.setInt(1, processID);
				       pStmt.setInt(2, parameterID);
				       pStmt.setString(3, jsonIn.getString("value"));
				       pStmt.setInt(4, userID);
					   break;
			        }
	        case 5: {  pStmt= dBconn.conn.prepareStatement(
        		 		"INSERT INTO p_string_data VALUES(DEFAULT,?,?,?,NOW(),?) RETURNING ID");
				       pStmt.setInt(1, processID);
				       pStmt.setInt(2, parameterID);
				       pStmt.setString(3, jsonIn.getString("value"));
				       pStmt.setInt(4, userID);
				   }
	        case 7: {  pStmt= dBconn.conn.prepareStatement( 			// Timestamp data	
		 			"INSERT INTO p_timestamp_data VALUES (default,?,?,?,?,NOW(),?)");
			       pStmt.setInt(1, processID);
			       pStmt.setInt(2, parameterID);
				   SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
				   SimpleDateFormat sqldf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
				   java.sql.Timestamp ts = java.sql.Timestamp.valueOf(sqldf.format(sdf.parse(jsonIn.getString("value"))));		   
				   pStmt.setTimestamp(3, (Timestamp) ts);
				   pStmt.setInt(4, jsonIn.getInt("tz")); //Timezone in Minutes
				   pStmt.setInt(5, userID);
				   break;
			   }
			}
		
	    int id=dBconn.getSingleIntValue(pStmt);
   		pStmt.close();
		dBconn.closeDB();
	    // tell client that everything is fine
	    PrintWriter out = response.getWriter();
	    JSONObject myResponse= new JSONObject();
	    myResponse.put("status", status);
	    myResponse.put("id", id);
		out.println(myResponse.toString());
	} catch (SQLException e) {
		System.err.println("UpdateProcessParameter: More Problems with SQL query");
		status="error";
		e.printStackTrace();
	} catch (JSONException e){
		System.err.println("UpdateProcessParameter: More Problems creating JSON");
		status="error";
	} catch (Exception e) {
		System.err.println("UpdateProcessParameter: More Strange Problems");
		status="error";
	}
		
	}
}	