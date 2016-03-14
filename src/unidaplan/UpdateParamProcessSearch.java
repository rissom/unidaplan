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

	public class UpdateParamProcessSearch extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doPost(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {		
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
	    request.setCharacterEncoding("utf-8");
	    String in = request.getReader().readLine();
	    String status = "ok";

	    JSONObject jsonIn = null;	
	    int searchID = -1;
	    int type = -1;
	    JSONArray otparameter = null;
	    
    
	    try {
			 System.out.println("in:"+in);
			 jsonIn = new JSONObject(in);
			 System.out.println("jsonIn:"+jsonIn.toString());
	         searchID=jsonIn.getInt("searchid");
			 otparameter=jsonIn.getJSONArray("pparameter");
		} catch (JSONException e) {
			System.err.println("UpdateParamProcessSearch: Error parsing ID-Field or comment");
			response.setStatus(404);
		}
	    
	 	DBconnection dBconn=new DBconnection(); // initialize database
	    PreparedStatement pStmt = null;
	    
		
	    try {  
		    dBconn.startDB();
	    	// get basic search data (id,name,owner,operation)
			pStmt= dBconn.conn.prepareStatement( 	
			    "SELECT type FROM searches WHERE id=?");
			pStmt.setInt(1, searchID);
			type=dBconn.getSingleIntValue(pStmt);
			pStmt.close();
			
			// get the searchparameters according to searchtype
			String table="";
			
			switch (type){
				case 1:   //Object scearch
						  table ="searchobject";
						  break;
				case 2:   // Process search
						  table ="searchprocess";
						  break;
				default : // samplespecific process parameter
						  table ="searchpo";
						  break;
			}		
			
			pStmt= dBconn.conn.prepareStatement( 	
				    "INSERT INTO "+table+" (search,otparameter,lastchange,lastuser) "
				    +"VALUES (?,?,NOW(),?)");
			
			for (int i=0; i<otparameter.length();i++){
				pStmt.setInt(1, searchID);
				pStmt.setInt(2, otparameter.getInt(i));
				pStmt.setInt(3, userID);
				pStmt.addBatch();
			}
			pStmt.executeBatch();
			pStmt.close();
			
		} catch (SQLException e) {
			System.err.println("UpdateParamProcessSearch: Problems with SQL query");
			status="SQL error";
		} catch (Exception e) {
			System.err.println("UpdateParamProcessSearch: some error occured");
			status="misc error";
		}
		
		dBconn.closeDB();
		
    // tell client that everything is fine
    Unidatoolkit.sendStandardAnswer(status,response);
	}
}	