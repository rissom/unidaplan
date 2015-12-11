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

	public class UpdateSearchOperation extends HttpServlet {
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
	    Boolean operation = false;	    

	    
    
	    try {
			  jsonIn = new JSONObject(in);
		} catch (JSONException e) {
			System.err.println("UpdateSearchOperation: Input is not valid JSON");
		}
		

	    // get the input parameters
	    try {
	         operation=jsonIn.getBoolean("operation");
	         searchID=jsonIn.getInt("searchid");
		} catch (JSONException e) {
			System.err.println("UpdateSearchOperation: Error parsing ID-Field or comment");
			response.setStatus(404);
		}
	    
	 	DBconnection dBconn=new DBconnection(); // initialize database
	    
		
		try {
		    dBconn.startDB();   
			PreparedStatement pStmt= dBconn.conn.prepareStatement( 			
					 "UPDATE searches SET (operation,lastuser)=(?,?) WHERE id=?");
			pStmt.setBoolean(1,operation);
			pStmt.setInt(2,userID);
			pStmt.setInt(3, searchID);
			pStmt.executeUpdate();
			pStmt.close();
			
			
		} catch (SQLException e) {
			System.err.println("UpdateSearchOperation: Problems with SQL query");
			status="SQL error";
		} catch (Exception e) {
			System.err.println("UpdateSearchOperation: some error occured");
			status="misc error";
		}
		
		dBconn.closeDB();

		
    // tell client that everything is fine
    Unidatoolkit.sendStandardAnswer(status,response);
	}
}	