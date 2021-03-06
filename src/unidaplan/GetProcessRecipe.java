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

public class GetProcessRecipe extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static JSONObject recipe;
       
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {

    	Authentificator authentificator = new Authentificator();
		int userID = authentificator.GetUserID(request,response);
		
		ArrayList<String> stringkeys = new ArrayList<String>(); // Array for translation strings
      
		response.setContentType("application/json");
		request.setCharacterEncoding("utf-8");
		response.setCharacterEncoding("utf-8");
		PrintWriter out = response.getWriter();
		DBconnection dBconn = new DBconnection();
		String privilege = "n";
		int recipeID = 1;
  	  	int processTypeID = 1;
  	  	JSONArray parametergrps = null;
  	  	
  	  	try  {
  	  		recipeID = Integer.parseInt(request.getParameter("recipeid")); 
  	  	}
  	  	catch (Exception e1) {
  	  		recipeID = 1;
  	  		System.err.print("Process: no recipe ID given!");
  	  		e1.printStackTrace();
  	  	}

  	  	PreparedStatement pStmt = null;

  	  	try {
  	  		
  	  		dBconn.startDB();
  	  		
  	  		// check privileges
	        pStmt = dBconn.conn.prepareStatement( 	
					"SELECT getProcessRecipeRights( vuserid := ?, vprocessrecipe := ?)");
			pStmt.setInt(1,userID);
			pStmt.setInt(2,recipeID);
			privilege = dBconn.getSingleStringValue(pStmt);
  	  		
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
  	  	
  	  	if (privilege.equals("r") || privilege.equals("w")){
 
  	  	  	try{
	  	  		// get name 
	  	  		pStmt = dBconn.conn.prepareStatement(
	  	  				  "SELECT "
	  	  				+ "  processrecipes.id,"
	  	  				+ "  name, "
	  	  				+ "  processrecipes.processtype, "
	  	  				+ "  users.id AS owner "
						+ "FROM processrecipes "
	  	  				+ "LEFT JOIN users ON users.id = processrecipes.owner "
						+ "WHERE processrecipes.id = ?");
	  	  		pStmt.setInt(1, recipeID);
	  	  		recipe = dBconn.jsonObjectFromPreparedStmt(pStmt);
	  	  		if ( recipe.length() > 0 ) {
	  	  			processTypeID = recipe.getInt("processtype");
	  	  			stringkeys.add(Integer.toString(recipe.getInt("name")));
	  	  		}else{
	  	  			System.err.println("no such recipe");
	  	  			response.setStatus(404);
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

  	  	  	
		    // get parametergroups
			try {
				pStmt = dBconn.conn.prepareStatement(
						  "SELECT "
						+ "  parametergroup, "
						+ "  max(stringkey) AS paramgrpkey, "
						+ "  min(p_parametergrps.pos) AS pos "
						+ "FROM p_parameters "
						+ "JOIN p_parametergrps ON parametergroup = p_parametergrps.id "
						+ "WHERE processtypeid = ? GROUP BY parametergroup");
				pStmt.setInt(1,processTypeID);
				parametergrps = dBconn.jsonArrayFromPreparedStmt(pStmt);
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
			
	    
		    // get the process recipe Parameters:
		    try{
		    	pStmt = dBconn.conn.prepareStatement(
			    	  "SELECT "
			    	+ "  p_parameters.id, "
			    	+ "  parametergroup, "
			    	+ "  compulsory, "
			    	+ "  p_parameters.pos, "
					+"   COALESCE (p_parameters.stringkeyname,paramdef.stringkeyname) AS stringkeyname,  "
					+ "  a.parameterid, "
					+ "  a.data, "
					+ "  p_parametergrps.id AS pgrpid, " 
					+ "  p_parametergrps.stringkey as parametergrp_key, "
					+ "  st.description, paramdef.datatype, "
					+ "  paramdef.stringkeyunit as unit, "
					+ "  paramdef.sampletype, "
					+ "  p_parameters.definition "
					+ "FROM p_parameters "
					+ "JOIN p_parametergrps ON (p_parameters.Parametergroup = p_parametergrps.ID) " 
					+ "JOIN paramdef ON (paramdef.id = p_parameters.definition) "
					+ "LEFT JOIN processrecipedata a ON "
					+ "(a.recipeid = ? AND a.parameterid = p_parameters.id AND hidden = FALSE) "
					+ "LEFT JOIN String_key_table st ON st.id = p_parameters.stringkeyname "
					+ "WHERE (p_parameters.processtypeID = ? "
					+ "		  AND p_parameters.id_field = FALSE AND p_parameters.hidden = FALSE) "
					+ "ORDER BY pos");
		    	pStmt.setInt( 1, recipeID );
		    	pStmt.setInt( 2, processTypeID );
				JSONArray parameters = dBconn.jsonArrayFromPreparedStmt(pStmt);
		
				if ( parameters.length() > 0 && parametergrps.length() > 0 ) { 		
					for (int j = 0; j < parametergrps.length(); j++){
						JSONArray prmgrpprms = new JSONArray();
						JSONObject prmgrp = parametergrps.getJSONObject(j);
			      		stringkeys.add(Integer.toString(prmgrp.getInt("paramgrpkey")));				
			      		
				      	for (int i = 0; i<parameters.length(); i++) {  
				      		JSONObject tParam = parameters.getJSONObject(i);
				      		stringkeys.add(Integer.toString(tParam.getInt("stringkeyname")));
				      		if ( tParam.has("parametergroup") &&
					      		tParam.getInt("parametergroup") == prmgrp.getInt("parametergroup") ){		
				      			
					      		if (tParam.has("unit")){
						      		stringkeys.add( Integer.toString(tParam.getInt("unit")) );
					      		}
				      			int datatype = tParam.getInt("datatype");
					      		tParam.remove("datatype");
					      		switch (datatype) {
					      		case 1: tParam.put("datatype","integer"); 
					      				if (tParam.has("value")){
					      					int x = Integer.parseInt(tParam.getString("value"));
					      					tParam.remove("value");
						      				tParam.put("value", x);
					      				}
					      				break;
					      		case 2: tParam.put("datatype","float"); 
					      				if (tParam.has("value")){
						      				double y = Double.parseDouble(tParam.getString("value"));
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
							      					  "SELECT string "
							      					+ "FROM possible_values "
							      					+ "WHERE parameterid = ? ORDER BY position");
							      		pStmt.setInt(1, tParam.getInt("definition"));
							      		JSONArray pvalues = dBconn.ArrayFromPreparedStmt(pStmt);
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
					      		case 11: tParam.put("datatype","email");
			      						break;
					      		case 12:	// sampletype 
				      				tParam.put("datatype", "sample"); 
					      			pStmt = dBconn.conn.prepareStatement(
					      					  "SELECT "
					      					+ "  id,"
					      					+ "  name "
					      					+ "FROM samplenames "
					      					+ "WHERE typeid = ? ORDER by name");
					      			pStmt.setInt(1, tParam.getInt("sampletype"));
					      			JSONArray possvalues = dBconn.jsonArrayFromPreparedStmt(pStmt);
					      			tParam.put("possiblesamples", possvalues);
					      			pStmt.close();
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
				recipe.put("parametergroups",parametergrps);
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
		      
			// get the strings
			try{
		        recipe.put( "strings", dBconn.getStrings(stringkeys) );
		        recipe.put( "editable", privilege.equals("w") );
			} catch (JSONException e) {
				System.err.println("Showsample: JSON Problem while getting Stringkeys");
			} catch (Exception e2) {
				System.err.println("Showsample: Strange Problem while getting Stringkeys");
			}
			if ( recipe.length() > 0 ){
				out.println(recipe.toString());
			}else{
				out.println("{error:nodata}");
			}
  	  	} else {
  	  		response.setStatus(401);
  	  	}
		
  	  	dBconn.closeDB();
    }
};