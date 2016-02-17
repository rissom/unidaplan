package unidaplan;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

	public class ResultType4 extends HttpServlet {
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
		int type;
		String status="ok";
		PreparedStatement pStmt;
		ArrayList<String> stringkeys = new ArrayList<String>(); 
		JSONObject search = null;
	    request.setCharacterEncoding("utf-8");
	    response.setCharacterEncoding("utf-8");
	    PrintWriter out = response.getWriter();
	 	DBconnection dBconn=new DBconnection();
	    JSONArray inSampleParams = new JSONArray();
	    JSONArray inProcessParams = new JSONArray();
	    JSONObject result = new JSONObject();
	    int searchID=-1;
	    String output="json";
	    
	    
	    // Create a set of the datatypes which are stored as strings
	    Set<Integer> stringTypes = new HashSet<Integer>();
	    stringTypes.add(4);
	    stringTypes.add(5);
	    stringTypes.add(6);
	    stringTypes.add(10);
	    stringTypes.add(11);

	 
	  	
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
	    	searchID =jsonIn.getInt("searchid");
	    	if (jsonIn.has("output")){
	    		output = jsonIn.getString("output");
	    	}
	    	inSampleParams = jsonIn.getJSONArray("sparameters");
	    	inProcessParams = jsonIn.getJSONArray("pparameters");

	    } catch (JSONException e) {
			System.err.println("Result: input parameters are missing");
    		response.setStatus(404);
			status="searchid is missing";
		}}
	    
	    if (status=="ok"){
	    try{

		    dBconn.startDB();
	    	// get basic search data (id,name,owner,operation)
			pStmt= dBconn.conn.prepareStatement( 	
			    "SELECT operation,type FROM searches "
			   +"WHERE id=?");
			pStmt.setInt(1, searchID);
			search=dBconn.jsonObjectFromPreparedStmt(pStmt);
			type = search.getInt("type");
	    	if (type!=4){
	    		throw (new Exception("Wrong Type"));
	    	}
	    	
			pStmt.close();
			
			// get the searchparameters from the database
			
			String query = "SELECT otparameter AS pid, comparison, value, "
			  		 +"COALESCE(ot_parameters.stringkeyname,paramdef.stringkeyname) AS stringkeyname, "
					 +"paramdef.datatype "
					 +"FROM searchobject "
					 +"JOIN ot_parameters ON (ot_parameters.id=otparameter) "
					 +"JOIN paramdef ON (paramdef.id=ot_parameters.definition) "
					 +"WHERE search=?";
			String[] otables= {"o_integer_data","o_float_data","o_measurement_data","o_string_data","o_timestamp_data"};
			
			pStmt= dBconn.conn.prepareStatement(query);
			pStmt.setInt(1,searchID);
			parameters = dBconn.jsonArrayFromPreparedStmt(pStmt);
		
			String resultQuery = "WITH \n" 
					+"-- Find samples and processes with \n"
					+"-- search criteria for samples s1,s2,... and processes p1,p2,... \n"
					+"psamples AS ( \n"
					+"SELECT samples.id AS sampleid, processes.id AS processid \n"
					+"FROM samples \n"
					+"JOIN samplesinprocess sp ON (sp.sampleid=samples.id) \n"
					+"JOIN processes ON processes.id=sp.processid \n";
			
		
			int datatype;
			
			for (int i=0; i<parameters.length();i++){
				JSONObject parameter=parameters.getJSONObject(i);
				// find corresponding inParameter
				for (int j=0; j<parameters.length();j++){
					if (inSampleParams.getJSONObject(j).getInt("pid")==parameter.getInt("pid")) {
						parameter.put("comparison", inSampleParams.getJSONObject(j).getInt("comparison"));
						parameter.put("value", inSampleParams.getJSONObject(j).getString("value"));
					}
				}
				stringkeys.add(Integer.toString(parameter.getInt("stringkeyname")));
				datatype=parameter.getInt("datatype");

				String colon="";
			    
				if (stringTypes.contains(datatype)){colon="'";}
				String value=parameter.getString("value");
				String notPrefix="";
				if (parameter.getInt("comparison")==4){ notPrefix="NOT ";}
				if (parameter.getInt("comparison")==5){value="%"+value+"%";}
				resultQuery+=" JOIN "+otables[datatype-1]+" s"+i+" ON (s"+i+".objectid=samples.id AND s"
						+i+".ot_parameter_id="+parameter.getInt("pid")+" AND "+notPrefix+"s"+i+".value "+
						Unidatoolkit.comparators[parameter.getInt("comparison")-1]+colon+value+colon+") \n";
			}
			
			
			// process search criteria
			
			pStmt = dBconn.conn.prepareStatement(
					  "SELECT pparameter AS pid, comparison, value, \n"
			  		 +"p_parameters.stringkeyname,p_parameters.stringkeyname,paramdef.datatype \n"
					 +"FROM searchprocess \n"
					 +"JOIN p_parameters ON (p_parameters.id=pparameter) \n"
					 +"JOIN paramdef ON (paramdef.id=p_parameters.definition) \n"
					 +"WHERE search=? \n"
				  );
			pStmt.setInt(1,searchID);
			parameters = dBconn.jsonArrayFromPreparedStmt(pStmt);


			String[] pptables= {"p_integer_data","p_float_data","p_measurement_data","p_string_data","p_timestamp_data"};
			

			for (int i=0; i<parameters.length();i++){
				JSONObject parameter=parameters.getJSONObject(i);
				// find corresponding inParameter
				for (int j=0; j<parameters.length();j++){
					if (inProcessParams.getJSONObject(j).getInt("pid")==parameter.getInt("pid")) {
						parameter.put("comparison", inProcessParams.getJSONObject(j).getInt("comparison"));
						parameter.put("value", inProcessParams.getJSONObject(j).getString("value"));
					}
				}
				stringkeys.add(Integer.toString(parameter.getInt("stringkeyname")));
				datatype=parameter.getInt("datatype");

				String colon="";  // String type datatypes have to be quoted.
				if (stringTypes.contains(datatype)){colon="'";}
				String value=parameter.getString("value");
				String notPrefix="";
				if (parameter.getInt("comparison")==4){ notPrefix="NOT ";}
				if (parameter.getInt("comparison")==5){value="%"+value+"%";}
				resultQuery += " JOIN "+pptables[datatype-1]+" p"+i+" ON p"+i+".processid=processes.id AND p"
							   +i+".p_parameter_id="+parameter.getInt("pid")+" AND"+
							   notPrefix+" p"+i+".value "+Unidatoolkit.comparators[parameter.getInt("comparison")-1]+
							   colon+value+colon;
			}

			resultQuery +=	"), \n"
					+" \n"
					+"-- Distinct sample ids \n"
					+"dsamples AS ( \n"
					+"	SELECT sampleid  \n"
					+"	FROM psamples \n"
					+"	GROUP BY sampleid \n"
					+"), \n"
					+" \n"
					+" \n"
					+"-- Distinct process ids \n"
					+"dprocesses AS ( \n"
					+"	SELECT processid \n" 
					+"	FROM psamples \n"
					+"	GROUP BY processid \n"
					+"), \n"
					+" \n"
					+" \n"
					+"-- data for samples \n"
					+"sampledata AS ( \n"
					+"	SELECT  \n"
					+"		sampleid,  \n"
					+"		array_to_json(array_agg(asp.value ORDER BY position)) AS sampledata \n"
					+"		FROM dsamples \n"
					+"		LEFT JOIN osearchoutput oso ON oso.search=? \n"
					+"		LEFT JOIN acc_sample_parameters  asp ON (asp.id=oso.otparameter AND asp.objectid=dsamples.sampleid) \n"
					+"	GROUP BY dsamples.sampleid \n"
					+"	ORDER BY max(oso.position) \n"
					+"), \n"
					+" \n"
					+"-- data for processes \n"
					+"processdata AS ( \n"
					+"	SELECT \n"
					+"		max(dprocesses.processid) AS process, \n"
					+"		max(pnumbers.processtype) AS processtype, \n"
					+"		max(pnumbers.p_number) AS processnumber, \n"
					+"		array_to_json(array_agg(app.value ORDER BY position)) AS processdata \n"
					+"		FROM dprocesses  \n"
					+"  JOIN pnumbers ON pnumbers.id = dprocesses.processid"
					+"	LEFT JOIN psearchoutput pso ON pso.search=? \n"
					+"	LEFT JOIN acc_process_parameters app ON (app.ppid=pso.pparameter AND app.processid=dprocesses.processid) \n"
					+"	GROUP BY dprocesses.processid \n"
					+") \n"
					+" \n"
					+"-- MAIN QUERY -- \n"
					+"-- composes a JSON-Object for every sample with an Array of JSON Objects for every process \n"
					+"SELECT json_build_object( \n"
					+"		  'sampleid',psamples.sampleid, \n"
					+"		  'sampletype',(array_agg(sn.typeid))[1], \n"
					+" 		  'samplename',(array_agg(sn.name))[1], \n"
					+"		  'sampledata',(array_agg(sd.sampledata))[1],  \n"
					+"		  'processes',array_agg(json_build_object('process',processid,'processnumber',processnumber,'processtype',processtype,'processdata',processdata)) \n"
					+"		) AS samples \n"
					+"FROM psamples \n"
					+"JOIN samplenames sn ON sn.id=psamples.sampleid \n"
					+"JOIN sampledata sd ON sd.sampleid=psamples.sampleid \n"
					+"JOIN processdata pd ON pd.process=processid \n"
					+"GROUP BY psamples.sampleid \n";
			
			pStmt= dBconn.conn.prepareStatement(resultQuery);
			pStmt.setInt(1, searchID);
			pStmt.setInt(2, searchID);
//			System.out.println(pStmt.toString());
			sResults = dBconn.jsonArrayFromPreparedStmt(pStmt);
//			System.out.println(sResults.toString());
			pStmt.close();
			
			
			JSONArray headings= null;				
				
			//create headings
			pStmt= dBconn.conn.prepareStatement(
				"SELECT "
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
				+"pso.position +100000, "
				+"COALESCE (p_parameters.stringkeyname,pa.stringkeyname) AS stringkeyname, " 
				+"pa.stringkeyunit," 
				+"pa.datatype " 
				+"FROM psearchoutput pso "
				+"JOIN p_parameters ON p_parameters.id=pso.pparameter " 
				+"JOIN paramdef pa ON p_parameters.definition=pa.id "
				+"WHERE pso.search=? "
				+"ORDER BY position");
			pStmt.setInt(1, searchID);
			pStmt.setInt(2, searchID);

			headings = dBconn.jsonArrayFromPreparedStmt(pStmt);
			JSONObject heading = new JSONObject(); 
				if (headings.length()>0){
					for (int i=0;i<headings.length();i++){
						heading=headings.getJSONObject(i);
						if (heading.has("stringkeyname")){
							stringkeys.add(Integer.toString(heading.getInt("stringkeyname")));
						}
						if (heading.has("stringkeyunit") && heading.getInt("datatype")<4){
							stringkeys.add(Integer.toString(heading.getInt("stringkeyunit")));
						}
						if (heading.has("stringkeyunit") && heading.getInt("datatype")>3){
							heading.remove("stringkeyunit");
						}
						heading.remove("datatype");
					}
				}
			pStmt.close();
	

			
			if (output.equalsIgnoreCase("json")){
			    response.setContentType("application/json");
				// compose answer JSON
				result.put("headings",headings);
				result.put("status",status);
				result.put("type",4);
				result.put("samples",sResults);
				result.put("strings",dBconn.getStrings(stringkeys));
				result.put("objectnames",samplenames);
				result.put("searchid", searchID);
				result.put("insampleparams", inSampleParams);
				result.put("inprocessparams", inProcessParams);
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