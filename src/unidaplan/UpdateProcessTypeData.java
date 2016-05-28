package unidaplan;
import java.io.IOException;
import java.io.PrintWriter;
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
	    int processTypeID=0;
	    int id=0;
	    int stringkey=-1;
	    String newValue = null;
	    String lang = null;
		DBconnection dBconn=new DBconnection();
	    PreparedStatement pStmt = null;

	    try {
		    dBconn.startDB();
		    
		    
	    } catch (SQLException e) {
			System.err.println("UpdateProcessTypeData: More Problems with SQL query");
			status = "SQL Error";
		} catch (JSONException e) {
			System.err.println("UpdateProcessTypeData: Error parsing ID-Field");
			status = "Error parsing ID-Field";
			response.setStatus(404);
		}   catch (Exception e) {
			System.err.println("UpdateProcessTypeData: More Strange Problems");
			status = "Misc. Error";
		}
	    
	    
	    if (Unidatoolkit.userHasAdminRights(userID, dBconn)){
	    
		    try{
				processTypeID = jsonIn.getInt("processtypeid");
				String field = jsonIn.getString("field");
				lang=jsonIn.getString("lang");
				newValue=jsonIn.getString("newvalue");
				
				pStmt= dBconn.conn.prepareStatement( 			
						 "SELECT name,description FROM processtypes WHERE processtypes.id=?");
				pStmt.setInt(1,  processTypeID);
				JSONObject pt=dBconn.jsonObjectFromPreparedStmt(pStmt);
				
				if (field.equals("name")) { 
					stringkey=pt.getInt("name");
				} else{
					stringkey=pt.getInt("description");
				}
				pStmt= dBconn.conn.prepareStatement( 			
							 "SELECT st.id FROM stringtable st "
						   + "WHERE st.string_key=? AND st.language=?");
			   	pStmt.setInt(1,stringkey);
			   	pStmt.setString(2, lang);
			   	id=dBconn.getSingleIntValue(pStmt);
			   	pStmt.close();
	
			} catch (SQLException e) {
				System.err.println("UpdateProcessTypeData: More Problems with SQL query");
				status = "SQL Error";
			} catch (JSONException e) {
				System.err.println("UpdateProcessTypeData: Error parsing ID-Field");
				status = "Error parsing ID-Field";
				response.setStatus(404);
			}   catch (Exception e) {
				System.err.println("UpdateProcessTypeData: More Strange Problems");
				status = "Misc. Error";
			}
	
		    try {	   
		    	pStmt= dBconn.conn.prepareStatement( 			
						 "INSERT INTO stringtable VALUES (default, ?, ?,?,NOW(),?)");
			    if (id<1) { // No id, new Entry
				   	pStmt.setInt(1, stringkey);
				   	pStmt.setString(2, lang);
				   	pStmt.setString(3, newValue);
				   	pStmt.setInt(4, userID);
			    } else{			    // If we have an id: update the field
					pStmt= dBconn.conn.prepareStatement( 			
						 "UPDATE stringtable SET (value,lastuser)=(?,?) WHERE id=?");
				   	pStmt.setString(1, newValue);
				   	pStmt.setInt(2, userID);
				   	pStmt.setInt(3, id);
			    }
				pStmt.executeUpdate();
				pStmt.close();
				dBconn.closeDB();
			} catch (SQLException e) {
				System.err.println("UpdateProcessTypeData: Even More Problems with SQL query");
				status = "SQL Error";
			} catch (Exception e) {
				System.err.println("UpdateProcessTypeData: More Strange Problems");
				status = "Misc. Error";
			}
	    } else {
	    	response.setStatus(401);
	    }
		
	    
    // tell client that everything is fine
    PrintWriter out = response.getWriter();
	out.println("{\"status\":\""+status+"\"}");
	}
}	