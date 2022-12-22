

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class ResetPassword
 */
@WebServlet("/ResetPassword")
public class ResetPassword extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ResetPassword() {
        super();
        // TODO Auto-generated constructor stub
    }
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException { // reset from email or send mail to reset
		// TODO Auto-generated method stub
		PrintWriter pr=response.getWriter();
		Functions fob=new Functions();
		String key,userid,email,s="",error;
		key=request.getParameter("key");
		email=request.getParameter("email");
		userid=request.getParameter("userid");
	
		/////////////////////////////  process reset link from email ////////////////////////////////////////////
		if(key!=null&&userid!=null&&fob.validKey(key)&&fob.validId(userid)){
			try{
				Class.forName("com.mysql.jdbc.Driver").newInstance();
				Connection cn=DriverManager.getConnection("jdbc:mysql://localhost:3306/Qfire","root","qazzaq");
				Statement smt=cn.createStatement();
				ResultSet rs;
				smt.executeUpdate("delete from reset_session where user_id='"+userid+"'");
				rs=smt.executeQuery("select * from reset_password where user_id='"+userid+"' and reset_key='"+key+"' limit 1");
				cn.setAutoCommit(false);
				if(rs.next()){
					smt.executeUpdate("delete from reset_password where user_id='"+userid+"'");
					HttpSession ses=request.getSession(true);
					do{
						key=fob.nextKey();
						rs=smt.executeQuery("select * from reset_session where reset_key='"+key+"' limit 1");
					}while(rs.next()==true);
					smt.executeUpdate("insert into reset_session values (null,'"+userid+"','"+key+"')");
					ses.setAttribute("userid", userid);
					ses.setAttribute("key", key);
					cn.commit();rs.close();smt.close();cn.close();
					response.sendRedirect("ResetPassword"); return;
				} else{
					smt.executeUpdate("delete from reset_password where reset_key='"+key+"'");
					s="<span class=\"t-red\">Invalid Link</span>";
					cn.commit();
				}
				cn.rollback();rs.close();smt.close();cn.close();
			}catch(Exception e){
				s="<span class=\"t-red\">Error occured please try again</span>";
			}
			request.setAttribute("error", s);
			request.getRequestDispatcher("Error").forward(request, response); return;
		}
		
		/////////////////////  send reset link  ////////////////////////////////////////////////
		String send;
		send=request.getParameter("send");

		if(send!=null&&send.equals("true")==true&&email!=null&&fob.validEmail(email)==true){
			try{
				Class.forName("com.mysql.jdbc.Driver").newInstance();
				Connection cn=DriverManager.getConnection("jdbc:mysql://localhost:3306/Qfire","root","qazzaq");
				Statement smt=cn.createStatement();
				ResultSet rs;
				rs=smt.executeQuery("select * from users where user_email='"+email+"' limit 1");
				if(rs.next()){
					userid=rs.getString("user_id");
					smt.executeUpdate("delete from reset_password where user_id='"+userid+"'");
					do{
						key=fob.nextKey();
						rs=smt.executeQuery("select * from reset_password where reset_key='"+key+"' limit 1");
					}while(rs.next()==true);
					///////// sending mail ////////////
					String to,content,subject,from,password,url;
					from="infoQfire@gmail.com";
					password="DB@admin123.com";
					to=email;
					subject="Reset Password - Qfire";
					url="http://localhost:8080/Qfire/ResetPassword?userid="+userid+"&key="+key;
					content="<html><body>"+
							"Hello, <b><i>"+userid+"</i></b></br> Someone has created password"+
							" reset link of your <b><i>Qfire</i></b> account if it was not you please ignore. "+
							"</br>To reset password click on link below if unable to click please paste this link on URL and hit Enter </br><a href=\""+url+"\">"+url+"</a>"+
							"</br></br><b> This is one time password reset link</b></body></html>";
					s=fob.sendMail(from, password, to, subject, content);
					if(s.equals("Success")==true){
						s="Failed";
						smt.executeUpdate("insert into reset_password values (null,'"+userid+"','"+key+"')");
						s="Success";
					}
				}
				else s="NotExist";
				rs.close();smt.close();cn.close();
			}catch(Exception e){
				s="Failed";
			}
			pr.println(s);
			pr.flush();
			return;
		}
		
		////////////////////////////  Reset password page ////////////////////////////
		if(request.getAttribute("error")==null) error="";
		else error=request.getAttribute("error").toString();
		
		s=fob.head("")+
						"<div class=\"jumbotron\">"+
							"<div>"+
								"<img style=\"display: block;margin-left: auto;margin-right:auto;\" class=\"img-responsive\" src=\"Images/Qfire_logo.png\" alt=\"Qfire\" />"+
							"</div>"+
						"</div>"+
						"<div class=\"container\">"+
							"<div class=\"row\">"+
								"<div class=\"col-md-6 col-md-offset-3 col-sm-8 col-sm-offset-2\">"+
									"<div class=\"panel panel-info\">"+
										"<div class=\"panel-heading\">Reset Password</div>"+
										"<div style=\"padding-top:40px\" class=\"panel-body\">"+
											"<form action=\"ResetPassword\" method=\"post\">"+
												"<div class=\"form-group\">"+
													"<div class=\"input-group\">"+
														"<span class=\"input-group-addon\"><i class=\"glyphicon glyphicon-lock\"></i></span>"+
														"<input class=\"form-control\" type=\"password\" onkeyup=checkChangePassword(\"user\"); id=\"password\" name=\"password\" placeholder=\"Password\">"+
													"</div>"+
												"</div>"+
												"<div class=\"form-group\">"+
													"<div class=\"input-group\">"+
														"<span class=\"input-group-addon\"><i class=\"glyphicon glyphicon-lock\"></i></span>"+
														"<input class=\"form-control\" type=\"password\" onkeyup=checkChangePassword(\"user\"); id=\"confirmpassword\" name=\"confirmpassword\" placeholder=\"Confirm Password\">"+
													"</div>"+
												"</div>"+
												"<div class=\"form-group\">"+
													"<span>"+error+"&nbsp;</span>"+
												"</div>"+
												"<div class=\"form-group\" style=\"float:right\" >"+
													"<button type=\"submit\" class=\"btn btn-success\"><span class=\"glyphicon glyphicon-ok\"></span> Change</button> "+
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
		HttpSession ses=request.getSession(false);
		String password,confirmpassword,userid="",message,key;
		Functions fob=new Functions();
		String s;
		//////////////// verification //////////////////
		try{
			userid=ses.getAttribute("userid").toString();
			key=ses.getAttribute("key").toString();
			boolean valid=true;
			if(fob.validId(userid)==false) valid=false;
			if(fob.validKey(key)==false) valid=false;
			if(valid==false){ response.sendRedirect("Login"); return;}
			
			try{
				Class.forName("com.mysql.jdbc.Driver").newInstance();
				Connection cn=DriverManager.getConnection("jdbc:mysql://localhost:3306/Qfire","root","qazzaq");
				Statement smt=cn.createStatement();
				ResultSet rs=smt.executeQuery("select * from reset_session where user_id='"+userid+"' and reset_key='"+key+"' limit 1");
				if(rs.next()==false){
					smt.executeUpdate("delete from reset_session where user_id='"+userid+"'");
					ses.removeAttribute("userid");
					ses.removeAttribute("key");
					ses.invalidate();
					response.sendRedirect("Login");
					rs.close();smt.close();cn.close(); return;
				}
				rs.close();smt.close();cn.close();
			}catch(Exception e){
				doGet(request, response); return;
			}
		}catch(Exception e){
			response.sendRedirect("Login");	return;
		}
		
		///////////// change password ///////////////////
		
		password=request.getParameter("password");
		confirmpassword=request.getParameter("confirmpassword");
		
		
		if(password!=null&&confirmpassword!=null&&fob.validPassword(password)==true&&password.equals(confirmpassword)==true&&fob.validId(userid)==true){
			try{
				Class.forName("com.mysql.jdbc.Driver").newInstance();
				Connection cn=DriverManager.getConnection("jdbc:mysql://localhost:3306/Qfire","root","qazzaq");
				Statement smt=cn.createStatement();
				smt.executeUpdate("update users set user_pass='"+password+"' where user_id='"+userid+"' limit 1");
				smt.executeUpdate("delete from reset_session where user_id='"+userid+"'");
				ses.removeAttribute("userid");
				ses.removeAttribute("key");
				ses.invalidate();
				smt.close();cn.close();
				message="<span class=\"t-blue t-lg\">Password has been changed successfully</span>";
				s=fob.login(userid, password, request);
				if(s.equals("User")==true){
					request.setAttribute("message", message);
					request.getRequestDispatcher("UserHome").forward(request, response); return;
				} else{
					response.sendRedirect("Login"); return;
				}
			}catch(Exception e){
				request.setAttribute("error", "<span class=\"t-red\">Error occured try again</span>");
				doGet(request,response); return;
			}
		}
		request.setAttribute("error", "<span class=\"t-red\">Invalid input</span>");
		doGet(request,response);
	}
}
