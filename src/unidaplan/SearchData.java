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

	public class SearchData extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doGet(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
		JSONArray parameter = null;
		JSONArray output = null;
		userID=userID+1;
		userID=userID-1;
		String status="ok";
		PreparedStatement pStmt;
		ArrayList<String> stringkeys = new ArrayList<String>(); 
		JSONObject search = null;
	    response.setContentType("application/json");
	    request.setCharacterEncoding("utf-8");
	    response.setCharacterEncoding("utf-8");
	    PrintWriter out = response.getWriter();
	 	DBconnection dBconn=new DBconnection();
	    JSONObject answer = new JSONObject();
	    int id=-1;
	    
	  	try {
	   		 id=Integer.parseInt(request.getParameter("id")); 
	    } catch (Exception e1) {
	   		System.err.println("no search ID given!");
			response.setStatus(404);
	   	}
	    try {  
		    dBconn.startDB();
	    	// get basic search data (id,name,owner,operation)
			pStmt= dBconn.conn.prepareStatement( 	
			    "SELECT id,name,owner,operation,type FROM searches "
			   +"WHERE id=?");
			pStmt.setInt(1, id);
			search=dBconn.jsonObjectFromPreparedStmt(pStmt);
			stringkeys.add(Integer.toString(search.getInt("name")));
			pStmt.close();
			
			// get the searchparameters according to searchtype
			String query="";
			switch (search.getInt("type")){
				case 1:   //Object scearch
						  query = "SELECT otparameter, comparison, value, "
						  		 +"ot_parameters.stringkeyname,ot_parameters.stringkeyname,paramdef.datatype "
								 +"FROM searchobject "
								 +"JOIN ot_parameters ON (ot_parameters.id=otparameter) "
								 +"JOIN paramdef ON (paramdef.id=ot_parameters.definition) "
								 +"WHERE search=?";
						  break;
				case 2:   // Process search
						  query = "SELECT pparameter, comparison, value, "
						  		 +"p_parameters.stringkeyname,p_parameters.stringkeyname,paramdef.datatype "
								 +"FROM searchprocess "
								 +"JOIN p_parameters ON (p_parameters.id=pparameter) "
								 +"JOIN paramdef ON (paramdef.id=p_parameters.definition) "
								 +"WHERE search=?";
						  break;
				default : query = "SELECT poparameter, comparison, value, "
						  		 +"po_parameters.stringkeyname,po_parameters.stringkeyname,paramdef.datatype "
								 +"FROM searchpo "
								 +"JOIN po_parameters ON (po_parameters.id=poparameter) "
								 +"JOIN paramdef ON (paramdef.id=po_parameters.definition) "
								 +"WHERE search=?";
						  break;
			}
			pStmt= dBconn.conn.prepareStatement(query);
			pStmt.setInt(1,id);
			parameter = dBconn.jsonArrayFromPreparedStmt(pStmt);
			
			// get the outputparameters according to searchtype
			switch (search.getInt("type")){
				case 1:   //Object scearch
						  query = "SELECT ot_parameters.id, ot_parameters.stringkeyname,paramdef.datatype "
								 +"FROM osearchoutput "
								 +"JOIN ot_parameters ON (ot_parameters.id=otparameter) "
								 +"JOIN paramdef ON (paramdef.id=ot_parameters.definition) "
								 +"WHERE search=?";
						  break;
				case 2:   //Process search (nicht getestet)
					  query = "SELECT p_parameters.id, p_parameters.stringkeyname,paramdef.datatype "
								 +"FROM psearchoutput "
								 +"JOIN p_parameters ON (p_parameters.id=pparameter) "
								 +"JOIN paramdef ON (paramdef.id=p_parameters.definition) "
								 +"WHERE search=?";
						  break;
				default : query = "";
						  break;
			}
			pStmt= dBconn.conn.prepareStatement(query);
			pStmt.setInt(1,id);
			System.out.println("output: "+pStmt.toString());
			output = dBconn.jsonArrayFromPreparedStmt(pStmt);
			
			
    	} catch (SQLException e) {
    		System.err.println("Search: Problems with SQL query for search");
    		response.setStatus(404);
			status="SQL Problem while getting experiment";
    	} catch (JSONException e) {
			System.err.println("Search: JSON Problem while getting experiment");
    		response.setStatus(404);
			status="JSON Problem while getting experiment";
    	} catch (Exception e2) {
			System.err.println("Search: Strange Problem while getting experiment");
			status="Problem while getting experiment";
    		response.setStatus(404);
    	} 
	    
	   try {
		   answer.put("search", search);
		   answer.put("strings", dBconn.getStrings(stringkeys));
		   answer.put("parameter",parameter);
		   answer.put("output",parameter);
		   out.println(answer.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    
	    		
	    
		dBconn.closeDB();
	}}	