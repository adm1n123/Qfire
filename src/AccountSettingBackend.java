

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class AccountSettingBackend
 */
@WebServlet("/AccountSettingBackend")
public class AccountSettingBackend extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AccountSettingBackend() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		PrintWriter pr=response.getWriter();
		HttpSession ses=request.getSession(false);
		Functions fob=new Functions();
		if(fob.validate(ses)==false){ response.sendRedirect("Login");return;}
		
		String s="",who,id="",user_id,fname,lname,pass,confirmpass,email,oldemail,gender,country,about,facebook,google,github,quora;
		boolean valid=true;
		try{
			try{
				id=ses.getAttribute("user_id").toString();who="user";
			}catch(Exception p){
				id=ses.getAttribute("admin_id").toString();who="admin";
			}
		}catch(Exception e){ response.sendRedirect("Login");return;}
		
		try{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection cn=DriverManager.getConnection("jdbc:mysql://localhost:3306/Qfire","root","qazzaq");
			Statement smt=cn.createStatement();
			PreparedStatement psmt;
			ResultSet rs;;
			if(request.getParameter("who").equals(who)==false) valid=false;

			user_id=request.getParameter("id");
			fname=request.getParameter("fname");
			lname=request.getParameter("lname");
			pass=request.getParameter("password");
			confirmpass=request.getParameter("confirmpassword");
			email=request.getParameter("email");
			gender=request.getParameter("gender");
			country=request.getParameter("country");
			about=request.getParameter("about");
			facebook=request.getParameter("facebook");
			google=request.getParameter("google");
			github=request.getParameter("github");
			quora=request.getParameter("quora");
			if(fob.validId(user_id)==false) valid=false;
			if(id.equals(user_id)==false) valid=false;
			if(fob.validName(fname)==false) valid=false;
			if(fob.validName(lname)==false) valid=false;
			if(fob.validPassword(pass)==false) valid=false;
			if(fob.validPassword(confirmpass)==false) valid=false;
			if(fob.validEmail(email)==false) valid=false;
			if(fob.validText(about)==false) valid=false;
			if(fob.validName(gender)==false) valid=false;
			if(fob.validName(country)==false) valid=false;
			if(fob.validLink(facebook)==false) valid=false;
			if(fob.validLink(google)==false) valid=false;
			if(fob.validLink(github)==false) valid=false;
			if(fob.validLink(quora)==false) valid=false;
			
			if(pass.equals(confirmpass)==false) valid=false;
			if(valid==false){ 
				pr.println("I");
				pr.flush();
				smt.close();cn.close();
				return;
			}
			
			if(who.equals("user")==true){
				
				rs=smt.executeQuery("select * from users where user_id='"+id+"'");
				rs.next();
				oldemail=rs.getString("user_email");
				if(email.equalsIgnoreCase(oldemail)==false){
					rs=smt.executeQuery("select * from users where user_email='"+email+"'");
					if(rs.next()){
						pr.println("E");
						rs.close();smt.close();cn.close();
						return;
					}
					rs=smt.executeQuery("select * from admin where admin_email='"+email+"'");
					if(rs.next()){
						pr.println("E");
						rs.close();smt.close();cn.close();
						return;
					}
				}
				psmt=cn.prepareStatement("update users set user_fname='"+fname+"', user_lname='"+lname+"', user_pass='"+pass+"', user_email='"+email+"', user_gender='"+gender+"', user_country='"+country+"', user_about=?, facebook_link=?, google_link=?, github_link=?,quora_link=? where user_id='"+id+"'");
				psmt.setString(1, about);
				psmt.setString(2, facebook);
				psmt.setString(3, google);
				psmt.setString(4, github);
				psmt.setString(5, quora);
				psmt.executeUpdate();
				pr.println("Success");
				rs.close();psmt.close();
			} else if(who.equals("admin")){
				rs=smt.executeQuery("select * from admin where admin_id='"+id+"'");
				rs.next();
				oldemail=rs.getString("admin_email");
				if(email.equalsIgnoreCase(oldemail)==false){
					rs=smt.executeQuery("select * from users where user_email='"+email+"'");
					if(rs.next()){
						pr.println("E");
						rs.close();smt.close();cn.close();
						return;
					}
					rs=smt.executeQuery("select * from admin where admin_email='"+email+"'");
					if(rs.next()){
						pr.println("E");
						rs.close();smt.close();cn.close();
						return;
					}
				}
				psmt=cn.prepareStatement("update admin set admin_fname='"+fname+"', admin_lname='"+lname+"', admin_pass='"+pass+"', admin_email='"+email+"', admin_gender='"+gender+"', admin_country='"+country+"', admin_about=?, facebook_link=?, google_link=?, github_link=?,quora_link=? where admin_id='"+id+"'");
				psmt.setString(1, about);
				psmt.setString(2, facebook);
				psmt.setString(3, google);
				psmt.setString(4, github);
				psmt.setString(5, quora);
				psmt.executeUpdate();
				pr.println("Success");
				rs.close();psmt.close();
			}
		}catch(Exception e){
			pr.println("failed");
		}
		pr.flush();
	}
}