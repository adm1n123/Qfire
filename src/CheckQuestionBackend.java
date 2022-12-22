

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
 * Servlet implementation class CheckQuestionBackend
 */
@WebServlet("/CheckQuestionBackend")
public class CheckQuestionBackend extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CheckQuestionBackend() {
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
		if(fob.validate(ses)==false){ response.sendRedirect("Login");return;}
		
		String id,questionid,Q,name,who="";
		boolean valid=true;
		

		try{
			try{
				id=ses.getAttribute("user_id").toString();who="user";
			}catch(Exception p){
				id=ses.getAttribute("admin_id").toString();who="admin";
			}
		}catch(Exception e){ response.sendRedirect("Login");return;}
		
		if(who.equals("admin")==false) valid=false;
		questionid=request.getParameter("id");
		if(fob.validNumber(id)==false) valid=false;
		if(valid==false) return;
		
		try{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection cn=DriverManager.getConnection("jdbc:mysql://localhost:3306/Qfire","root","qazzaq");
			Statement smt=cn.createStatement();
			Q="select * from questions where id="+questionid+" limit 1";
			ResultSet rs=smt.executeQuery(Q);
			if(rs.next()){
				pr.println("Exist"); //question Exist
			}
			else{
				pr.println("Not exits");// question does not exist
			}
			rs.close();smt.close();cn.close();
		}catch(Exception e){
			pr.println("failed");
		}
	}

}
