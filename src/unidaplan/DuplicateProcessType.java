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

public class DuplicateProcessType extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DuplicateProcessType() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
		userID=userID+1;
		userID=userID-1;
		request.setCharacterEncoding("utf-8");
	    response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    PrintWriter out = response.getWriter(); 
	    String status="ok";
	    
		PreparedStatement pStmt = null; 	// Declare variables
		int processTypeID=0;
 		int nameKey=0;
 		int descKey=0;
 		int id = 0;
	 	DBconnection dBconn=new DBconnection(); // New connection to the database
	 	dBconn.startDB();
	 	
		// get Parameter for id
		try{
			 processTypeID=Integer.parseInt(request.getParameter("id")); }
		catch (Exception e1) {
			processTypeID=-1;
			System.err.print("Delete Processtype: no process ID given!");
			status="error: no process ID";
		}
		
		//copy the strings
		 try {
			 	if (processTypeID>0){		
			        pStmt = dBconn.conn.prepareStatement(	
			        	"SELECT language,value FROM Stringtable WHERE string_key = (SELECT name FROM processtypes WHERE id=?)");
					pStmt.setInt(1,processTypeID);
					JSONArray nameStrings = dBconn.jsonArrayFromPreparedStmt(pStmt);
					pStmt.close();
			        pStmt = dBconn.conn.prepareStatement(	
			        	"SELECT language,value FROM Stringtable WHERE string_key = (SELECT description FROM processtypes WHERE id=?)");
					pStmt.setInt(1,processTypeID);
					JSONArray descStrings = dBconn.jsonArrayFromPreparedStmt(pStmt);
					pStmt.close();
					if (nameStrings.length()>0){
						pStmt = dBconn.conn.prepareStatement(	
					       	"INSERT INTO string_key_table (description) SELECT description FROM string_key_table WHERE id=(SELECT name FROM processtypes WHERE id=?) RETURNING ID");
						pStmt.setInt(1,processTypeID);
						nameKey = dBconn.getSingleIntValue(pStmt);
						pStmt.close();
						for (int i=0; i<nameStrings.length();i++){
							
							pStmt = dBconn.conn.prepareStatement(	
						        	"INSERT INTO stringtable (string_key,language,value,lastUser) VALUES (?,?,?,?)");
							pStmt.setInt(1,nameKey);
							pStmt.setString(2,nameStrings.getJSONObject(i).getString("language"));
							pStmt.setString(3,"copy of "+nameStrings.getJSONObject(i).getString("value"));
							pStmt.setInt(4,userID);
							pStmt.executeUpdate();
							pStmt.close();
						}
					}
					if (descStrings.length()>0){
						pStmt = dBconn.conn.prepareStatement(	
					       	"INSERT INTO string_key_table (description) SELECT description FROM string_key_table WHERE id=(SELECT name FROM processtypes WHERE id=?) RETURNING ID");
						pStmt.setInt(1,processTypeID);
						descKey = dBconn.getSingleIntValue(pStmt);
						pStmt.close();
						for (int i=0; i<nameStrings.length();i++){
							
							pStmt = dBconn.conn.prepareStatement(	
						        	"INSERT INTO stringtable (string_key,language,value,lastUser) VALUES (?,?,?,?)");
							pStmt.setInt(1,descKey);
							pStmt.setString(2,descStrings.getJSONObject(i).getString("language"));
							pStmt.setString(3,descStrings.getJSONObject(i).getString("value"));
							pStmt.setInt(4,userID);
							System.out.println();
							System.out.println(pStmt.toString());
							pStmt.executeUpdate();
							pStmt.close();
						}
					}

				}
		    } catch (SQLException eS) {
				System.err.println("Delete Process: SQL Error");
				status="error: SQL error";
				eS.printStackTrace();
			} catch (Exception e) {
				System.err.println("Delete Sample Type: Some Error, probably JSON");
				status="error: JSON error";
				e.printStackTrace();
			}
		 
		 
		 
		
	 	
		 try {
			 	//copy the entry
			 	if (processTypeID>0){			
					// get string_key_table references for later deletion
			        pStmt = dBconn.conn.prepareStatement(	
			        	"INSERT INTO processtypes(position,ptgroup,name,description,lastUser) "
			        	+"SELECT  position,ptgroup,?,?,? "
			        	+"FROM processtypes WHERE id=? RETURNING id");
					pStmt.setInt(1,nameKey);
					pStmt.setInt(2,descKey);
					pStmt.setInt(3,userID);
					pStmt.setInt(4,processTypeID);
					id = dBconn.getSingleIntValue(pStmt);
					pStmt.close();
				}
		    } catch (SQLException eS) {
				System.err.println("Delete Process: SQL Error");
				status="error: SQL error";
				eS.printStackTrace();
			} catch (Exception e) {
				System.err.println("Delete Sample Type: Some Error, probably JSON");
				status="error: JSON error";
				e.printStackTrace();
			}

	  
	    
	    finally {
		try{	
	         
	    	   if (dBconn.conn != null) { 
	    		   dBconn.closeDB();  // close the database 
	    	   }
	        } catch (Exception e) {
				status="error: error closing the database";
				System.err.println("Delete Processtype: Some Error closing the database");
				e.printStackTrace();
		   	}
        }
		try {
			JSONObject answer=new JSONObject();
			answer.put("status", status);
			answer.put("id", id);
		    out.println(answer.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	

		
	}


}
