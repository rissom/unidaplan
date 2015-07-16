package unidaplan;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONException;
import org.json.JSONObject;

	public class AddExperimentParameter extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@SuppressWarnings("resource")
	@Override
	  public void doPost(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {		
	    request.setCharacterEncoding("utf-8");
	    String in = request.getReader().readLine();
	    int pid=-1;
	    JSONObject  jsonIn = null;	    
	    try {
			  jsonIn = new JSONObject(in);
		} catch (JSONException e) {
			System.err.println("AddExperimentParameter: Input is not valid JSON");
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
			System.err.println("AddExperimentParameter: Error parsing ID-Field");
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
			System.err.println("AddExperimentParameter: Problems with SQL query");
		} catch (JSONException e){
			System.err.println("AddExperimentParameter: Problems creating JSON");
		} catch (Exception e) {
			System.err.println("AddExperimentParameter: Strange Problems");
		}
		
		// differentiate according to type
		try {	

			switch (type) {
	        case 1: {   pstmt= DBconn.conn.prepareStatement( 			// Integer values
			   					 "INSERT INTO expp_integer_data VALUES(DEFAULT,?,?,?) RETURNING ID");
			   			pstmt.setInt(1, id);
				        pstmt.setInt(2, sampleid);
			   			pstmt.setInt(3, jsonIn.getInt("value"));
			   			break;
			        }
	        case 2: {   pstmt= DBconn.conn.prepareStatement( 			// Double values
	   					 		"INSERT INTO expp_float_data VALUES(DEFAULT,?,?,?) RETURNING ID");
				        pstmt.setInt(1, sampleid);
			   			pstmt.setInt(2, id);				        
				        pstmt.setDouble(3, jsonIn.getDouble("value"));
	   					break;
        			}
	        case 3: {   pstmt= DBconn.conn.prepareStatement( 			// Measurement data
						 		"INSERT INTO expp_measurement_data VALUES(DEFAULT,?,?,?,?) RETURNING ID");
	        			pstmt.setInt(1, sampleid);
	        			pstmt.setInt(2, id);
						pstmt.setDouble(3, Double.parseDouble(jsonIn.getString("value").split("±")[0]));
						pstmt.setDouble(4, Double.parseDouble(jsonIn.getString("value").split("±")[1]));
						break;
			        }
	        case 4:  { pstmt= DBconn.conn.prepareStatement( 			// String data	
				 		"INSERT INTO expp_string_data VALUES(DEFAULT,?,?,?) RETURNING ID");
				        pstmt.setInt(1, sampleid);
				        pstmt.setInt(2, id);
				        pstmt.setString(3, jsonIn.getString("value"));
					   break;
			        }
	        case 5: {  pstmt= DBconn.conn.prepareStatement( 			
	        		 	"INSERT INTO expp_string_data VALUES(DEFAULT,?,?,?) RETURNING ID");
				        pstmt.setInt(1, sampleid);
				        pstmt.setInt(2, id);
				        pstmt.setString(3, jsonIn.getString("value"));
				   }
			}
		
			
		ResultSet pidResult=pstmt.executeQuery();
		pidResult.next();
		pid=pidResult.getInt(1);
		pstmt.close();
		DBconn.closeDB();
	} catch (SQLException e) {
		System.err.println("AddExperimentParameter: More Problems with SQL query");
		e.printStackTrace();
	} catch (JSONException e){
		System.err.println("AddExperimentParameter: More Problems creating JSON");
	} catch (Exception e) {
		System.err.println("AddExperimentParameter: More Strange Problems");
	}
		
    // tell client that everything is fine
    PrintWriter out = response.getWriter();
    out.print("{\"pid\":"+pid+",");
	out.println("\"status\":\"ok\"}");
	}
}	