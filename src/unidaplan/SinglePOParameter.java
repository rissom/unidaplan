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

public class SinglePOParameter extends HttpServlet {
	private static final long serialVersionUID = 1L;
   
	
    public SinglePOParameter() {
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
		 	if (Unidatoolkit.userHasAdminRights(userID, dBconn)){
	 			pStmt = dBconn.conn.prepareStatement(
	 					 "SELECT "
	 					+"	po_parameters.id, " 
	 					+"	compulsory, "
	 					+"	hidden, " 
	 					+"	definition, "
	 					+"	COALESCE(po_parameters.description,paramdef.description) AS description, "   
	 					+"	(blabla.count) IS NULL as deletable, "
	 					+"	processtypeid AS processtype, "   
	 					+"	paramdef.datatype, "
	 					+"	paramdef.format, "
	 					+"	processtypes.name AS processtypename, "   
	 					+"	COALESCE(po_parameters.stringkeyname,paramdef.stringkeyname) as name, "   
	 					+"	(blabla.count) IS NULL as deletable,   "
	 					+"	stringkeyunit "
	 					+"FROM po_parameters " 
	 					+"JOIN paramdef ON (definition=paramdef.id) " 
	 					+"LEFT JOIN  processtypes ON (processtypes.id=po_parameters.processtypeid) "  
	 					+"LEFT JOIN ( "
	 					+"	SELECT count(a.id),po_parameter_id FROM po_integer_data a GROUP BY po_parameter_id "   
	 					+"	UNION ALL 	"
	 					+"	SELECT count(b.id),po_parameter_id FROM po_float_data b GROUP BY po_parameter_id	UNION ALL " 	
	 					+"	SELECT count(c.id),po_parameter_id FROM po_string_data c GROUP BY po_parameter_id   "
	 					+"	UNION ALL 	"
	 					+"	SELECT count(d.id),po_parameter_id FROM po_measurement_data d GROUP BY po_parameter_id "   
	 					+"	UNION ALL 	SELECT count(e.id),po_parameter_id FROM po_timestamp_data e GROUP BY po_parameter_id " 
	 					+") AS blabla ON blabla.po_parameter_id=po_parameters.id "
	 					+"WHERE po_parameters.id=?");
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
				stringkeys.add(Integer.toString(parameter.getInt("processtypename")));
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
