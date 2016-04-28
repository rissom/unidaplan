package unidaplan;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

	public class AddProcessType extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doPost(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {		
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
		String status="ok";
	    request.setCharacterEncoding("utf-8");
	    PreparedStatement pStmt = null;
	    String in = request.getReader().readLine();
	    JSONObject  jsonIn = null;
	    
	    try {
			jsonIn = new JSONObject(in);
		} catch (JSONException e) {
			System.err.println("AddProcessType: Input is not valid JSON");
			status="input error";
		}
		response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    
 
	    
	    int stringKeyName=0;
	    int stringKeyDesc=0; 
	    int position=0;
	    int ptgroup=1;
	    int id=0;


	    
	    // generate strings for the name 
	    try {	
		 	DBconnection dBconn=new DBconnection();
		    dBconn.startDB();	  

		    int admins=1;
		    if (userID>0 && Unidatoolkit.isMemberOfGroup(userID,admins, dBconn)){
		    
		    	if (jsonIn.has("name")){
		    		JSONObject name=jsonIn.getJSONObject("name");
		    		String [] names = JSONObject.getNames(name);
		    		stringKeyName=dBconn.createNewStringKey(name.getString(names[0]));
		    		for (int i=0; i<names.length; i++){
		    			dBconn.addString(stringKeyName,names[i],name.getString(names[i]));
		    		}
		    	}else{
		    		System.out.println("no name exists");
		    	}
		    	if (jsonIn.has("description")){
		    		JSONObject description=jsonIn.getJSONObject("description");
		    		String [] descriptions = JSONObject.getNames(description);
		    		if (descriptions!=null && descriptions.length>0){
		    			stringKeyDesc=dBconn.createNewStringKey(description.getString(descriptions[0]));
		    			for (int i=0; i<descriptions.length; i++){
		    				dBconn.addString(stringKeyDesc,descriptions[i],description.getString(descriptions[i]));
		    			} 
		    		}
		    	}
				if (jsonIn.has("position")){
					position=jsonIn.getInt("position");
				}
				if (jsonIn.has("ptgroup")){
					ptgroup=jsonIn.getInt("ptgroup");
				}

  

				 pStmt= dBconn.conn.prepareStatement( 			
					"INSERT INTO processtypes (position,ptgroup,name,description,lastChange,lastUser) "
					+"VALUES (?,?,?,?,NOW(),?) RETURNING id");
				 pStmt.setInt(1, position);
				 pStmt.setInt(2, ptgroup);
				 pStmt.setInt(3, stringKeyName);
			   	 if (stringKeyDesc>0){
			   		pStmt.setInt(4, stringKeyDesc);
			   	}else{
			   		pStmt.setNull(4, java.sql.Types.INTEGER);
			   	}
			   	pStmt.setInt(5, userID);
			   	id=dBconn.getSingleIntValue(pStmt);
			   	pStmt.close();
		
//			int parameterGrp=0;
//			pStmt= dBconn.conn.prepareStatement( 			
//			//define a new parametergroup for basic parameters of the process
//				"INSERT INTO p_parametergrps (processtype,stringkey,pos,lastuser) "
//				+"VALUES(?,5,1,?) "
//				+"RETURNING id");
//			pStmt.setInt(1, id);
//			pStmt.setInt(2, userID);
//		   	parameterGrp=dBconn.getSingleIntValue(pStmt);
			pStmt= dBconn.conn.prepareStatement( 			
			//define status parameter (hidden)
				"INSERT INTO p_parameters (ProcesstypeID,compulsory,ID_Field,Formula,Hidden,pos,definition,StringKeyName,lastUser )"
				+"values(?,True,False,'',True,1,1,1,?)");
			pStmt.setInt(1, id);
//			pStmt.setInt(2, parameterGrp);
			pStmt.setInt(2, userID);
		   	pStmt.executeUpdate();
		    pStmt.close();
		   	// define parameter for processnumber
			pStmt= dBconn.conn.prepareStatement( 			
					"INSERT INTO p_parameters (ProcesstypeID,compulsory,ID_Field,Formula,Hidden,pos,definition,StringKeyName,lastUser) "
					+"VALUES (?,True,True,'',False,1,8,36,?)");
			pStmt.setInt(1, id);
//			pStmt.setInt(2, parameterGrp);
			pStmt.setInt(2, userID);
		   	pStmt.executeUpdate();
		    pStmt.close();
		   	// define parameter for processtime
			pStmt= dBconn.conn.prepareStatement( 			
					"INSERT INTO p_parameters (ProcesstypeID,compulsory,ID_Field,Formula,Hidden,pos,definition,StringKeyName,lastUser) "
					+"VALUES (?,True,False,'',False,1,10,40,?)");
			pStmt.setInt(1, id);
//			pStmt.setInt(2, parameterGrp);
			pStmt.setInt(2, userID);
		   	pStmt.executeUpdate();		
		    pStmt.close();
		    } else{
		    	response.setStatus(401);
		    }
			dBconn.closeDB();
		} catch (SQLException e) {
			status="SQL error";
			System.err.println("AddProcessType: Problems with SQL query2");
			response.setStatus(404);
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("AddProcessType: Strange Problems");
			e.printStackTrace();
			response.setStatus(404);
		}	
	
		
    // tell client that everything is fine
	    Unidatoolkit.sendStandardAnswer(status, response);
	}
}	