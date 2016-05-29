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
		String status = "ok";
	   	String privilege = "n";
		int userID = authentificator.GetUserID(request,response);
	    request.setCharacterEncoding("utf-8");
	    String in = request.getReader().readLine();
	    JSONObject jsonIn = null;
	    int experimentID = 0;
	    
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
		    
		    // Check privileges
		    pStmt = dBconn.conn.prepareStatement( 	
					"SELECT getExperimentRights(vuserid:=?,vexperimentid:=?)");
			pStmt.setInt(1,userID);
			pStmt.setInt(2,experimentID);
			privilege = dBconn.getSingleStringValue(pStmt);
			pStmt.close();
	    } catch (SQLException e) {
			System.err.println("UpdateExperimentParameter: Problems with SQL query");
			status = "SQL Error";
			e.printStackTrace();
			status = "SQL Error";
		} catch (Exception e) {
			System.err.println("UpdateExperimentParameter: Strange Problems");
			status = "Misc Error (line67)";
		}
	    
			
		if (privilege.equals("w")){
				
			try{
				pStmt= dBconn.conn.prepareStatement( 			
						 "DELETE FROM Expp_integer_data WHERE expp_id=? AND expp_param=?");
			   	pStmt.setInt(1, expID);
			   	pStmt.setInt(2, expParamID);
			   	pStmt.executeUpdate();
				pStmt= dBconn.conn.prepareStatement( 			
						 "DELETE FROM Expp_float_data WHERE expp_id=? AND expp_param=?");
			   	pStmt.setInt(1, expID);
			   	pStmt.setInt(2, expParamID);
			   	pStmt.executeUpdate();
				pStmt= dBconn.conn.prepareStatement( 			
						 "DELETE FROM Expp_string_data WHERE expp_id=? AND expp_param=?");
			   	pStmt.setInt(1, expID);
			   	pStmt.setInt(2, expParamID);
			   	pStmt.executeUpdate();
				pStmt= dBconn.conn.prepareStatement( 			
						 "DELETE FROM Expp_measurement_data WHERE expp_id=? AND expp_param=?");
			   	pStmt.setInt(1, expID);
			   	pStmt.setInt(2, expParamID);
			   	pStmt.executeUpdate();
				pStmt= dBconn.conn.prepareStatement( 			
						 "DELETE FROM Expp_timestamp_data WHERE expp_id=? AND expp_param=?");
			   	pStmt.setInt(1, expID);
			   	pStmt.setInt(2, expParamID);
			   	pStmt.executeUpdate();
			} catch (SQLException e) {
				System.err.println("UpdateExperimentParameter: Problems with SQL query");
				status = "SQL Error";
				e.printStackTrace();
				status = "SQL Error";
			} catch (Exception e) {
				System.err.println("UpdateExperimentParameter: Strange Problems");
				status = "Misc Error (line67)";
			}
	    
	    
		    // look up the datatype in Database	    
		    int dataType=-1;
			try {	
	
				pStmt= dBconn.conn.prepareStatement( 			
						 "SELECT paramdef.datatype FROM Expp_param ep "
						+"JOIN paramdef ON ep.definition=paramdef.id "
						+"WHERE ep.id=?");
			   	pStmt.setInt(1, expParamID);
				dataType= dBconn.getSingleIntValue(pStmt);
			} catch (SQLException e) {
				System.err.println("UpdateExperimentParameter: Problems with SQL query");
				e.printStackTrace();
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
		        case 1: if (jsonIn.has("value") && !jsonIn.isNull("value")){  
			        		pStmt= dBconn.conn.prepareStatement( 			// Integer values
					   				 "INSERT INTO expp_integer_data (expp_id,expp_param,value,lastUser) VALUES (?,?,?,?)");
							pStmt.setInt(1, expID); //experiment ID
		   					pStmt.setInt(2, expParamID); // Parameter ID
					   		pStmt.setInt(3, jsonIn.getInt("value")); // Value
			   				pStmt.setInt(4, userID); // UserID
			   				pStmt.executeUpdate();
			   				pStmt.close();
		        		}
				   		break;
				        
		        case 2: if (jsonIn.has("value") && !jsonIn.isNull("value")){  
		        			pStmt= dBconn.conn.prepareStatement( 			// Double values
		   				 			"INSERT INTO expp_float_data (expp_id,expp_param,value,lastUser) VALUES (?,?,?,?)");
							pStmt.setInt(1, expID); //experiment ID
		   					pStmt.setInt(2, expParamID); // Parameter ID
			   				pStmt.setDouble(3, jsonIn.getDouble("value"));
			   				pStmt.setInt(4, userID); // UserID
			   				pStmt.executeUpdate();
			   				pStmt.close();
		        		}
		   				break;
		        
		        case 3:	if (jsonIn.has("value") && !jsonIn.isNull("value")){  	
	    					pStmt= dBconn.conn.prepareStatement( 			// Measurement data
	    							"INSERT INTO expp_measurement_data (expp_id,expp_param,value,error,lastUser) VALUES (?,?,?,?,?)");
	    					pStmt.setInt(1, expID);
	    					pStmt.setInt(2, expParamID);
	    					pStmt.setDouble(3, jsonIn.getDouble("value"));
			    			if (jsonIn.has("error")&&!jsonIn.isNull("error")){
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
						 			"INSERT INTO expp_string_data (expp_id,expp_param,value,lastUser) VALUES (?,?,?,?)");
							pStmt.setInt(1, expID); //experiment ID
						    pStmt.setInt(2, expParamID); // Parameter ID
							pStmt.setString(3, jsonIn.getString("value"));
			   				pStmt.setInt(4, userID);
			   				pStmt.executeUpdate();
			   				pStmt.close();
		        		}
						break;
				        
		        case 5: if (jsonIn.has("value") && !jsonIn.isNull("value")){  
			        		pStmt= dBconn.conn.prepareStatement( 			// String data	
				 					"INSERT INTO expp_string_data (expp_id,expp_param,value,lastUser) VALUES (?,?,?,?)");
							pStmt.setInt(1, expID); //experiment ID
						    pStmt.setInt(2, expParamID); // Parameter ID
							pStmt.setString(3, jsonIn.getString("value"));
							pStmt.setInt(4, userID);
							pStmt.executeUpdate();
							pStmt.close();
		        		}
						break;
						
		        case 6: if (jsonIn.has("value") && !jsonIn.isNull("value")){  
			        		pStmt= dBconn.conn.prepareStatement( 			// String data	
						 			"INSERT INTO expp_string_data (expp_id,expp_param,value,lastUser) VALUES (?,?,?,?)");
							pStmt.setInt(1, expID); //experiment ID
						    pStmt.setInt(2, expParamID); // Parameter ID
							pStmt.setString(3, jsonIn.getString("value"));
			   				pStmt.setInt(4, userID);
			   				pStmt.executeUpdate();
			   				pStmt.close();
			    		}
						break;
		        
		        case 7: if (jsonIn.has("date") && !jsonIn.isNull("date")){  //   7: date,
			        		pStmt= dBconn.conn.prepareStatement( 			
			        				"INSERT INTO expp_timestamp_data (expp_id,expp_param,value,tz,lastUser) VALUES (?,?,?,?,?)");
							pStmt.setInt(1, expID); //experiment ID
						    pStmt.setInt(2, expParamID); // Parameter ID
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
							SimpleDateFormat sqldf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
							java.sql.Timestamp ts = java.sql.Timestamp.valueOf(sqldf.format(sdf.parse(jsonIn.getString("date"))));
							pStmt.setTimestamp(3, (Timestamp) ts);
							pStmt.setInt(4, jsonIn.getInt("tz")); //Timezone in Minutes
							pStmt.setInt(5, userID);
							pStmt.executeUpdate();
							pStmt.close();
		        		}
						break;
						
		        case 8: if (jsonIn.has("value") && !jsonIn.isNull("value")){  //   8: checkbox,
		   		  	pStmt= dBconn.conn.prepareStatement( 			
		   		  			"INSERT INTO expp_integer_data (expp_id,expp_param,value,lastUser) VALUES (?,?,?,?)");
	    			pStmt.setInt(1, expID);
	    			pStmt.setInt(2, expParamID);
		   		  	pStmt.setInt(3, jsonIn.getBoolean("value")?1:0);
		   		  	pStmt.setInt(4, userID);
	    			pStmt.executeUpdate();
	    			pStmt.close();
				}
		   		break;
		        
		        case 9: if (jsonIn.has("date") && !jsonIn.isNull("date")){  //   9: timestamp,
			   	  	pStmt= dBconn.conn.prepareStatement( 			
			   	  			"INSERT INTO expp_timestamp_data (objectid,ot_parameter_id,value,tz,lastUser) VALUES (?,?,?,?,?)");
			   		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
					SimpleDateFormat sqldf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
					java.sql.Timestamp ts = java.sql.Timestamp.valueOf(sqldf.format(sdf.parse(jsonIn.getString("date"))));		   
	    			pStmt.setInt(1, expID);
	    			pStmt.setInt(2, expParamID);
					pStmt.setTimestamp(3, (Timestamp) ts);
					pStmt.setInt(4, jsonIn.getInt("tz")); //Timezone in Minutes
					pStmt.setInt(5, userID);
	    			pStmt.executeUpdate();
	    			pStmt.close();
	    		}
			   	break;
		    
		        case 10: if (jsonIn.has("value") && !jsonIn.isNull("value")){
	    			pStmt= dBconn.conn.prepareStatement( 			// 10: URL
	    					"INSERT INTO expp_string_data (objectid,ot_parameter_id,value,lastUser) VALUES (?,?,?,?)");
	    			pStmt.setInt(1, expID);
	    			pStmt.setInt(2, expParamID);
	    			pStmt.setString(3, jsonIn.getString("value"));
	    			pStmt.setInt(4, userID);
	    			pStmt.executeUpdate();
	    			pStmt.close();
	    		}	
	    		break;
		        
		        case 11: if (jsonIn.has("value") && !jsonIn.isNull("value")){ // 11: email
	    			pStmt= dBconn.conn.prepareStatement( 			
	    					"INSERT INTO expp_string_data (objectid,ot_parameter_id,value,lastUser) VALUES (?,?,?,?)");
	    			pStmt.setInt(1, expID);
	    			pStmt.setInt(2, expParamID);
	    			pStmt.setString(3, jsonIn.getString("value"));
	    			pStmt.setInt(4, userID);
	    			pStmt.executeUpdate();
	    			pStmt.close();
	    		}
	    		break;					
				}
				dBconn.closeDB();
				
			} catch (SQLException e) {
				System.err.println("UpdateExperimentParameter: More Problems with SQL query");
				e.printStackTrace();
				status = "SQL Error";
			} catch (JSONException e){
				System.err.println("UpdateExperimentParameter: More Problems creating JSON");
				e.printStackTrace();
				System.err.println(pStmt.toString());
				status = "JSON Error";
			} catch (Exception e) {
				System.err.println("UpdateExperimentParameter: More Strange Problems");
				e.printStackTrace();
				status = "Misc. Error";
			}
		} else {
			response.setStatus(401);
		}
				
	    // tell client that everything is fine
	    Unidatoolkit.sendStandardAnswer(status, response);
	}
}	