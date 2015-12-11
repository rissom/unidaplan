package unidaplan;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
	@Override
	  public void doPost(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {		
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
	    request.setCharacterEncoding("utf-8");
	    String in = request.getReader().readLine();
	    String status = "ok";
	    JSONObject  jsonIn = null;	    
	    try {
			  jsonIn = new JSONObject(in);
		} catch (JSONException e) {
			System.err.println("SaveSampleParameter: Input is not valid JSON");
		}
		response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    
	    // get the id
	    int id=0;
	    int sampleid=-1;
	    try {
			 id=jsonIn.getInt("id");	
			 sampleid=Integer.parseInt(request.getParameter("sampleid")); 
//			 sampleid=jsonIn.getInt("sampleid");			 
		} catch (JSONException e) {
			System.err.println("AddSampleParameter: Error parsing ID-Field");
			response.setStatus(404);
		}

	    
	    try {
		    // look up the datatype in Database	    
		 	DBconnection DBconn=new DBconnection();
		    DBconn.startDB();	   
		    PreparedStatement pStmt = null;
		    int type=-1;
			pStmt= DBconn.conn.prepareStatement( 			
					 "SELECT paramdef.datatype FROM Ot_parameters otp \n"
					+"JOIN paramdef ON otp.definition=paramdef.id \n"
					+"WHERE otp.id=? \n");
		   	pStmt.setInt(1, id);
		   	JSONObject answer=DBconn.jsonObjectFromPreparedStmt(pStmt);
			type= answer.getInt("datatype");
			pStmt.close();

			pStmt= DBconn.conn.prepareStatement("DELETE FROM o_integer_data WHERE ot_parameter_id=? AND objectid=?");
		   	pStmt.setInt(1, id);
		   	pStmt.setInt(2,sampleid);
		   	pStmt.executeUpdate();
			pStmt.close();


	
			// differentiate according to type
			if (jsonIn.getString("value").length()>0) {

			switch (type) {
	        case 1: {   pStmt= DBconn.conn.prepareStatement( 			// Integer values
			   					 "INSERT INTO o_integer_data VALUES(DEFAULT,?,?,?,NOW(),?) RETURNING ID");
				        pStmt.setInt(1, sampleid);
			   			pStmt.setInt(2, id);
			   			pStmt.setInt(3, jsonIn.getInt("value"));
			   			pStmt.setInt(4, userID);
			   			break;
			        }
	        case 2: {   pStmt= DBconn.conn.prepareStatement( 			// Double values
	   					 		"INSERT INTO o_float_data VALUES(DEFAULT,?,?,?,NOW(),?) RETURNING ID");
				        pStmt.setInt(1, sampleid);
			   			pStmt.setInt(2, id);				        
				        pStmt.setDouble(3, jsonIn.getDouble("value"));
			   			pStmt.setInt(4, userID);
	   					break;
        			}
	        case 3: {   pStmt= DBconn.conn.prepareStatement( 			// Measurement data
						 		"INSERT INTO o_measurement_data (ObjectID, Ot_Parameter_ID, Value, "
						 		+"Error, lastChange, lastUser) "
						 		+"VALUES(?,?,?,?,NOW(),?) RETURNING ID");
	        			pStmt.setInt(1, sampleid);
	        			pStmt.setInt(2, id);
	        			if (jsonIn.getString("value").contains("±")){
							pStmt.setDouble(3, Double.parseDouble(jsonIn.getString("value").split("±")[0]));
							pStmt.setDouble(4, Double.parseDouble(jsonIn.getString("value").split("±")[1]));
	        			} else {
	        				pStmt.setDouble(3, jsonIn.getDouble("value"));
	        				pStmt.setDouble(4, 0);
	        			}
			   			pStmt.setInt(5, userID);
						break;
			        }
	        case 4:  { pStmt= DBconn.conn.prepareStatement( 			// String data	
				 		"INSERT INTO o_string_data VALUES(DEFAULT,?,?,?,NOW(),?) RETURNING ID");
				        pStmt.setInt(1, sampleid);
				        pStmt.setInt(2, id);
				        pStmt.setString(3, jsonIn.getString("value"));
			   			pStmt.setInt(4, userID);
					   break;
			        }
	        case 5: {  pStmt= DBconn.conn.prepareStatement( 			
	        		 	"INSERT INTO o_string_data VALUES(DEFAULT,?,?,?,NOW(),?) RETURNING ID");
				        pStmt.setInt(1, sampleid);
				        pStmt.setInt(2, id);
				        pStmt.setString(3, jsonIn.getString("value"));
			   			pStmt.setInt(4, userID);
				   }
			}
			}
		
			ResultSet pidResult=pStmt.executeQuery();
			pidResult.next();
			pStmt.close();
			DBconn.closeDB();
	} catch (SQLException e) {
		System.err.println("SaveSampleParameter: More Problems with SQL query");
		response.setStatus(404);
	} catch (JSONException e){
		System.err.println("SaveSampleParameter: More Problems creating JSON");
		response.setStatus(404);
	} catch (Exception e) {
		System.err.println("SaveSampleParameter: More Strange Problems");
		e.printStackTrace();
		response.setStatus(404);
	}

		
    // tell client that everything is fine
    Unidatoolkit.sendStandardAnswer(status,response);

	}
}	