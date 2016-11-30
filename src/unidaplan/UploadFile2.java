package unidaplan;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;


@MultipartConfig

	public class UploadFile2 extends HttpServlet {
		private static final long serialVersionUID = 1L;

	@Override
		public void doPost(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException {		
		Authentificator authentificator = new Authentificator();
		int userID = authentificator.GetUserID(request,response);
		DBconnection dBconn=null;
		int id = -1;
//		String fileType = "dat";

	    

	    request.setCharacterEncoding("utf-8");
		response.setContentType("application/json");
	    response.setCharacterEncoding("utf-8");
	    
	    
	 
	    // path to upload directory
	    final String path = "/mnt/data-store";
	    
	    
	    // check if an upload folder exists, if not: create one.
	    File theDir = new File(path);

		// if the directory does not exist, create it
		if (!theDir.exists()) {
			System.out.println("creating upload directory");
	
		    try{
		         theDir.mkdir();
		    } 
		    catch(SecurityException se){
		    	System.out.println("Error creating Directory!");
		        se.printStackTrace();
		    }        
		 }
	    
		// get id of process or object
		String sampleString = request.getParameter("sample");
		String processString = request.getParameter("process");
		String experimentString = request.getParameter("experiment");

		// get the filename
		final Part filePart = request.getPart("file");
		String fileName = null;
	    for (String content : filePart.getHeader("content-disposition").split(";")) {
	        if (content.trim().startsWith("filename")) {
	            fileName = content.substring(content.indexOf('=') + 1).trim().replace("\"", "");
//	            int i = fileName.lastIndexOf('.');
//	            if (i > 0) {
//	                fileType = fileName.substring(i+1);
//	            }

	        }
	    }

	    OutputStream out = null;
	    InputStream filecontent = null;
	    final PrintWriter writer = response.getWriter();
		PreparedStatement pStmt;

	    try {
	    	dBconn = new DBconnection();
			dBconn.startDB();
			if (!Unidatoolkit.isMemberOfGroup(userID, 1, dBconn)){
				response.setStatus(401);
				throw new Exception("not allowed");
			}
	    	
			
			
			pStmt = dBconn.conn.prepareStatement(
					"INSERT INTO files (filename, sample, process, experiment, lastuser) VALUES (?,?,?,?,?) RETURNING ID");
			pStmt.setString(1, fileName);
			if (sampleString != null){
				pStmt.setInt(2, Integer.parseInt(sampleString));
				pStmt.setNull(3, java.sql.Types.INTEGER);
				pStmt.setNull(4, java.sql.Types.INTEGER);
			} else {
				if (experimentString != null){
						pStmt.setInt(4, Integer.parseInt(experimentString));
						pStmt.setNull(3, java.sql.Types.INTEGER);
						pStmt.setNull(2, java.sql.Types.INTEGER);
				} else {
					pStmt.setNull(2, java.sql.Types.INTEGER);
					pStmt.setInt(3, Integer.parseInt(processString));
					pStmt.setInt(4, Integer.parseInt(processString));
				}
			}
			pStmt.setInt(5, userID);
			id = dBconn.getSingleIntValue(pStmt);
			pStmt.close();
			dBconn.closeDB();
			
			
			// Save file to disk with an 10-digit number as filename
	    	File dings = new File(path + File.separator + String.format("%010d", id));
	        out = new FileOutputStream(dings);
	        filecontent = filePart.getInputStream();

	        int read = 0;
	        final byte[] bytes = new byte[1024];

	        while ((read = filecontent.read(bytes)) != -1) {
	            out.write(bytes, 0, read);
	        }
	        writer.println("New file " + fileName + " created at " + path);
	    } catch (FileNotFoundException fne) {
	        writer.println("You either did not specify a file to upload or are "
	                + "trying to upload a file to a protected or nonexistent "
	                + "location.");
	        writer.println("<br/> ERROR: " + fne.getMessage());
	    } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
	        if (out != null) {
	            out.close();
	        }
	        if (filecontent != null) {
	            filecontent.close();
	        }
	        if (writer != null) {
	            writer.close();
	        }
	    }
	}
	

}