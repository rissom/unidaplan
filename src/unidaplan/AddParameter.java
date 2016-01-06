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
	    String pattern="";
	    JSONObject  jsonIn = null;
	    int maxdigits=0;
	    
	    try {
			jsonIn = new JSONObject(in);
			if (jsonIn.has("maxdigits") && !jsonIn.isNull("maxdigits")) {
	  	  		maxdigits=jsonIn.getInt("maxdigits");
			}
			dataType= jsonIn.getInt("datatype");
		} catch (JSONException e) {
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
			 if (jsonIn.has("pattern")){
				 pattern=jsonIn.getString("pattern");
			 } else {
				 pattern="";
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
  
			 PreparedStatement pstmt = null;

			pstmt= dBconn.conn.prepareStatement( 			
					"INSERT INTO paramdef (StringKeyName,StringKeyUnit,Datatype,maxdigits,description,pattern,lastChange,lastUser) "
					+ "VALUES (?,?,?,?,?,?,NOW(),?)");
		   	pstmt.setInt(1, stringKeyName);
		   	if (stringKeyUnit>0){
		   		pstmt.setInt(2, stringKeyUnit);
		   	}else{
		   		pstmt.setNull(2, Types.INTEGER);
		   	}
		   	pstmt.setInt(3, dataType);
		   	pstmt.setInt(4, maxdigits);
		   	pstmt.setInt(5, stringKeyDesc);
		   	if (pattern!=""){
		   		pstmt.setString(6, pattern);
		   	}else{
		   		pstmt.setInt(6, java.sql.Types.VARCHAR);
		   	}
		   	pstmt.setInt(7, userID);
			if (dataType>0 && dataType<11){
			   	pstmt.executeUpdate();
			}else{
				status="illegal datatype";
			}
			pstmt.close();
			dBconn.closeDB();

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
	Unidatoolkit.sendStandardAnswer(status, response);
	}
}	