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

	public class UpdateObjectOutputParamSearch extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doPut(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {		
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
	    request.setCharacterEncoding("utf-8");
	    String in = request.getReader().readLine();
	    String status = "ok";
	    String privilege = "n";
	    JSONObject  jsonIn = null;	
	    int searchID = -1;

	    try {
	    	jsonIn = new JSONObject(in);
		} catch (JSONException e) {
			System.err.println("UpdateParameterSampleSearch: Input is not valid JSON");
		}
		
	    JSONArray newParameters=null; // get parameters

	    try {
	    	searchID=jsonIn.getInt("searchid");
		} catch (JSONException e) {
			System.err.println("UpdateParameterSampleSearch: Error parsing ID-Field or comment");
			response.setStatus(404);
		}
	    
	 	DBconnection dBconn=new DBconnection(); // initialize database
	    PreparedStatement pStmt = null;
	    
		if (jsonIn.has("parameters")){
		    try{
		    	newParameters=jsonIn.getJSONArray("parameters");
			} catch (JSONException e) {
				System.err.println("UpdateParameterSampleSearch: Error parsing ID-Field or comment");
				response.setStatus(404);
			}
		   
			try {
				// delete old searchobjects
			    dBconn.startDB();
			    
			    // Check privileges
			    pStmt = dBconn.conn.prepareStatement( 	
						"SELECT getSearchRights(vuserid:=?,vsearchid:=?)");
				pStmt.setInt(1,userID);
				pStmt.setInt(2,searchID);
				privilege = dBconn.getSingleStringValue(pStmt);
				pStmt.close();
							
				if (privilege.equals("w")){
			    
					pStmt=dBconn.conn.prepareStatement("DELETE FROM searchobject WHERE search=?");
					pStmt.setInt(1,searchID);
					pStmt.executeUpdate();
					pStmt.close();
	
					// put new searchobjects
					pStmt=dBconn.conn.prepareStatement(
							"INSERT INTO searchobject (search,otparameter,lastchange,lastuser) "
							+"VALUES (?,?,NOW(),?)");
					for (int i=0;i<newParameters.length();i++){
						pStmt.setInt(1,searchID);
						pStmt.setInt(2,newParameters.getInt(i));
						pStmt.setInt(3,userID);
//						System.out.println(pStmt.toString());
						pStmt.addBatch();
					}
					pStmt.executeBatch();
					pStmt.close();
				} else {
					response.setStatus(401);
				}
			} catch (JSONException e) {
				System.err.println("UpdateParameterSampleSearch: Error parsing ID-Field or comment");
				response.setStatus(404);
			} catch (SQLException e) {
				System.err.println("UpdateParameterSampleSearch: SQL error");
				response.setStatus(404);
			} catch (Exception e) {
				System.err.println("UpdateParameterSampleSearch: misc error");
				response.setStatus(404);
			}
		}
		dBconn.closeDB();

	    // tell client that everything is fine
	    Unidatoolkit.sendStandardAnswer(status,response);
	}
}	