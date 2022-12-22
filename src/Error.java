

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class Error
 */
@WebServlet("/Error")
public class Error extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Error() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		// response.sendRedirect("Login");
		doPost(request,response);
	}
	
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String id="",who="",name,date,Q,s="Error",message;
		boolean valid=true;
		PrintWriter pr=response.getWriter();
		HttpSession ses=request.getSession(false);
		Functions fob=new Functions(); fob.noCache(response);
		
		try{
			try{
				name=ses.getAttribute("user_fname").toString();id=ses.getAttribute("user_id").toString();who="user";
			}catch(Exception p){
				name=ses.getAttribute("admin_fname").toString();id=ses.getAttribute("admin_id").toString();who="admin";
			}
			if(who.equals("user")==true) s=fob.head(id)+fob.navbar(ses);
			else if(who.equals("admin")==true) s=fob.head(id)+fob.navbarAdmin(ses);
		}catch(Exception e){}
		
		
		if(request.getAttribute("error")!=null) message=request.getAttribute("error").toString();
		else message="<span class=\"t-lg t-red\">Error occured try again make sure that javascript is enabled?</span>";
		
		s+=	
				"<div class=\"messagediv\">"+
					"<div class=\"jumbotron\" >"+
						"<table class=\"messagetable\">"+
							"<tr>"+
								"<td>"+
									"<div class=\"message\">"+
										message+
									"</div>"+
								"</td>"+
							"</tr>"+
						"</table>"+
					"</div>"+
				"</div>"+
			"</body>"+
		"</html>";
		pr.println(s);
		
	}
}
