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

public class AddProcess extends HttpServlet {
	private static final long serialVersionUID = 1L;

	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {		
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
		int id = -1;
		String status="ok";
	    request.setCharacterEncoding("utf-8");
		response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    
	    // get the processTypeID
	    int processTypeID=-1;
	   	int lastProcessID=0;
	   	int timeZone=120;
	   	
	    try {
			 processTypeID=Integer.parseInt(request.getParameter("processtypeid")); 
			 timeZone=Integer.parseInt(request.getParameter("timezone")); 
		} catch (Exception e) {
			System.err.println("AddProcess: Error parsing type ID");
			response.setStatus(404);
		}
	    

	    
		try{
		    // create entry in the database	    
		 	DBconnection dBconn=new DBconnection();
		    dBconn.startDB();	   
		    PreparedStatement pstmt = null;
			
			// find the current maximum of process number parameter
			pstmt= dBconn.conn.prepareStatement( 	
			"SELECT n.value as maximum, pp.id AS parameterid "
			+"FROM processes "
			+"JOIN p_integer_data n ON (n.ProcessID=processes.ID) "
			+"JOIN p_parameters pp ON (pp.id=n.P_Parameter_ID AND pp.id_field=true) "
			+"WHERE (processes.ProcesstypesID=?) "
			+"ORDER BY n.value DESC "
			+"LIMIT 1");
		   	pstmt.setInt(1, processTypeID);
		   	JSONObject answer=dBconn.jsonObjectFromPreparedStmt(pstmt);
		   	if (answer.has("maximum")){
		   		lastProcessID= answer.getInt("maximum");
		   	}
			pstmt.close();

			pstmt= dBconn.conn.prepareStatement( 			
					 "INSERT INTO processes values(default, ?, NOW(), ?) RETURNING id");
		   	pstmt.setInt(1, processTypeID);
		   	pstmt.setInt(2, userID);
		   	JSONObject newProcessID=dBconn.jsonObjectFromPreparedStmt(pstmt);
		   	pstmt.close();
			id= newProcessID.getInt("id");

	   	
			// write processnumber 
	    	pstmt= dBconn.conn.prepareStatement("INSERT INTO p_integer_data VALUES(default, ?,"
	    			+ " (SELECT id FROM P_Parameters WHERE definition=8 AND processtypeid=?), ?, NOW(),?)");
	    	pstmt.setInt(1, id);
	    	pstmt.setInt(2, processTypeID);
	    	pstmt.setInt(3, lastProcessID+1);
	    	pstmt.setInt(4, userID);
	    	pstmt.executeUpdate();
	    	pstmt.close();
			
	    	
			// set status to "ok" 
	    	pstmt= dBconn.conn.prepareStatement("INSERT INTO p_integer_data VALUES(default, ?,"
	    			+ " (SELECT id FROM P_Parameters WHERE definition=1 AND processtypeid=?), ?, NOW(),?)");
	    	pstmt.setInt(1, id);
	    	pstmt.setInt(2, processTypeID);
	    	pstmt.setInt(3, 1);
	    	pstmt.setInt(4, userID);
	    	pstmt.executeUpdate();
	    	pstmt.close();
			
			
			// find date parameter
	    	pstmt= dBconn.conn.prepareStatement("SELECT id FROM p_parameters pp "
	    			+ "WHERE (pp.definition=10 AND pp.processtypeid=?)");
	    	pstmt.setInt(1, processTypeID);
		   	JSONObject dateIDObj=dBconn.jsonObjectFromPreparedStmt(pstmt);
		   	int dateID = dateIDObj.getInt("id");
		   	pstmt.close();
		   	
		   	
			// set date parameter to now
	    	pstmt= dBconn.conn.prepareStatement("INSERT INTO p_timestamp_data VALUES(default,?,?,NOW(),?,NOW(),?)");
	    	pstmt.setInt(1, id);
	    	pstmt.setInt(2, dateID);
	    	pstmt.setInt(3, timeZone);
	    	pstmt.setInt(4, userID);
	    	pstmt.executeUpdate();
	    	pstmt.close();
	
			dBconn.closeDB();

		} catch (SQLException e) {
			System.err.println("AddProcess: Problems with SQL query2");
			status="SQL error";
		} catch (JSONException e){
			System.err.println("AddProcess: Problems creating JSON later");
			status="JSON error";
		} catch (Exception e) {
			System.err.println("AddProcess: Strange Problems");
			status="error";
		}
		
			
	    // tell client the new id and that everything is fine
	    PrintWriter out = response.getWriter();
	    out.print("{\"id\":"+id+",");
		out.println("\"status\":\""+status+"\"}");
	}
}	