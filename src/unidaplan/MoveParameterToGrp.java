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

	public class MoveParameterToGrp extends HttpServlet {
		private static final long serialVersionUID = 1L;

		@Override
		public void doPut(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {		
			Authentificator authentificator = new Authentificator();
			int userID=authentificator.GetUserID(request,response);
		    request.setCharacterEncoding("utf-8");
		    String in = request.getReader().readLine();
		    String status = "ok";
	
		    JSONObject jsonIn = null;	
		    int parameterID = -1;
		    int destination = -1;
		    
	    
		    try {
				  jsonIn = new JSONObject(in);
			} catch (JSONException e) {
				System.err.println("MoveParameterToGrp: Input is not valid JSON");
			}
			
		    
		    try {
		    	parameterID=jsonIn.getInt("parameterid");
				destination=jsonIn.getInt("destination");
			} catch (JSONException e) {
				System.err.println("MoveParameterToGrp: Error parsing ID-Field or comment");
				response.setStatus(404);
			}
		    
		 	DBconnection dBconn=new DBconnection(); // initialize database
		    PreparedStatement pStmt = null;
		    
			
			try {
			    dBconn.startDB();
			    
			    if (Unidatoolkit.userHasAdminRights(userID, dBconn)){
	
					// find the stringkey
					pStmt=dBconn.conn.prepareStatement(
							"UPDATE ot_parameters SET (parametergroup,id_field,lastUser)=(?,FALSE,?) WHERE id=?");
					pStmt.setInt(1,destination);
					pStmt.setInt(2,userID);
					pStmt.setInt(3,parameterID);
					pStmt.executeUpdate();
					pStmt.close();
			    }else{
			    	response.setStatus(401);
			    }
			} catch (SQLException e) {
				System.err.println("MoveParameterToGrp: Problems with SQL query");
				status="SQL error";
				response.setStatus(404);
			} catch (Exception e) {
				System.err.println("MoveParameterToGrp: some error occured");
				status="misc error";
				response.setStatus(404);
			}
			
			dBconn.closeDB();
	
	    // tell client that everything is fine
	    Unidatoolkit.sendStandardAnswer(status,response);
	}
}	