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

	public class SaveProcessParameter extends HttpServlet {
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
			System.err.println("SaveProcessParameter: Input is not valid JSON");
			status="error";
		}
		response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    PreparedStatement pStmt;

	    // get the id
	    int processID=0;
	    int parameterID=-1;
	    int datatype=-1;
	    
	    try {
			processID=jsonIn.getInt("processid");	
     		parameterID=jsonIn.getInt("parameterid");
		} catch (JSONException e) {
			System.err.println("SaveProcessParameter: Error parsing ID-Field");
			status="error parsing ID-Field";
			response.setStatus(404);
		}

	 	DBconnection dBconn=new DBconnection();
	 	
	 	try {	
		    // look up the datatype in Database	    
		    dBconn.startDB();	   
			pStmt = dBconn.conn.prepareStatement( 			
					 "SELECT paramdef.datatype FROM p_parameters pp \n"
					+"JOIN paramdef ON pp.definition=paramdef.id \n"
					+"WHERE pp.id=?");
		   	pStmt.setInt(1, parameterID);
		   	JSONObject answer=dBconn.jsonObjectFromPreparedStmt(pStmt);
		   	pStmt.close();
			datatype= answer.getInt("datatype");			
			

			// delete the old values.
			String[] tables={"","p_integer_data","p_float_data","p_measurement_data","p_string_data","p_string_data","p_string_data","p_timestamp_data","p_integer_data","p_timestamp_data","p_string_data"};
			pStmt= dBconn.conn.prepareStatement( 			
					 "DELETE FROM "+tables[datatype]+" "
					+"WHERE p_parameter_id=? AND processid=?");
		   	pStmt.setInt(1, parameterID);
		   	pStmt.setInt(2, processID);
		   	pStmt.executeUpdate();
		   	pStmt.close();
		} catch (SQLException e) {
			System.err.println("SaveSampleParameter: Problems with SQL query");
			status="Problems with SQL query";
			e.printStackTrace();
		} catch (JSONException e){
			System.err.println("SaveSampleParameter: Problems creating JSON");
			status="Problems creating JSON";
		} catch (Exception e) {
			System.err.println("SaveSampleParameter: Strange Problems");
			status="Strange Problems";
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
			System.err.println("SaveProcessParameter: Problems with SQL query");
			status="error";
		} catch (JSONException e){
			System.err.println("SaveProcessParameter: Problems creating JSON");
			status="error";
		} catch (Exception e) {
			System.err.println("SaveProcessParameter: Strange Problems");
			status="error";
		}
		
		pStmt=null; // fooling eclipse to not show warnings
		
		int id=0; // id of the newly created value
		// differentiate according to type
		try {	
			switch (type) {
			case 1:	if (jsonIn.has("value") && !jsonIn.isNull("value") ){
        				pStmt= dBconn.conn.prepareStatement( 			// Integer values
        						"INSERT INTO p_integer_data VALUES(DEFAULT,?,?,?,NOW(),?) RETURNING ID");
			   			pStmt.setInt(1, processID);
				        pStmt.setInt(2, parameterID);
			   			pStmt.setInt(3, jsonIn.getInt("value"));
			   			pStmt.setInt(4, userID);
			   			id=dBconn.getSingleIntValue(pStmt);
			   	   		pStmt.close();
        			}
		   			break;
			        
	        case 2:	if (jsonIn.has("value") && !jsonIn.isNull("value")){ // Double values
	        			pStmt= dBconn.conn.prepareStatement( 	
				         "INSERT INTO p_float_data VALUES(DEFAULT,?,?,?,NOW(),?) RETURNING ID");
				        pStmt.setInt(1, processID);
			   			pStmt.setInt(2, parameterID);
        				pStmt.setDouble(3, jsonIn.getDouble("value"));		
				        pStmt.setInt(4, userID);
				        id=dBconn.getSingleIntValue(pStmt);
				   		pStmt.close();
				   	}
   					break;
        			
	        case 3:	if (jsonIn.has("value") && !jsonIn.isNull("value")){   
	        			pStmt= dBconn.conn.prepareStatement( 			// Measurement data
				         "INSERT INTO p_measurement_data VALUES(DEFAULT,?,?,?,?,NOW(),?) RETURNING ID");
						pStmt.setInt(1, processID);
						pStmt.setInt(2, parameterID);
			   			pStmt.setDouble(3, jsonIn.getDouble("value"));
						if (jsonIn.has("error") && !jsonIn.isNull("error")){
							pStmt.setDouble(4, jsonIn.getDouble("error"));
						} else {
							pStmt.setNull(4,java.sql.Types.DOUBLE);
						}
						pStmt.setInt(5, userID);
						id=dBconn.getSingleIntValue(pStmt);
				   		pStmt.close();
        			}
					break;
					
	        case 4: if (jsonIn.has("value") && !jsonIn.isNull("value")){ 
	        			pStmt= dBconn.conn.prepareStatement( 			// String data	
	        					"INSERT INTO p_string_data VALUES(DEFAULT,?,?,?,NOW(),?) RETURNING ID");
	        			pStmt.setInt(1, processID);
	        			pStmt.setInt(2, parameterID);
	        			pStmt.setString(3, jsonIn.getString("value"));
	        			pStmt.setInt(4, userID);
	        			id=dBconn.getSingleIntValue(pStmt);
				   		pStmt.close();
	        		}
	        		break;
			        
	        case 5:	if (jsonIn.has("value") && !jsonIn.isNull("value")){ //String
	        			pStmt= dBconn.conn.prepareStatement(
	        					"INSERT INTO p_string_data VALUES(DEFAULT,?,?,?,NOW(),?) RETURNING ID");
	        			pStmt.setInt(1, processID);
	        			pStmt.setInt(2, parameterID); 
				    	pStmt.setString(3, jsonIn.getString("value"));
				    	pStmt.setInt(4, userID);
				    	id=dBconn.getSingleIntValue(pStmt);
				   		pStmt.close();
	        		}
	        		break;
	        		
	        case 6:	if (jsonIn.has("value") && !jsonIn.isNull("value")){ //String
		    			pStmt= dBconn.conn.prepareStatement(
		    					"INSERT INTO p_string_data VALUES(DEFAULT,?,?,?,NOW(),?) RETURNING ID");
		    			pStmt.setInt(1, processID);
		    			pStmt.setInt(2, parameterID); 
				    	pStmt.setString(3, jsonIn.getString("value"));
				    	pStmt.setInt(4, userID);
				    	id=dBconn.getSingleIntValue(pStmt);
				   		pStmt.close();
		    		}
		    		break;
				   
	        case 7:	if (jsonIn.has("value") && !jsonIn.isNull("date")){ // Date 
	        			pStmt= dBconn.conn.prepareStatement( 				
	        					"INSERT INTO p_timestamp_data VALUES (default,?,?,?,?,NOW(),?) RETURNING ID");
	        			pStmt.setInt(1, processID);
	        			pStmt.setInt(2, parameterID);
	        			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
	        			SimpleDateFormat sqldf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	        			java.sql.Timestamp ts = java.sql.Timestamp.valueOf(sqldf.format(sdf.parse(jsonIn.getString("date"))));
	        			pStmt.setTimestamp(3, (Timestamp) ts);
	        			pStmt.setInt(4, jsonIn.getInt("tz")); //Timezone in Minutes
	        			pStmt.setInt(5, userID);
	        		}
				    break;
			   
	        case 9: if (jsonIn.has("value") && !jsonIn.isNull("date")){ 
	        			pStmt= dBconn.conn.prepareStatement( 			// Timestamp data	
	        					"INSERT INTO p_timestamp_data VALUES (default,?,?,?,?,NOW(),?) RETURNING ID");
	        			pStmt.setInt(1, processID);
	        			pStmt.setInt(2, parameterID);
	        			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
	        			SimpleDateFormat sqldf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	        			java.sql.Timestamp ts = java.sql.Timestamp.valueOf(sqldf.format(sdf.parse(jsonIn.getString("date"))));
	        			pStmt.setTimestamp(3, (Timestamp) ts);
	        			pStmt.setNull(3, java.sql.Types.TIMESTAMP);
	        			pStmt.setInt(4, jsonIn.getInt("tz")); //Timezone in Minutes
	        			pStmt.setInt(5, userID);
	       			}
				    break;
	        case 10: if (jsonIn.has("value") && !jsonIn.isNull("value")){ 
	        	 		pStmt= dBconn.conn.prepareStatement( 			// URL
	        	 				"INSERT INTO p_string_data VALUES(DEFAULT,?,?,?,NOW(),?) RETURNING ID");
	        	 		pStmt.setInt(1, processID);
				       	pStmt.setInt(2, parameterID);
				       	pStmt.setString(3, jsonIn.getString("value"));
				       	pStmt.setInt(4, userID);
	        		}
					break;
	        case 11: if (jsonIn.has("value") && !jsonIn.isNull("value")){ 
	        			pStmt= dBconn.conn.prepareStatement( 			// e-mail	
	        					"INSERT INTO p_string_data VALUES(DEFAULT,?,?,?,NOW(),?) RETURNING ID");
	        			pStmt.setInt(1, processID);
	        			pStmt.setInt(2, parameterID);
	        			pStmt.setString(3, jsonIn.getString("value"));
	        			pStmt.setInt(4, userID);
		        	}
					break; // completely unnecessary
	        
			} // end of switch statement
		
		dBconn.closeDB();
	    // tell client that everything is fine
	    PrintWriter out = response.getWriter();
	    JSONObject myResponse= new JSONObject();
	    myResponse.put("status", status);
	    myResponse.put("id", id);
		out.println(myResponse.toString());
	} catch (SQLException e) {
		System.err.println("SaveProcessParameter: More Problems with SQL query");
		status="error";
		e.printStackTrace();
	} catch (JSONException e){
		System.err.println("SaveProcessParameter: More Problems creating JSON");
		e.printStackTrace();
		System.err.println(pStmt.toString());
		status="error";
	} catch (Exception e) {
		System.err.println("SaveProcessParameter: More Strange Problems");
		System.err.println(pStmt.toString());
		e.printStackTrace();
		status="error";
	}
		
	}
}	