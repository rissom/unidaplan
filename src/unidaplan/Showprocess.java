package unidaplan;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Servlet implementation class Showprocess
 */
@WebServlet("/showprocess.json")
public class Showprocess extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static JSONArray parameters;
	private static JSONArray intparameters;
	private static JSONArray floatparameters;
	private static JSONArray stringparameters;
	private static JSONArray measurementparameters;
	private static JSONObject jsProcess;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Showprocess() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
      response.setContentType("text/html");
      PrintWriter out = response.getWriter();
      DBconnection.startDB();
      
  	int processID=1;
  	int processTypeID=1;
  	try  {
  		 processID=Integer.parseInt(request.getParameter("ID")); }
  	catch (Exception e1) {
  		processID=1;
  		System.out.print("no object ID given!");
//  		e1.printStackTrace();
  	 	}
  	String plang="de"; // primary language = Deutsch
  	String slang="en"; // secondary language = english

	PreparedStatement stmt = null;

	try {
		// get number and type 
		stmt= DBconnection.conn.prepareStatement(	
					"SELECT process_type_id, p_number FROM pnumbers \n"
				  	+"WHERE id=?");
		stmt.setInt(1, processID);
		JSONArray table= DBconnection.jsonFromPreparedStmt(stmt);
		jsProcess=table.getJSONObject(0);
		processTypeID=jsProcess.getInt("process_type_id");
	} catch (SQLException e) {
		System.out.println("Problems with SQL query");
		e.printStackTrace();
	} catch (JSONException e){
		System.out.println("Problems creating JSON");
		e.printStackTrace();
	} catch (Exception e) {
		System.out.println("Strange Problems");
		e.printStackTrace();
	}
		
		
		// get processtype as localized string
	try {	
		stmt= DBconnection.conn.prepareStatement( 			
				 "SELECT COALESCE(sta.value,stb.value) AS processtype FROM processes \n"
				+"JOIN processtypes pt ON (processes.processtypesid=pt.id) \n"
				+"LEFT JOIN stringtable sta ON (sta.string_key=pt.name AND sta.language=?) \n"
				+"LEFT JOIN stringtable stb ON (stb.string_key=pt.name AND stb.language=?) \n"
				+"WHERE processes.id=? \n");
	   stmt.setString(1, plang);
	   stmt.setString(2, slang);
	   stmt.setInt(3, processID);
	   JSONArray table= DBconnection.jsonFromPreparedStmt(stmt);
	   jsProcess.put("processtype",table.getJSONObject(0).get("processtype")); // no easy way to merge JSON Objects.
	} catch (SQLException e) {
		System.out.println("Problems with SQL query");
		e.printStackTrace();
	} catch (JSONException e){
		System.out.println("Problems creating JSON");
		e.printStackTrace();
	} catch (Exception e) {
		System.out.println("Strange Problems");
		e.printStackTrace();
	}
	
	
    // get next process
    try {       
		stmt=DBconnection.conn.prepareStatement( 
		"SELECT id,p_number FROM pnumbers \n"
		+"WHERE (p_number>? AND process_type_id=?) \n");
		stmt.setInt(1,processID);
		stmt.setInt(2,processTypeID);
		JSONArray table= DBconnection.jsonFromPreparedStmt(stmt);
		if (table.length()>0) {
		jsProcess.put("next",table.getJSONObject(0)); } 
	} catch (SQLException e) {
		System.out.println("Problems with SQL query for next process");
		e.printStackTrace();
	} catch (JSONException e){
		System.out.println("Problems creating JSON for next process");
		e.printStackTrace();
	} catch (Exception e) {
		System.out.println("Strange Problems with the next process");
		e.printStackTrace();
	}	
		
//  	parameters=new JSONArray();
//  	
//      query =  // fetch integerparameters from database
//  	"SELECT op.id, op.pos, COALESCE (a.value,alt_a.value) AS name, COALESCE (c.value,alt_c.value) AS unit, \n" +
//  	"  o_integer_data.value,n.description, ot_parametergrps.id AS parametergrpID, \n" +
//  	"  p.description AS Parameter_desc, COALESCE (b.value,alt_b.value) AS param_grp \n" +
//  	"FROM o_integer_data \n" +
//  	"JOIN ot_parameters op ON (ot_parameter_id=op.id) \n" +
//  	"JOIN string_key_table n ON (n.id=op.stringkeyname) \n" +
//  	"JOIN ot_parametergrps ON (op.Parametergroup=ot_parametergrps.ID)  \n" +
//  	"JOIN string_key_table p ON (p.id=ot_parametergrps.stringkey)  \n" +
//  	"LEFT JOIN stringtable a ON (a.string_key=n.id AND a.language='"+plang+"' ) \n" +
//  	"LEFT JOIN stringtable alt_a ON (alt_a.string_key=n.id AND alt_a.language='"+slang+"') \n" +
//  	"LEFT JOIN stringtable b ON (b.string_key=p.id AND b.language='"+plang+"' ) \n" +
//  	"LEFT JOIN stringtable alt_b ON (alt_b.string_key=p.id AND alt_b.language='"+slang+"') \n" +
//  	"JOIN paramdef pd ON (pd.id=op.definition) \n" +
//  	"LEFT JOIN stringtable c ON (c.string_key=pd.StringKeyUnit AND c.language='"+plang+"' ) \n" +
//  	"LEFT JOIN stringtable alt_c ON (alt_c.string_key=pd.StringKeyUnit AND alt_c.language='"+slang+"') \n" +
//  	"WHERE objectID="+processID+" ORDER BY op.pos";
//
//      try {  // get json from the database using the query
//  		intparameters=DBconnection.jsonfromquery(query);
//  	} catch (Exception e1) {
//  		e1.printStackTrace();
//  	}
//      
//      for (int i = 0; i < intparameters.length(); i++) {
//          try {
//  			parameters.put(intparameters.get(i));
//  		} catch (JSONException e) {
//  			System.out.print("Error appending intparameters");
//  		}
//      }
//      
//      query = // fetch floatparameters from database
//  	"SELECT op.id, op.pos, COALESCE (a.value,alt_a.value) AS name, COALESCE (c.value,alt_c.value) AS unit, \n" +
//  	"  o_float_data.value,n.description, ot_parametergrps.id AS parametergrpID, \n" +
//  	"  p.description AS Parameter_desc, COALESCE (b.value,alt_b.value) AS param_grp \n" +
//  	"FROM o_float_data \n" +
//  	"JOIN ot_parameters op ON (ot_parameter_id=op.id) \n" +
//  	"JOIN string_key_table n ON (n.id=op.stringkeyname) \n" +
//  	"JOIN ot_parametergrps ON (op.Parametergroup=ot_parametergrps.ID)  \n" +
//  	"JOIN string_key_table p ON (p.id=ot_parametergrps.stringkey)  \n" +
//  	"LEFT JOIN stringtable a ON (a.string_key=n.id AND a.language='"+plang+"' ) \n" +
//  	"LEFT JOIN stringtable alt_a ON (alt_a.string_key=n.id AND alt_a.language='"+slang+"') \n" +
//  	"LEFT JOIN stringtable b ON (b.string_key=p.id AND b.language='"+plang+"' ) \n" +
//  	"LEFT JOIN stringtable alt_b ON (alt_b.string_key=p.id AND alt_b.language='"+slang+"') \n" +
//  	"JOIN paramdef pd ON (pd.id=op.definition) \n" +
//  	"LEFT JOIN stringtable c ON (c.string_key=pd.StringKeyUnit AND c.language='"+plang+"' ) \n" +
//  	"LEFT JOIN stringtable alt_c ON (alt_c.string_key=pd.StringKeyUnit AND alt_c.language='"+slang+"') \n" +
//  	"WHERE objectID="+processID+" ORDER BY op.pos";
//      
//      try {  // get json from the database using the query
//      	floatparameters=DBconnection.jsonfromquery(query);
//  	} catch (Exception e1) {
//  		e1.printStackTrace();
//  	}    
//      
//      for (int i = 0; i < floatparameters.length(); i++) {
//          try {
//  			parameters.put(floatparameters.get(i));
//  		} catch (JSONException e) {
//  			System.out.print("Error appending floatparameters");
//  		}
//      }
//      
//      query = // fetch measurementparameters from database
//  	"SELECT op.id, op.pos, COALESCE (a.value,alt_a.value) AS name, COALESCE (c.value,alt_c.value) AS unit, \n" +
//  	"  o_measurement_data.value, o_measurement_data.error,n.description, ot_parametergrps.id AS parametergrpID, \n" +
//  	"  p.description AS Parameter_desc, COALESCE (b.value,alt_b.value) AS param_grp \n" +
//  	"FROM o_measurement_data \n" +
//  	"JOIN ot_parameters op ON (ot_parameter_id=op.id) \n" +
//  	"JOIN string_key_table n ON (n.id=op.stringkeyname) \n" +
//  	"JOIN ot_parametergrps ON (op.Parametergroup=ot_parametergrps.ID)  \n" +
//  	"JOIN string_key_table p ON (p.id=ot_parametergrps.stringkey)  \n" +
//  	"LEFT JOIN stringtable a ON (a.string_key=n.id AND a.language='"+plang+"' ) \n" +
//  	"LEFT JOIN stringtable alt_a ON (alt_a.string_key=n.id AND alt_a.language='"+slang+"') \n" +
//  	"LEFT JOIN stringtable b ON (b.string_key=p.id AND b.language='"+plang+"' ) \n" +
//  	"LEFT JOIN stringtable alt_b ON (alt_b.string_key=p.id AND alt_b.language='"+slang+"') \n" +
//  	"JOIN paramdef pd ON (pd.id=op.definition) \n" +
//  	"LEFT JOIN stringtable c ON (c.string_key=pd.StringKeyUnit AND c.language='"+plang+"' ) \n" +
//  	"LEFT JOIN stringtable alt_c ON (alt_c.string_key=pd.StringKeyUnit AND alt_c.language='"+slang+"') \n" +
//  	"WHERE objectID="+processID+" ORDER BY op.pos";
//      
//      try {  // get json from the database using the query
//      	measurementparameters=DBconnection.jsonfromquery(query);
//  	} catch (Exception e1) {
//  		e1.printStackTrace();
//  	}
//      
//      for (int i = 0; i < measurementparameters.length(); i++) {
//          try {
//  			parameters.put(measurementparameters.get(i));
//  		} catch (JSONException e) {
//  			System.out.print("Error appending measurementparameters");
//  		}
//      }
//      
//      
//      query = // fetch Stringparameter from database
//  	"SELECT op.id, op.pos, COALESCE (a.value,alt_a.value) AS name, \n" +
//  	"  o_string_data.value,n.description, ot_parametergrps.id AS parametergrpID, \n" +
//  	"  p.description AS Parameter_desc, COALESCE (b.value,alt_b.value) AS param_grp \n" +
//  	"FROM o_string_data \n" +
//  	"JOIN ot_parameters op ON (ot_parameter_id=op.id) \n" +
//  	"JOIN string_key_table n ON (n.id=op.stringkeyname) \n" +
//  	"JOIN ot_parametergrps ON (op.Parametergroup=ot_parametergrps.ID)  \n" +
//  	"JOIN string_key_table p ON (p.id=ot_parametergrps.stringkey)  \n" +
//  	"LEFT JOIN stringtable a ON (a.string_key=n.id AND a.language='"+plang+"' ) \n" +
//  	"LEFT JOIN stringtable alt_a ON (alt_a.string_key=n.id AND alt_a.language='"+slang+"') \n" +
//  	"LEFT JOIN stringtable b ON (b.string_key=p.id AND b.language='"+plang+"' ) \n" +
//  	"LEFT JOIN stringtable alt_b ON (alt_b.string_key=p.id AND alt_b.language='"+slang+"') \n" +
//  	"WHERE objectID="+processID+" ORDER BY op.pos";
//      
//      try {  // get json from the database using the query
//      	stringparameters=DBconnection.jsonfromquery(query);
//  	} catch (Exception e1) {
//  		e1.printStackTrace();
//  	}
//      
//      for (int i = 0; i < stringparameters.length(); i++) {
//          try {
//  			parameters.put(stringparameters.get(i));
//  		} catch (JSONException e) {
//  			System.out.print("Error appending floatparameters");
//  		}
//      }
//      
//      
//
//     
////      System.out.print(result.toString());   // JSON auf Console ausgeben
//      if (parameters.length()==0) {
//      	try {
//  			jsSample.put("error", "sample not found");
//  		} catch (JSONException e) {
//  			// TODO Auto-generated catch block
//  			e.printStackTrace();
//  		}
//      }
//      else {
//      	
//      	try {	
//  			jsSample.put("parameters",parameters);
//  			
//  			JSONArray vps = new JSONArray();
//  			vps.put (123);
//  			vps.put (456);
//  			jsSample.put("plans",vps);
//  				    
//  			PreparedStatement stmt = null;
//
//  			stmt= DBconnection.conn.prepareStatement(	 // find previous process
//      		"SELECT id, p_number FROM pnumbers WHERE (p_number>? AND Process_type_ID=?))"
//      		+"ORDER BY p_number DESC"
//      		+"LIMIT 1");
//  	       stmt.setInt(1, processID);
//	       stmt.setString(2, type);
//	       result=stmt.executeQuery(); // get ResultSet from the database using the query
//  			
//  			
//  			if (result.length() > 0) {
//  			    jsSample.put("previous",result.get(0));
//  			}
//      		
//      		query= // find next sample
//      		"SELECT  objectnames.id, objectnames.name, objectnames.type \n"
//  			+"FROM objectnames \n"
//  			+"WHERE ((objectnames.name > (SELECT objectnames.name FROM objectnames WHERE objectnames.id='"+processID+"')) \n"
//  			+"AND objectnames.type=(SELECT objectnames.type FROM objectnames WHERE objectnames.id='"+processID+"')) \n"
//  			+"ORDER BY objectnames.name \n"
//      		+"LIMIT 1 \n";
//  			try {  // get json from the database using the query
//  				result=DBconnection.jsonfromquery(query);
//  			} catch (Exception e2) {
//  				e2.printStackTrace();
//  			}
//  			if (result.length() > 0) {
//  			    jsSample.put("next",result.get(0));
//  			}
//  			
//      	} catch (JSONException e) {
//  			System.out.print("Problem while creating JSON object");
//  			e.printStackTrace();
//      	}
//      	
//      }
  	out.println(jsProcess.toString());
  	DBconnection.closeDB();
    	}
  }