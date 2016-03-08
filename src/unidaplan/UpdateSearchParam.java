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
import org.json.JSONObject;

	public class UpdateSearchParam extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doPost(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {		
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
	    request.setCharacterEncoding("utf-8");
	    String in = request.getReader().readLine();
	    String status = "ok";
	    String table = "";
	    String col = "";
	    


	    JSONObject jsonIn = null;	
	    int searchID = -1;
	    JSONArray parameter = null;
	    
    
	    try {
			 jsonIn = new JSONObject(in);
	         searchID=jsonIn.getInt("searchid");
	         parameter = jsonIn.getJSONArray("parameter");
		} catch (JSONException e) {
			System.err.println("UpdateSearchParam: Error parsing ID-Field or comment");
			response.setStatus(404);
			e.printStackTrace();
		}
	    
	 	DBconnection dBconn=new DBconnection(); // initialize database
	    PreparedStatement pStmt = null;
	    
		
	    try {  
		    dBconn.startDB();
	    	// get basic search data (id,name,owner,operation)

			
			// get the searchparameters according to searchtype
			for (int i=0; i<parameter.length();i++){
				switch (jsonIn.getString("type")){
					case "o" : table="searchobject";  col="otparameter";break;
					case "p" : table="searchprocess"; col="pparameter"; break;
					case "op" : table="searchpo";  	  col="poparameter"; break;
				}
				pStmt= dBconn.conn.prepareStatement("INSERT INTO "+table+
						" (search,"+col+",lastchange,lastuser) VALUES (?,?,NOW(),?)");
				pStmt.setInt(1, searchID);
				pStmt.setInt(2, parameter.getInt(i));
				pStmt.setInt(3, userID);
				pStmt.executeUpdate();
				pStmt.close();
			}
			
		} catch (SQLException e) {
			System.err.println("UpdateSearchParam: Problems with SQL query");
			status="SQL error";
		} catch (Exception e) {
			System.err.println("UpdateSearchParam: some error occured");
			e.printStackTrace();
			status="misc error";
		}
		
		dBconn.closeDB();
		
    // tell client that everything is fine
    Unidatoolkit.sendStandardAnswer(status,response);
	}
}	