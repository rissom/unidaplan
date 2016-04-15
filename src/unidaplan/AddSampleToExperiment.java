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

	public class AddSampleToExperiment extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doPost(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {		
	    
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
	    PreparedStatement pStmt = null;
	   	String privilege="n";
	    int experimentID=0;
	    int position=0;
	    int sampleID=0;

		request.setCharacterEncoding("utf-8");


	  	  	try{
	  	  		experimentID=Integer.parseInt(request.getParameter("experimentid")); 
	  	  		position=Integer.parseInt(request.getParameter("position")); 
	  	  		sampleID=Integer.parseInt(request.getParameter("sampleid")); 
	  	  	}
	  	  	catch (Exception e1) {
	  	  		System.err.print("AddSampleToExperiment: Parameters missing!");
	  	  	}
	    String status="ok";

	    try {
		    // Delete the user to the database	    
		 	DBconnection dBconn=new DBconnection();
		    dBconn.startDB();	   
		    
		    
		    
		    // check privilege
		    pStmt = dBconn.conn.prepareStatement( 	
					"SELECT getExperimentRights(vuserid:=?,vexperimentid:=?)");
			pStmt.setInt(1,userID);
			pStmt.setInt(2,experimentID);
			privilege = dBconn.getSingleStringValue(pStmt);
			pStmt.close();
		    
			if (privilege.equals("w")){
		    
				
				pStmt= dBconn.conn.prepareStatement( 			
						"INSERT INTO expp_samples VALUES (default, ?, NULL, ?, ?, NOW(),?)");
			   	pStmt.setInt(1, position);
			   	pStmt.setInt(2, experimentID);
			   	pStmt.setInt(3, sampleID);
			   	pStmt.setInt(4, userID);
			   	pStmt.executeUpdate();
				pStmt.close();
				
			} else {
				response.setStatus(401);
			}
			dBconn.closeDB();
			
		} catch (SQLException e) {
			System.err.println("AddSampleToExperiment: Problems with SQL query");
			status="SQL Error; AddSampleToExperiment";
		} catch (Exception e) {
			System.err.println("AddSampleToExperiment: Strange Problems");
			status="Error AddSampleToExperiment";
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
			System.err.println("AddSampleToExperiment: Problems creating JSON answer");
		}    
	}
}	