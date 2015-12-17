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
		JSONArray parameters = null;
		JSONArray samples = null;
		JSONArray samplenames = null;
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
	    JSONArray inParams = new JSONArray();
	    JSONObject result = new JSONObject();
	    int id=-1;
	    String operationString="AND";
	 
	  	
	  	request.setCharacterEncoding("utf-8");
	    String in = request.getReader().readLine();
	    JSONObject  jsonIn = null;
	    try {
			  jsonIn = new JSONObject(in);
		} catch (JSONException e) {
			response.setStatus(404);
			System.err.println("UpdateProcessTypeData: Input is not valid JSON");
		}
		response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	  	
	    try {  
	    	id =jsonIn.getInt("searchid");
	    	inParams = jsonIn.getJSONArray("parameters");

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
			switch (search.getInt("type")){
				case 1:   // sample search
					query = "SELECT otparameter AS pid, comparison, value, "
					  		 +"ot_parameters.stringkeyname,ot_parameters.stringkeyname,paramdef.datatype "
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
			query="SELECT samples.id FROM samples ";
			String where="";
			int datatype;
			String[] comparators= {"<",">","=","NOT"};

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
				parameterTable= ptables[datatype-1];
				query=query+" JOIN "+parameterTable+" p"+i+" ON p"+i+".objectid=samples.id AND p"
						+i+".ot_parameter_id="+parameter.getInt("pid");
				if (i>0) {where+=" "+operationString;}
				where=where+" p"+i+".value "+comparators[parameter.getInt("comparison")-1]+
						" "+parameter.getString("value");
			}
			query += " WHERE "+ where +" GROUP BY samples.id";

			pStmt= dBconn.conn.prepareStatement(query);
			samples = dBconn.jsonArrayFromPreparedStmt(pStmt);
			pStmt.close();
			
			// get samplenames (remove this shit)
			if (samples!=null){
				query="SELECT id,name,typeid FROM samplenames WHERE id=ANY('{";
				StringBuilder buff = new StringBuilder(); // join numbers with commas
				String sep = "";
				for (int i=0; i<samples.length();i++){
					buff.append(sep);
					buff.append(samples.getJSONObject(i).getInt("id"));
					sep = ",";
				}
				query+= buff.toString() + "}'::int[])";
				pStmt= dBconn.conn.prepareStatement(query);
				samplenames = dBconn.jsonArrayFromPreparedStmt(pStmt);

				
				//create headings
				JSONArray headings= null;
				pStmt= dBconn.conn.prepareStatement(
						"SELECT ot_parameters.id,ot_parameters.stringkeyname, "
						+"paramdef.stringkeyunit FROM searches "
						+"JOIN osearchoutput ON osearchoutput.search = searches.id "
						+"JOIN ot_parameters ON ot_parameters.id=otparameter "
						+"JOIN paramdef ON ot_parameters.definition=paramdef.id "
						+"WHERE searches.id=1");
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
				
				// get the data
				JSONArray data= null;
				String table="";
				switch (search.getInt("type")){
					case 1:   // sample search
						table = "acc_sample_parameters";
						break;
					case 2:   // process search
						table = "acc_process_parameters";
						break;
					default : // sample specific processparameter
						table="acc_ps_parameters";
				}
				// build sample Array
				StringBuilder samBuff = new StringBuilder(); // join numbers with commas
				sep = "";
				for (int i=0; i<samples.length();i++){
					samBuff.append(sep);
					samBuff.append(samples.getJSONObject(i).getInt("id"));
					sep = ",";
				}
				
				// build SELECT statement and JOIN statements
				String joins="";
				sep = "";
				StringBuilder valuebuff = new StringBuilder(); // comma separated params for Select


				for (int i=0; i<headings.length();i++){
					valuebuff.append(",");
					valuebuff.append("p"+i+".value");
					
					joins  += "LEFT JOIN "+table+" p"+i+" ON samples.id=p"+i+".objectid AND p"+i+".id="+
							headings.getJSONObject(i).getInt("id")+" \n";
				}

				
//				query = "SELECT "+ valuebuff.toString()+" FROM samples "
//						+"LEFT JOIN acc_sample_parameters p0 ON samples.id=p0.objectid AND p0.id=3 "
//						+"LEFT JOIN acc_sample_parameters p1 ON samples.id=p1.objectid AND p1.id=5 "
//						+"WHERE samples.id = ANY('{1,2}'::int[])";
				
				query = "SELECT sn.id,sn.name,typeid"+ valuebuff.toString()+" FROM samples "+ joins +
						"LEFT JOIN samplenames sn ON sn.id=samples.id "+
						"WHERE samples.id = ANY('{"+samBuff.toString()+"}'::int[])";
				pStmt= dBconn.conn.prepareStatement(query);
				data = dBconn.getSearchTable(pStmt);

			
				
				
				// compose answer
				result.put("headings",headings);
				result.put("status",status);
				result.put("data",data);
				result.put("strings",dBconn.getStrings(stringkeys));
				result.put("samplenames",samplenames);
				result.put("searchid", id);
			}
			
 		    
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
    	} 
	    
	    out.println(result.toString());
		dBconn.closeDB();
	}}	