package unidaplan;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

	public class ProcessByNumber extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
	  public void doGet(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {
		PreparedStatement pStmt;
	    response.setContentType("application/json");
	    request.setCharacterEncoding("utf-8");
	    response.setCharacterEncoding("utf-8");
	    int processNumber=-1;
	    int processType=-1;
	    String privilege="n";
	    
		Authentificator authentificator = new Authentificator();
		int userID=authentificator.GetUserID(request,response);
	    
		// get Parameter for number and processType
		try{
			 processNumber=Integer.parseInt(request.getParameter("number"));
			 processType=Integer.parseInt(request.getParameter("type"));
		}
		catch (Exception e1) {
			System.err.print("ProcessByNumber: no processnumber given!");
		}
	    PrintWriter out = response.getWriter();
	 	DBconnection dBconn=new DBconnection();
	    try {  
		    dBconn.startDB();
			pStmt= dBconn.conn.prepareStatement( 	
					"SELECT p_integer_data.processid FROM p_integer_data "
					+"JOIN p_parameters pp ON (p_integer_data.p_parameter_id=pp.id AND id_field=true) "
					+"WHERE processtypeid=? AND p_integer_data.value=?"); 
			pStmt.setInt(1, processType);
			pStmt.setInt(2, processNumber);
			int processID=dBconn.getSingleIntValue(pStmt);
			pStmt.close();
			
			// check privileges
	        pStmt= dBconn.conn.prepareStatement( 	
					"SELECT getProcessRights(vuserid:=?,vprocess:=?)");
			pStmt.setInt(1,userID);
			pStmt.setInt(2,processID);
			privilege = dBconn.getSingleStringValue(pStmt);
			pStmt.close();
			
			// output
			if (privilege.equals("r")||privilege.equals("w")){
				JSONObject answer = new JSONObject();
				answer.put("processid", processID);
				out.println(answer.toString());
			}
			
    	} catch (SQLException e) {
    		System.err.println("ProcessByNumber: Problems with SQL query");
    	} catch (JSONException e) {
			System.err.println("ProcessByNumber: JSON Problem while getting Stringkeys");
    	} catch (Exception e2) {
			System.err.println("ProcessByNumber: Strange Problem while getting Stringkeys");
    	}
		dBconn.closeDB();
	}}	