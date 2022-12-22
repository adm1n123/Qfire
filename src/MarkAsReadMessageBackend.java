

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
 * Servlet implementation class MarkAsReadMessageBackend
 */
@WebServlet("/MarkAsReadMessageBackend")
public class MarkAsReadMessageBackend extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public MarkAsReadMessageBackend() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		PrintWriter pr=response.getWriter();
		HttpSession ses=request.getSession(false);
		Functions fob=new Functions();
		if(fob.validate(ses)==false) { response.sendRedirect("Login");return;}
		
		String s="Failed",who="",messageid,Q,id="",userid,operation;
		boolean valid=true;
		
		try{
			try{
				id=ses.getAttribute("user_id").toString();who="user";
			}catch(Exception p){
				id=ses.getAttribute("admin_id").toString();who="admin";
			}
		}catch(Exception e){ response.sendRedirect("Login");return;}

		
		operation = request.getParameter("operation");
		userid=request.getParameter("userid");
		if(fob.validId(userid)==false) valid=false;
		if(id.equals(userid)==false) valid=false;
		if(fob.validName(operation)==false) valid=false;
		if(valid==false) return;
		
		try{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection cn=DriverManager.getConnection("jdbc:mysql://localhost:3306/Qfire","root","qazzaq");
			Statement smt=cn.createStatement();
			ResultSet rs;
			if(operation.equals("one")==true){
				messageid=request.getParameter("messageid");
				if(fob.validNumber(messageid)==false) valid=false;
				if(valid==false) return;
				Q="update inbox set status=1 where id="+messageid+" and receiver='"+id+"' limit 1";
				smt.executeUpdate(Q);
				Q="select * from inbox where receiver='"+id+"' and status=1"; // read messages
				rs=smt.executeQuery(Q);
				s="Success "+fob.inboxReadMessages(id, rs);
				rs.close();
			} else if(operation.equals("all")==true){
				Q="update inbox set status=1 where (receiver='"+id+"' and status=0)";
				smt.executeUpdate(Q);
				Q="select * from inbox where receiver='"+id+"' and status=1"; // read messages
				rs=smt.executeQuery(Q);
				s="Success "+fob.inboxReadMessages(id, rs);
				rs.close();
			}
			smt.close();cn.close();
		}catch(Exception e){
		}
		pr.println(s);
		pr.flush();
	}
}
