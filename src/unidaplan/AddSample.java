package unidaplan;
import java.io.IOException;
import java.io.PrintWriter;
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
	  public void doPost(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {
		
	
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
		int id = -1;
	    PreparedStatement pStmt = null;
		String status="ok";
	   	String privilege="n";
		
		
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
	 	DBconnection dBconn=new DBconnection();
	 	
		try {
		    dBconn.startDB();	 
	 	
		 	 // check privilege
		    pStmt = dBconn.conn.prepareStatement( 	
					"SELECT getSampleTypeRights(vuserid:=?,vsampletype:=?)");
			pStmt.setInt(1,userID);
			pStmt.setInt(2,sampletypeID);
			privilege = dBconn.getSingleStringValue(pStmt);
			pStmt.close();

			if (privilege.equals("w")){	 	
  
				pStmt= dBconn.conn.prepareStatement( 			
						 "INSERT INTO samples values(default, ?, ?, NOW(), NOW(),?) RETURNING id");
			   	pStmt.setInt(1, sampletypeID);
			   	pStmt.setInt(2, userID);
			   	pStmt.setInt(3, userID);
				id= dBconn.getSingleIntValue(pStmt);
			   	pStmt.close();
			
					
				// find the current maximum of sample name parameters
				pStmt= dBconn.conn.prepareStatement( 	
				"SELECT id FROM samplenames WHERE typeid=? ORDER BY UPPER(name) DESC LIMIT 1");
			   	pStmt.setInt(1, sampletypeID);
			   	JSONObject answer=dBconn.jsonObjectFromPreparedStmt(pStmt);
			   	int lastSampleID=0;
			   	if (answer.length()>0){
			   		lastSampleID= answer.getInt("id");
			   	} 
				pStmt.close();
				
				// List of titleparameters of type Integer:
				pStmt= dBconn.conn.prepareStatement( 	
				"SELECT ot_parameters.id,idata.value FROM ot_parameters " 
				+"JOIN o_integer_data idata ON idata.ot_parameter_id=ot_parameters.id "
				+"WHERE ID_Field=true AND idata.objectid=? ORDER BY pos DESC");
			   	pStmt.setInt(1, lastSampleID);
			   	JSONArray lastTitleIntParameters=dBconn.jsonArrayFromPreparedStmt(pStmt);
				pStmt.close();
				Boolean intParameterExists=false;
				
				// check if there is any
				if (lastTitleIntParameters.length()>0){
				
			   	
					// copy old integer parameters and increase last integer parameter
					intParameterExists=true;
					int increment=1;
			        for (int i=0; i<lastTitleIntParameters.length();i++){   
			        	JSONObject parameter=(JSONObject) lastTitleIntParameters.get(i);
			        	pStmt= dBconn.conn.prepareStatement("INSERT INTO o_integer_data values(default, ?, ?, ?, NOW());");
			        	pStmt.setInt(1, id);
			        	pStmt.setInt(2, parameter.getInt("id"));
			        	pStmt.setInt(3, parameter.getInt("value")+increment);
			        	pStmt.executeUpdate();
			        	pStmt.close();
			        	increment=0;
			        }
				} 
				
				// List of titleparameters of type String:
				pStmt= dBconn.conn.prepareStatement( 	
				"SELECT ot_parameters.id,sdata.value FROM ot_parameters " 
				+"JOIN o_string_data sdata ON sdata.ot_parameter_id=ot_parameters.id "
				+"WHERE ID_Field=true AND sdata.objectid=? ORDER BY pos DESC");
			   	pStmt.setInt(1, lastSampleID);
			   	JSONArray lastTitleStrParameters=dBconn.jsonArrayFromPreparedStmt(pStmt);
				pStmt.close();
				
				
				// copy old string parameters and increase replace the last string parameter
		        for (int i=0; i<lastTitleStrParameters.length();i++){   
		        	JSONObject parameter=(JSONObject) lastTitleStrParameters.get(i);
		        	pStmt= dBconn.conn.prepareStatement(
		        			"INSERT INTO o_string_data (objectid,ot_parameter_id,value,lastchange,lastuser)"
		        			+ " VALUES(?,?,?,NOW(),?);");
		        	pStmt.setInt(1, id);
		        	pStmt.setInt(2, parameter.getInt("id"));
		        	String value="";
		        	System.out.println("intParameterExists: "+intParameterExists);
		        	System.out.println("i: "+i);
		        	System.out.println("lastTitleStrParameters.length: "+lastTitleStrParameters.length());
		        	if (!intParameterExists && i+1==lastTitleStrParameters.length()){
		        		value="new";
		        	}else{
		        		value=parameter.getString("value");
		        	}
		        	pStmt.setString(3, value);
		        	pStmt.setInt(4, userID);
		        	pStmt.executeUpdate();
		        	pStmt.close();
		        }
			} else {
				response.setStatus(401);
			}
		} catch (SQLException e) {
			System.err.println("AddSample: Problems with SQL query");
			status="SQL error";
			response.setStatus(404);
		} catch (JSONException e){
			e.printStackTrace();
			System.err.println("AddSample: Problems creating JSON");
			status="JSON error";
			response.setStatus(404);
		} catch (Exception e) {
			System.err.println("AddSample: Strange Problems");
			status="error";
			response.setStatus(404);
		} 
        
		dBconn.closeDB();
		
		
    // tell client that everything is fine
	Unidatoolkit.returnID(id, status, response);
	}
}	