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

	public class WriteParameter extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@SuppressWarnings("resource")
	@Override
	  public void doPost(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {	
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
		userID=userID+1;
		userID=userID-1;
		String status="ok";
	    request.setCharacterEncoding("utf-8");
	    String in = request.getReader().readLine();
	    JSONObject  jsonIn = null;	    
	    try {
			  jsonIn = new JSONObject(in);
		} catch (JSONException e) {
			System.err.println("SaveSampleParameter: Input is not valid JSON");
		}

	    
	    // get the id
	    int id=0;
	    int pid=-1;
	    try {
			 id=jsonIn.getInt("id");	
     		pid=jsonIn.getInt("pid");
		} catch (JSONException e) {
			System.err.println("SaveSampleParameter: Error parsing ID-Field");
			response.setStatus(404);
			status="Error parsing ID-Field";
		}

	    
	    // look up the datatype in Database	    
	 	DBconnection DBconn=new DBconnection();
	    PreparedStatement pStmt = null;
	    int type=-1;
		try {	
		    DBconn.startDB();	   
			pStmt= DBconn.conn.prepareStatement( 			
					 "SELECT paramdef.datatype FROM Ot_parameters otp \n"
					+"JOIN paramdef ON otp.definition=paramdef.id \n"
					+"WHERE otp.id=? \n");
		   	pStmt.setInt(1, id);
		   	JSONObject answer=DBconn.jsonObjectFromPreparedStmt(pStmt);
			type= answer.getInt("datatype");
		} catch (SQLException e) {
			System.err.println("SaveSampleParameter: Problems with SQL query");
			status="Problems with SQL query";
		} catch (JSONException e){
			System.err.println("SaveSampleParameter: Problems creating JSON");
			status="Problems creating JSON";
		} catch (Exception e) {
			System.err.println("SaveSampleParameter: Strange Problems");
			status="Strange Problems";
		}
		
		// differentiate according to type
		// Datatype        INTEGER NOT NULL,  
		// 1: integer, 2: float, 3: measurement, 4: string, 5: long string 
		// 6: chooser, 7: timestamp, 8: checkbox, 9: URL
		try {	

			switch (type) {
	        case 1: {   pStmt= DBconn.conn.prepareStatement( 			// Integer values
			   					 "UPDATE o_integer_data SET (value,lastUser)=(?,?) WHERE id=? \n");
			   			pStmt.setInt(1, jsonIn.getInt("value"));
			   			pStmt.setInt(2, userID);
			   			pStmt.setInt(3, pid);
			   			break;
			        }
	        case 2: {   pStmt= DBconn.conn.prepareStatement( 			// Double values
	   					 		"UPDATE o_float_data SET (value,lastuser)=(?,?) WHERE id=? \n");
	   					pStmt.setDouble(1, jsonIn.getDouble("value"));
	   					pStmt.setInt(2, userID);
	   					pStmt.setInt(3, pid);
	   					break;
        			}
	        case 3: {   pStmt= DBconn.conn.prepareStatement( 			// Measurement data
						 		"UPDATE o_measurement_data SET (value,error,lastUser)=(?,?,?) WHERE id=? \n");
				        if (jsonIn.getString("value").contains("±")){
							pStmt.setDouble(1, Double.parseDouble(jsonIn.getString("value").split("±")[0]));
							pStmt.setDouble(2, Double.parseDouble(jsonIn.getString("value").split("±")[1]));
						} else {
							pStmt.setDouble(1, jsonIn.getDouble("value"));
							pStmt.setDouble(2, 0);
						}
						pStmt.setInt(3,userID);
						pStmt.setInt(4, pid);
						break;
			        }
	        case 4: { pStmt= DBconn.conn.prepareStatement( 			// String data	
				 		"UPDATE o_string_data SET (value,lastUser)=(?,?) WHERE id=? \n");
					   pStmt.setString(1, jsonIn.getString("value"));
					   pStmt.setInt(2, userID);
					   pStmt.setInt(3, pid);
					   break;
			        }
	        case 5: {  pStmt= DBconn.conn.prepareStatement( 			
				 	   			"UPDATE o_string_data SET (value,lastUser)=(?,?) WHERE id=? \n");
					   pStmt.setString(1, jsonIn.getString("value"));
					   pStmt.setInt(2, userID);
					   pStmt.setInt(3, pid);
					   break;
				    }
	        case 6: {  //  6: chooser, (saves as a string)
	        		   pStmt= DBconn.conn.prepareStatement( 			
	 	   				"UPDATE o_string_data SET (value,lastUser)=(?,?) WHERE id=? \n");
					   pStmt.setString(1, jsonIn.getString("value"));
					   pStmt.setInt(2, userID);
					   pStmt.setInt(3, pid);
					   break;
	        		}
	        case 7: {  //   7: timestamp,
     		   		   pStmt= DBconn.conn.prepareStatement( 			
     		   				"UPDATE o_timestamp_data SET (value,tz,lastUser)=(?,?,?) WHERE id=?");
     		   		   SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
					   SimpleDateFormat sqldf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
					   java.sql.Timestamp ts = java.sql.Timestamp.valueOf(sqldf.format(sdf.parse(jsonIn.getString("date"))));		   
					   pStmt.setTimestamp(1, (Timestamp) ts);
					   pStmt.setInt(2, jsonIn.getInt("tz")); //Timezone in Minutes
					   pStmt.setInt(3, userID);     		   			
					   pStmt.setInt(4, pid);
     		   		   break;
     			    }
	        case 8: {  //   8: checkbox,
			   		   pStmt= DBconn.conn.prepareStatement( 			
			   					 "UPDATE o_integer_data SET value=? WHERE id=? \n");
			   		   pStmt.setString(1, jsonIn.getString("value"));
			   		   pStmt.setInt(2, pid);
			   		   break;
			        }
	        case 9: { pStmt= DBconn.conn.prepareStatement( 			// URL
				 		"UPDATE o_string_data SET (value,lastUser)=(?,?) WHERE id=? \n");
					  pStmt.setString(1, jsonIn.getString("value"));
					  pStmt.setInt(2, userID);
					  pStmt.setInt(3, pid);
					  break;
			        }
			}
		pStmt.executeUpdate();
		System.out.println(pStmt.toString());
		pStmt.close();
		DBconn.closeDB();
	} catch (SQLException e) {
		System.err.println("SaveSampleParameter: More Problems with SQL query");
		System.out.println("query: "+pStmt.toString());
	} catch (JSONException e){
		System.err.println("SaveSampleParameter: More Problems creating JSON");
	} catch (Exception e) {
		System.err.println("SaveSampleParameter: More Strange Problems");
	}
		
    // tell client that everything is fine
	Unidatoolkit.sendStandardAnswer(status, response);
	}
}	