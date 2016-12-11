package unidaplan;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;


public class DeleteFile extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
		
		Authentificator authentificator = new Authentificator();
	 	DBconnection dBconn = new DBconnection();
		PreparedStatement pStmt;
		int fileID;


	 	try{
	 		dBconn.startDB();
			int userID = authentificator.GetUserID(request,response);
			fileID = Integer.parseInt(request.getParameter("fileid"));
		    String privilege = "n";

			// Check priveleges 
		    if (Unidatoolkit.isMemberOfGroup(userID, 1, dBconn)){
		    	privilege = "w";
		    } else {
				pStmt = dBconn.conn.prepareStatement( 	
						"SELECT sample,process FROM files WHERE files.id=?");
		 		pStmt.setInt(1, fileID);
		 		JSONObject fileData = dBconn.jsonObjectFromPreparedStmt(pStmt);
		 		String query = "";
		 		String type = "";
		 		if (fileData.has("sample")){
		 			type  = "sample";
		 			query = "SELECT getSampleRights(vuserid := ?, vsample := ?)";
		 		} else { // file is attached to a process
		 			type = "process";
		 			query="SELECT getProcessRights(vuserid := ?, vprocess := ?)";
		 		}
		 		pStmt = dBconn.conn.prepareStatement(query);
	 		    pStmt.setInt(1, userID);
	 			pStmt.setInt(2, fileData.getInt(type));
	 		    privilege = dBconn.getSingleStringValue(pStmt);
		 		pStmt.close();
		    }
			
			// Get filename and type
	 		if (privilege.equals("w")||privilege.equals("r")){
		 		pStmt = dBconn.conn.prepareStatement( 	
						"DELETE FROM files WHERE files.id = ?");
		 		pStmt.setInt(1, fileID);
		 		pStmt.executeUpdate();
		 		
		        Path path = FileSystems.getDefault().getPath("/mnt/data-store", String.format("%010d", fileID));
		        Files.delete(path);
	 		}else{
	 			response.setStatus(401);
	 		}
	 	}catch (SQLException e) {
			System.err.println("Showsample: Problems with SQL query for sample name");
		} catch (Exception e2) {
			System.err.println("Showsample: Strange Problem while getting sample name");
			e2.printStackTrace();
		}
    }

}