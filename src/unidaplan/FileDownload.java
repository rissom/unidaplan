package unidaplan;
import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

public class FileDownload extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
		
		Authentificator authentificator = new Authentificator();
	 	DBconnection dBconn = new DBconnection();
		PreparedStatement pStmt;
	    String privilege = "n";

	 	try{
	 		dBconn.startDB();
			int userID = authentificator.GetUserID(request,response);
	        int fileID = Integer.parseInt(URLDecoder.decode(request.getPathInfo().substring(1), "UTF-8"));

			
			// Check priveleges 
			if (dBconn.isAdmin(userID)){
		    	privilege = "w";
		    } else {
				pStmt = dBconn.conn.prepareStatement( 	
						"SELECT sample,process FROM files WHERE files.id = ?");
		 		pStmt.setInt(1, fileID);
		 		JSONObject fileData = dBconn.jsonObjectFromPreparedStmt(pStmt);
		 		String query = "";
		 		String type = "";
		 		if (fileData.has("sample")){
		 			type = "sample";
		 			query = "SELECT getSampleRights( vuserid := ?, vsample := ? )";
		 		} else { // file is attached to a process
		 			type = "process";
		 			query = "SELECT getProcessRights( vuserid := ?, vprocess := ? )";
		 		}
		 		pStmt = dBconn.conn.prepareStatement(query);
	 		    pStmt.setInt(1,userID);
	 			pStmt.setInt(2,fileData.getInt(type));
	 		    privilege = dBconn.getSingleStringValue(pStmt);
		 		pStmt.close();
		    }
			
			
			// Get filename
			if ( privilege.equals("w") || privilege.equals("r") ){
		 		pStmt = dBconn.conn.prepareStatement( 	
							  "SELECT filename "
							+ "FROM files " 
							+ "WHERE files.id = ?");
		 		pStmt.setInt(1, fileID);
		 		JSONObject fileInfo = dBconn.jsonObjectFromPreparedStmt(pStmt);
	
		        File file = new File("/mnt/data-store", String.format("%010d", fileID));
		        response.setHeader("Content-Type", getServletContext().getMimeType("." + fileInfo.getString("filename")));
		        response.setHeader("Content-Length", String.valueOf(file.length()));
		        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileInfo.getString("filename") + "\"");
		        Files.copy(file.toPath(), response.getOutputStream());
			} else {
				response.setStatus(401);
				Unidatoolkit.sendStandardAnswer("insufficient rights", response);
			}
	 	}catch (SQLException e) {
			System.err.println("Showsample: Problems with SQL query for sample name");
		} catch (JSONException e) {
			System.err.println("Showsample: JSON Problem while getting sample name");
		} catch (Exception e2) {
			System.err.println("Showsample: Strange Problem while getting sample name");
			e2.printStackTrace();
		}
    }
}