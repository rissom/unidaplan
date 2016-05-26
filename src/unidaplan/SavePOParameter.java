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

	public class SavePOParameter extends HttpServlet {
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
			System.err.println("SavePOParameter: Input is not valid JSON");
		}

	    
	    // get the id
	    int popid = 0;
	    int opid = 0;
	    try {
			 popid=jsonIn.getInt("parameterid");
			 opid=jsonIn.getInt("opid");
		} catch (JSONException e) {
			System.err.println("SavePOParameter: Error parsing ID-Field");
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
					"SELECT getProcessRights(vuserid:=?,vprocess:=(SELECT processid FROM samplesinprocess WHERE id=? ))");
			pStmt.setInt(1,userID);
			pStmt.setInt(2,opid);
			privilege=dBconn.getSingleStringValue(pStmt);
			pStmt.close();
		} catch (SQLException e) {
			System.err.println("SavePOParameter: Problems with SQL query");
			status="Problems with SQL query";
			e.printStackTrace();
		} catch (JSONException e){
			System.err.println("SavePOParameter: Problems creating JSON");
			status="Problems creating JSON";
		} catch (Exception e) {
			System.err.println("SavePOParameter: Strange Problems");
			status="Strange Problems";
		}	
		
		
		if (privilege.equals("w")){

			try{
				pStmt= dBconn.conn.prepareStatement( 			
						 "SELECT paramdef.datatype FROM po_parameters pop \n"
						+"JOIN paramdef ON pop.definition=paramdef.id \n"
						+"WHERE pop.id=?");
			   	pStmt.setInt(1, popid);
			   	JSONObject answer = dBconn.jsonObjectFromPreparedStmt(pStmt);
			   	pStmt.close();
				datatype= answer.getInt("datatype");			
				
				// delete old values.
				String[] tables = {"","po_integer_data","po_float_data",
								   "po_measurement_data","po_string_data",
								   "po_string_data","po_string_data",
								   "po_timestamp_data","po_integer_data",
								   "po_timestamp_data","po_string_data",
								   "po_string_data"};
				pStmt= dBconn.conn.prepareStatement( 			
						 "DELETE FROM "+tables[datatype]+" "
						+"WHERE po_parameter_id = ? AND opid = ?");
			   	pStmt.setInt(1, popid);
			   	pStmt.setInt(2, opid);
			   	pStmt.executeUpdate();
			   	pStmt.close();
			} catch (SQLException e) {
				System.err.println("SavePOParameter: Problems with SQL query");
				status="Problems with SQL query";
				e.printStackTrace();
			} catch (JSONException e){
				System.err.println("SavePOParameter: Problems creating JSON");
				status="Problems creating JSON";
			} catch (Exception e) {
				System.err.println("SavePOParameter: Strange Problems");
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
		        					"INSERT INTO po_integer_data (opid,po_parameter_id,value,lastUser) VALUES (?,?,?,?)");
		        			pStmt.setInt(1, opid);
		        			pStmt.setInt(2, popid);
				   		  	pStmt.setInt(3, jsonIn.getInt("value"));
				   		  	pStmt.setInt(4, userID);
		        			pStmt.executeUpdate();
		        			pStmt.close();
		        		}
				   		break;
				   		
		        case 2: if (jsonIn.has("value") && !jsonIn.isNull("value")){  
		        			pStmt= dBconn.conn.prepareStatement( 			// Double values
		        					"INSERT INTO po_float_data (opid,po_parameter_id,value,lastuser) VALUES (?,?,?,?)");
		        			pStmt.setInt(1, opid);
		        			pStmt.setInt(2, popid);
		   				  	pStmt.setDouble(3, jsonIn.getDouble("value"));
		   				  	pStmt.setInt(4, userID);
		        			pStmt.executeUpdate();
		        			pStmt.close();
		        		}
		   				break;
	        			
		        case 3: if (jsonIn.has("value") && !jsonIn.isNull("value")){  	
		        			pStmt= dBconn.conn.prepareStatement( 			// Measurement data
		        					"INSERT INTO po_measurement_data (opid,po_parameter_id,value,error,lastUser) VALUES (?,?,?,?,?)");
		        			pStmt.setInt(1, opid);
		        			pStmt.setInt(2, popid);
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
		        					"INSERT INTO po_string_data (opid,po_parameter_id,value,lastUser) VALUES (?,?,?,?)");
		        			pStmt.setInt(1, opid);
		        			pStmt.setInt(2, popid);
		        			pStmt.setString(3, jsonIn.getString("value"));
		        			pStmt.setInt(4, userID);
		        			pStmt.executeUpdate();
		        			pStmt.close();
		        		}
		        		break;
				        
		        case 5: if (jsonIn.has("value") && !jsonIn.isNull("value")){  
			        		pStmt= dBconn.conn.prepareStatement( 			
			        				"INSERT INTO po_string_data (opid,po_parameter_id,value,lastUser) VALUES (?,?,?,?)");
		        			pStmt.setInt(1, opid);
		        			pStmt.setInt(2, popid);
							pStmt.setString(3, jsonIn.getString("value"));
							pStmt.setInt(4, userID);
		        			pStmt.executeUpdate();
		        			pStmt.close();
		        		}
						break;
					    
		        case 6: if (jsonIn.has("value") && !jsonIn.isNull("value")){   //  6: chooser, (saves as a string)
		        			pStmt= dBconn.conn.prepareStatement( 			
		        					"INSERT INTO po_string_data (opid,po_parameter_id,value,lastUser) VALUES (?,?,?,?)");
		        			pStmt.setInt(1, opid);
		        			pStmt.setInt(2, popid);
		        			pStmt.setString(3, jsonIn.getString("value"));
		        			pStmt.setInt(4, userID);
		        			pStmt.executeUpdate();
		        			pStmt.close();
		        		}
						break;

		        case 7: if (jsonIn.has("date") && !jsonIn.isNull("date")){   //   7: date,
	     		   		  	pStmt= dBconn.conn.prepareStatement( 			
	     		   		  			"INSERT INTO po_timestamp_data (opid,po_parameter_id,value,tz,lastUser) VALUES (?,?,?,?,?)");
	     		   		  	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
	     		   		  	SimpleDateFormat sqldf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	     		   		  	java.sql.Timestamp ts = java.sql.Timestamp.valueOf(sqldf.format(sdf.parse(jsonIn.getString("date"))));	
		        			pStmt.setInt(1, opid);
		        			pStmt.setInt(2, popid);
	     		   		  	pStmt.setTimestamp(3, (Timestamp) ts);
	     		   		  	pStmt.setInt(4, jsonIn.getInt("tz")); //Timezone in Minutes
	     		   		  	pStmt.setInt(5, userID);
		        			pStmt.executeUpdate();
		        			pStmt.close();
		        		}
	     		   		break;
	     			    
		        case 8: if (jsonIn.has("value") && !jsonIn.isNull("value")){  //   8: checkbox,
				   		  	pStmt= dBconn.conn.prepareStatement( 			
				   		  			"INSERT INTO po_integer_data (opid,po_parameter_id,value,lastUser) VALUES (?,?,?,?)");
		        			pStmt.setInt(1, opid);
		        			pStmt.setInt(2, popid);
				   		  	pStmt.setString(3, jsonIn.getString("value"));
				   		  	pStmt.setInt(4, userID);
		        			pStmt.executeUpdate();
		        			pStmt.close();
						}
				   		break;
				        
		        case 9: if (jsonIn.has("date") && !jsonIn.isNull("date")){  //   9: timestamp,
					   	  	pStmt= dBconn.conn.prepareStatement( 			
					   	  			"INSERT INTO po_timestamp_data (opid,po_parameter_id,value,tz,lastUser) VALUES (?,?,?,?,?)");
					   		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
							SimpleDateFormat sqldf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
							java.sql.Timestamp ts = java.sql.Timestamp.valueOf(sqldf.format(sdf.parse(jsonIn.getString("date"))));		   
		        			pStmt.setInt(1, opid);
		        			pStmt.setInt(2, popid);
							pStmt.setTimestamp(3, (Timestamp) ts);
							pStmt.setInt(4, jsonIn.getInt("tz")); //Timezone in Minutes
							pStmt.setInt(5, userID);
		        			pStmt.executeUpdate();
		        			pStmt.close();
		        		}
					   	break;
				    
		        case 10: if (jsonIn.has("value") && !jsonIn.isNull("value")){
		        			pStmt= dBconn.conn.prepareStatement( 			// 10: URL
		        					"INSERT INTO po_string_data (objectid,po_parameter_id,value,lastUser) VALUES (?,?,?,?)");
		        			pStmt.setInt(1, opid);
		        			pStmt.setInt(2, popid);
		        			pStmt.setString(3, jsonIn.getString("value"));
		        			pStmt.setInt(4, userID);
		        			pStmt.executeUpdate();
		        			pStmt.close();
		        		}	
		        		break;
				        
		        case 11: if (jsonIn.has("value") && !jsonIn.isNull("value")){ // 11: email
		        			pStmt= dBconn.conn.prepareStatement( 			
		        					"INSERT INTO po_string_data (objectid,po_parameter_id,value,lastUser) VALUES (?,?,?,?)");
		        			pStmt.setInt(1, opid);
		        			pStmt.setInt(2, popid);
		        			pStmt.setString(3, jsonIn.getString("value"));
		        			pStmt.setInt(4, userID);
		        			pStmt.executeUpdate();
		        			pStmt.close();
		        		}
		        		break;
			} // end of switch Statement
			dBconn.closeDB();
		} catch (SQLException e) {
			System.err.println("SavePOParameter: More Problems with SQL query");
			e.printStackTrace();
		} catch (JSONException e){
			System.err.println("SavePOParameter: More Problems creating JSON");
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("SavePOParameter: More Strange Problems");
		}
		}else{
			response.setStatus(401);
			status="insufficient rights";
		}
		
    // tell client that everything is fine
	Unidatoolkit.sendStandardAnswer(status, response);
	}
}	