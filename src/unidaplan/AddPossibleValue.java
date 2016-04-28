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

	public class AddPossibleValue extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doPost(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {		
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
	    request.setCharacterEncoding("utf-8");
	    String in = request.getReader().readLine();
	    String status = "ok";
	    String newPValueString="";
	    int parameterID=-1;
	    

	    JSONObject  jsonIn = null;	    
	    try {
			  jsonIn = new JSONObject(in);
		} catch (JSONException e) {
			System.err.println("AddPossibleValue: Input is not valid JSON");
		}

		response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
		
		try {	
		 	DBconnection dBconn=new DBconnection();
		    dBconn.startDB();
		    
		    // check if admin
			int admins=1;
			if (userID>0 && Unidatoolkit.isMemberOfGroup(userID,admins, dBconn)){
			
		    PreparedStatement pstmt = null;
		    
			    // read parameters
			    newPValueString=jsonIn.getString("value");
			    parameterID=jsonIn.getInt("parameterid");
			 	
			 	// insert new Value into the database
				pstmt= dBconn.conn.prepareStatement( 			
						 "INSERT INTO possible_values (parameterid, position, string, lastchange, lastuser) "
						+"VALUES(?,"
						+ "(SELECT COALESCE (MAX(position)+1,1) FROM possible_values b WHERE b.parameterid=? ) "
						+ ",?,NOW(),?)");
				pstmt.setInt(1, parameterID);
				pstmt.setInt(2, parameterID);
				pstmt.setString(3, newPValueString);
				pstmt.setInt(4, userID);
				pstmt.executeUpdate();
				pstmt.close();
			}else{
				dBconn.closeDB();
			}


		} catch (SQLException e) {
			System.err.println("AddPossibleValue: Problems with SQL query");
			status="SQL error";
			e.printStackTrace();
		} catch (JSONException e){
			System.err.println("AddPossibleValue: Problems creating JSON");
			status="JSON error";
		} catch (Exception e) {
			System.err.println("AddPossibleValue: Strange Problems");
			e.getStackTrace();
			status="error";
		}	
		

		
    // tell client that everything is fine
    Unidatoolkit.sendStandardAnswer(status, response);
	}
}	