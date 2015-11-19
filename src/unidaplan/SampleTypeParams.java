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
		userID=userID+1;
		userID=userID-1;
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
	 	DBconnection dBconn=new DBconnection(); // New connection to the database
	 	ArrayList<String> stringkeys = new ArrayList<String>(); 
		 	
	    try{
		 	dBconn.startDB();
 			pStmt = dBconn.conn.prepareStatement(	
			   "SELECT id,ot_id AS sampletype,stringkey AS name "
			  +"FROM ot_parametergrps "
			  +"WHERE ot_parametergrps.id=?");
	 		pStmt.setInt(1, paramgroupid);
 			paramGrp=dBconn.jsonObjectFromPreparedStmt(pStmt); // get ResultSet from the database using the query
 			pStmt.close();
 			if (paramGrp.length()>0){
	 		 	stringkeys.add(Integer.toString(paramGrp.getInt("name"))); 
	 		// check if the parameter can be deleted. (No, if corresponding data exists).
	           	pStmt = dBconn.conn.prepareStatement(
	     		  	   "SELECT ot_parameters.id, compulsory, formula, hidden, pos, definition, ot_parameters.stringkeyname as name, "
	     		  	  + "(blabla.count) IS NULL as deletable, stringkeyunit " 
	     		  	  +"FROM ot_parameters " 
	     		  	  +"JOIN paramdef ON (definition=paramdef.id)"
	     		  	  +"LEFT JOIN "
				  	  +"( "
					  +"  SELECT count(a.id),ot_parameter_id FROM o_integer_data a GROUP BY ot_parameter_id "
					  +"  UNION ALL "
					  +"  SELECT count(b.id),ot_parameter_id FROM o_float_data b GROUP BY ot_parameter_id	"
					  +"  UNION ALL "
				      +"  SELECT count(c.id),ot_parameter_id FROM o_string_data c GROUP BY ot_parameter_id "
					  +"  UNION ALL "
				      +"  SELECT count(d.id),ot_parameter_id FROM o_measurement_data d GROUP BY ot_parameter_id "
					  +"  UNION ALL "
					  +"  SELECT count(e.id),ot_parameter_id FROM o_timestamp_data e GROUP BY ot_parameter_id "
					  +") AS blabla ON blabla.ot_parameter_id=ot_parameters.id "
					  +"WHERE parametergroup=?"); // status, processnumber and date cannot be edited
		 		pStmt.setInt(1, paramgroupid);
				processTypeGrps=dBconn.jsonArrayFromPreparedStmt(pStmt); // get ResultSet from the database using the query
				if (processTypeGrps.length()>0) {
	           		for (int j=0; j<processTypeGrps.length();j++) {
	           			stringkeys.add(Integer.toString(processTypeGrps.getJSONObject(j).getInt("name")));
	           			if (processTypeGrps.getJSONObject(j).has("stringkeyunit")){
	           				stringkeys.add(Integer.toString(processTypeGrps.getJSONObject(j).getInt("stringkeyunit")));
	           			}
	           		}
	           	}		
 			} else {
 				System.err.println("not found");
 			}
   		
		
	        JSONObject answer=new JSONObject();
	        answer=paramGrp;
	        answer.put("parameters", processTypeGrps);
	        answer.put("parametergrps",parameterGrps);
	        answer.put("strings", dBconn.getStrings(stringkeys));
	        out.println(answer.toString());
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
