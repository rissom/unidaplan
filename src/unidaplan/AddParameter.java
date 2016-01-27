package unidaplan;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

	public class AddParameter extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doPost(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {		
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
		String status="ok";
		int dataType=0;
	    request.setCharacterEncoding("utf-8");
	    String in = request.getReader().readLine();
	    int id=0;
	    JSONObject  jsonIn = null;
	    
	    try {
			jsonIn = new JSONObject(in);
			String dataTypeString= jsonIn.getString("datatype");
			for (int i=0; i<Unidatoolkit.Datatypes.length; i++){
				if (Unidatoolkit.Datatypes[i].equalsIgnoreCase(dataTypeString)){
					dataType=i;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
			System.err.println("AddParameter: Input is not valid JSON");
		}
	    

	    
	    int stringKeyName=0;
	    int stringKeyUnit=0;
	    int stringKeyDesc=0;

	    
	    // generate strings for the name and the unit
	    try {	
		 	DBconnection dBconn=new DBconnection();
		    dBconn.startDB();	   
		    
			 if (jsonIn.has("name")){
				 JSONObject name=jsonIn.getJSONObject("name");
				 String [] names = JSONObject.getNames(name);
				 stringKeyName=dBconn.createNewStringKey(name.getString(names[0]));
				 for (int i=0; i<names.length; i++){
					 dBconn.addString(stringKeyName,names[i],name.getString(names[i]));
				 }
			 }else
			 {
				 System.err.println("no name exists");
			 }
			 if (jsonIn.has("unit")){
				 JSONObject unit=jsonIn.getJSONObject("unit");
				 String [] units = JSONObject.getNames(unit);
				 if (units!=null) {
					 if (units.length>0){
						 stringKeyUnit=dBconn.createNewStringKey(unit.getString(units[0]));
						 for (int i=0; i<units.length; i++){
							 dBconn.addString(stringKeyUnit,units[i],unit.getString(units[i]));
						 }
					 }
				 }
			 }

			 if (jsonIn.has("description")){
				 JSONObject description=jsonIn.getJSONObject("description");
				 if (description.length()>0){
					 String [] descriptions = JSONObject.getNames(description);
					 stringKeyDesc=dBconn.createNewStringKey(description.getString(descriptions[0]));
					 for (int i=0; i<descriptions.length; i++){
						 dBconn.addString(stringKeyDesc,descriptions[i],description.getString(descriptions[i]));
					 }	 
				 } else {
					 stringKeyDesc=38;
				 }
			 }
  
			 PreparedStatement pStmt = null;

			pStmt= dBconn.conn.prepareStatement( 			
					"INSERT INTO paramdef (StringKeyName,StringKeyUnit,Datatype,format,regex,min,max,description,lastChange,lastUser) "
					+ "VALUES (?,?,?,?,?,?,?,?,NOW(),?) RETURNING id");
		   	pStmt.setInt(1, stringKeyName);
		   	if (stringKeyUnit>0){
		   		pStmt.setInt(2, stringKeyUnit);
		   	}else{
		   		pStmt.setNull(2, Types.INTEGER);
		   	}
		   	pStmt.setInt(3, dataType);
		   	if (jsonIn.has("format")){
		   		pStmt.setString(4, jsonIn.getString("format"));
		   	}else{
		   		pStmt.setNull(4, Types.VARCHAR);
		   	}
		   	if (jsonIn.has("regex")){
		   		pStmt.setString(5, jsonIn.getString("regex"));
		   	}else{
		   		pStmt.setNull(5, Types.VARCHAR);
		   	}
		   	if (jsonIn.has("min")){
		   		pStmt.setDouble(6, jsonIn.getDouble("min"));
		   	}else{
		   		pStmt.setNull(6, Types.DOUBLE);
		   	}
		   	if (jsonIn.has("max")){
		   		pStmt.setDouble(7, jsonIn.getDouble("max"));
		   	}else{
		   		pStmt.setNull(7, Types.DOUBLE);
		   	}
		   	if (jsonIn.has("description")){
		   		pStmt.setInt(8, stringKeyDesc);	
		   	}else{
		   		pStmt.setNull(8,Types.INTEGER);
		   	}
		   	pStmt.setInt(9, userID);
			if (dataType>0 && dataType<11){
			   	id =  dBconn.getSingleIntValue(pStmt);
			}else{
				status="illegal datatype";
			}
			pStmt.close();
			dBconn.closeDB();
			
			Unidatoolkit.returnID(id, status, response);

		} catch (SQLException e) {
			System.err.println("AddParameter: Problems with SQL query");
			response.setStatus(404);
			e.printStackTrace();
		} catch (JSONException e) {
			System.err.println("AddParameter: Error JSON-Error");
			e.printStackTrace();
		} catch (Exception e) {
			System.err.println("AddParameter: Error");
			e.printStackTrace();
		}					
    // tell client that everything is fine
	   
	;
	}
}	