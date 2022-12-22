

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.EmptyStackException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class Outbox
 */
@WebServlet("/Outbox")
public class Outbox extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Outbox() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String s="",id="",Q,messageid,receiver;
		boolean valid=true;
		int totalcount=0;
		PrintWriter pr=response.getWriter();
		HttpSession ses=request.getSession(false);
		Functions fob=new Functions(); fob.noCache(response);
		if(fob.validate(ses)==false) { response.sendRedirect("Login");return;}
		
		String Head="",Navbar,who="",name="",date="",st[];
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
		
		try{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection cn=DriverManager.getConnection("jdbc:mysql://localhost:3306/Qfire","root","qazzaq");
			Statement smt=cn.createStatement();
			Q="select * from outbox where sender='"+id+"' order by datetime desc";
			ResultSet rs=smt.executeQuery(Q);
			while(rs.next()){
				totalcount++;
			}
			rs.beforeFirst();
			s+=
						"<div class=\"container\" id=\"bodycontainer\">"+
							"<div class=\"row\">"+
								"<div class=\"col-md-6 col-md-offset-3\">"+
									"<div class=\"page-header\">"+
										"<h4 class=\"pageheading\">Outbox</h4>"+
									"</div>"+
									"<div>"+
										"<button id=\"deleteall\" class=\"btn btn-danger f-right\" onclick=deleteAllMessages(\""+id+"\",\"outbox\");><i class=\"glyphicon glyphicon-trash\"></i> Delete all &nbsp;<span class=\"b-count\" id=\"totalcountspan\">"+totalcount+"</span></button>"+
										"<div class=\"clearfix\"></div></br>"+
										"<div>"+
											"<div class=\"panel panel-default\">" + 
												"<div class=\"panel-heading\"><span id=\"span\" class=\"s-p-heading\">Sent messages</span></div>"+ 
												"<div class=\"panel-body\">"+
													"<table id=\"outboxtable\" class=\"table table-striped table-hover\">";
			while(rs.next()==true){
				messageid=rs.getString("id");
				receiver=rs.getString("receiver");
				date=fob.date(rs.getString("datetime"));
				s+=										"<tr>"+
															"<td>"+
																"<button class=\"btnCustom btn-Custom-default b-s-delete f-right\" id=\"delete"+messageid+"\" onclick=deleteMessage(\""+messageid+"\",\""+id+"\",\"outbox\");><i class=\"fa fa-close fa-lg i-red\"></i></button>"+
																"<a href=\"Profile?id="+receiver+"\">@"+receiver+"</a> <span class=\"t-grey t-xs\">"+date+"</span></br>"+
																"<span>"+rs.getString("message")+"</span>"+
															"</td>"+
														"</tr>";
			}
			s+=	 									 "</table>"+
												"</div>"+
											"</div>"+
										"</div>"+
									"</div>"+
								"</div>"+
							"</div>"+
						"</div>"+
					"</body>"+
				"</html>";
			rs.close();smt.close();cn.close();
		}catch(Exception e){}
		pr.println(s);
		pr.flush();
	}
}
