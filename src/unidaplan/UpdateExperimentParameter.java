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

public class UpdateExperimentParameter extends HttpServlet {
	private static final long serialVersionUID = 1L;

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
	    int parameterID = -1;
	    
	    try {
			 jsonIn = new JSONObject(in);
		} catch (JSONException e) {
			System.err.println("UpdateExperimentParameter: Input is not valid JSON");
		}
		response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    
	    // get the id

	    try {
			 experimentID = jsonIn.getInt("experimentid");	
     		 parameterID = jsonIn.getInt("id");
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
						 "DELETE FROM experimentdata WHERE experimentid=? AND parameterid=?");
			   	pStmt.setInt(1, experimentID);
			   	pStmt.setInt(2, parameterID);
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
			   	pStmt.setInt(1, parameterID);
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
				JSONObject data = new JSONObject();
				if (jsonIn.has("data")){
					JSONObject inData = jsonIn.getJSONObject("data");
					switch (dataType) {
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
			        case 12: if (inData.has("id") && !inData.isNull("id")){ // sample
	        					data.put("id", inData.getInt("id"));
			        		}
	        				break;
					} // end of switch Statement
					pStmt = dBconn.conn.prepareStatement(	
							"INSERT INTO experimentdata (experimentID, parameterID, data, lastUser) "
							+ "VALUES (?, ?, ?, ?)"); 
        			pStmt.setInt(1, experimentID);
        			pStmt.setInt(2, parameterID);
		   		  	pStmt.setObject(3, data, java.sql.Types.OTHER);
        			pStmt.setInt(4, userID);
        			pStmt.executeUpdate();
		   	   		pStmt.close();
		   	   		dBconn.closeDB();
				}
				
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