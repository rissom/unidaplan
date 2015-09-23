package unidaplan;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

	public class UpdateProcessParameter extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doPost(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {
		
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
	    request.setCharacterEncoding("utf-8");
	    String status="ok";
	    String in = request.getReader().readLine();
	    JSONObject  jsonIn = null;	    
	    try {
			  jsonIn = new JSONObject(in);
		} catch (JSONException e) {
			System.err.println("UpdateProcessParameter: Input is not valid JSON");
			status="error";
		}
		response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    PreparedStatement pStmt;

	    // get the id
	    int processID=0;
	    int parameterID=-1;
	    try {
			processID=jsonIn.getInt("processid");	
     		parameterID=jsonIn.getInt("parameterid");
		} catch (JSONException e) {
			System.err.println("UpdateProcessParameter: Error parsing ID-Field");
			status="error parsing ID-Field";
			response.setStatus(404);
		}

	 	DBconnection DBconn=new DBconnection();
	    DBconn.startDB();
	    
	    // Delete the old parameter.
	    try{
		    pStmt= DBconn.conn.prepareStatement( "DELETE FROM p_string_data WHERE ProcessID=? AND P_Parameter_ID=?");
		    pStmt.setInt(1, processID);
		    pStmt.setInt(2, parameterID);
		    pStmt.executeUpdate();
		    pStmt= DBconn.conn.prepareStatement( "DELETE FROM p_float_data WHERE ProcessID=? AND P_Parameter_ID=?");
		    pStmt.setInt(1, processID);
		    pStmt.setInt(2, parameterID);
		    pStmt.executeUpdate();
		    pStmt= DBconn.conn.prepareStatement( "DELETE FROM p_integer_data WHERE ProcessID=? AND P_Parameter_ID=?");
		    pStmt.setInt(1, processID);
		    pStmt.setInt(2, parameterID);
		    pStmt.executeUpdate();
		    pStmt= DBconn.conn.prepareStatement( "DELETE FROM p_measurement_data WHERE ProcessID=? AND P_Parameter_ID=?");
		    pStmt.setInt(1, processID);
		    pStmt.setInt(2, parameterID);
		    pStmt.executeUpdate();
		    pStmt.close();
	    } catch (SQLException e) {
			System.err.println("UpdateProcessParameter: Problems with SQL query for deletion");
			status="error";
		} catch (Exception e) {
			System.err.println("UpdateProcessParameter: Strange Problems deleting old parameter");
			status="error";
		}
	    
	    
	    // look up the datatype in Database	    
	    int type=-1;
		try {	
			pStmt= DBconn.conn.prepareStatement( 			
					 "SELECT paramdef.datatype FROM p_parameters p "
					+"JOIN paramdef ON p.definition=paramdef.id "
					+"WHERE p.id=?");
		   	pStmt.setInt(1, parameterID);
		   	JSONObject answer=DBconn.jsonObjectFromPreparedStmt(pStmt);
			type= answer.getInt("datatype");
		} catch (SQLException e) {
			System.err.println("UpdateProcessParameter: Problems with SQL query");
			status="error";
		} catch (JSONException e){
			System.err.println("UpdateProcessParameter: Problems creating JSON");
			status="error";
		} catch (Exception e) {
			System.err.println("UpdateProcessParameter: Strange Problems");
			status="error";
		}
		
		pStmt=null; // fooling eclipse to not show warnings
		
		// differentiate according to type
		try {	
			switch (type) {
	        case 1: {   pStmt= DBconn.conn.prepareStatement( 			// Integer values
				         "INSERT INTO p_integer_data VALUES(DEFAULT,?,?,?,NOW(),?) RETURNING ID");
			   			pStmt.setInt(1, processID);
				        pStmt.setInt(2, parameterID);
			   			pStmt.setInt(3, jsonIn.getInt("value"));
			   			pStmt.setInt(4, userID);
			   			break;
			        }
	        case 2: {   pStmt= DBconn.conn.prepareStatement( 			// Double values
				         "INSERT INTO p_float_data VALUES(DEFAULT,?,?,?,NOW(),?) RETURNING ID");
				        pStmt.setInt(1, processID);
			   			pStmt.setInt(2, parameterID);				        
				        pStmt.setDouble(3, jsonIn.getDouble("value"));
				        pStmt.setInt(4, userID);
	   					break;
        			}
	        case 3: {   pStmt= DBconn.conn.prepareStatement( 			// Measurement data
				         "INSERT INTO p_measurement_data VALUES(DEFAULT,?,?,?,?,NOW(),?) RETURNING ID");
						pStmt.setInt(1, processID);
						pStmt.setInt(2, parameterID);
						pStmt.setDouble(3, Double.parseDouble(jsonIn.getString("value").split("±")[0]));
						pStmt.setDouble(4, Double.parseDouble(jsonIn.getString("value").split("±")[1]));
						pStmt.setInt(5, userID);
						break;
			        }
	        case 4:  { pStmt= DBconn.conn.prepareStatement( 			// String data	
			        	"INSERT INTO p_string_data VALUES(DEFAULT,?,?,?,NOW(),?) RETURNING ID");
				       pStmt.setInt(1, processID);
				       pStmt.setInt(2, parameterID);
				       pStmt.setString(3, jsonIn.getString("value"));
				       pStmt.setInt(4, userID);
					   break;
			        }
	        case 5: {  pStmt= DBconn.conn.prepareStatement(
        		 		"INSERT INTO p_string_data VALUES(DEFAULT,?,?,?,NOW(),?) RETURNING ID");
				       pStmt.setInt(1, processID);
				       pStmt.setInt(2, parameterID);
				       pStmt.setString(3, jsonIn.getString("value"));
				       pStmt.setInt(4, userID);
				   }
	        case 7: {  pStmt= DBconn.conn.prepareStatement(
    		 		"INSERT INTO p_string_data VALUES(DEFAULT,?,?,?,NOW(),?) RETURNING ID");
			       pStmt.setInt(1, processID);
			       pStmt.setInt(2, parameterID);
			       java.sql.Timestamp ts= Timestamp.valueOf(jsonIn.getString("value"));
				   pStmt.setTimestamp(3, ts);
				   pStmt.setInt(4, userID);
			   }
			}
		
	    int id=DBconn.getSingleIntValue(pStmt);
   		pStmt.close();
		DBconn.closeDB();
	    // tell client that everything is fine
	    PrintWriter out = response.getWriter();
	    JSONObject myResponse= new JSONObject();
	    myResponse.put("status", status);
	    myResponse.put("id", id);
		out.println(myResponse.toString());
	} catch (SQLException e) {
		System.err.println("UpdateProcessParameter: More Problems with SQL query");
		status="error";
		e.printStackTrace();
	} catch (JSONException e){
		System.err.println("UpdateProcessParameter: More Problems creating JSON");
		status="error";
	} catch (Exception e) {
		System.err.println("UpdateProcessParameter: More Strange Problems");
		status="error";
	}
		
	}
}	