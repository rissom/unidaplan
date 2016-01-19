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

import java.io.*;


public class ImportCSV extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	  	public void doPost(HttpServletRequest request, HttpServletResponse response) 
		      throws ServletException, IOException {		
		  	response.setContentType("application/json");
		    request.setCharacterEncoding("utf-8");
		    response.setCharacterEncoding("utf-8");
		    PrintWriter out = response.getWriter();
		    Authentificator authentificator = new Authentificator();
			int userID=authentificator.GetUserID(request,response);
		
			String status = "ok";
		   	int paramgrpID=0;
	        
			String in = request.getReader().readLine();
			JSONObject  jsonIn = null;
			String file="";
			try {
				jsonIn = new JSONObject(in);
		    	file = "uploads/"+jsonIn.getString("file");
			} catch (JSONException e) {
				System.err.println("UpdateSampleTypeData: Input is not valid JSON");
			}
		    


		    	
		    BufferedReader CSVFile = new BufferedReader(new FileReader(file));

	        String dataRow = CSVFile.readLine(); // Read the first line of data.
	        // The while checks to see if the data is null. If it is, we've hit
	        //  the end of the file. If not, process the data.
	        String sep1="";
	        out.print("{\"data\":[");
	        
	        while (dataRow != null){
	            String[] dataArray = dataRow.split(";");
	            out.print(sep1+"[\"");
	            String seperator="";
	            for (String item:dataArray) {
			        out.print(seperator+item);
			        seperator="\",\"";
			    }
	            out.print("\"]");
	            sep1=",";
	            out.println(); // Print the data line.
	            dataRow = CSVFile.readLine(); // Read next line of data.
	        }

	        // Close the file once all data has been read.
	        CSVFile.close();

	        // End the printout with a blank line.
	        out.println("]}");

	    } //doGet()
	}

