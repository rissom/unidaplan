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

	public class Experiment extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doGet(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
		userID=userID+1;
		PreparedStatement pstmt;
		ArrayList<String> stringkeys = new ArrayList<String>(); 
		JSONObject experiment = null;
		JSONArray processes = null;
		JSONArray samples = null;
	    response.setContentType("application/json");
	    request.setCharacterEncoding("utf-8");
	    response.setCharacterEncoding("utf-8");
//	    HttpSession session = request.getSession();
	    PrintWriter out = response.getWriter();
	 	DBconnection dBconn=new DBconnection();
	    dBconn.startDB();
	    JSONObject expPlans = new JSONObject();
	    int id=-1;
	    
	 // Print the current Session's ID
//	    out.println("Session ID:" + " " + session.getId());
//	    out.println("<br>");
//
//	    // Print the current Session's Creation Time
//	    out.println("Session Created:" + " " + new Date(session.getCreationTime()) + "<br>");
//
//	    // Print the current Session's Last Access Time
//	    out.println("Session Last Accessed" + " " + new Date(session.getLastAccessedTime()));
//	    
	  	try {
	   		 id=Integer.parseInt(request.getParameter("id")); 
	    } catch (Exception e1) {
	   		System.err.println("no experiment ID given!");
			response.setStatus(404);
	   	}
	    try {  
	    	// get basic experiment data (creator, id, name, status, number)
			pstmt= dBconn.conn.prepareStatement( 	
			"SELECT exp_plan.ID AS ID,users.fullname as creator, exp_plan.name ,status , intd.value AS number "
			+"FROM  exp_plan "
			+"JOIN users ON (users.id=exp_plan.Creator) "
			+"JOIN expp_integer_data intd ON (intd.expp_id=exp_plan.ID) "
			+"JOIN expp_param ON (intd.expp_param_id=expp_param.id AND expp_param.definition=2) "
			+"WHERE exp_plan.ID=?");
			pstmt.setInt(1, id);
			experiment=dBconn.jsonObjectFromPreparedStmt(pstmt);
			pstmt.close();
    	} catch (SQLException e) {
    		System.err.println("Experiments: Problems with SQL query for experiment");
    	} catch (JSONException e) {
			System.err.println("Experiments: JSON Problem while getting experiment");
    	} catch (Exception e2) {
			System.err.println("Experiments: Strange Problem while getting experiment");
    	} 
	    
	   
    
	    		
	    if (experiment.length()>0) {
		    try {
		    		// Get the default processes for this experiment
		 			pstmt= dBconn.conn.prepareStatement( 
		 			"SELECT exp_plan_processes.id, p_recipes.name AS recipename, position, ptid AS processtype, recipe, note "
					+"FROM exp_plan_processes "
					+"LEFT JOIN p_recipes ON (p_recipes.id=exp_plan_processes.recipe) " 
					+"WHERE expp_id=? ORDER BY exp_plan_processes.position");
		 			pstmt.setInt(1,id);
		 			processes=dBconn.jsonArrayFromPreparedStmt(pstmt);
		 			if (processes.length()>0) {
		 				// get keys for the names of the receipes:
		 				for (int i=0; i<processes.length();i++){
		 					JSONObject tempObj = processes.getJSONObject(i);
		 					if (!tempObj.isNull("recipename")){
		 						int receipename=tempObj.getInt("recipename");
		 						stringkeys.add(Integer.toString(receipename));
		 					}
		 					if (!tempObj.isNull("note")){
		 						int note=tempObj.getInt("note");
		 						stringkeys.add(Integer.toString(note));
		 					}
		 				}				
		 				experiment.put("processes", processes);
		 			};
		 			
		 			
		 			// Get the associated samples and their associated processes
		 			pstmt= dBconn.conn.prepareStatement( 
		 			"SELECT expp_samples.id,expp_samples.position AS sampleposition,expp_samples.sample AS sampleid, "
		 			+"samplenames.typeid, samplenames.name, note "
		 			+"FROM expp_samples "
		 			+"JOIN samplenames ON expp_samples.sample=samplenames.id "
		 			+"WHERE expp_samples.expp_id=? ORDER BY sampleposition");
		 			pstmt.setInt(1,id);
		 			samples=dBconn.jsonArrayFromPreparedStmt(pstmt);
		 			if (samples.length()>0) {
		 				for (int i=0; i<samples.length();i++){
		 					JSONObject tempSample = samples.getJSONObject(i);
		 					if (!tempSample.isNull("note")){
		 						int sampleNote=tempSample.getInt("note");
		 						stringkeys.add(Integer.toString(sampleNote));
		 					}
		 					
		 					// get planned Processes for a sample
	 						pstmt= dBconn.conn.prepareStatement("SELECT eps.id as process_step_id, "
								+"epp.position AS processposition, epp.ptid AS processtype, eps.recipe, eps.note, " 
								+"p_recipes.name AS recipename, epp.id AS eppprocess "
								+"FROM exp_plan_steps eps "
								+"JOIN exp_plan_processes epp ON (epp.id=eps.exp_plan_pr) "
								+"LEFT JOIN p_recipes ON (p_recipes.id=eps.recipe) " 
								+"WHERE eps.expp_s_id=? "
								+"ORDER BY processposition");
	 			 			pstmt.setInt(1,samples.getJSONObject(i).getInt("id"));
	 			 			JSONArray pprocesses=dBconn.jsonArrayFromPreparedStmt(pstmt);
	 			 			if (pprocesses.length()>0) {
	 			 				samples.getJSONObject(i).put("pprocesses",pprocesses);
	 			 				for (int j=0; j<pprocesses.length();j++){
	 			 					JSONObject tempPProcess = pprocesses.getJSONObject(j);
	 			 					if (!tempPProcess.isNull("note")){
				 						int processNote=tempPProcess.getInt("note");
				 						stringkeys.add(Integer.toString(processNote));
	 			 					}
	 			 				}
		 					}
	 			 			
		 					// get finished Processes for a sample
	 			 			pstmt= dBconn.conn.prepareStatement( 
	 			 				   "SELECT samplesinprocess.processid, processes.processtypesid as processtype, ptd.value AS date, n.value AS number, "
	 			 				  +"n2.value AS status "
	 			 				  +"FROM samplesinprocess "
	 			 				  +"JOIN processes ON (processes.id=samplesinprocess.processid) " 
	 			 				  +"JOIN processtypes ON (processes.processtypesid=processtypes.id) "  
	 			 				  +"JOIN p_parameters pp ON (pp.definition=10) " // date
	 			 				  +"JOIN p_parameters pp2 ON (pp2.definition=8) "  // number
	 			 				  +"JOIN p_parameters pp3 ON (pp3.definition=1) "  // status
	 			 				  +"JOIN p_timestamp_data ptd ON (ptd.processID=samplesinprocess.processid AND ptd.P_Parameter_ID=pp.id) "
	 			 				  +"JOIN p_integer_data n ON (n.ProcessID=samplesinprocess.processid AND n.P_Parameter_ID=pp2.id) "
	 			 				  +"JOIN p_integer_data n2 ON (n2.ProcessID=samplesinprocess.processid AND n2.P_Parameter_ID=pp3.id) "
	 			 				  +"WHERE sampleid=?");
	 			 			pstmt.setInt(1,samples.getJSONObject(i).getInt("sampleid"));
	 			 			JSONArray fprocesses=dBconn.jsonArrayFromPreparedStmt(pstmt);
	 			 			if (fprocesses.length()>0) {
	 			 				samples.getJSONObject(i).put("fprocesses",fprocesses);
	 			 			}
			 			}
		 				experiment.put("samples", samples);
		 			}

		 		} catch (SQLException e) {
		 			System.err.println("Experiments: Problems with SQL query for processes");
		 		} catch (JSONException e) {
		 			System.err.println("Experiments: JSON Problem while getting processes");
		 		} catch (Exception e2) {
		 			System.err.println("Experiments: Strange Problem while getting processes");
		 		} 
		    
		    try {  	 
		    	stringkeys.add(Integer.toString(experiment.getInt("name")));
		    	
	     		// Output the Parameters
				pstmt= dBconn.conn.prepareStatement( 	
	     		"SELECT expp_param.id, expp_param.pos, "
				+"expp_param.stringkeyname,  pid, value, "
				+"st.description, paramdef.datatype "
				+"FROM expp_param "
				+"JOIN paramdef ON (paramdef.id=expp_param.definition) " 
				+"LEFT JOIN acc_expp_parameters a ON  "
				+"(a.expp_id=expp_param.exp_plan_id AND a.id=expp_param.id ) " 
				+"JOIN String_key_table st ON st.id=expp_param.stringkeyname "
				+"WHERE expp_param.exp_plan_id=? AND hidden=false "
				+"ORDER BY pos ");
				pstmt.setInt(1, id);
				JSONArray parameters=dBconn.jsonArrayFromPreparedStmt(pstmt);
				pstmt.close();
				if (parameters.length()>0) {
					experiment.put("parameters", parameters);
			      	for (int i=0; i<parameters.length();i++) {
			      		JSONObject tempObj=(JSONObject) parameters.get(i);
			      		stringkeys.add(Integer.toString(tempObj.getInt("stringkeyname")));
			      	}
				}
	    	} catch (SQLException e) {
	    		System.err.println("Experiments: Problems with SQL query for Parameters");
	    	} catch (JSONException e) {
				System.err.println("Experiments: JSON Problem while getting Parameters");
	    	} catch (Exception e2) {
				System.err.println("Experiments: Strange Problem while getting Parameters");
	    	} try {
			
						  
				// get the strings
		        String query="SELECT id,string_key,language,value FROM Stringtable WHERE string_key=ANY('{";
		      	
		        StringBuilder buff = new StringBuilder(); // join numbers with commas
		        String sep = "";
		        for (String str : stringkeys) {
	         	    buff.append(sep);
	         	    buff.append(str);
	         	    sep = ",";
		        }
		        query+= buff.toString() + "}'::int[])";
		        JSONArray theStrings=dBconn.jsonfromquery(query);
		        expPlans.put("experiment", experiment);
		        expPlans.put("strings", theStrings);
				out.println(expPlans.toString());
	    	} catch (SQLException e) {
	    		System.err.println("Experiments: Problems with SQL query for Stringkeys");
	    	} catch (JSONException e) {
				System.err.println("Experiments: JSON Problem while getting Stringkeys");
	    	} catch (Exception e2) {
				System.err.println("Experiments: Strange Problem while getting Stringkeys");
	    	}}else{
	    		response.setStatus(404);
	    		out.println("{\"error\":\"not found\"}");
	    	}
		dBconn.closeDB();
	}}	