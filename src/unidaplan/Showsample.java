package unidaplan;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Showsample extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static PreparedStatement pStmt;
	private static JSONArray table;


@Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
	
	Authentificator authentificator = new Authentificator();
	int userID=authentificator.GetUserID(request,response);
	if (userID>0){
	 	ArrayList<String> stringkeys = new ArrayList<String>(); 
	 	Boolean deletable=false;
	 	Boolean editable=false;
	    response.setContentType("application/json");
	    request.setCharacterEncoding("utf-8");
	    response.setCharacterEncoding("utf-8");
	    PrintWriter out = response.getWriter();
	 	DBconnection dBconn=new DBconnection();
		int sampleID=1;      // variable initialisation
		int typeid=1;
		String privilege = "";
		JSONObject jsSample=new JSONObject(); // variable initialisation
		
		// get Parameter for id
		try{
			 sampleID=Integer.parseInt(request.getParameter("id")); 
		} catch (Exception e1) {
			sampleID=1;
			System.err.print("Showsample: no sample ID given!");
		}
		   
	    // check privileges
	    try{
	        dBconn.startDB();
	        pStmt= dBconn.conn.prepareStatement( 	
					"SELECT getSampleRights(vuserid:=?,vsample:=?)");
			pStmt.setInt(1,userID);
			pStmt.setInt(2,sampleID);
			privilege=dBconn.getSingleStringValue(pStmt);
			pStmt.close();
	        
	        editable= privilege!=null && privilege.equals("w");
	        
		} catch (SQLException e) {
			System.err.println("Showsample: Problems with SQL query for sample name");
		} catch (JSONException e) {
			System.err.println("Showsample: JSON Problem while getting sample name");
		} catch (Exception e2) {
			System.err.println("Showsample: Strange Problem while getting sample name");
			e2.printStackTrace();
		} 
	        
		if (editable || (privilege != null && privilege.equals("r"))){
			try {
		        pStmt= dBconn.conn.prepareStatement( 	
						"SELECT objecttypesid FROM samples WHERE id=?");
				pStmt.setInt(1,sampleID);
				typeid=dBconn.getSingleIntValue(pStmt);
				pStmt.close();
				pStmt= dBconn.conn.prepareStatement( 	
						"SELECT name FROM samplenames WHERE id=?");
				pStmt.setInt(1,sampleID);
				jsSample= dBconn.jsonObjectFromPreparedStmt(pStmt);
				jsSample.put("id", sampleID);
				jsSample.put("typeid", typeid);
				pStmt= dBconn.conn.prepareStatement( 	
				"SELECT string_key FROM objecttypes WHERE id=?");
				pStmt.setInt(1,typeid);
				int stringkey= dBconn.jsonObjectFromPreparedStmt(pStmt).getInt("string_key");
				stringkeys.add(Integer.toString(stringkey));
				jsSample.put("typestringkey", stringkey);
				
			} catch (SQLException e) {
				System.err.println("Showsample: Problems with SQL query for sample name");
			} catch (JSONException e) {
				System.err.println("Showsample: JSON Problem while getting sample name");
			} catch (Exception e2) {
				System.err.println("Showsample: Strange Problem while getting sample name");
				e2.printStackTrace();
			}
	
	    
	    // Error if the sample is not found
	    if (jsSample.length()==0) {
	    	try {
				jsSample.put("error", "sample not found");
			    System.err.println("sample not found");
			    response.sendError(HttpServletResponse.SC_NOT_FOUND);  // 404 Error
			} catch (JSONException e) {
			    System.err.println("Showsample: Strange JSON-Error");
			}
	    }
	    else {
	    	
		//get the title parameters
		try {
			pStmt= dBconn.conn.prepareStatement( 	 
			   "SELECT * FROM acc_sample_parameters WHERE id_field=true AND objectid=?");		
			pStmt.setInt(1,sampleID);
			JSONArray titleparameters=dBconn.jsonArrayFromPreparedStmt(pStmt);
			if (titleparameters.length()>0) {
				jsSample.put("titleparameters",titleparameters);
		      	for (int i=0; i<titleparameters.length();i++) {
		      		JSONObject tempObj=(JSONObject) titleparameters.get(i);
		      		stringkeys.add(Integer.toString(tempObj.getInt("name_key")));
		      	}
			}
		} catch (SQLException e) {
			System.err.println("Showsample: Problems with SQL query for sample parameters");
			e.printStackTrace();
		} catch (JSONException e){
			System.err.println("Showsample: Problems creating JSON for sample parameters");
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("Showsample: Strange Problems with the sample parameters");
			e.printStackTrace();
		}	
	    	
	    // get the parametergroups
		try {
			pStmt= dBconn.conn.prepareStatement(
					"SELECT parametergroup, max(stringkey) AS paramgrpkey, min(ot_parametergrps.pos) AS pos FROM ot_parameters "+
					"JOIN ot_parametergrps ON parametergroup=ot_parametergrps.id "+
					"WHERE objecttypesid=? GROUP BY parametergroup");
			pStmt.setInt(1,typeid);
			JSONArray parametergrps=dBconn.jsonArrayFromPreparedStmt(pStmt);
			pStmt.close();
			
			
			
			//get the parameters
			pStmt= dBconn.conn.prepareStatement( 
			   "SELECT "
			  +"op.id, "
			  +"op.parametergroup, "
	 		  +"op.compulsory, "
	 		  +"op.pos, "
	 		  +"COALESCE (op.stringkeyname,paramdef.stringkeyname) AS name_key,  "
	 		  +"pid, "
	 		  +"a.value, "
	 		  +"ot_parametergrps.id AS pgrpid, "
	 		  +"ot_parametergrps.stringkey as paramgrpkey, "
	 		  +"COALESCE (op.description, paramdef.description) AS description, "
	 		  +"paramdef.datatype, "
	 		  +"op.id_field, "
	 		  +"paramdef.format, "
	 		  +"paramdef.stringkeyunit AS unit, "
	 		  +"op.definition "
			  +"FROM ot_parameters op "
			  +"JOIN ot_parametergrps ON (op.Parametergroup=ot_parametergrps.ID) "
			  +"JOIN paramdef ON (paramdef.id=op.definition) "
			  +"LEFT JOIN acc_sample_parameters a ON "
			  +"(a.objectid=? AND a.id=op.id AND hidden=FALSE) "
			  +"WHERE (op.objecttypesID=? AND op.id_field=false AND op.hidden=false)");
			pStmt.setInt(1,sampleID);
			pStmt.setInt(2,typeid);
//			System.err.println("pstmt: "+pstmt.toString());
			JSONArray parameters=dBconn.jsonArrayFromPreparedStmt(pStmt);
			if (parameters.length()>0 && parametergrps!=null && parametergrps.length()>0) {
				for (int j=0;j<parametergrps.length();j++){
					JSONArray prmgrpprms=new JSONArray();
					JSONObject prmgrp=parametergrps.getJSONObject(j);
		      		stringkeys.add(Integer.toString(prmgrp.getInt("paramgrpkey")));				
			      	for (int i=0; i<parameters.length();i++) {
			      		JSONObject tParam=(JSONObject) parameters.get(i);
	
			      		stringkeys.add(Integer.toString(tParam.getInt("name_key")));
			      		if (tParam.has("parametergroup")&&
			      			tParam.getInt("parametergroup")==prmgrp.getInt("parametergroup")){		      			
				      		if (tParam.has("unit")){ 
				      			stringkeys.add(Integer.toString(tParam.getInt("unit")));
				      		}
				      		if (tParam.has("description")){ 
				      			stringkeys.add(Integer.toString(tParam.getInt("description")));
				      		}
			      			int datatype=tParam.getInt("datatype");
				      		tParam.remove("datatype");
					      	tParam.put("datatype",Unidatoolkit.Datatypes[datatype]); 
				      		if (datatype==1 && tParam.has("value")) {				      		
					      					int x=Integer.parseInt(tParam.getString("value"));
					      					tParam.remove("value");
						      				tParam.put("value", x);
					      				}
				      		if (datatype==2 && tParam.has("value")) {	 
						      				double y=Double.parseDouble(tParam.getString("value"));
						      				tParam.remove("value");
						      				tParam.put("value", y);
					      				}
				      		if (datatype==6) {	// chooser 
				      			pStmt= dBconn.conn.prepareStatement(
				      					"SELECT string FROM possible_values "
				      					+"WHERE parameterid=? ORDER BY position");
				      			pStmt.setInt(1, tParam.getInt("definition"));
				      			JSONArray pvalues=dBconn.ArrayFromPreparedStmt(pStmt);
				      			tParam.put("possiblevalues", pvalues);
				      			pStmt.close();
		      				}
				      		if (datatype>3 && tParam.has("unit")) {	 
			      				tParam.remove("unit");
		      				}
		      				tParam.remove("definition");
						    prmgrpprms.put(tParam);
			      		}
			      	}
		      		prmgrp.put("parameter",prmgrpprms);
				}
			}
			jsSample.put("parametergroups",parametergrps);
	
		} catch (SQLException e) {
			System.err.println("Showsample: Problems with SQL query for sample parameters");
			e.printStackTrace();
		} catch (JSONException e){
			System.err.println("Showsample: Problems creating JSON for sample parameters");
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("Showsample: Strange Problems with the sample parameters");
			e.printStackTrace();
		}	
		
		
	    	
		// find all corresponding processes + timestamp
		try {
			pStmt= dBconn.conn.prepareStatement( 
			   "SELECT samplesinprocess.processid, processes.processtypesid as processtype, ptd.value AS date, "
			  +"n.value AS number, n2.value AS status "
			  +"FROM samplesinprocess "
			  +"JOIN processes ON (processes.id=samplesinprocess.processid) " 
			  +"JOIN processtypes ON (processes.processtypesid=processtypes.id) "  
			  +"JOIN p_parameters pp ON (pp.definition=10) "   // date
			  +"JOIN p_parameters pp2 ON (pp2.definition=8) "  // number
			  +"JOIN p_parameters pp3 ON (pp3.definition=1) "  // status
			  +"JOIN p_timestamp_data ptd ON (ptd.processID=samplesinprocess.processid AND ptd.P_Parameter_ID=pp.id) "
			  +"JOIN p_integer_data n ON (n.ProcessID=samplesinprocess.processid AND n.P_Parameter_ID=pp2.id) "
			  +"JOIN p_integer_data n2 ON (n2.ProcessID=samplesinprocess.processid AND n2.P_Parameter_ID=pp3.id) " 
			  +"WHERE sampleid=?");
			pStmt.setInt(1,sampleID);
			JSONArray processes=dBconn.jsonArrayFromPreparedStmt(pStmt);
	//	   	String validToString = jsToken.optString("token_valid_to");
	//	   	Timestamp validToDate = Timestamp.valueOf(validToString); 
			if (processes.length()>0) { // TODO: Why not directly? Timestamp???
				JSONArray processes2 = new JSONArray();
		      	for (int i=0; i<processes.length();i++) {	      		
		      		JSONObject tempObj=(JSONObject) processes.get(i);
		      		JSONObject tempObj2=new JSONObject();
		      		int number= ((JSONObject) processes.get(i)).getInt("number");
		      		tempObj2.put("number",number);
		      		int ptype= ((JSONObject) processes.get(i)).getInt("processtype");
		      		tempObj2.put("processtype",ptype);
		      		int processid= ((JSONObject) processes.get(i)).getInt("processid");
		      		tempObj2.put("processid",processid);
		      		int status= ((JSONObject) processes.get(i)).getInt("status");
		      		tempObj2.put("status",status);
		    	   	String dateString = tempObj.optString("date");
		    	   	Timestamp date = Timestamp.valueOf(dateString); 	    	   	
		      		tempObj2.put("date", date.getTime());	      		
		      		processes2.put(tempObj2);
		      	}
				jsSample.put("processes",processes2);
			}	
		} catch (SQLException e) {
			System.err.println("Showsample: Problems with SQL query for processes");
			e.printStackTrace();
		} catch (JSONException e){
			System.err.println("Showsample: Problems creating JSON for processes");
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("Showsample: Strange Problems with the sample processes");
			e.printStackTrace();
		}
		
		
		
		// Find all experiment plans
		try {pStmt= dBconn.conn.prepareStatement( 
			"SELECT ep.id as exp_id, name, creator, status FROM exp_plan ep "
			+"JOIN expp_samples es ON es.expp_ID=ep.id "
			+"WHERE sample=?");
			pStmt.setInt(1,sampleID);
			JSONArray eps = dBconn.jsonArrayFromPreparedStmt(pStmt);
			pStmt.close();
			for (int i=0; i<eps.length();i++) {
	      		  stringkeys.add(Integer.toString(eps.getJSONObject(i).getInt("name")));
	      		// get planned processes
	      		pStmt= dBconn.conn.prepareStatement("SELECT expp_samples.expp_ID, eps.id AS process_step_id, "
					+"epp.position AS processposition, epp.ptid AS processtype, eps.recipe, eps.note, " 
					+"p_recipes.name as recipename " 
					+"FROM expp_samples "
					+"JOIN exp_plan_steps eps ON (eps.expp_s_ID=expp_samples.id) "
					+"LEFT JOIN p_recipes ON (p_recipes.id=eps.recipe) " 
					+"JOIN exp_plan_processes epp ON (epp.id=eps.exp_plan_pr) "
					+"WHERE expp_samples.sample=? AND expp_samples.expp_id=? "
					+"ORDER BY processposition");
	      		pStmt.setInt(1,sampleID);
	      		int experimentID=eps.getJSONObject(i).getInt("exp_id");
	      		pStmt.setInt(2,experimentID);
	      		JSONArray pprocesses = dBconn.jsonArrayFromPreparedStmt(pStmt);
	      		for (int j=0; j<pprocesses.length();j++){
	      			if (pprocesses.getJSONObject(j).has("recipename")){
	      				stringkeys.add(Integer.toString(pprocesses.getJSONObject(j).getInt("recipename")));
	      			}
	      			if (pprocesses.getJSONObject(j).has("note")){
	      				stringkeys.add(Integer.toString(pprocesses.getJSONObject(j).getInt("note")));
	      			}
	      		}
	      		eps.getJSONObject(i).put("plannedprocesses",pprocesses);
	      	  }
			jsSample.put("plans",eps);
				
	    	} catch (Exception e){
	    		e.printStackTrace();
	    	}
	    	
		
		
		// Find all corresponding files
    	try{
		    pStmt=  dBconn.conn.prepareStatement( 	
			"SELECT files.id,filename "+
			"FROM files "+
			"WHERE files.sample=?");
			pStmt.setInt(1,sampleID);
			JSONArray files= dBconn.jsonArrayFromPreparedStmt(pStmt);
			if (files.length()>0) {
				jsSample.put("files",files); 
			}
	    } catch (SQLException e) {
    		System.err.println("Showsample: Problems with SQL query for child samples");
		} catch (JSONException e2) {
			System.err.println("Showsample: JSON Problem while getting child samples");
		} catch (Exception e3) {
			System.err.println("Showsample: Strange Problem while getting child samples");
    	}
    	
		
				
			// Find all child objects
	    	try{
			    pStmt=  dBconn.conn.prepareStatement( 	
				"SELECT originates_from.id, samplenames.id AS sampleid, samplenames.name, samplenames.typeid \n"+
				"FROM originates_from \n"+
				"JOIN samplenames ON (samplenames.id=originates_from.child) \n"+
				"WHERE originates_from.parent=? \n");
				pStmt.setInt(1,sampleID);
				table= dBconn.jsonArrayFromPreparedStmt(pStmt);
				if (table.length()>0) {
					for(int i=0;i<table.length();i++) {
						pStmt= dBconn.conn.prepareStatement( 	
						"SELECT string_key FROM objecttypes WHERE id=?");
						pStmt.setInt(1,((JSONObject)table.get(i)).getInt("typeid"));
						int stringkey= dBconn.jsonObjectFromPreparedStmt(pStmt).getInt("string_key");
						((JSONObject)table.get(i)).put("typestringkey", stringkey);
						stringkeys.add(Integer.toString(stringkey));
					} 
					jsSample.put("children",table); 
				}
		    } catch (SQLException e) {
	    		System.err.println("Showsample: Problems with SQL query for child samples");
			} catch (JSONException e2) {
				System.err.println("Showsample: JSON Problem while getting child samples");
			} catch (Exception e3) {
				System.err.println("Showsample: Strange Problem while getting child samples");
	    	}
	    	
	    	
			// find all parent objects
			try{    
			    pStmt=  dBconn.conn.prepareStatement( 	
				"SELECT originates_from.id, samplenames.id AS sampleid, samplenames.name, samplenames.typeid \n" +
				"FROM originates_from \n" +
				"JOIN samplenames ON (samplenames.id=originates_from.parent) \n" +
				"WHERE originates_from.child=? \n");
				pStmt.setInt(1,sampleID);
				table= dBconn.jsonArrayFromPreparedStmt(pStmt);
				if (table.length()>0) {
					for(int i=0;i<table.length();i++) {
						pStmt= dBconn.conn.prepareStatement( 	
						"SELECT string_key FROM objecttypes WHERE id=?");
						pStmt.setInt(1,((JSONObject)table.get(i)).getInt("typeid"));
						int stringkey= dBconn.jsonObjectFromPreparedStmt(pStmt).getInt("string_key");
						stringkeys.add(Integer.toString(stringkey));
						((JSONObject)table.get(i)).put("typestringkey", stringkey);
					}
				jsSample.put("ancestors",table);	
				}
		    } catch (SQLException e) {
	    		System.err.println("Showsample: Problems with SQL query for parent samples");
			} catch (JSONException e2) {
				System.err.println("Showsample: JSON Problem while getting parent samples");
			} catch (Exception e3) {
				System.err.println("Showsample: Strange Problem while getting parent samples");
	    	}
			
			
			// find the previous sample
			try{
			    pStmt=  dBconn.conn.prepareStatement( 	
	    		"SELECT  samplenames.id, samplenames.name, samplenames.typeid "
				+"FROM samplenames "
				+"WHERE ((UPPER(samplenames.name) < UPPER((SELECT samplenames.name FROM samplenames WHERE samplenames.id=?))) "
				+"AND samplenames.typeid=(SELECT samplenames.typeid FROM samplenames WHERE samplenames.id=?)) "
				+"ORDER BY UPPER(samplenames.name) DESC "
				+"LIMIT 1");
				pStmt.setInt(1,sampleID);
				pStmt.setInt(2,sampleID);
				table= dBconn.jsonArrayFromPreparedStmt(pStmt);
				if (table.length()>0) {
					jsSample.put("previous",table.get(0)); }
		    } catch (SQLException e) {
	    		System.err.println("Showsample: Problems with SQL query for previous sample");
			} catch (JSONException e2) {
				System.err.println("Showsample: JSON Problem while getting previous sample");
			} catch (Exception e3) {
				System.err.println("Showsample: Strange Problem while getting previous sample");
	    	}
			
			
			// find next sample	
			try{
			    pStmt=  dBconn.conn.prepareStatement( 	
	    		"SELECT  samplenames.id, samplenames.name, samplenames.typeid "
				+"FROM samplenames "
				+"WHERE ((UPPER(samplenames.name) > UPPER((SELECT samplenames.name FROM samplenames WHERE samplenames.id=?))) "
				+"AND samplenames.typeid=(SELECT samplenames.typeid FROM samplenames WHERE samplenames.id=?)) "
				+"ORDER BY UPPER(samplenames.name) "	
	    		+"LIMIT 1 ");
				pStmt.setInt(1,sampleID);
				pStmt.setInt(2,sampleID); 
				table= dBconn.jsonArrayFromPreparedStmt(pStmt);
				if (table.length()>0) {
					jsSample.put("next",table.get(0)); }	
			} catch (SQLException e) {
	    		System.err.println("Showsample: Problems with SQL query for next sample");
	    	} catch (JSONException e) {
				System.err.println("Showsample: JSON Problem while getting next sample");
	    	} catch (Exception e2) {
				System.err.println("Showsample: Strange Problem while getting next sample");
	    	}
	
			
			// Can we delete this sample?
			try{
		        pStmt = dBconn.conn.prepareStatement(	
		    	"SELECT processid, sampleid FROM samplesinprocess "
		 		+"WHERE sampleid=?");
				pStmt.setInt(1,sampleID);
				ResultSet resultset=pStmt.executeQuery();
				if (resultset.next()) {
					deletable=false;
				}
				pStmt.close();
				
				// Check if experiments with this sample exist
		        pStmt = dBconn.conn.prepareStatement(	
		        	"SELECT id FROM expp_samples WHERE sample=?");
				pStmt.setInt(1,sampleID);
				resultset=pStmt.executeQuery();
				if (resultset.next()) {deletable=false;}
				pStmt.close();
				jsSample.put("deletable", deletable);
				jsSample.put("editable", editable);

				
			} catch (SQLException e) {
				System.err.println("Showsample: Problems with SQL query for deletable");
			} catch (JSONException e) {
				System.err.println("Showsample: JSON Problem while getting deletable");
			} catch (Exception e2) {
				System.err.println("Showsample: Strange Problem while getting deletable");
			}
				
			try{
		        jsSample.put("strings", dBconn.getStrings(stringkeys));
	    	} catch (JSONException e) {
				System.err.println("Showsample: JSON Problem while getting Stringkeys");
	    	} catch (Exception e2) {
				System.err.println("Showsample: Strange Problem while getting Stringkeys");
	    	}
	    }
		out.println(jsSample.toString());
		} else {
			String status="insufficient rights";
			response.setStatus(401);
			Unidatoolkit.sendStandardAnswer(status, response);
		}
		dBconn.closeDB();}
  	} 
}