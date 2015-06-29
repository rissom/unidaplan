package unidaplan;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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
  static Connection conn = null;
  static String dbURL2 = "jdbc:postgresql://localhost/thorse";
  static String user = "thorse";
  static String pass = "jame765!";
  private static final String DEFAULT_DRIVER = "org.postgresql.Driver";
    
    
    
  public static void startDB() {
	try {
		Class.forName(DEFAULT_DRIVER);
		conn = DriverManager.getConnection(dbURL2, user, pass);
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	} catch (ClassNotFoundException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  	if (conn==null) {
    	System.out.print("conn null! " );
    	System.out.print("Error connecting to database");
    } else
    {
//        System.out.println("Connected to database.");
    } 
  }
  
  
  public static void closeDB() {
	try {
		conn.close();
	} catch (SQLException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
  }

  
  
  public static JSONArray jsonfromquery(String query) throws Exception{
	  Statement stmt = null;
	  JSONArray result = null;
	  ResultSet queryresult=null;
	  	  try {
	          stmt = DBconnection.conn.createStatement();
	          if (stmt==null) {
		          System.out.print("statement null! " );
	      }
          queryresult = stmt.executeQuery(query);
          if (queryresult==null) {
        	  System.out.print("statement result null! ");
        	  result=new JSONArray();
          } else {
        	  result = table2json(queryresult);
        	  queryresult.close();
          }     
          stmt.close();
  	  } catch (SQLException e) {   // Exception for SQL database
  		  System.out.print("Problem with the database! Error! ");
  		  e.printStackTrace();
  	  } catch (Exception e) {
  		  System.out.print("Some problem during first database query. Error! ");
	  		  e.printStackTrace();
	  }
	  return result;
  }
  
  
  public static JSONArray jsonFromPreparedStmt(PreparedStatement pStmt) throws Exception{
	  JSONArray result = null;
	  ResultSet queryResult=null;
	  	  try {
		          if (pStmt==null) {
			          System.out.print("prepared statement null! " );
		          }
		          queryResult = pStmt.executeQuery();
          if (queryResult==null) {
        	  System.out.print("statement result null! ");
        	  result=new JSONArray();
          } else {
        	  result = table2json(queryResult);
        	  queryResult.close();
          }     
          pStmt.close();
  	  } catch (SQLException e) {   // Exception for SQL database
  		  System.out.print("Problem with the database! Error! ");
  		  e.printStackTrace();
  	  } catch (Exception e) {
  		  System.out.print("Some problem with database query. Error! ");
	  		  e.printStackTrace();
	  }
	  return result;
  }
     
  
  
  public static JSONArray table2json(ResultSet rs) throws Exception {
        JSONArray jsArray = new JSONArray();        
        int rows = rs.getMetaData().getColumnCount();
        while (rs.next()) {                
            JSONObject obj = new JSONObject();
            for (int i = 1; i <= rows; i++) {
                obj.put(rs.getMetaData().getColumnLabel(i), rs.getObject(i));
            }
            jsArray.put(obj);
        }
        return jsArray;
  }
     
	
	
//        /**
//         * Convert a result set into a XML List
//         * @param resultSet
//         * @return a XML String with list elements
//         * @throws Exception if something happens
//         */
//        public String convertToXML(ResultSet resultSet)
//                throws Exception {
//            StringBuffer xmlArray = new StringBuffer("<results>");
//            while (resultSet.next()) {
//                int total_rows = resultSet.getMetaData().getColumnCount();
//                xmlArray.append("<result ");
//                ="" for="" (int="" i="0;" <="" total_rows;="" i++)="" {="" xmlarray.append("="" "="" +="" resultset.getmetadata().getcolumnlabel(i="" 1).tolowercase()="" }="">");            }
//            xmlArray.append("</result></results>");
//            return xmlArray.toString();
//        }
//    }
    
	
    
  public static void testqueryDB(String query) {
    	System.out.println("testqueryDB");
    	if (conn==null) {
        	System.out.print("conn null! " );
        	return;
        }
    	Statement stmt;
		try {
			stmt = conn.createStatement();
			if (stmt==null) {
	        	System.out.print("statement null! " );
	        	return;
	        }
	        String sql;
	        sql = "select value from stringtable where stringID=5";
	        ResultSet rs = stmt.executeQuery(sql);
	        if (rs==null) {
	        	System.out.print("statement result null! " + sql);
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
