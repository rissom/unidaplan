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
	    int processTypeID = -1;
	   	int lastProcessID = 1;
	   	int timeZone = 120;
	   	int recipe = 0;
	   	String privilege = "n";
	    String in = request.getReader().readLine();
	    JSONObject jsonIn = null;
	    String date = null;
	    
	    try {
			jsonIn = new JSONObject(in);
		} catch (JSONException e) {
			System.err.println("AddProcessType: Input is not valid JSON");
			status="input error";
		}
		
	    try {
			 processTypeID = jsonIn.getInt("processtypeid");
			 timeZone = jsonIn.getInt("tz");
			 if (jsonIn.has("recipe")){
				 recipe = jsonIn.getInt("recipe");
			 }
			 date = jsonIn.getString("date");
		} catch (Exception e) {
			System.err.println("AddProcess: Error parsing type ID");
			response.setStatus(404);
		}
	    

	    
		try{
			
		    // create entry in the database	    
		 	DBconnection dBconn = new DBconnection();
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
				pStmt = dBconn.conn.prepareStatement( 	
						  "SELECT "
						+ " p_number AS maximum "
						+ "FROM pnumbers "
						+ "WHERE (pnumbers.processtype = ?) "
						+ "ORDER BY p_number DESC "
						+ "LIMIT 1");
			   	pStmt.setInt(1, processTypeID);
			   	lastProcessID = dBconn.getSingleIntValue(pStmt);
			   	if (lastProcessID < 1) { lastProcessID = 1;};
				pStmt.close();
	
				// Create new database entry
				pStmt = dBconn.conn.prepareStatement( 			
						 "INSERT INTO processes (processtypesid, lastuser) "
					   + "VALUES (?, ?) "
					   + "RETURNING id");
			   	pStmt.setInt(1, processTypeID);
			   	pStmt.setInt(2, userID);
				id = dBconn.getSingleIntValue(pStmt);
			   	pStmt.close();
	
		   	
				// write processnumber 
		    	pStmt = dBconn.conn.prepareStatement(
		    			  "INSERT INTO processdata (ProcessID, ParameterID, Data, lastUser) "
		    			+ "VALUES( "
		    			+ " ?,"
		    			+ " (SELECT id FROM P_Parameters WHERE definition=8 AND processtypeid=?),"
		    			+ " ?, ?)");
		    	pStmt.setInt(1, id);
		    	pStmt.setInt(2, processTypeID);
		    	pStmt.setObject(3,new JSONObject().put("value",lastProcessID+1),java.sql.Types.OTHER);
		    	pStmt.setInt(4, userID);
		    	pStmt.executeUpdate();
		    	pStmt.close();
				
		    	
				// set status to "ok" // TODO: should be undefined
		    	pStmt = dBconn.conn.prepareStatement(
		    			  "INSERT INTO processdata (ProcessID, ParameterID, Data, lastUser) "
		    			+ "VALUES ( "
		    			+ "  ?, "
		    			+ "  (SELECT id FROM P_Parameters WHERE definition=1 AND processtypeid=?), "
		    			+ "  ?, ?)");
		    	pStmt.setInt(1, id);
		    	pStmt.setInt(2, processTypeID);
		    	pStmt.setObject(3, new JSONObject().put("value",1), java.sql.Types.OTHER);
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
		    	pStmt = dBconn.conn.prepareStatement(
		    			  "INSERT INTO processdata (ProcessID, ParameterID, Data, lastUser) "
		    			+ "VALUES (?,?,?,?)");
		    	
//		    	TimeZone tz = TimeZone.getTimeZone("UTC");
//		    	DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mmZ");
//		    	df.setTimeZone(tz);
//		    	String nowAsISO = df.format(new Date());
		    	JSONObject data = new JSONObject().put("date", date);
		    	pStmt.setInt(1, id);
		    	pStmt.setInt(2, dateID);
		    	pStmt.setObject(3, data, java.sql.Types.OTHER);
		    	pStmt.setInt(4, userID);
		    	pStmt.executeUpdate();
		    	pStmt.close();
		    	
		    	pStmt = dBconn.conn.prepareStatement(	
	   	   				"REFRESH MATERIALIZED VIEW pnumbers");
	   	   		pStmt.executeUpdate();
	   	   		pStmt.close();
	   	   		
	   	   		
	   	   		// fill in default parameter values from recipe
	   	   		if (recipe>0){
			   	   	pStmt = dBconn.conn.prepareStatement(	
				   	   	  "INSERT INTO processdata (processid, parameterid, data, lastuser) "
			   	   		+ "SELECT "
				   	 	+ "? AS processid, "
				   	 	+ "parameterid, "
				   	 	+ "data, "
				   	 	+ "? AS lastuser " 
			   	   		+ "FROM processrecipedata " 
			   	   		+ "WHERE recipeid = ? ");
			   	   	pStmt.setInt(1, id);
			   	   	pStmt.setInt(2, userID);
			   	   	pStmt.setInt(3, recipe);
		   	   		pStmt.executeUpdate();
	   	   		}

	   	   		
		    	
			} else {
				response.setStatus(401);
			}
		
			dBconn.closeDB();

		} catch (SQLException e) {
			System.err.println("AddProcess: Problems with SQL query2");
			status="SQL error";
		} catch (JSONException e){
			e.printStackTrace();
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