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

	public class AddSampletypeParameters extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doPost(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {
		
		Authentificator authentificator = new Authentificator();
	    PreparedStatement pStmt = null;
		String status="ok";
		int userID = authentificator.GetUserID(request,response);
		int sampleTypeID = 0;
	    JSONObject jsonIn = null;	    

		
	    request.setCharacterEncoding("utf-8");
	    String in = request.getReader().readLine();
	    try {
			  jsonIn = new JSONObject(in);
		} catch (JSONException e) {
			System.err.println("AddSampletypePGParameters: Input is not valid JSON");
		}
		response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    
	    // get the id
	    int parameterGrpID=0;
	    JSONArray ids=null;

	    try {
	    	 if (jsonIn.has("parametergroupid")){
	    		 parameterGrpID = jsonIn.getInt("parametergroupid");
	    	 } else {
	    		 sampleTypeID = jsonIn.getInt("sampletypeid");
	    	 }
     		 ids=jsonIn.getJSONArray("parameterids");
		} catch (JSONException e) {
			System.err.println("AddSampletypePGParameters: Error parsing ID-Field");
			status = "Error parsing ID-Field";
			response.setStatus(404);
		}

	    
	    
	    // add Parameters 
		try {	
		    // Initialize Database
			DBconnection dBconn = new DBconnection();
		    dBconn.startDB();
		    
		    //check if admin
	    	int admins=1;
			if (userID>0 && Unidatoolkit.isMemberOfGroup(userID,admins, dBconn)){
		    
						if (parameterGrpID>0){ 	    // add Parameters to the parametergroup
					for (int i=0; i<ids.length();i++){
						pStmt= dBconn.conn.prepareStatement( 			
								 "INSERT INTO ot_parameters (objecttypesID,parametergroup,compulsory,id_field,hidden,pos,definition,lastuser) "
								 + " VALUES((SELECT ot_id FROM ot_parametergrps WHERE ot_parametergrps.id=?),?,False,False,False, "
								 + "(SELECT COALESCE ((SELECT max(p2.pos)+1 FROM ot_parameters p2 WHERE p2.parametergroup=?),1)), "
								 + "?,?)");
					   	pStmt.setInt(1, parameterGrpID);
					   	pStmt.setInt(2, parameterGrpID);
					   	pStmt.setInt(3, parameterGrpID);
					   	pStmt.setInt(4, ids.getInt(i));
					   	pStmt.setInt(5, userID);
		//				pStmt.addBatch();  // Does not work. I don't know why.
					   	pStmt.executeUpdate();
						pStmt.close();
					}
				} else {   // add Titleparameters 
					for (int i=0; i<ids.length(); i++){
						pStmt= dBconn.conn.prepareStatement( 			
							 "INSERT INTO ot_parameters (objecttypesID,compulsory,id_field,hidden,pos,definition,lastuser) "
							 + " VALUES(?,True,True,False,("
							 + "  SELECT COALESCE(max(pos)+1,1) FROM ot_parameters WHERE objecttypesID=? AND parametergroup IS null "
							 + "),?,?)");
					   	pStmt.setInt(1, sampleTypeID);
					   	pStmt.setInt(2, sampleTypeID);
					   	pStmt.setInt(3, ids.getInt(i));
					   	pStmt.setInt(4, userID);
					   	pStmt.executeUpdate();
						pStmt.close();
					}
				}
				pStmt.close();
			} else{
				response.setStatus(401);
			}
			dBconn.closeDB();

			
		} catch (SQLException e) {
			System.err.println("AddSampletypePGParameters: Problems with SQL query");
			status = "SQL Error";
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("AddSampletypePGParameters: Strange Problems");
			status = "Misc Error (line70)";
		}

		
    // tell client that everything is fine
	Unidatoolkit.sendStandardAnswer(status, response);
	}
}	