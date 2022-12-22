

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
 * Servlet implementation class Suspicious
 */
@WebServlet("/Suspicious")
public class Suspicious extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Suspicious() {
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
		
		String s="",Q,type,id,reporter,who;
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
								"<h4 class=\"pageheading\">Suspicious activity</h4>"+
							"</div>"+
							"<div>";
		
		try{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection cn=DriverManager.getConnection("jdbc:mysql://localhost:3306/Qfire","root","qazzaq");
			Statement smt=cn.createStatement();
			Q="select * from suspicious";
			ResultSet rs=smt.executeQuery(Q);
			if(rs.next()){
				rs.beforeFirst();
						s+=
							"<button class=deleteallbutton id=deleteallsuspicious onclick=deleteAllSuspicious();>Delete all</button></br></br>"+
							"<table class=\"table table-striped table-hover\">"+
								"<tr><th>Reporter</th><th>Type: Id</th><th>Link</th><th>Action</th></tr>";
				while(rs.next()){
					type="Question";
					if(type.equalsIgnoreCase(rs.getString("type"))==false) type="Answer";
					id=rs.getString("id");
					reporter=rs.getString("reporter");
					s+="<tr>"+
							"<td><a href=Profile?id="+reporter+"><button class=profilelink>"+reporter+"</button></a></td>"+ // "<td><a href=UserProfile?user_id="+reporter+"><button >"+reporter+"</button></a></td>"+
							"<td>"+type+": "+id+"</td>"+
							"<td><a href="+type+"?id="+id+"><button class=readanswerbutton>view</button></a></td>"+
							"<td><button class=deletebutton id=deletesuspicious"+type.toLowerCase()+id+reporter+" onclick=deleteSuspicious(\""+type.toLowerCase()+"\",\""+id+"\",\""+reporter+"\");>Remove</button></td>"+
						"</tr>";
				}
				s+="</table>";
			}
			else s+="<p style=\"font-size:14pt;color:red;\">No suspicious activity reported</p>";
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
}
