package unidaplan;

import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class Authentificator {
 	static ArrayList<String> userSessions = new ArrayList<String>();
 	static ArrayList<Integer> userIDs = new ArrayList<Integer>(); 

	
 	// See if the current session is assgned to a known user
	public int GetUserID(HttpServletRequest request, HttpServletResponse response){		
		HttpSession session = request.getSession();
		int user=-1;
		for (int i=0; i<userSessions.size(); i++)
		 	{
				if (session.getId()==userSessions.get(i)){ 
					user=userIDs.get(i);
				}
		 	}	
		if (user==-1){
			response.setStatus(401);
		}
		return user;
	}
	
	
	
	// Ask user to enter his password 
	public int Authentificate(HttpServletRequest request, HttpServletResponse response){
		HttpSession session = request.getSession();
		int user=-1;
		for (int i=0; i<userSessions.size(); i++)
		 	{
				if (session.getId()==userSessions.get(i)){ 
					user=userIDs.get(i);
				}
		 	}	
		return user;
	}
}
