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

	public class AddSampleParameter extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@SuppressWarnings("resource")
	  public void doPost(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {		
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
	    request.setCharacterEncoding("utf-8");
	    String in = request.getReader().readLine();
	    String status = "ok";
	    JSONObject  jsonIn = null;
	    PreparedStatement pStmt = null;
		String privilege = "n";


	    try {
			  jsonIn = new JSONObject(in);
		} catch (JSONException e) {
			System.err.println("AddSampleParameter: Input is not valid JSON");
		}
		response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    
	    // get the id
	    int id=0;
	    int sampleID=-1;
	    try {
			 id=jsonIn.getInt("id");	
			 sampleID=Integer.parseInt(request.getParameter("sampleid")); 
//			 sampleid=jsonIn.getInt("sampleid");			 
		} catch (JSONException e) {
			System.err.println("AddSampleParameter: Error parsing ID-Field");
			response.setStatus(404);
		}

	    
	    try {
		    // look up the datatype in Database	    
		 	DBconnection dBconn=new DBconnection();
		    dBconn.startDB();	   
		    
		    
		    // check privileges
	        pStmt = dBconn.conn.prepareStatement( 	
					"SELECT getSampleRights(vuserid:=?,vsample:=?)");
			pStmt.setInt(1,userID);
			pStmt.setInt(2,sampleID);
			privilege = dBconn.getSingleStringValue(pStmt);
			pStmt.close();
		    
			
			if (privilege.equals("w")){
	 
		    
			    int type=-1;
				pStmt= dBconn.conn.prepareStatement( 			
						 "SELECT paramdef.datatype FROM Ot_parameters otp \n"
						+"JOIN paramdef ON otp.definition=paramdef.id \n"
						+"WHERE otp.id=?");
			   	pStmt.setInt(1, id);
			   	JSONObject answer=dBconn.jsonObjectFromPreparedStmt(pStmt);
				type= answer.getInt("datatype");
				pStmt.close();
	
				pStmt= dBconn.conn.prepareStatement("DELETE FROM o_integer_data WHERE ot_parameter_id=? AND objectid=?");
			   	pStmt.setInt(1, id);
			   	pStmt.setInt(2,sampleID);
			   	pStmt.executeUpdate();
				pStmt.close();
	
	
		
				// Differentiate according to type
				if (jsonIn.getString("value").length()>0) {
					switch (type) {
				        case 1: pStmt= dBconn.conn.prepareStatement( 			// Integer values
						   					 "INSERT INTO o_integer_data VALUES(DEFAULT,?,?,?,NOW(),?) RETURNING ID");
							    pStmt.setInt(1, sampleID);
						   		pStmt.setInt(2, id);
						   		pStmt.setInt(3, jsonIn.getInt("value"));
						   		pStmt.setInt(4, userID);
								pStmt.executeUpdate();
								pStmt.close();
						   		break;
						        
				        case 2: pStmt= dBconn.conn.prepareStatement( 			// Double values
				   					 		"INSERT INTO o_float_data VALUES(DEFAULT,?,?,?,NOW(),?) RETURNING ID");
							    pStmt.setInt(1, sampleID);
						   		pStmt.setInt(2, id);				        
							    pStmt.setDouble(3, jsonIn.getDouble("value"));
						   		pStmt.setInt(4, userID);
								pStmt.executeUpdate();
								pStmt.close();
				   				break;
			        			
				        case 3: pStmt= dBconn.conn.prepareStatement( 			// Measurement data
									 		"INSERT INTO o_measurement_data (ObjectID, Ot_Parameter_ID, Value, "
									 		+"Error, lastChange, lastUser) "
									 		+"VALUES(?,?,?,?,NOW(),?) RETURNING ID");
				        		pStmt.setInt(1, sampleID);
				        		pStmt.setInt(2, id);
				        		if (jsonIn.getString("value").contains("±")){
									pStmt.setDouble(3, Double.parseDouble(jsonIn.getString("value").split("±")[0]));
									pStmt.setDouble(4, Double.parseDouble(jsonIn.getString("value").split("±")[1]));
				        		} else {
				        			pStmt.setDouble(3, jsonIn.getDouble("value"));
				        			pStmt.setDouble(4, 0);
				        		}
						   		pStmt.setInt(5, userID);
								pStmt.executeUpdate();
								pStmt.close();
								break;
				        case 4: pStmt= dBconn.conn.prepareStatement( 			// String data	
							 		"INSERT INTO o_string_data VALUES(DEFAULT,?,?,?,NOW(),?) RETURNING ID");
							    pStmt.setInt(1, sampleID);
							    pStmt.setInt(2, id);
							    pStmt.setString(3, jsonIn.getString("value"));
						   		pStmt.setInt(4, userID);
								pStmt.executeUpdate();
								pStmt.close();
								break;
						        
				        case 5: pStmt = dBconn.conn.prepareStatement( 			
				        		 		"INSERT INTO o_string_data VALUES(DEFAULT,?,?,?,NOW(),?) RETURNING ID");
							    pStmt.setInt(1, sampleID);
							    pStmt.setInt(2, id);
							    pStmt.setString(3, jsonIn.getString("value"));
						   		pStmt.setInt(4, userID);
								pStmt.executeUpdate();
								pStmt.close();
					}
				}
			} else {
				response.setStatus(401);
			}
			dBconn.closeDB();
	} catch (SQLException e) {
		System.err.println("AddSampleParameter: More Problems with SQL query");
		response.setStatus(404);
	} catch (JSONException e){
		System.err.println("AddSampleParameter: More Problems creating JSON");
		response.setStatus(404);
	} catch (Exception e) {
		System.err.println("AddSampleParameter: More Strange Problems");
		e.printStackTrace();
		response.setStatus(404);
	}

		
    // tell client that everything is fine
    Unidatoolkit.sendStandardAnswer(status,response);

	}
}	