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
	   	String privilege="n";
	   	
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
		    PreparedStatement pStmt = null;
		    
		    // check privilege
		    pStmt = dBconn.conn.prepareStatement( 	
					"SELECT getProcessTypeRights(vuserid:=?,vprocesstype:=?)");
			pStmt.setInt(1,userID);
			pStmt.setInt(2,processTypeID);
			privilege = dBconn.getSingleStringValue(pStmt);
			pStmt.close();
		    
		    
			if (privilege.equals("w")){
				
				// find the current maximum of process number parameter
				pStmt= dBconn.conn.prepareStatement( 	
				"SELECT n.value as maximum, pp.id AS parameterid "
				+"FROM processes "
				+"JOIN p_integer_data n ON (n.ProcessID=processes.ID) "
				+"JOIN p_parameters pp ON (pp.id=n.P_Parameter_ID AND pp.id_field=true) "
				+"WHERE (processes.ProcesstypesID=?) "
				+"ORDER BY n.value DESC "
				+"LIMIT 1");
			   	pStmt.setInt(1, processTypeID);
			   	JSONObject answer=dBconn.jsonObjectFromPreparedStmt(pStmt);
			   	if (answer.has("maximum")){
			   		lastProcessID= answer.getInt("maximum");
			   	}
				pStmt.close();
	
				pStmt= dBconn.conn.prepareStatement( 			
						 "INSERT INTO processes values(default, ?, NOW(), ?) RETURNING id");
			   	pStmt.setInt(1, processTypeID);
			   	pStmt.setInt(2, userID);
			   	JSONObject newProcessID=dBconn.jsonObjectFromPreparedStmt(pStmt);
			   	pStmt.close();
				id= newProcessID.getInt("id");
	
		   	
				// write processnumber 
		    	pStmt= dBconn.conn.prepareStatement("INSERT INTO p_integer_data VALUES(default, ?,"
		    			+ " (SELECT id FROM P_Parameters WHERE definition=8 AND processtypeid=?), ?, NOW(),?)");
		    	pStmt.setInt(1, id);
		    	pStmt.setInt(2, processTypeID);
		    	pStmt.setInt(3, lastProcessID+1);
		    	pStmt.setInt(4, userID);
		    	pStmt.executeUpdate();
		    	pStmt.close();
				
		    	
				// set status to "ok" 
		    	pStmt= dBconn.conn.prepareStatement("INSERT INTO p_integer_data VALUES(default, ?,"
		    			+ " (SELECT id FROM P_Parameters WHERE definition=1 AND processtypeid=?), ?, NOW(),?)");
		    	pStmt.setInt(1, id);
		    	pStmt.setInt(2, processTypeID);
		    	pStmt.setInt(3, 1);
		    	pStmt.setInt(4, userID);
		    	pStmt.executeUpdate();
		    	pStmt.close();
				
				
				// find date parameter
		    	pStmt= dBconn.conn.prepareStatement("SELECT id FROM p_parameters pp "
		    			+ "WHERE (pp.definition=10 AND pp.processtypeid=?)");
		    	pStmt.setInt(1, processTypeID);
			   	JSONObject dateIDObj=dBconn.jsonObjectFromPreparedStmt(pStmt);
			   	int dateID = dateIDObj.getInt("id");
			   	pStmt.close();
			   	
			   	
				// set date parameter to now
		    	pStmt= dBconn.conn.prepareStatement("INSERT INTO p_timestamp_data VALUES(default,?,?,NOW(),?,NOW(),?)");
		    	pStmt.setInt(1, id);
		    	pStmt.setInt(2, dateID);
		    	pStmt.setInt(3, timeZone);
		    	pStmt.setInt(4, userID);
		    	pStmt.executeUpdate();
		    	pStmt.close();
		    	
			} else {
				response.setStatus(401);
			}
		
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