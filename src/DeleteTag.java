

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
 * Servlet implementation class DeleteTag
 */
@WebServlet("/DeleteTag")
public class DeleteTag extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DeleteTag() {
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
		
		String s="",id="",who="",message="";
		boolean valid=true;
		
		try{
			try{
				id=ses.getAttribute("user_id").toString();who="user";
			}catch(Exception p){
				id=ses.getAttribute("admin_id").toString();who="admin";
			}
		}catch(Exception e){ response.sendRedirect("Login");return;}
		
		if(who.equals("admin")==false) valid=false;
		if(valid==false){ response.sendRedirect("Login");return;}
		
		if(request.getAttribute("message")!=null) message=request.getAttribute("message").toString();
		
		s=fob.head(id)+fob.navbarAdmin(ses)+
					"<div class=\"container\" id=\"bodycontainer\">"+
						"<p class=pageheading>Delete tag</p></br>"+
						"<form action=\"DeleteTag\" method=\"post\">"+
						"<table>"+
							"<tr>"+
								"<td> <input type=\"text\" name=\"tagid\" ></td>"+
							"</tr>"+
							"<tr>"+
								"<td><button type=\"submit\" class=\"deletebutton\">Delete</button></td>"+
							"</tr>"+
							"<tr><td>"+message+"</td></tr>"+
						"</table>"+
						"</form>"+
					"</div>"+
				"</body>"+
		"</html>";
		pr.println(s);
		pr.flush();
		
		
	
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		HttpSession ses=request.getSession(false);
		Functions fob=new Functions();
		if(fob.validate(ses)==false) { response.sendRedirect("Login");return;}
		
		String who="",id="",tagid,message;
		boolean valid=true;
		
		try{
			try{
				id=ses.getAttribute("user_id").toString();who="user";
			}catch(Exception p){
				id=ses.getAttribute("admin_id").toString();who="admin";
			}
		}catch(Exception e){ response.sendRedirect("Login");return;}
		
		tagid=request.getParameter("tagid");
		if(fob.validNumber(tagid)==false) valid=false;
		if(who.equals("admin")==false) valid=false;
		if(valid==false){
			response.sendRedirect("DeleteTag");
			return;
		}
		
		message="<span class=\"t-green\">Deleted</span>";
		try{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection cn=DriverManager.getConnection("jdbc:mysql://localhost:3306/Qfire","root","qazzaq");
			Statement smt=cn.createStatement();
			cn.setAutoCommit(false);
			try{
				if(smt.executeUpdate("delete from tags where id="+tagid)==0) message="<span class=\"t-red\">Tag do not exist</span>";
				smt.executeUpdate("delete from tag_map where tag_id="+tagid);
				cn.commit();
			}catch(Exception e){}
			cn.rollback();smt.close();cn.close();
		}catch(Exception e){
			message="<span class=\"t-red\">Error</span>";
		}
		request.setAttribute("message", message);
		doGet(request, response);
		
	}
}
