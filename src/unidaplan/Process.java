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

/**
 * Servlet implementation class Process
 */
public class Process extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static JSONObject jsProcess;
       


	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    	
    	Boolean editable = false;
		boolean found=false;


    	Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
		
		ArrayList<String> stringkeys = new ArrayList<String>(); // Array for translation strings
      
		response.setContentType("application/json");
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		DBconnection dBconn = new DBconnection();
		String privilege = "n";
		int processID = 1;
  	  	int processTypeID = 1;
  	  	int pnumber = 0;
  	  	JSONArray parametergrps = null;
  	  	JSONArray fields = null;
  	  	
  	  	try  {
  	  		processID=Integer.parseInt(request.getParameter("id")); 
  	  	}
  	  	catch (Exception e1) {
  	  		processID=1;
  	  		System.err.print("Process: no object ID given!");
//  		e1.printStackTrace();
  	  	}

  	  	PreparedStatement pStmt = null;

  	  	try {

  	  		dBconn.startDB();
  	  		
  	  		// check privileges
	        pStmt= dBconn.conn.prepareStatement( 	
					"SELECT getProcessRights(vuserid:=?,vprocess:=?)");
			pStmt.setInt(1,userID);
			pStmt.setInt(2,processID);
			privilege=dBconn.getSingleStringValue(pStmt);
			pStmt.close();
	        
	        editable= privilege!=null && privilege.equals("w");
  	    } catch (SQLException e) { 
	  		System.err.println("Problems with SQL query");
	  		e.printStackTrace();
	  	} catch (JSONException e){
	  		System.err.println("Problems creating JSON");
	  		e.printStackTrace();
	  	} catch (Exception e) {
	  		System.err.println("Strange Problems");
	  		e.printStackTrace();
	  	}
  	  	

 
  	  	if (privilege.equals("r")||privilege.equals("w")){
  	  	  	try{
	  	  		// get number, type and status 
	  	  		pStmt= dBconn.conn.prepareStatement(
					  "SELECT "
					+ "  processes.id, "
					+ "  processes.processtypesid as processtype, "
					+ "  ptd.value AS date, "
					+ "  n1.value AS pnumber, "
					+ "  processtypes.name AS pt_string_key, "
					+ "  n2.value AS status, "
					+ "  pp3.id AS statuspid "
					+ "FROM processes "
					+ "JOIN processtypes ON (processes.processtypesid=processtypes.id) "
					+ "JOIN p_parameters pp1 ON (pp1.definition=10 AND pp1.ProcesstypeID=processes.processtypesid) " // date
					+ "JOIN p_parameters pp2 ON (pp2.definition=8 AND pp2.ProcesstypeID=processes.processtypesid) " // number
					+ "JOIN p_parameters pp3 ON (pp3.definition=1 AND pp3.ProcesstypeID=processes.processtypesid) " // status
					+ "LEFT JOIN p_timestamp_data ptd ON (ptd.processID=processes.id AND ptd.P_Parameter_ID=pp1.id) "
					+ "LEFT JOIN p_integer_data n1 ON (n1.ProcessID=processes.id AND n1.P_Parameter_ID=pp2.id) "
					+ "LEFT JOIN p_integer_data n2 ON (n2.ProcessID=processes.id AND n2.P_Parameter_ID=pp3.id) "
					+ "WHERE processes.id=?");
	  	  		pStmt.setInt(1, processID);
	  	  		jsProcess= dBconn.jsonObjectFromPreparedStmt(pStmt);
	  	  		if (jsProcess.length()>0) {
	  	  			processTypeID = jsProcess.getInt("processtype");
	  	  			pnumber = jsProcess.getInt("pnumber");
	  	  			found = true;
	  	  			stringkeys.add(Integer.toString(jsProcess.getInt("pt_string_key")));
	  	  		}else{
	  	  			System.err.println("no such process");
	  	  			response.setStatus(404);
	  	  			found=false;
	  	  		}
			
	  	  	} catch (SQLException e) { 
	  	  		System.err.println("Problems with SQL query");
	  	  		e.printStackTrace();
	  	  	} catch (JSONException e){
	  	  		System.err.println("Problems creating JSON");
	  	  		e.printStackTrace();
	  	  	} catch (Exception e) {
	  	  		System.err.println("Strange Problems");
	  	  		e.printStackTrace();
	  	  	}
				
			if (found){
			    // get next process
			    try {      
					pStmt=dBconn.conn.prepareStatement( 
					  "SELECT "
					+ "  id,p_number "
					+ "FROM pnumbers "
					+ "WHERE (p_number>? AND processtype=?) "
					+ "ORDER BY p_number LIMIT 1");
					pStmt.setInt(1,pnumber);
					pStmt.setInt(2,processTypeID);
					JSONObject next = dBconn.jsonObjectFromPreparedStmt(pStmt);
					if (next.length()>0) {
					jsProcess.put("next",next); } 
				} catch (SQLException e) {
					e.printStackTrace();
				} catch (JSONException e){
					System.err.println("Problems creating JSON for next process");
					e.printStackTrace();
				} catch (Exception e) {
					System.err.println("Strange Problems with the next process");
					e.printStackTrace();
				}	
					
		    
			    // get previous process
			    try {       
					pStmt=dBconn.conn.prepareStatement( 
					  "SELECT "
					+ "  id,p_number "
					+ "FROM pnumbers "
					+ "WHERE (p_number<? AND processtype=?) "
					+ "ORDER BY p_number DESC LIMIT 1");
					pStmt.setInt(1,pnumber);
					pStmt.setInt(2,processTypeID);
					JSONObject previous= dBconn.jsonObjectFromPreparedStmt(pStmt);
					if (previous.length()>0) {
					jsProcess.put("previous",previous); } 
				} catch (SQLException e) {
					System.err.println("Process: Problems with SQL query for previous process");
					e.printStackTrace();
				} catch (JSONException e){
					System.err.println("Process: Problems creating JSON for previous process");
					e.printStackTrace();
				} catch (Exception e) {
					System.err.println("Process: Strange Problems with the previous process");
					e.printStackTrace();
				}	
		    
		    
			    // get parametergroups
				try {
					pStmt= dBconn.conn.prepareStatement(
							"SELECT parametergroup, max(stringkey) AS paramgrpkey, min(p_parametergrps.pos) AS pos FROM p_parameters "+
							"JOIN p_parametergrps ON parametergroup=p_parametergrps.id "+
							"WHERE processtypeid=? GROUP BY parametergroup");
					pStmt.setInt(1,processTypeID);
					parametergrps=dBconn.jsonArrayFromPreparedStmt(pStmt);
					pStmt.close();
				} catch (SQLException e) {
					System.err.println("Problems with SQL query for parameters");
					e.printStackTrace();
				} catch (JSONException e){
					System.err.println("Problems creating JSON for parameters");
					e.printStackTrace();
				} catch (Exception e) {
					System.err.println("Strange Problems with the parameters");
					e.printStackTrace();
				}
				
		    
			    // get the process Parameters:
			    try{
			    	pStmt = dBconn.conn.prepareStatement(
			    	"SELECT "
			    	+ "p_parameters.id, "
			    	+ "parametergroup, "
			    	+ "compulsory, "
			    	+ "p_parameters.pos, "
					+" p_parameters.stringkeyname,  "
					+ "pid, "
					+ "value, "
					+ "p_parametergrps.id AS pgrpid, " 
					+ "p_parametergrps.stringkey as parametergrp_key, "
					+ "st.description, paramdef.datatype, "
					+ "paramdef.stringkeyunit as unit, "
					+ "p_parameters.definition "
					+ "FROM p_parameters "
					+ "JOIN p_parametergrps ON (p_parameters.Parametergroup=p_parametergrps.ID) " 
					+ "JOIN paramdef ON (paramdef.id=p_parameters.definition)"
					+ "LEFT JOIN acc_process_parameters a ON "
					+ "(a.processid=? AND a.ppid=p_parameters.id AND hidden=FALSE) "
					+ "JOIN String_key_table st ON st.id=p_parameters.stringkeyname "
					+ "WHERE (p_parameters.processtypeID=? AND p_parameters.id_field=FALSE AND p_parameters.hidden=FALSE) "
					+ "ORDER BY pos");
			    	pStmt.setInt(1,processID);
			    	pStmt.setInt(2,processTypeID);
					JSONArray parameters=dBconn.jsonArrayFromPreparedStmt(pStmt);
			
					if (parameters.length()>0 && parametergrps.length()>0) { 		
						for (int j=0;j<parametergrps.length();j++){
							JSONArray prmgrpprms=new JSONArray();
							JSONObject prmgrp=parametergrps.getJSONObject(j);
				      		stringkeys.add(Integer.toString(prmgrp.getInt("paramgrpkey")));				
				      		
					      	for (int i=0; i<parameters.length();i++) {  
					      		JSONObject tParam=parameters.getJSONObject(i);
					      		stringkeys.add(Integer.toString(tParam.getInt("stringkeyname")));
					      		if (tParam.has("parametergroup")&&
						      		tParam.getInt("parametergroup")==prmgrp.getInt("parametergroup")){		
					      			
						      		if (tParam.has("unit")){
							      		stringkeys.add(Integer.toString(tParam.getInt("unit")));
						      		}
					      			int datatype=tParam.getInt("datatype");
						      		tParam.remove("datatype");
						      		switch (datatype) {
						      		case 1: tParam.put("datatype","integer"); 
						      				if (tParam.has("value")){
						      					int x=Integer.parseInt(tParam.getString("value"));
						      					tParam.remove("value");
							      				tParam.put("value", x);
						      				}
						      				break;
						      		case 2: tParam.put("datatype","float"); 
						      				if (tParam.has("value")){
							      				double y=Double.parseDouble(tParam.getString("value"));
							      				tParam.remove("value");
							      				tParam.put("value", y);
						      				}
						      				break;
						      		case 3: tParam.put("datatype","measurement");  
						      				break;
						      		case 4: tParam.put("datatype","string"); 
						      				break;
						      		case 5: tParam.put("datatype","long string");  
						      				break;
						      		case 6: tParam.put("datatype","chooser"); 
								      		pStmt = dBconn.conn.prepareStatement(
								      					"SELECT string FROM possible_values "
								      					+"WHERE parameterid=? ORDER BY position");
								      		pStmt.setInt(1, tParam.getInt("definition"));
								      		JSONArray pvalues=dBconn.ArrayFromPreparedStmt(pStmt);
								      		tParam.put("possiblevalues", pvalues);
								      		pStmt.close();
						      				break;
						      		case 7: tParam.put("datatype","date");
						      				break;
						      		case 8: tParam.put("datatype","checkbox"); 
								      		if (tParam.has("value")){
												Boolean v = tParam.getString("value").equals("1");
												tParam.put("value", v);
											}
						      				break;
						      		case 9: tParam.put("datatype","timestamp");
						      				break;
						      		case 10: tParam.put("datatype","URL");
						      				break;
						      		default: tParam.put("datatype","undefined"); 
						      				break;	    
					      		}
							    prmgrpprms.put(tParam);
					      		}
					      	}
				      		prmgrp.put("parameter",prmgrpprms);
						}				
					}	
					jsProcess.put("parametergroups",parametergrps);
				} catch (SQLException e) {
					System.err.println("Problems with SQL query for parameters");
					e.printStackTrace();
				} catch (JSONException e){
					System.err.println("Problems creating JSON for parameters");
					e.printStackTrace();
				} catch (Exception e) {
					System.err.println("Strange Problems with the parameters");
					e.printStackTrace();
				}
		    
		    
			    // get sample related parameters
			    try{
			    	pStmt = dBconn.conn.prepareStatement(
			    	  "SELECT " 
			    	 +"COALESCE(po_parameters.stringkeyname,paramdef.stringkeyname) AS stringkeyname, "
			    	 +"COALESCE(po_parameters.description,paramdef.description) AS description, "
			    	 +"  po_parameters.id, "
					 +"	 hidden, "
					 +"  compulsory, "
					 +"  position, "
					 +"  paramdef.stringkeyunit AS unit, "
					 +"  paramdef.datatype, "
					 +"  paramdef.format, "
					 +"  paramdef.regex, "
					 +"  paramdef.min, "
					 +"  paramdef.max "
					 +"FROM po_parameters "
					 +"JOIN paramdef ON po_parameters.definition=paramdef.id "
					 +"WHERE processtypeid = (SELECT processtypesid FROM processes WHERE id=?) "
					 +"ORDER BY position ");
			    	pStmt.setInt(1,processID);
					fields = dBconn.jsonArrayFromPreparedStmt(pStmt);
					if (fields.length()>0){
						for (int i=0 ; i<fields.length(); i++){
							JSONObject field = fields.getJSONObject(i);
			  	  			stringkeys.add(Integer.toString(field.getInt("description")));
			  	  			stringkeys.add(Integer.toString(field.getInt("stringkeyname")));
			  	  			if (field.has("unit")){
			  	  				stringkeys.add(Integer.toString(field.getInt("unit")));
			  	  			}
						}
					}
					
					jsProcess.put("fields",fields);
			    } catch (SQLException e) {
					System.err.println("Problems with SQL query for parameters");
					e.printStackTrace();
				} catch (JSONException e){
					System.err.println("Problems creating JSON for parameters");
					e.printStackTrace();
				} catch (Exception e) {
					System.err.println("Strange Problems with the parameters");
					e.printStackTrace();
				}	  	
			    
			    // get the assigned objects 
			    try{
			    	pStmt = dBconn.conn.prepareStatement(
			    	  "SELECT sp.sampleid, sn.name, sn.typeid, sp.id AS opid  "
			    	+ "FROM samplesinprocess sp "
			    	+ "JOIN samplenames sn ON sp.sampleid=sn.id "
			    	+ "WHERE ProcessID = ?");
			    	pStmt.setInt(1,processID);
					JSONArray samples=dBconn.jsonArrayFromPreparedStmt(pStmt);
					
					
				// get all corresponding sample related parameters
					for (int i=0; i<samples.length(); i++ ){
						JSONObject sample = samples.getJSONObject(i);
				    	pStmt = dBconn.conn.prepareStatement(
				    			"SELECT "
				    			+ "asp.value, "
				    			+ "paramdef.datatype, "
				    			+ "pop.definition, "
				    			+ "pop.id AS parameterid "
				    			+"FROM po_parameters pop "
				    			+"LEFT JOIN acc_srp_parameters asp "
				    			+"		ON asp.opid = ? AND asp.po_parameter_id=pop.id "
				    			+"JOIN paramdef ON paramdef.id = pop.definition "
				    			+"ORDER BY pop.position");
				    	pStmt.setInt(1, sample.getInt("opid"));
						JSONArray poParameters = dBconn.jsonArrayFromPreparedStmt(pStmt);
						for (int j=0; j<poParameters.length(); j++ ){
							JSONObject p = poParameters.getJSONObject(j);
							if (p.has("datatype")){
								int dt = p.getInt("datatype");
								p.put("datatype", Unidatoolkit.Datatypes[dt]);
							}
							if (p.getString("datatype").equals("chooser")) {	// chooser 
				      			pStmt= dBconn.conn.prepareStatement(
				      					"SELECT string FROM possible_values "
				      					+"WHERE parameterid=? ORDER BY position");
				      			pStmt.setInt(1, p.getInt("definition"));
				      			JSONArray pvalues=dBconn.ArrayFromPreparedStmt(pStmt);
				      			p.put("possiblevalues", pvalues);
				      			pStmt.close();
		      				}
							if (p.getString("datatype").equals("checkbox") && p.has("value")) {	// checkbox 
								Boolean v = p.getString("value").equals("1");
				      			p.put("value", v);
				      			pStmt.close();
		      				}
						}
						sample.put("parameters", poParameters);
				    }
					
					jsProcess.put("samples",samples);

				} catch (SQLException e) {
					System.err.println("Problems with SQL query for parameters");
					e.printStackTrace();
				} catch (JSONException e){
					System.err.println("Problems creating JSON for parameters");
					e.printStackTrace();
				} catch (Exception e) {
					System.err.println("Strange Problems with the parameters");
					e.printStackTrace();
				}	  	
			    
		  	
		    
				// Find all corresponding files
		    	try{
				    pStmt=  dBconn.conn.prepareStatement( 	
					"SELECT files.id,filename "+
					"FROM files "+
					"WHERE files.process=?");
					pStmt.setInt(1,processID);
					JSONArray files= dBconn.jsonArrayFromPreparedStmt(pStmt);
					if (files.length()>0) {
						jsProcess.put("files",files); 
					}
			    } catch (SQLException e) {
		    		System.err.println("Showsample: Problems with SQL query for child samples");
				} catch (JSONException e2) {
					System.err.println("Showsample: JSON Problem while getting child samples");
				} catch (Exception e3) {
					System.err.println("Showsample: Strange Problem while getting child samples");
		    	}
	
		
				// get the strings
				try{
			        jsProcess.put("strings",dBconn.getStrings(stringkeys));
			        jsProcess.put("editable", editable);
				} catch (JSONException e) {
					System.err.println("Showsample: JSON Problem while getting Stringkeys");
				} catch (Exception e2) {
					System.err.println("Showsample: Strange Problem while getting Stringkeys");
				}
				if (jsProcess.length()>0){
					out.println(jsProcess.toString());
				}else{
					out.println("{error:nodata}");
				}
			}else{
				out.println("{\"error\":\"notfound\"}");
			}
  	  	} else {
  	  		response.setStatus(401);
  	  	}
	dBconn.closeDB();
  }
};