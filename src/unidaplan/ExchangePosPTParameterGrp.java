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

public class ExchangePosPTParameterGrp extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ExchangePosPTParameterGrp() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
		request.setCharacterEncoding("utf-8");
	    response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    PrintWriter out = response.getWriter(); 
		String in = request.getReader().readLine();
	    String status="ok";
	    
		PreparedStatement pstmt = null; 	// Declare variables
		int paramGrpID1 = 0;
		int paramGrpID2 = 0;
		int pos1=0;
		int pos2=0;
	 	DBconnection DBconn=new DBconnection(); // New connection to the database
	 	DBconn.startDB();

		

	    JSONObject jsonIn = null;	    
	    try {
	    	jsonIn = new JSONObject(in);
			paramGrpID1=jsonIn.getInt("id1"); 
			paramGrpID2=jsonIn.getInt("id2"); 
			pos1=jsonIn.getInt("pos1");
			pos2=jsonIn.getInt("pos2");
		} catch (JSONException e) {
			System.err.println("ExchangePosPTParameterGrp: Input is not valid JSON");
			status="error:invalid json";
		}

	 	
		
	    try {
				// set new position id for PG 1.
		        pstmt = DBconn.conn.prepareStatement(	
		        	"UPDATE p_parametergrps pg SET (pos,lastuser)=(?,?) WHERE ID=?");
				pstmt.setInt(1,pos1);
				pstmt.setInt(2,userID);
				pstmt.setInt(3,paramGrpID1);
				pstmt.executeUpdate();
				pstmt.close();
				
				// set new position id for PG 2.
		        pstmt = DBconn.conn.prepareStatement(	
			        "UPDATE p_parametergrps pg SET (pos,lastuser)=(?,?) WHERE ID=?");
				pstmt.setInt(1,pos2);
				pstmt.setInt(2,userID);
				pstmt.setInt(3,paramGrpID2);
				pstmt.executeUpdate();
				pstmt.close();				
	    } catch (SQLException eS) {
			System.err.println("Delete Process: SQL Error");
			status="error: SQL error";
			eS.printStackTrace();
		} catch (Exception e) {
			System.err.println("ExchangePosPTParameterGrp: Some Error, probably JSON");
			status="error: JSON error";
			e.printStackTrace();
		} finally {
			
			
			
		try{	
	    	   if (DBconn.conn != null) { 
	    		   DBconn.closeDB();  // close the database 
	    	   }
	        } catch (Exception e) {
				status="error: error closing the database";
				System.err.println("ExchangePosPTParameterGrp: Some Error closing the database");
				e.printStackTrace();
		   	}
        }
	    
	    
	    
	    try {
	        JSONObject answer = new JSONObject();
			answer.put("status", status);
			out.println(answer.toString());
		} catch (JSONException e) {
			System.err.println("DeletePTParameterGrp: Problems creating JSON answer");
    	}


}}

