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

	public class AddSample extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doGet(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {		
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
		int id = -1;
		String status="ok";
	    request.setCharacterEncoding("utf-8");
		response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    
	    // get the sampletypeID
	    int sampletypeID=-1;
	    try {
			 sampletypeID=Integer.parseInt(request.getParameter("sampletypeid")); 
		} catch (Exception e) {
			System.err.println("AddSample: Error parsing type ID");
			response.setStatus(404);
		}

	    
	    // create entry in the database	    

		try {
		 	DBconnection dBconn=new DBconnection();
		    dBconn.startDB();	   
		    PreparedStatement pstmt = null;
			pstmt= dBconn.conn.prepareStatement( 			
					 "INSERT INTO samples values(default, ?, ?, NOW(), NOW(),?) RETURNING id");
		   	pstmt.setInt(1, sampletypeID);
		   	pstmt.setInt(2, userID);
		   	pstmt.setInt(3, userID);
		   	JSONObject answer=dBconn.jsonObjectFromPreparedStmt(pstmt);
		   	pstmt.close();
			id= answer.getInt("id");
		
			// find the current maximum of sample name parameters
			pstmt= dBconn.conn.prepareStatement( 	
			"SELECT id FROM samplenames WHERE typeid=? ORDER BY name DESC LIMIT 1");
		   	pstmt.setInt(1, sampletypeID);
			int lastSampleID= dBconn.getSingleIntValue(pstmt);
			pstmt.close();
			
			// Liste mit Titelparametern
			pstmt= dBconn.conn.prepareStatement( 	
			"SELECT ot_parameters.id,idata.value FROM ot_parameters " 
			+"JOIN o_integer_data idata ON idata.ot_parameter_id=ot_parameters.id "
			+"WHERE ID_Field=true AND idata.objectid=? ORDER BY pos DESC");
		   	pstmt.setInt(1, lastSampleID);
		   	JSONArray lastTitleParameters=dBconn.jsonArrayFromPreparedStmt(pstmt);
			pstmt.close();
				
	   	
			// Titelparameter schreiben
			int increment=1;
	        for (int i=0; i<lastTitleParameters.length();i++){   
	        	JSONObject parameter=(JSONObject) lastTitleParameters.get(i);
	        	pstmt= dBconn.conn.prepareStatement("INSERT INTO o_integer_data values(default, ?, ?, ?, NOW());");
	        	pstmt.setInt(1, id);
	        	pstmt.setInt(2, parameter.getInt("id"));
	        	pstmt.setInt(3, parameter.getInt("value")+increment);
	        	pstmt.executeUpdate();
	        	pstmt.close();
	        	increment=0;
	        }
		dBconn.closeDB();
		
		
	} catch (SQLException e) {
		System.err.println("AddSample: Problems with SQL query");
		status="SQL error";
	} catch (JSONException e){
		System.err.println("AddSample: Problems creating JSON");
		status="JSON error";
	} catch (Exception e) {
		System.err.println("AddSample: Strange Problems");
		status="error";
	}
		// Preset sample name parameters
		
		
    // tell client that everything is fine
    Unidatoolkit.sendStandardAnswer(status, response);
	}
}	