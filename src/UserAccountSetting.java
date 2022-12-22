

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class UserAccountSetting
 */
@WebServlet("/UserAccountSetting")
public class UserAccountSetting extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserAccountSetting() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String s="",Q,id="",fname,lname,gender,email,about,male="",female="",password,countries="",country,facebook,google,github,quora;
		boolean valid=true;
		PrintWriter pr=response.getWriter();
		HttpSession ses=request.getSession(false);
		Functions fob=new Functions(); fob.noCache(response);
		if(fob.validate(ses)==false) { response.sendRedirect("Login");return;}
		
		
		String Head,Navbar,who="";
		try{
			try{
				id=ses.getAttribute("user_id").toString();who="user";
			}catch(Exception p){
				id=ses.getAttribute("admin_id").toString();who="admin";
			}
		}catch(Exception e){ response.sendRedirect("Login");return;}
		
		if(who.equals("user")==false) valid=false;
		if(fob.validId(id)==false) valid=false;
		if(valid==false) { response.sendRedirect("Error");return;}
		
		Head=fob.head(id);
		Navbar=fob.navbar(ses);
		s=Head+Navbar;
		
		try{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection cn=DriverManager.getConnection("jdbc:mysql://localhost:3306/Qfire","root","qazzaq");
			Statement smt=cn.createStatement();
			Q="select * from users where user_id='"+id+"' limit 1";
			ResultSet rs=smt.executeQuery(Q);
			if(rs.next()){
				fname=rs.getString("user_fname");
				lname=rs.getString("user_lname");
				password=rs.getString("user_pass");
				gender=rs.getString("user_gender");
				email=rs.getString("user_email");
				about=rs.getString("user_about");
				country=rs.getString("user_country");
				facebook=rs.getString("facebook_link");
				google=rs.getString("google_link");
				github=rs.getString("github_link");
				quora=rs.getString("quora_link");
				
				
				rs=smt.executeQuery("select name from countries");
				while(rs.next()){
					if(country.equals(rs.getString("name"))==true) countries+="<option selected>"+rs.getString("name")+"</option>";
					else countries+="<option>"+rs.getString("name")+"</option>";
				}
				
				if(gender.equals("Male")) male="checked=\"checked\"";
				else female="checked=\"checked\"";
				
				s+=
							"<div class=\"container\" id=\"bodycontainer\">"+
								"<div class=\"row\">"+
									"<div class=\"col-md-6 col-md-offset-3\">"+
										"<div class=\"page-header\">"+
											"<h4 class=\"pageheading\">Account settings</h4>"+
										"</div>"+
										"<div>"+
											"<table id=\"accountsettingtable\">"+
												"<tr><th></th><th style=\"width:70%;\"></th><tr>"+
												"<tr>"+
													"<td>User id </td><td><input class=\"form-control\" type=\"text\" id=\"userid\" name=\"id\" readonly value=\""+id+"\"></td>"+
												"</tr>"+
												"<tr>"+
													"<td>First name </td><td><input class=\"form-control\" type=\"text\" id=\"userfirstname\" name=\"fname\" value=\""+fname+"\" onkeyup=checkName(\"user\");></td>"+
												"</tr>"+
												"<tr>"+
													"<td>Last name </td><td><input class=\"form-control\" type=\"text\" id=\"userlastname\" name=\"lname\" value=\""+lname+"\" onkeyup=checkName(\"user\");></td>"+
												"</tr>"+
												"<tr>"+
													"<td>Password </td><td><input class=\"form-control\" type=\"password\" id=\"usernewpassword\" name=\"password\" value=\""+password+"\" onkeyup=checkChangePassword(\"user\");></td>"+
												"</tr>"+
												"<tr>"+
													"<td>Confirm password </td><td><input class=\"form-control\" type=\"password\" id=\"userconfirmpassword\" name=\"confirmpassword\" value=\""+password+"\" onkeyup=checkChangePassword(\"user\");></td>"+
												"</tr>"+
												"<tr>"+
													"<td>Gender </td><td><input type=\"radio\" id=\"male\" name=\"gender\" "+male+" value=\"Male\"> Male <input type=\"radio\" id=\"female\" "+female+" value=\"Female\" name=\"gender\"> Female</td>"+
												"</tr>"+
												"<tr>"+
													"<td>Email </td><td><input class=\"form-control\" type=\"text\" id=\"email\" name=\"email\" value=\""+email+"\" onkeyup=checkEmail();><span id=\"emailspan\"></span></td>"+
												"</tr>"+
												"<tr>"+
													"<td>Country </td><td><select class=\"form-control\" id=\"country\" name=\"country\">"+countries+"</select></td>"+
												"</tr>"+
												"<tr>"+
													"<td>Facebook </td><td><input class=\"form-control\" type=\"text\" id=\"facebook\" name=\"facebook\" value=\""+facebook+"\"></td>"+
												"</tr>"+
												"<tr>"+
													"<td>Google+</td><td><input class=\"form-control\" type=\"text\" id=\"google\" name=\"google\" value=\""+google+"\"></td>"+
												"</tr>"+
												"<tr>"+
													"<td>Github </td><td><input class=\"form-control\" type=\"text\" id=\"github\" name=\"github\" value=\""+github+"\"></td>"+
												"</tr>"+
												"<tr>"+
													"<td>Quora </td><td><input class=\"form-control\" type=\"text\" id=\"quora\" name=\"quora\" value=\""+quora+"\"></td>"+
												"</tr>"+
												"<tr>"+
													"<td>About </td><td><span id=\"aboutmespan\"></span></br><textarea class=\"form-control\" id=\"about\" name=\"about\" rows=\"3\"  maxlength=\"200\" onkeyup=aboutMe(); placeholder=\"write about you\">"+about+"</textarea></td>"+
												"</tr>"+
												"<tr>"+
													"<td></td><td><button class=\"btn btn-success b-save\" id=\"useraccountsetting\" onclick=accountSetting(\"user\");><i class=\"glyphicon glyphicon-save\"></i> save</button> <span id=\"submitbuttonspan\"></span></td>"+
												"</tr>"+
											"</table>"+
											"<span class=\"t-blue t-lg\">Unable to change Email Id?</span></br>"+
											"<div style=\"width:76%\">"+
												"<input type=\"text\" id=\"verifyemail\" class=\"form-control\" maxlength=\"45\" placeholder=\"Enter email id to verify\">"+
												"<div class=\"b-save\"><button class=\"btn btn-info\" id=\"verifyemailbutton\" onclick=verifyEmail(\""+id+"\");><i class=\"glyphicon glyphicon-ok\"></i> Verify</button></div>"+
												"<span id=\"verifyemailspan\"></span>"+
											"</div>"+
										"</div>"+
									"</div>"+
								"</div>"+
							"</div>"+
						"</body>"+
					"</html>";
			}
			rs.close();smt.close();cn.close();
		}catch(Exception e){
			
		}
		pr.println(s);
		pr.flush();
	}
}
