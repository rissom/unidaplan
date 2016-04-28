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

	public class UpdateComparison extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doPut(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {		
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
	    request.setCharacterEncoding("utf-8");
	    String in = request.getReader().readLine();
		String privilege = "n";
	    String status = "ok";
	    String table="";

	    JSONObject jsonIn = null;
	    int searchID = -1;
	    int id = -1;
	    int datatype=-1;
	    int comparison=-1;
	    
    
	    try {
			 jsonIn = new JSONObject(in);
	         searchID=jsonIn.getInt("searchid");
			 id=jsonIn.getInt("id");
			 comparison=jsonIn.getInt("comparison");
		} catch (JSONException e) {
			System.err.println("UpdateComparison: Error parsing ID-Field or comment");
			response.setStatus(404);
		}
	    
	 	DBconnection dBconn = new DBconnection(); // initialize database
	    PreparedStatement pStmt = null;
	    
		
	    try {  
		    dBconn.startDB();
		 
		    // check privileges
		    pStmt = dBconn.conn.prepareStatement( 	
					"SELECT getExperimentRights(vuserid:=?,vexperimentid:=?)");
			pStmt.setInt(1,userID);
			pStmt.setInt(2,searchID);
			privilege = dBconn.getSingleStringValue(pStmt);
			pStmt.close();
						
			if (privilege.equals("w")){    
			
				// get the searchparameters according to searchtype
				String query="";
	
				switch (jsonIn.getString("type")){
					case "o":   //Object scearch
							  query = "SELECT paramdef.datatype "
									 +"FROM searchobject "
									 +"JOIN ot_parameters ON (ot_parameters.id=otparameter) "
									 +"JOIN paramdef ON (paramdef.id=ot_parameters.definition) "
									 +"WHERE search=? AND searchobject.id=?";
							  table ="searchobject";
							  break;
					case "p":   // Process search
							  query = "SELECT paramdef.datatype "
									 +"FROM searchprocess "
									 +"JOIN p_parameters ON (p_parameters.id=pparameter) "
									 +"JOIN paramdef ON (paramdef.id=p_parameters.definition) "
									 +"WHERE search=? AND searchprocess.id=?";
							  table ="searchprocess";
							  break;
					case "po" : query = "SELECT paramdef.datatype "
									 +"FROM searchpo "
									 +"JOIN po_parameters ON (po_parameters.id=poparameter) "
									 +"JOIN paramdef ON (paramdef.id=po_parameters.definition) "
									 +"WHERE search=? AND searchpo.id=?";
					  		  table ="searchpo";
							  break;
				}
				pStmt= dBconn.conn.prepareStatement(query);
				pStmt.setInt(1,searchID);
				pStmt.setInt(2,id);
				datatype = dBconn.getSingleIntValue(pStmt);
				
				// possible comparators: 1:< , 2:> , 3:=, 4:not, 5:contains
				switch (datatype){  
					case 1: // integer,
						if (comparison<1 || comparison>4) { status="error: illegal comparator";}
						break;
					case 2: // float,
						if (comparison<1 || comparison>4) { status="error: illegal comparator";}
						break;
					case 3: // measurement
						if (comparison<1 || comparison>4) { status="error: illegal comparator";}
						break;
					case 4: // string
						if (comparison<3 || comparison>5) { status="error: illegal comparator";}
						break;
					case 5: // long string 
						if (comparison<3 || comparison>5) { status="error: illegal comparator";}
						break;
					case 6: // chooser
						if (comparison<3 || comparison>4) { status="error: illegal comparator";}
						break;
					case 7: // date
						if (comparison<1 || comparison>4) { status="error: illegal comparator";}
						break;
					case 8: // checkbox
						if (comparison<3 || comparison>4) { status="error: illegal comparator";}
						break;
					case 9: // timestamp
						if (comparison<1 || comparison>4) { status="error: illegal comparator";}
						break;
					case 10: // url
						if (comparison<3 || comparison>5) { status="error: illegal comparator";}
						break;
					case 11: // email
						if (comparison<3 || comparison>5) { status="error: illegal comparator";}
						break;
					default: 
						status="error:unknown datatype";
				}
				if (status=="ok") {
					pStmt= dBconn.conn.prepareStatement( 	
						    "UPDATE "+table+" SET (comparison,lastuser)=(?,?) WHERE search=? AND id=?");
					pStmt.setInt(1, comparison);
					pStmt.setInt(2, userID);
					pStmt.setInt(3, searchID);
					pStmt.setInt(4, id);
					pStmt.executeUpdate();
					pStmt.close();
				}	
			} else {
				response.setStatus(401);
			}
			
		} catch (SQLException e) {
			System.err.println("UpdateComparison: Problems with SQL query");
			status="SQL error";
		} catch (Exception e) {
			System.err.println("UpdateComparison: some error occured");
			status="misc error";
		}
		
		dBconn.closeDB();

		
    // tell client that everything is fine
    Unidatoolkit.sendStandardAnswer(status,response);
	}
}	