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

public class SampleTypeParams extends HttpServlet {
	private static final long serialVersionUID = 1L;
   
	
    public SampleTypeParams() {
        super();
    }

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
		throws ServletException, IOException {
		  
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
		int paramgroupid=0;
		request.setCharacterEncoding("utf-8");
	    response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    PrintWriter out = response.getWriter(); 
	  	  	try  {
	  	  		paramgroupid=Integer.parseInt(request.getParameter("paramgrpid")); 
	  	  	}
	  	  	catch (Exception e1) {
	  	  		System.err.print("SampleTypeParameters: no paramgroupID given!");
	  	  		e1.printStackTrace();
	  	  	}
		PreparedStatement pStmt = null; 	// Declare variables
	    JSONObject paramGrp= null;
	    JSONArray parameterGrps= null;		
	    JSONArray processTypeGrps= null;
	    JSONArray siblings=null;
	 	DBconnection dBconn=new DBconnection(); // New connection to the database
	 	ArrayList<String> stringkeys = new ArrayList<String>(); 
		 	
	    try{
		 	dBconn.startDB();
		 	
		 	// check if admin
		 	if (Unidatoolkit.userHasAdminRights(userID, dBconn)){
	 			pStmt = dBconn.conn.prepareStatement(	
				   "SELECT ot_parametergrps.id,ot_id AS sampletype,ot_parametergrps.stringkey AS name, "
	   		  	  +" objecttypes.string_key AS sampletypename "
				  +"FROM ot_parametergrps "
	   		  	  +"LEFT JOIN objecttypes ON (ot_parametergrps.ot_id=objecttypes.id) "					   
				  +"WHERE ot_parametergrps.id=?");
		 		pStmt.setInt(1, paramgroupid);
	 			paramGrp=dBconn.jsonObjectFromPreparedStmt(pStmt); // get ResultSet from the database using the query
	 			pStmt.close();
	 			if (paramGrp.length()>0){
		 		 	stringkeys.add(Integer.toString(paramGrp.getInt("name"))); 
		 		 	stringkeys.add(Integer.toString(paramGrp.getInt("sampletypename")));
		 		 	// get siblings
		 		 	pStmt = dBconn.conn.prepareStatement(	
		 				   "SELECT otp.id,stringkey AS name "
		 				  +"FROM objecttypes "
		 				  +"JOIN ot_parametergrps otp ON otp.ot_id=objecttypes.id "
		 				  +"WHERE objecttypes.id=? AND NOT otp.id=?");
		 		 	pStmt.setInt(1, paramGrp.getInt("sampletype"));
		 		 	pStmt.setInt(2, paramgroupid);
		 		 	siblings=dBconn.jsonArrayFromPreparedStmt(pStmt);
		 		 	if (siblings.length()>0){
		 		 		for (int i=0;i<siblings.length();i++){
		 		 			stringkeys.add(Integer.toString(siblings.getJSONObject(i).getInt("name")));
		 		 		}
		 		 	}
	
		 		 	
		 		// check if the parameter can be deleted. (No, if corresponding data exists).
		           	pStmt = dBconn.conn.prepareStatement(
		     		  	   "SELECT ot_parameters.id, compulsory, id_field, formula, hidden, pos, definition, "
		           		  +"  COALESCE(ot_parameters.stringkeyname,paramdef.stringkeyname) as name, "
		     		  	  +"  (blabla.count) IS NULL as deletable, stringkeyunit, paramdef.datatype "
		     		  	  +"FROM ot_parameters " 
		     		  	  +"JOIN paramdef ON (definition=paramdef.id) "
		     		  	  +"LEFT JOIN "
					  	  +"( "
						  +"  SELECT count(a.id),ot_parameter_id FROM sampledata a GROUP BY parameter_id "
						  +") AS blabla ON blabla.ot_parameter_id=ot_parameters.id "
						  +"WHERE parametergroup=?"); // status, processnumber and date cannot be edited
			 		pStmt.setInt(1, paramgroupid);
					processTypeGrps=dBconn.jsonArrayFromPreparedStmt(pStmt); // get ResultSet from the database using the query
					if (processTypeGrps.length()>0) {
		           		for (int j=0; j<processTypeGrps.length();j++) {
		           			JSONObject tObj=processTypeGrps.getJSONObject(j);
		           			int datatype=tObj.getInt("datatype");
		           			stringkeys.add(Integer.toString(processTypeGrps.getJSONObject(j).getInt("name")));
		           			if (datatype<4){
			           			if (tObj.has("stringkeyunit")){
			           				stringkeys.add(Integer.toString(tObj.getInt("stringkeyunit")));
			           			}
		           			} else {
		           				tObj.remove("stringkeyunit");
		           			}
		           			tObj.remove("datatype");
		           			tObj.put("datatype",Unidatoolkit.Datatypes[datatype]);
		           		}
		           	}		
	 			} else {
	 				System.err.println("not found");
	 			}
	   		
			
		        JSONObject answer=new JSONObject();
		        answer=paramGrp;
		        if (siblings.length()>0){
		        	answer.put("siblings", siblings);
		        }
		        answer.put("parameters", processTypeGrps);
		        answer.put("parametergrps",parameterGrps);
		        answer.put("strings", dBconn.getStrings(stringkeys));
		        out.println(answer.toString());
		 	}else{
		 		response.setStatus(401);
		 	}
	    } catch (SQLException eS) {
			System.err.println("SampleTypeParameters: SQL Error");
			eS.printStackTrace();
		} catch (Exception e) {
			System.err.println("SampleTypeParameters: Some Error, probably JSON");
			e.printStackTrace();
		} finally {
		try{
			if (pStmt != null) { 
				try {
	        	  	pStmt.close();
	        	} catch (SQLException e) {
					System.err.println("SampleTypeParameters: SQL Error ");
	        	} 
			}
	    	if (dBconn.conn != null) { 
	    		dBconn.closeDB();  // close the database 
	    	}
	        } catch (Exception e) {
				System.err.println("SampleTypeParameters: Some Error closing the database");
				e.printStackTrace();
		   	}
        }       
	}
}
