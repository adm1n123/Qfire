

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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
 * Servlet implementation class SignUp
 */
@WebServlet("/SignUp")
public class SignUp extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SignUp() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String s,head,Q,country="",id="";
		PrintWriter pr=response.getWriter();
		Functions fob=new Functions();
		
		try{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection cn=DriverManager.getConnection("jdbc:mysql://localhost:3306/Qfire","root","qazzaq");
			Statement smt=cn.createStatement();
			Q="select * from countries order by name";
			ResultSet rs=smt.executeQuery(Q);
			while(rs.next()){
				country+="<option>"+rs.getString("name")+"</option>";
			}
			rs.close();smt.close();cn.close();
		}catch(Exception e){
			pr.println("<html>Error occured <a href=SignUp> nbsp;nbsp;try again</a></html>");
		}
		
		head=fob.head(id);
		s=head+	"<div class=\"jumbotron\">"+
					"<div>"+
						"<img style=\"display: block;margin-left: auto;margin-right:auto;\" class=\"img-responsive\" src=\"Images/Qfire_logo.png\" alt=\"Qfire\" />"+
					"</div>"+
				"</div>"+
				"<div class=\"container\">"+
					"<div class=\"row\">"+
						"<div class=\"col-md-6 col-md-offset-3 col-sm-8 col-sm-offset-2\">"+
							"<div class=\"panel panel-success\">"+
								"<div class=\"panel-heading\">Sign Up</div>"+
								"<div class=\"panel-body\">"+
									"<form id=\"signupform\" action=\"SignUp\" method=\"post\">"+
										"<div class=\"form-group has-feedback\">"+
											"<table class=\"table\" id=\"signuptable\">"+
												"<tr>"+
													"<td>"+
														"<div class=\"input-group\">"+
															"<span class=\"input-group-addon\"><i class=\"fa fa-user-circle\"></i></span>"+
															"<input class=\"form-control input-sm\" type=\"text\" id=\"id\" name=\"id\" maxlength=\"45\" onkeyup=checkIdAvailable(); placeholder=\"user id\">"+
														"</div>"+
													"</td>"+
													"<td><span id=\"idspan\"></span></td>"+
												"</tr>"+
												"<tr>"+
													"<td>"+
														"<div class=\"input-group\">"+
															"<span class=\"input-group-addon\"><i class=\"glyphicon glyphicon-envelope\"></i></span>"+
															"<input class=\"form-control input-sm\" type=\"text\" id=\"email\" name=\"email\" maxlength=\"45\" onkeyup=checkEmail(); placeholder=\"email\">"+
														"</div>"+
													"</td>"+
													"<td><span id=\"emailspan\">&nbsp;</span></td>"+
												"</tr>"+
												"<tr>"+
													"<td>"+
														"<div class=\"input-group\">"+
															"<span class=\"input-group-addon\"><i class=\"glyphicon\">F</i></span>"+
															"<input class=\"form-control input-sm\" type=\"text\" id=\"userfirstname\" name=\"fname\" maxlength=\"45\" onkeyup=checkName(\"user\"); placeholder=\"First name\">"+
														"</div>"+
													"</td>"+
													"<td>"+
														"<div class=\"input-group\">"+
															"<span class=\"input-group-addon\"><i class=\"glyphicon\">L</i></span>"+
															"<input class=\"form-control input-sm\" type=\"text\" id=\"userlastname\" name=\"lname\" maxlength=\"45\" onkeyup=checkName(\"user\"); placeholder=\"Last name\">"+
														"</div>"+
													"</td>"+
												"</tr>"+
												"<tr>"+
													"<td>"+
														"<div class=\"input-group\">"+
															"<span class=\"input-group-addon\"><i class=\"glyphicon glyphicon-lock\"></i></span>"+
															"<input class=\"form-control input-sm\" type=\"password\" id=\"usernewpassword\" name=\"password\" maxlength=\"45\" onkeyup=checkChangePassword(\"user\"); placeholder=\"Password\">"+
														"</div>"+
													"</td>"+
													"<td>"+
														"<div class=\"input-group\">"+
															"<span class=\"input-group-addon\"><i class=\"glyphicon glyphicon-lock\"></i></span>"+
															"<input class=\"form-control input-sm\" type=\"password\" id=\"userconfirmpassword\" name=\"confirmpassword\" maxlength=\"45\" onkeyup=checkChangePassword(\"user\"); placeholder=\"Confirm password\">"+
														"</div>"+
													"</td>"+
												"</tr>"+
												"<tr>"+
													"<td class=\"entity\">Gender </td><td><input type=\"radio\"  name=\"gender\" value=\"Male\" checked=\"checked\"> Male &nbsp;<input type=\"radio\" value=\"Female\" name=\"gender\"> Female</td>"+
												"</tr>"+
												"<tr>"+
													"<td class=\"entity\">Country </td><td><select class=\"form-control input-sm\" name=\"country\">"+country+"</select></td>"+
												"</tr>"+
												"<tr>"+
													"<td class=\"entity\">About </td><td><textarea type=\"text\" class=\"form-control input-sm\" id=\"about\" name=\"about\" rows=\"2\" maxlength=\"200\" onkeyup=aboutMe(); placeholder=\"few lines about you\"></textarea></td>"+
												"</tr>"+
												"<tr>"+
													"<td>already have account?<a href=\"Login\"> Login</a></td><td><input class=\"btn btn-success\" type=\"button\" onclick=confirmDetails(); value=\"Sign Up\"> <button class=\"btn btn-danger\" type=\"reset\"><i class=\"fa fa-refresh\"></i> Reset</button></td>"+
												"</tr>"+
											"</table>"+
										"</div>"+
									"</form>"+
								"</div>"+
							"</div>"+
						"</div>"+
					"</div>"+
				"</div>"+
			"</body>"+
		"</html>";
		pr.println(s);
		pr.flush();
		
	}
	
	
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		Functions fob=new Functions(); fob.noCache(response);
		String s,id,fname,lname,password,confirmpassword,email,country,about,gender,message,notification;
		boolean valid=true;
		PrintWriter pr=response.getWriter();
		id=request.getParameter("id");
		fname=request.getParameter("fname");
		lname=request.getParameter("lname");
		password=request.getParameter("password");
		confirmpassword=request.getParameter("confirmpassword");
		email=request.getParameter("email");
		country=request.getParameter("country");
		about=request.getParameter("about");
		gender=request.getParameter("gender");
		if(id.toLowerCase().equals("admin")==true) valid=false;
		if(fob.validId(id)==false) valid=false;
		if(fob.validName(fname)==false) valid=false;
		if(fob.validName(lname)==false) valid=false;
		if(fob.validPassword(password)==false) valid=false;
		if(fob.validPassword(confirmpassword)==false) valid=false;
		if(fob.validEmail(email)==false) valid=false;
		if(fob.validName(country)==false) valid=false;
		if(fob.validText(about)==false) valid=false;
		if(fob.validName(gender)==false) valid=false;
		if(password.equals(confirmpassword)==false) valid=false;
		if(valid==false){
			response.sendRedirect("Error");return;
		}
		else{
			try{
				Class.forName("com.mysql.jdbc.Driver").newInstance();
				Connection cn=DriverManager.getConnection("jdbc:mysql://localhost:3306/Qfire","root","qazzaq");
				Statement smt=cn.createStatement();
				PreparedStatement psmt;
				ResultSet rs;
				if(fob.noOfAlphabet(about)<2) about="I am Qfire user :)";
				rs=smt.executeQuery("select * from countries where name='"+country+"'");
				if(rs.next()==false) country="India";
				rs=smt.executeQuery("select * from admin where admin_id='"+id+"' limit 1");
				if(rs.next()==true){
					pr.println("<html>Registration failed <a href=SignUp>try again</a></html>");return;
				}
				rs=smt.executeQuery("select * from users where user_email='"+email+"' limit 1");
				if(rs.next()==true){
					pr.println("<html>Registration failed Email id already exist<a href=SignUp>try again</a></html>");return;
				}
				rs=smt.executeQuery("select * from users where user_id='"+id+"' limit 1");
				if(rs.next()==true){
					pr.println("<html>Registration failed <a href=SignUp>try again</a></html>");return;
				}
				psmt=cn.prepareStatement("insert into users (user_id,user_fname,user_lname,user_pass,user_email,user_country,user_about,user_gender,facebook_link,google_link,github_link,quora_link) values('"+id+"','"+fname+"','"+lname+"','"+password+"','"+email+"','"+country+"',?,'"+gender+"','#','#','#','#')");
				psmt.setString(1,about);
				psmt.executeUpdate();
				psmt.close();
				
				///////// sending mail ////////////
				String to,content,subject,from,fpassword,url,key;
				do{
					key=fob.nextKey();
					rs=smt.executeQuery("select * from verify_email where verify_key='"+key+"'");
				}while(rs.next()==true);
				from="infoQfire@gmail.com";
				fpassword="DB@admin123.com";
				to=email;
				subject="Email Verification -Qfire";
				url="http://localhost:8080/Qfire/VerifyEmail?userid="+id+"&email="+email+"&key="+key;
				content=
						"Hello, <b><i>"+id+"</i></b></br> Welcome to <b><i>Qfire</i></b>"+
						" this is email verification link of your <b><i>Qfire</i></b> account. "+
						"</br>click on link below if unable to click please paste this link on URL and hit Enter </br><a href=\""+url+"\">"+url+"</a>"+
						"</br></br><b> This is one time email verification link</b>";
				s=fob.sendMail(from, fpassword, to, subject, content);
				if(s.equals("Success")==true){
					s="Failed";
					smt.executeUpdate("insert into verify_email values (null,'"+id+"','"+key+"')");
					s="Success";
				}
				message="<span class=\"t-grey t-lg\"> Email verification link has been sent to address:<span class=\"t-link\"> "+email+" </span>please verify</span>";
				s=fob.login(id, password, request);
				if(s.equals("User")==true){
					try{
							//////////////// sending message //////////////////////
								smt.executeUpdate("insert into inbox(sender,receiver,message,status,datetime) values('admin123','"+id+"','your unread messages appear here',0,now())");
								smt.executeUpdate("insert into inbox(sender,receiver,message,status,datetime) values('admin123','"+id+"','and read messages here',1,now())");
								smt.executeUpdate("insert into outbox(sender,receiver,message,datetime) values('"+id+"','receiver','sent messages appear here',now())");
							//////// sending notification //////////////////////
							HashSet<String> idset=new HashSet<String>();
							notification="<span>Hello, "+fname+" "+lname+" welcome on <span class=\"t-bold\">Qfire</span> just fire any query a large group of people is here to handle it. <a href=\"Profile?id=admin123\"><b>admin123</b></a> <span class=\"t-grey t-sx\"> "+fob.date()+"</span></span>";
							idset.add(id);
							fob.sendNotification(idset, notification);
					}catch(Exception e){}
					
					request.setAttribute("message", message);
					request.getRequestDispatcher("UserHome").forward(request, response);
				} else{
					response.sendRedirect("Login");
				}
				rs.close();smt.close();cn.close();
			}catch(Exception e){
				request.setAttribute("error", "Registration failed <a href=SignUp> try again</a>");
				request.getRequestDispatcher("Error").forward(request, response);
			}
		}
		pr.flush();
	}
}
