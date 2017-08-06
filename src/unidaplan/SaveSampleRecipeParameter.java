package unidaplan;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

	public class SaveSampleRecipeParameter extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doPost(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {
		
		Authentificator authentificator = new Authentificator();
		int userID = authentificator.GetUserID(request,response);
	    request.setCharacterEncoding("utf-8");
	    String privilege = "n";
	    String status = "ok";
	    String in = request.getReader().readLine();
	    JSONObject jsonIn = null;	    
	    try {
			  jsonIn = new JSONObject(in);
		} catch (JSONException e) {
			System.err.println("SaveSampleRecipeParameter: Input is not valid JSON");
			status="error";
		}
		response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    PreparedStatement pStmt;

	    // get the id
	    int sampleRecipeID = 0;
	    int parameterID = -1;
	    
	    try {
			sampleRecipeID = jsonIn.getInt("samplerecipeid");	
     		parameterID = jsonIn.getInt("parameterid");
		} catch (JSONException e) {
			System.err.println("SaveSampleRecipeParameter: Error parsing ID-Field");
			status="error parsing ID-Field";
			response.setStatus(404);
		}

	 	DBconnection dBconn=new DBconnection();
	 	
	 	try {	
		    dBconn.startDB();	   
		    
	        pStmt = dBconn.conn.prepareStatement( 	
					"SELECT getsampleRecipeRights(vuserid := ?, vsamplerecipe := ?)");
			pStmt.setInt(1,userID);
			pStmt.setInt(2,sampleRecipeID);
			privilege = dBconn.getSingleStringValue(pStmt);
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
	    	
	    	
	    	  // delete old entries
			try {	
				pStmt = dBconn.conn.prepareStatement(
					    "DELETE FROM samplerecipedata "
					  + "WHERE recipe = ? AND parameter = ?");
			   	pStmt.setInt(1, sampleRecipeID);
			   	pStmt.setInt(2, parameterID);
			   	pStmt.executeUpdate();
			} catch (SQLException e) {
				System.err.println("SaveSampleRecipeParameter: Problems with SQL query");
				status="error";
			} catch (Exception e) {
				System.err.println("SaveSampleRecipeParameter: Strange Problems");
				status="error";
			}
	    	
 
		    // look up the datatype in Database	    
		    int type = -1;
			try {	
				pStmt = dBconn.conn.prepareStatement(
						 "SELECT paramdef.datatype "
					   + "FROM ot_parameters p "
					   + "JOIN paramdef ON p.definition = paramdef.id "
					   + "WHERE p.id = ?");
			   	pStmt.setInt(1, parameterID);
			   	JSONObject answer = dBconn.jsonObjectFromPreparedStmt(pStmt);
				type = answer.getInt("datatype");
			} catch (SQLException e) {
				System.err.println("SaveSampleRecipeParameter: Problems with SQL query");
				status="error";
			} catch (JSONException e){
				System.err.println("SaveSampleRecipeParameter: Problems creating JSON");
				status="error";
			} catch (Exception e) {
				System.err.println("SaveSampleRecipeParameter: Strange Problems");
				status="error";
			}
			
			pStmt = null; // fooling eclipse to not show warnings
			
			int id=0; // id of the newly created value
			// differentiate according to type
			try {	
				JSONObject data = new JSONObject();
				if (jsonIn.has("data")){
					JSONObject inData = jsonIn.getJSONObject("data");
					switch (type) {
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
					        
			        case 4: if (inData.has("value") && !inData.isNull("value") && !inData.getString("value").equals("")){  // String data
			        			data.put("value", inData.getString("value"));
			        		}
			        		break;
					        
			        case 5: if (inData.has("value") && !inData.isNull("value") && !inData.getString("value").equals("")){    // String data again
	        					data.put("value", inData.getString("value"));
	        				}
							break;
						    
			        case 6: if (inData.has("value") && !inData.isNull("value") && inData.getString("value")!=""){   //  6: chooser, (saves as a string)
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
					
					
					if (data.length()>0){
						pStmt = dBconn.conn.prepareStatement(	
								"INSERT INTO samplerecipedata (recipe, parameter, data, lastUser) "
								+ "VALUES (?, ?, ?, ?)  RETURNING ID"); 
	        			pStmt.setInt(1, sampleRecipeID);
	        			pStmt.setInt(2, parameterID);
			   		  	pStmt.setObject(3, data, java.sql.Types.OTHER);
	        			pStmt.setInt(4, userID);
	        			id = dBconn.getSingleIntValue(pStmt);
			   	   		pStmt.close();
					}
				}
			
			    // tell client that everything is fine
			    PrintWriter out = response.getWriter();
			    JSONObject myResponse= new JSONObject();
			    myResponse.put("status", status);
			    myResponse.put("id", id);
				out.println(myResponse.toString());
			} catch (SQLException e) {
				System.err.println("SaveSampleRecipeParameter: More Problems with SQL query");
				status = "error";
				e.printStackTrace();
			} catch (JSONException e){
				System.err.println("SaveSampleRecipeParameter: More Problems creating JSON");
				e.printStackTrace();
				System.err.println(pStmt.toString());
				status="error";
			} catch (Exception e) {
				System.err.println("SaveSampleRecipeParameter: More Strange Problems");
				System.err.println(pStmt.toString());
				e.printStackTrace();
				status="error";
			}
	    } else {
	    	response.setStatus(401);
	    }
		dBconn.closeDB();
	}
}	