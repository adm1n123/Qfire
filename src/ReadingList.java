

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.EmptyStackException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class ReadingList
 */
@WebServlet("/ReadingList")
public class ReadingList extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ReadingList() {
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
		
		String s="",id="",Q,delete="delete",author;
		int no=1,totalcount=0;
		boolean valid=true;
		String who="",date="";
		try{
			try{
				id=ses.getAttribute("user_id").toString();who="user";
			}catch(Exception p){
				id=ses.getAttribute("admin_id").toString();who="admin";
			}
		}catch(Exception e){ response.sendRedirect("Login");return;}

		author=request.getParameter("user");
		if(author==null) author=id;
		
		if(fob.validId(id)==false) valid=false;
		if(fob.validId(author)==false) valid=false;
		if(valid==false) { 
			response.sendRedirect("Error");return;
		}
		
		if(who.equals("user")) s=fob.head(id)+fob.navbar(ses);
		else if(who.equals("admin")==true) s=fob.head(id)+fob.navbarAdmin(ses);
		
		try{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection cn=DriverManager.getConnection("jdbc:mysql://localhost:3306/Qfire","root","qazzaq");
			Statement smt=cn.createStatement();
			
			Q="select * from readinglist where user_id='"+author+"' order by datetime desc";
			ResultSet rs=smt.executeQuery(Q);
			while(rs.next()) totalcount++;
			
			rs.beforeFirst();
			s+=
					"<div class=\"container\" id=\"bodycontainer\">"+
						"<div class=\"row\">"+
							"<div class=\"col-md-6 col-md-offset-3\">"+
								"<div class=\"page-header\">"+
									"<h4 class=\"pageheading\">Reading list</h4>"+
								"</div>"+
								"<div>";
			if(author.equals(id)) s+=
									"<button class=\"btn btn-danger f-right\" id=\"deleteall\" onclick=deleteAllReadingList(\""+id+"\",\""+totalcount+"\");><i class=\"glyphicon glyphicon-remove\"></i> Remove all &nbsp;<span class=\"b-count\" id=\"totalcountspan\">"+totalcount+"</span></button>";
			s+=		 				"<div class=\"clearfix\"></div></br>"+
									"<table id=\"readinglisttable\" class=\"table table-striped table-hover\">"+
										"<tr><th>Questions to be read </th></tr>";
			if(rs.next()){
				do{
					date=fob.date(rs.getString("datetime"));
					
					s+=					"<tr>"+
											"<td>";
					if(author.equals(id)) s+=	"<button class=\"btnCustom btnCustom-default b-s-delete t-orange f-right\" id=\""+delete+no+"\" onclick=deleteReadingList(\""+no+"\",\""+rs.getString("question_id")+"\",\""+id+"\");><i class=\"fa fa-close fa-lg i-orange\"></i></button>";
					s+=							"<div><h4>"+rs.getString("question")+"</h4></div>"+
												"<div class=\"f-right\">"+
													"<span class=\"t-grey t-xs\">added "+date+"</span> &nbsp;&nbsp;"+
													"<a href=\"Question?id="+rs.getString("question_id")+"\"> Read answers</a>"+
												"</div>"+
											"</td>"+
										"</tr>";
					no+=1;
				}while(rs.next());
			}
			s+=						"</table>"+
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
	
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String s="Failed",who,id,questionid,userid,Q,question;
		boolean valid=true;
		PrintWriter pr=response.getWriter();
		HttpSession ses=request.getSession(false);
		Functions fob=new Functions();
		if(fob.validate(ses)==false){ response.sendRedirect("Login");return;}
		try{
			try{
				id=ses.getAttribute("user_id").toString();who="user";
			}catch(Exception p){
				id=ses.getAttribute("admin_id").toString();who="admin";
			}
		}catch(Exception e){ response.sendRedirect("Login");return;}
		
		questionid=request.getParameter("questionid");
		userid=request.getParameter("userid");
		if(fob.validNumber(questionid)==false) valid=false;
		if(fob.validId(userid)==false) valid=false;
		if(valid==false) return;
		
		try{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection cn=DriverManager.getConnection("jdbc:mysql://localhost:3306/Qfire","root","qazzaq");
			Statement smt=cn.createStatement();
			PreparedStatement psmt;
			Q="select * from questions where id="+questionid;
			ResultSet rs=smt.executeQuery(Q);
			if(rs.next()){
				question=rs.getString("question");
				Q="select * from readinglist where question_id="+questionid+" and user_id='"+userid+"'";
				rs=smt.executeQuery(Q);
				if(rs.next()==true){
					Q="delete from readinglist where question_id="+questionid+" and user_id='"+userid+"'";
					smt.executeUpdate(Q);
					s="Removed";
				} else{
					psmt=cn.prepareStatement("insert into readinglist (question_id,user_id,question,datetime) values ("+questionid+",'"+userid+"',?,now())");
					psmt.setString(1,question);
					psmt.executeUpdate();
					psmt.close();
					s="Added";
				}
			}
		}catch(Exception e){
		}
		pr.println(s);
		pr.flush();
	}
}
