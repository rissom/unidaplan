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

	public class UpdateSearchOwner extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doPut(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {		
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
	    request.setCharacterEncoding("utf-8");
	    String in = request.getReader().readLine();
	    String status = "ok";

	    JSONObject  jsonIn = null;	
	    int searchID = -1;
	       
	    try {
	    	jsonIn = new JSONObject(in);
		} catch (JSONException e) {
			System.err.println("UpdateSearchOwner: Input is not valid JSON");
		}
		

	    int newOwner=-1; // get parameters
	    
	    try {
	         searchID=jsonIn.getInt("searchid");
			 newOwner=jsonIn.getInt("newowner");
		} catch (JSONException e) {
			System.err.println("UpdateSearchOwner: Error parsing ID-Field or comment");
			response.setStatus(404);
		}
	    
	 	DBconnection dBconn=new DBconnection(); // initialize database
	    PreparedStatement pStmt = null;
	    
		
		try {
		    dBconn.startDB();	   

		    // find the stringkey
			pStmt=dBconn.conn.prepareStatement(
				"UPDATE searches SET owner=? WHERE searches.id=?");
			pStmt.setInt(1,newOwner);
			pStmt.setInt(2,searchID);
			pStmt.executeUpdate();
			pStmt.close();			
		} catch (SQLException e) {
			System.err.println("UpdateSearchOwner: Problems with SQL query");
			response.setStatus(404);
			status="SQL error";
		} catch (Exception e) {
			System.err.println("UpdateSearchOwner: some error occured");
			response.setStatus(404);
			status="misc error";
		}
		
		dBconn.closeDB();

		
    // tell client that everything is fine
    Unidatoolkit.sendStandardAnswer(status,response);
	}
}	