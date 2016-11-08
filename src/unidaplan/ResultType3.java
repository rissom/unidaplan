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

	public class ResultType3 extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doPost(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {
		Authentificator authentificator = new Authentificator();
		int userID = authentificator.GetUserID(request,response);
		if (userID > 0) {
		JSONArray parameters = null;
		JSONArray sResults = null;
		JSONArray samplenames = null;
		JSONArray processnumbers = null;
		String status = "ok";
		PreparedStatement pStmt;
		ArrayList<String> stringkeys = new ArrayList<String>(); 
		JSONObject search = null;
	    request.setCharacterEncoding("utf-8");
	    response.setCharacterEncoding("utf-8");
	    PrintWriter out = response.getWriter();
	 	DBconnection dBconn = new DBconnection();
	    JSONArray inParams = new JSONArray();
	    JSONObject result = new JSONObject();
	    int searchID = -1;
	    String operationString = "AND";
	    String output = "json";
	    int datatype = 0;

	 
	    // Read json input

	  	request.setCharacterEncoding("utf-8");
	    String in = request.getReader().readLine();
	    JSONObject  jsonIn = null;
	    try {
			  jsonIn = new JSONObject(in);
		} catch (JSONException e) {
			response.setStatus(404);
			System.err.println("Result: Input is not valid JSON");
			status = "no parameters for performing search";
		}
		response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    
	    
	  	if (status == "ok"){ 
	    try {  
	    	searchID = jsonIn.getInt("searchid");
	    	if (jsonIn.has("output")){
	    		output = jsonIn.getString("output");
	    	}
	    	inParams = jsonIn.getJSONArray("parameters");
	    } catch (JSONException e) {
			System.err.println("Result: input parameters missing");
    		response.setStatus(404);
			status = "searchid is missing";
		}}
	    
	  	
	  	// check user privileges 
	    if (status == "ok"){
	    try{

		    dBconn.startDB();
		    pStmt = dBconn.conn.prepareStatement( 	
		    			  "SELECT getSearchRights(vuserid := ?, vsearchid := ?)");
			pStmt.setInt(1, userID);
			pStmt.setInt(2, searchID);
			String privilege = dBconn.getSingleStringValue(pStmt);
			if (!privilege.equals("w") || privilege.equals("r")){
				response.setStatus(401);
				status = "not allowed";
				throw new Exception("not allowed!");
			}
		    
		    
	    	// get basic search data (id,name,owner,operation)
			pStmt = dBconn.conn.prepareStatement( 	
					      "SELECT "
					    + "  operation "
					    + "FROM searches "
					    + "WHERE id = ?");
			pStmt.setInt(1, searchID);
			search = dBconn.jsonObjectFromPreparedStmt(pStmt);
	    	operationString = search.getBoolean("operation") ? "AND" : "OR";
			pStmt.close();		
			
			
			// get the searchparameters from the database		
			pStmt = dBconn.conn.prepareStatement(
						  "SELECT "
						+ "  poparameter AS pid, "
						+ "  poparameter, "
						+ "  comparison, "
						+ "  value, "
				 		+ "  COALESCE (po_parameters.stringkeyname,paramdef.stringkeyname) AS stringkeyname,"
				 		+ "  paramdef.datatype \n"
						+ "FROM searchpo "
						+ "JOIN po_parameters ON (po_parameters.id = poparameter) "
						+ "JOIN paramdef ON (paramdef.id = po_parameters.definition) "
						+ "WHERE search = ?");			
			pStmt.setInt(1,searchID);
			parameters = dBconn.jsonArrayFromPreparedStmt(pStmt);
			
			
			
			
			String query = "SELECT "
						 + "  samplesinprocess.id, "
					     + "  processes.id AS processid, "
					     + "  samplesinprocess.sampleid "
					     + "FROM processes "
					     + "JOIN samplesinprocess ON samplesinprocess.processid = processes.id ";
	
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

				query += " JOIN spdata p" + i + " ON p" + i + ".sip = samplesinprocess.id AND p" 
						 + i + ".parameterid = " + parameter.getInt("pid");
				if (i>0) {
					where += " " + operationString + " ";
				}
				where += "compare (val := p" + i + ".data, "
						+ "datatype := '" + parameter.getString("datatype") + "', "
						+ "comparator := " + parameter.getInt("comparison") + ", "
						+ "comval := '" + parameter.getString("value") + "')";
			}
			query += " WHERE "+ where ;
			pStmt = dBconn.conn.prepareStatement(query);
			sResults = dBconn.jsonArrayFromPreparedStmt(pStmt);
			pStmt.close();
			
			
			JSONArray headings = null;
			JSONArray data = null;

			
			if (sResults != null){
				
				// get data for samples
				StringBuilder buff = new StringBuilder(); // join numbers with commas
				String sep = "";
				for (int i = 0; i < sResults.length(); i++){
					buff.append(sep);
					buff.append(sResults.getJSONObject(i).getInt("sampleid"));
					sep = ",";
				}
				pStmt = dBconn.conn.prepareStatement(
						  "SELECT "
						+ "  id, "
						+ "  name, "
						+ "  typeid "
						+ "FROM samplenames "
						+ "WHERE id = ANY('{"
						+ buff.toString() + "}'::int[])");
				samplenames = dBconn.jsonArrayFromPreparedStmt(pStmt);
				
				
				// get data for processes
				StringBuilder buff2 = new StringBuilder(); // join numbers with commas
				String sep2 = "";
				for (int i = 0; i < sResults.length(); i++){
					buff.append(sep2);
					buff.append(sResults.getJSONObject(i).getInt("processid"));
					sep2 = ",";
				}
				pStmt = dBconn.conn.prepareStatement(
						  "SELECT "
						+ "  id, "
						+ "  p_number AS processnumber, "
						+ "  processtype "
						+ "FROM pnumbers "
						+ "WHERE id = ANY('{"
						+ buff2.toString() + "}'::int[])");
				processnumbers = dBconn.jsonArrayFromPreparedStmt(pStmt);


				
				//create headings
				pStmt = dBconn.conn.prepareStatement(
						"  SELECT "
						+ "  po_parameters.id,"
						+ "  COALESCE (po_parameters.stringkeyname,paramdef.stringkeyname) AS stringkeyname, "
						+ "  paramdef.stringkeyunit, "
						+ "  paramdef.datatype "
						+ "FROM searches "
						+ "JOIN posearchoutput ON posearchoutput.search = searches.id "
						+ "JOIN po_parameters ON po_parameters.id = poparameter "
						+ "JOIN paramdef ON po_parameters.definition = paramdef.id "
						+ "WHERE searches.id = ? "
						+ "ORDER BY posearchoutput.position");
				
				/* "UNION ALL */
				 /* "SELECT "
				+"'object' AS type,"
				+"ot_parameters.id,"
				+"oso.position, "
				+"COALESCE (ot_parameters.stringkeyname,pa.stringkeyname) AS stringkeyname, "
				+"pa.stringkeyunit," 
				+"pa.datatype " 
				+"FROM osearchoutput oso "
				+"JOIN ot_parameters ON ot_parameters.id=oso.otparameter " 
				+"JOIN paramdef pa ON ot_parameters.definition=pa.id " 
				+"WHERE oso.search=? "
				+"UNION ALL "
				+"SELECT "
				+"'process' AS type, "
				+"p_parameters.id, "
				+"pso.position + 100000, "
				+"COALESCE (p_parameters.stringkeyname,pa.stringkeyname) AS stringkeyname, " 
				+"pa.stringkeyunit," 
				+"pa.datatype " 
				+"FROM psearchoutput pso "
				+"JOIN p_parameters ON p_parameters.id=pso.pparameter " 
				+"JOIN paramdef pa ON p_parameters.definition=pa.id "
				+"WHERE pso.search=? "
				+"ORDER BY position"); */
				
				
				pStmt.setInt(1, searchID);
				headings = dBconn.jsonArrayFromPreparedStmt(pStmt);
				JSONObject heading = new JSONObject(); 
				if (headings.length() > 0){
					for (int i = 0; i < headings.length(); i++){
						heading = headings.getJSONObject(i);
						if (heading.has("stringkeyname")){
							stringkeys.add(Integer.toString(heading.getInt("stringkeyname")));
						}
						if (heading.has("stringkeyunit") && heading.getInt("datatype") < 4){
							stringkeys.add(Integer.toString(heading.getInt("stringkeyunit")));
						}
						if (heading.has("stringkeyunit") && heading.getInt("datatype") > 3){
							heading.remove("stringkeyunit");
						}
						heading.put("datatype",Unidatoolkit.Datatypes[heading.getInt("datatype")]);
					}
				}
				pStmt.close();
				
				
				// build Array of Processes and Samples
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
					String dt = headings.getJSONObject(i).getString("datatype");
					if (dt.equals("date") || dt.equals("timestamp")){
						valuebuff.append("p" + i + ".data->>'date'");
					} else {		
						valuebuff.append("p" + i + ".data->>'value'");
					}
					
					joins += "LEFT JOIN spdata p" + i + " ON samplesinprocess.id = p" + i +
							  ".sip AND p" + i + ".parameterid =" +
							  headings.getJSONObject(i).getInt("id") + " \n";
				}
				
				pStmt = dBconn.conn.prepareStatement(
						  "SELECT "
						+ "  pnum.id AS processid, "
						+ "  pnum.p_number AS processnumber, "		  
						+ "  sn.id AS sampleid, "
						+ "  sn.name AS samplename "
						+  valuebuff.toString() + " "
						+ "FROM samplesinprocess " + joins 
						+ "LEFT JOIN samplenames sn ON sn.id = samplesinprocess.sampleid " 
						+ "LEFT JOIN pnumbers pnum ON pnum.id = samplesinprocess.processid " +
						  "WHERE samplesinprocess.id = ANY('{" + samBuff.toString() + "}'::int[])");
				data = dBconn.getSearchTable(pStmt);
			}
				
			
			// get data for processes
			if (sResults != null){
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
				processnumbers = dBconn.jsonArrayFromPreparedStmt(pStmt);
			}
				
				
				
			
			
			if (output.equalsIgnoreCase("json")){
			    response.setContentType("application/json");
				// compose answer JSON
				result.put("headings",headings);
				result.put("type",3);
				result.put("status",status);
				result.put("data",data);
				result.put("strings",dBconn.getStrings(stringkeys));
				result.put("objectnames",samplenames);
				result.put("processnumbers",processnumbers);
				result.put("searchid", searchID);
				result.put("inparams", inParams);
			    out.println(result.toString());
			} 
			
			if (output.equalsIgnoreCase("csv")){
			    response.setContentType("text/plain");
				// compose answer
				for (int i = 0; i < headings.length(); i++){
					out.print(headings.getString(i) + ";");
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