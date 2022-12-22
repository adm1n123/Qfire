

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
 * Servlet implementation class AskedQuestions
 */
@WebServlet("/AskedQuestions")
public class AskedQuestions extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AskedQuestions() {
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
		if(fob.validate(ses)==false){ response.sendRedirect("Login");return;}
		String s="",id="",who="",Q,author;
		int count=0;
		boolean valid=true;
		String questions="",t,page="askedquestions";
		
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
		if(valid==false) { response.sendRedirect("Error");return;}
		
		if(who.equals("user")) s=fob.head(id)+fob.navbar(ses);
		else if(who.equals("admin")==true) s=fob.head(id)+fob.navbarAdmin(ses);
		
		try{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection cn=DriverManager.getConnection("jdbc:mysql://localhost:3306/Qfire","root","qazzaq");
			Statement smt=cn.createStatement();
			Q="select * from questions where author='"+author+"' order by datetime desc";
			ResultSet rs=smt.executeQuery(Q);
			s+=
					"<div class=\"container\" id=\"bodycontainer\">"+
						"<div class=\"row\">"+
							"<div class=\"col-md-6 col-md-offset-3\">"+
								"<div class=\"page-header\">"+
									"<h4 class=\"pageheading\">Asked questions</h4>"+
								"</div>"+
								"<div id=\"maindiv\">";
			
			
			while(rs.next()){
				t=fob.question(id, page, rs);
				if(t.equals("Failed")==true||t.length()==0) continue;
				questions+=			"<table id=\"askedquestionstable\" class=\"table table-hover\">"+
										"<tr>"+
											"<td>"+
												t+
											"</td>"+
										"</tr>"+
									"</table>";
				count++;
			}
			s+=						"<div><span class=\"t-bold t-sm t-grey t-underline\">Query fired</span> &nbsp;<span class=\"b-count t-grey\" id=\"totalcountspan\">"+count+"</span></div></br>";
			if(count==0) s+=		"<span class=\"t-blue t-lg\"></br>You didn't fire any query</span>";
			else s+=				"<span id=\"askedquestionsspan\" class=\"t-blue t-lg\" style=\"display:none\"></br>You didn't fire any query</span>";
			s+=						questions+
								"</div>"+
							"</div>"+
						"</div>"+
					"</div>"+
				"</body>"+
			"</html>";
			rs.close();smt.close();cn.close();
			pr.println(s);
			pr.flush();
		}catch(Exception e){
		}
	}
}