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
	    JSONObject  jsonIn = null;
	    int maxdigits=0;
	    
	    try {
			jsonIn = new JSONObject(in);
	  	  	maxdigits=jsonIn.getInt("maxdigits");
			dataType= jsonIn.getInt("datatype");
		} catch (JSONException e) {
			System.err.println("AddParameter: Input is not valid JSON");
		}
	    
	 	DBconnection dBconn=new DBconnection();
	    dBconn.startDB();	   
	    
	    int stringKeyName=0;
	    int stringKeyUnit=0;
	    int stringKeyDesc=0;

	    
	    // generate strings for the name and the unit
	    try {	
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
				 stringKeyUnit=dBconn.createNewStringKey(unit.getString(units[0]));
				 for (int i=0; i<units.length; i++){
					 dBconn.addString(stringKeyUnit,units[i],unit.getString(units[i]));
				 }
			 }
			 if (jsonIn.has("description")){
				 JSONObject description=jsonIn.getJSONObject("description");
				 String [] descriptions = JSONObject.getNames(description);
				 stringKeyDesc=dBconn.createNewStringKey(description.getString(descriptions[0]));
				 for (int i=0; i<descriptions.length; i++){
					 dBconn.addString(stringKeyDesc,descriptions[i],description.getString(descriptions[i]));
				 }	 
			 }
		} catch (JSONException e) {
			System.err.println("AddParameter: Error creating Strings");
			response.setStatus(404);
		} catch (Exception e) {
			System.err.println("AddParameter: Error creating Strings");
		}	
  
	    PreparedStatement pstmt = null;
		try {	
			pstmt= dBconn.conn.prepareStatement( 			
					"INSERT INTO paramdef values(default,?,?,?,?,?, NOW(),?)");
		   	pstmt.setInt(1, stringKeyName);
		   	pstmt.setInt(2, stringKeyUnit);
		   	pstmt.setInt(3, dataType);
		   	pstmt.setInt(4, maxdigits);
		   	pstmt.setInt(5, stringKeyDesc);
		   	pstmt.setInt(6, userID);
			if (dataType>0 && dataType<9){
			   	pstmt.executeUpdate();
			}else{
				status="illegal datatype";
			}

		} catch (SQLException e) {
			System.err.println("AddParameter: Problems with SQL query");
		} catch (Exception e) {
			System.err.println("AddParameter: Strange Problems");
		}	
				
    // tell client that everything is fine
	Unidatoolkit.sendStandardAnswer(status, response);
	}
}	