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
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
		JSONArray poparameter = null;
		JSONArray sparameter = null;
		JSONArray pparameter = null;
		JSONArray output = null;
		userID=userID+1;
		userID=userID-1;
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
		int type=0;
		int defaultObjecttype=0;
		int defaultProcesstype=0;
	    
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
			switch (search.getInt("type")){
				case 1:   //sample search
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
				case 2:   // Process search
					query = "SELECT searchprocess.id, pparameter AS pid, "
							 + "comparison, value, "
					  		 + "p_parameters.stringkeyname,"
					  		 + "p_parameters.processtypeid AS typeid,"
					  		 + "paramdef.stringkeyunit,paramdef.datatype "
							 + "FROM searchprocess "
							 + "JOIN p_parameters ON (p_parameters.id=pparameter) "
							 + "JOIN paramdef ON (paramdef.id=p_parameters.definition) "
							 + "WHERE search=?";
						// get the processtype
						pStmt= dBconn.conn.prepareStatement(query);
						pStmt.setInt(1,id);
						pparameter = dBconn.jsonArrayFromPreparedStmt(pStmt);
						pStmt.close();
					  
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
				case 3 : // not tested
						query = "SELECT poparameter, comparison, value, "
						  		 +"po_parameters.stringkeyname,po_parameters.stringkeyname,paramdef.datatype "
								 +"FROM searchpo "
								 +"JOIN po_parameters ON (po_parameters.id=poparameter) "
								 +"JOIN paramdef ON (paramdef.id=po_parameters.definition) "
								 +"WHERE search=?";
						pStmt= dBconn.conn.prepareStatement(query);
						pStmt.setInt(1,id);
						pparameter = dBconn.jsonArrayFromPreparedStmt(pStmt);
						pStmt.close();
						break;
				case 4 : // combined search
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
			
			// get the outputparameters according to searchtype
			switch (search.getInt("type")){
				case 1:   //Object scearch
						query = "SELECT ot_parameters.id, "
								+ "position, "
							    + "COALESCE (ot_parameters.stringkeyname,paramdef.stringkeyname) AS stringkeyname, "
							    + "paramdef.datatype, "
							    + "osearchoutput.id AS outputid "
							    + "FROM osearchoutput "
							    + "JOIN ot_parameters ON (ot_parameters.id=otparameter) "
							    + "JOIN paramdef ON (paramdef.id=ot_parameters.definition) "
							    + "WHERE search=?";
						break;
				case 2:   //Process search (nicht getestet)
					  query = "SELECT p_parameters.id, "
					  			+ "position, "
					  		    + "COALESCE (p_parameters.stringkeyname,paramdef.stringkeyname) AS stringkeyname, "
					  		    + "paramdef.datatype, "
					  		    + "psearchoutput.id AS outputid "
							    + "FROM psearchoutput "
							    + "JOIN p_parameters ON (p_parameters.id=pparameter) "
							    + "JOIN paramdef ON (paramdef.id=p_parameters.definition) "
							    + "WHERE search=?";
						break;
				case 3 : query =  "SELECT po_parameters.id, "
								+ "position,"
								+ "po_parameters.stringkeyname,paramdef.datatype "
								+ "posearchoutput.id AS outputid "
							    + "FROM posearchoutput "
							    + "JOIN po_parameters ON (po_parameters.id=poparameter) "
							    + "JOIN paramdef ON (paramdef.id=po_parameters.definition) "
							    + "WHERE search=?";
				  break;			
			}
			pStmt= dBconn.conn.prepareStatement(query);
			pStmt.setInt(1,id);
			output = dBconn.jsonArrayFromPreparedStmt(pStmt);
			pStmt.close();
			for (int i=0; i<output.length();i++){
				stringkeys.add(Integer.toString(output.getJSONObject(i).getInt("stringkeyname")));	
			}
			
			
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
		   if (pparameter!=null && pparameter.length()>0){
			   search.put("pparameter",pparameter);
		   }
		   if (sparameter!=null && sparameter.length()>0){
			   search.put("sparameter",sparameter);
		   }
		   if (poparameter!=null && poparameter.length()>0){
			   search.put("poparameter",poparameter);
		   }
		   search.put("output",output);
		   if (type==1 && defaultObjecttype>0){
			   search.put("defaultobject", defaultObjecttype);
		   }
		   if (type==2 && defaultProcesstype>0){
			   search.put("defaultprocess", defaultProcesstype);
		   }
		   answer.put("search", search);
		   answer.put("status",status);
		   answer.put("strings", dBconn.getStrings(stringkeys));
		   out.println(answer.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    
	    		
	    
		dBconn.closeDB();
	}}	