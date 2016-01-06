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

	public class UpdateSTParameter extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doPut(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {		
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
	    request.setCharacterEncoding("utf-8");
	    String in = request.getReader().readLine();
	    String status = "ok";
	    String language = "";
	    String value ="";

	    JSONObject  jsonIn = null;	
	    int parameterID = -1;
	    
    
	    try {
			  jsonIn = new JSONObject(in);
		} catch (JSONException e) {
			System.err.println("UpdateSTParameter: Input is not valid JSON");
		}
		

	    JSONObject newName=null; // get parameters
	    
	    try {
			 parameterID=jsonIn.getInt("parameterid");
		} catch (JSONException e) {
			System.err.println("UpdateSTParameter: Error parsing ID-Field or comment");
			response.setStatus(404);
		}
	    
	 	DBconnection dBconn=new DBconnection(); // initialize database
	    PreparedStatement pStmt = null;
	    
		if (jsonIn.has("name")){
		    try{
				 newName=jsonIn.getJSONObject("name");
				 language=JSONObject.getNames(newName)[0];
				 value=newName.getString(language);
			} catch (JSONException e) {
				System.err.println("UpdateSTParameter: Error parsing ID-Field or comment");
				response.setStatus(404);
			}

			try {
			    dBconn.startDB();	   
				// find the stringkey
				pStmt=dBconn.conn.prepareStatement(
						"SELECT stringkeyname FROM ot_parameters WHERE id=?");
				pStmt.setInt(1,parameterID);
				int stringKey=dBconn.getSingleIntValue(pStmt);
				pStmt.close();
				
				dBconn.addString(stringKey, language, value);
				
			} catch (SQLException e) {
				System.err.println("UpdateSTParameter: Problems with SQL query");
				status="SQL error";
			} catch (Exception e) {
				System.err.println("UpdateSTParameter: some error occured");
				status="misc error";
			}
		}
		
		
		
		if (jsonIn.has("compulsory")){
			try {
			    dBconn.startDB();	   
				Boolean compulsory=jsonIn.getBoolean("compulsory");
				pStmt=dBconn.conn.prepareStatement(
						"UPDATE ot_parameters SET (compulsory,lastuser)=(?,?) WHERE id=?");
				pStmt.setBoolean(1, compulsory);
				pStmt.setInt(2,userID);
				pStmt.setInt(3,parameterID);				
				pStmt.executeUpdate();
				pStmt.close();	
			} catch (SQLException e){
				System.err.println("UpdateSTParameter: SQL error reading compulsory field");
				status="SQL error, compulsory field";
			}catch(JSONException e) {
				System.err.println("UpdateSTParameter: JSON error reading compulsory field");
				status="JSON error, compulsory field";
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
		
		if (jsonIn.has("hidden")){
			try {
			    dBconn.startDB();	   
				boolean hidden=jsonIn.getBoolean("hidden");
				pStmt=dBconn.conn.prepareStatement(
						"UPDATE ot_parameters SET (hidden,lastuser)=(?,?) WHERE id=?");
				pStmt.setBoolean(1, hidden);
				pStmt.setInt(3,userID);
				pStmt.setInt(3,parameterID);
				pStmt.executeUpdate();
				pStmt.close();	
			} catch (SQLException e){
				System.err.println("UpdateSTParameter: SQL error reading hidden field");
				status="SQL error, hidden field";
			}catch(JSONException e) {
				System.err.println("UpdateSTParameter: JSON error reading hidden field");
				status="JSON error, hidden field";
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

			dBconn.closeDB();

	    // tell client that everything is fine
	    Unidatoolkit.sendStandardAnswer(status,response);
	}
}	