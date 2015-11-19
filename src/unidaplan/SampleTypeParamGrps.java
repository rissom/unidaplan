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

public class SampleTypeParamGrps extends HttpServlet {
	private static final long serialVersionUID = 1L;
   
	
    public SampleTypeParamGrps() {
        super();
    }

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) 
		throws ServletException, IOException {
		  
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
		userID=userID+1;
		userID=userID-1;
		int sampleTypeID=0;
		request.setCharacterEncoding("utf-8");
	    response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    PrintWriter out = response.getWriter(); 
	  	  	try  {
	  	  		sampleTypeID=Integer.parseInt(request.getParameter("sampletypeid")); 
	  	  	}
	  	  	catch (Exception e1) {
	  	  		System.err.print("SampleTypeParameters: no sampleTypeID given!");
	  	  		e1.printStackTrace();
	  	  	}
		PreparedStatement pStmt = null; 	// Declare variables
	    JSONObject sampleType= null;
	    JSONArray parameterGrps= null;		
	    JSONArray sampleTypeGrps= null;
	 	DBconnection dBconn=new DBconnection(); // New connection to the database
	 	ArrayList<String> stringkeys = new ArrayList<String>(); 
		 	
	    try{
		 	dBconn.startDB();
 			pStmt = dBconn.conn.prepareStatement(	
			   "SELECT objecttypes.id, objecttypes.position, otgrp, objecttypes.string_key, "
 			  +"description, objecttypes.lastuser "
			  +"FROM objecttypes "
			  +"WHERE objecttypes.id=?");
	 		pStmt.setInt(1, sampleTypeID);
 			sampleType=dBconn.jsonObjectFromPreparedStmt(pStmt); 
 			// get ResultSet from the database using the query
 			pStmt.close();
           	stringkeys.add(Integer.toString(sampleType.getInt("string_key")));
           	stringkeys.add(Integer.toString(sampleType.getInt("description")));
           	
           	pStmt = dBconn.conn.prepareStatement(
     		  	   "SELECT id, position, name FROM objecttypesgrp");
			sampleTypeGrps=dBconn.jsonArrayFromPreparedStmt(pStmt); 
			// get ResultSet from the database using the query
			if (sampleTypeGrps.length()>0) {
           		for (int j=0; j<sampleTypeGrps.length();j++) {
           			stringkeys.add(Integer.toString(sampleTypeGrps.getJSONObject(j).getInt("name")));
           		}
           	}		
           	
   			pStmt = dBconn.conn.prepareStatement(
		  	   "SELECT id,pos,stringkey FROM ot_parametergrps "
			  +"WHERE (ot_parametergrps.ot_id=?) ");
   			pStmt.setInt(1, sampleTypeID);
   			parameterGrps=dBconn.jsonArrayFromPreparedStmt(pStmt); 
   			// get ResultSet from the database using the query

           	if (parameterGrps.length()>0) {
           		for (int j=0; j<parameterGrps.length();j++) {
           			stringkeys.add(Integer.toString(parameterGrps.getJSONObject(j).getInt("stringkey")));
           		}
           	}
     
	        JSONObject answer=new JSONObject();
	        answer=sampleType;
	        answer.put("sampletypegrps", sampleTypeGrps);
	        answer.put("parametergrps",parameterGrps);
	        answer.put("strings", dBconn.getStrings(stringkeys));
	        out.println(answer.toString());
	    } catch (SQLException eS) {
			System.err.println("SampleTypeParameters: SQL Error");
		} catch (Exception e) {
			System.err.println("SampleTypeParameters: Some Error, probably JSON");
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
