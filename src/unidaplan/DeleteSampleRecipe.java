package unidaplan;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DeleteSampleRecipe extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	@Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		int stringkey = 0;
		Authentificator authentificator = new Authentificator();
		int userID = authentificator.GetUserID(request,response);
	   	String privilege = "n";
		request.setCharacterEncoding("utf-8");
	    String status = "ok";
	    
		PreparedStatement pStmt = null; 	// Declare variables
		int recipeID;
	 	DBconnection dBconn = new DBconnection(); // New connection to the database
	 	
		// get Parameter for id
		try{
			recipeID = Integer.parseInt(request.getParameter("id")); 
		} catch (Exception e1) {
			recipeID = -1;
			System.err.print("DeleteSampleRecipe: no recipe ID given!");
			status="error: no sample ID";
		}
	 	
		
	    try {
		 	dBconn.startDB();
		 	if (recipeID>0){		
		 		 // Check privileges
			    pStmt = dBconn.conn.prepareStatement( 	
							"SELECT getSampleRecipeRights(vuserid := ?, vsamplerecipe := ? )");
				pStmt.setInt(1,userID);
				pStmt.setInt(2,recipeID);
				privilege = dBconn.getSingleStringValue(pStmt);
				pStmt.close();
				
				if (privilege.equals("w")){
					
					// get stringkey for the name
				    pStmt = dBconn.conn.prepareStatement(	
					        	"SELECT name FROM samplerecipes WHERE id = ?");
					pStmt.setInt(1,recipeID);
					stringkey = dBconn.getSingleIntValue(pStmt);
					pStmt.close();
							
			 		
					// delete the samplerecipe
			        pStmt = dBconn.conn.prepareStatement(	
			        			"DELETE FROM samplerecipes WHERE id = ?");
					pStmt.setInt(1,recipeID);
					pStmt.executeUpdate();
					pStmt.close();
					
					// delete the name
			        pStmt = dBconn.conn.prepareStatement(	
			        			"DELETE FROM string_key_table WHERE id = ?");
					pStmt.setInt(1,stringkey);
					pStmt.executeUpdate();
					pStmt.close();
					
				} else {
					response.setStatus(401);
				}
		 	}
	    } catch (SQLException eS) {
			System.err.println("DeleteSampleRecipe: SQL Error");
			status="error: SQL error";
			eS.printStackTrace();
		} catch (Exception e) {
			System.err.println("DeleteSampleRecipe: Some Error, probably JSON");
			status="error: JSON error";
			e.printStackTrace();
		} finally {
		try{	
	         
	    	   if (dBconn.conn != null) { 
	    		   dBconn.closeDB();  // close the database 
	    	   }
	        } catch (Exception e) {
				status="error: error closing the database";
				System.err.println("DeleteSampleRecipe: Some Error closing the database");
				e.printStackTrace();
		   	}
        }
	    Unidatoolkit.sendStandardAnswer(status, response);

		
	}


}
