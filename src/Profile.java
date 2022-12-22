

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
 * Servlet implementation class Profile
 */
@WebServlet("/Profile")
public class Profile extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Profile() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		PrintWriter pr=response.getWriter();
		HttpSession ses=request.getSession(false);
		Functions fob=new Functions(); fob.noCache(response);
		if(fob.validate(ses)==false) { response.sendRedirect("Login");return;}
		
		String who="";
		String s="",userid,Q,id="",hash,notification,fname="",lname="";
		boolean valid=true;
		
		try{
			try{
				id=ses.getAttribute("user_id").toString();who="user";
				fname=ses.getAttribute("user_fname").toString();
				lname=ses.getAttribute("user_lname").toString();
			}catch(Exception p){
				id=ses.getAttribute("admin_id").toString();who="admin";
				fname=ses.getAttribute("admin_fname").toString();
				lname=ses.getAttribute("admin_lname").toString();
			}
		}catch(Exception e){ response.sendRedirect("Login");return;}

		
		userid=request.getParameter("id");
		if(fob.validId(id)==false) valid=false;
		if(fob.validId(userid)==false) valid=false;
		if(valid==false) { response.sendRedirect("Error");return;}
		
		if(who.equals("user")) s=fob.head(id)+fob.navbar(ses);
		else if(who.equals("admin")==true) s=fob.head(id)+fob.navbarAdmin(ses);
		
		s+="<div id=\"bodycontainer\" class=\"container\">";
		
		try{
			
			if(id.equals(userid)==false&&who.equals("admin")==false){
				HashSet<String> idset=new HashSet<String>();
				notification="<span><a href=\"Profile?id="+id+"\">"+fname+" "+lname+"</a> viewed you profile. <span class=\"t-grey t-sx\"> "+fob.date()+"</span><span>";
				idset.add(userid);
				fob.sendNotification(idset, notification);
				idset.clear();
			}
			
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection cn=DriverManager.getConnection("jdbc:mysql://localhost:3306/Qfire","root","qazzaq");
			Statement smt=cn.createStatement();
			Q="select * from users where user_id='"+userid+"' limit 1";
			ResultSet rs=smt.executeQuery(Q);
			if(rs.next()){
				hash=fob.md5Hex(rs.getString("user_email"));
				
							s+="<div class=\"row\">"+
									"<div class=\"col-md-6 col-md-offset-3\">"+
										"<div class=\"page-header\">"+
											"<h4 class=\"pageheading\">User profile</h4>"+
										"</div>"+
										"<div class=\"row\">"+
											"<div class=\"col-md-3\">"+
												"<img class=\"img-thumbnail\" src=\"https://www.gravatar.com/avatar/"+hash+"?s=100.jpg\" title=\"Email Gravatar image\" />"+
											"</div>"+
											"<div class=\"col-md-9\">"+
												"<div class=\"t-lg t-bold t-grey\">"+rs.getString("user_fname")+" "+rs.getString("user_lname")+"</div>"+
												"<div class=\"t-sm t-gre blockquote\">"+rs.getString("user_about")+"</div>"+
											"</div>"+
										"</div>"+
										"</br></br>"+
										"<div>"+
											"<a href=\"AskedQuestions?user="+userid+"\">Asked Questions</a>&nbsp;&nbsp;&nbsp;&nbsp;"+
											"<a href=\"MyAnswers?user="+userid+"\">My Answers</a>&nbsp;&nbsp;&nbsp;"+
											"<a href=\"ReadingList?user="+userid+"\">Reading List</a>"+
											"<div class=\"f-right\">"+
												"<a href=\""+rs.getString("facebook_link")+"\" target=\"_blank\"><i class=\"fa fa-facebook-official fa-2x\"></i></a>&nbsp;&nbsp;"+
												"<a href=\""+rs.getString("google_link")+"\" target=\"_blank\"><i class=\"fa fa-google-plus-official fa-2x i-red\"></i></a>&nbsp;&nbsp;"+
												"<a href=\""+rs.getString("github_link")+"\" target=\"_blank\"><i class=\"fa fa-github fa-2x i-black\"></i></a>&nbsp;&nbsp;"+
												"<a href=\""+rs.getString("quora_link")+"\" target=\"_blank\"><i class=\"fa fa-quora fa-2x i-red\"></i></a>"+
											"</div>"+
										"</div>"+
										"<table class=\"table table-bordered\">"+
											"<tr>"+
												"<td class=entity>User Id </td><td>"+rs.getString("user_id")+"</td>"+
											"</tr>"+
											"<tr>"+
												"<td class=entity>Gender </td><td>"+rs.getString("user_gender")+"</td>"+
											"</tr>"+
											"<tr>"+
												"<td class=entity>Country </td><td>"+rs.getString("user_country")+"</td>"+
											"</tr>"+
											"<tr>"+
												"<td class=entity>Email </td><td>"+rs.getString("user_email")+"</td>"+
											"</tr>"+
										"</table>";
								
								if(id.equals(userid)==false){ // user looking at someone else profile
									s+=	"<div>"+
											"<span id=\"sendmessagespan\"></span></br>"+
											"<textarea class=\"form-control\" id=\"sendtextarea\" rows=\"4\" maxlength=\"200\" placeholder=\"write message here\" onkeyup=findMessageLength();></textarea>"+
											"<div class=\"b-save\"><button class=\"btn btn-success\" id=\"sendbutton\" onclick=sendMessage(\""+userid+"\",\""+id+"\"); ><i class=\"glyphicon glyphicon-send\"></i> Send message</button></div>"+
										"</div>";
								}
					
			} else{
				
				Q="select * from admin where admin_id='"+userid+"' limit 1";
				rs=smt.executeQuery(Q);
				if(rs.next()==true){
					hash=fob.md5Hex(rs.getString("admin_email"));
					
					s+="<div class=\"row\">"+
							"<div class=\"col-md-6 col-md-offset-3\">"+
								
								"<div class=\"page-header\">"+
									"<h4 class=\"pageheading\">Admin profile</h4>"+
								"</div>"+
								"<div class=\"row\">"+
									"<div class=\"col-md-3\">"+
										"<img class=\"img-thumbnail\" src=\"https://www.gravatar.com/avatar/"+hash+"?s=100.jpg\" title=\"Email Gravatar image\" />"+
									"</div>"+
									"<div class=\"col-md-9\">"+
										"<div class=\"t-lg t-bold t-grey\">"+rs.getString("admin_fname")+" "+rs.getString("admin_lname")+"</div>"+
										"<div class=\"t-sm t-gre blockquote\">"+rs.getString("admin_about")+"</div>"+
									"</div>"+
								"</div>"+
								"</br></br>"+
								"<div>"+
									"<a href=\"AskedQuestions?user="+userid+"\">Asked Questions</a>&nbsp;&nbsp;&nbsp;&nbsp;"+
									"<a href=\"MyAnswers?user="+userid+"\">My Answers</a>&nbsp;&nbsp;&nbsp;"+
									"<a href=\"ReadingList?user="+userid+"\">Reading List</a>"+
									"<div class=\"f-right\">"+
										"<a href=\""+rs.getString("facebook_link")+"\" target=\"_blank\"><i class=\"fa fa-facebook-official fa-2x\"></i></a>&nbsp;&nbsp;"+
										"<a href=\""+rs.getString("google_link")+"\" target=\"_blank\"><i class=\"fa fa-google-plus-official fa-2x i-red\"></i></a>&nbsp;&nbsp;"+
										"<a href=\""+rs.getString("github_link")+"\" target=\"_blank\"><i class=\"fa fa-github fa-2x i-black\"></i></a>&nbsp;&nbsp;"+
										"<a href=\""+rs.getString("quora_link")+"\" target=\"_blank\"><i class=\"fa fa-quora fa-2x i-red\"></i></a>"+
									"</div>"+
								"</div>"+
								"<table class=\"table table-bordered\">"+
									"<tr>"+
										"<td class=entity>Admin Id </td><td>"+rs.getString("admin_id")+"</td>"+
									"</tr>"+
									"<tr>"+
										"<td class=entity>Gender </td><td>"+rs.getString("admin_gender")+"</td>"+
									"</tr>"+
									"<tr>"+
										"<td class=entity>Country </td><td>"+rs.getString("admin_country")+"</td>"+
									"</tr>"+
								"</table>";
						
							s+=	"<div>"+
									"<span id=\"sendmessagespan\"></span></br>"+
									"<textarea class=\"form-control\" id=\"sendtextarea\" rows=\"4\" maxlength=\"200\" placeholder=\"write message here\" onkeyup=findMessageLength();></textarea>"+
									"<div class=\"b-save\"><button class=\"btn btn-success\" id=\"sendbutton\" onclick=sendMessage(\"admin123\",\""+id+"\"); ><i class=\"glyphicon glyphicon-send\"></i> Send message</button></div>"+
								"</div>";
					
				} else s+=		"<p class=\"t-lg t-red\">Error: 404 user profile not found</p>";
			}
			rs.close();smt.close();cn.close();
		}catch(Exception e){
			s+=					"<p style=\"font-size:14pt;color:red\">Error occured please try again</p>";
		}
		s+=					"</div>"+
						"</div>"+
					"</div>"+
				"</body>"+
			"</html>";		
		pr.println(s);
		pr.flush();
		
		
	}
}
