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

	public class Result extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doPost(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
		if (userID>0) {
		JSONArray parameters = null;
		JSONArray sResults = null;
		JSONArray samplenames = null;
		int type=1;
		String status="ok";
		PreparedStatement pStmt;
		ArrayList<String> stringkeys = new ArrayList<String>(); 
		JSONObject search = null;
	    request.setCharacterEncoding("utf-8");
	    response.setCharacterEncoding("utf-8");
	    PrintWriter out = response.getWriter();
	 	DBconnection dBconn=new DBconnection();
	    JSONArray inParams = new JSONArray();
	    JSONObject result = new JSONObject();
	    int id=-1;
	    String operationString = "AND";
	    String output = "json";
	    int datatype = 0;

	 
	  	
	  	request.setCharacterEncoding("utf-8");
	    String in = request.getReader().readLine();
	    JSONObject  jsonIn = null;
	    try {
			  jsonIn = new JSONObject(in);
		} catch (JSONException e) {
			response.setStatus(404);
			System.err.println("Result: Input is not valid JSON");
			status="no parameters for performing search";
		}
		response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    
	  	if (status=="ok"){ 
	    try {  
	    	id =jsonIn.getInt("searchid");
	    	if (jsonIn.has("output")){
	    		output = jsonIn.getString("output");
	    	}
	    	inParams = jsonIn.getJSONArray("parameters");
	    } catch (JSONException e) {
			System.err.println("Result: input parameters missing");
    		response.setStatus(404);
			status="searchid is missing";
		}}
	    
	    if (status == "ok"){
	    try{

		    dBconn.startDB();
		    // check if the user is allowed to see this data: (1. userrights, 2. grouprights, 3. admin
		    pStmt = dBconn.conn.prepareStatement( 	
			    "SELECT EXISTS( "
			    + "SELECT 1 FROM rightssearchuser WHERE searchid = 1 AND userid = ? "
			    + "  AND (permission = 'w' OR permission='r')) "
		    	+ "OR EXISTS ( "
		    	+ "SELECT 1 FROM rightssearchgroups rg "
		    	+ "JOIN groupmemberships gm ON (rg.groupid = gm.groupid AND gm.userid = ?) "
				+ "WHERE searchid=1 AND (permission='w' OR permission='r'))"
				+ "OR EXISTS (SELECT 1 FROM groupmemberships WHERE groupid = 1 AND userid = ?)");
			pStmt.setInt(1, userID);
			pStmt.setInt(2, userID);
			pStmt.setInt(3, userID);
			if (!dBconn.getSingleBooleanValue(pStmt)){
				response.setStatus(401);
				status="not allowed";
				throw new Exception("not allowed!");
			}
		    
		    
	    	// get basic search data (id,name,owner,operation)
			pStmt = dBconn.conn.prepareStatement( 	
				      "SELECT operation, type "
				    + "FROM searches "
				    + "WHERE id=?");
			pStmt.setInt(1, id);
			search=dBconn.jsonObjectFromPreparedStmt(pStmt);
	    	operationString = search.getBoolean("operation")?"AND":"OR";
			pStmt.close();		
			
			// get the searchparameters according to searchtype from the database
			String query="";
			type=search.getInt("type");
			switch (type){
				case 1:   // sample search
					query =   "SELECT "
							+ "  otparameter AS pid, "
							+ "  comparison, "
							+ "  value, "
					  		+ "  COALESCE(ot_parameters.stringkeyname,paramdef.stringkeyname) AS stringkeyname, \n"
							+ "  paramdef.datatype "
							+ "FROM searchobject "
							+ "JOIN ot_parameters ON (ot_parameters.id=otparameter) "
							+ "JOIN paramdef ON (paramdef.id=ot_parameters.definition) "
							+ "WHERE search=?";
					break;
				case 2:   // process search
					query =   "SELECT "
							+ "  pparameter AS pid, "
							+ "  comparison, value, "
					  		+ "  COALESCE (p_parameters.stringkeyname, paramdef.stringkeyname) AS stringkeyname,"
					  		+ "  paramdef.datatype \n"
							+ "FROM searchprocess "
							+ "JOIN p_parameters ON (p_parameters.id=pparameter) "
							+ "JOIN paramdef ON (paramdef.id=p_parameters.definition) "
							+ "WHERE search=?";
					break;
				case 3:   // sample specific processparameter
					query =   "SELECT "
							+ "poparameter AS pid, "
							+ "poparameter, "
							+ "comparison, "
							+ "value, "
					 		+ "COALESCE (po_parameters.stringkeyname,paramdef.stringkeyname) AS stringkeyname,"
					 		+ "paramdef.datatype \n"
							+ "FROM searchpo "
							+ "JOIN po_parameters ON (po_parameters.id=poparameter) "
							+ "JOIN paramdef ON (paramdef.id=po_parameters.definition) "
							+ "WHERE search=?";
					break;
			}
			pStmt = dBconn.conn.prepareStatement(query);
			pStmt.setInt(1,id);
			parameters = dBconn.jsonArrayFromPreparedStmt(pStmt);
			String idString="";
			String idString2="";
			String tString="";
			String datatable;
			switch (type){
			case 1:   // sample search
				query = "SELECT samples.id \n FROM samples ";
				tString = "samples";
				idString = "objectid";
				idString2 = "ot_parameter_id";
				datatable = "sampledata";
				break;
			case 2:  // process search
				query = "SELECT processes.id \n FROM processes ";
				tString = "processes";
				idString = "processid";
				idString2 = "parameterid";
				datatable = "processdata";
				break;
			case 3: // samplerelated in processparameter search
				query = "SELECT processes.id, samplesinprocess.sampleid FROM processes "
						+ "JOIN samplesinprocess ON samplesinprocess.processid = processes.id ";
				tString = "processes";
				idString = "processid";
				idString2 = "parameterid";
				datatable = "spdata";
				break;
			default : // sample specific processparameter
				tString = "processes";
				query = "SELECT processes.id FROM processes ";
				idString = "processid";
				idString2 = "parameterid";
				datatable = "spdata";
			}
			String where = "";

			for (int i = 0; i < parameters.length(); i++){
				JSONObject parameter = parameters.getJSONObject(i);
				// find corresponding inParameter
				for (int j = 0; j < parameters.length(); j++){
					if (inParams.getJSONObject(j).getInt("pid") == parameter.getInt("pid")) {
						parameter.put("comparison", inParams.getJSONObject(j).getInt("comparison"));
						parameter.put("value", inParams.getJSONObject(j).getString("value"));
					}
				}
				stringkeys.add(Integer.toString(parameter.getInt("stringkeyname")));
				datatype = parameter.getInt("datatype");
				parameter.put("datatype",Unidatoolkit.Datatypes[datatype]);

				query += " JOIN " + datatable + " p" + i + " ON p" + i + "." + idString + "=" + 
						 tString + ".id AND p" + i + "." + idString2 + "=" + parameter.getInt("pid");
				if (i>0) {
					where += " " + operationString + " ";
				}
				where += "compare (val:= p" + i + ".data, "
						+ "datatype:='" + parameter.getString("datatype") + "', "
						+ "comparator:=" + parameter.getInt("comparison") + ", "
						+ "comval:='" + parameter.getString("value") + "') ";
			}
			query += " WHERE "+ where +" GROUP BY " + tString + ".id";
//			System.out.println(query);
			pStmt = dBconn.conn.prepareStatement(query);
			System.out.println(query);
			sResults = dBconn.jsonArrayFromPreparedStmt(pStmt);
			pStmt.close();
			
			
			JSONArray headings = null;
			JSONArray data = null;

			
			// get data for samples
			if (sResults != null && type == 1){
				query = "SELECT id,name,typeid FROM samplenames WHERE id = ANY('{";
				StringBuilder buff = new StringBuilder(); // join numbers with commas
				String sep = "";
				for (int i=0; i<sResults.length();i++){
					buff.append(sep);
					buff.append(sResults.getJSONObject(i).getInt("id"));
					sep = ",";
				}
				query += buff.toString() + "}'::int[])";
				pStmt= dBconn.conn.prepareStatement(query);
				samplenames = dBconn.jsonArrayFromPreparedStmt(pStmt);

				
				//create headings
				pStmt= dBconn.conn.prepareStatement(
						"  SELECT ot_parameters.id,"
						+ "COALESCE (ot_parameters.stringkeyname,paramdef.stringkeyname) AS stringkeyname, "
						+ "paramdef.stringkeyunit, "
						+ "paramdef.datatype "
						+ "FROM searches "
						+ "JOIN osearchoutput ON osearchoutput.search = searches.id "
						+ "JOIN ot_parameters ON ot_parameters.id=otparameter "
						+ "JOIN paramdef ON ot_parameters.definition=paramdef.id "
						+ "WHERE searches.id=? "
						+ "ORDER BY osearchoutput.position");
				pStmt.setInt(1, id);
				headings = dBconn.jsonArrayFromPreparedStmt(pStmt);
				JSONObject heading = new JSONObject(); 
				if (headings.length()>0){
					for (int i=0; i<headings.length(); i++){
						heading = headings.getJSONObject(i);
						if (heading.has("stringkeyname")){
							stringkeys.add(Integer.toString(heading.getInt("stringkeyname")));
						}
						if (heading.has("stringkeyunit") && heading.getInt("datatype")<4){
							stringkeys.add(Integer.toString(heading.getInt("stringkeyunit")));
						}
						if (heading.has("stringkeyunit") && heading.getInt("datatype")>3){
							heading.remove("stringkeyunit");
						}
						heading.put("datatype",Unidatoolkit.Datatypes[heading.getInt("datatype")]);
					}
				}
				pStmt.close();
				
				
				// build sample Array
				StringBuilder samBuff = new StringBuilder(); // join numbers with commas
				sep = "";
				for (int i = 0; i < sResults.length(); i++){
					samBuff.append(sep);
					samBuff.append(sResults.getJSONObject(i).getInt("id"));
					sep = ",";
				}
				
				// build SELECT statement and JOIN statements
				String joins = "";
				sep = "";
				StringBuilder valuebuff = new StringBuilder(); // comma separated params for Select


				for (int i = 0; i < headings.length(); i++){
					valuebuff.append(",");
//					System.out.println (headings.getJSONObject(i).toString());
					String dt = headings.getJSONObject(i).getString("datatype");
					if (dt.equals("date") || dt.equals("timestamp")){
						valuebuff.append("p" + i + ".data->>'date'");
					} else {		
						valuebuff.append("p" + i + ".data->>'value'");
					}
					
					joins += "LEFT JOIN sampledata p" + i + " ON samples.id = p" + i +
							  ".objectid AND p" + i + ".ot_parameter_id=" +
							  headings.getJSONObject(i).getInt("id") + " \n";
				}
				
				query = "SELECT sn.id,sn.name,typeid" + valuebuff.toString()+" FROM samples " + joins +
						"LEFT JOIN samplenames sn ON sn.id = samples.id "+
						"WHERE samples.id = ANY('{"+samBuff.toString()+"}'::int[])";
//				System.out.println(query);
				pStmt = dBconn.conn.prepareStatement(query);
				data = dBconn.getSearchTable(pStmt);
			}
				
			
			// get data for processes
			if (sResults != null && type == 2){
				query = "SELECT id, p_number, processtype FROM pnumbers WHERE id = ANY('{";
				StringBuilder buff = new StringBuilder(); // join numbers with commas
				String sep = "";
				for (int i = 0; i < sResults.length(); i++){
					buff.append(sep);
					buff.append(sResults.getJSONObject(i).getInt("id"));
					sep = ",";
				}
				query += buff.toString() + "}'::int[])";
				pStmt = dBconn.conn.prepareStatement(query);
				samplenames = dBconn.jsonArrayFromPreparedStmt(pStmt);

				
				//create headings
				pStmt= dBconn.conn.prepareStatement(
						"  SELECT "
						+ "  p_parameters.id,"
						+ "  p_parameters.stringkeyname, "
						+ "  paramdef.stringkeyunit,"
						+ "  paramdef.datatype "
						+ "FROM searches "
						+ "JOIN psearchoutput ON psearchoutput.search = searches.id "
						+ "JOIN p_parameters ON p_parameters.id = pparameter "
						+ "JOIN paramdef ON p_parameters.definition = paramdef.id "
						+ "WHERE searches.id = ? "
						+ "ORDER BY psearchoutput.position");
				pStmt.setInt(1, id);
				headings = dBconn.jsonArrayFromPreparedStmt(pStmt);
				JSONObject heading = new JSONObject(); 
				if (headings.length() > 0){
					for (int i = 0; i < headings.length(); i++){
						heading = headings.getJSONObject(i);
						if (heading.has("stringkeyname")){
							stringkeys.add(Integer.toString(heading.getInt("stringkeyname")));
						}
						if (heading.has("stringkeyunit")){
							stringkeys.add(Integer.toString(heading.getInt("stringkeyunit")));
						}
					}
				}
				pStmt.close();
				
			
				// build process Array
				StringBuilder samBuff = new StringBuilder(); // join numbers with commas
				sep = "";
				for (int i = 0; i < sResults.length(); i++){
					samBuff.append(sep);
					samBuff.append(sResults.getJSONObject(i).getInt("id"));
					sep = ",";
				}
				
				// build SELECT statement and JOIN statements
				String joins="";
				sep = "";
				StringBuilder valuebuff = new StringBuilder(); // comma separated params for Select


				for (int i = 0; i < headings.length(); i++){
					valuebuff.append(",");
					datatype = headings.getJSONObject(i).getInt("datatype");
					String dt = Unidatoolkit.Datatypes[datatype];

					if (dt.equals("date") || dt.equals("timestamp")){
						valuebuff.append("p" + i + ".data->>'date'");
					} else {		
						valuebuff.append("p" + i + ".data->>'value'");
					}
					
					joins  += "LEFT JOIN processdata p" + i + " ON processes.id = p" + i + ".processid AND p" 
								+i+".parameterid = " + headings.getJSONObject(i).getInt("id")+" \n";
				}
				
				query = "SELECT pn.id,pn.p_number,processtypesid"+ valuebuff.toString()+" FROM processes "+ joins +
						"LEFT JOIN pnumbers pn ON pn.id = processes.id "+
						"WHERE processes.id = ANY('{"+samBuff.toString()+"}'::int[])";
				pStmt = dBconn.conn.prepareStatement(query);
				data = dBconn.getSearchTable(pStmt);

			}
			
			if (output.equalsIgnoreCase("json")){
			    response.setContentType("application/json");
				// compose answer JSON
				result.put("headings",headings);
				result.put("status",status);
				result.put("data",data);
				result.put("strings",dBconn.getStrings(stringkeys));
				result.put("objectnames",samplenames);
				result.put("searchid", id);
				result.put("type",type);
				result.put("inparams", inParams);
			    out.println(result.toString());
			} 
			
			if (output.equalsIgnoreCase("csv")){
			    response.setContentType("text/plain");
				// compose answer
				for (int i=0; i<headings.length();i++){
					out.print(headings.getString(i)+";");
				}
			    out.println(result.toString());
			}
			
			dBconn.closeDB();

    	} catch (SQLException e) {
    		System.err.println("Search: Problems with SQL query for search");
    		response.setStatus(404);
    		e.printStackTrace();
			status="Problems with SQL query for search";
    	} catch (JSONException e) {
			System.err.println("Search: JSON Problem while performing search");
    		response.setStatus(404);
    		e.printStackTrace();
			status="JSON Problem while performing search";
    	} catch (Exception e2) {
			System.err.println("Search: Strange Problem while performing search");
			status="Strange Problem while performing search";
    		response.setStatus(404);
    		e2.printStackTrace();
    	} }
	}}	}