package unidaplan;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Showsample extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static JSONArray result;
	private static PreparedStatement pstmt;
	private static JSONArray table;



@Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    response.setContentType("application/json");
    request.setCharacterEncoding("utf-8");
    response.setCharacterEncoding("utf-8");
    PrintWriter out = response.getWriter();
 	DBconnection DBconn=new DBconnection();
    DBconn.startDB();
	String plang="de"; // primary language = Deutsch
	String slang="en"; // secondary language = english
	int objID=1;      // variable initialisation
	JSONObject jsSample=new JSONObject(); // variable initialisation
	
	// get Parameter for id
	try{
		 objID=Integer.parseInt(request.getParameter("id")); }
	catch (Exception e1) {
		objID=1;
		System.out.print("no object ID given!");
	}
	   
    // fetch name and type of the object from the database (objectnames is a view)
    try{
		pstmt= DBconn.conn.prepareStatement( 	
				"SELECT name, type FROM objectnames WHERE id=?");
		pstmt.setInt(1,objID);
		jsSample= DBconn.jsonObjectFromPreparedStmt(pstmt);
	} catch (SQLException e) {
		System.out.println("Problems with SQL query for sample name");
		e.printStackTrace();	
	} catch (JSONException e) {
		System.out.println("JSON Problem while getting sample name");
		e.printStackTrace();
	} catch (Exception e2) {
		System.out.println("Strange Problem while getting sample name");
		e2.printStackTrace();
	}

    
    // Error if the sample is not found
    if (jsSample.length()==0) {
    	try {
			jsSample.put("error", "sample not found");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    else {
    
	try {
		JSONArray parameters=new JSONArray();  	
		pstmt= DBconn.conn.prepareStatement( 	
		    "SELECT op.id,  o_integer_data.id as pid, COALESCE (a.value,alt_a.value) AS name, COALESCE (c.value,alt_c.value) AS unit, \n"
		+       "''||o_integer_data.value AS value,n.description, ot_parametergrps.id AS parametergrpID, \n"
		+ 	   "p.description AS Parameter_desc, COALESCE (b.value,alt_b.value) AS param_grp \n"
		+"FROM o_integer_data \n"
		+"JOIN ot_parameters 	 op	 ON (ot_parameter_id=op.id AND op.ID_Field=false) \n" // Don't show title fields
		+"JOIN string_key_table n  	 ON (n.id=op.stringkeyname) \n"
		+"JOIN ot_parametergrps 	 ON (op.Parametergroup=ot_parametergrps.ID) \n"
		+"JOIN string_key_table p	 ON (p.id=ot_parametergrps.stringkey) \n" 
		+"LEFT JOIN stringtable a 	 ON (a.string_key=n.id AND a.language=? ) \n"  // Primary language
		+"LEFT JOIN stringtable alt_a ON (alt_a.string_key=n.id AND alt_a.language=?) \n" // 2. lang. 
		+"LEFT JOIN stringtable b 	 ON (b.string_key=p.id AND b.language=? ) \n" // Primary language
		+"LEFT JOIN stringtable alt_b ON (alt_b.string_key=p.id AND alt_b.language=?) \n" // 2. lang. \n"
		+"JOIN paramdef pd 			 ON (pd.id=op.definition) \n"
		+"LEFT JOIN stringtable c 	 ON (c.string_key=pd.StringKeyUnit AND c.language=? )\n" // Primary language
		+"LEFT JOIN stringtable alt_c ON (alt_c.string_key=pd.StringKeyUnit AND alt_c.language=?)\n" // 2. lang. 
		+"WHERE objectID=?"
		+"UNION ALL \n"
		+"SELECT op.id, o_float_data.id as pid, COALESCE (a.value,alt_a.value) AS name, COALESCE (c.value,alt_c.value) AS unit, \n"
		+      "to_char(o_float_data.value, 'FM99999.99999') AS value,n.description, ot_parametergrps.id AS parametergrpID, \n"
		+ 	   "p.description AS Parameter_desc, COALESCE (b.value,alt_b.value) AS param_grp \n"
		+"FROM o_float_data  \n"
		+"LEFT JOIN ot_parameters op ON (ot_parameter_id=op.id) \n"
		+"JOIN string_key_table n  	 ON (n.id=op.stringkeyname) \n"
		+"JOIN ot_parametergrps 	 ON (op.Parametergroup=ot_parametergrps.ID) \n" 
		+"JOIN string_key_table p	 ON (p.id=ot_parametergrps.stringkey)  \n"
		+"LEFT JOIN stringtable a 	 ON (a.string_key=n.id AND a.language=? ) \n" // Primary language 
		+"LEFT JOIN stringtable alt_a	 ON (alt_a.string_key=n.id AND alt_a.language=?)  \n" // 2. lang. 
		+"LEFT JOIN stringtable b 	 ON (b.string_key=p.id AND b.language=? )  \n" // Primary language
		+"LEFT JOIN stringtable alt_b	 ON (alt_b.string_key=p.id AND alt_b.language=?)  \n" // 2. lang. \n"
		+"JOIN paramdef pd 			 ON (pd.id=op.definition) \n"
		+"LEFT JOIN stringtable c 	 ON (c.string_key=pd.StringKeyUnit AND c.language=? ) \n" // Primary language
		+"LEFT JOIN stringtable alt_c	 ON (alt_c.string_key=pd.StringKeyUnit AND alt_c.language=?) \n" // 2. lang. 
		+"WHERE objectID=? \n"
		+"UNION ALL \n"
		+"SELECT op.id, o_measurement_data.id as pid, COALESCE (a.value,alt_a.value) AS name, COALESCE (c.value,alt_c.value) AS unit, \n"
		+      "to_char(o_measurement_data.value,'FM99999.99999')||' ± '||to_char(o_measurement_data.error,'FM99999.99999')"
		+       " AS value,n.description, ot_parametergrps.id AS parametergrpID, \n"
		+ 	   "p.description AS Parameter_desc, COALESCE (b.value,alt_b.value) AS param_grp \n"
		+"FROM o_measurement_data  \n"
		+"JOIN ot_parameters  op ON (ot_parameter_id=op.id) \n"
		+"JOIN string_key_table n  	 ON (n.id=op.stringkeyname) \n"
		+"JOIN ot_parametergrps 	 ON (op.Parametergroup=ot_parametergrps.ID) \n" 
		+"JOIN string_key_table p	 ON (p.id=ot_parametergrps.stringkey)  \n"
		+"LEFT JOIN stringtable a 	 ON (a.string_key=n.id AND a.language=? ) \n" // Primary language
		+"LEFT JOIN stringtable alt_a ON (alt_a.string_key=n.id AND alt_a.language=?) \n" // 2. lang. 
		+"LEFT JOIN stringtable b 	 ON (b.string_key=p.id AND b.language=? ) \n" // Primary language
		+"LEFT JOIN stringtable alt_b ON (alt_b.string_key=p.id AND alt_b.language=?)  \n"// 2. lang. \n"
		+"JOIN paramdef pd 			 ON (pd.id=op.definition) \n"
		+"LEFT JOIN stringtable c 	 ON (c.string_key=pd.StringKeyUnit AND c.language=? ) \n" // Primary language
		+"LEFT JOIN stringtable alt_c ON (alt_c.string_key=pd.StringKeyUnit AND alt_c.language=?)  \n" // 2. lang. \n"
		+"WHERE objectID=? \n"
		+"UNION ALL \n"
		+"SELECT op.id, o_string_data.id as pid, COALESCE (a.value,alt_a.value) AS name, COALESCE (c.value,alt_c.value) AS unit, \n"
		+"       o_string_data.value AS value,n.description, ot_parametergrps.id AS parametergrpID, \n"
		+" 	   p.description AS Parameter_desc, COALESCE (b.value,alt_b.value) AS param_grp \n"
		+"FROM o_string_data  \n"
		+"JOIN ot_parameters op	   	ON (ot_parameter_id=op.id) \n"
		+"JOIN string_key_table n  	ON (n.id=op.stringkeyname) \n"
		+"JOIN ot_parametergrps 	ON (op.Parametergroup=ot_parametergrps.ID) \n" 
		+"JOIN string_key_table p	ON (p.id=ot_parametergrps.stringkey)  \n"
		+"LEFT JOIN stringtable a 	ON (a.string_key=n.id AND a.language=? ) \n" // Primary language
		+"LEFT JOIN stringtable alt_a ON (alt_a.string_key=n.id AND alt_a.language=?)  \n" // 2. lang. 
		+"LEFT JOIN stringtable b 	ON (b.string_key=p.id AND b.language=? )  \n" // Primary language
		+"LEFT JOIN stringtable alt_b ON (alt_b.string_key=p.id AND alt_b.language=?)  \n" // 2. lang. \n"
		+"JOIN paramdef pd 			ON (pd.id=op.definition) \n"
		+"LEFT JOIN stringtable c 	ON (c.string_key=pd.StringKeyUnit AND c.language=? )  \n" // Primary language 
		+"LEFT JOIN stringtable alt_c ON (alt_c.string_key=pd.StringKeyUnit AND alt_c.language=?)  \n" // 2. lang. \n"
		+"WHERE objectID=?"); 
		pstmt.setString(1,plang);
		pstmt.setString(2,slang);
		pstmt.setString(3,plang);
		pstmt.setString(4,slang);
		pstmt.setString(5,plang);
		pstmt.setString(6,slang);
		pstmt.setInt(7,objID);
		pstmt.setString(8,plang);
		pstmt.setString(9,slang);
		pstmt.setString(10,plang);
		pstmt.setString(11,slang);
		pstmt.setString(12,plang);
		pstmt.setString(13,slang);
		pstmt.setInt(14,objID);
		pstmt.setString(15,plang);
		pstmt.setString(16,slang);
		pstmt.setString(17,plang);
		pstmt.setString(18,slang);
		pstmt.setString(19,plang);
		pstmt.setString(20,slang);
		pstmt.setInt(21,objID);
		pstmt.setString(22,plang);
		pstmt.setString(23,slang);
		pstmt.setString(24,plang);
		pstmt.setString(25,slang);
		pstmt.setString(26,plang);
		pstmt.setString(27,slang);
		pstmt.setInt(28,objID);
		table= DBconn.jsonArrayFromPreparedStmt(pstmt);
		if (table.length()>0) {
		jsSample.put("parameters",table); } 
	} catch (SQLException e) {
		System.out.println("Problems with SQL query for sample parameters");
		e.printStackTrace();
	} catch (JSONException e){
		System.out.println("Problems creating JSON for sample parameters");
		e.printStackTrace();
	} catch (Exception e) {
		System.out.println("Strange Problems with the sample parameters");
		e.printStackTrace();
	}	

    	
		// Find all experiment plans (TODO)
    	try {				
			JSONArray vps = new JSONArray();
			vps.put (123);
			vps.put (456);
			jsSample.put("plans",vps);
    	} catch (Exception e){
    		e.printStackTrace();
    	}
			
		// Find all child objects
    	try{
		    pstmt=  DBconn.conn.prepareStatement( 	
			"SELECT originates_from.id, objectnames.id, objectnames.name, objectnames.type \n"+
			"FROM originates_from \n"+
			"JOIN objectnames ON (objectnames.id=originates_from.child) \n"+
			"WHERE originates_from.parent=? \n");
			pstmt.setInt(1,objID);
			table= DBconn.jsonArrayFromPreparedStmt(pstmt);
			if (table.length()>0) {
				jsSample.put("children",table); } 
    	} catch (SQLException e) {
    		System.out.println("Problems with SQL query for sample children");
    		e.printStackTrace();
    	} catch (Exception e2) {
				e2.printStackTrace();
		}
    	
		// find all parent objects
		try{    
		    pstmt=  DBconn.conn.prepareStatement( 	
			"SELECT originates_from.id, objectnames.id, objectnames.name, objectnames.type \n" +
			"FROM originates_from \n" +
			"JOIN objectnames ON (objectnames.id=originates_from.parent) \n" +
			"WHERE originates_from.child=? \n");
			pstmt.setInt(1,objID);
			table= DBconn.jsonArrayFromPreparedStmt(pstmt);
			if (table.length()>0) {
				jsSample.put("ancestors",table); } 
	    } catch (SQLException e) {
    		System.out.println("Problems with SQL query for parent samples");
    		e.printStackTrace();	
		} catch (JSONException e2) {
			System.out.println("JSON Problem while getting parent samples");
			e2.printStackTrace();
		} catch (Exception e3) {
			System.out.println("Strange Problem while getting parent samples");
    		e3.printStackTrace();
    	}
		
		// find the previous sample
		try{
		    pstmt=  DBconn.conn.prepareStatement( 	
    		"SELECT  objectnames.id, objectnames.name, objectnames.type \n"
			+"FROM objectnames \n"
			+"WHERE ((objectnames.name < (SELECT objectnames.name FROM objectnames WHERE objectnames.id=?)) \n"
			+"AND objectnames.type=(SELECT objectnames.type FROM objectnames WHERE objectnames.id=?)) \n"
			+"ORDER BY objectnames.name DESC \n"
			+"LIMIT 1");
			pstmt.setInt(1,objID);
			pstmt.setInt(2,objID);
			table= DBconn.jsonArrayFromPreparedStmt(pstmt);
			if (table.length()>0) {
				jsSample.put("previous",table.get(0)); }
	    } catch (SQLException e) {
    		System.out.println("Problems with SQL query for previous sample");
    		e.printStackTrace();	
		} catch (JSONException e2) {
			System.out.println("JSON Problem while getting previous sample");
			e2.printStackTrace();
		} catch (Exception e3) {
			System.out.println("Strange Problem while getting previous sample");
    		e3.printStackTrace();
    	}
		
		// find next sample	
		try{
		    pstmt=  DBconn.conn.prepareStatement( 	
    		"SELECT  objectnames.id, objectnames.name, objectnames.type \n"
			+"FROM objectnames \n"
			+"WHERE ((objectnames.name > (SELECT objectnames.name FROM objectnames WHERE objectnames.id=?)) \n"
			+"AND objectnames.type=(SELECT objectnames.type FROM objectnames WHERE objectnames.id=?)) \n"
			+"ORDER BY objectnames.name \n"	
    		+"LIMIT 1 \n");
			pstmt.setInt(1,objID);
			pstmt.setInt(2,objID); 
			table= DBconn.jsonArrayFromPreparedStmt(pstmt);
			if (table.length()>0) {
				jsSample.put("next",table.get(0)); }	
		} catch (SQLException e) {
    		System.out.println("Problems with SQL query for sample children");
    		e.printStackTrace();	
    	} catch (JSONException e) {
			System.out.println("JSON Problem while getting next sample");
			e.printStackTrace();
    	} catch (Exception e2) {
			System.out.println("Strange Problem while getting next sample");
    		e2.printStackTrace();
    	}

    	
    }
	out.println(jsSample.toString());
	DBconn.closeDB();
  	}
}