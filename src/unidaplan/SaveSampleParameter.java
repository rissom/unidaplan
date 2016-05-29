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

	public class SaveSampleParameter extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@SuppressWarnings("resource")
	@Override
	  public void doPost(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {	
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
		String status="ok";
		String privilege="n";
	    request.setCharacterEncoding("utf-8");
	    String in = request.getReader().readLine();
	    JSONObject  jsonIn = null;	    
	    try {
			  jsonIn = new JSONObject(in);
		} catch (JSONException e) {
			System.err.println("SaveSampleParameter: Input is not valid JSON");
		}

	    
	    // get the id
	    int pid=0;
	    int sampleID=0;
	    try {
			 pid=jsonIn.getInt("parameterid");
			 sampleID=jsonIn.getInt("sampleid");
//			 System.out.println(jsonIn);
		} catch (JSONException e) {
			System.err.println("SaveSampleParameter: Error parsing ID-Field");
			response.setStatus(404);
			status="Error parsing ID-Field";
		}

	    
	 	DBconnection dBconn=new DBconnection();
	    PreparedStatement pStmt = null;
	    int datatype=-1;
	    
	    
		try {	
		    // look up the datatype in Database	    
		    dBconn.startDB();
		    pStmt= dBconn.conn.prepareStatement( 	
					"SELECT getSampleRights(vuserid:=?,vsample:=?)");
			pStmt.setInt(1,userID);
			pStmt.setInt(2,sampleID);
			privilege=dBconn.getSingleStringValue(pStmt);
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
		
		
		if (privilege.equals("w")){

			try{
				pStmt= dBconn.conn.prepareStatement( 			
						 "SELECT paramdef.datatype FROM Ot_parameters otp \n"
						+"JOIN paramdef ON otp.definition=paramdef.id \n"
						+"WHERE otp.id=?");
			   	pStmt.setInt(1, pid);
			   	JSONObject answer=dBconn.jsonObjectFromPreparedStmt(pStmt);
			   	pStmt.close();
				datatype= answer.getInt("datatype");			
				
				// delete old values.
				String[] tables = {"","o_integer_data","o_float_data",
								   "o_measurement_data","o_string_data",
								   "o_string_data","o_string_data",
								   "o_timestamp_data","o_integer_data",
								   "o_timestamp_data","o_string_data",
								   "o_string_data"};
				pStmt= dBconn.conn.prepareStatement( 			
						 "DELETE FROM "+tables[datatype]+" "
						+"WHERE ot_parameter_id=? AND objectid=?");
			   	pStmt.setInt(1, pid);
			   	pStmt.setInt(2, sampleID);
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
				e.printStackTrace();
			}
			
			// differentiate according to type
			// Datatype        INTEGER NOT NULL,  
			// 1: integer, 2: float, 3: measurement, 4: string, 5: long string 
			// 6: chooser, 7: date+time, 8: checkbox 9:timestring 10: URL
			try {	
	
			  switch (datatype) {
		        case 1:	if (jsonIn.has("value") && !jsonIn.isNull("value")){  
		        			pStmt= dBconn.conn.prepareStatement( 			// Integer values
		        					"INSERT INTO o_integer_data (objectid,ot_parameter_id,value,lastUser) VALUES (?,?,?,?)");
		        			pStmt.setInt(1, sampleID);
		        			pStmt.setInt(2, pid);
				   		  	pStmt.setInt(3, jsonIn.getInt("value"));
				   		  	pStmt.setInt(4, userID);
		        			pStmt.executeUpdate();
		        			pStmt.close();
		        		}
				   		break;
				   		
		        case 2: if (jsonIn.has("value") && !jsonIn.isNull("value")){  
		        			pStmt= dBconn.conn.prepareStatement( 			// Double values
		        					"INSERT INTO o_float_data (objectid,ot_parameter_id,value,lastuser) VALUES (?,?,?,?)");
		        			pStmt.setInt(1, sampleID);
		        			pStmt.setInt(2, pid);
		   				  	pStmt.setDouble(3, jsonIn.getDouble("value"));
		   				  	pStmt.setInt(4, userID);
		        			pStmt.executeUpdate();
		        			pStmt.close();
		        		}
		   				break;
	        			
		        case 3: if (jsonIn.has("value") && !jsonIn.isNull("value")){  	
		        			pStmt= dBconn.conn.prepareStatement( 			// Measurement data
		        					"INSERT INTO o_measurement_data (objectid,ot_parameter_id,value,error,lastUser) VALUES (?,?,?,?,?)");
		        			pStmt.setInt(1, sampleID);
		        			pStmt.setInt(2, pid);
		        			pStmt.setDouble(3, jsonIn.getDouble("value"));
		        			if (jsonIn.has("error")){
		        				pStmt.setDouble(4, jsonIn.getDouble("error")); 
		        			} else {
		        				pStmt.setNull(4, java.sql.Types.DOUBLE);
		        			}
		        			pStmt.setInt(5,userID);
		        			pStmt.executeUpdate();
		        			pStmt.close();
		        		}
						break;
				        
		        case 4: if (jsonIn.has("value") && !jsonIn.isNull("value")){  
		        			pStmt= dBconn.conn.prepareStatement( 			// String data	
		        					"INSERT INTO o_string_data (objectid,ot_parameter_id,value,lastUser) VALUES (?,?,?,?)");
		        			pStmt.setInt(1, sampleID);
		        			pStmt.setInt(2, pid);
		        			pStmt.setString(3, jsonIn.getString("value"));
		        			pStmt.setInt(4, userID);
		        			pStmt.executeUpdate();
		        			pStmt.close();
		        		}
		        		break;
				        
		        case 5: if (jsonIn.has("value") && !jsonIn.isNull("value")){  
			        		pStmt= dBconn.conn.prepareStatement( 			
			        				"INSERT INTO o_string_data (objectid,ot_parameter_id,value,lastUser) VALUES (?,?,?,?)");
		        			pStmt.setInt(1, sampleID);
		        			pStmt.setInt(2, pid);
							pStmt.setString(3, jsonIn.getString("value"));
							pStmt.setInt(4, userID);
		        			pStmt.executeUpdate();
		        			pStmt.close();
		        		}
						break;
					    
		        case 6: if (jsonIn.has("value") && !jsonIn.isNull("value")){   //  6: chooser, (saves as a string)
		        			pStmt= dBconn.conn.prepareStatement( 			
		        					"INSERT INTO o_string_data (objectid,ot_parameter_id,value,lastUser) VALUES (?,?,?,?)");
		        			pStmt.setInt(1, sampleID);
		        			pStmt.setInt(2, pid);
		        			pStmt.setString(3, jsonIn.getString("value"));
		        			pStmt.setInt(4, userID);
		        			pStmt.executeUpdate();
		        			pStmt.close();
		        		}
						break;
		        		
		        case 7: if (jsonIn.has("date") && !jsonIn.isNull("date")){   //   7: date,
	     		   		  	pStmt= dBconn.conn.prepareStatement( 			
	     		   		  			"INSERT INTO o_timestamp_data (objectid,ot_parameter_id,value,tz,lastUser) VALUES (?,?,?,?,?)");
	     		   		  	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
	     		   		  	SimpleDateFormat sqldf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	     		   		  	java.sql.Timestamp ts = java.sql.Timestamp.valueOf(sqldf.format(sdf.parse(jsonIn.getString("date"))));	
		        			pStmt.setInt(1, sampleID);
		        			pStmt.setInt(2, pid);
	     		   		  	pStmt.setTimestamp(3, (Timestamp) ts);
	     		   		  	pStmt.setInt(4, jsonIn.getInt("tz")); //Timezone in Minutes
	     		   		  	pStmt.setInt(5, userID);
		        			pStmt.executeUpdate();
		        			pStmt.close();
		        		}
	     		   		break;
	     			    
		        case 8: if (jsonIn.has("value") && !jsonIn.isNull("value")){  //   8: checkbox,
				   		  	pStmt= dBconn.conn.prepareStatement( 			
				   		  			"INSERT INTO o_integer_data (objectid,ot_parameter_id,value,lastUser) VALUES (?,?,?,?)");
				   		  	pStmt.setInt(1, sampleID);
					        pStmt.setInt(2, pid);
				   			pStmt.setInt(3, jsonIn.getBoolean("value")?1:0);
				   			pStmt.setInt(4, userID);
		        			pStmt.executeUpdate();
		        			pStmt.close();
						}
				   		break;
				        
		        case 9: if (jsonIn.has("date") && !jsonIn.isNull("date")){  //   9: timestamp,
					   	  	pStmt= dBconn.conn.prepareStatement( 			
					   	  			"INSERT INTO o_timestamp_data (objectid,ot_parameter_id,value,tz,lastUser) VALUES (?,?,?,?,?)");
					   		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
							SimpleDateFormat sqldf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
							java.sql.Timestamp ts = java.sql.Timestamp.valueOf(sqldf.format(sdf.parse(jsonIn.getString("date"))));		   
		        			pStmt.setInt(1, sampleID);
		        			pStmt.setInt(2, pid);
							pStmt.setTimestamp(3, (Timestamp) ts);
							pStmt.setInt(4, jsonIn.getInt("tz")); //Timezone in Minutes
							pStmt.setInt(5, userID);
		        			pStmt.executeUpdate();
		        			pStmt.close();
		        		}
					   	break;
				    
		        case 10: if (jsonIn.has("value") && !jsonIn.isNull("value")){
		        			pStmt= dBconn.conn.prepareStatement( 			// 10: URL
		        					"INSERT INTO o_string_data (objectid,ot_parameter_id,value,lastUser) VALUES (?,?,?,?)");
		        			pStmt.setInt(1, sampleID);
		        			pStmt.setInt(2, pid);
		        			pStmt.setString(3, jsonIn.getString("value"));
		        			pStmt.setInt(4, userID);
		        			pStmt.executeUpdate();
		        			pStmt.close();
		        		}	
		        		break;
				        
		        case 11: if (jsonIn.has("value") && !jsonIn.isNull("value")){ // 11: email
		        			pStmt= dBconn.conn.prepareStatement( 			
		        					"INSERT INTO o_string_data (objectid,ot_parameter_id,value,lastUser) VALUES (?,?,?,?)");
		        			pStmt.setInt(1, sampleID);
		        			pStmt.setInt(2, pid);
		        			pStmt.setString(3, jsonIn.getString("value"));
		        			pStmt.setInt(4, userID);
		        			pStmt.executeUpdate();
		        			pStmt.close();
		        		}
		        		break;
			} // end of switch Statement
			dBconn.closeDB();
		} catch (SQLException e) {
			System.err.println("SaveSampleParameter: More Problems with SQL query");
			e.printStackTrace();
		} catch (JSONException e){
			System.err.println("SaveSampleParameter: More Problems creating JSON");
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("SaveSampleParameter: More Strange Problems");
		}
		}else{
			response.setStatus(401);
			status="insufficient rights";
		}
		
    // tell client that everything is fine
	Unidatoolkit.sendStandardAnswer(status, response);
	}
}	