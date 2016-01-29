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

	public class AddSampletypePGParameters extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doPost(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {
		
		Authentificator authentificator = new Authentificator();
		String status="ok";
		int userID=authentificator.GetUserID(request,response);
	    request.setCharacterEncoding("utf-8");
	    String in = request.getReader().readLine();
	    JSONObject  jsonIn = null;	    
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
			 parameterGrpID=jsonIn.getInt("parametergroupid");
     		 ids=jsonIn.getJSONArray("parameterids");
		} catch (JSONException e) {
			System.err.println("AddSampletypePGParameters: Error parsing ID-Field");
			status = "Error parsing ID-Field";
			response.setStatus(404);
		}

	    
	    
	    // add Parameters to the parametergroup
		try {	
		    // Initialize Database
			DBconnection dBconn=new DBconnection();
		    PreparedStatement pStmt = null;
		    dBconn.startDB();	
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
			}
//			pStmt.executeBatch();
			pStmt.close();
			dBconn.closeDB();

			
		} catch (SQLException e) {
			System.err.println("AddSampletypePGParameters: Problems with SQL query");
			status = "SQL Error";
		} catch (Exception e) {
			System.err.println("AddSampletypePGParameters: Strange Problems");
			status = "Misc Error (line70)";
		}

		
    // tell client that everything is fine
	Unidatoolkit.sendStandardAnswer(status, response);
	}
}	