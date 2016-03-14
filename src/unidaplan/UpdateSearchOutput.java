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

	public class UpdateSearchOutput extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doPut(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {
		
		Authentificator authentificator = new Authentificator();
		String status="ok";
		String type="";
		int userID=authentificator.GetUserID(request,response);
	    request.setCharacterEncoding("utf-8");
	    String in = request.getReader().readLine();
	    JSONObject  jsonIn = null;
	    try {
			response.setContentType("application/json");
		    response.setCharacterEncoding("utf-8");
		    
		    // get id, type and search
			jsonIn = new JSONObject(in);
		    int searchID=0;
		    JSONArray output=null;
			searchID=jsonIn.getInt("searchid");
			output  =jsonIn.getJSONArray("output");
			type    =jsonIn.getString("type");

		    // connect to DB
			DBconnection dBconn=new DBconnection();
		    PreparedStatement pStmt = null;
		    dBconn.startDB();
		    
		    pStmt = dBconn.conn.prepareStatement("SELECT getSearchRights(vuserid:=?,vsearchid:=?)");
		    pStmt.setInt(1,userID);
		    pStmt.setInt(2,searchID);
		    String rights=dBconn.getSingleStringValue(pStmt);
		    
		    if (rights.equals("w")){
		    	
				String table="";
				String column="";
				
				switch (type) {
					case "o" : table="osearchoutput"; column="otparameter"; break;
					case "p" : table="psearchoutput"; column="pparameter"; break;
					case "po" : table="posearchoutput"; column="poparameter";
				}
				pStmt= dBconn.conn.prepareStatement( 			
						 "DELETE FROM "+table+" WHERE search=?");
				pStmt.setInt(1, searchID);
				pStmt.executeUpdate();
				pStmt.close();
				int parameter;	
			
				pStmt= dBconn.conn.prepareStatement( 		
				"INSERT INTO "+table+" (search,position,"+column+",lastuser) VALUES (?,?,?,?)");
				for (int i=0;i<output.length();i++){
					parameter=output.getInt(i);
					pStmt.setInt(1,searchID);
					pStmt.setInt(2,i+1);
					pStmt.setInt(3,parameter);
					pStmt.setInt(4,userID);
					pStmt.addBatch();
				}
				pStmt.executeBatch();
				pStmt.close();
			} else {
				status="not authorized";
				response.setStatus(401);
			}
			dBconn.closeDB();

		} catch (JSONException e) {
			System.err.println("UpdateParameterSampleSearch: Input is not valid JSON");
			response.setStatus(404);
			e.printStackTrace();
		} catch (SQLException e) {
			System.err.println("UpdateParameterSampleSearch: Even More Problems with SQL query");
			status = "SQL Error";
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("UpdateParameterSampleSearch: More Strange Problems");
			status = "Misc. Error";
			e.printStackTrace();
		}
	    
	    // tell client that everything is fine
	    Unidatoolkit.sendStandardAnswer(status, response);
	}
}	