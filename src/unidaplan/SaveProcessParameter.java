package unidaplan;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import expr.Expr;
import expr.Parser;
import expr.SyntaxException;
import expr.Variable;

	public class SaveProcessParameter extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doPost(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {
		
		Authentificator authentificator = new Authentificator();
		int userID = authentificator.GetUserID(request,response);
	    request.setCharacterEncoding("utf-8");
	    Boolean needsRecalc = false;
	    String privilege = "n";
	    String status = "ok";
	    String in = request.getReader().readLine();
	    JSONObject jsonIn = null;	    
	    try {
			  jsonIn = new JSONObject(in);
		} catch (JSONException e) {
			System.err.println("SaveProcessParameter: Input is not valid JSON");
			status="error";
		}
		response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    PreparedStatement pStmt;

	    // get the id
	    int processID = 0;
	    int parameterID = -1;
	    
	    try {
			processID = jsonIn.getInt("processid");	
     		parameterID = jsonIn.getInt("parameterid");
		} catch (JSONException e) {
			System.err.println("SaveProcessParameter: Error parsing ID-Field");
			status="error parsing ID-Field";
			response.setStatus(404);
		}

	 	DBconnection dBconn=new DBconnection();
	 	
	 	try {	
		    dBconn.startDB();	   
		    
	        pStmt= dBconn.conn.prepareStatement( 	
					"SELECT getProcessRights(vuserid:=?,vprocess:=?)");
			pStmt.setInt(1,userID);
			pStmt.setInt(2,processID);
			privilege=dBconn.getSingleStringValue(pStmt);
			pStmt.close();
		} catch (SQLException e) {
			System.err.println("SaveSampleParameter: Problems with SQL query");
			status="Problems with SQL query";
			e.printStackTrace();
		} catch (JSONException e){
			System.err.println("SaveSampleParameter: Problems creating JSON");
			status="Problems creating JSON";
		} catch (Exception e) {
			System.err.println("SaveSampleParameter: Strange Problems");
			status="Strange Problems";
		}
	        
			
			
	    if (privilege.equals("w")){
 	    	    
		    // look up the datatype in Database	    
		    int type = -1;

			try {	
				
				pStmt= dBconn.conn.prepareStatement( 			
						   "DELETE FROM processdata "
						 + "WHERE processid = ? AND parameterid = ?");
			   	pStmt.setInt(1, processID);
			   	pStmt.setInt(2, parameterID);
				pStmt.executeUpdate();
			   	
				pStmt = dBconn.conn.prepareStatement( 			
						 "SELECT paramdef.datatype FROM p_parameters p "
						+"JOIN paramdef ON p.definition = paramdef.id "
						+"WHERE p.id = ?");
			   	pStmt.setInt(1, parameterID);
			   	JSONObject answer = dBconn.jsonObjectFromPreparedStmt(pStmt);
				type = answer.getInt("datatype");
			} catch (SQLException e) {
				System.err.println("SaveProcessParameter: Problems with SQL query");
				status="error";
			} catch (JSONException e){
				System.err.println("SaveProcessParameter: Problems creating JSON");
				status="error";
			} catch (Exception e) {
				System.err.println("SaveProcessParameter: Strange Problems");
				status="error";
			}
			
			pStmt = null; // fooling eclipse to not show warnings
			
			int id = 0; // id of the newly created value
			// differentiate according to type
			try {	
				JSONObject data = new JSONObject();
				if (jsonIn.has("data")){
					JSONObject inData = jsonIn.getJSONObject("data");
					switch (type) {
			        case 1:	if (inData.has("value") && !inData.isNull("value")){  // Integer values
			        			data.put("value", inData.getInt("value"));
			        			needsRecalc = true;
			        		}
					   		break;
					   		
			        case 2: if (inData.has("value") && !inData.isNull("value")){  // Floating point data
	        					data.put("value", inData.getDouble("value"));
	        					needsRecalc = true;
			        		}
			   				break;
		        			
			        case 3: if (inData.has("value") && !inData.isNull("value")){  	// Measurement data
    							data.put("value", inData.getDouble("value"));
    							needsRecalc = true;
			        			if (inData.has("error")){
		        					data.put("error", inData.getDouble("error"));
			        			}
			        		}
							break;
					        
			        case 4: if (inData.has("value") && !inData.isNull("value")){  // String data
			        			data.put("value", inData.getString("value"));
			        		}
			        		break;
					        
			        case 5: if (inData.has("value") && !inData.isNull("value")){    // String data again
	        					data.put("value", inData.getString("value"));
	        				}
							break;
						    
			        case 6: if (inData.has("value") && !inData.isNull("value")){   //  6: chooser, (saves as a string)
	        					data.put("value", inData.getString("value"));
	        				}
							break;
							
			        case 7: if (inData.has("date") && !inData.isNull("date")){   //   7: date,
		     		   		  	
//		     		   		  	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
//		     		   		  	SimpleDateFormat sqldf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//		     		   		  	java.sql.Timestamp ts = java.sql.Timestamp.valueOf(sqldf.format(sdf.parse(jsonIn.getString("date"))));	
			        			data.put("tz",  inData.getInt("tz"));
		     		   		  	data.put("date", inData.getString("date"));
			        		}
		     		   		break;
		     			    
			        case 8: if (inData.has("value") && !inData.isNull("value")){  //   8: checkbox,
			        			data.put("value", inData.getBoolean("value"));
							}
					   		break;
					        
			        case 9: if (inData.has("date") && !inData.isNull("date")){  //   9: timestamp,
//						   		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSX");
//								SimpleDateFormat sqldf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
//								java.sql.Timestamp ts = java.sql.Timestamp.valueOf(sqldf.format(sdf.parse(jsonIn.getString("date"))));		   
								data.put("tz",  inData.getInt("tz"));
		     		   		  	data.put("date", inData.getString("date"));
			        		}
						   	break;
					    
			        case 10: if (inData.has("value") && !inData.isNull("value")){ // URL
					        	data.put("value", inData.getString("value"));
			        		}
			        		break;
					        
			        case 11: if (inData.has("value") && !inData.isNull("value")){ // e-mail
			        			data.put("value", inData.getString("value"));
			        		} 
		        		break;
					} // end of switch Statement
					pStmt = dBconn.conn.prepareStatement(	
							  "INSERT INTO processdata (ProcessID, ParameterID, Data, lastUser) "
							+ "VALUES (?, ?, ?, ?)  RETURNING ID"); 
        			pStmt.setInt(1, processID);
        			pStmt.setInt(2, parameterID);
		   		  	pStmt.setObject(3, data, java.sql.Types.OTHER);
        			pStmt.setInt(4, userID);
        			id = dBconn.getSingleIntValue(pStmt);
		   	   		pStmt.close();
		   	   		
		   	   		pStmt = dBconn.conn.prepareStatement(	
		   	   				"REFRESH MATERIALIZED VIEW pnumbers");
		   	   		pStmt.executeUpdate();
		   	   		pStmt.close();
		   	   		
			   	   	if (needsRecalc){
						
						// query hierarchy
						JSONArray dependentParameters = null;
	
						pStmt = dBconn.conn.prepareStatement(
								  "WITH RECURSIVE dependencyrank(parameterid, rank, path, cycle) AS ( "
								+ "	SELECT ? , 1, ARRAY[?], false "
								+ "		UNION "
								+ "		SELECT "
								+ "			pp.id AS parameterid, "
								+ "			rank + 1 AS rank, "
								+ "			path || pp.id, "
								+ "			pp.id = ANY (path) "
								+ "		FROM dependencyrank dr, p_parameters pp "
								+ "		WHERE pp.formula LIKE '%p' || dr.parameterid || '%' AND NOT cycle "
								+ "	) "
								+ ""
								+ "SELECT parameterid, max(rank) AS rank "
								+ "FROM dependencyrank "
								+ "WHERE rank > 1 "
								+ "GROUP BY parameterid ORDER BY rank ");
						pStmt.setInt(1, parameterID);
						pStmt.setInt(2, parameterID);
						dependentParameters = dBconn.jsonArrayFromPreparedStmt(pStmt);
						pStmt.close();
						
						if (dependentParameters.length() > 0 ){
							// recalc values
							
							for (int i = 0; i < dependentParameters.length(); i++){
								int dParameterID = dependentParameters.getJSONObject(i).getInt("parameterid");
								pStmt = dBconn.conn.prepareStatement(
										  "SELECT formula "
										+ "FROM p_parameters "
										+ "WHERE id = ?");
								pStmt.setInt(1,dParameterID);
								String formula = dBconn.getSingleStringValue(pStmt);
								ArrayList <Variable> myVariables = new ArrayList <Variable>(); 
								Expr expr;
								try {
								    expr = Parser.parse(formula); 
								} catch (SyntaxException e) {
								    System.err.println(e.explain());
								    return;
								}
								
								// find all parameters for this formula
								for ( Matcher m = Pattern.compile("p\\d+").matcher(formula); m.find(); ){
									myVariables.add (Variable.make(m.toMatchResult().group()));
								}
								
								
								//	calculate value for this parameter
								for (Variable v : myVariables){
									pStmt = dBconn.conn.prepareStatement(
											  "SELECT data->>'value' AS value "
											+ "FROM processdata "
											+ "WHERE processid = ? AND parameterid = ?");
									int fParameterID = Integer.parseInt(v.toString().split("p")[1]);
									pStmt.setInt(1, processID);
									pStmt.setInt(2, fParameterID);
									Double newValue = Double.parseDouble( dBconn.getSingleStringValue(pStmt));
									v.setValue(newValue);
								};
								
								// delete previous value
								pStmt = dBconn.conn.prepareStatement( 			
										   "DELETE FROM "
										 + "  processdata "
										 + "WHERE processid = ? AND parameterid = ?");
							   	pStmt.setInt(1, processID);
							   	pStmt.setInt(2, dParameterID);
							   	pStmt.executeUpdate();
							   	pStmt.close();
								
							   	// save calculated value
							   	data = new JSONObject();
								data.put("value", expr.value());
							   	
							   	pStmt = dBconn.conn.prepareStatement( 			// Integer values
										  "INSERT INTO processdata ("
										+ "  processid,"
										+ "  parameterid,"
										+ "  data,"
										+ "  lastUser) "
										+ "VALUES (?,?,?,?)");
								pStmt.setInt(1, processID);
								pStmt.setInt(2, dParameterID);
					   		  	pStmt.setObject(3, data, java.sql.Types.OTHER);
					   		  	pStmt.setInt(4, userID);
								pStmt.executeUpdate();
								pStmt.close();
							}
						}
					} // end of 'if (needsRecalc){'
				}
			    // tell client that everything is fine
			    PrintWriter out = response.getWriter();
			    JSONObject myResponse = new JSONObject();
			    myResponse.put("status", status);
			    myResponse.put("id", id);
				out.println(myResponse.toString());
			} catch (SQLException e) {
				System.err.println("SaveProcessParameter: More Problems with SQL query");
				status = "error";
				e.printStackTrace();
			} catch (JSONException e){
				System.err.println("SaveProcessParameter: More Problems creating JSON");
				e.printStackTrace();
				System.err.println(pStmt.toString());
				status = "error";
			} catch (Exception e) {
				System.err.println("SaveProcessParameter: More Strange Problems");
				System.err.println(pStmt.toString());
				e.printStackTrace();
				status = "error";
			}
	    } else {
	    	response.setStatus(401);
	    }
		dBconn.closeDB();
	}
}	