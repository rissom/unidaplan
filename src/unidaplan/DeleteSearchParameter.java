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
		request.setCharacterEncoding("utf-8");
		String privilege = "n";
	    String status="ok";
	    String type="";
		String table="";
		int searchID=-1;
		int parameterID=-1;

	 	
		// get the id
		try{
			 searchID=Integer.parseInt(request.getParameter("searchid"));
			 parameterID=Integer.parseInt(request.getParameter("parameterid"));
			 type=request.getParameter("type");
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
		    
			// Check privileges
		    pStmt = dBconn.conn.prepareStatement( 	
					"SELECT getExperimentRights(vuserid:=?,vexperimentid:=?)");
			pStmt.setInt(1,userID);
			pStmt.setInt(2,searchID);
			privilege = dBconn.getSingleStringValue(pStmt);
			pStmt.close();
						
			if (privilege.equals("w")){
			
				// get the searchparameters according to searchtype
	
				switch (type){
					case "o":   //Object scearch
							  table ="searchobject";
							  break;
					case "p":   // Process search
							  table ="searchprocess";
							  break;
					case "po" : // samplespecific parameter search
							  table ="searchpo";
							  break;
				}
		    	
			 	if (parameterID>0){			
					// delete the search
			        pStmt = dBconn.conn.prepareStatement(	
			        	"DELETE FROM "+table+" WHERE id=? AND search=?");
					pStmt.setInt(1,parameterID);
					pStmt.setInt(2,searchID);
					pStmt.executeUpdate();
					pStmt.close();
				}
			}else{
				response.setStatus(401);
			}
 		    dBconn.closeDB();  // close the database 

	    } catch (SQLException eS) {
			System.err.println("DeleteSearch: SQL Error");
			status="error: SQL error";
			eS.printStackTrace();
			response.setStatus(404);
		} catch (Exception e) {
			System.err.println("DeleteSearch: Some Error, probably JSON");
			status="error: JSON error";
			response.setStatus(404);
		}
	    
	    Unidatoolkit.sendStandardAnswer(status, response);

	}


}
