package unidaplan;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

public class Unidatoolkit {
   
	
    public static void sendStandardAnswer(String status, HttpServletResponse response) {
	    response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e) {
			System.err.println("Error sending standard answer");
			e.printStackTrace();
		} 
    	JSONObject answer=new JSONObject();
        try {
			answer.put("status", status);
		} catch (JSONException e) {
			e.printStackTrace();
		}
        out.println(answer.toString());
    }
    
    
    
    public static void notFound(HttpServletResponse response) {
	    PrintWriter out = null;
		try {
			out = response.getWriter();
		} catch (IOException e) {
			System.err.println("Error sending standard answer");
			e.printStackTrace();
		} 
  	    response.setStatus(404);
		out.println("{\"error\":\"not found\"}");
    }

    
    
    public static int copyStringEntry(int stringKey, int userID, DBconnection dBconn){
    	// function for copying stringentries in the database
		PreparedStatement pStmt = null; 
		int newKey=0;
		
    	try {		
	    	// copy entry in stringkeytable
			pStmt = dBconn.conn.prepareStatement(	
		       	"INSERT INTO string_key_table (description) "
				+"SELECT description FROM string_key_table WHERE id=? "
		        +"RETURNING ID");
			pStmt.setInt(1,stringKey);
			newKey = dBconn.getSingleIntValue(pStmt);
			pStmt.close();
			
			// copy all entries
			pStmt = dBconn.conn.prepareStatement(	
			    	"INSERT INTO stringtable (string_key,language,value,lastuser) "
					+"SELECT ?, language, value, ? FROM stringtable WHERE string_key=? ");
			pStmt.setInt(1,newKey);
			pStmt.setInt(2,userID);
			pStmt.setInt(3,stringKey);
			pStmt.executeUpdate();
			pStmt.close();
			
		} catch (SQLException e) {
			System.err.println("SQL-Error copying Strings");
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("Misc error copying Strings");
			e.printStackTrace();
		}
    	return newKey;
    }


}
