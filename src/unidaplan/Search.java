package unidaplan;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

	public class Search extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@SuppressWarnings("resource")
	@Override
	  public void doGet(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
		JSONArray poparameter = null;
		JSONArray sparameter = null;
		JSONArray pparameter = null;
		int type = 0;
		String status = "ok";
		String privilege = "n";
		PreparedStatement pStmt;
		ArrayList<String > stringkeys = new ArrayList<String>(); 
		JSONObject search = null;
	    response.setContentType("application/json");
	    request.setCharacterEncoding("utf-8");
	    response.setCharacterEncoding("utf-8");
	    PrintWriter out = response.getWriter();
	 	DBconnection dBconn = new DBconnection();
	    JSONObject answer = new JSONObject();
	    int searchID=-1;	    
	  	try {
	   		 searchID=Integer.parseInt(request.getParameter("id")); 
	    } catch (Exception e1) {
	   		System.err.println("no search ID given!");
			response.setStatus(404);
	   	}
	    try {  
		    dBconn.startDB();
		    // check if the user is allowed to see this data: (1. userrights, 2. grouprights, 3. admin
		    pStmt = dBconn.conn.prepareStatement( 	
			    "SELECT getSearchRights(vuserid:=?, vsearchid:=?)");
			pStmt.setInt(1, userID);
			pStmt.setInt(2, searchID);
	
			privilege = dBconn.getSingleStringValue(pStmt);
			
		} catch (SQLException e) {
			System.err.println("Search: Problems with SQL query for search");
			response.setStatus(404);
			e.printStackTrace();
			status="SQL Problem while getting experiment";
		} catch (JSONException e) {
			System.err.println("Search: JSON Problem while getting experiment");
			e.printStackTrace();
			response.setStatus(404);
			status="JSON Problem while getting experiment";
		} catch (Exception e2) {
			System.err.println("Search: Strange Problem while getting the search");
			status="Problem while getting the search";
			e2.printStackTrace();
		} 
		
	    
		if (privilege.equals("r") || privilege.equals("w")){
			try{
		
			    
		    	// get basic search data (id,name,owner,operation)
				pStmt= dBconn.conn.prepareStatement( 	
				    "SELECT id,name,owner,operation,type FROM searches "
				   +"WHERE id = ?");
				pStmt.setInt(1, searchID);
				search=dBconn.jsonObjectFromPreparedStmt(pStmt);
				type=search.getInt("type");
				stringkeys.add(Integer.toString(search.getInt("name")));
				pStmt.close();
				
				// get the searchparameters according to searchtype
				String query="";
				
				
				
				switch (type){
				
					case 1:   // sample search
						query =   "SELECT "
								+ "  searchobject.id, "
								+ "otparameter AS pid, "
								+ "comparison, "
								+ "value, "
						  		+ "COALESCE (ot_parameters.stringkeyname,paramdef.stringkeyname) AS stringkeyname, "
						  		+ "ot_parameters.objecttypesid AS typeid,paramdef.stringkeyunit,paramdef.datatype "
								+ "FROM searchobject "
								+ "JOIN ot_parameters ON (ot_parameters.id=otparameter) "
								+ "JOIN paramdef ON (paramdef.id=ot_parameters.definition) "
								+ "WHERE search = ?";
						pStmt = dBconn.conn.prepareStatement(query);
						pStmt.setInt(1,searchID);
						sparameter = dBconn.jsonArrayFromPreparedStmt(pStmt);
						pStmt.close();
						// get the sampletype
						if (sparameter.length() > 0){
							
							for (int i = 0; i < sparameter.length(); i++){
								stringkeys.add(Integer.toString(sparameter.getJSONObject(i).getInt("stringkeyname")));
								int datatype=sparameter.getJSONObject(i).getInt("datatype");
								sparameter.getJSONObject(i).remove("datatype");
								sparameter.getJSONObject(i).put("datatype", Unidatoolkit.Datatypes[datatype]);
								if (sparameter.getJSONObject(i).has("stringkeyunit")){
									stringkeys.add(Integer.toString(sparameter.getJSONObject(i).getInt("stringkeyunit")));
								}
							}
						}
						break;
						
					
					case 2:   // process search
						query =   "SELECT "
								+ "  searchprocess.id, "
								+ "  pparameter AS pid, "
								+ "  comparison, "
								+ "  value, "
						  		+ "  p_parameters.stringkeyname, "
						  		+ "  p_parameters.processtypeid AS typeid,"
						  		+ "  paramdef.stringkeyunit,"
						  		+ "  paramdef.datatype "
								+ "FROM searchprocess "
								+ "JOIN p_parameters ON (p_parameters.id=pparameter) "
								+ "JOIN paramdef ON (paramdef.id = p_parameters.definition) "
								+ "WHERE search = ?";
						pStmt = dBconn.conn.prepareStatement(query);
						pStmt.setInt(1,searchID);
						pparameter = dBconn.jsonArrayFromPreparedStmt(pStmt);
						pStmt.close();
						
						
						// get the processtype
						if (pparameter.length()>0){
							for (int i=0; i < pparameter.length(); i++){
								stringkeys.add(Integer.toString(pparameter.getJSONObject(i).getInt("stringkeyname")));
								int datatype=pparameter.getJSONObject(i).getInt("datatype");
								pparameter.getJSONObject(i).remove("datatype");
								pparameter.getJSONObject(i).put("datatype", Unidatoolkit.Datatypes[datatype]);
								if (pparameter.getJSONObject(i).has("stringkeyunit")){
									stringkeys.add(Integer.toString(pparameter.getJSONObject(i).getInt("stringkeyunit")));
								}
							}
						}
						break;
						
						
					case 3 : // sample related process parameters 
						query =   "SELECT "
								+ "  searchpo.id, " 
								+ "  poparameter AS pid, " 
								+ "  comparison, " 
								+ "  value, " 
								+ "  COALESCE (po_parameters.stringkeyname, paramdef.stringkeyname) AS stringkeyname, " 
								+ "  po_parameters.processtypeid AS typeid,"
								+ "  paramdef.stringkeyunit, "
								+ "  paramdef.datatype " 
								+ "FROM searchpo "
								+ "JOIN po_parameters ON (po_parameters.id = poparameter) " 
								+ "JOIN paramdef ON (paramdef.id = po_parameters.definition) " 
								+ "WHERE search = ?";
						pStmt = dBconn.conn.prepareStatement(query);
						pStmt.setInt(1,searchID);
						poparameter = dBconn.jsonArrayFromPreparedStmt(pStmt);
						pStmt.close();
						if (poparameter.length() > 0){
							for (int i = 0; i < poparameter.length(); i++){
								stringkeys.add(Integer.toString(poparameter.getJSONObject(i).getInt("stringkeyname")));
								int datatype = poparameter.getJSONObject(i).getInt("datatype");
								poparameter.getJSONObject(i).remove("datatype");
								poparameter.getJSONObject(i).put("datatype", Unidatoolkit.Datatypes[datatype]);
								if (poparameter.getJSONObject(i).has("stringkeyunit")){
									stringkeys.add(Integer.toString(poparameter.getJSONObject(i).getInt("stringkeyunit")));
								}
							}
						}

						pStmt.close();
						break;
						
						
					case 4:  // combined search, sample parameters and processparameters
						query =   "SELECT "
								+ "  searchprocess.id, "
								+ "  pparameter AS pid, "
								+ "  comparison, "
								+ "  value, "
						  		+ "  p_parameters.stringkeyname,"
						  		+ "  p_parameters.processtypeid AS typeid,"
						  		+ "  paramdef.stringkeyunit,"
						  		+ "  paramdef.datatype "
								+ "FROM searchprocess "
								+ "JOIN p_parameters ON (p_parameters.id=pparameter) "
								+ "JOIN paramdef ON (paramdef.id = p_parameters.definition) "
								+ "WHERE search = ?";
						pStmt = dBconn.conn.prepareStatement(query);
						pStmt.setInt(1,searchID);
						pparameter = dBconn.jsonArrayFromPreparedStmt(pStmt);
						pStmt.close();
						
						
						query =   "SELECT "
								+ "  searchobject.id, "
								+ "  otparameter AS pid, "
								+ "  comparison, "
								+ "  value, "
						  		+ "  COALESCE (ot_parameters.stringkeyname,paramdef.stringkeyname) AS stringkeyname, "
						  		+ "  ot_parameters.objecttypesid AS typeid,"
						  		+ "  paramdef.stringkeyunit,"
						  		+ "  paramdef.datatype "
								+ "FROM searchobject "
								+ "JOIN ot_parameters ON (ot_parameters.id=otparameter) "
								+ "JOIN paramdef ON (paramdef.id=ot_parameters.definition) "
								+ "WHERE search = ?";
						pStmt= dBconn.conn.prepareStatement(query);
						pStmt.setInt(1, searchID);
						sparameter = dBconn.jsonArrayFromPreparedStmt(pStmt);
						pStmt.close();
					
						
						// get the sample type
						if (sparameter.length() > 0){
							for (int i = 0; i < sparameter.length(); i++){
								stringkeys.add(Integer.toString(sparameter.getJSONObject(i).getInt("stringkeyname")));
								int datatype=sparameter.getJSONObject(i).getInt("datatype");
								sparameter.getJSONObject(i).remove("datatype");
								sparameter.getJSONObject(i).put("datatype", Unidatoolkit.Datatypes[datatype]);
								if (sparameter.getJSONObject(i).has("stringkeyunit")){
									stringkeys.add(Integer.toString(sparameter.getJSONObject(i).getInt("stringkeyunit")));
								}
							}
						}
						
						// get the processtype
						if (pparameter.length()>0){
							for (int i = 0; i < pparameter.length(); i++){
								stringkeys.add(Integer.toString(pparameter.getJSONObject(i).getInt("stringkeyname")));
								int datatype=pparameter.getJSONObject(i).getInt("datatype");
								pparameter.getJSONObject(i).remove("datatype");
								pparameter.getJSONObject(i).put("datatype", Unidatoolkit.Datatypes[datatype]);
								if (pparameter.getJSONObject(i).has("stringkeyunit")){
									stringkeys.add(Integer.toString(pparameter.getJSONObject(i).getInt("stringkeyunit")));
								}
							}
						}
						break;							
				}

				
	    	} catch (SQLException e) {
	    		System.err.println("Search: Problems with SQL query for search");
	    		response.setStatus(404);
				e.printStackTrace();
				status="SQL Problem while getting experiment";
	    	} catch (JSONException e) {
				System.err.println("Search: JSON Problem while getting experiment");
				e.printStackTrace();
	    		response.setStatus(404);
				status="JSON Problem while getting experiment";
	    	} catch (Exception e2) {
				System.err.println("Search: Strange Problem while getting the search");
				status="Problem while getting the search";
				e2.printStackTrace();
	    	} 
		    
		   try {
			   switch (type){
			   
			   case 1: 	   	search.put("sparameters",sparameter);
			   				break;
			   case 2: 		search.put("pparameters", pparameter);
			   				break;
			   case 3: 		search.put("poparameters", poparameter);
							break;
			   case 4: 		search.put("sparameters", sparameter);
			   				search.put("pparameters", pparameter);
			   				break;
			   }
			   
			   search.put("editable", privilege.equals("w"));
			   answer.put("search", search);
			   answer.put("strings", dBconn.getStrings(stringkeys));
			   answer.put("status", status);
			   out.println(answer.toString());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
	   }else {
		   response.setStatus(401);
		   status = "insufficient rights";
	   }		
	    
		dBconn.closeDB();
	}}	