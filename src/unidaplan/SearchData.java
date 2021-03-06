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

	public class SearchData extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@SuppressWarnings("resource")
	@Override
	  public void doGet(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {
		String status="ok";
		Authentificator authentificator = new Authentificator();
		int userID = authentificator.GetUserID(request,response);
		if (userID < 1){
			status = "not logged in";
			response.setStatus(401);
		}
		JSONArray poparameter = null;
		JSONArray sparameter = null;
		JSONArray pparameter = null;
		JSONArray output = null;
		JSONArray userRights = null;
		JSONArray groupRights = null;
		PreparedStatement pStmt;
		ArrayList<String> stringkeys = new ArrayList<String>(); 
		JSONObject search = null;
	    response.setContentType("application/json");
	    request.setCharacterEncoding("utf-8");
	    response.setCharacterEncoding("utf-8");
	    PrintWriter out = response.getWriter();
	 	DBconnection dBconn = new DBconnection();
	    JSONObject answer = new JSONObject();
	    int searchID = -1;
		int type = 0;
		int defaultObjecttype = 0;
		int defaultProcesstype = 0;
	    String privilege = "n";
	    
	    
	    // get Parameters
	  	try {
	   		 searchID = Integer.parseInt(request.getParameter("id")); 
	    } catch (Exception e1) {
	   		System.err.println("no search ID given!");
			response.setStatus(404);
	   	}
	  	
	  	// initialize database
	    try {
			dBconn.startDB();
		} catch (Exception e1) {
			System.err.println("SearchData: Problems initializing database");
    		e1.printStackTrace();
    		response.setStatus(404);
			status = "Problems initializing database";
		}
	    
	    
	    // check if the user is allowed to see this data:
	    try{
		    pStmt = dBconn.conn.prepareStatement("SELECT getSearchRights(vuserid:=?,vsearchid:=?)");
		    pStmt.setInt(1,userID);
		    pStmt.setInt(2,searchID);
		    privilege=dBconn.getSingleStringValue(pStmt);
		} catch (Exception e) {
    		System.err.println("SearchData: Problems with SQL query for priveleges for this search");
    		e.printStackTrace();
    		response.setStatus(404);
			status="SQL Problem while getting search";	    	
	    }
	    
	    
	    if (privilege.equals("w")){
	    try{
	    	// get basic search data (id,name,owner,operation)
			pStmt = dBconn.conn.prepareStatement( 	
			      "SELECT "
			    + "  id,"
			    + "  name,"
			    + "  owner,"
			    + "  operation,"
			    + "  type "
			    + "FROM searches "
			    + "WHERE id = ?");
			pStmt.setInt(1, searchID);
			search=dBconn.jsonObjectFromPreparedStmt(pStmt);
			type=search.getInt("type");
			stringkeys.add(Integer.toString(search.getInt("name")));
			pStmt.close();
			
			// get the searchparameters according to searchtype
			String query="";
			switch (search.getInt("type")){
				case 1:   //sample search
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
					  	pStmt = dBconn.conn.prepareStatement(query);
						pStmt.setInt(1,searchID);
						sparameter = dBconn.jsonArrayFromPreparedStmt(pStmt);
						pStmt.close();
						// get the sampletype
						if (sparameter.length() > 0){
							defaultObjecttype = sparameter.getJSONObject(0).getInt("typeid");
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
				case 2:   // Process search
					query =   "SELECT searchprocess.id, "
						    + "pparameter AS pid, "
						    + "  comparison, "
						    + "  value, "
					  	    + "  COALESCE (p_parameters.stringkeyname, paramdef.stringkeyname) AS stringkeyname, "
					  	    + "  p_parameters.processtypeid AS typeid,"
					  	    + "  paramdef.stringkeyunit,"
					  	    + "  paramdef.datatype "
						    + "FROM searchprocess "
						    + "JOIN p_parameters ON (p_parameters.id=pparameter) "
						    + "JOIN paramdef ON (paramdef.id = p_parameters.definition) "
						    + "WHERE search = ?";
						// get the processtype
						pStmt = dBconn.conn.prepareStatement(query);
						pStmt.setInt(1,searchID);
						pparameter = dBconn.jsonArrayFromPreparedStmt(pStmt);
						pStmt.close();
					  
						if (pparameter.length() > 0){
							defaultProcesstype = pparameter.getJSONObject(0).getInt("typeid");
							for (int i = 0; i < pparameter.length(); i++){
								stringkeys.add(Integer.toString(pparameter.getJSONObject(i).getInt("stringkeyname")));
								int datatype = pparameter.getJSONObject(i).getInt("datatype");
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
							defaultProcesstype = poparameter.getJSONObject(0).getInt("typeid");
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
						break;
				case 4 : // combined search
						query =   "SELECT "
								+ "  searchprocess.id, "
								+ "  pparameter AS pid, "
								+ "  comparison, "
								+ "  value, "
						  		+ "  p_parameters.stringkeyname,p_parameters.processtypeid AS typeid,paramdef.stringkeyunit,paramdef.datatype "
								+ "FROM searchprocess "
								+ "JOIN p_parameters ON (p_parameters.id=pparameter) "
								+ "JOIN paramdef ON (paramdef.id=p_parameters.definition) "
								+ "WHERE search=?";
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
								+ "JOIN ot_parameters ON (ot_parameters.id = otparameter) "
								+ "JOIN paramdef ON (paramdef.id = ot_parameters.definition) "
								+ "WHERE search=?";
						pStmt = dBconn.conn.prepareStatement(query);
						pStmt.setInt(1, searchID);
						sparameter = dBconn.jsonArrayFromPreparedStmt(pStmt);
						pStmt.close();
						
						if (pparameter.length()>0){
							defaultProcesstype=pparameter.getJSONObject(0).getInt("typeid");
						}
							
						// get the sample type
						if (sparameter.length()>0){
							defaultObjecttype=sparameter.getJSONObject(0).getInt("typeid");
							for (int i=0; i<sparameter.length();i++){
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
							defaultProcesstype=pparameter.getJSONObject(0).getInt("typeid");
							for (int i=0; i<pparameter.length();i++){
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
				
				// get the outputparameters according to searchtype
					
				pStmt = dBconn.conn.prepareStatement(
						  "SELECT \n"
						+ "  ot_parameters.id, \n"
						+ "  osearchoutput.position, \n"
						+ "  COALESCE (ot_parameters.stringkeyname,paramdef.stringkeyname) AS stringkeyname, \n"
						+ "  paramdef.datatype, \n"
						+ "  osearchoutput.id AS outputid, \n"
						+ "  'o' as type \n"
						+ "  FROM osearchoutput \n"
						+ "  JOIN ot_parameters ON (ot_parameters.id=otparameter) \n"
						+ "  JOIN paramdef ON (paramdef.id=ot_parameters.definition) \n" 
						+ "  WHERE search=? \n"
						+ "  \n"
						+ "UNION ALL \n"
						+ " \n"
						+ "SELECT p_parameters.id, \n" 
						+ "  psearchoutput.position, \n"
						+ "  COALESCE (p_parameters.stringkeyname,paramdef.stringkeyname) AS stringkeyname, \n" 
						+ "  paramdef.datatype, \n"
						+ "  psearchoutput.id AS outputid, \n"
						+ "  'p' as type \n"
						+ "FROM psearchoutput \n"
						+ "JOIN p_parameters ON (p_parameters.id=pparameter) \n"
						+ "JOIN paramdef ON (paramdef.id=p_parameters.definition) \n" 
						+ "WHERE search = ? \n"
						+ "\n"
						+ "UNION ALL \n"
						+ "\n"
						+ "SELECT "
						+ "  po_parameters.id, \n" 
						+ "  posearchoutput.position, \n"
						+ "  COALESCE (po_parameters.stringkeyname,paramdef.stringkeyname) AS stringkeyname, \n"
						+ "  paramdef.datatype, \n"
						+ "  posearchoutput.id AS outputid, \n" 
						+ "  'po' as type \n"
						+ "FROM posearchoutput \n"
						+ "JOIN po_parameters ON (po_parameters.id = poparameter) \n"
						+ "JOIN paramdef ON (paramdef.id = po_parameters.definition) \n"
						+ "WHERE search = ? \n");
				pStmt.setInt(1,searchID);
				pStmt.setInt(2,searchID);
				pStmt.setInt(3,searchID);
				output = dBconn.jsonArrayFromPreparedStmt(pStmt);
				pStmt.close();
				
				for (int i = 0; i < output.length(); i++){
					stringkeys.add(Integer.toString(output.getJSONObject(i).getInt("stringkeyname")));	
				}
				
				if ((type == 1 || type == 4) && defaultObjecttype == 0){  //get the first objecttype
					pStmt = dBconn.conn.prepareStatement(""
							+ "SELECT id "
							+ "FROM objecttypes " 
							+ "LIMIT 1");
					defaultObjecttype = dBconn.getSingleIntValue(pStmt);
				}
				
				if ((type == 2 || type == 4) && defaultProcesstype == 0){  //get the first processtype
					pStmt = dBconn.conn.prepareStatement(""
							+ "SELECT id "
							+ "FROM processtypes " 
							+"LIMIT 1");
					defaultProcesstype = dBconn.getSingleIntValue(pStmt);
				}
				
				
				// get search rights for groups:
				pStmt = dBconn.conn.prepareStatement(
						"SELECT " 
						+ "groupid AS id, "
						+ "permission " 
						+ "FROM rightssearchgroups "
						+ "WHERE searchid = ?");
				pStmt.setInt(1, searchID);
				groupRights = dBconn.jsonArrayFromPreparedStmt(pStmt);
				pStmt.close();
				
				
				
				// get search rights for users:
				pStmt = dBconn.conn.prepareStatement(
						"SELECT " 
						+ "userid AS id, "
						+ "permission " 
						+ "FROM rightssearchuser "
						+ "WHERE searchid = ?");
				pStmt.setInt(1, searchID);
				userRights = dBconn.jsonArrayFromPreparedStmt(pStmt);
				pStmt.close();
				
				
				
	    	} catch (SQLException e) {
	    		System.err.println("SearchData: Problems with SQL query for search");
	    		e.printStackTrace();
	    		response.setStatus(404);
				status="SQL Problem while getting experiment";
	    	} catch (JSONException e) {
				System.err.println("SearchData: JSON Problem while getting experiment");
	    		response.setStatus(404);
				e.printStackTrace();
				status="JSON Problem while getting experiment";
	    	} catch (Exception e2) {
				System.err.println("SearchData: Strange Problem while getting experiment");
				status="Problem while getting experiment";
	    		response.setStatus(404);
	    	} 
		    
		   try {
			   JSONObject rights = new JSONObject(); 
			   if (userRights != null){
				   rights.put("users",userRights);
			   }
			   if (groupRights != null){
				   rights.put("groups", groupRights);
			   }
			   if (!(rights.isNull("groups") && rights.isNull("users"))){
				   search.put("rights", rights);
			   }
			   if (pparameter != null && pparameter.length() > 0){
				   search.put("pparameter",pparameter);
			   }
			   if (sparameter != null && sparameter.length() > 0){
				   search.put("sparameter",sparameter);
			   }
			   if (poparameter != null && poparameter.length() > 0){
				   search.put("poparameter",poparameter);
			   }
			   if (output != null){
				   search.put("output",output);
			   }
			   if ((type == 1 || type == 4) && defaultObjecttype > 0){
				   search.put("defaultobject", defaultObjecttype);
			   }
			   if ((type == 2 || type == 4) && defaultProcesstype>0){
				   search.put("defaultprocess", defaultProcesstype);
			   }
			   answer.put("status",status);
			   answer.put("strings", dBconn.getStrings(stringkeys));
			   answer.put("search", search);
		   } catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		   }
	    }else{
			response.setStatus(401);
			status = "not allowed";
		}
	    
		out.println(answer.toString());
		dBconn.closeDB();
	}}	