

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class ReportSuspiciousBackend
 */
@WebServlet("/ReportSuspiciousBackend")
public class ReportSuspiciousBackend extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ReportSuspiciousBackend() {
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
		Functions fob=new Functions();
		if(fob.validate(ses)==false) { response.sendRedirect("Login");return;}
		
		String typeid,id,type,user_id,Q,who="",Type="",notification;
		boolean valid=true;
		
		try{
			try{
				id=ses.getAttribute("user_id").toString();who="user";
			}catch(Exception p){
				id=ses.getAttribute("admin_id").toString();who="admin";
			}
		}catch(Exception e){ response.sendRedirect("Login");return;}

		user_id=request.getParameter("user_id");
		typeid=request.getParameter("id");
		type=request.getParameter("type");
		
		if(fob.validId(user_id)==false) valid=false;
		if(fob.validNumber(typeid)==false) valid=false;
		if(fob.validName(type)==false) valid=false;
		if(valid==false) { response.sendRedirect("Error");return;}
		
		try{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection cn=DriverManager.getConnection("jdbc:mysql://localhost:3306/Qfire","root","qazzaq");
			Statement smt=cn.createStatement();
			ResultSet rs=smt.executeQuery("select * from "+type+"s where id="+typeid);
			Q="insert into suspicious (id,type,reporter) values ("+typeid+",'"+type+"','"+user_id+"')";
			if(rs.next()){
				smt.executeUpdate(Q);pr.println("Reported");
			}
			else pr.println("NotFound");
			rs.close();smt.close();cn.close();
			
			if(type.equals("question")==true) Type="Question";else Type="Answer";
			HashSet<String> idset=new HashSet<String>();
			notification="<span><a href=\"Profile?id="+id+"\">"+id+"</a> reported suspicious <a href=\""+Type+"?id="+typeid+"\">"+type+"</a><span class=\"t-grey t-sx\"> on "+fob.date()+"</span><span>";
			idset.add("admin123");
			fob.sendNotification(idset, notification);
			idset.clear();
		}catch(Exception e){
		}
		pr.flush();
	}
}
