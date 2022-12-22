

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
 * Servlet implementation class Notes
 */
@WebServlet("/Notes")
public class Notes extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Notes() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String id="",s="",Q,notes="",who="",date="";
		boolean valid=true;
		PrintWriter pr=response.getWriter();
		HttpSession ses=request.getSession(false);
		Functions fob=new Functions(); fob.noCache(response);
		if(fob.validate(ses)==false) { response.sendRedirect("Login");return;}
		
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
			Q="select * from notes where user_id='"+id+"' limit 1";
			ResultSet rs=smt.executeQuery(Q);
			if(rs.next()){
				notes=rs.getString("notes");
				date="saved on "+fob.date(rs.getString("datetime"));
			}
			s+=
					"<div class=\"container\" id=\"bodycontainer\">"+
							"<div class=\"row\">"+
								"<div class=\"col-md-6 col-md-offset-3\">"+
									"<div class=\"page-header\">"+
										"<h4 class=\"pageheading\">My notes</h4>"+
									"</div>"+
									"<div>"+
										"<div class=\"btn-group f-right\">"+
											"<button class=\"btn btn-default\" id=\"editnotes\" onclick=editNotes(\"button\");><i class=\"glyphicon glyphicon-edit i-blue\"></i> Edit</button>"+
											"<button class=\"btn btn-danger\" id=\"deletenotes\" onclick=saveNotes(\""+id+"\",\"clear\");><i class=\"glyphicon glyphicon-trash\"></i> Clear</button>"+
										"</div>"+
										"<div><span id=\"lastsaved\" class=\"t-grey t-sx\">"+date+"</span></div>"+
										"<div class=\"clearfix\"></div>"+
										"<div style=\"border-style:outset; padding:10px;\">You can use HTML/CSS for text decoration</br></br><span id=\"notestext\">"+notes+"</spna></div>"+
										"<div class=\"clearfix\"></div>"+
										"<div id=\"editnotesdiv\" style=\"display:none\">"+
											"<span id=\"notesspan\"></span></br>"+
											"<textarea class=\"form-control\" id=\"notes\" rows=\"15\" cols=\"100\" onkeyup=editNotes(\"textarea\"); placeholder=\"Write notes here\" >"+notes+"</textarea>"+
											"<div class=\"b-save\"><button class=\"btn btn-success\" id=\"savenotes\" onclick=saveNotes(\""+id+"\",\"save\");><i class=\"glyphicon glyphicon-save\"></i> Save</button></div>"+
										"</div>"+
									"</div>"+
								"</div>"+
							"</div>"+
						"</body>"+
					"</html>";
			pr.println(s);
			rs.close();smt.close();cn.close();
		}catch(Exception e){
			pr.println(e);
		}
		pr.flush();
	}
}
