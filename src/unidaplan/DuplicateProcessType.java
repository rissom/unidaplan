package unidaplan;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;

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
	    String status="ok";
	    
		PreparedStatement pStmt = null; 	// Declare variables
		int processTypeID=0;
 		int nameKey=0;
 		int descKey=0;
 		int id = 0;
 		
		// New connection to the database
	 	DBconnection dBconn=new DBconnection(); 
	 	try {
			dBconn.startDB();
		} catch (Exception e2) {
			status="cannot connect to database";
			response.setStatus(404);
			e2.printStackTrace();
		}

		// get Parameter for id
		try{
			 processTypeID=Integer.parseInt(request.getParameter("id")); }
		catch (Exception e1) {
			processTypeID=-1;
			System.err.print("Duplicate Processtype: no process ID given!");
			status="error: no process ID";
		}
		
		if (Unidatoolkit.userHasAdminRights(userID, dBconn)){

			//copy the strings
			try {
			 	if (processTypeID>0){		
			        pStmt = dBconn.conn.prepareStatement(	
			        	"SELECT language,value FROM Stringtable WHERE string_key = (SELECT name FROM processtypes WHERE id=?)");
					pStmt.setInt(1,processTypeID);
					JSONArray nameStrings = dBconn.jsonArrayFromPreparedStmt(pStmt);
					pStmt.close();
			       
					if (nameStrings.length()>0){
						// copy entry in stringkeytable
						pStmt = dBconn.conn.prepareStatement(	
					       	"INSERT INTO string_key_table (description) "
							+"SELECT description FROM string_key_table WHERE id=(SELECT name FROM processtypes WHERE id=?) RETURNING ID");
						pStmt.setInt(1,processTypeID);
						nameKey = dBconn.getSingleIntValue(pStmt);
						pStmt.close();
						// copy entries in stringtable. Add "copy of " at the beginning.
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
						
					pStmt = dBconn.conn.prepareStatement(	
					        	"SELECT description FROM processtypes WHERE id=?");
					pStmt.setInt(1,processTypeID);
					int oldDescKey = dBconn.getSingleIntValue(pStmt);
					pStmt.close();
					if (oldDescKey>0){
						// copy entry in stringkeytable
						descKey=Unidatoolkit.copyStringEntry(oldDescKey, userID, dBconn);
					}
	
				}
			} catch (SQLException eS) {
				System.err.println("Duplicate Process: SQL Error");
				status="error: SQL error";
				eS.printStackTrace();
			} catch (Exception e) {
				System.err.println("Duplicate Process: Some Error, probably JSON");
				status="error: JSON error";
				e.printStackTrace();
			}
			 
			
		 	
			try {
				 //copy the entry
			 	if (processTypeID>0){			
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
				System.err.println("Duplicate Process: SQL Error");
				status="error: SQL error";
				eS.printStackTrace();
			} catch (Exception e) {
				System.err.println("Duplicate Process: Some Error, probably JSON");
				status="error: JSON error";
				e.printStackTrace();
			}
	
		 
		 	
			try {			
			 	//query the parametergroups
				JSONArray parameterGrps=null;	
				JSONArray parameters=null;			
		        pStmt = dBconn.conn.prepareStatement(	
		        	"SELECT * FROM p_parametergrps WHERE processtype=?");
				pStmt.setInt(1,processTypeID);
				parameterGrps=dBconn.jsonArrayFromPreparedStmt(pStmt);
				pStmt.close();
	
	
				// copy the parametergroups 
			 	for (int i=0; i<parameterGrps.length();i++){
			 		int stringKey=parameterGrps.getJSONObject(i).getInt("stringkey");
			 		int pos=parameterGrps.getJSONObject(i).getInt("pos");
			 		int oldParamterGrpID=parameterGrps.getJSONObject(i).getInt("id");
			 		int newKey=Unidatoolkit.copyStringEntry(stringKey,userID,dBconn);
			 		pStmt = dBconn.conn.prepareStatement(	
				        	"INSERT INTO p_parametergrps(processtype,stringkey,pos,lastUser) "
				        	+"VALUES (?,?,?,?) RETURNING id");
			 		pStmt.setInt(1, id);
			 		pStmt.setInt(2, newKey);
			 		pStmt.setInt(3, pos);
			 		pStmt.setInt(4, userID);
					int newParamterGrpID=dBconn.getSingleIntValue(pStmt);
					pStmt.close();
			 		pStmt = dBconn.conn.prepareStatement(	
				        	"SELECT * FROM p_parameters WHERE parametergroup=?");
			 		pStmt.setInt(1, oldParamterGrpID);
			 		parameters=dBconn.jsonArrayFromPreparedStmt(pStmt);
	
				 	for (int j=0; j<parameters.length();j++){
	
				 		int parameterName=Unidatoolkit.copyStringEntry(parameters.getJSONObject(j).getInt("stringkeyname"), userID, dBconn);
				 		pStmt = dBconn.conn.prepareStatement(	
					        	"INSERT INTO p_parameters(ProcesstypeID,Parametergroup,compulsory,ID_Field,Formula,Hidden,pos,definition,StringKeyName,lastUser ) "
					        	+"VALUES (?,?,?,?,?,?,?,?,?,?)");
				 		
				 		pStmt.setInt(1, id);
				 		
				 		pStmt.setInt(2, newParamterGrpID);
				 		
				 		if (parameters.getJSONObject(j).has("compulsory")){
					 		pStmt.setBoolean(3, parameters.getJSONObject(j).getBoolean("compulsory"));
				 		}else{
					 		pStmt.setBoolean(3, false);
				 		}	
				 		
				 		pStmt.setBoolean(4, parameters.getJSONObject(j).getBoolean("id_field"));
				
				 		if (parameters.getJSONObject(j).has("formula")){
				 			pStmt.setString(5, parameters.getJSONObject(j).getString("formula"));
				 		}else{
				 			pStmt.setNull(5, java.sql.Types.VARCHAR);
				 		}
				 		if (parameters.getJSONObject(j).has("hidden")){
				 			pStmt.setBoolean(6, parameters.getJSONObject(j).getBoolean("hidden"));
				 		}else{
				 			pStmt.setBoolean(6,false);
				 		}			 	
				 		if (parameters.getJSONObject(j).has("pos")){
					 		pStmt.setInt(7, parameters.getJSONObject(j).getInt("pos"));
				 		}else{
				 			pStmt.setInt(7, j);
				 		}
				 		pStmt.setInt(8, parameters.getJSONObject(j).getInt("definition"));
				 		pStmt.setInt(9, parameterName);
				 		pStmt.setInt(10, userID);
				 			
				 		pStmt.executeUpdate();
			 		}
			 	}
			 	
	
			} catch (SQLException eS) {
				System.err.println("Duplicate Process: SQL Error");
				status="error: SQL error";
				eS.printStackTrace();
			} catch (Exception e) {
				System.err.println("Duplicate Process: Some Error, probably JSON");
				status="error: JSON error";
				e.printStackTrace();
			}
		}else{
			response.setStatus(401);
		}
	    
	    
	
		try{	
			if (dBconn.conn != null) { 
				dBconn.closeDB();  // close the database 
			}
        } catch (Exception e) {
			status="error: error closing the database";
			System.err.println("Duplicate Process: Some Error closing the database");
			e.printStackTrace();
	   	}
	
	
	    // tell client that everything is fine
	    Unidatoolkit.sendStandardAnswer(status,response);

	}


}
