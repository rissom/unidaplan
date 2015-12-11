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
		int userID=authentificator.GetUserID(request,response);
	    request.setCharacterEncoding("utf-8");
	    String in = request.getReader().readLine();
	    JSONObject  jsonIn = null;
	    try {
			jsonIn = new JSONObject(in);

			response.setContentType("application/json");
		    response.setCharacterEncoding("utf-8");
		    
		    // get the id
		    int searchID=0;
		    int searchType=-1;
		    JSONArray output=null;
			DBconnection dBconn=new DBconnection();
		    PreparedStatement pStmt = null;

		    dBconn.startDB();	   
			output=jsonIn.getJSONArray("output");
			searchID=jsonIn.getInt("searchid");

			pStmt= dBconn.conn.prepareStatement( 			
					 "SELECT type FROM searches WHERE id=?");
			pStmt.setInt(1, searchID);
			searchType = dBconn.getSingleIntValue(pStmt);
			System.out.println(pStmt.toString());
			System.out.println("Searchtype:" +searchType);
			pStmt.close();
			
			String table="";
			String col="";

			switch (searchType){
				case 1  : table="osearchoutput";  col="otparameter"; break;
				case 2  : table="psearchoutput";  col="pparameter"; break;
				default : table="posearchoutput"; col="poparameter"; 
			}
			
			pStmt= dBconn.conn.prepareStatement( 			
					 "DELETE FROM "+table+" WHERE search=?");
			pStmt.setInt(1, searchID);
			pStmt.executeUpdate();
			pStmt.close();
			int parameter;	
		
			pStmt= dBconn.conn.prepareStatement( 		
			"INSERT INTO "+table+" (search,position,"+col+",lastuser) VALUES (?,?,?,?)");
			for (int i=0;i<output.length();i++){
				parameter=output.getInt(i);
				pStmt.setInt(1,searchID);
				pStmt.setInt(2,i+1);
				pStmt.setInt(3,parameter);
				pStmt.setInt(4,userID);
				pStmt.addBatch();
				System.out.println(pStmt.toString());
			}
			pStmt.executeBatch();
			pStmt.close();
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