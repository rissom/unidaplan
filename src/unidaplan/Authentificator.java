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
		Integer user = -1;
		if (session.getAttribute("userID") != null) {
			user = (Integer) session.getAttribute("userID");
		} else {
			response.setStatus(511);
			user = -1;
		}
			

		return user;
	}
}
