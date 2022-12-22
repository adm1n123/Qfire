
// Qfire
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class Login
 */
@WebServlet("/Login")
public class Login extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Login() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String s,wrong=" ",Head;
		PrintWriter pr=response.getWriter();
		HttpSession ses=request.getSession(false);
		Functions fob=new Functions(); fob.noCache(response);
		
		try{
			ses.invalidate();
		}catch(Exception e){}
		try{
			if(request.getAttribute("wrong").equals("true")) wrong="<span class=\"t-red\">Invalid user id or password</span>";
			
		}catch(Exception e){}
		Head=fob.head("");
		s=Head+	"<div class=\"jumbotron\">"+
					"<div>"+
						"<img style=\"display: block;margin-left: auto;margin-right:auto;\" class=\"img-responsive\" src=\"Images/Qfire_logo.png\" alt=\"Qfire\" />"+
					"</div>"+
				"</div>"+
				"<div class=\"container\">"+
					"<div class=\"row\">"+
						"<div class=\"col-md-6 col-md-offset-3 col-sm-8 col-sm-offset-2\">"+
							"<div class=\"panel panel-info \" id=\"loginpanel\">"+
								"<div class=\"panel-heading\">Login</div>"+
								"<div style=\"padding-top:40px\" class=\"panel-body\">"+
									"<form action=\"Login\" method=\"post\">"+
										"<div class=\"form-group\">"+
											"<div class=\"input-group\">"+
												"<span class=\"input-group-addon\"><i class=\"fa fa-user-circle fa-lg\"></i></span>"+
												"<input class=\"form-control\" type=\"text\" onkeyup=checkLogin(); id=\"id\" name=\"id\" maxlength=\"45\" placeholder=\"Handle\">"+
											"</div>"+
										"</div>"+
										"<div class=\"form-group has-feedback\">"+
											"<div class=\"input-group\">"+
												"<span class=\"input-group-addon\"><i class=\"fa fa-unlock fa-lg\"></i></span>"+
												"<input class=\"form-control\" type=\"password\" onkeyup=checkLogin(); id=\"password\" name=\"password\" maxlength=\"45\" placeholder=\"Password\">"+
											"</div>"+
											"<span class=\"form-control-feedback eye\" style=\"pointer-events: initial;\">"+
												"<i class=\"fa fa-eye fa-lg i-link\" onclick=eye(\"password\");></i>"+
											"</span>"+
										"</div>"+
										"<div class=\"form-group\">"+
											"<span id=\"span\">"+wrong+"&nbsp;</span>"+
										"</div>"+
										"<div class=\"form-group\" style=\"float:right\" >"+
											"<button type=\"submit\" class=\"btn btn-success\" value=\"Login\"><span class=\"glyphicon glyphicon-log-in\"></span> Login</button> "+
											"<button class=\"btn btn-danger\" type=\"reset\"><span class=\"fa fa-refresh\"></span> Reset</button>"+
										"</div>"+
										"<div class=\"form-group\">"+
											"<a href=\"#\" id=\"forgot\">Forgot password?</a>"+
										"</div>"+
										"<div class=\"form-group\">"+
											"<p>Don't have account? <a href=\"SignUp\">Sign Up</a></p>"+
										"</div>"+
									"</form>"+
								"</div>"+
							"</div>"+
							"<div class=\"panel panel-info\" id=\"resetpanel\" style=\"display:none\">"+
								"<div class=\"panel-heading\">Send Link</div>"+
								"<div style=\"padding-top:40px\" class=\"panel-body\">"+
									"<div class=\"input-group\">"+
										"<span class=\"input-group-addon\"><i class=\"glyphicon glyphicon-envelope\"></i></span>"+
										"<input class=\"form-control\" type=\"email\" id=\"email\" placeholder=\"Enter your email\">"+
									"</div>"+
									"</br>"+
									"<div class=\"input-group\" style=\"float:right\" >"+
										"<button class=\"btn btn-success\" onclick=sendForgotPassword();><i class=\"glyphicon glyphicon-send\"></i> Send</button> "+
									"</div>"+
									"<div>"+
										"<span id=\"forgotspan\">&nbsp;</span>"+
									"</div>"+
								"</div>"+
							"</div>"+
						"<div>"+
					"</div>"+
				"</div>"+
			"</body>"+
		"</html>";
		pr.println(s);
		pr.flush();
	}
	
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		Functions fob=new Functions();
		
		String id,password,s="";
		
		id=request.getParameter("id");
		password=request.getParameter("password");
		
		s=fob.login(id, password, request);
		
		if(s.equals("Wrong")==true){

			try{
				String notification;
				HashSet<String> idset=new HashSet<String>();
				notification="<span> Login attempt failed with user_id="+id+" and password="+password+"<span class=\"t-grey t-sx\"> on "+fob.date()+"</span><span>";
				idset.add("admin123");
				fob.sendNotification(idset, notification);
				idset.clear();
			}catch(Exception e){}
			
			request.setAttribute("wrong", "true");
			doGet(request, response);
		} else if(s.equals("User")==true){
			response.sendRedirect("UserHome");
		} else if(s.equals("Admin")==true){
			response.sendRedirect("AdminHome");
		} else if(s.equals("Failed")==true){
			String notification;
				HashSet<String> idset=new HashSet<String>();
				notification="<span> Login attempt failed with user_id="+id+" and password="+password+"<span class=\"t-grey t-sx\"> on "+fob.date()+"</span><span>";
				idset.add("admin123");
				fob.sendNotification(idset, notification);
				idset.clear();

			response.sendRedirect("Login");
		}
	}
}
