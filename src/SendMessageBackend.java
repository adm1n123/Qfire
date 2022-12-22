

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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
 * Servlet implementation class SendMessageBackend
 */
@WebServlet("/SendMessageBackend")
public class SendMessageBackend extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SendMessageBackend() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	
		PrintWriter pr=response.getWriter();
		HttpSession ses=request.getSession(false);
		Functions fob=new Functions();
		if(fob.validate(ses)==false) { response.sendRedirect("Login");return;}
		
		String sender,receiver,message,Q,id,who,fname="",lname="",notification;
		boolean valid=true;
		
		try{
			try{
				id=ses.getAttribute("user_id").toString();who="user";
				fname=ses.getAttribute("user_fname").toString();
				lname=ses.getAttribute("user_lname").toString();
			}catch(Exception p){
				id=ses.getAttribute("admin_id").toString();who="admin";
				fname=ses.getAttribute("admin_fname").toString();
				lname=ses.getAttribute("admin_lname").toString();
			}
		}catch(Exception e){ response.sendRedirect("Login");return;}

		sender=request.getParameter("sender");
		receiver=request.getParameter("receiver");
		message=request.getParameter("message");
		if(id.equals(sender)==false) valid=false;
		if(fob.validId(sender)==false) valid=false;
		if(fob.validId(receiver)==false) valid=false;
		if(fob.validText(message)==false) valid=false;
		if(valid==false){
			pr.println("Failed");
			pr.flush();
			return;
		}
		
		if(message.length()>200) message=message.substring(0,200);
		try{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection cn=DriverManager.getConnection("jdbc:mysql://localhost:3306/Qfire","root","qazzaq");
			Statement smt=cn.createStatement();
			PreparedStatement psmt;
			cn.setAutoCommit(false);
			
			try{
				psmt=cn.prepareStatement("insert into inbox(sender,receiver,message,status,datetime) values('"+sender+"','"+receiver+"',?,0,now())");
				psmt.setString(1, message);
				psmt.executeUpdate();
				psmt.close();
				psmt=cn.prepareStatement("insert into outbox(sender,receiver,message,datetime) values('"+sender+"','"+receiver+"',?,now())");
				psmt.setString(1, message);
				psmt.executeUpdate();
				psmt.close();
				pr.println("Success");
				cn.commit();
				/////////// sending notification /////////////
				HashSet<String> idset=new HashSet<String>();
				idset.add(receiver);
				notification="<span><a href=\"Profile?id="+id+"\">"+fname+" "+lname+"</a> sent message to you.<span class=\"t-grey t-sx\"> on "+fob.date()+"</span><span>";
				fob.sendNotification(idset, notification);
				idset.clear();
			}catch(Exception e){}
			
			smt.close();cn.close();
		}catch(Exception e){
			pr.println("Failed");
		}
		pr.flush();
	}

}
