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
	 	DBconnection dBconn=new DBconnection();
		PreparedStatement pStmt;
		int fileID;


	 	try{
	 		dBconn.startDB();
			int userID = authentificator.GetUserID(request,response);
			fileID = Integer.parseInt(request.getParameter("fileid"));

			// Check priveleges 
			// not implemented yet...
			pStmt= dBconn.conn.prepareStatement( 	
					"SELECT sample,process FROM files WHERE files.id=?");
	 		pStmt.setInt(1, fileID);
	 		JSONObject dings = dBconn.jsonObjectFromPreparedStmt(pStmt);
	 		
			
			// Get filename and type
	 		pStmt= dBconn.conn.prepareStatement( 	
					"DELETE FROM files WHERE files.id=?");
	 		pStmt.setInt(1, fileID);
	 		pStmt.executeUpdate();
	 		
	        Path path = FileSystems.getDefault().getPath("/mnt/data-store", String.format("%010d", fileID));
	        Files.delete(path);

	 	}catch (SQLException e) {
			System.err.println("Showsample: Problems with SQL query for sample name");
		} catch (Exception e2) {
			System.err.println("Showsample: Strange Problem while getting sample name");
			e2.printStackTrace();
		}
    }

}