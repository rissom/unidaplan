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

public class ExchangePosPTSRParameter extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Authentificator authentificator = new Authentificator();
		int userID = authentificator.GetUserID(request,response);
		request.setCharacterEncoding("utf-8");
	    response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
		String in = request.getReader().readLine();
	    String status = "ok";
	    
		PreparedStatement pStmt = null; 	// Declare variables
		int paramGrpID1 = 0;
		int paramGrpID2 = 0;
		int pos1 = 0;
		int pos2 = 0;
	 	DBconnection dBconn = new DBconnection(); // New connection to the database

		

	    JSONObject jsonIn = null;	    
	    try {
	    	jsonIn = new JSONObject(in);
			paramGrpID1 = jsonIn.getInt("id1"); 
			paramGrpID2 = jsonIn.getInt("id2"); 
			pos1 = jsonIn.getInt("pos1");
			pos2 = jsonIn.getInt("pos2");
		} catch (JSONException e) {
			System.err.println("ExchangePosPTSRParameter: Input is not valid JSON");
			status = "error:invalid json";
		}

	 	
		
	    try {
		 	dBconn.startDB();

			if (dBconn.isAdmin(userID)){

				// set new position id for PG 1.
		        pStmt = dBconn.conn.prepareStatement(	
		        	"UPDATE po_parameters pg SET (position,lastuser)=(?,?) WHERE ID=?");
				pStmt.setInt(1,pos1);
				pStmt.setInt(2,userID);
				pStmt.setInt(3,paramGrpID1);
				pStmt.executeUpdate();
				pStmt.close();
				
				// set new position id for PG 2.
		        pStmt = dBconn.conn.prepareStatement(	
			        "UPDATE po_parameters pg SET (position,lastuser)=(?,?) WHERE ID=?");
				pStmt.setInt(1,pos2);
				pStmt.setInt(2,userID);
				pStmt.setInt(3,paramGrpID2);
				pStmt.executeUpdate();
				pStmt.close();		
			}else{
				// no admin rights
				response.setStatus(401);
			}		
	    } catch (SQLException eS) {
			System.err.println("Delete Process: SQL Error");
			status = "error: SQL error";
			eS.printStackTrace();
		} catch (Exception e) {
			System.err.println("ExchangePosPTSRParameter: Some Error, probably JSON");
			status = "error: JSON error";
			e.printStackTrace();
		} finally {
			
			
			
		try{
	    	   if (dBconn.conn != null) { 
	    		   dBconn.closeDB();  // close the database 
	    	   }
	        } catch (Exception e) {
				status = "error: error closing the database";
				System.err.println("ExchangePosPTSRParameter: Some Error closing the database");
				e.printStackTrace();
		   	}
        }
	    
	    Unidatoolkit.sendStandardAnswer(status, response);

	}
}

