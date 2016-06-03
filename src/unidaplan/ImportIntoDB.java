package unidaplan;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;


public class ImportIntoDB extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private int timeZone = 0;
	DBconnection dBconn;
	int userID;
	
		void saveValueSample(String value,int parameterid, int sampleid){
		    try {
			    // look up the datatype in database
		    	dBconn.startDB();
			    PreparedStatement pStmt = null;
			    int type=-1;
				pStmt= dBconn.conn.prepareStatement( 			
						 "SELECT paramdef.datatype FROM Ot_parameters otp "
						+"JOIN paramdef ON otp.definition=paramdef.id "
						+"WHERE otp.id=?");
			   	pStmt.setInt(1, parameterid);
			   	type=dBconn.getSingleIntValue(pStmt);
//			   	System.out.println(pStmt.toString());
				pStmt.close();
				JSONObject data = new JSONObject();
		
				// differentiate according to type
				if (value.length()>0) {

					switch (type) {
			        case 1: data.put("value", Integer.valueOf(value));
					   		break;

			        case 2: data.put("value", Double.valueOf(value));
			   				break;
		        			
			        case 3: if (value.contains("±")){
			        			data.put("value", Double.valueOf(value.split("±")[0]));
				        		data.put("error", Double.valueOf(value.split("±")[1]));
			        		} else {
			        			data.put("value", Double.valueOf(value.split("±")[0]));
				        		data.put("error", 0);
			        		}
					   		pStmt.setInt(5, userID);
							break;
					        
			        case 4: data.put("value", value);
			        		break;
					        
			        case 5: data.put("value", value);
						    break;
						   
			        case 6: data.put("value", value);
				   			break;
					   
			        case 7: // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
						    LocalDate date = LocalDate.parse(value); // , formatter);
//						    System.out.println(date); // 2010-01-02
			        		data.put("date", date);
				   			break;
					   
			        case 8: Boolean bit = false;
					        if (value.equals("1")  || value.contains("t") || 
					        		value.contains("y")  || value.contains("j")) {
					        	bit = true;
					        }
			        		data.put("value", bit);
			   				break;
					       
					   
			        case 9: // DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
						    LocalDate date2 = LocalDate.parse(value); // , formatter);
	//					    System.out.println(date); // 2010-01-02
			        		data.put("date", date2);
				   			break;
					  
			        case 10: data.put("value", value);
				   			 break;
					   
			        case 11: data.put("value", value);
				   			break;
					   
					}
					pStmt= dBconn.conn.prepareStatement( 			// Integer values
							"INSERT INTO sampledata (objectid,ot_parameter_id,data,lastUser) VALUES (?,?,?,?)");
					pStmt.setInt(1, sampleid);
					pStmt.setInt(2, parameterid);
		   		  	pStmt.setObject(3, data, java.sql.Types.OTHER);
		   		  	pStmt.setInt(4, userID);
					pStmt.executeUpdate();
					pStmt.close();
					dBconn.closeDB();
				}
				
				
		} catch (SQLException e) {
			System.err.println("SaveSampleParameter: More Problems with SQL query");
			e.printStackTrace();
		} catch (JSONException e){
			System.err.println("SaveSampleParameter: More Problems creating JSON");
		} catch (Exception e) {
			System.err.println("SaveSampleParameter: More Strange Problems");
			e.printStackTrace();
		}
			
		}
		
		
		void saveValueProcess(String value,int id, int processid){
		    try {
			    // look up the datatype in Database	   			 
			    PreparedStatement pStmt = null;
			    int type=-1;
				pStmt= dBconn.conn.prepareStatement( 			
						 "SELECT paramdef.datatype FROM p_parameters pp "
						+"JOIN paramdef ON pp.definition=paramdef.id "
						+"WHERE pp.id=?");
			   	pStmt.setInt(1, id);
			   	type=dBconn.getSingleIntValue(pStmt);
//			   	System.out.println(pStmt.toString());
				pStmt.close();				
		
				// differentiate according to type
				if (value.length()>0) {

					switch (type) {
			        case 1: pStmt= dBconn.conn.prepareStatement( 			// Integer values
					   					 "INSERT INTO p_integer_data VALUES(DEFAULT,?,?,?,NOW(),?)");
					   		pStmt.setInt(2, id);
					   		pStmt.setInt(3, Integer.valueOf(value));
					   		pStmt.setInt(4, userID);
					   		break;
					        
			        case 2: pStmt= dBconn.conn.prepareStatement( 			// Double values
			   					 		"INSERT INTO p_float_data VALUES(DEFAULT,?,?,?,NOW(),?)");
					   		pStmt.setInt(2, id);				        
						    pStmt.setDouble(3, Double.valueOf(value));
					   		pStmt.setInt(4, userID);
			   				break;
		        			
			        case 3: pStmt= dBconn.conn.prepareStatement( 			// Measurement data
								 		"INSERT INTO p_measurement_data (ObjectID, Ot_Parameter_ID, Value, "
								 		+"Error, lastChange, lastUser) "
								 		+"VALUES(?,?,?,?,NOW(),?)");
			        		pStmt.setInt(2, id);
			        		if (value.contains("±")){
								pStmt.setDouble(3, Double.valueOf(value.split("±")[0]));
								pStmt.setDouble(4, Double.valueOf(value.split("±")[1]));
			        		} else {
			        			pStmt.setDouble(3, Double.valueOf(value));
			        			pStmt.setDouble(4, 0);
			        		}
					   		pStmt.setInt(5, userID);
							break;
					        
			        case 4: pStmt= dBconn.conn.prepareStatement( 			// String data	
						 		"INSERT INTO p_string_data VALUES(DEFAULT,?,?,?,NOW(),?)");
						    pStmt.setInt(2, id);
						    pStmt.setString(3, value);
					   		pStmt.setInt(4, userID);
							break;
					        
			        case 5: pStmt= dBconn.conn.prepareStatement( 			
			        		 	"INSERT INTO p_string_data VALUES(DEFAULT,?,?,?,NOW(),?)");
						        pStmt.setInt(2, id);
						        pStmt.setString(3, value);
					   			pStmt.setInt(4, userID);
					   			break;
						   
			        case 6: pStmt= dBconn.conn.prepareStatement( 			
		        		 	"INSERT INTO p_string_data VALUES(DEFAULT,?,?,?,NOW(),?)");
					        pStmt.setInt(2, id);
					        pStmt.setString(3, value);
				   			pStmt.setInt(4, userID);
				   			break;
					   
			        case 7: pStmt= dBconn.conn.prepareStatement( 			// date
		        		 	"INSERT INTO p_timestamp_data VALUES(DEFAULT,?,?,?,NOW(),?)");
					        pStmt.setInt(2, id);
					        pStmt.setString(3, value);
				   			pStmt.setInt(4, userID);
				   			break;
					   
			        case 8: pStmt= dBconn.conn.prepareStatement( 			// checkbox
		        		 	"INSERT INTO p_integer_data VALUES(DEFAULT,?,?,?,NOW(),?)");
					        pStmt.setInt(2, id);
					        Integer iValue=0;
					        if (value.equals("1")  || value.equalsIgnoreCase("true") || 
					        		value.equalsIgnoreCase("yes")  || value.equalsIgnoreCase("ja")) {
					        	iValue=1;
					        }
					        pStmt.setInt(2, id);
					        pStmt.setInt(3, iValue);
				   			pStmt.setInt(4, userID);
				   			break;
					   
			        case 9: pStmt= dBconn.conn.prepareStatement( 			// date
		        		 	"INSERT INTO p_timestamp_data VALUES(DEFAULT,?,?,?,NOW(),?)");
					        pStmt.setInt(2, id);
					        pStmt.setString(3, value);
				   			pStmt.setInt(4, userID);
				   			break;
					   
			        case 10: pStmt= dBconn.conn.prepareStatement( 			
		        		 	"INSERT INTO p_string_data VALUES(DEFAULT,?,?,?,NOW(),?)");
					        pStmt.setInt(2, id);
					        pStmt.setString(3, value);
				   			pStmt.setInt(4, userID);
				   			break;
					   
			        case 11: pStmt= dBconn.conn.prepareStatement( 			
		        		 	"INSERT INTO p_string_data VALUES(DEFAULT,?,?,?,NOW(),?)");
					        pStmt.setInt(2, id);
					        pStmt.setString(3, value);
				   			pStmt.setInt(4, userID);
				   			break;
					}
				}
		        pStmt.setInt(1, processid);
				pStmt.executeUpdate();
				pStmt.close();
		} catch (SQLException e) {
			System.err.println("SaveProcessParameter: More Problems with SQL query");
			e.printStackTrace();
		} catch (JSONException e){
			System.err.println("SaveProcessParameter: More Problems creating JSON");
		} catch (Exception e) {
			System.err.println("SaveProcessParameter: More Strange Problems");
			e.printStackTrace();
		}
			
		}
		
		
		
		public int createSample(int sampletypeID){	
			// returns the sample id
			int sampleID=-1;
		    PreparedStatement pStmt = null;
		    try{
			pStmt= dBconn.conn.prepareStatement( 			
					 "INSERT INTO samples (objecttypesid,creator, creationdate, lastchange, lastuser) "
					+"VALUES (?, ?, NOW(), NOW(),?) RETURNING id");
		   	pStmt.setInt(1, sampletypeID);
		   	pStmt.setInt(2, userID);
		   	pStmt.setInt(3, userID);
//		   	System.out.println(pStmt.toString());
			sampleID= dBconn.getSingleIntValue(pStmt);
//			System.out.println("sampleID:"+sampleID);
		   	pStmt.close();
		    } catch (SQLException e) {
				System.err.println("SaveSampleParameter: More Problems with SQL query");
			} catch (JSONException e){
				System.err.println("SaveSampleParameter: More Problems creating JSON");
			} catch (Exception e) {
				System.err.println("SaveSampleParameter: More Strange Problems");
				e.printStackTrace();
			}
		   	return sampleID;
		}
	
		
		
		public int createProcess(int processTypeID){
			
			// some variables
			int pnumber;
			int processID=-1;
		    PreparedStatement pStmt = null;
		    
		    try{
		    	
				// ask for current of processnumber
			    pStmt= dBconn.conn.prepareStatement( 			
							"SELECT max(p_number) FROM pnumbers WHERE processtype=? GROUP BY processtype");
				pStmt.setInt(1, processTypeID);
			    pnumber=dBconn.getSingleIntValue(pStmt);
			   	pStmt.close();

			    
			    
				// create entry in processes and returns the sample id
				pStmt= dBconn.conn.prepareStatement( 			
						 "INSERT INTO processes (processtypesid, lastchange, lastuser) "
						+"VALUES (?, NOW(),?) RETURNING id");
			   	pStmt.setInt(1, processTypeID);
			   	pStmt.setInt(2, userID);
//			   	System.out.println(pStmt.toString());
				processID= dBconn.getSingleIntValue(pStmt);
//				System.out.println("processID:"+processID);
			   	pStmt.close();
			   	
			   	
			   	
			   	// write processnumber 
			   	pStmt= dBconn.conn.prepareStatement("INSERT INTO p_integer_data VALUES(default, ?,"
		    			+ " (SELECT id FROM P_Parameters WHERE definition=8 AND processtypeid=?), ?, NOW(),?)");
			   	pStmt.setInt(1, processID);
			   	pStmt.setInt(2, processTypeID);
			   	pStmt.setInt(3, pnumber+1);
			   	pStmt.setInt(4, userID);
			   	pStmt.executeUpdate();
			   	pStmt.close();
				
		    	
				// set status to "ok" 
			   	pStmt= dBconn.conn.prepareStatement("INSERT INTO p_integer_data VALUES(default, ?,"
		    			+ " (SELECT id FROM P_Parameters WHERE definition=1 AND processtypeid=?), ?, NOW(),?)");
			   	pStmt.setInt(1, processID);
			   	pStmt.setInt(2, processTypeID);
			   	pStmt.setInt(3, 1);
			   	pStmt.setInt(4, userID);
			   	pStmt.executeUpdate();
			   	pStmt.close();
				
				
				// find date parameter
			   	pStmt= dBconn.conn.prepareStatement("SELECT id FROM p_parameters pp "
		    			+ "WHERE (pp.definition=10 AND pp.processtypeid=?)");
			   	pStmt.setInt(1, processTypeID);
			   	JSONObject dateIDObj=dBconn.jsonObjectFromPreparedStmt(pStmt);
			   	int dateID = dateIDObj.getInt("id");
			   	pStmt.close();			   	
			   	
			   	// set date parameter to now
			   	pStmt= dBconn.conn.prepareStatement("INSERT INTO p_timestamp_data VALUES(default,?,?,NOW(),?,NOW(),?)");
			   	pStmt.setInt(1, processID);
			   	pStmt.setInt(2, dateID);
			   	pStmt.setInt(3, timeZone);
			   	pStmt.setInt(4, userID);
			   	pStmt.executeUpdate();
			   	pStmt.close();
			   	
		    } catch (SQLException e) {
				System.err.println("ImportIntoDB->createProcess: More Problems with SQL query");
			} catch (JSONException e){
				System.err.println("ImportIntoDB->createProcess: More Problems creating JSON");
			} catch (Exception e) {
				System.err.println("ImportIntoDB->createProcess: More Strange Problems");
				e.printStackTrace();
			}
		   	return processID;
		}
		
		
		
	// Main function
		public void doPost(HttpServletRequest request, HttpServletResponse response) 
		      throws ServletException, IOException {	
			JSONArray parameter=null;
			response.setContentType("application/json");
			request.setCharacterEncoding("utf-8");
			response.setCharacterEncoding("utf-8");

			Authentificator authentificator = new Authentificator();
			userID=authentificator.GetUserID(request,response);
			
			PrintWriter out = response.getWriter();
	    	int sampleID=0;
	    	int processID=0;
		   	int paramgrpID=0;
	 		String type;
			JSONObject  jsonIn = null;
			try {
				jsonIn = new JSONObject(request.getReader().readLine());
			} catch (JSONException e) {
				System.err.println("ImportIntoDB: Input is not valid JSON");
			}
		    
		    try {
		    	parameter = jsonIn.getJSONArray("parameters");
		    	String file = "uploads/"+jsonIn.getString("file");
		    	type = jsonIn.getString("type");
		    	int sampletype=-1;
		    	int processtype=-1;
//			   	int timeZone=jsonIn.getInt("timezone");
			    PreparedStatement pStmt = null;
			    int stringKeyName=0;

				dBconn=new DBconnection();
			    dBconn.startDB();	
			    
			    if (Unidatoolkit.userHasAdminRights(stringKeyName, dBconn)){
			    	
			    	if (jsonIn.has("sampletype")){
			    		sampletype=jsonIn.getInt("sampletype");
			    	}
			    	if (jsonIn.has("processtype")){
			    		processtype=jsonIn.getInt("processtype");
			    	}
		    			    	
		    
			    
				    // create new datatype and parameters if necessary.
				    if (sampletype==0 || processtype==0){
					    int stringKeyDesc=0; 
					    int position=0;
				    
					    // generate strings for the name
						if (jsonIn.has("name")){
							 JSONObject name=jsonIn.getJSONObject("name");
							 String [] names = JSONObject.getNames(name);
							 stringKeyName=dBconn.createNewStringKey(name.getString(names[0]));
							 for (int i=0; i<names.length; i++){
								 dBconn.addString(stringKeyName,names[i],name.getString(names[i]));
							 }
						 }else{
							 System.out.println("no name exists");
						 }
						 if (jsonIn.has("description")){
							 JSONObject description=jsonIn.getJSONObject("description");
							 String [] descriptions = JSONObject.getNames(description);
							 stringKeyDesc=dBconn.createNewStringKey(description.getString(descriptions[0]));
							 for (int i=0; i<descriptions.length; i++){
								 dBconn.addString(stringKeyDesc,descriptions[i],description.getString(descriptions[i]));
							 }	 
						 }
						 if (jsonIn.has("position")){
							 position=jsonIn.getInt("position");
						 }
		
						 
				    
				    
					    if (sampletype==0){
						    int otgroup=1;
							if (jsonIn.has("otgroup")){
								otgroup=jsonIn.getInt("otgroup");
							}  
							pStmt= dBconn.conn.prepareStatement( 			
								"INSERT INTO objecttypes values(default,?,?,?,?,NOW(),?) RETURNING id");
							pStmt.setInt(1, position);
							pStmt.setInt(2, otgroup);
						   	pStmt.setInt(3, stringKeyName);
						   	pStmt.setNull(4,java.sql.Types.INTEGER);
						   	pStmt.setInt(5, userID);
						   	sampletype=dBconn.getSingleIntValue(pStmt);
						   					   	
						   	int paramGrpName=38;
		
						   	// create a new parametergroup
						   	if (jsonIn.has("paramgrp")){
						   		JSONObject paramgrp=jsonIn.getJSONObject("paramgrp");
								String [] paramgrps = JSONObject.getNames(paramgrp);
	//							System.out.println("paramgrp0:"+paramgrps[0]);
	//							System.out.println("key:"+paramgrp.getString(paramgrps[0]));
								paramGrpName=dBconn.createNewStringKey(paramgrp.getString(paramgrps[0]));
								for (int i=0; i<paramgrps.length; i++){
									 dBconn.addString(paramGrpName,paramgrps[i],paramgrp.getString(paramgrps[i]));
								}
							}else{
								 System.out.println("no paramgrp exists");
							}
						   	
							pStmt= dBconn.conn.prepareStatement( 			
								"INSERT INTO ot_parametergrps (ot_id,stringkey,pos,lastchange,lastuser) "
								+"VALUES(?,?,?,NOW(),?) RETURNING id");
							pStmt.setInt(1, sampletype);
							pStmt.setInt(2, paramGrpName);
						   	pStmt.setInt(3, 1);
						   	pStmt.setInt(4,userID);
						   	paramgrpID=dBconn.getSingleIntValue(pStmt);
						   	pStmt.close();
					    }
					    
					    
					    
					    if (processtype==0){
				    		int ptgroup=1;
					    	if (jsonIn.has("ptgroup")){
								ptgroup=jsonIn.getInt("ptgroup");
							}  
							pStmt= dBconn.conn.prepareStatement( 			
								"INSERT INTO processtypes (position, ptgroup, name, description, lastchange, lastuser ) "
								+"VALUES (?,?,?,?,NOW(),?) RETURNING id");
							pStmt.setInt(1, position);
							pStmt.setInt(2, ptgroup);
						   	pStmt.setInt(3, stringKeyName);
						   	pStmt.setNull(4,java.sql.Types.INTEGER);
						   	pStmt.setInt(5, userID);
						   	sampletype=dBconn.getSingleIntValue(pStmt);
	
						   	int paramGrpName=38;
		
						   	// create a new parametergroup
						   	if (jsonIn.has("paramgrp")){
						   		JSONObject paramgrp=jsonIn.getJSONObject("paramgrp");
								String [] paramgrps = JSONObject.getNames(paramgrp);
	//							System.out.println("paramgrp0:"+paramgrps[0]);
	//							System.out.println("key:"+paramgrp.getString(paramgrps[0]));
								paramGrpName=dBconn.createNewStringKey(paramgrp.getString(paramgrps[0]));
								for (int i=0; i<paramgrps.length; i++){
									 dBconn.addString(paramGrpName,paramgrps[i],paramgrp.getString(paramgrps[i]));
								}
							}else{
								 System.out.println("no paramgrp exists");
							}
						   	
							pStmt= dBconn.conn.prepareStatement( 			
								"INSERT INTO p_parametergrps (ot_id,stringkey,pos,lastchange,lastuser) "
								+"VALUES(?,?,?,NOW(),?) RETURNING id");
							pStmt.setInt(1, sampletype);
							pStmt.setInt(2, paramGrpName);
						   	pStmt.setInt(3, 1);
						   	pStmt.setInt(4,userID);
						   	paramgrpID=dBconn.getSingleIntValue(pStmt);
						   	pStmt.close();
					    }
				    }
				    
				    
				    
				    
				    // read the file
				    BufferedReader CSVFile = new BufferedReader(new FileReader(file));
		
			        String dataRow = CSVFile.readLine(); // Read the first line of data (headings)
		            String[] headingsArray = dataRow.split(";");
		            Boolean idField=true;
		            int headingKey=0;
		            for (int i=0; i<headingsArray.length;i++) { // go through headings array.
	            		int par=0;
	            		
	            		if (sampletype!=0 && processtype!=0){
	            			par=parameter.getInt(i);
	            		}
		            	
		            	if (sampletype==0){
			            	if (parameter.getInt(i)>0){
			            		headingKey=dBconn.createNewStringKey(headingsArray[i]);
			            		dBconn.addString(headingKey,"de", headingsArray[i]);
			            	   	pStmt= dBconn.conn.prepareStatement( 			
										"INSERT INTO ot_parameters (objecttypesid,parametergroup,compulsory,id_field,"
							   			+"hidden,pos,definition,stringkeyname,lastchange,lastuser) "
										+"VALUES(?,?,?,?,?,?,?,?,NOW(),?) RETURNING id");
			            		pStmt.setInt(1,sampletype); // objecttypesid
			            		pStmt.setInt(2,paramgrpID); // parametergroup
			            		pStmt.setBoolean(3,false);  // compulsory
			            		pStmt.setBoolean(4,idField);  // id-field	 
			            		pStmt.setBoolean(5,false); // hidden
			            		pStmt.setInt(6,i+1); // position
			            		pStmt.setInt(7,parameter.getInt(i)); // definition
			            		pStmt.setInt(8,headingKey); // stringkeyname
			            		pStmt.setInt(9,userID); // lastuser
	//		            		System.out.println(pStmt.toString());
			            		idField=false;
	//		            		saveValueSample(headingsArray[i],parameter.getInt(i), sampleID);
			            		par=dBconn.getSingleIntValue(pStmt);
			            	}
		            	}
		            	
		            	if (processtype==0){
			            	if (parameter.getInt(i)>0){
			            		headingKey=dBconn.createNewStringKey(headingsArray[i]);
			            		dBconn.addString(headingKey,"de", headingsArray[i]);
			            	   	pStmt= dBconn.conn.prepareStatement( 			
										"INSERT INTO p_parameters (processtypeid,parametergroup,compulsory,id_field,"
							   			+"hidden,pos,definition,stringkeyname,lastchange,lastuser) "
										+"VALUES(?,?,?,?,?,?,?,?,NOW(),?) RETURNING id");
			            		pStmt.setInt(1,processtype); // processtypeid
			            		pStmt.setInt(2,paramgrpID); // parametergroup
			            		pStmt.setBoolean(3,false);  // compulsory
			            		pStmt.setBoolean(4,idField);  // id-field	 
			            		pStmt.setBoolean(5,false); // hidden
			            		pStmt.setInt(6,i+1); // position
			            		pStmt.setInt(7,parameter.getInt(i)); // definition
			            		pStmt.setInt(8,headingKey); // stringkeyname
			            		pStmt.setInt(9,userID); // lastuser
	//		            		System.out.println(pStmt.toString());
			            		idField=false;
	//		            		saveValueProcess(headingsArray[i],parameter.getInt(i), sampleID);
			            		par=dBconn.getSingleIntValue(pStmt);
			            	}
		            	}      
		            	parameter.put(i,par);
	//	            	System.out.println("par: "+par);
		            }
			        
		            dataRow = CSVFile.readLine(); // Read next line of data.
	
			        // The while checks to see if the data is null. If it is, we've hit
			        //  the end of the file. If not, process the data.  
		        	if (type.equals("sample")){
	
				        while (dataRow != null){
				        	sampleID=createSample(sampletype);	        		
				            String[] dataArray = dataRow.split(";");
				            for (int i=0; i<dataArray.length;i++) {
				            	if (parameter.getInt(i)>0){
				            		saveValueSample(dataArray[i],parameter.getInt(i), sampleID);
				            	} 
						    }
				            out.println(); // Print the data line.
				            dataRow = CSVFile.readLine(); // Read next line of data.
					    }
			        
		        	}
		        	if (type.equals("process")){
		        		
		        		while (dataRow != null){
				        	processID=createProcess(processtype);	        		
				            String[] dataArray = dataRow.split(";");
				            for (int i=0; i<dataArray.length;i++) {
				            	if (parameter.getInt(i)>0){
				            		saveValueProcess(dataArray[i],parameter.getInt(i), processID);
				            	} 
						    }
				            out.println(); // Print the data line.
				            dataRow = CSVFile.readLine(); // Read next line of data.
					    }
		        		
		        	}
			        
			        
	
			        // Close the file once all data has been read.
			        CSVFile.close();
			        
			        
			        
			        // Delete the file
			        Path path=FileSystems.getDefault().getPath(file);
			        try {
			            Files.delete(path);
			        } catch (NoSuchFileException x) {
			            System.err.format("%s: no such" + " file or directory%n", path);
			        } catch (DirectoryNotEmptyException x) {
			            System.err.format("%s not empty%n", path);
			        } catch (IOException x) {
			            // File permission problems are caught here.
			            System.err.println(x);
			        }
			    } else {
			    	response.setStatus(401);
			    }
				dBconn.closeDB();

		        // End the printout with a blank line.
		    } catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    } //doGet()
	}

