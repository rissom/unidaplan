	package unidaplan;
	import java.io.IOException;
	import java.io.PrintWriter;
	import javax.servlet.ServletException;
	import javax.servlet.http.HttpServlet;
	import javax.servlet.http.HttpServletRequest;
	import javax.servlet.http.HttpServletResponse;
	import org.json.JSONArray;

	public class Experiments extends HttpServlet {
		private static final long serialVersionUID = 1L;
		private static JSONArray result;

	@Override
	  public void doGet(HttpServletRequest request, HttpServletResponse response)
	      throws ServletException, IOException {
	    response.setContentType("text/html");
	    PrintWriter out = response.getWriter();
	    DBconnection.startDB();
		String plang="de"; // primary language = Deutsch
		String slang="en"; // secondary language = english
		
	    String query = 
		"SELECT exp_plan.ID AS ID,users.name as creator, COALESCE (a.value,b.value) as name "
	    + "FROM  exp_plan \n"
	    + "JOIN users ON (users.id=exp_plan.Creator) \n"
	    + "JOIN stringtable a ON (exp_plan.Name=a.string_key AND a.language='"+plang+"')"
	    + "JOIN stringtable b ON (exp_plan.Name=b.string_key AND b.language='"+slang+"')";
	    try {  // get json from the database using the query
			result=DBconnection.jsonfromquery(query);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		out.println(result.toString());
		DBconnection.closeDB();
	}
}	