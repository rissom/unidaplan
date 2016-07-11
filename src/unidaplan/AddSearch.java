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

	public class AddSearch extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doPost(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {		
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
		String status="ok";
		int searchID=0;
	    request.setCharacterEncoding("utf-8");
	    String in = request.getReader().readLine();
	    PreparedStatement pStmt = null;
	    JSONObject  jsonIn = null;
	    
	    try {
			jsonIn = new JSONObject(in);
		} catch (JSONException e) {
			System.err.println("AddSearch: Input is not valid JSON");
		}
	    

	    
	    int stringKeyName=0;

	    
	    // generate strings for the name and the unit
	    try {	
		 	DBconnection dBconn=new DBconnection();
		    dBconn.startDB();	   
		    
		    //check if admin
	    	int admins=1;
			if (userID>0 && Unidatoolkit.isMemberOfGroup(userID,admins, dBconn)){
				
			
				if (jsonIn.has("name")){
					JSONObject name = jsonIn.getJSONObject("name");
					String [] names = JSONObject.getNames(name);
					stringKeyName=dBconn.createNewStringKey(name.getString(names[0]));
					for (int i = 0; i < names.length; i++){
						dBconn.addString(stringKeyName,names[i],name.getString(names[i]));
					}
				}else{
					System.err.println("no name exists");
					response.setStatus(404);
				}
			
			} else {
				response.setStatus(401);
			}

			pStmt= dBconn.conn.prepareStatement( 			
					"INSERT INTO searches (Name,lastChange,operation,owner,type) VALUES(?,NOW(),true,?,1) "
				   +"RETURNING ID");
		   	pStmt.setInt(1, stringKeyName);
		   	pStmt.setInt(2, userID);
			searchID = dBconn.getSingleIntValue(pStmt);

		} catch (SQLException e) {
			System.err.println("AddSearch: Problems with SQL query");
			response.setStatus(404);
		} catch (JSONException e) {
			System.err.println("AddSearch: Error JSON-Error");
		} catch (Exception e) {
			System.err.println("AddSearch: Error");
		}					
	    

    // tell client the new id and that everything is fine
	Unidatoolkit.returnID(searchID, status, response);
	}
}	