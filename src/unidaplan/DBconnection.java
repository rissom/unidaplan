package unidaplan;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;
//import java.util.Properties;
/** Simple servlet for testing deployment on server.
 *  <p>
 *  From <a href="http://courses.coreservlets.com/Course-Materials/">the
 *  coreservlets.com tutorials on servlets, JSP, Struts, JSF, Ajax, GWT, 
 *  Spring, Hibernate/JPA, and Java programming</a>.
 */



public class DBconnection  {
  Connection conn = null;
  static String dbURL2 = "jdbc:postgresql://localhost/thorse";
  static String user = "thorse";
  static String pass = "jame765!";
  private static final String DEFAULT_DRIVER = "org.postgresql.Driver";

    
    
  public void startDB() throws Exception {
	try {
		Class.forName(DEFAULT_DRIVER);
		conn = DriverManager.getConnection(dbURL2, user, pass);
	} catch (SQLException e) {
		System.err.println("DBconnection: Error connecting to database! Is PostgreSQL running?");
		throw new Exception();
	} catch (ClassNotFoundException e) {
		System.err.println("DBconnection: Error connecting to database. Class not found.");
		throw new Exception();
	}
  	if (conn==null) {
    	System.err.println("DBconnection: conn null! " );
    	System.err.println("DBconnection: Error connecting to database");
		throw new Exception();
    } 
  }
  
  
  public void closeDB() {
	try {
		conn.close();
	} catch (SQLException e) {
		System.err.println("Error closing database");
	}
  }

  
  
  public JSONArray jsonfromquery(String query) throws Exception{
	  Statement stmt = null;
	  JSONArray result = null;
	  ResultSet queryresult=null;
	  	  try {
	          stmt = conn.createStatement();
	          if (stmt==null) {
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
  	  } catch (Exception e) {
  		  System.err.println("DBconnection: Some problem during first database query. Error! ");
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
        	  result=new JSONArray();
          } else {
        	  result = table2json(queryResult);
        	  queryResult.close();
          }     
          pStmt.close();
  	  } catch (SQLException e) {   // Exception for SQL database
  		  System.err.println("DBconnection: No result, or problem with the database"); // please remove this!
  		  System.err.println(pStmt.toString());  // please remove this!
  	  } catch (Exception e) {
  		  System.err.print("DBconnection: Some problem with database query. Error! ");
	  		  e.printStackTrace();
	  }
	  return result;
  }
  
  
  
  	public JSONArray getDataTable(PreparedStatement pStmt) throws Exception{
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
	  PreparedStatement pStmt= null;
	  pStmt=conn.prepareStatement("INSERT INTO string_key_table VALUES (default,?,NOW()) RETURNING id");
	  pStmt.setString(1, input);
	  int id=getSingleIntValue(pStmt);
	  return id;
  }
  
  
  
  public int addString(int key, String lang,String input) throws Exception {
	  PreparedStatement pStmt=conn.prepareStatement("INSERT INTO stringtable values(default,?,?,?) RETURNING id");
	  pStmt.setInt(1, key);
	  pStmt.setString(2, lang);
	  pStmt.setString(3, input);
	  int id=getSingleIntValue(pStmt);
	  return id;
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
//	  		  e.printStackTrace();
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
	  		  result=0;
	  	  } catch (Exception e) {
	  		  System.err.println("DBconnection: Some problem with database query. Error! ");
//	  		  e.printStackTrace();
		  }
      return result;
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
	  	  } catch (Exception e) {
	  		  System.err.println("DBconnection: Some problem with database query. Error! ");
//	  		  e.printStackTrace();
		  }
      return result;
      }
  
  
  
  public JSONArray table2json(ResultSet rs) throws Exception {
        JSONArray jsArray = new JSONArray();        
        int columns = rs.getMetaData().getColumnCount();
        while (rs.next()) {
            JSONObject obj = new JSONObject();
            for (int i = 1; i <= columns; i++) {
            	Object tempObject=rs.getObject(i);
                obj.put(rs.getMetaData().getColumnLabel(i), tempObject);
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
