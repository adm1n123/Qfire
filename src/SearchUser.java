//Qfire

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class SearchUser
 */
@WebServlet("/SearchUser")
public class SearchUser extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchUser() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String s="",who="",id="";
		boolean valid=true;
		PrintWriter pr=response.getWriter();
		HttpSession ses=request.getSession(false);
		Functions fob=new Functions(); fob.noCache(response);
		if(fob.validate(ses)==false) { response.sendRedirect("Login");return;}
		
		try{
			try{
				id=ses.getAttribute("user_id").toString();who="user";
			}catch(Exception p){
				id=ses.getAttribute("admin_id").toString();who="admin";
			}
		}catch(Exception e){ response.sendRedirect("Login");return;}

		if(fob.validId(id)==false) valid=false;
		if(valid==false) { response.sendRedirect("Error");return;}
		
		if(who.equals("user")) s=fob.head(id)+fob.navbar(ses);
		else if(who.equals("admin")==true) s=fob.head(id)+fob.navbarAdmin(ses);
		
		s+=
						
					"<div class=\"container\" id=\"bodycontainer\">"+
						"<div class=\"row\">"+
							"<div class=\"col-md-6 col-md-offset-3\">"+
								"<div class=\"page-header\">"+
									"<h4 class=\"pageheading\">Find user</h4>"+
								"</div>"+
								"<div>"+
									"<div class=\"input-group\">"+
										"<span class=\"input-group-addon\"><i class=\"glyphicon glyphicon-user\"></i></span>"+
										"<input class=\"form-control\" type=\"text\" id=\"userid\" maxlength=\"91\" placeholder=\"Enter either user id or full name or first/last name\">"+
									"</div>"+
									"</br>"+
									"<div class=\"btn-group f-right\">"+
										"<button class=\"finduser btn\" onclick=SearchById();><i class=\"glyphicon glyphicon-search\"></i> Search By Id</button> "+
										"<button class=\"finduser btn\" onclick=SearchByName();><i class=\"glyphicon glyphicon-search\"></i> Search By Name</button></br>"+
									"</div>"+
									"<div><span class=\"t-blue\">Type asterisk(*) to select all users</span></div>"+
								"</div>"+
								"<div class=\"clearfix\"></div>"+
								"<div id=\"resultdiv\"></div>"+
							"</div>"+
						"</div>"+
					"</div>"+
				"</body>"+
			"</html>";
		pr.println(s);
		pr.flush();
	}
}
