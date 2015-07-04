	package unidaplan;
	import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

	public class SaveSampleParameter extends HttpServlet {
		private static final long serialVersionUID = 1L;
		private static JSONArray result;

	@Override
	  public void doPost(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {		
	    request.setCharacterEncoding("utf-8");
	    String in = request.getReader().readLine();
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
	    int pid=-1;
	    String svalue="";
	    try {
			 id=jsonIn.getInt("id");	
     		pid=jsonIn.getInt("pid");
		} catch (JSONException e) {
			System.err.println("SaveSampleParameter: Error parsing ID-Field");
			response.setStatus(404);
		}

	    
	    // look up the datatype in Database	    
	 	DBconnection DBconn=new DBconnection();
	    DBconn.startDB();	   
	    PreparedStatement pstmt = null;
	    int type=-1;
		try {	
			pstmt= DBconn.conn.prepareStatement( 			
					 "SELECT paramdef.datatype FROM Ot_parameters otp \n"
					+"JOIN paramdef ON otp.definition=paramdef.id \n"
					+"WHERE otp.id=? \n");
		   	pstmt.setInt(1, id);
		   	JSONObject answer=DBconn.jsonObjectFromPreparedStmt(pstmt);
			type= answer.getInt("datatype");
		} catch (SQLException e) {
			System.err.println("SaveSampleParameter: Problems with SQL query");
		} catch (JSONException e){
			System.err.println("SaveSampleParameter: Problems creating JSON");
		} catch (Exception e) {
			System.err.println("SaveSampleParameter: Strange Problems");
		}
		
		// differentiate according to type
		try {	

			switch (type) {
	        case 1: {   pstmt= DBconn.conn.prepareStatement( 			// Integer values
			   					 "UPDATE o_integer_data SET value=? WHERE id=? \n");
			   			pstmt.setInt(1, jsonIn.getInt("value"));
			   			pstmt.setInt(2, pid);
			   			break;
			        }
	        case 2: {   pstmt= DBconn.conn.prepareStatement( 			// Double values
	   					 		"UPDATE o_float_data SET value=? WHERE id=? \n");
	   					pstmt.setDouble(1, jsonIn.getDouble("value"));
	   					pstmt.setInt(2, pid);
	   					break;
        			}
	        case 3: {   pstmt= DBconn.conn.prepareStatement( 			// Measurement data
						 		"UPDATE o_measurement_data SET value=? WHERE id=? \n");
						pstmt.setDouble(1, Double.parseDouble(jsonIn.getString("value").split("±")[0]));
						pstmt.setInt(2, pid);
						break;
			        }
	        case 4:  { pstmt= DBconn.conn.prepareStatement( 			// String data	
				 		"UPDATE o_string_data SET value=? WHERE id=? \n");
					   pstmt.setString(1, jsonIn.getString("value"));
					   pstmt.setInt(2, pid);
					   break;
			        }
	        case 5: {  pstmt= DBconn.conn.prepareStatement( 			
				 	   			"UPDATE o_string_data SET value=? WHERE id=? \n");
					   pstmt.setString(1, jsonIn.getString("value"));
					   pstmt.setInt(2, pid);
				   }
			}
		  
		
			
		pstmt.executeUpdate();
		pstmt.close();
		DBconn.closeDB();
	} catch (SQLException e) {
		System.err.println("SaveSampleParameter: More Problems with SQL query");
		e.printStackTrace();
	} catch (JSONException e){
		System.err.println("SaveSampleParameter: More Problems creating JSON");
	} catch (Exception e) {
		System.err.println("SaveSampleParameter: More Strange Problems");
	}
		
    // tell client that everything is fine
    PrintWriter out = response.getWriter();
	out.println("{\"status\":\"ok\"}");
	}
}	