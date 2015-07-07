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
	private static PreparedStatement pstmt;
	private static JSONArray table;


@Override
  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
 	ArrayList<String> stringkeys = new ArrayList<String>(); 
    response.setContentType("application/json");
    request.setCharacterEncoding("utf-8");
    response.setCharacterEncoding("utf-8");
    PrintWriter out = response.getWriter();
 	DBconnection DBconn=new DBconnection();
    DBconn.startDB();
	int objID=1;      // variable initialisation
	int typeid=1;
	JSONObject jsSample=new JSONObject(); // variable initialisation
	
	// get Parameter for id
	try{
		 objID=Integer.parseInt(request.getParameter("id")); }
	catch (Exception e1) {
		objID=1;
		System.err.print("Showsample: no sample ID given!");
	}
	   
    // fetch name and type of the object from the database (samplenames is a view)
    try{
		pstmt= DBconn.conn.prepareStatement( 	
				"SELECT name, typeid FROM samplenames WHERE id=?");
		pstmt.setInt(1,objID);
		jsSample= DBconn.jsonObjectFromPreparedStmt(pstmt);
		if (jsSample.length()>0) {
			typeid=jsSample.getInt("typeid");
			pstmt= DBconn.conn.prepareStatement( 	
			"SELECT string_key FROM objecttypes WHERE id=?");
			pstmt.setInt(1,typeid);
			int stringkey= DBconn.jsonObjectFromPreparedStmt(pstmt).getInt("string_key");
			stringkeys.add(Integer.toString(stringkey));
			jsSample.put("typestringkey", stringkey);
		}
	} catch (SQLException e) {
		System.err.println("Showsample: Problems with SQL query for sample name");
	} catch (JSONException e) {
		System.err.println("Showsample: JSON Problem while getting sample name");
	} catch (Exception e2) {
		System.err.println("Showsample: Strange Problem while getting sample name");
		e2.printStackTrace();
	}

    
    // Error if the sample is not found
    if (jsSample.length()==0) {
    	try {
			jsSample.put("error", "sample not found");
		    System.err.println("sample not found");
		    response.sendError(HttpServletResponse.SC_NOT_FOUND);  // 404 Error
		} catch (JSONException e) {
		    System.err.println("Showsample: Strange JSON-Error");
		}
    }
    else {
	try {
		JSONArray parameters=new JSONArray();  

		pstmt= DBconn.conn.prepareStatement( 	 //get parameters
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
			"SELECT originates_from.id, samplenames.id, samplenames.name, samplenames.typeid \n"+
			"FROM originates_from \n"+
			"JOIN samplenames ON (samplenames.id=originates_from.child) \n"+
			"WHERE originates_from.parent=? \n");
			pstmt.setInt(1,objID);
			table= DBconn.jsonArrayFromPreparedStmt(pstmt);
			if (table.length()>0) {
				for(int i=0;i<table.length();i++) {
					pstmt= DBconn.conn.prepareStatement( 	
					"SELECT string_key FROM objecttypes WHERE id=?");
					pstmt.setInt(1,((JSONObject)table.get(i)).getInt("typeid"));
					int stringkey= DBconn.jsonObjectFromPreparedStmt(pstmt).getInt("string_key");
					((JSONObject)table.get(i)).put("typestringkey", stringkey);
					stringkeys.add(Integer.toString(stringkey));
				} 
				jsSample.put("children",table); 
			}
	    } catch (SQLException e) {
    		System.err.println("Showsample: Problems with SQL query for child samples");
		} catch (JSONException e2) {
			System.err.println("Showsample: JSON Problem while getting child samples");
		} catch (Exception e3) {
			System.err.println("Showsample: Strange Problem while getting child samples");
    	}
    	
    	
		// find all parent objects
		try{    
		    pstmt=  DBconn.conn.prepareStatement( 	
			"SELECT originates_from.id, samplenames.id, samplenames.name, samplenames.typeid \n" +
			"FROM originates_from \n" +
			"JOIN samplenames ON (samplenames.id=originates_from.parent) \n" +
			"WHERE originates_from.child=? \n");
			pstmt.setInt(1,objID);
			table= DBconn.jsonArrayFromPreparedStmt(pstmt);
			if (table.length()>0) {
				for(int i=0;i<table.length();i++) {
					pstmt= DBconn.conn.prepareStatement( 	
					"SELECT string_key FROM objecttypes WHERE id=?");
					pstmt.setInt(1,((JSONObject)table.get(i)).getInt("typeid"));
					int stringkey= DBconn.jsonObjectFromPreparedStmt(pstmt).getInt("string_key");
					stringkeys.add(Integer.toString(stringkey));
					((JSONObject)table.get(i)).put("typestringkey", stringkey);
				}
			jsSample.put("ancestors",table);	
			}
	    } catch (SQLException e) {
    		System.err.println("Showsample: Problems with SQL query for parent samples");
		} catch (JSONException e2) {
			System.err.println("Showsample: JSON Problem while getting parent samples");
		} catch (Exception e3) {
			System.err.println("Showsample: Strange Problem while getting parent samples");
    	}
		
		
		// find the previous sample
		try{
		    pstmt=  DBconn.conn.prepareStatement( 	
    		"SELECT  samplenames.id, samplenames.name, samplenames.typeid \n"
			+"FROM samplenames \n"
			+"WHERE ((samplenames.name < (SELECT samplenames.name FROM samplenames WHERE samplenames.id=?)) \n"
			+"AND samplenames.typeid=(SELECT samplenames.typeid FROM samplenames WHERE samplenames.id=?)) \n"
			+"ORDER BY samplenames.name DESC \n"
			+"LIMIT 1");
			pstmt.setInt(1,objID);
			pstmt.setInt(2,objID);
			table= DBconn.jsonArrayFromPreparedStmt(pstmt);
			if (table.length()>0) {
				jsSample.put("previous",table.get(0)); }
	    } catch (SQLException e) {
    		System.err.println("Showsample: Problems with SQL query for previous sample");
		} catch (JSONException e2) {
			System.err.println("Showsample: JSON Problem while getting previous sample");
		} catch (Exception e3) {
			System.err.println("Showsample: Strange Problem while getting previous sample");
    	}
		
		
		// find next sample	
		try{
		    pstmt=  DBconn.conn.prepareStatement( 	
    		"SELECT  samplenames.id, samplenames.name, samplenames.typeid \n"
			+"FROM samplenames \n"
			+"WHERE ((samplenames.name > (SELECT samplenames.name FROM samplenames WHERE samplenames.id=?)) \n"
			+"AND samplenames.typeid=(SELECT samplenames.typeid FROM samplenames WHERE samplenames.id=?)) \n"
			+"ORDER BY samplenames.name \n"	
    		+"LIMIT 1 \n");
			pstmt.setInt(1,objID);
			pstmt.setInt(2,objID); 
			table= DBconn.jsonArrayFromPreparedStmt(pstmt);
			if (table.length()>0) {
				jsSample.put("next",table.get(0)); }	
		} catch (SQLException e) {
    		System.err.println("Showsample: Problems with SQL query for next sample");
    	} catch (JSONException e) {
			System.err.println("Showsample: JSON Problem while getting next sample");
    	} catch (Exception e2) {
			System.err.println("Showsample: Strange Problem while getting next sample");
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
	        jsSample.put("strings", theStrings);
		} catch (SQLException e) {
    		System.err.println("Showsample: Problems with SQL query for Stringkeys");
    	} catch (JSONException e) {
			System.err.println("Showsample: JSON Problem while getting Stringkeys");
    	} catch (Exception e2) {
			System.err.println("Showsample: Strange Problem while getting Stringkeys");
    	}
    }
	out.println(jsSample.toString());
	DBconn.closeDB();
  	}
}