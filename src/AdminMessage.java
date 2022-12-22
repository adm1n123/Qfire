

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
 * Servlet implementation class AdminMessage
 */
@WebServlet("/AdminMessage")
public class AdminMessage extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AdminMessage() {
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
		
		String s="",Q,id,who;
		boolean valid=true;
		
		try{
			try{
				id=ses.getAttribute("user_id").toString();who="user";
			}catch(Exception p){
				id=ses.getAttribute("admin_id").toString();who="admin";
			}
		}catch(Exception e){ response.sendRedirect("Login");return;}
		
		if(fob.validId(id)==false) valid=false;
		if(who.equals("admin")==false) valid=false;
		if(valid==false){
			response.sendRedirect("Login");return;
		}
		
		s=fob.head(id)+fob.navbarAdmin(ses)+
				"<div class=\"container\" id=\"bodycontainer\">"+
					"<div class=\"row\">"+
						"<div class=\"col-md-6 col-md-offset-3\">"+
							"<div class=\"page-header\">"+
								"<h4 class=\"pageheading\">Admin Messages</h4>"+
							"</div>"+
							"<div>";
		
		try{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection cn=DriverManager.getConnection("jdbc:mysql://localhost:3306/Qfire","root","qazzaq");
			Statement smt=cn.createStatement();
			Q="select * from admin_messages";
			ResultSet rs=smt.executeQuery(Q);
			s+=
								"<button class=\"deleteallbutton f-right\" id=deletealladminmessage onclick=deleteadminmessage(\"all\");>Delete all</button></br></br>"+
								"<table class=\"table table-striped table-hover\">"+
									"<tr><th>ID</th><th>Type</th><th>sender</th><th>Date</th><th>Message</th><th>Action</th></tr>";
			while(rs.next()){
						
					s+="<tr>"+
							"<td>"+rs.getString("id")+"</td>"+
							"<td>"+rs.getString("type")+"</td>"+
							"<td><a href=Profile?id="+rs.getString("sender")+">"+rs.getString("sender")+"</a></td>"+ 
							"<td>"+rs.getString("datetime")+"</td>"+
							"<td>"+rs.getString("message")+"</td>"+
							"<td><button class=\"btn btn-default\" id="+rs.getString("id")+" onclick=deleteadminmessage(\""+rs.getString("id")+"\");>Delete</button></td>"+
						"</tr>";
				}
				s+=		"</table>";
			rs.close();smt.close();cn.close();
		}catch(Exception e){
			s+="<p style=\"font-size:14pt;color:red;\">Error occured please try again</p>";
		}
		s+=					"</div>"+
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
		PrintWriter pr=response.getWriter();
		HttpSession ses=request.getSession(false);
		Functions fob=new Functions();
		if(fob.validate(ses)==false) { response.sendRedirect("Login");return;}
		
		String s="Failed",Q,id,who,oid;
		boolean valid=true;
		
		try{
			try{
				id=ses.getAttribute("user_id").toString();who="user";
			}catch(Exception p){
				id=ses.getAttribute("admin_id").toString();who="admin";
			}
		}catch(Exception e){ response.sendRedirect("Login");return;}
		
		
		oid=request.getParameter("id");
		if(fob.validText(oid)==false) valid=false;
		if(fob.validId(id)==false) valid=false;
		if(who.equals("admin")==false) valid=false;
		if(valid==false){
			response.sendRedirect("Login");return;
		}
		
		
		try{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection cn=DriverManager.getConnection("jdbc:mysql://localhost:3306/Qfire","root","qazzaq");
			Statement smt=cn.createStatement();
			
			if(oid.equals("all")==true){
				Q="delete from admin_messages";
				smt.executeUpdate(Q);
				s="Success";
			} else if(fob.validNumber(oid)==true){
				Q="delete from admin_messages where id="+oid;
				smt.executeUpdate(Q);
				s="Success";
			}
			smt.close();cn.close();
		}catch(Exception e){}
		pr.println(s);
		pr.flush();
	}
}