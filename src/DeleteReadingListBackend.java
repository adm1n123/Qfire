

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
 * Servlet implementation class DeleteReadingListBackend
 */
@WebServlet("/DeleteReadingListBackend")
public class DeleteReadingListBackend extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DeleteReadingListBackend() {
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
		
		String Q,questionid,id="",userid,who="",operation;
		boolean valid=true;
		
		try{
			try{
				id=ses.getAttribute("user_id").toString();who="user";
			}catch(Exception p){
				id=ses.getAttribute("admin_id").toString();who="admin";
			}
		}catch(Exception e){ response.sendRedirect("Login");return;}
		
		operation=request.getParameter("operation");
		userid=request.getParameter("userid");
		if(fob.validId(userid)==false) valid=false;
		if(fob.validName(operation)==false) valid=false;
		if(id.equals(userid)==false) valid=false;
		if(valid==false) return;
		
		if(operation.equals("one")==true){
			questionid=request.getParameter("id");
			if(fob.validNumber(questionid)==false) valid=false;
			if(valid==false) return;
			
			try{
				Class.forName("com.mysql.jdbc.Driver").newInstance();
				Connection cn=DriverManager.getConnection("jdbc:mysql://localhost:3306/Qfire","root","qazzaq");
				Statement smt=cn.createStatement();
				Q="delete from readinglist where question_id="+questionid+" and user_id='"+id+"'";
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
				Q="delete from readinglist where user_id='"+id+"'";
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
