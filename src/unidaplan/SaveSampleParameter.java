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
		} catch (JSONException e) {
			System.err.println("SaveSampleParameter: Error parsing ID-Field");
			response.setStatus(404);
			status="Error parsing ID-Field";
		}

	    
	 	DBconnection DBconn=new DBconnection();
	    PreparedStatement pStmt = null;
	    int datatype=-1;
		try {	
		    // look up the datatype in Database	    
		    DBconn.startDB();	   
			pStmt= DBconn.conn.prepareStatement( 			
					 "SELECT paramdef.datatype FROM Ot_parameters otp \n"
					+"JOIN paramdef ON otp.definition=paramdef.id \n"
					+"WHERE otp.id=?");
		   	pStmt.setInt(1, pid);
		   	JSONObject answer=DBconn.jsonObjectFromPreparedStmt(pStmt);
		   	pStmt.close();
			datatype= answer.getInt("datatype");			
			
			// delete old values.
			String[] tables={"","o_integer_data","o_float_data","o_measurement_data","o_string_data","o_string_data","o_string_data","o_timestamp_data","o_integer_data","o_timestamp_data","o_string_data"};
			pStmt= DBconn.conn.prepareStatement( 			
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
		}
		
		// differentiate according to type
		// Datatype        INTEGER NOT NULL,  
		// 1: integer, 2: float, 3: measurement, 4: string, 5: long string 
		// 6: chooser, 7: date+time, 8: checkbox 9:timestring 10: URL
		try {	

		  switch (datatype) {
	        case 1: { pStmt= DBconn.conn.prepareStatement( 			// Integer values
			   			"INSERT INTO o_integer_data (objectid,ot_parameter_id,value,lastchange,lastUser) VALUES (?,?,?,NOW(),?)");
			   		  pStmt.setInt(3, jsonIn.getInt("value"));
			   		  pStmt.setInt(4, userID);
			   		  break;
			        }
	        case 2: { pStmt= DBconn.conn.prepareStatement( 			// Double values
	   					"INSERT INTO o_float_data (objectid,ot_parameter_id,value,lastchange,lastuser) VALUES (?,?,?,NOW(),?)");
	   				  pStmt.setDouble(3, jsonIn.getDouble("value"));
	   				  pStmt.setInt(4, userID);
	   				  break;
        			}
	        case 3: { pStmt= DBconn.conn.prepareStatement( 			// Measurement data
						"INSERT INTO o_measurement_data (objectid,ot_parameter_id,value,error,lastchange,lastUser) VALUES (?,?,?,?,NOW(),?)");
				      if (jsonIn.getString("value").contains("±")){
							pStmt.setDouble(3, Double.parseDouble(jsonIn.getString("value").split("±")[0]));
							pStmt.setDouble(4, Double.parseDouble(jsonIn.getString("value").split("±")[1]));
					  } else {
							pStmt.setDouble(3, jsonIn.getDouble("value"));
							pStmt.setDouble(4, 0);
					  }
					  pStmt.setInt(5,userID);
					  break;
			        }
	        case 4: { pStmt= DBconn.conn.prepareStatement( 			// String data	
				 		"INSERT INTO o_string_data (objectid,ot_parameter_id,value,lastchange,lastUser) VALUES (?,?,?,NOW(),?)");
					  pStmt.setString(3, jsonIn.getString("value"));
					  pStmt.setInt(4, userID);
					  break;
			        }
	        case 5: { pStmt= DBconn.conn.prepareStatement( 			
				 	  	"INSERT INTO o_string_data (objectid,ot_parameter_id,value,lastchange,lastUser) VALUES (?,?,?,NOW(),?)");
					  pStmt.setString(3, jsonIn.getString("value"));
					  pStmt.setInt(4, userID);
					  break;
				    }
	        case 6: { //  6: chooser, (saves as a string)
	        		  pStmt= DBconn.conn.prepareStatement( 			
	 	   				"INSERT INTO o_string_data (objectid,ot_parameter_id,value,lastchange,lastUser) VALUES (?,?,?,NOW(),?)");
					  pStmt.setString(3, jsonIn.getString("value"));
					  pStmt.setInt(4, userID);
					  break;
	        		}
	        case 7: {  //   7: date,
     		   		  pStmt= DBconn.conn.prepareStatement( 			
     		   			"INSERT INTO o_timestamp_data (objectid,ot_parameter_id,value,tz,lastchange,lastUser) VALUES (?,?,?,?,NOW(),?)");
     		   		  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
					  SimpleDateFormat sqldf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
					  java.sql.Timestamp ts = java.sql.Timestamp.valueOf(sqldf.format(sdf.parse(jsonIn.getString("date"))));		   
					  pStmt.setTimestamp(3, (Timestamp) ts);
					  pStmt.setInt(4, jsonIn.getInt("tz")); //Timezone in Minutes
					  pStmt.setInt(5, userID);     		   			
     		   		  break;
     			    }
	        case 8: { //   8: checkbox,
			   		  pStmt= DBconn.conn.prepareStatement( 			
			   			"INSERT INTO o_integer_data (objectid,ot_parameter_id,value,lastchange,lastUser) VALUES (?,?,?,NOW(),?)");
			   		  pStmt.setString(3, jsonIn.getString("value"));
					  pStmt.setInt(4, userID);
			   		  break;
			        }
	        case 9: {  //   9: timestamp,
		   		  pStmt= DBconn.conn.prepareStatement( 			
		   			"INSERT INTO o_timestamp_data (objectid,ot_parameter_id,value,tz,lastchange,lastUser) VALUES (?,?,?,?,NOW(),?)");
		   		  SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
				  SimpleDateFormat sqldf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
				  java.sql.Timestamp ts = java.sql.Timestamp.valueOf(sqldf.format(sdf.parse(jsonIn.getString("date"))));		   
				  pStmt.setTimestamp(3, (Timestamp) ts);
				  pStmt.setInt(4, jsonIn.getInt("tz")); //Timezone in Minutes
				  pStmt.setInt(5, userID);     		   			
		   		  break;
			    }
	        case 10: { pStmt= DBconn.conn.prepareStatement( 			// URL
				 		"INSERT INTO o_string_data (objectid,ot_parameter_id,value,lastchange,lastUser) VALUES (?,?,?,NOW(),?)");
					  pStmt.setString(3, jsonIn.getString("value"));
					  pStmt.setInt(4, userID);
					  break;
			        }
	        case 11: { pStmt= DBconn.conn.prepareStatement( 			// URL
				 		"INSERT INTO o_string_data (objectid,ot_parameter_id,value,lastchange,lastUser) VALUES (?,?,?,NOW(),?)");
					  pStmt.setString(3, jsonIn.getString("value"));
					  pStmt.setInt(4, userID);
					  break;
			        }
		}
//		System.out.println(pStmt.toString());
		pStmt.setInt(1, sampleID);
		pStmt.setInt(2, pid);
//		System.out.println(pStmt.toString());
		pStmt.executeUpdate();
		pStmt.close();
		DBconn.closeDB();
	} catch (SQLException e) {
		System.err.println("SaveSampleParameter: More Problems with SQL query");
		e.printStackTrace();
	} catch (JSONException e){
		System.err.println("SaveSampleParameter: More Problems creating JSON");
		e.printStackTrace();
	} catch (Exception e) {
		System.err.println("SaveSampleParameter: More Strange Problems");
	}
		
    // tell client that everything is fine
	Unidatoolkit.sendStandardAnswer(status, response);
	}
}	