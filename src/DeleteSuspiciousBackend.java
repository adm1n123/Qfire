

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class DeleteSuspiciousBackend
 */
@WebServlet("/DeleteSuspiciousBackend")
public class DeleteSuspiciousBackend extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DeleteSuspiciousBackend() {
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
		
		String Q,type,typeid,id,reporter,who="",operation;
		boolean valid=true;
		
		try{
			try{
				id=ses.getAttribute("user_id").toString();who="user";
			}catch(Exception p){
				id=ses.getAttribute("admin_id").toString();who="admin";
			}
		}catch(Exception e){ response.sendRedirect("Login");return;}
		
		operation=request.getParameter("operation");
		if(who.equals("admin")==false) valid=false;
		if(fob.validName(operation)==false) valid=false;
		
		if(operation.equals("one")==true){
			type=request.getParameter("type");
			typeid=request.getParameter("id");
			reporter=request.getParameter("reporter");
			
			if(fob.validName(type)==false) valid=false;
			if(fob.validId(reporter)==false) valid=false;
			if(fob.validNumber(typeid)==false) valid=false;
			if(valid==false) return;
			
			try{
				Class.forName("com.mysql.jdbc.Driver").newInstance();
				Connection cn=DriverManager.getConnection("jdbc:mysql://localhost:3306/Qfire","root","qazzaq");
				Statement smt=cn.createStatement();
				Q="delete from suspicious where (id="+typeid+" and type='"+type+"' and reporter='"+reporter+"')";
				smt.executeUpdate(Q);
				pr.println("Deleted");
				smt.close();cn.close();
			}catch(Exception e){
				pr.println("failed");
			}
		} else if(operation.equals("all")==true){
			try{
				Class.forName("com.mysql.jdbc.Driver").newInstance();
				Connection cn=DriverManager.getConnection("jdbc:mysql://localhost:3306/Qfire","root","qazzaq");
				Statement smt=cn.createStatement();
				Q="delete from suspicious";
				smt.executeUpdate(Q);
				pr.println("Deleted");
				smt.close();cn.close();
			}catch(Exception e){
				pr.println("failed");
			}
		}

		pr.flush();
	}

}
