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
	    int pid = 0;
	    int sampleID = 0;
	    try {
			 pid = jsonIn.getInt("parameterid");
			 sampleID = jsonIn.getInt("sampleid");
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
				pStmt= dBconn.conn.prepareStatement( 			
						 "DELETE FROM sampledata "
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
				
				JSONObject data = new JSONObject();
				if (jsonIn.has("data")){
					JSONObject inData = jsonIn.getJSONObject("data");
					switch (datatype) {
			        case 1:	if (inData.has("value") && !inData.isNull("value")){  // Integer values
			        			data.put("value", inData.getInt("value"));
			        		}
					   		break;
					   		
			        case 2: if (inData.has("value") && !inData.isNull("value")){  // Floating point data
	        					data.put("value", inData.getDouble("value"));
			        		}
			   				break;
		        			
			        case 3: if (inData.has("value") && !inData.isNull("value")){  	// Measurement data
    							data.put("value", inData.getDouble("value"));
			        			if (inData.has("error")){
		        					data.put("error", inData.getDouble("error"));
			        			}
			        		}
							break;
					        
			        case 4: if (inData.has("value") && !inData.isNull("value")){  // String data
			        			data.put("value", inData.getString("value"));
			        		}
			        		break;
					        
			        case 5: if (inData.has("value") && !inData.isNull("value")){    // String data again
	        					data.put("value", inData.getString("value"));
	        				}
							break;
						    
			        case 6: if (inData.has("value") && !inData.isNull("value")){   //  6: chooser, (saves as a string)
	        					data.put("value", inData.getString("value"));
	        				}
							break;
							
			        case 7: if (inData.has("date") && !inData.isNull("date")){   //   7: date,
		     		   		  	
//		     		   		  	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
//		     		   		  	SimpleDateFormat sqldf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//		     		   		  	java.sql.Timestamp ts = java.sql.Timestamp.valueOf(sqldf.format(sdf.parse(jsonIn.getString("date"))));	
			        			data.put("tz",  inData.getInt("tz"));
		     		   		  	data.put("date", inData.getString("date"));
			        		}
		     		   		break;
		     			    
			        case 8: if (inData.has("value") && !inData.isNull("value")){  //   8: checkbox,
			        			data.put("value", inData.getBoolean("value"));
							}
					   		break;
					        
			        case 9: if (inData.has("date") && !inData.isNull("date")){  //   9: timestamp,
//						   		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
//								SimpleDateFormat sqldf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//								java.sql.Timestamp ts = java.sql.Timestamp.valueOf(sqldf.format(sdf.parse(jsonIn.getString("date"))));		   
								data.put("tz",  inData.getInt("tz"));
		     		   		  	data.put("date", inData.getString("date"));
			        		}
						   	break;
					    
			        case 10: if (inData.has("value") && !inData.isNull("value")){ // URL
					        	data.put("value", inData.getString("value"));
			        		}
			        		break;
					        
			        case 11: if (inData.has("value") && !inData.isNull("value")){ // e-mail
			        			data.put("value", inData.getString("value"));
			        		}
		        		break;
					} // end of switch Statement
					
					pStmt= dBconn.conn.prepareStatement( 			// Integer values
							"INSERT INTO sampledata (objectid,ot_parameter_id,data,lastUser) VALUES (?,?,?,?)");
					pStmt.setInt(1, sampleID);
					pStmt.setInt(2, pid);
		   		  	pStmt.setObject(3, data, java.sql.Types.OTHER);
		   		  	pStmt.setInt(4, userID);
					pStmt.executeUpdate();
					pStmt.close();
					dBconn.closeDB();
					
				} // end of "if (json.has("data"))"

    		
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