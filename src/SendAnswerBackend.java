

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
 * Servlet implementation class SendAnswerBackend
 */
@WebServlet("/SendAnswerBackend")
public class SendAnswerBackend extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SendAnswerBackend() {
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
		
		String s="Failed",id,who,questionid,userid,Q,answer,notification,authorq,fname="",lname="",date;
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


		questionid=request.getParameter("id");
		userid=request.getParameter("user_id");
		answer=request.getParameter("answer");
		
		if(fob.validId(userid)==false) valid=false;
		if(userid.equals(id)==false) valid=false;
		if(fob.validNumber(questionid)==false) valid=false;
		if(fob.validText(answer)==false||answer.length()==0) valid=false;
		if(valid==false) return;
		if(answer.length()>5000) answer=answer.substring(0, 5000);
		
		try{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection cn=DriverManager.getConnection("jdbc:mysql://localhost:3306/Qfire","root","qazzaq");
			Statement smt=cn.createStatement();
			PreparedStatement psmt;
			ResultSet rs;
			cn.setAutoCommit(false);
			
			try{
				psmt=cn.prepareStatement("insert into answers (question_id,author,answer,upvote,downvote,datetime,commentcount) values ("+questionid+",'"+userid+"',?,0,0,now(),0)");
				psmt.setString(1,answer);
				psmt.executeUpdate();
				psmt.close();
				Q="update questions set answercount=answercount+1 where id="+questionid+" limit 1";
				smt.executeUpdate(Q);
				s="Success";
				cn.commit();
				//////// sending notification //////////////////////
				HashSet<String> idset=new HashSet<String>();
				Q="select author from questions where id="+questionid+" limit 1";
				rs=smt.executeQuery(Q);
				if(rs.next()==true){
					authorq=rs.getString("author");
					date=fob.date();
					notification="<span><a href=\"Profile?id="+id+"\">"+fname+" "+lname+"</a> also answered <a href=\"Profile?id="+authorq+"\">"+authorq+"'s</a> <a href=\"Question?id="+questionid+"\">question</a>. <span class=\"t-grey t-sx\"> "+date+"</span></span>";
					Q="select author from answers where question_id="+questionid;
					rs=smt.executeQuery(Q);
					while(rs.next()==true) idset.add(rs.getString("author"));
					idset.remove(authorq);idset.remove(id);
					fob.sendNotification(idset, notification);
					idset.clear();idset.add(authorq);idset.remove(id);
					notification="<span><a href=\"Profile?id="+id+"\">"+fname+" "+lname+"</a> answered your <a href=\"Question?id="+questionid+"\">question</a>. <span class=\"t-grey t-sx\"> "+date+"</span><span>";
					fob.sendNotification(idset, notification);
					idset.clear();
				}
				
				rs.close();
			}catch(Exception e){}
			cn.rollback();
			
			// notification need not to be success
			
			// sending notification
			smt.close();cn.close();
		}catch(Exception e){
		}
		pr.println(s);
		pr.flush();
	}
}
