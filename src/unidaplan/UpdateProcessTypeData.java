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

	public class UpdateProcessTypeData extends HttpServlet {
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
			response.setStatus(404);
			System.err.println("UpdateProcessTypeData: Input is not valid JSON");
		}
		response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    
	    // get the id
	    int processTypeID = 0;
	    int stringkey = -1;
	    String newValue = null;
	    String lang = null;
		DBconnection dBconn = new DBconnection();
	    PreparedStatement pStmt = null;

	    try {
		    dBconn.startDB();
	  
	    
		    if (dBconn.isAdmin(userID)){
	    
		 
				processTypeID = jsonIn.getInt("processtypeid");
				String field = jsonIn.getString("field");
				lang = jsonIn.getString("lang");
				newValue = jsonIn.getString("newvalue");
				
				pStmt = dBconn.conn.prepareStatement( 			
						   "SELECT "
						 + "  name,"
						 + "  description "
						 + "FROM processtypes "
						 + "WHERE processtypes.id = ?");
				pStmt.setInt(1, processTypeID);
				JSONObject pt = dBconn.jsonObjectFromPreparedStmt(pStmt);
				
				if (field.equals("name")) { 
					stringkey = pt.getInt("name");
	                dBconn.addString(stringkey, lang, newValue);
				} 
				if (field.equalsIgnoreCase("description")){
                    if (pt.has("description")){
                        // if a stringkey exists: try to get id of stringtable field.
                        stringkey = pt.getInt("description");
                        dBconn.addString(stringkey, lang, newValue);
                    }else{
                        // We have no stringkey
                        stringkey = dBconn.createNewStringKey(newValue);
                        dBconn.addString(stringkey, lang, newValue);
                        pStmt = dBconn.conn.prepareStatement(            
                                 "UPDATE processtypes SET description = ? WHERE id = ?");
                        pStmt.setInt(1, stringkey);
                        pStmt.setInt(2, processTypeID);
                        pStmt.executeUpdate();
                        pStmt.close();
                    }      
                }
				dBconn.closeDB();
			
		    } else {
		    	response.setStatus(401);
		    }
		    
		} catch (SQLException e) {
			System.err.println("UpdateProcessTypeData: More Problems with SQL query");
			status = "SQL Error";
		} catch (JSONException e) {
			System.err.println("UpdateProcessTypeData: JSON Error");
			status = "Error parsing ID-Field";
			response.setStatus(404);
		}   catch (Exception e) {
			System.err.println("UpdateProcessTypeData: More Strange Problems");
			status = "Misc. Error";
		}
			
		    	    
	    // tell client that everything is fine
	    Unidatoolkit.sendStandardAnswer(status, response);
	}
}	