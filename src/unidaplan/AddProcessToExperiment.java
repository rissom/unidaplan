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

public class AddProcessToExperiment extends HttpServlet {
	private static final long serialVersionUID = 1L;

		@Override
	  	public void doPost(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {		
	    
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
		request.setCharacterEncoding("utf-8");
	    int experimentID=0;
	    int processTypeID=0;
	    PreparedStatement pStmt = null;
	   	String privilege="n";

	  	  	try{
	  	  		experimentID=Integer.parseInt(request.getParameter("experimentid")); 
	  	  		processTypeID=Integer.parseInt(request.getParameter("processtype")); 
	  	  	}
	  	  	catch (Exception e1) {
	  	  		System.err.print("AddProcessToExperiment: Parameters missing!");
	  	  	}
	    String status="ok";

	    try {
		    // Connect to database	    
		 	DBconnection dBconn=new DBconnection();
		    dBconn.startDB();
		    
		    // Check privileges
		    pStmt = dBconn.conn.prepareStatement( 	
					"SELECT getExperimentRights(vuserid:=?,vexperimentid:=?)");
			pStmt.setInt(1,userID);
			pStmt.setInt(2,experimentID);
			System.out.println(pStmt.toString());
			privilege = dBconn.getSingleStringValue(pStmt);
			pStmt.close();
			
			if (privilege.equals("w")){	    
		    
				pStmt= dBconn.conn.prepareStatement( 			
						"INSERT INTO exp_plan_processes VALUES "
						+ "(default, (SELECT count(position)+1 FROM exp_plan_processes WHERE expp_id=?),"
						+ "?, ?, NULL, NULL,NOW(),?)");
			   	pStmt.setInt(1, experimentID);
			   	pStmt.setInt(2, experimentID);
			   	pStmt.setInt(3, processTypeID);
			   	pStmt.setInt(4, userID);
			   	pStmt.executeUpdate();
				pStmt.close();
			} else {
				response.setStatus(401);
			}
		
			dBconn.closeDB();
			
		} catch (SQLException e) {
			System.err.println("AddProcessToExperiment: Problems with SQL query");
			status="SQL Error; AddProcessToExperiment";
		} catch (Exception e) {
			System.err.println("AddProcessToExperiment: Strange Problems");
			status="Error AddProcessToExperiment";
		}	
		
	    // tell client that everything is fine
		response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    PrintWriter out = response.getWriter();
	    try {
	        JSONObject answer = new JSONObject();
			answer.put("status", status);
			out.println(answer.toString());
		} catch (JSONException e) {
			System.err.println("AddProcessToExperiment: Problems creating JSON answer");
		}    
	}
}	