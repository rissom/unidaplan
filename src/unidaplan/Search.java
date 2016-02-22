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
		userID=userID+1;
		userID=userID-1;
		int type=0;
		int defaultObjecttype=0;
		int defaultProcesstype=0;
		String status="ok";
		PreparedStatement pStmt;
		ArrayList<String> stringkeys = new ArrayList<String>(); 
		JSONObject search = null;
	    response.setContentType("application/json");
	    request.setCharacterEncoding("utf-8");
	    response.setCharacterEncoding("utf-8");
	    PrintWriter out = response.getWriter();
	 	DBconnection dBconn=new DBconnection();
	    JSONObject answer = new JSONObject();
	    int id=-1;	    
	  	try {
	   		 id=Integer.parseInt(request.getParameter("id")); 
	    } catch (Exception e1) {
	   		System.err.println("no search ID given!");
			response.setStatus(404);
	   	}
	    try {  
		    dBconn.startDB();
	    	// get basic search data (id,name,owner,operation)
			pStmt= dBconn.conn.prepareStatement( 	
			    "SELECT id,name,owner,operation,type FROM searches "
			   +"WHERE id=?");
			pStmt.setInt(1, id);
			search=dBconn.jsonObjectFromPreparedStmt(pStmt);
			type=search.getInt("type");
			stringkeys.add(Integer.toString(search.getInt("name")));
			pStmt.close();
			
			// get the searchparameters according to searchtype
			String query="";
			
			
			
			switch (type){
			
				case 1:   // sample search
					query = "SELECT searchobject.id, otparameter AS pid, comparison, value, "
					  		 +"COALESCE (ot_parameters.stringkeyname,paramdef.stringkeyname) AS stringkeyname, "
					  		 +"ot_parameters.objecttypesid AS typeid,paramdef.stringkeyunit,paramdef.datatype "
							 +"FROM searchobject "
							 +"JOIN ot_parameters ON (ot_parameters.id=otparameter) "
							 +"JOIN paramdef ON (paramdef.id=ot_parameters.definition) "
							 +"WHERE search=?";
					pStmt= dBconn.conn.prepareStatement(query);
					pStmt.setInt(1,id);
					sparameter = dBconn.jsonArrayFromPreparedStmt(pStmt);
					pStmt.close();
					// get the sampletype
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
					break;
					
				
				case 2:   // process search
					query = "SELECT searchprocess.id, pparameter AS pid, comparison, value, "
					  		 +"p_parameters.stringkeyname,p_parameters.processtypeid AS typeid,paramdef.stringkeyunit,paramdef.datatype "
							 +"FROM searchprocess "
							 +"JOIN p_parameters ON (p_parameters.id=pparameter) "
							 +"JOIN paramdef ON (paramdef.id=p_parameters.definition) "
							 +"WHERE search=?";
					pStmt= dBconn.conn.prepareStatement(query);
					pStmt.setInt(1,id);
					pparameter = dBconn.jsonArrayFromPreparedStmt(pStmt);
					pStmt.close();
					
					
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
					
					
				case 3:  // sample specific processparameter
					query = "SELECT searchpo.id, poparameter AS pid, comparison, value, "
					 		 +"po_parameters.stringkeyname,po_parameters.processtypeid AS typeid,paramdef.stringkeyunit,paramdef.datatype "
							 +"FROM searchpo "
							 +"JOIN po_parameters ON (po_parameters.id=poparameter) "
							 +"JOIN paramdef ON (paramdef.id=po_parameters.definition) "
							 +"WHERE search=?";
					pStmt= dBconn.conn.prepareStatement(query);
					pStmt.setInt(1,id);
					poparameter = dBconn.jsonArrayFromPreparedStmt(pStmt);
					pStmt.close();
					break;
					
					
				case 4:  // combined search, sample parameters and processparameters
					query = "SELECT searchprocess.id, pparameter AS pid, comparison, value, "
					  		 +"p_parameters.stringkeyname,p_parameters.processtypeid AS typeid,paramdef.stringkeyunit,paramdef.datatype "
							 +"FROM searchprocess "
							 +"JOIN p_parameters ON (p_parameters.id=pparameter) "
							 +"JOIN paramdef ON (paramdef.id=p_parameters.definition) "
							 +"WHERE search=?";
					pStmt= dBconn.conn.prepareStatement(query);
					pStmt.setInt(1,id);
					pparameter = dBconn.jsonArrayFromPreparedStmt(pStmt);
					pStmt.close();
					
					
					query = "SELECT searchobject.id, otparameter AS pid, comparison, value, "
					  		 +"COALESCE (ot_parameters.stringkeyname,paramdef.stringkeyname) AS stringkeyname, "
					  		 +"ot_parameters.objecttypesid AS typeid,paramdef.stringkeyunit,paramdef.datatype "
							 +"FROM searchobject "
							 +"JOIN ot_parameters ON (ot_parameters.id=otparameter) "
							 +"JOIN paramdef ON (paramdef.id=ot_parameters.definition) "
							 +"WHERE search=?";
					pStmt= dBconn.conn.prepareStatement(query);
					pStmt.setInt(1, id);
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
    		response.setStatus(404);
    	} 
	    
	   try {
		   switch (type){
		   
		   case 1: 	   	search.put("sparameters",sparameter);
					   	if (defaultObjecttype>0){
						   search.put("defaultobject", defaultObjecttype);
					   	}
		   				break;
		   case 2: 		search.put("pparameters", pparameter);
						if (defaultProcesstype>0){
							   search.put("defaultprocess", defaultProcesstype);
						}
		   				break;
		   case 3: 		search.put("poparameters", poparameter);
						break;
		   case 4: 		search.put("sparameters", sparameter);
		   				search.put("pparameters", pparameter);
		   				break;
		   }
		   
		   answer.put("search", search);
		   answer.put("strings", dBconn.getStrings(stringkeys));
		   answer.put("status", status);
		   out.println(answer.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    
	    		
	    
		dBconn.closeDB();
	}}	