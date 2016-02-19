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
import org.json.JSONObject;

public class SingleSTParameter extends HttpServlet {
	private static final long serialVersionUID = 1L;
   
	
    public SingleSTParameter() {
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
	    int parameterID=0;
	    PrintWriter out = response.getWriter(); 
	  	  	try  {
	  	  		parameterID=Integer.parseInt(request.getParameter("parameterid")); 
	  	  	}
	  	  	catch (Exception e1) {
	  	  		System.err.print("SingleSTParameter: no paramgroupID given!");
	  	  		e1.printStackTrace();
	  	  	}
		PreparedStatement pStmt = null; 	// Declare variables
	    JSONObject parameter= null;
	 	DBconnection dBconn=new DBconnection(); // New connection to the database
	 	ArrayList<String> stringkeys = new ArrayList<String>(); 
		 	
	    try{
		 	dBconn.startDB();
		 	if (Unidatoolkit.isMemberOfGroup(userID, 1, dBconn)){
	 			pStmt = dBconn.conn.prepareStatement(
	 					 "SELECT ot_parameters.id, compulsory, id_field, formula, hidden, definition,"
	 					  +"  objecttypesid AS sampletype, "
	 					  +"  paramdef.datatype, "
	 					  +"  parametergroup, "
	 					  +"  pgs.stringkey AS parametergroupname, "
	 					  +"  paramdef.format, "
	 					  +"  objecttypes.string_key AS sampletypename, " 
		           		  +"  COALESCE(ot_parameters.stringkeyname,paramdef.stringkeyname) as name, "
		     		  	  +"  (blabla.count) IS NULL as deletable, stringkeyunit, paramdef.datatype, " 
		     		  	  +"  COALESCE(ot_parameters.description,paramdef.description) as description, "
		     		  	  +"  (blabla.count) IS NULL as deletable, stringkeyunit, paramdef.datatype " 
		     		  	  +"FROM ot_parameters " 
		     		  	  +"JOIN paramdef ON (definition=paramdef.id) "
		     		  	  +"LEFT JOIN ot_parametergrps pgs ON (pgs.id=ot_parameters.parametergroup)  "
		     		  	  +"LEFT JOIN  objecttypes ON (objecttypes.id=ot_parameters.objecttypesid)  "
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
						  +"WHERE ot_parameters.id=?");
				pStmt.setInt(1, parameterID);
				parameter=dBconn.jsonObjectFromPreparedStmt(pStmt);
				pStmt.close();
				int datatype=parameter.getInt("datatype");
				parameter.put("datatype", Unidatoolkit.Datatypes[datatype]);
				stringkeys.add(Integer.toString(parameter.getInt("name")));
				stringkeys.add(Integer.toString(parameter.getInt("description")));
				if (parameter.has("parametergroupname")){
					stringkeys.add(Integer.toString(parameter.getInt("parametergroupname")));
				}
				stringkeys.add(Integer.toString(parameter.getInt("sampletypename")));
				if (parameter.has("stringkeyunit")){
					stringkeys.add(Integer.toString(parameter.getInt("stringkeyunit")));
				}
		        parameter.put("strings", dBconn.getStrings(stringkeys));
		        out.println(parameter.toString());
		        dBconn.closeDB();
		 	} else{
		 		response.setStatus(401);
		 		out.println("{status:\"not allowed\"}");
		 	}
		 		
	    } catch (SQLException eS) {
			System.err.println("SingleSTParameter: SQL Error");
			eS.printStackTrace();
		} catch (Exception e) {
			System.err.println("SingleSTParameter: Some Error, probably JSON");
			e.printStackTrace();
		}             
	}
}
