package unidaplan;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;

	public class UpdatePositionsForProcessesInExperiment extends HttpServlet {
		/* Updates the positions for the default processes of an experiment */
		private static final long serialVersionUID = 1L;

	@Override
	  public void doPost(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {		
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
	    request.setCharacterEncoding("utf-8");
	    String in = request.getReader().readLine();
	    String status = "ok";
		String privilege = "n";

	    JSONArray  jsonIn = null;	    
	    try {
	    		
			  jsonIn = new JSONArray(in);
		} catch (JSONException e) {
			System.err.println("UpdatePositionsForProcessesInExperiment: Input is not valid JSON");
		}
		
		response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");

	 	DBconnection dBconn=new DBconnection();
	    PreparedStatement pStmt = null;
	    
		
		try {	
		    dBconn.startDB();
			for (int i=0; i<jsonIn.length();i++){
				
				// get corresponding experimentID
				pStmt = dBconn.conn.prepareStatement(  
							"SELECT expp_id FROM exp_plan_processes WHERE id=?");
				pStmt.setInt(1, jsonIn.getJSONObject(i).getInt("id"));
				int experimentID = dBconn.getSingleIntValue(pStmt);
				pStmt.close();

			    
			    // check privileges
			    pStmt = dBconn.conn.prepareStatement( 	
						"SELECT getExperimentRights(vuserid:=?,vexperimentid:=?)");
				pStmt.setInt(1,userID);
				pStmt.setInt(2,experimentID);
				privilege = dBconn.getSingleStringValue(pStmt);
				pStmt.close();
					
				if (privilege.equals("w")){
					pStmt = dBconn.conn.prepareStatement(  
						"UPDATE exp_plan_processes SET position=? WHERE id=?");
					pStmt.setInt(1, jsonIn.getJSONObject(i).getInt("position"));
					pStmt.setInt(2, jsonIn.getJSONObject(i).getInt("id"));
					pStmt.executeUpdate();
				} else {
			    	response.setStatus(401);
			    }
			}


		} catch (SQLException e) {
			System.err.println("UpdatePositionsForProcessesInExperiment: Problems with SQL query");
			status="SQL error";
		} catch (JSONException e){
			System.err.println("UpdatePositionsForProcessesInExperiment: Problems creating JSON");
			status="JSON error";
		} catch (Exception e) {
			System.err.println("UpdatePositionsForProcessesInExperiment: Strange Problems");
			status="error";
		}	
		
		dBconn.closeDB();

		
    // tell client that everything is fine
    Unidatoolkit.sendStandardAnswer(status, response);
	}
}	