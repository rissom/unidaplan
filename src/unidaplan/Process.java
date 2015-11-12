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

/**
 * Servlet implementation class Process
 */
public class Process extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private static JSONObject jsProcess;
       


	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
    	
    	Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
		userID = userID+1;
		userID = userID-1;
		
      ArrayList<String> stringkeys = new ArrayList<String>(); // Array for translation strings
      
      response.setContentType("application/json");
      request.setCharacterEncoding("utf-8");
      response.setCharacterEncoding("utf-8");
      PrintWriter out = response.getWriter();
      DBconnection DBconn=new DBconnection();
      DBconn.startDB();
      boolean found=false;
      int processID=1;
  	  int processTypeID=1;
  	  int pnumber=0;
  	  try  {
  		 processID=Integer.parseInt(request.getParameter("id")); 
      }
  	  catch (Exception e1) {
  	  	processID=1;
  		System.err.print("Process: no object ID given!");
//  		e1.printStackTrace();
  	  }

	  PreparedStatement pstmt = null;

	  try {
		  
		// get number, type and status 
		pstmt= DBconn.conn.prepareStatement(
				"SELECT processes.id, processes.processtypesid as processtype, ptd.value AS date, n1.value AS pnumber, "
				+"processtypes.name AS pt_string_key, n2.value AS status, pp3.id AS statuspid "
				+"FROM processes "
				+"JOIN processtypes ON (processes.processtypesid=processtypes.id) "
				+"JOIN p_parameters pp1 ON (pp1.definition=10 AND pp1.ProcesstypeID=processes.processtypesid) " // date
				+"JOIN p_parameters pp2 ON (pp2.definition=8 AND pp2.ProcesstypeID=processes.processtypesid) " // number
				+"JOIN p_parameters pp3 ON (pp3.definition=1 AND pp3.ProcesstypeID=processes.processtypesid) " // status
				+"LEFT JOIN p_timestamp_data ptd ON (ptd.processID=processes.id AND ptd.P_Parameter_ID=pp1.id) "
				+"LEFT JOIN p_integer_data n1 ON (n1.ProcessID=processes.id AND n1.P_Parameter_ID=pp2.id) "
				+"LEFT JOIN p_integer_data n2 ON (n2.ProcessID=processes.id AND n2.P_Parameter_ID=pp3.id) "
				+"WHERE processes.id=?");
		pstmt.setInt(1, processID);
		jsProcess= DBconn.jsonObjectFromPreparedStmt(pstmt);
		if (jsProcess.length()>0) {
			processTypeID=jsProcess.getInt("processtype");
			pnumber=jsProcess.getInt("pnumber");
			found=true;
			stringkeys.add(Integer.toString(jsProcess.getInt("pt_string_key")));
		}else{
			System.err.println("no such process");
			response.setStatus(404);
			found=false;
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
			
	if (found){
	    // get next process
	    try {      
			pstmt=DBconn.conn.prepareStatement( 
			"SELECT id,p_number FROM pnumbers "
			+"WHERE (p_number>? AND processtype=?) LIMIT 1");
			pstmt.setInt(1,pnumber);
			pstmt.setInt(2,processTypeID);
			JSONObject next= DBconn.jsonObjectFromPreparedStmt(pstmt);
			if (next.length()>0) {
			jsProcess.put("next",next); } 
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (JSONException e){
			System.err.println("Problems creating JSON for next process");
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("Strange Problems with the next process");
			e.printStackTrace();
		}	
			
	    
	    // get previous process
	    try {       
			pstmt=DBconn.conn.prepareStatement( 
			"SELECT id,p_number FROM pnumbers "
			+"WHERE (p_number<? AND processtype=?) ORDER BY p_number DESC LIMIT 1");
			pstmt.setInt(1,pnumber);
			pstmt.setInt(2,processTypeID);
			JSONObject previous= DBconn.jsonObjectFromPreparedStmt(pstmt);
			if (previous.length()>0) {
			jsProcess.put("previous",previous); } 
		} catch (SQLException e) {
			System.err.println("Process: Problems with SQL query for previous process");
			e.printStackTrace();
		} catch (JSONException e){
			System.err.println("Process: Problems creating JSON for previous process");
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("Process: Strange Problems with the previous process");
			e.printStackTrace();
		}	
	    
	    
	    // get the process Parameters:
	    try{
	    	pstmt = DBconn.conn.prepareStatement(
	    	"SELECT p_parameters.id, parametergroup, compulsory, p_parameters.pos, "
			+" p_parameters.stringkeyname,  pid, value, p_parametergrps.id AS pgrpid, " 
			+" p_parametergrps.stringkey as parametergrp_key, st.description, paramdef.datatype, "
			+" paramdef.stringkeyunit as unit "
			+"FROM p_parameters "
			+"JOIN p_parametergrps ON (p_parameters.Parametergroup=p_parametergrps.ID) " 
			+"JOIN paramdef ON (paramdef.id=p_parameters.definition)"
			+"LEFT JOIN acc_process_parameters a ON "
			+"(a.processid=? AND a.ppid=p_parameters.id AND hidden=FALSE) "
			+"JOIN String_key_table st ON st.id=p_parameters.stringkeyname "
			+"WHERE (p_parameters.processtypeID=? AND p_parameters.id_field=FALSE AND p_parameters.hidden=FALSE) "
			+"ORDER BY pos");
	    	pstmt.setInt(1,processID);
	    	pstmt.setInt(2,processTypeID);
			JSONArray parameters=DBconn.jsonArrayFromPreparedStmt(pstmt);
	
			if (parameters.length()>0) { 		
				jsProcess.put("parameters",parameters);
				
	      		// extract the Stringkeys
		      	for (int i=0; i<parameters.length();i++) {  
		      		JSONObject tempObj=parameters.getJSONObject(i);
		      		stringkeys.add(Integer.toString(tempObj.getInt("stringkeyname")));
		      		if (tempObj.has("unit")){
			      		stringkeys.add(Integer.toString(tempObj.getInt("unit")));
		      		}
		      		String datatype="undefined";
		      		switch (tempObj.getInt("datatype")) {
			      		case 1: datatype="integer"; break;
			      		case 2: datatype="float";  break;
			      		case 3: datatype="measurement";  break;
			      		case 4: datatype="string"; break;
			      		case 5: datatype="long string";  break;
			      		case 6: datatype="chooser"; break;
			      		case 7: datatype="date+time";  break;
			      		case 8: datatype="checkbox"; break;
			      		default: datatype="undefined"; break;		    
		      		}
		      		parameters.getJSONObject(i).remove("datatype");
		      		parameters.getJSONObject(i).put("datatype",datatype);
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
	    
	    
	    // get the assigned objects:
	    try{
	    	pstmt = DBconn.conn.prepareStatement(
	    	"SELECT sp.sampleid, sn.name, sn.typeid  FROM samplesinprocess sp "
	    	+"JOIN samplenames sn ON sp.sampleid=sn.id "
	    	+"WHERE ProcessID=?");
	    	pstmt.setInt(1,processID);
			JSONArray samples=DBconn.jsonArrayFromPreparedStmt(pstmt);
			jsProcess.put("samples",samples);
				
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
	}else{
		out.println("{\"error\":\"notfound\"}");
    }
	DBconn.closeDB();
  }
}