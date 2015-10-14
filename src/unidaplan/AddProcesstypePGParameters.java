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

	public class AddProcesstypePGParameters extends HttpServlet {
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
			System.err.println("AddProcesstypePGParameters: Input is not valid JSON");
		}
		response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    
	    // get the id
	    int parameterGrpID=0;
	    int processTypeID = 0;
	    JSONArray ids=null;

	    try {
			 parameterGrpID=jsonIn.getInt("parametergroupid");
			 processTypeID=jsonIn.getInt("processtypeid");	
     		 ids=jsonIn.getJSONArray("parameterids");
		} catch (JSONException e) {
			System.err.println("AddProcesstypePGParameters: Error parsing ID-Field");
			status = "Error parsing ID-Field";
			response.setStatus(404);
		}

	    
	    // Initialize Database
		DBconnection dBconn=new DBconnection();
	    dBconn.startDB();	
	    PreparedStatement pStmt = null;
	    
	    
	    // add Parameters to the parametergroup
		try {	
			for (int i=0; i<ids.length();i++){
				pStmt= dBconn.conn.prepareStatement( 			
						 "INSERT INTO p_parameters (ProcesstypeID,Parametergroup,compulsory,ID_Field,Hidden,pos,definition,StringKeyName,lastUser)"
						 + " VALUES(?,?,False,False,False,"
						 + "(SELECT max(p2.pos)+1 FROM p_parameters p2 WHERE p2.parametergroup=?),"
						 + "?,(SELECT stringkeyname FROM paramdef WHERE paramdef.id=?),?)");
			   	pStmt.setInt(1, processTypeID);
			   	pStmt.setInt(2, parameterGrpID);
			   	pStmt.setInt(3, parameterGrpID);
			   	pStmt.setInt(4, ids.getInt(i));
			   	pStmt.setInt(5, ids.getInt(i));
			   	pStmt.setInt(6, userID);
//				pStmt.addBatch();  // Does not work. I don't know why.
				pStmt.executeUpdate();
			}
//			pStmt.executeBatch();
			pStmt.close();
		
		} catch (SQLException e) {
			System.err.println("AddProcesstypePGParameters: Problems with SQL query");
			status = "SQL Error";
		} catch (Exception e) {
			System.err.println("AddProcesstypePGParameters: Strange Problems");
			status = "Misc Error (line70)";
		}

		dBconn.closeDB();
		
    // tell client that everything is fine
    PrintWriter out = response.getWriter();
	out.println("{\"status\":\""+status+"\"}");
	}
}	