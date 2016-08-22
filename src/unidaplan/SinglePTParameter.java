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
import org.json.JSONObject;

public class SinglePTParameter extends HttpServlet {
	private static final long serialVersionUID = 1L;
   
	
    public SinglePTParameter() {
        super();
    }

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
		throws ServletException, IOException {
		  
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
		request.setCharacterEncoding("utf-8");
	    response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    int parameterID = 0;
	    PrintWriter out = response.getWriter(); 
	  	  	try  {
	  	  		parameterID=Integer.parseInt(request.getParameter("parameterid")); 
	  	  	}
	  	  	catch (Exception e1) {
	  	  		System.err.print("SinglePTParameter: no paramgroupID given!");
	  	  		e1.printStackTrace();
	  	  	}
		PreparedStatement pStmt = null; 	// Declare variables
	    JSONObject parameter= null;
	 	DBconnection dBconn=new DBconnection(); // New connection to the database
	 	ArrayList<String> stringkeys = new ArrayList<String>(); 
		 	
	    try{
		 	dBconn.startDB();
		 	if (Unidatoolkit.userHasAdminRights(userID, dBconn)){
	 			pStmt = dBconn.conn.prepareStatement(
	 					    "SELECT \n"
	 					  + "  p_parameters.id, \n"
	 					  + "  compulsory, \n"
	 					  + "  id_field, \n"
	 					  + "  formula, \n"
	 					  + "  hidden, \n"
	 					  + "  definition, \n"
	 					  + "  processtypeid AS processtype, \n"
	 					  + "  paramdef.datatype, \n"
	 					  + "  parametergroup, \n"
	 					  + "  pgs.stringkey AS parametergroupname, \n"
	 					  + "  paramdef.format, \n"
	 					  + "  processtypes.name AS processtypename, \n" 
		           		  + "  COALESCE(p_parameters.stringkeyname,paramdef.stringkeyname) as name, \n"
		     		  	  + "  (blabla.count) IS NULL as deletable, stringkeyunit, paramdef.datatype, \n" 
		     		  	  + "  COALESCE(p_parameters.description,paramdef.description) as description, \n"
		     		  	  + "  (blabla.count) IS NULL as deletable, stringkeyunit, paramdef.datatype \n" 
		     		  	  + "FROM p_parameters \n" 
		     		  	  + "JOIN paramdef ON (definition = paramdef.id) \n"
		     		  	  + "LEFT JOIN p_parametergrps pgs ON (pgs.id = p_parameters.parametergroup)  \n"
		     		  	  + "LEFT JOIN  processtypes ON (processtypes.id = p_parameters.processtypeid) \n"
		     		  	  + "LEFT JOIN \n"
					  	  + "( \n"
						  + "  SELECT \n"
						  + "	  count(a.id), \n"
						  + "     parameterid \n"
						  + "  FROM processdata a \n"
						  + "  GROUP BY parameterid \n"
						  + ") AS blabla ON blabla.parameterid = p_parameters.id \n"
						  + "WHERE p_parameters.id = ?\n");
				pStmt.setInt(1, parameterID);
				parameter=dBconn.jsonObjectFromPreparedStmt(pStmt);
				pStmt.close();
				int datatype = parameter.getInt("datatype");
				parameter.put("datatype", Unidatoolkit.Datatypes[datatype]);
				stringkeys.add(Integer.toString(parameter.getInt("name")));
				stringkeys.add(Integer.toString(parameter.getInt("description")));
				if (parameter.has("parametergroupname")){
					stringkeys.add(Integer.toString(parameter.getInt("parametergroupname")));
				}
				stringkeys.add(Integer.toString(parameter.getInt("processtypename")));
				if (parameter.has("stringkeyunit")){
					stringkeys.add(Integer.toString(parameter.getInt("stringkeyunit")));
				}
				
		        // get all parameters of this sampletype for formula editing
		        pStmt = dBconn.conn.prepareStatement(
		        		"SELECT " 
		        	  + "  pp.id, "
		        	  + "  COALESCE (pp.stringkeyname, paramdef.stringkeyname) AS stringkeyname, "
		        	  + "  pp.pos, "
		        	  + "paramdef.stringkeyunit "
		        	  + "FROM p_parameters pp "
		        	  + "JOIN paramdef ON paramdef.id = pp.definition "
		        	  + "WHERE processtypeid = ? AND pp.id != ? AND paramdef.datatype IN (1,2,3)");
		        pStmt.setInt(1,parameter.getInt("processtype"));
		        pStmt.setInt(2,parameterID);
		        JSONArray otherparameters = dBconn.jsonArrayFromPreparedStmt(pStmt);
		        
		        // extract all stringkeys
		        for (int i = 0; i < otherparameters.length(); i++){
					stringkeys.add(Integer.toString(otherparameters.getJSONObject(i).getInt("stringkeyname")));
		        }
		        
		        // add otherparameter of same objecttype
		        parameter.put("otherparameters", otherparameters);
				
		        parameter.put("strings", dBconn.getStrings(stringkeys));
		        out.println(parameter.toString());
		        dBconn.closeDB();
		 	} else{
		 		response.setStatus(401);
		 		out.println("{status:\"not allowed\"}");
		 	}
		 		
	    } catch (SQLException eS) {
			System.err.println("SinglePTParameter: SQL Error");
			eS.printStackTrace();
		} catch (Exception e) {
			System.err.println("SinglePTParameter: Some Error, probably JSON");
			e.printStackTrace();
		}             
	}
}
