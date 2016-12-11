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

	public class UpdateUserRights extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doPut(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {
		
		Authentificator authentificator = new Authentificator();
		int userID = authentificator.GetUserID(request,response);
	    request.setCharacterEncoding("utf-8");
	    String status = "ok";
	    String in = request.getReader().readLine();
	    JSONObject jsonIn = null;
	    
	    try {
			  jsonIn = new JSONObject(in);
		} catch (JSONException e) {
			System.err.println("UpdateUserRights: Input is not valid JSON");
			status="error";
		}
		response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    PreparedStatement pStmt;

	    int ID = 0;
	    try {
			ID = jsonIn.getInt("userid");	
		} catch (JSONException e) {
			System.err.println("UpdateUserRights: Error parsing ID-Field");
			status="error parsing ID-Field";
			response.setStatus(404);
		}

	 	DBconnection dBconn=new DBconnection();
	    try{   
		    dBconn.startDB();
			if (dBconn.isAdmin(userID)){
				
				 // for experiments
			    if (jsonIn.has("updatedExRights")){
			    	JSONArray updatedExRights = jsonIn.getJSONArray("updatedExRights");
			    	for (int i=0; i<updatedExRights.length(); i++){
			    		String permission = updatedExRights.getJSONObject(i).getString("permission");
			    		int sampletypeId = updatedExRights.getJSONObject(i).getInt("id");
			    		pStmt = dBconn.conn.prepareStatement("DELETE FROM rightsexperimentuser WHERE userId=? AND experiment=?");
						pStmt.setInt(1,ID);
						pStmt.setInt(2,sampletypeId);	
				    	pStmt.executeUpdate();
					    pStmt.close();
						if (!permission.equals("l")){ // permission is 'read' or 'write'
			    			pStmt = dBconn.conn.prepareStatement("INSERT INTO rightsexperimentuser "
			    					+" (userId,experiment,permission,lastuser) VALUES (?,?,?,?)");
			    			pStmt.setInt(1,ID);
							pStmt.setInt(2,sampletypeId);
							pStmt.setString(3,permission);
							pStmt.setInt(4,userID);
					    	pStmt.executeUpdate();
						    pStmt.close();
			    		}
			    	}
			    }
	
			    // for sampletypes
			    if (jsonIn.has("updatedSTrights")){
			    	JSONArray updatedSTrights = jsonIn.getJSONArray("updatedSTrights");
			    	for (int i = 0; i < updatedSTrights.length(); i++){
			    		String permission = updatedSTrights.getJSONObject(i).getString("permission");
			    		int sampletypeId = updatedSTrights.getJSONObject(i).getInt("id");
			    		pStmt = dBconn.conn.prepareStatement("DELETE FROM rightssampletypeuser WHERE userId = ? AND sampletype = ?");
						pStmt.setInt(1,ID);
						pStmt.setInt(2,sampletypeId);	
				    	pStmt.executeUpdate();
					    pStmt.close();
						if (!permission.equals("l")){ // permission is 'read' or 'write'
			    			pStmt = dBconn.conn.prepareStatement(
			    						"INSERT INTO rightssampletypeuser "
			    					  + " (userId,sampletype,permission,lastuser) "
			    					  + "VALUES (?,?,?,?)");
			    			pStmt.setInt(1,ID);
							pStmt.setInt(2,sampletypeId);
							pStmt.setString(3,permission);
							pStmt.setInt(4,userID);
					    	pStmt.executeUpdate();
						    pStmt.close();
			    		}
			    	}
			    }
			    
			    // for processtypes
			    if (jsonIn.has("updatedPTrights")){
			    	JSONArray updatedSTrights = jsonIn.getJSONArray("updatedPTrights");
			    	for (int i=0; i<updatedSTrights.length(); i++){
			    		String permission = updatedSTrights.getJSONObject(i).getString("permission");
			    		int processtypeId = updatedSTrights.getJSONObject(i).getInt("id");
			    		pStmt = dBconn.conn.prepareStatement("DELETE FROM rightsprocesstypeuser WHERE userId=? AND processtype=?");
						pStmt.setInt(1,ID);
						pStmt.setInt(2,processtypeId);	
				    	pStmt.executeUpdate();
					    pStmt.close();
						if (!permission.equals("l")){ // permission is 'read' or 'write'
			    			pStmt = dBconn.conn.prepareStatement("INSERT INTO rightsprocesstypeuser "
			    					+" (userid,processtype,permission,lastuser) VALUES (?,?,?,?)");
			    			pStmt.setInt(1,ID);
							pStmt.setInt(2,processtypeId);
							pStmt.setString(3,permission);
							pStmt.setInt(4,userID);
					    	pStmt.executeUpdate();
						    pStmt.close();
			    		}
			    	}
			    }
			    
			    
			} else {
				response.setStatus(401);
				status="insufficient rights";
			}
		   
	    } catch (SQLException e) {
			System.err.println("UpdateUserRights: Problems with SQL query");
			status="error";
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("UpdateUserRights: Strange Problems");
			status="error";
			e.printStackTrace();
		}
	    
	try{
		dBconn.closeDB();
	    // tell client that everything is fine
	    PrintWriter out = response.getWriter();
	    JSONObject myResponse= new JSONObject();
	    myResponse.put("status", status);
		out.println(myResponse.toString());
	} catch (JSONException e){
		System.err.println("UpdateUserRights: More Problems creating JSON");
		status="error";
	} catch (Exception e) {
		System.err.println("UpdateUserRights: More Strange Problems");
		status="error";
	}
		
	}
}	