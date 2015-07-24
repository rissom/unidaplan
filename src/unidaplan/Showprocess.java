package unidaplan;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

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
    	
    	Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
		
      ArrayList<String> stringkeys = new ArrayList<String>(); // Array for translation strings
      
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

	  PreparedStatement pstmt = null;

	  try {
		  
		// get number and type 
		pstmt= DBconn.conn.prepareStatement(	
					"SELECT process_type_id, p_number, pt_string_key "
					+"FROM pnumbers \n"
				  	+"WHERE id=?");
		pstmt.setInt(1, processID);
		JSONArray table= DBconn.jsonArrayFromPreparedStmt(pstmt);
		if (table.length()>0) {
			jsProcess=table.getJSONObject(0);
			processTypeID=jsProcess.getInt("process_type_id");
			stringkeys.add(Integer.toString(jsProcess.getInt("pt_string_key")));
		}else{
			System.out.println("What the fuck");
		}
		
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
		JSONArray table= DBconn.jsonArrayFromPreparedStmt(pstmt);
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
		
    
    // get the process Parameters:
    try{
    	pstmt = DBconn.conn.prepareStatement(
    	"SELECT p_parameters.id, parametergroup, compulsory, p_parameters.pos, "
		+" p_parameters.stringkeyname,  pid, value, p_parametergrps.id AS pgrpid, " 
		+" p_parametergrps.stringkey as parametergrp_key, st.description, paramdef.datatype " 
		+"FROM p_parameters "
		+"JOIN p_parametergrps ON (p_parameters.Parametergroup=p_parametergrps.ID) " 
		+"JOIN paramdef ON (paramdef.id=p_parameters.definition)"
		+"LEFT JOIN acc_process_parameters a ON "
		+"(a.processid=? AND a.id=p_parameters.id AND hidden=FALSE) "
		+"JOIN String_key_table st ON st.id=p_parameters.stringkeyname "
		+"WHERE (p_parameters.processtypeID=? AND p_parameters.id_field=False) " 
		+"ORDER BY pos");
    	pstmt.setInt(1,processID);
    	pstmt.setInt(2,processTypeID);
		JSONArray parameters=DBconn.jsonArrayFromPreparedStmt(pstmt);

		if (parameters.length()>0) { 		
			jsProcess.put("parameters",parameters);
			
      		// extract the Stringkeys
	      	for (int i=0; i<parameters.length();i++) {  
	      		JSONObject tempObj=(JSONObject) parameters.get(i);
	      		stringkeys.add(Integer.toString(tempObj.getInt("stringkeyname")));
	      	}
		}	
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
  	

	// get the strings
	try{
        String query="SELECT id,string_key,language,value FROM Stringtable WHERE string_key=ANY('{";
      	
        StringBuilder buff = new StringBuilder(); // join numbers with commas
        String sep = "";
        for (String str : stringkeys) {
     	    buff.append(sep);
     	    buff.append(str);
     	    sep = ",";
        }
        query+= buff.toString() + "}'::int[])";
        JSONArray theStrings=DBconn.jsonfromquery(query);
        jsProcess.put("strings", theStrings);
	} catch (SQLException e) {
		System.err.println("Showsample: Problems with SQL query for Stringkeys");
	} catch (JSONException e) {
		System.err.println("Showsample: JSON Problem while getting Stringkeys");
	} catch (Exception e2) {
		System.err.println("Showsample: Strange Problem while getting Stringkeys");
	}
	if (jsProcess.length()>0){
		out.println(jsProcess.toString());
	}else{
		out.println("{error:nodata}");
	}
  	DBconn.closeDB();
    }
  }