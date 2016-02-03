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
		userID=userID+1;
		userID=userID-1;
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
	    String operationString="AND";
	    String output="json";

	 
	  	
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
			System.err.println("Search: searchid is missing");
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
			pStmt.setInt(1, id);
			search=dBconn.jsonObjectFromPreparedStmt(pStmt);
	    	operationString = search.getBoolean("operation")?"AND":"OR";
			pStmt.close();		
			
			// get the searchparameters according to searchtype from the database
			String query="";
			String[] ptables = null;
			String parameterTable="";
			type=search.getInt("type");
			switch (type){
				case 1:   // sample search
					query = "SELECT otparameter AS pid, comparison, value, "
					  		 +"COALESCE(ot_parameters.stringkeyname,paramdef.stringkeyname) AS stringkeyname, "
							 +"paramdef.datatype "
							 +"FROM searchobject "
							 +"JOIN ot_parameters ON (ot_parameters.id=otparameter) "
							 +"JOIN paramdef ON (paramdef.id=ot_parameters.definition) "
							 +"WHERE search=?";
					String[] otables= {"o_integer_data","o_float_data","o_measurement_data","o_string_data","o_timestamp_data"};
					ptables = otables;
					break;
				case 2:   // process search
					query = "SELECT pparameter AS pid, comparison, value, "
					  		 +"p_parameters.stringkeyname,p_parameters.stringkeyname,paramdef.datatype "
							 +"FROM searchprocess "
							 +"JOIN p_parameters ON (p_parameters.id=pparameter) "
							 +"JOIN paramdef ON (paramdef.id=p_parameters.definition) "
							 +"WHERE search=?";
					String[] pptables= {"p_integer_data","p_float_data","p_measurement_data","p_string_data","p_timestamp_data"};
					ptables = pptables; // Warum kann man keine Arraykonstanten definieren???
					break;
				default : // sample specific processparameter
					query = "SELECT poparameter AS pid, poparameter, comparison, value, "
					 		 +"po_parameters.stringkeyname,po_parameters.stringkeyname,paramdef.datatype "
							 +"FROM searchpo "
							 +"JOIN po_parameters ON (po_parameters.id=poparameter) "
							 +"JOIN paramdef ON (paramdef.id=po_parameters.definition) "
							 +"WHERE search=?";
					String[] potables= {"po_integer_data","po_float_data","po_measurement_data","po_string_data","po_timestamp_data"};
					ptables = potables;
					break;
			}
			pStmt= dBconn.conn.prepareStatement(query);
			pStmt.setInt(1,id);
			parameters = dBconn.jsonArrayFromPreparedStmt(pStmt);
			String idString="";
			String idString2="";
			String tString="";
			switch (type){
			case 1:   // sample search
				query="SELECT samples.id FROM samples ";
				tString="samples";
				idString= "objectid";
				idString2="ot_parameter_id";
				break;
			case 2:  // process search
				query="SELECT processes.id FROM processes ";
				tString="processes";
				idString= "processid";
				idString2="p_parameter_id";
				break;
			default : // sample specific processparameter
				tString="processes";
				query="SELECT processes.id FROM processes ";
				idString= "processid";
				idString2="po_parameter_id";
			}
			String where="";
			int datatype;
			String[] comparators= {"<",">","=","= ","LIKE "};

			for (int i=0; i<parameters.length();i++){
				JSONObject parameter=parameters.getJSONObject(i);
				// find corresponding inParameter
				for (int j=0; j<parameters.length();j++){
					if (inParams.getJSONObject(j).getInt("pid")==parameter.getInt("pid")) {
						parameter.put("comparison", inParams.getJSONObject(j).getInt("comparison"));
						parameter.put("value", inParams.getJSONObject(j).getString("value"));
					}
				}
				stringkeys.add(Integer.toString(parameter.getInt("stringkeyname")));
				datatype=parameter.getInt("datatype");

				String colon="";
			    Set<Integer> set = new HashSet<Integer>();
	            set.add(4);
	            set.add(5);
	            set.add(6);
	            set.add(10);
	            set.add(11);
				if (set.contains(datatype)){colon="'";}
				String value=parameter.getString("value");
				String notPrefix="";
				if (parameter.getInt("comparison")==4){ notPrefix="NOT ";}
				if (parameter.getInt("comparison")==5){value="%"+value+"%";}
				parameterTable= ptables[datatype-1];
				query=query+" JOIN "+parameterTable+" p"+i+" ON p"+i+"."+idString+"="+tString+".id AND p"
						+i+"."+idString2+"="+parameter.getInt("pid");
				if (i>0) {where+=" "+operationString;}
				where=where+notPrefix+" p"+i+".value "+comparators[parameter.getInt("comparison")-1]+
						colon+value+colon;
			}
			query += " WHERE "+ where +" GROUP BY "+tString+".id";

			pStmt= dBconn.conn.prepareStatement(query);
			sResults = dBconn.jsonArrayFromPreparedStmt(pStmt);
//			System.out.println(pStmt.toString());
			pStmt.close();
			
			
			JSONArray headings= null;
			JSONArray data= null;

			
			// get data for samples
			if (sResults!=null && type==1){
				query="SELECT id,name,typeid FROM samplenames WHERE id=ANY('{";
				StringBuilder buff = new StringBuilder(); // join numbers with commas
				String sep = "";
				for (int i=0; i<sResults.length();i++){
					buff.append(sep);
					buff.append(sResults.getJSONObject(i).getInt("id"));
					sep = ",";
				}
				query+= buff.toString() + "}'::int[])";
				pStmt= dBconn.conn.prepareStatement(query);
				samplenames = dBconn.jsonArrayFromPreparedStmt(pStmt);

				
				//create headings
				pStmt= dBconn.conn.prepareStatement(
						"SELECT ot_parameters.id,"
						+"COALESCE (ot_parameters.stringkeyname,paramdef.stringkeyname) AS stringkeyname, "
						+"paramdef.stringkeyunit, "
						+"paramdef.datatype "
						+"FROM searches "
						+"JOIN osearchoutput ON osearchoutput.search = searches.id "
						+"JOIN ot_parameters ON ot_parameters.id=otparameter "
						+"JOIN paramdef ON ot_parameters.definition=paramdef.id "
						+"WHERE searches.id=?");
				pStmt.setInt(1, id);
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
				
				
				// build sample Array
				StringBuilder samBuff = new StringBuilder(); // join numbers with commas
				sep = "";
				for (int i=0; i<sResults.length();i++){
					samBuff.append(sep);
					samBuff.append(sResults.getJSONObject(i).getInt("id"));
					sep = ",";
				}
				
				// build SELECT statement and JOIN statements
				String joins="";
				sep = "";
				StringBuilder valuebuff = new StringBuilder(); // comma separated params for Select


				for (int i=0; i<headings.length();i++){
					valuebuff.append(",");
					valuebuff.append("p"+i+".value");
					
					joins  += "LEFT JOIN acc_sample_parameters p"+i+" ON samples.id=p"+i+".objectid AND p"+i+".id="+
							headings.getJSONObject(i).getInt("id")+" \n";
				}
				
				query = "SELECT sn.id,sn.name,typeid"+ valuebuff.toString()+" FROM samples "+ joins +
						"LEFT JOIN samplenames sn ON sn.id=samples.id "+
						"WHERE samples.id = ANY('{"+samBuff.toString()+"}'::int[])";
				pStmt= dBconn.conn.prepareStatement(query);
				data = dBconn.getSearchTable(pStmt);

			}
				
			
			// get data for processes
			if (sResults!=null && type==2){
				query="SELECT id,p_number,processtype FROM pnumbers WHERE id=ANY('{";
				StringBuilder buff = new StringBuilder(); // join numbers with commas
				String sep = "";
				for (int i=0; i<sResults.length();i++){
					buff.append(sep);
					buff.append(sResults.getJSONObject(i).getInt("id"));
					sep = ",";
				}
				query+= buff.toString() + "}'::int[])";
				pStmt= dBconn.conn.prepareStatement(query);
				samplenames = dBconn.jsonArrayFromPreparedStmt(pStmt);

				
				//create headings
				pStmt= dBconn.conn.prepareStatement(
						"SELECT p_parameters.id,p_parameters.stringkeyname, "
						+"paramdef.stringkeyunit FROM searches "
						+"JOIN psearchoutput ON psearchoutput.search = searches.id "
						+"JOIN p_parameters ON p_parameters.id=pparameter "
						+"JOIN paramdef ON p_parameters.definition=paramdef.id "
						+"WHERE searches.id=?");
				pStmt.setInt(1, id);
				headings = dBconn.jsonArrayFromPreparedStmt(pStmt);
				JSONObject heading = new JSONObject(); 
				if (headings.length()>0){
					for (int i=0;i<headings.length();i++){
						heading=headings.getJSONObject(i);
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
				for (int i=0; i<sResults.length();i++){
					samBuff.append(sep);
					samBuff.append(sResults.getJSONObject(i).getInt("id"));
					sep = ",";
				}
				
				// build SELECT statement and JOIN statements
				String joins="";
				sep = "";
				StringBuilder valuebuff = new StringBuilder(); // comma separated params for Select


				for (int i=0; i<headings.length();i++){
					valuebuff.append(",");
					valuebuff.append("p"+i+".value");
					
					joins  += "LEFT JOIN acc_process_parameters p"+i+" ON processes.id=p"+i+".processid AND p"+i+".ppid="+
							headings.getJSONObject(i).getInt("id")+" \n";
				}
				
				query = "SELECT pn.id,pn.p_number,processtypesid"+ valuebuff.toString()+" FROM processes "+ joins +
						"LEFT JOIN pnumbers pn ON pn.id=processes.id "+
						"WHERE processes.id = ANY('{"+samBuff.toString()+"}'::int[])";
				pStmt= dBconn.conn.prepareStatement(query);
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