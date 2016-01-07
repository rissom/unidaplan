package unidaplan;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;


public class ImportIntoDB extends HttpServlet {
	private static final long serialVersionUID = 1L;
	DBconnection dBconn;
	int userID;
	
		void saveValue(String value,int id, int sampleid){
		    try {
			    // look up the datatype in Database	   			 
			    PreparedStatement pStmt = null;
			    int type=-1;
				pStmt= dBconn.conn.prepareStatement( 			
						 "SELECT paramdef.datatype FROM Ot_parameters otp "
						+"JOIN paramdef ON otp.definition=paramdef.id "
						+"WHERE otp.id=?");
			   	pStmt.setInt(1, id);
			   	type=dBconn.getSingleIntValue(pStmt);
			   	System.out.println(pStmt.toString());
				pStmt.close();				
		
				// differentiate according to type
				if (value.length()>0) {

					switch (type) {
			        case 1: {   pStmt= dBconn.conn.prepareStatement( 			// Integer values
					   					 "INSERT INTO o_integer_data VALUES(DEFAULT,?,?,?,NOW(),?) RETURNING ID");
					   			pStmt.setInt(2, id);
					   			pStmt.setInt(3, Integer.valueOf(value));
					   			pStmt.setInt(4, userID);
					   			break;
					        }
			        case 2: {   pStmt= dBconn.conn.prepareStatement( 			// Double values
			   					 		"INSERT INTO o_float_data VALUES(DEFAULT,?,?,?,NOW(),?) RETURNING ID");
					   			pStmt.setInt(2, id);				        
						        pStmt.setDouble(3, Double.valueOf(value));
					   			pStmt.setInt(4, userID);
			   					break;
		        			}
			        case 3: {   pStmt= dBconn.conn.prepareStatement( 			// Measurement data
								 		"INSERT INTO o_measurement_data (ObjectID, Ot_Parameter_ID, Value, "
								 		+"Error, lastChange, lastUser) "
								 		+"VALUES(?,?,?,?,NOW(),?) RETURNING ID");
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
					        }
			        case 4:  { pStmt= dBconn.conn.prepareStatement( 			// String data	
						 		"INSERT INTO o_string_data VALUES(DEFAULT,?,?,?,NOW(),?) RETURNING ID");
						        pStmt.setInt(2, id);
						        pStmt.setString(3, value);
					   			pStmt.setInt(4, userID);
							   break;
					        }
			        case 5: {  pStmt= dBconn.conn.prepareStatement( 			
			        		 	"INSERT INTO o_string_data VALUES(DEFAULT,?,?,?,NOW(),?) RETURNING ID");
						        pStmt.setInt(2, id);
						        pStmt.setString(3, value);
					   			pStmt.setInt(4, userID);
						   }
					}
				}
		        pStmt.setInt(1, sampleid);
			   	System.out.println(pStmt.toString());
				pStmt.executeUpdate();
				pStmt.close();
				dBconn.closeDB();
		} catch (SQLException e) {
			System.err.println("SaveSampleParameter: More Problems with SQL query");
		} catch (JSONException e){
			System.err.println("SaveSampleParameter: More Problems creating JSON");
		} catch (Exception e) {
			System.err.println("SaveSampleParameter: More Strange Problems");
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
		   	System.out.println(pStmt.toString());
			sampleID= dBconn.getSingleIntValue(pStmt);
			System.out.println("sampleID:"+sampleID);
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
			String status = "ok";
		   	int paramgrpID=0;
	        
			String in = request.getReader().readLine();
			System.out.println("input:");
			System.out.println(in);
			JSONObject  jsonIn = null;
			try {
				jsonIn = new JSONObject(in);
			} catch (JSONException e) {
				System.err.println("UpdateSampleTypeData: Input is not valid JSON");
			}
		    
		    try {
		    	parameter = jsonIn.getJSONArray("parameters");
		    	String file = "/Users/thorse/Desktop/"+jsonIn.getString("file");
		    	int sampletype=jsonIn.getInt("sampletype");
		    	int sampleID=0;
		    	
		    	dBconn=new DBconnection();
			    dBconn.startDB();	
			    PreparedStatement pStmt = null;
			    int stringKeyName=0;

			    // create new datatype and parameters if necessary.
			    if (sampletype==0){
				    int stringKeyDesc=0; 
				    int position=0;
				    int otgroup=1;


			    
				    // generate strings for the name
					if (jsonIn.has("name")){
						 JSONObject name=jsonIn.getJSONObject("name");
						 String [] names = JSONObject.getNames(name);
						 System.out.println("names0:"+names[0]);
						 System.out.println("key:"+name.getString(names[0]));
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
					 if (jsonIn.has("otgroup")){
						 otgroup=jsonIn.getInt("ptgroup");
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
						System.out.println("paramgrp0:"+paramgrps[0]);
						System.out.println("key:"+paramgrp.getString(paramgrps[0]));
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
			    
			    // read the file
			    BufferedReader CSVFile = new BufferedReader(new FileReader(file));
	
		        String dataRow = CSVFile.readLine(); // Read the first line of data (headings)
	            String[] headingsArray = dataRow.split(";");
	            Boolean idField=true;
	            int headingKey=0;
	            for (int i=0; i<headingsArray.length;i++) {
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
	            		System.out.println(pStmt.toString());
	            		idField=false;
	            		saveValue(headingsArray[i],parameter.getInt(i), sampleID);
	            		int par=dBconn.getSingleIntValue(pStmt);
	            		parameter.put(i,par);
	            	}
			    }
		        
		        
	            dataRow = CSVFile.readLine(); // Read next line of data.

		        // The while checks to see if the data is null. If it is, we've hit
		        //  the end of the file. If not, process the data.  
		        while (dataRow != null){
				    sampleID=createSample(sampletype);
		            String[] dataArray = dataRow.split(";");
		            for (int i=0; i<dataArray.length;i++) {
		            	if (parameter.getInt(i)>0){
		            		saveValue(dataArray[i],parameter.getInt(i), sampleID);
		            	} 
				    }
		            out.println(); // Print the data line.
		            dataRow = CSVFile.readLine(); // Read next line of data.
			    }

		        // Close the file once all data has been read.
		        CSVFile.close();
	
		        // End the printout with a blank line.
		    } catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

	    } //doGet()
	}

