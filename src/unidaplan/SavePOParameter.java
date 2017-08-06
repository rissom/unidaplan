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

	public class SavePOParameter extends HttpServlet {
		private static final long serialVersionUID = 1L;


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
	    int sip = 0;
	    try {
			 popid=jsonIn.getInt("parameterid");
			 sip=jsonIn.getInt("opid");
		} catch (JSONException e) {
			System.err.println("SavePOParameter: Error parsing ID-Field");
			response.setStatus(404);
			status="Error parsing ID-Field";
		}

	    
	 	DBconnection dBconn=new DBconnection();
	    PreparedStatement pStmt = null;
	    int datatype=-1;
	    
	    
		try {	
			// check privileges
		    dBconn.startDB();
		    pStmt= dBconn.conn.prepareStatement( 	
					"SELECT getProcessRights(vuserid:=?,vprocess:=(SELECT processid FROM samplesinprocess WHERE id=? ))");
			pStmt.setInt(1,userID);
			pStmt.setInt(2,sip);
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
				
				
			    // look up the datatype in Database	    
				pStmt= dBconn.conn.prepareStatement( 			
						 "SELECT paramdef.datatype FROM po_parameters pop \n"
						+"JOIN paramdef ON pop.definition=paramdef.id \n"
						+"WHERE pop.id = ?");
			   	pStmt.setInt(1, popid);
			   	JSONObject answer = dBconn.jsonObjectFromPreparedStmt(pStmt);
			   	pStmt.close();
				datatype= answer.getInt("datatype");			
				
				
				// delete old values.
				pStmt= dBconn.conn.prepareStatement( 			
						 "DELETE FROM spdata "
						+"WHERE parameterid = ? AND sip = ?");
			   	pStmt.setInt(1, popid);
			   	pStmt.setInt(2, sip);
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
			try {	
				JSONObject data = new JSONObject();
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
	     		   		  	
//	     		   		  	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
//	     		   		  	SimpleDateFormat sqldf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//	     		   		  	java.sql.Timestamp ts = java.sql.Timestamp.valueOf(sqldf.format(sdf.parse(jsonIn.getString("date"))));	
		        			data.put("tz",  inData.getInt("tz"));
	     		   		  	data.put("date", inData.getString("date"));
		        		}
	     		   		break;
	     			    
		        case 8: if (inData.has("value") && !inData.isNull("value")){  //   8: checkbox,
		        			data.put("value", inData.getBoolean("value"));
						}
				   		break;
				        
		        case 9: if (inData.has("date") && !inData.isNull("date")){  //   9: timestamp,
//					   		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
//							SimpleDateFormat sqldf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//							java.sql.Timestamp ts = java.sql.Timestamp.valueOf(sqldf.format(sdf.parse(jsonIn.getString("date"))));		   
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
			pStmt = dBconn.conn.prepareStatement( 			// Integer values
  					"INSERT INTO spdata (sip,parameterid,data,lastUser) VALUES (?,?,?,?)");
  			pStmt.setInt(1, sip);
  			pStmt.setInt(2, popid);
   		  	pStmt.setObject(3, data, java.sql.Types.OTHER);
	   		pStmt.setInt(4, userID);
  			pStmt.executeUpdate();
  			pStmt.close();
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