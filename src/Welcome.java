

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class Welcome
 */
@WebServlet("/Welcome")
public class Welcome extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Welcome() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String s="",t,who="",id="",questionid,Q,page="question",answercount,buttons,Home;
		boolean valid=true;
		PrintWriter pr=response.getWriter();
		HttpSession ses=request.getSession(false);
		Functions fob=new Functions(); fob.noCache(response);
		
		String Head="",Navbar,name="Guest",date="",st[];
		try{
			try{
				id=ses.getAttribute("user_id").toString();name=ses.getAttribute("user_fname").toString();who="user";
			}catch(Exception p){
				id=ses.getAttribute("admin_id").toString();name=ses.getAttribute("admin_fname").toString();who="admin";
			}
		}catch(Exception e){}
		
		s=		"<!DOCTYPE html>" + 
				"<html lang=\"en\">" + 
					"<head>" + 
						"<title>Qfire</title>"+
						"<meta charset=\"utf-8\">"+
						"<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">" + 
						"<link rel=\"stylesheet\" href=\"font-awesome-4.7.0/css/font-awesome.min.css\">" + 
						"<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css\">" +
						"<link href=\"https://fonts.googleapis.com/css?family=Montserrat\" rel=\"stylesheet\">"+
						
						"<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js\"></script>" + // this must be a less prior than bootstrapcdn
						"<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js\"></script>" + // must be after jquery.min
						
						"<script src=\"script.js\"></script>"+
						"<link rel=\"stylesheet\" href=\"style.css\" type=\"text/css\" media=\"all\"/>"+
				"</head>" +
				"<body class=\"wcmbody\">"+
				"<div>"+fob.modal(id)+"</div>";
		
			
			s+=fob.head(id);
			Home=
					"<nav class=\"navbar navbar-default navbar-fixed-top\" role=\"navigation\">"+
						"<div class=\"container\">"+
							"<div class=\"navbar-header\">"+
								"<button type=\"button\" class=\"navbar-toggle\" data-toggle=\"collapse\" data-target=\"#mynavbar\">"+
									"<span class=\"sr-only\">Toggle navigation</span>"+	
									"<span class=\"icon-bar\"></span>"+
									"<span class=\"icon-bar\"></span>"+
									"<span class=\"icon-bar\"></span>"+
								"</button>"+
								"<span class=\"navbar-brand\"><a href=\"Welcome\"><img class=\"img-responsive\" src=\"Images/Qfire_logo.png\" alt=\"Qfire\" id=\"sitelogo\" /></a></span>"+
							"</div>"+
							"<div class=\"collapse navbar-collapse\" id=\"mynavbar\">"+
								"<ul class=\"nav navbar-nav navbar-left\">"+
									"<li><a href=\"UserHome\">Hi &nbsp;<span id=\"username\">"+name+"</span></a></li>"+
									"<li>"+
										"<form class=\"navbar-form\" action=\"SearchOrAskQuestion\" method=\"post\">"+
											"<div class=\"input-group\">"+
												"<input type=\"text\" class=\"form-control\" size=\"45\" maxlength=\"200\" name=\"question\" required=\"required\" onfocus=modal(); pattern=\".{10,200}\" title=\"only 10-200 characters\" placeholder=\" Search or Ask query here\">"+
												"<div class=\"input-group-btn\">"+
													"<button class=\"btn btn-info\" name=\"searchquestion\" value=\"search\"><i class=\"glyphicon glyphicon-search\"></i> Search</button>"+
													"<button class=\"btn btn-default\" name=\"askquestion\" value=\"ask\">Ask</button>"+
												"</div>"+
											"</div>"+
										"</form>"+
									"</li>"+
								"</ul>"+
								"<ul class=\"nav navbar-nav navbar-right\">"+
									"<li>"+
										"<a href=\"Login\"><i class=\"fa fa-sign-in fa-lg\"></i> Login</a>"+
									"</li>"+
									"<li>"+
									"<a href=\"SignUp\"><i class=\"fa fa-chevron-circle-up fa-lg\"></i> Signup</a>"+
									"</li>"+
								"</ul>"+
							"</div>"+
						"</div>"+
					"</nav>";
			s+=Home;
			s+=		"<div class=\"container-fluid wcmcontainer-fluid text-center\">" + 
						"<h1 class=\"margin\">Welcome</h1>" +
						"<p>I'm glad to see you on Qfire. This is plateform to fire any query and get handled by someone like you. You are free to post your response on any question around.</p>"+
						"<p>I will invite you to join us, contribute and help this community to become large.</p>"+
					"</div>"+
					"<div class=\"container-fluid bg-2 text-center\">" +
						"<h3 class=\"margin\">Features</h3>" +
						"<p>This site has many features not only questions and answers but also voting/comment/tag system, inbox/outbox, searching user, managing reading list/notes</p>"+
						"<p>next word prediction while typing question and</p>"+
						"<p>finding the online users, account settings, searching the question by tag, sorting the answers by popularity/oldest etc.</p>"+
					"</div>"+
					"<div class=\"container-fluid text-center\">" +
						"<h3 class=\"margin\">About</h3>" +
						"<p>This web application is developed by Deepak Baghel a student of <a href=\"http://mitsgwalior.in/\" target=\"_blank\">Madhav Institute of Technology and Science, Gwalior</a> as minor project during 6<sup>th</sup> semester of Bachelor of Engineering in Computer Science stream.</p>"+
						"<p><span class=\"t-bold\">Technology: </span> Backend is developed in Java servlet with MySQL as database engine and frontend involves HTML, CSS, bootstrap, javascript, jquery.</p></br>"+
						"<img src=\"https://www.gravatar.com/avatar/82c79e1d6a75e9fb03edc6b53f859a25?s=150.jpg\" class=\"img-responsive img-circle margin admin123\" alt=\"Author\">"+
						"<p>Hi i am competitive programmer learning data-structures and algorithms having problem solving ability, this is my first web application i did my best in backend.</p>"+
						"<p>If you find any bug then please let me know and suggestions/improvements are welcome.</p>"+
						"<div><a href=\"Deepak_Baghel_CV.pdf\" target=\"_blank\">Download CV</a></div>"+
						"</br></br>"+
					"</div>"+
					"<footer class=\"container-fluid text-center bg-4\">"+
						"<div>"+
							"</br>"+
							"<span class=\"t-bold\">My Profiles </span>"+
							"<a href=\"https://www.codechef.com/users/admin123\" target=\"_blank\">CodeChef</a>&nbsp;&nbsp;"+
							"<a href=\"http://www.spoj.com/users/admin123/\" target=\"_blank\">SPOJ</a>&nbsp;&nbsp;"+
							"<a href=\"https://www.hackerrank.com/adm1n123\" target=\"_blank\">Hacker Rank</a>&nbsp;&nbsp;"+
							"<a href=\"http://codeforces.com/profile/admin123\" target=\"_blank\">Codeforces</a>&nbsp;&nbsp;"+
							"<a href=\"https://www.linkedin.com/in/adm1n123\" target=\"_blank\"><i class=\"fa fa-linkedin-square fa-2x\"></i></a>&nbsp;&nbsp;"+
							"<a href=\"https://www.facebook.com/adm1n123\" target=\"_blank\"><i class=\"fa fa-facebook-official fa-2x\"></i></a>"+
							"</br></br></br></br>"+
						"</div>"+
					"</footer>"+
				"</body>"+
			"<html>";
		
		
		pr.println(s);
		
	}

}
