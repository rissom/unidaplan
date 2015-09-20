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

	public class UpdateExperimentParameter extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@SuppressWarnings("resource")
	@Override
	  public void doPost(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {
		
		Authentificator authentificator = new Authentificator();
		String status="ok";
		int userID=authentificator.GetUserID(request,response);
	    request.setCharacterEncoding("utf-8");
	    String in = request.getReader().readLine();
	    JSONObject  jsonIn = null;	    
	    try {
			  jsonIn = new JSONObject(in);
		} catch (JSONException e) {
			System.err.println("UpdateExperimentParameter: Input is not valid JSON");
		}
		response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    
	    // get the id
	    int id=0;
	    int pid=-1;
	    try {
			 id=jsonIn.getInt("id");	
     		pid=jsonIn.getInt("pid");
		} catch (JSONException e) {
			System.err.println("UpdateExperimentParameter: Error parsing ID-Field");
			status = "Error parsing ID-Field";
			response.setStatus(404);
		}

	    
	    // look up the datatype in Database	    
	 	DBconnection DBconn=new DBconnection();
	    DBconn.startDB();	   
	    PreparedStatement pstmt = null;
	    int type=-1;
		try {	
			pstmt= DBconn.conn.prepareStatement( 			
					 "SELECT paramdef.datatype FROM Expp_param ep \n"
					+"JOIN paramdef ON ep.definition=paramdef.id \n"
					+"WHERE ep.id=? \n");
		   	pstmt.setInt(1, id);
		   	JSONObject answer=DBconn.jsonObjectFromPreparedStmt(pstmt);
			type= answer.getInt("datatype");
		} catch (SQLException e) {
			System.err.println("UpdateExperimentParameter: Problems with SQL query");
			status = "SQL Error";
		} catch (JSONException e){
			System.err.println("UpdateExperimentParameter: Problems creating JSON");
			status = "JSON Error";
		} catch (Exception e) {
			System.err.println("UpdateExperimentParameter: Strange Problems");
			status = "Misc Error (line70)";
		}
		
		// differentiate according to type
		try {	

			switch (type) {
	        case 1: {   pstmt= DBconn.conn.prepareStatement( 			// Integer values
			   					 "UPDATE expp_integer_data SET (value,lastUser)=(?,?)  WHERE id=?");
			   			pstmt.setInt(1, jsonIn.getInt("value"));
	   					pstmt.setInt(2, userID);
			   			pstmt.setInt(3, pid);
			   			break;
			        }
	        case 2: {   pstmt= DBconn.conn.prepareStatement( 			// Double values
	   					 		"UPDATE expp_float_data SET (value,lastUser)=(?,?) WHERE id=?");
	   					pstmt.setDouble(1, jsonIn.getDouble("value"));
	   					pstmt.setInt(2, userID);
	   					pstmt.setInt(3, pid);
	   					break;
        			}
	        case 3: {   pstmt= DBconn.conn.prepareStatement( 			// Measurement data
						 		"UPDATE expp_measurement_data SET (value,error,lastuser)=(?,?,?) WHERE id=?");
						pstmt.setDouble(1, Double.parseDouble(jsonIn.getString("value").split("±")[0]));
						pstmt.setDouble(2, Double.parseDouble(jsonIn.getString("value").split("±")[1]));
	   					pstmt.setInt(3, userID);
						pstmt.setInt(4, pid);
						break;
			        }
	        case 4:  { pstmt= DBconn.conn.prepareStatement( 			// String data	
				 		"UPDATE expp_string_data SET (value,lastuser)=(?,?) WHERE id=?");
					   pstmt.setString(1, jsonIn.getString("value"));
	   				   pstmt.setInt(2, userID);
					   pstmt.setInt(3, pid);
					   break;
			        }
	        case 5: {  pstmt= DBconn.conn.prepareStatement( 			
				 	   			"UPDATE expp_string_data SET (value,lastuser)=(?,?) WHERE id=?");
					   pstmt.setString(1, jsonIn.getString("value"));
	   				   pstmt.setInt(2, userID);
					   pstmt.setInt(3, pid);
				   }
			}
		
		pstmt.executeUpdate();
		pstmt.close();
		DBconn.closeDB();
	} catch (SQLException e) {
		System.err.println("UpdateExperimentParameter: More Problems with SQL query");
		status = "SQL Error";
	} catch (JSONException e){
		System.err.println("UpdateExperimentParameter: More Problems creating JSON");
		status = "JSON Error";
	} catch (Exception e) {
		System.err.println("UpdateExperimentParameter: More Strange Problems");
		status = "Misc. Error";
	}
		
    // tell client that everything is fine
    PrintWriter out = response.getWriter();
	out.println("{\"status\":\""+status+"\"}");
	}
}	