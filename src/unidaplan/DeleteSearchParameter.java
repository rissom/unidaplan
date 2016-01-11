package unidaplan;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DeleteSearchParameter extends HttpServlet {
	private static final long serialVersionUID = 1L;

	

    @Override
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
		userID=userID+1; // REMOVE ME!!!
		userID=userID-1; // REMOVE ME!!!
		request.setCharacterEncoding("utf-8");
	    String status="ok";
		int searchID=-1;
		int parameterID=-1;
		int type=-1;

	 	
		// get the id
		try{
			 searchID=Integer.parseInt(request.getParameter("searchid"));
			 parameterID=Integer.parseInt(request.getParameter("parameterid"));
		}
		catch (Exception e1) {
			searchID=-1;
			System.err.print("DeleteSearch: no search ID given!");
			status="error: no search ID";
			response.setStatus(404);
		}
	 	
	 	DBconnection dBconn=new DBconnection(); // initialize database
	    PreparedStatement pStmt = null;
	    
	    
	    try {
		 
		    dBconn.startDB();
	    	// get basic search data (id,name,owner,operation)
			pStmt= dBconn.conn.prepareStatement( 	
			    "SELECT type FROM searches WHERE id=?");
			pStmt.setInt(1, searchID);
			type=dBconn.getSingleIntValue(pStmt);
			pStmt.close();
			
			// get the searchparameters according to searchtype
			String table="";

			switch (type){
				case 1:   //Object scearch
						  table ="searchobject";
						  break;
				case 2:   // Process search
						  table ="searchprocess";
						  break;
				default : // samplespecific parameter search
						  table ="searchpo";
						  break;
			}
	    	
			PreparedStatement pstmt = null; 	// Declare variables
		 	DBconnection DBconn=new DBconnection(); // New connection to the database
		 	DBconn.startDB();
		 	if (parameterID>0){			
				// delete the search
		        pstmt = DBconn.conn.prepareStatement(	
		        	"DELETE FROM "+table+" WHERE id=?");
				pstmt.setInt(1,parameterID);
				pstmt.executeUpdate();
				pstmt.close();
			}
 		   DBconn.closeDB();  // close the database 

	    } catch (SQLException eS) {
			System.err.println("DeleteSearch: SQL Error");
			status="error: SQL error";
			response.setStatus(404);
		} catch (Exception e) {
			System.err.println("DeleteSearch: Some Error, probably JSON");
			status="error: JSON error";
			response.setStatus(404);
		}
	    
	    
	    Unidatoolkit.sendStandardAnswer(status, response);

	}


}
