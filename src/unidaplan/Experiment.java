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

	
	 public Experiment() {
	        super();
	 };
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		Authentificator authentificator = new Authentificator();
		int userID = authentificator.GetUserID(request, response);
		PreparedStatement pStmt;
		String privilege = "n";
		ArrayList<String> stringkeys = new ArrayList<String>();
		JSONObject experiment = null;
		JSONArray processes = null;
		JSONArray samples = null;
		response.setContentType("application/json");
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		DBconnection dBconn = new DBconnection();
		JSONObject answer = new JSONObject();
		Boolean editable = false;
		int id = -1;

		try {
			id = Integer.parseInt(request.getParameter("id"));
		
			if (id > 0){
				dBconn.startDB();
	
				// check privileges
				pStmt = dBconn.conn.prepareStatement(
						"SELECT getExperimentRights(vuserid := ?, vexperimentid := ?)");
				pStmt.setInt(1, userID);
				pStmt.setInt(2, id);
				privilege = dBconn.getSingleStringValue(pStmt);
	
				editable = privilege != null && privilege.equals("w");
			
				if ( privilege.equals("w") || privilege.equals("r") ){
				
					// get basic experiment data (creator, id, name, status, number)
					pStmt = dBconn.conn.prepareStatement("SELECT "
							+ "  experiments.id," + "  users.fullname as creator, "
							+ "  experiments.name," + "  status, "
							+ "  experiments.number " + "FROM experiments "
							+ "JOIN users ON (users.id = experiments.Creator) "
							+ "WHERE experiments.id = ?");
					pStmt.setInt(1, id);
					experiment = dBconn.jsonObjectFromPreparedStmt(pStmt);
				
			
					if (experiment.length() > 0) {
						// Get the default processes for this experiment
						pStmt = dBconn.conn.prepareStatement(
										  "SELECT "
										+ "  exp_plan_processes.id, "
										+ "  processrecipes.name AS recipename, "
										+ "  exp_plan_processes.position AS position, "
										+ "  ptid AS processtype, "
										+ "  recipe, "
										+ "  note "
										+ "FROM exp_plan_processes "
										+ "LEFT JOIN processrecipes ON (processrecipes.id = exp_plan_processes.recipe) "
										+ "WHERE expp_id = ? ORDER BY exp_plan_processes.position");
						pStmt.setInt(1, id);
						processes = dBconn.jsonArrayFromPreparedStmt(pStmt);
						if (processes.length() > 0) {
							// get keys for the names of the receipes:
							for (int i = 0; i < processes.length(); i++) {
								JSONObject tempObj = processes.getJSONObject(i);
								if (!tempObj.isNull("recipename")) {
									int receipename = tempObj.getInt("recipename");
									stringkeys.add(Integer.toString(receipename));
								}
								if (!tempObj.isNull("note")) {
									int note = tempObj.getInt("note");
									stringkeys.add(Integer.toString(note));
								}
							}
							experiment.put("processes", processes);
						}
						
		
						// Get the associated samples and their associated processes
						pStmt = dBconn.conn.prepareStatement(
										  "SELECT "
										+ "  expp_samples.id,"
										+ "  expp_samples.position AS sampleposition,"
										+ "  expp_samples.sample AS sampleid, "
										+ "  samplenames.typeid, "
										+ "  samplenames.name, "
										+ "  note "
										+ "FROM expp_samples "
										+ "JOIN samplenames ON expp_samples.sample = samplenames.id "
										+ "WHERE expp_samples.expp_id = ? ORDER BY sampleposition");
						pStmt.setInt(1, id);
						samples = dBconn.jsonArrayFromPreparedStmt(pStmt);
						if (samples.length() > 0) {
							for (int i = 0; i < samples.length(); i++) {
								JSONObject tempSample = samples.getJSONObject(i);
								if (!tempSample.isNull("note")) {
									int sampleNote = tempSample.getInt("note");
									stringkeys.add(Integer.toString(sampleNote));
								}
		
								// get planned Processes for a sample
								pStmt = dBconn.conn.prepareStatement(
											  "SELECT "
											+ "  eps.id as process_step_id, "
											+ "  epp.position AS processposition, "
											+ "  epp.ptid AS processtype, "
											+ "  eps.recipe, "
											+ "  eps.note, "
											+ "  processrecipes.name AS recipename, "
											+ "  epp.id AS eppprocess "
											+ "FROM exp_plan_steps eps "
											+ "JOIN exp_plan_processes epp ON (epp.id = eps.exp_plan_pr) "
											+ "LEFT JOIN processrecipes ON (processrecipes.id = eps.recipe) "
											+ "WHERE eps.expp_s_id = ? "
											+ "ORDER BY processposition");
								pStmt.setInt(1, samples.getJSONObject(i).getInt("id"));
								JSONArray pprocesses = dBconn.jsonArrayFromPreparedStmt(pStmt);
								if (pprocesses.length() > 0) {
									samples.getJSONObject(i).put("pprocesses",
											pprocesses);
									for (int j = 0; j < pprocesses.length(); j++) {
										JSONObject tempPProcess = pprocesses
												.getJSONObject(j);
										if (!tempPProcess.isNull("note")) {
											int processNote = tempPProcess
													.getInt("note");
											stringkeys.add(Integer
													.toString(processNote));
										}
									}
								}
		
								// get finished Processes for a sample
								pStmt = dBconn.conn.prepareStatement(
											  "SELECT "
											+ "  samplesinprocess.processid, "
											+ "  processes.processtypesid AS processtype, "
											+ "  ptd.data->>'date' AS date, "
											+ "  (n.data->>'value')::integer AS number, "
											+ "  (n2.data->>'value')::integer AS status "
											+ "FROM samplesinprocess "
											+ "JOIN processes ON (processes.id = samplesinprocess.processid) "
											+ "JOIN processtypes ON (processes.processtypesid = processtypes.id) "
											+ "JOIN p_parameters pp ON (pp.definition = 8) " // date
											+ "JOIN p_parameters pp2 ON (pp2.definition = 7) " // number
											+ "JOIN p_parameters pp3 ON (pp3.definition = 1) " // status
											+ "JOIN processdata ptd ON (ptd.processID = samplesinprocess.processid AND ptd.parameterID = pp.id) "
											+ "JOIN processdata n ON (n.ProcessID = samplesinprocess.processid AND n.parameterID = pp2.id) "
											+ "JOIN processdata n2 ON (n2.ProcessID = samplesinprocess.processid AND n2.parameterID = pp3.id) "
											+ "WHERE sampleid = ?");
								pStmt.setInt(1,samples.getJSONObject(i).getInt("sampleid"));
								JSONArray fprocesses = dBconn.jsonArrayFromPreparedStmt(pStmt);
								if (fprocesses.length() > 0) {
									samples.getJSONObject(i).put("fprocesses", fprocesses);
								}
							}
							experiment.put("samples", samples);
						}
			
					
					
						// Find all corresponding files
					    pStmt = dBconn.conn.prepareStatement( 	
									  "SELECT "
									+ "  files.id,"
									+ "  filename "
									+ "FROM files "
									+ "WHERE files.experiment = ?");
						pStmt.setInt(1,id);
						JSONArray files = dBconn.jsonArrayFromPreparedStmt(pStmt);
						if (files.length() > 0) {
							experiment.put("files",files); 
						}
				  
					
			
				
						stringkeys.add(Integer.toString(experiment.getInt("name")));
		
						// Query the parameters
						pStmt = dBconn.conn.prepareStatement(
										  "SELECT "
										+ "  expp_param.id, "
										+ "  expp_param.definition, "
										+ "  expp_param.pos, "
										+ "	 paramdef.format, "
										+ "  COALESCE (expp_param.stringkeyname, paramdef.stringkeyname) AS stringkeyname, "
										+ "  ed.data, "
										+ "  paramdef.datatype, "
										+ "  paramdef.sampletype, "
										+ "  paramdef.stringkeyunit AS unit "
										+ "FROM expp_param "
										+ "JOIN paramdef ON (paramdef.id=expp_param.definition) "
										+ "LEFT JOIN experimentdata ed ON  "
										+ "(ed.experimentid = expp_param.exp_plan_id AND ed.parameterid = expp_param.id ) "
										+ "WHERE expp_param.exp_plan_id = ? AND hidden = false "
										+ "ORDER BY pos ");
						pStmt.setInt(1, id);
						JSONArray parameters = dBconn.jsonArrayFromPreparedStmt(pStmt);
						if (parameters.length() > 0) {
							for (int i = 0; i < parameters.length(); i++) {
								JSONObject tParam = parameters.getJSONObject(i);
								stringkeys.add(Integer.toString(tParam
										.getInt("stringkeyname")));
								if (tParam.has("unit")) {
									stringkeys.add(Integer.toString(tParam
											.getInt("unit")));
								}
								int datatype = tParam.getInt("datatype");
								if ( datatype == 6 ) {	// chooser 
					      			pStmt = dBconn.conn.prepareStatement(
					      					  "SELECT string FROM possible_values "
					      					+ "WHERE parameterid = ? ORDER BY position");
					      			pStmt.setInt(1, tParam.getInt("definition"));
					      			JSONArray pvalues = dBconn.ArrayFromPreparedStmt(pStmt);
					      			tParam.put("possiblevalues", pvalues);
			      				}
								if ( datatype > 3 && tParam.has("unit") ) {	 
				      				tParam.remove("unit");
			      				}
								if (datatype == 12) {	// sampletype 
					      			pStmt = dBconn.conn.prepareStatement(
					      					  "SELECT "
					      					+ "  id,"
					      					+ "  name "
					      					+ "FROM samplenames "
					      					+ "WHERE typeid = ? ORDER by name");
					      			pStmt.setInt(1, tParam.getInt("sampletype"));
					      			JSONArray pvalues = dBconn.jsonArrayFromPreparedStmt(pStmt);
					      			tParam.put("possiblesamples", pvalues);
					      			pStmt.close();
					      	 			
			      				} else {
					      			tParam.remove("sampletype");
					      		}
								parameters.getJSONObject(i).remove("datatype");
								parameters.getJSONObject(i).put("datatype", Unidatoolkit.Datatypes[datatype]);
							}
							experiment.put("parameters", parameters);
						}
				


			
						// Output data
						
						experiment.put("editable", editable);
						answer.put("experiment", experiment);
						answer.put("strings", dBconn.getStrings(stringkeys));
						out.println(answer.toString());
						
						// close database connection
						dBconn.closeDB();
						dBconn = null;
						answer = null;
					} else {
						Unidatoolkit.notFound(response);
					}
				}else{
					response.setStatus(401);
				}
			}
		} catch (SQLException e) {
			System.err.println("Showsample: Problems with SQL query for child samples");
		} catch (JSONException e2) {
			System.err.println("Showsample: JSON Problem while getting child samples");
		} catch (Exception e3) {
			System.err.println("Showsample: Strange Problem while getting child samples");
		} finally{
			if (dBconn != null){ dBconn.closeDB();}
		}
	}
}