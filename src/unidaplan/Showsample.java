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
	int typeid=1;
	JSONObject jsSample=new JSONObject(); // variable initialisation
	
	// get Parameter for id
	try{
		 objID=Integer.parseInt(request.getParameter("id")); }
	catch (Exception e1) {
		objID=1;
		System.err.print("Showsample: no object ID given!");
	}
	   
    // fetch name and type of the object from the database (objectnames is a view)
    try{
		pstmt= DBconn.conn.prepareStatement( 	
				"SELECT name, type, typeid FROM objectnames WHERE id=?");
		pstmt.setInt(1,objID);
		jsSample= DBconn.jsonObjectFromPreparedStmt(pstmt);
		typeid=jsSample.getInt("typeid");
	} catch (SQLException e) {
		System.err.println("Showsample: Problems with SQL query for sample name");
		e.printStackTrace();	
	} catch (JSONException e) {
		System.err.println("Showsample: JSON Problem while getting sample name");
		e.printStackTrace();
	} catch (Exception e2) {
		System.err.println("Showsample: Strange Problem while getting sample name");
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
	 	ArrayList<String> stringkeys = new ArrayList<String>(); 

		pstmt= DBconn.conn.prepareStatement( 	
		    "SELECT ot_parameters.id, parametergroup, compulsory, ot_parameters.pos,"
		   +"stringkeyname,  pid, value, ot_parametergrps.id AS pgrpid, "
		   +"ot_parametergrps.stringkey as parametergrp_key, st.description " 
		   +"FROM ot_parameters " 
		   +"JOIN ot_parametergrps ON (ot_parameters.Parametergroup=ot_parametergrps.ID) " 
		   +"LEFT JOIN acc_sample_parameters a ON "
		   +"(a.objectid=? AND a.id=ot_parameters.id AND hidden=FALSE) "
		   +"JOIN String_key_table st ON st.id=stringkeyname "
		   +"WHERE (ot_parameters.objecttypesID=? AND ot_parameters.id_field=False) "
		   +"ORDER BY pos"); 
		pstmt.setInt(1,objID);
		pstmt.setInt(2,typeid);
		parameters = DBconn.jsonArrayFromPreparedStmt(pstmt);
		if (parameters.length()>0) {
			jsSample.put("parameters",parameters);
	      	  for (int i=0; i<parameters.length();i++) {
	      		  JSONObject tempObj=(JSONObject) parameters.get(i);
	      		  stringkeys.add(Integer.toString(tempObj.getInt("stringkeyname")));
	      	  }
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
	         jsSample.put("strings", theStrings);
		}
	} catch (SQLException e) {
		System.err.println("Showsample: Problems with SQL query for sample parameters");
		e.printStackTrace();
	} catch (JSONException e){
		System.err.println("Showsample: Problems creating JSON for sample parameters");
		e.printStackTrace();
	} catch (Exception e) {
		System.err.println("Showsample: Strange Problems with the sample parameters");
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
    		System.err.println("Showsample: Problems with SQL query for sample children");
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
    		System.err.println("Showsample: Problems with SQL query for parent samples");
    		e.printStackTrace();	
		} catch (JSONException e2) {
			System.err.println("Showsample: JSON Problem while getting parent samples");
			e2.printStackTrace();
		} catch (Exception e3) {
			System.err.println("Showsample: Strange Problem while getting parent samples");
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
    		System.err.println("Showsample: Problems with SQL query for previous sample");
    		e.printStackTrace();	
		} catch (JSONException e2) {
			System.err.println("Showsample: JSON Problem while getting previous sample");
			e2.printStackTrace();
		} catch (Exception e3) {
			System.err.println("Showsample: Strange Problem while getting previous sample");
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
    		System.err.println("Showsample: Problems with SQL query for sample children");
    		e.printStackTrace();	
    	} catch (JSONException e) {
			System.err.println("Showsample: JSON Problem while getting next sample");
			e.printStackTrace();
    	} catch (Exception e2) {
			System.err.println("Showsample: Strange Problem while getting next sample");
    		e2.printStackTrace();
    	}

    	
    }
	out.println(jsSample.toString());
	DBconn.closeDB();
  	}
}