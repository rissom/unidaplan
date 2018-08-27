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

	public class UpdateProcessNumber extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doPut(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {
		
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
	    request.setCharacterEncoding("utf-8");
	    String status="ok";
	    String in = request.getReader().readLine();
	    String privilege = "n";
	    JSONObject jsonIn = null;	    
	    try {
			  jsonIn = new JSONObject(in);
		} catch (JSONException e) {
			System.err.println("SaveProcessParameter: Input is not valid JSON");
			status="error";
		}
		response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    PreparedStatement pStmt;

	    // get the id
	    int processID=0;
	    int number=-1;
	    
	    try {
	    	processID = jsonIn.getInt("processid");
			number = jsonIn.getInt("number");
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	    
	 	DBconnection dBconn=new DBconnection();

	    
		try {	
		    dBconn.startDB();	   
		    
	        pStmt= dBconn.conn.prepareStatement( 	
					"SELECT getProcessRights(vuserid:=?,vprocess:=?)");
			pStmt.setInt(1,userID);
			pStmt.setInt(2,processID);
			privilege=dBconn.getSingleStringValue(pStmt);
			pStmt.close();
		} catch (SQLException e) {
			System.err.println("SaveSampleParameter: Problems with SQL query");
			status="Problems with SQL query";
			e.printStackTrace();
		} catch (JSONException e){
			System.err.println("SaveSampleParameter: Problems creating JSON");
			status="Problems creating JSON";
		} catch (Exception e) {
			System.err.println("SaveSampleParameter: Strange Problems");
			status="Strange Problems";
		}
	        
			
			
	    if (privilege.equals("w")){
		    
		    try {
				processID=jsonIn.getInt("processid");	
	     		number=jsonIn.getInt("number");
			} catch (JSONException e) {
				System.err.println("SaveProcessParameter: Error parsing ID-Field");
				status="error parsing ID-Field";
				response.setStatus(404);
			}
	
		 	
		 	try {	
			    // Select p_parameter id
			    pStmt = dBconn.conn.prepareStatement( 			
						   "SELECT pp.id FROM processes "
					 	 + "JOIN p_parameters pp ON pp.processtypeid = processes.processtypesid AND pp.definition = 7 "
				 		 + "WHERE processes.id = ?");
			   	pStmt.setInt(1, processID);
			   	int ppid = dBconn.getSingleIntValue(pStmt);
			   	pStmt.close();
			    
			    // check if the number already exists for the processtype of the given process
				pStmt = dBconn.conn.prepareStatement( 			
						   "SELECT 1 AS exists "
						 + "FROM  processdata pid "
						 + "WHERE pid.parameterid = ? AND pid.data = ?");
			   	pStmt.setInt(1, ppid);
			   	JSONObject numberJSON = new JSONObject();
			   	numberJSON.put("value", number);
	   		  	pStmt.setObject(2, numberJSON, java.sql.Types.OTHER);
			   	Boolean exists = dBconn.getSingleIntValue(pStmt) == 1;
			   	
			   	if (exists){ // error!
			   		response.setStatus(409);
			   		status = "number already taken";
			   	} else {  	// update the old value.
					pStmt = dBconn.conn.prepareStatement( 			
							  "UPDATE processdata "
							+ "SET data = ? "
							+ "WHERE processdata.processid = ? AND parameterid = ?");
		   		  	pStmt.setObject(1, numberJSON, java.sql.Types.OTHER);
				   	pStmt.setInt(2, processID);
				   	pStmt.setInt(3, ppid);
				   	pStmt.executeUpdate();
				   	pStmt.close();
			   	}
			} catch (SQLException e) {
				System.err.println("SaveSampleParameter: Problems with SQL query");
				status = "Problems with SQL query";
				e.printStackTrace();
			} catch (JSONException e){
				System.err.println("SaveSampleParameter: Problems creating JSON");
				status = "Problems creating JSON";
			} catch (Exception e) {
				System.err.println("SaveSampleParameter: Strange Problems");
				status = "Strange Problems";
			}
	    } else {
	    	response.setStatus(401);
	    }
		
		dBconn.closeDB();
	    // tell client that everything is fine
	    Unidatoolkit.sendStandardAnswer(status, response);
	}
}	