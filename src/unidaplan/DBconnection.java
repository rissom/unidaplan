package unidaplan;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.naming.*;
import javax.sql.DataSource;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;



public class DBconnection  {
	Connection conn = null;
    static Boolean localDB = false;
    
  public void startDB() throws Exception {

	  String DATASOURCE_CONTEXT = "java:/comp/env/jdbc/postgres";
	    
	    try {
	      Context initialContext = new InitialContext();
	      DataSource datasource = (DataSource)initialContext.lookup(DATASOURCE_CONTEXT);
		  
	      if (datasource != null) {
	        conn = datasource.getConnection();
	      }
	      else {
	    	  System.err.println("Failed to lookup datasource.");
	      }
	    }
	    catch ( NamingException ex ) {
	    	System.err.println("DBconnection1: Cannot get connection: " + ex);
	    	ex.printStackTrace();
	    }
	    catch(SQLException ex){
	    	System.err.println("DBconnection: Cannot get connection: " + ex);
	    	ex.printStackTrace();
	    }
	    
	
  }
  
  
  
  public void closeDB() {
	try {
		conn.close();
	} catch (SQLException e) {
		System.err.println("Error closing database");
	}
  }
  
  
  public Boolean isAdmin(int userID) throws SQLException{
	  PreparedStatement pStmt;
	  pStmt= conn.prepareStatement( 	
				"SELECT EXISTS (SELECT 1 FROM groupmemberships WHERE groupid=1 AND userid=?)");
	  pStmt.setInt(1, userID);
	  ResultSet queryResult=null;
	  queryResult = pStmt.executeQuery();
	  queryResult.next();
	  return queryResult.getBoolean(1);
  }

  
  
  public JSONArray jsonfromquery(String query) throws Exception{
	  Statement stmt = null;
	  JSONArray result = null;
	  ResultSet queryresult=null;
	  	  try {
	          stmt = conn.createStatement();
	          if (stmt == null) {
		          System.err.println("DBconnection: statement null! " );
	      }
          queryresult = stmt.executeQuery(query);
          if (queryresult==null) {
        	  System.err.println("DBconnection: statement result null! ");
        	  result=new JSONArray();
          } else {
        	  result = table2json(queryresult);
        	  queryresult.close();
          }     
          stmt.close();
  	  } catch (SQLException e) {   // Exception for SQL database
  		  System.err.println("DBconnection: Problem with the database! Error! ");
  		  e.printStackTrace();
  	  } catch (Exception e) {
  		  System.err.println("DBconnection: Some problem with database query. Error! ");
	  }
	  return result;
  }
  
  
  
  public JSONArray jsonArrayFromCS(CallableStatement cs) throws Exception{
	  JSONArray result = null;
	  ResultSet queryResult=null;
	  	  try {
	  		queryResult = cs.executeQuery();
          if (queryResult == null) {
        	  System.err.println("DBconnection: statement result null! ");
          } else {
        	  result = new JSONArray();
        	  queryResult.next();
        	  result.put(queryResult.getObject(1));;
        	  queryResult.close();
          }     
          cs.close();
  	  } catch (SQLException e) {   // Exception for SQL database
  		  System.err.println("DBconnection: Problem with the database! Error! ");
  		  e.printStackTrace();
  	  } catch (Exception e) {
  		  System.err.println("DBconnection: Some problem with database query. Error! ");
	  }
	  return result;
  }
  
  
  
  public JSONArray jsonArrayFromPreparedStmt(PreparedStatement pStmt) throws Exception{
	  JSONArray result = null;
	  ResultSet queryResult=null;
	  	  try {
		          if (pStmt==null) {
			          System.err.println("DBconnection: prepared statement null! " );
		          }
		          queryResult = pStmt.executeQuery();
          if (queryResult==null) {
        	  System.err.println("DBconnection: statement result null! ");
        	  result = new JSONArray();
          } else {
        	  result = table2json(queryResult);
        	  queryResult.close();
          }     
          pStmt.close();
  	  } catch (SQLException e) {   // Exception for SQL database
  		  System.err.println("DBconnection: No result, or problem with the database"); // please remove this!
  		  e.printStackTrace();
  		  System.err.println(pStmt.toString());  // please remove this!
  	  } catch (Exception e) {
  		  System.err.print("DBconnection: Some problem with database query. Error! ");
	  		  e.printStackTrace();
	  }
	  return result;
  }
  
  
  
  public JSONArray ArrayFromPreparedStmt(PreparedStatement pStmt) throws Exception{
	  JSONArray result = null;
	  ResultSet queryResult=null;
	  	  try {
	          if (pStmt==null) {
		          System.err.println("DBconnection: prepared statement null! " );
	          }
	          queryResult = pStmt.executeQuery();
          if (queryResult==null) {
        	  System.err.println("DBconnection: statement result null! ");
        	  result=new JSONArray();
          } else {
        	  result = new JSONArray();
              while (queryResult.next()) {
                  result.put(queryResult.getObject(1));
              }
        	  queryResult.close();
          }     
          pStmt.close();
  	  } catch (SQLException e) {   // Exception for SQL database
  		  System.err.println("DBconnection: No result, or problem with the database"); // please remove this!
  		  e.printStackTrace();
  		  System.err.println(pStmt.toString());  // please remove this!
  	  } catch (Exception e) {
  		  System.err.print("DBconnection: Some problem with database query. Error! ");
	  	  e.printStackTrace();
	  }
	  return result;
  }
  
  
  
  public void deleteString(int stringKey, String language) throws Exception{
	PreparedStatement pStmt = conn.prepareStatement(
			"DELETE FROM stringtable WHERE language = ? AND string_key = ?");
	pStmt.setString(1,language);
	pStmt.setInt(2,stringKey);
	pStmt.executeUpdate();
	pStmt.close();
  }
  
  
  
  	public JSONArray getDataTable(PreparedStatement pStmt) throws Exception{
  		ResultSet rs = null;
  		JSONArray jsArray = new JSONArray();	  
	  		try{
	  			if (pStmt == null) {
	  				System.err.println("DBconnection: prepared statement null! " );
	  			} else {
	  				rs = pStmt.executeQuery(); 
	  			}
	  			if (rs == null) {
	  				System.err.println("DBconnection: statement result null! ");
	  			} else {
	  				int columns = rs.getMetaData().getColumnCount();
	  				while (rs.next()) {
	  					ArrayList<String> zeilenarray = new ArrayList<String>();
	  					for (int i = 1; i <= columns; i++) {
	  						zeilenarray.add(rs.getString(i));
	  					}
	  					jsArray.put(zeilenarray);                  
	  				}
	  			}   
	  			rs.close();
	  			pStmt.close();
	  		} catch (SQLException e) {   // Exception for SQL database
	  			System.err.println("DBconnection: No result, or problem with the database");
	  			System.err.println(pStmt.toString());
	  			e.printStackTrace();
	  		} catch (Exception e) {
	  			System.err.println("DBconnection: Some problem with database query. Error! ");
	  		  	e.printStackTrace();
	  		}
	  	return jsArray;
  	}
  	
  	
  	
	public JSONArray getSearchTable(PreparedStatement pStmt) throws Exception{
  		ResultSet rs=null;
  		JSONArray jsArray = new JSONArray();	  
	  		try{
	  			if (pStmt==null) {
	  				System.err.println("DBconnection: prepared statement null! " );
	  			} else {
	  				rs = pStmt.executeQuery(); 
	  			}
	  			if (rs==null) {
	  				System.err.println("DBconnection: statement result null! ");
	  			} else {
	  				int columns = rs.getMetaData().getColumnCount();
	  				while (rs.next()) {
	  					JSONObject rowObj=new JSONObject();
	  					rowObj.put("id", rs.getInt(1));
	  					rowObj.put("name", rs.getString(2));
	  					rowObj.put("type", rs.getInt(3));
	  					ArrayList<String> zeilenarray = new ArrayList<String>();
	  					for (int i = 4; i <= columns; i++) {
	  						zeilenarray.add(rs.getString(i));
	  					}
	  					rowObj.put("rowdata",zeilenarray);
	  					jsArray.put(rowObj);                  
	  				}
	  			}   
	  			rs.close();
	  			pStmt.close();
	  		} catch (SQLException e) {   // Exception for SQL database
	  			System.err.println("DBconnection: No result, or problem with the database");
	  			e.printStackTrace();
	  			System.err.println(pStmt.toString());
	  		} catch (Exception e) {
	  			System.err.println("DBconnection: Some problem with database query. Error! ");
	  		  	e.printStackTrace();
	  		}
	  	return jsArray;
  	}
  
  
  	
  
  public int createNewStringKey(String input) throws Exception{
	  PreparedStatement pStmt = conn.prepareStatement(
			  "INSERT INTO string_key_table VALUES (default,?,NOW()) RETURNING id");
	  pStmt.setString(1, input);
	  int id = getSingleIntValue(pStmt);
	  pStmt.close();
	  return id;
  }
  
  
  
  public int copyStringKey(int key,int userID) throws Exception{
	  PreparedStatement pStmt= null;
	  pStmt=conn.prepareStatement(
			  "INSERT INTO string_key_table (description,lastchange,lastuser) "
			  +"(SELECT description,NOW(),? FROM string_key_table WHERE id=?) "
			  +"RETURNING id");
	  pStmt.setInt(1,userID);
	  pStmt.setInt(2,key);
	  int newKey=getSingleIntValue(pStmt);
	  pStmt.close();
	  
	  // copy the corresponding stringtable-entries 
	  pStmt=conn.prepareStatement(
			  "INSERT INTO stringtable (string_key,language,value,lastchange,lastuser) "
	  		  + "  (SELECT ?, language, value, NOW(), ? "
	  		  + "  FROM stringtable WHERE string_key=?)");
	  pStmt.setInt(1,newKey);
	  pStmt.setInt(2,userID);
	  pStmt.setInt(3,key);
	  pStmt.executeUpdate();
	  pStmt.close();
	  return newKey;
  }
  
  
  
  public int copyStringKey(int key,int userID,String description) throws Exception{
	  PreparedStatement pStmt= null;
	  pStmt=conn.prepareStatement(
			  "INSERT INTO string_key_table (description,lastchange,lastuser) "
			  +"VALUES (?,NOW(),?) "
			  +"RETURNING id");
	  pStmt.setString(1,description);
	  pStmt.setInt(2,userID);
	  int newKey=getSingleIntValue(pStmt);
	  pStmt.close();
	  
	  // copy the corresponding stringtable-entries 
	  pStmt=conn.prepareStatement(
			  "INSERT INTO stringtable (string_key,language,value,lastchange,lastuser) "
	  		  + "  (SELECT ?, language, value, NOW(), ? "
	  		  + "  FROM stringtable WHERE string_key=?)");
	  pStmt.setInt(1,newKey);
	  pStmt.setInt(2,userID);
	  pStmt.setInt(3,key);
	  pStmt.executeUpdate();
	  pStmt.close();
	  return newKey;
  }
  
  
  
  public int addString(int key, String lang,String input) throws Exception {
	  PreparedStatement pStmt = conn.prepareStatement("DELETE FROM stringtable WHERE language=? AND string_key=?");
	  pStmt.setString(1, lang);
	  pStmt.setInt(2, key);
	  pStmt.executeUpdate();
	  pStmt.close();
	  pStmt=conn.prepareStatement("INSERT INTO stringtable (string_key,language,value,lastchange)"
	  		+ " VALUES (?,?,?,NOW()) RETURNING id");
	  pStmt.setInt(1, key);
	  pStmt.setString(2, lang);
	  pStmt.setString(3, input);
	  int id=getSingleIntValue(pStmt);
	  pStmt.close();
	  return id;
  }
  
  
  public void addStringSet (int key, JSONObject stringset) throws JSONException, Exception{
	  String[] names=JSONObject.getNames(stringset);
	  for (int i=0; i<names.length;i++){
		  addString(key,names[i],stringset.getString(names[i]));
	  }
  }
  
  
  public Boolean removeStringKey(int key) throws Exception {
	  PreparedStatement pStmt=conn.prepareStatement("DELETE FROM string_key_table WHERE string_key=?");
	  pStmt.setInt(1, key);
	  int keyDeleted=pStmt.executeUpdate();
	  pStmt.close();
	  return (keyDeleted==1);
  }
  
  
  public JSONObject jsonObjectFromPreparedStmt(PreparedStatement pStmt) throws Exception{
	  JSONObject result = null;
	  ResultSet queryResult=null;
	  result = new JSONObject();
	  	  try{
	          if (pStmt==null) {
		          System.err.println("DBconnection: prepared statement null! " );
	          } else {
	        	  queryResult = pStmt.executeQuery(); 
	          }
	          if (queryResult==null) {
	        	  System.err.println("DBconnection: statement result null! ");
	          } else {
	        	  int columns = queryResult.getMetaData().getColumnCount();
	        	  if ( queryResult.next() ){
		              for (int i = 1; i <= columns; i++) {
		                  result.put(queryResult.getMetaData().getColumnLabel(i), queryResult.getObject(i));
		              }
	        	  }
	        	  else {
//	        		  it is perfectly normal to not have a result...
//	        		  System.err.println("DBconnection: No result!");
	        	  }
	          }
	          queryResult.close();
	          pStmt.close();
	  	  } catch (SQLException e) {   // Exception for SQL database
	  		  System.err.println("DBconnection: No result, or problem with the database");
	  		  System.err.println(pStmt.toString());  // please remove this!
	  		  e.printStackTrace();
	  	  } catch (Exception e) {
	  		  System.err.println("DBconnection: Some problem with database query. Error! ");
//	  		  e.printStackTrace();
		  }
      return result;
  }
  

  
  	public JSONArray getStrings(ArrayList<String> stringkeys) {
		// get Strings from the database for an array of Stringkeys.
  		JSONArray strings = null;
		String query="SELECT id,string_key,language,value FROM Stringtable WHERE string_key=ANY('{";   	
		StringBuilder buff = new StringBuilder(); // join numbers with commas
		String sep = "";
		for (String str : stringkeys) {
			buff.append(sep);
			buff.append(str);
			sep = ",";
		}
		query+= buff.toString() + "}'::int[])";
		try {
			 strings=jsonfromquery(query);
		} catch (Exception e) {
			System.err.println("error fetching strings");
			e.printStackTrace();
		}
		return strings;
  	}
  
  	
  	
  	 public Boolean getSingleBooleanValue(PreparedStatement pStmt) throws Exception{
  		  Boolean result = false;
  		  ResultSet queryResult=null;
  		  	  try{
  		          if (pStmt==null) {
  			          System.err.println("DBconnection: prepared statement null! " );
  		          } else {
  		        	  queryResult = pStmt.executeQuery(); 
  		          }
  		          if (queryResult==null) {
  		        	  System.err.println("DBconnection: statement result null! ");
  		          } else {
  		        	  if ( queryResult.next() ){
  			                  result=queryResult.getBoolean(1);
  			              }
  		        	  else {
//  		        		  it is perfectly normal to not have a result...
//  		        		  System.out.println("DBconnection: No result!");
  		        	  }
  		          }
  		          queryResult.close();
  		          pStmt.close();
  		  	  } catch (SQLException e) {   // Exception for SQL database
  		  		  System.err.println("DBconnection: No result, or problem with the database");
  		  		  System.err.println(pStmt.toString());
  		  		  e.printStackTrace();
  		  		  result=false;
  		  	  } catch (Exception e) {
  		  		  System.err.println("DBconnection: Some problem with database query. Error! ");
//  		  		  e.printStackTrace();
  			  }
  	      return result;
  	      }
  	  
  
  
  public int getSingleIntValue(PreparedStatement pStmt) throws Exception{
	  int result = -1;
	  ResultSet queryResult=null;
	  	  try{
	          if (pStmt==null) {
		          System.err.println("DBconnection: prepared statement null! " );
	          } else {
	        	  queryResult = pStmt.executeQuery(); 
	          }
	          if (queryResult==null) {
	        	  System.err.println("DBconnection: statement result null! ");
	          } else {
	        	  if ( queryResult.next() ){
		                  result=queryResult.getInt(1);
		              }
	        	  else {
//	        		  it is perfectly normal to not have a result...
//	        		  System.out.println("DBconnection: No result!");
	        	  }
	          }
	          queryResult.close();
	          pStmt.close();
	  	  } catch (SQLException e) {   // Exception for SQL database
	  		  System.err.println("DBconnection: No result, or problem with the database");
	  		  System.err.println(pStmt.toString());
	  		  e.printStackTrace();
	  		  result=0;
	  	  } catch (Exception e) {
	  		  System.err.println("DBconnection: Some problem with database query. Error! ");
//	  		  e.printStackTrace();
		  }
      return result;
      }
  
  
  
  	public JSONObject getSingleJSONObject(PreparedStatement pStmt) throws Exception{
  		ResultSet queryResult=null;
		try{
			if (pStmt!=null) {
	        	queryResult = pStmt.executeQuery(); 
	        } else {
				System.err.println("DBconnection: prepared statement null! " );
	        }
	        if (queryResult==null) {
	        	System.err.println("DBconnection: statement result null! ");
	        } else {
	        	if ( queryResult.next() ){
	        		JSONObject tempObject = new JSONObject (queryResult.getString(1));
	    	        queryResult.close();
	        		return tempObject;
	        	} else {
//	        		  it is perfectly normal to not have a result...
//	        		  System.out.println("DBconnection: No result!");
	        		return null;
	        	}
	        }
	        pStmt.close();
	  	} catch (SQLException e) {   // Exception for SQL database
	  		System.err.println("DBconnection: No result, or problem with the database");
	  		System.err.println(pStmt.toString());
	  		e.printStackTrace();
	  	} catch (Exception e) {
	  		System.err.println("DBconnection: Some problem with database query. Error! ");
//	  		  e.printStackTrace();
		}
	  	return null;
  	}
  
  
  
  public String getSingleStringValue(PreparedStatement pStmt) throws Exception{
	  String result = "";
	  ResultSet queryResult=null;
	  	  try{
	          if (pStmt==null) {
		          System.err.println("DBconnection: prepared statement null! " );
	          } else {
	        	  queryResult = pStmt.executeQuery(); 
	          }
	          if (queryResult==null) {
	        	  System.err.println("DBconnection: statement result null! ");
	          } else {
	        	  if ( queryResult.next() ){
		                  result=queryResult.getString(1);
		              }
	        	  else {
//	        		  it is perfectly normal to not have a result...
//	        		  System.err.println("DBconnection: No result!");
	        	  }
	          }
	          queryResult.close();
	          pStmt.close();
	  	  } catch (SQLException e) {   // Exception for SQL database
	  		  System.err.println("DBconnection: No result, or problem with the database");
	  		  e.printStackTrace();
	  		  System.out.println(pStmt.toString());
	  	  } catch (Exception e) {
	  		  System.err.println("DBconnection: Some problem with database query. Error! ");
//	  		  e.printStackTrace();
		  }
      return result;
      }
  
  
//  public Map<Integer, String> getAllJdbcTypeNames() {
//
//	    Map<Integer, String> result = new HashMap<Integer, String>();
//
//	    for (Field field : Types.class.getFields()) {
//	        try {
//				result.put((Integer)field.get(null), field.getName());
//			} catch (IllegalArgumentException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			} catch (IllegalAccessException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//	    }
//
//	    return result;
//	}
  
  
  
  public JSONArray table2json(ResultSet rs) throws Exception {
        JSONArray jsArray = new JSONArray();
        
        
        ResultSetMetaData rsmd = rs.getMetaData();
        int columns = rsmd.getColumnCount();

//        Map<Integer, String> jdbcMappings = getAllJdbcTypeNames();
//
//        for (int i = 1; i <= columns; i++) {
//        	int dataType = rsmd.getColumnType(i);
//        	String typeName = jdbcMappings.get(dataType); // now that will return BIGINT
//        	System.out.println("Typenr.: "+dataType+"; Type: "+typeName);
//        }
//        
        
        while (rs.next()) {
            JSONObject obj = new JSONObject();
            for (int i = 1; i <= columns; i++) {
            	int dataType = rsmd.getColumnType(i);
            	if (dataType==1111){
            		JSONObject tempObject = null;
            		if (rs.getString(i)!=null) {
            			String jString = rs.getString(i);
            			if (jString.substring(0, 1) != null){
                			if (jString.substring(0, 1).equals("[")){
                    			JSONArray tempArray = new JSONArray (rs.getString(i));
            	                obj.put(rs.getMetaData().getColumnLabel(i), tempArray);
                			} else {
                    			tempObject = new JSONObject (rs.getString(i));
            	                obj.put(rs.getMetaData().getColumnLabel(i), tempObject);
                    		}
            			};
            		}
            	}else{
	            	Object tempObject = rs.getObject(i);
	                obj.put(rs.getMetaData().getColumnLabel(i), tempObject);
                }
            }
            jsArray.put(obj);
        }
        return jsArray;
  }
     
	
    
  public void testqueryDB(String query) {
    	System.out.println("testqueryDB");
    	if (conn==null) {
        	System.out.print("conn null! " );
        	return;
        }
    	Statement stmt;
		try {
			stmt = conn.createStatement();
			if (stmt==null) {
	        	System.err.print("DBconnection: statement null! " );
	        	return;
	        }
	        String sql;
	        sql = "select value from stringtable where stringID=5";
	        ResultSet rs = stmt.executeQuery(sql);
	        if (rs==null) {
	        	System.err.print("DBconnection: statement result null! " + sql);
	        } else {
		        while(rs.next()){
		            String wert = rs.getString("value"); 	//Retrieve by column name
		            System.out.print("String: " + wert);    //Display values
		         }
		         rs.close();  //Clean-up environment
	        }
	         stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
  }
}
