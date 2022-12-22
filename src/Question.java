

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class Question
 */
@WebServlet("/Question")
public class Question extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Question() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String s="",t,who="",id="",questionid,Q,page="question",answercount;
		boolean valid=true;
		PrintWriter pr=response.getWriter();
		HttpSession ses=request.getSession(false);
		Functions fob=new Functions(); fob.noCache(response);
		if(fob.validate(ses)==false) { response.sendRedirect("Login");return;}
		
		String Head="",Navbar,name="",date="",st[];
		try{
			try{
				id=ses.getAttribute("user_id").toString();who="user";
			}catch(Exception p){
				id=ses.getAttribute("admin_id").toString();who="admin";
			}
		}catch(Exception e){ response.sendRedirect("Login");return;}


		questionid=request.getParameter("id"); // question id
		
		if(fob.validId(id)==false) valid=false;
		if(fob.validNumber(questionid)==false) valid=false;
		if(valid==false) { response.sendRedirect("Error");return;}
		
		if(who.equals("user")) s=fob.head(id)+fob.navbar(ses);
		else if(who.equals("admin")==true) s=fob.head(id)+fob.navbarAdmin(ses);
		
		try{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection cn=DriverManager.getConnection("jdbc:mysql://localhost:3306/Qfire","root","qazzaq");
			Statement smt=cn.createStatement();
			ResultSet rs;
			s+=
					"<div class=\"container\" id=\"bodycontainer\">"+
						"<div class=\"row\">"+
							"<div class=\"col-sm-3 col-md-3 col-lg-3\"> </div>"+
							"<div class=\"col-sm-6 col-md-6 col-lg-6\">"+
								"<div class=\"page-header\">"+
									"<h4 class=\"pageheading\">Question</h4>"+
								"</div>"+
								"<div id=\"maindiv\">";											// maindiv id is used to count the number of answers when answer deleted
			
			Q="select * from questions where id="+questionid+" limit 1";
			rs=smt.executeQuery(Q);
			if(rs.next()){
				answercount=rs.getString("answercount");
				if(answercount.length()>2) answercount="100";
				
				s+=					"<table id=\"questiontable\" class=\"table table-hover\">"+
										"<tr>"+
											"<td>"+
												fob.question(id,page, rs)+
											"</td>"+
										"</tr>"+
									"</table>"+
									"<ul class=\"nav nav-tabs sortbytab\">" + 
										"<li class=\"active\"><a data-toggle=\"tab\" href=\"#popularity\" onclick=sortByAnswerTab(\"popularity\",\""+questionid+"\",\""+id+"\",\"question\");>popularity</a></li>"+
										"<li><a data-toggle=\"tab\" href=\"#oldest\" onclick=sortByAnswerTab(\"oldest\",\""+questionid+"\",\""+id+"\",\"question\");>oldest</a></li>"+
										"<li><a data-toggle=\"tab\" href=\"#newest\" onclick=sortByAnswerTab(\"newest\",\""+questionid+"\",\""+id+"\",\"question\");>newest</a></li>"+
										"<li><a data-toggle=\"tab\" href=\"#myanswer\" onclick=sortByAnswerTab(\"myanswer\",\""+questionid+"\",\""+id+"\",\"question\");>my answer</a></li>"+
										"<li style=\"float:right\"><span id=\"numberofanswer"+questionid+"\" class=\"numberofanswer\"> "+answercount+" Answers</span></li>"+
									"</ul>" + 
									"<div class=\"tab-content\">"+
										"<div id=\"popularity\" class=\"tab-pane fade in active\">";
				
				t=fob.popularAnswers(questionid, id, page);
				if(t.equals("Failed")==true) s+="</br><p class=\"t-red>Error while loading answers try again</p>";
				else if(t.equals("")==true) s+="</br><p class=\"t-blue\">No answers be first to answer <i class=\"fa fa-smiley-o\"></i></p>";
				else s+=t;
				
				s+=						"</div>"+
										"<div id=\"oldest\" class=\"tab-pane fade\">"+
										"</div>"+
										"<div id=\"newest\" class=\"tab-pane fade\">"+ 
										"</div>" + 
										"<div id=\"myanswer\" class=\"tab-pane fade\">"+ 
										"</div>" + 
									"</div>";
			}
			else{
				s+=					"<p class=\"t-red t-lg\">Question does not exist</p>";
			}
			s+=					"</div>"+
							"</div>"+
							"<div class=\"col-sm-3 col-md-3 col-lg-3\"> </div>"+
						"</div>"+
					"</div>"+
				"</body>"+
			"</html>";
			pr.println(s);
			rs.close();smt.close();cn.close();
		}catch(Exception e){
		}
		pr.flush();
	}
}
