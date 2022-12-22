

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
 * Servlet implementation class Inbox
 */
@WebServlet("/Inbox")
public class Inbox extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Inbox() {
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
		
		String s="",id="",Q,read,unread;
		int unreadcount=0,readcount=0;
		boolean valid=true;
		String who="";
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
		else if(who.equals("admin")==true){
			s=fob.head(id)+fob.navbarAdmin(ses);
			id="admin123";
		}
		
		
		try{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection cn=DriverManager.getConnection("jdbc:mysql://localhost:3306/Qfire","root","qazzaq");
			Statement smt=cn.createStatement();
			ResultSet rs;
			
			Q="select * from inbox where receiver='"+id+"' and status=1 order by datetime desc"; // read messages
			rs=smt.executeQuery(Q);
			while(rs.next()) readcount++;
			read=fob.inboxReadMessages(id, rs);
			
			Q="select * from inbox where (receiver='"+id+"' and status=0) order by datetime desc"; //number of unread messages
			rs=smt.executeQuery(Q);
			while(rs.next()) unreadcount++;
			unread=fob.inboxUnreadMessages(id, rs);
			
			s+=
						"<div class=\"container\" id=\"bodycontainer\">"+
							"<div class=\"row\">"+
								"<div class=\"col-md-6 col-md-offset-3\">"+
									"<div class=\"page-header\">"+
										"<h4 class=\"pageheading\">Inbox</h4>"+
									"</div>"+
									"<div class=\"btn-group f-right\">"+
										"<button class=\"btn btn-success\" onclick=markAllReadMessages(\""+id+"\");><i class=\"glyphicon glyphicon-ok\"></i> Mark all &nbsp;<span class=\"b-count\" id=\"unreadcountspan\">"+unreadcount+"</span></button>"+
										"<button class=\"btn btn-danger\" onclick=deleteReadMessages(\""+id+"\");><i class=\"glyphicon glyphicon-trash\"></i> Delete read &nbsp;<span class=\"b-count\" id=\"readcountspan\">"+readcount+"</span></button>"+
										"<button class=\"btn btn-danger\" onclick=deleteAllMessages(\""+id+"\",\"inbox\");><i class=\"glyphicon glyphicon-trash\"></i> Delete all &nbsp;<span class=\"b-count\" id=\"totalcountspan\">"+(unreadcount+readcount)+"</span></button>"+
									"</div>"+
									"<div class=\"clearfix\"></div></br>"+
									"<div id=\"unreaddiv\">"+
										"<div class=\"panel panel-info\">" + 
											"<div class=\"panel-heading\">"+
												"<span id=\"unreadspan\" class=\"s-p-heading\">Unread messages</span>"+
											"</div>"+ 
											"<div class=\"panel-body\" id=\"unreadpanelbody\">"+
												unread+
											"</div>"+ 
										"</div>"+
									"</div>"+
									"<div id=\"readdiv\">"+
										"<div class=\"panel panel-default\">" + 
											"<div class=\"panel-heading\"><span id=\"readspan\" class=\"s-p-heading\">Read messages</span></div>"+ 
											"<div class=\"panel-body\" id=\"readpanelbody\">"+
												read+
											"</div>"+
										"</div>"+
									"</div>"+
								"</div>"+
							"</div>"+
						"</div>"+
					"</body>"+
				"</html>";
			rs.close();smt.close();cn.close();					
		}catch(Exception e){
			
		}
		pr.println(s);
		pr.flush();
	}

}
