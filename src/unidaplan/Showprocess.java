package unidaplan;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
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
      response.setContentType("application/json");
      request.setCharacterEncoding("utf-8");
      response.setCharacterEncoding("utf-8");
      PrintWriter out = response.getWriter();
      DBconnection DBconn=new DBconnection();
      DBconn.startDB();
      
  	int processID=1;
  	int processTypeID=1;
  	try  {
  		 processID=Integer.parseInt(request.getParameter("id")); 
    }
  	catch (Exception e1) {
  		processID=1;
  		System.out.print("no object ID given!");
//  		e1.printStackTrace();
  	 	}
  	String plang="de"; // primary language = Deutsch
  	String slang="en"; // secondary language = english

	PreparedStatement pstmt = null;

	try {
		// get number and type 
		pstmt= DBconn.conn.prepareStatement(	
					"SELECT process_type_id, p_number FROM pnumbers \n"
				  	+"WHERE id=?");
		pstmt.setInt(1, processID);
		JSONArray table= DBconn.jsonFromPreparedStmt(pstmt);
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
		pstmt= DBconn.conn.prepareStatement( 			
				 "SELECT COALESCE(sta.value,stb.value) AS processtype FROM processes \n"
				+"JOIN processtypes pt ON (processes.processtypesid=pt.id) \n"
				+"LEFT JOIN stringtable sta ON (sta.string_key=pt.name AND sta.language=?) \n"
				+"LEFT JOIN stringtable stb ON (stb.string_key=pt.name AND stb.language=?) \n"
				+"WHERE processes.id=? \n");
	   pstmt.setString(1, plang);
	   pstmt.setString(2, slang);
	   pstmt.setInt(3, processID);
	   JSONArray table= DBconn.jsonFromPreparedStmt(pstmt);
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
		pstmt=DBconn.conn.prepareStatement( 
		"SELECT id,p_number FROM pnumbers \n"
		+"WHERE (p_number>? AND process_type_id=?) \n");
		pstmt.setInt(1,processID);
		pstmt.setInt(2,processTypeID);
		JSONArray table= DBconn.jsonFromPreparedStmt(pstmt);
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
		
// 		get the process Parameters:
    
    try{
    	pstmt = DBconn.conn.prepareStatement(
    	"SELECT pa.id, COALESCE (a.value,alt_a.value) AS name, COALESCE (c.value,alt_c.value) AS unit, \n"
	       +"''||p_integer_data.value AS value,n.description, p_parametergrps.id AS parametergrpID, \n"
	 	   +"p.description AS Parameter_desc, COALESCE (b.value,alt_b.value) AS param_grp \n"
	 	   +"FROM p_integer_data \n" 
	 	   +"JOIN p_parameters 	 pa	 ON (p_parameter_id=pa.id AND pa.ID_Field=false) \n" // Don't show title fields
	 	   +"JOIN string_key_table n  	 ON (n.id=pa.stringkeyname) \n"
	 	   +"JOIN p_parametergrps 	 ON (pa.Parametergroup=p_parametergrps.ID)  \n"
	 	   +"JOIN string_key_table p	 ON (p.id=p_parametergrps.stringkey)  \n"
	 	   +"LEFT JOIN stringtable a 	 ON (a.string_key=n.id AND a.language=? )  \n" // Primary language
	 	   +"LEFT JOIN stringtable alt_a ON (alt_a.string_key=n.id AND alt_a.language=?)  \n" // 2. lang. 
	 	   +"LEFT JOIN stringtable b 	 ON (b.string_key=p.id AND b.language=? )  \n" // Primary language
	 	   +"LEFT JOIN stringtable alt_b ON (alt_b.string_key=p.id AND alt_b.language=?)  \n" // 2. lang. \n"
	 	   +"JOIN paramdef pd 			 ON (pd.id=pa.definition) \n"
	 	   +"LEFT JOIN stringtable c 	 ON (c.string_key=pd.StringKeyUnit AND c.language=? )  \n" // Primary language
	 	   +"LEFT JOIN stringtable alt_c ON (alt_c.string_key=pd.StringKeyUnit AND alt_c.language=?) \n" // 2. lang. \n"
	 	   +"WHERE p_integer_data.ProcessID=? \n"
	 	   +"UNION ALL \n"
	 	   +"SELECT pa.id, COALESCE (a.value,alt_a.value) AS name, COALESCE (c.value,alt_c.value) AS unit, \n"
	 	   +"       to_char(p_float_data.value, 'FM99999.99999') AS value,n.description, p_parametergrps.id AS parametergrpID, \n"
	 	   +" 	   p.description AS Parameter_desc, COALESCE (b.value,alt_b.value) AS param_grp \n"
	 	   +"FROM p_float_data  \n"
	 	   +"JOIN p_parameters 	 pa	 ON (p_parameter_id=pa.id)  \n"  
	 	   +"JOIN string_key_table n  	 ON (n.id=pa.stringkeyname) \n"
	 	   +"JOIN p_parametergrps 	 ON (pa.Parametergroup=p_parametergrps.ID) \n" 
	 	   +"JOIN string_key_table p	 ON (p.id=p_parametergrps.stringkey)  \n"
	 	   +"LEFT JOIN stringtable a 	 ON (a.string_key=n.id AND a.language=? )  \n" // Primary language
	 	   +"LEFT JOIN stringtable alt_a ON (alt_a.string_key=n.id AND alt_a.language=?) \n" // 2. lang. 
	 	   +"LEFT JOIN stringtable b 	 ON (b.string_key=p.id AND b.language=? ) \n" // Primary language
	 	   +"LEFT JOIN stringtable alt_b ON (alt_b.string_key=p.id AND alt_b.language=?) \n" // 2. lang. 
	 	   +"JOIN paramdef pd 			 ON (pd.id=pa.definition) \n"
	 	   +"LEFT JOIN stringtable c 	 ON (c.string_key=pd.StringKeyUnit AND c.language=? ) \n" // Primary language
	 	   +"LEFT JOIN stringtable alt_c ON (alt_c.string_key=pd.StringKeyUnit AND alt_c.language=?)  \n" // 2. lang. 
	 	   +"WHERE p_float_data.ProcessID=? \n"
	 	   +"UNION ALL \n"
	 	   +"SELECT pa.id, COALESCE (a.value,alt_a.value) AS name, COALESCE (c.value,alt_c.value) AS unit, \n"
	 	   +"       to_char(p_measurement_data.value,'FM99999.99999')||'+/-'||to_char(p_measurement_data.error,'FM99999.99999') \n"
	 	   +"		 AS value,n.description, p_parametergrps.id AS parametergrpID, \n"
	 	   +" 	   p.description AS Parameter_desc, COALESCE (b.value,alt_b.value) AS param_grp \n"
	 	   +"FROM p_measurement_data  \n"
	 	   +"JOIN p_parameters 	 pa	 ON (p_parameter_id=pa.id AND pa.ID_Field=false)  \n" // Don't show title fields
	 	   +"JOIN string_key_table n  	 ON (n.id=pa.stringkeyname) \n"
	 	   +"JOIN p_parametergrps 	 ON (pa.Parametergroup=p_parametergrps.ID)  \n"
	 	   +"JOIN string_key_table p	 ON (p.id=p_parametergrps.stringkey)  \n"
	 	   +"LEFT JOIN stringtable a 	 ON (a.string_key=n.id AND a.language=? )  \n" // Primary language
	 	   +"LEFT JOIN stringtable alt_a ON (alt_a.string_key=n.id AND alt_a.language=?)  \n" // 2. lang. 
	 	   +"LEFT JOIN stringtable b 	 ON (b.string_key=p.id AND b.language=? )  \n" // Primary language
	 	   +"LEFT JOIN stringtable alt_b ON (alt_b.string_key=p.id AND alt_b.language=?)  \n" // 2. lang. 
	 	   +"JOIN paramdef pd 			 ON (pd.id=pa.definition) \n"
	 	   +"LEFT JOIN stringtable c 	 ON (c.string_key=pd.StringKeyUnit AND c.language=? ) \n" // Primary language
	 	   +"LEFT JOIN stringtable alt_c ON (alt_c.string_key=pd.StringKeyUnit AND alt_c.language=?) -- 2. lang. \n"
	 	   +"WHERE p_measurement_data.ProcessID=? \n"
	 	   +"UNION ALL \n"
	 	   +"SELECT pa.id, COALESCE (a.value,alt_a.value) AS name, COALESCE (c.value,alt_c.value) AS unit, \n"
	 	   +"       p_string_data.value AS value,n.description, p_parametergrps.id AS parametergrpID, \n"
	 	   +" 	   p.description AS Parameter_desc, COALESCE (b.value,alt_b.value) AS param_grp \n"
	 	   +"FROM p_string_data  \n"
	 	   +"JOIN p_parameters 	 pa	 ON (p_parameter_id=pa.id AND pa.ID_Field=false)  \n" // Don't show title fields
	 	   +"JOIN string_key_table n  	 ON (n.id=pa.stringkeyname) \n"
	 	   +"JOIN p_parametergrps 	 ON (pa.Parametergroup=p_parametergrps.ID)  \n"
	 	   +"JOIN string_key_table p	 ON (p.id=p_parametergrps.stringkey)  \n"
	 	   +"LEFT JOIN stringtable a 	 ON (a.string_key=n.id AND a.language=? )  \n" // Primary language
	 	   +"LEFT JOIN stringtable alt_a ON (alt_a.string_key=n.id AND alt_a.language=?) \n" // 2. lang. 
	 	   +"LEFT JOIN stringtable b 	 ON (b.string_key=p.id AND b.language=? ) \n" // Primary language
	 	   +"LEFT JOIN stringtable alt_b ON (alt_b.string_key=p.id AND alt_b.language=?)  \n" // 2. lang. 
	 	   +"JOIN paramdef pd 			 ON (pd.id=pa.definition) \n"
	 	   +"LEFT JOIN stringtable c 	 ON (c.string_key=pd.StringKeyUnit AND c.language=? )  \n" // Primary language
	 	   +"LEFT JOIN stringtable alt_c ON (alt_c.string_key=pd.StringKeyUnit AND alt_c.language=?) \n"// 2. lang. 
	 	   +"WHERE p_string_data.ProcessID=?");
    	pstmt.setString(1,plang);
    	pstmt.setString(2,slang);
    	pstmt.setString(3,plang);
    	pstmt.setString(4,slang);
    	pstmt.setString(5,plang);
    	pstmt.setString(6,slang);
    	pstmt.setInt(7,processID);
    	pstmt.setString(8,plang);
    	pstmt.setString(9,slang);
    	pstmt.setString(10,plang);
    	pstmt.setString(11,slang);
    	pstmt.setString(12,plang);
    	pstmt.setString(13,slang);
    	pstmt.setInt(14,processID);
    	pstmt.setString(15,plang);
    	pstmt.setString(16,slang);
    	pstmt.setString(17,plang);
    	pstmt.setString(18,slang);
    	pstmt.setString(19,plang);
    	pstmt.setString(20,slang);
    	pstmt.setInt(21,processID);
    	pstmt.setString(22,plang);
    	pstmt.setString(23,slang);
    	pstmt.setString(24,plang);
    	pstmt.setString(25,slang);
    	pstmt.setString(26,plang);
    	pstmt.setString(27,slang);
    	pstmt.setInt(28,processID);
		JSONArray table= DBconn.jsonFromPreparedStmt(pstmt);
		if (table.length()>0) {
			jsProcess.put("parameters",table); } 
	} catch (SQLException e) {
		System.out.println("Problems with SQL query for parameters");
		e.printStackTrace();
	} catch (JSONException e){
		System.out.println("Problems creating JSON for parameters");
		e.printStackTrace();
	} catch (Exception e) {
		System.out.println("Strange Problems with the parameters");
		e.printStackTrace();
	}	  	
  	

  	out.println(jsProcess.toString());
  	DBconn.closeDB();
    	}
  }