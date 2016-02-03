package unidaplan;

//import java.sql.PreparedStatement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


//import org.json.JSONObject;

public class Authentificator {

	
 	// See if the current session is assigned to a known user
	public int GetUserID(HttpServletRequest request, HttpServletResponse response){		
		HttpSession session = request.getSession();		
//		if (DBconnection.localDB){
//			return 1;
//		}
		Integer user=-1;
		if (session.getAttribute("userID") != null) {
			user=(Integer) session.getAttribute("userID");
		} else {
			response.setStatus(401);
			user=-1;
		}
		
//		PreparedStatement pstmt = null; 	// Declare variables
//	 	DBconnection DBconn=new DBconnection(); // New connection to the database
//	 	DBconn.startDB();
//	 	
//		// get Parameter for id
//		try{
//	 		// Check if a session with this id exists in the database
//	        pstmt = DBconn.conn.prepareStatement(	
//	    	"SELECT userid FROM sessions WHERE sessionid=?");
//			pstmt.setString(1,session.getId());
//			JSONObject sessionJS=DBconn.jsonObjectFromPreparedStmt(pstmt);
//			pstmt.close();
//			user=sessionJS.getInt("userid");
//		}catch (Exception e1) {
//					System.err.print("Authentificator: SessionID not found!");
//				}
//		
//		DBconn.closeDB();
			

		return user;
	}
}
