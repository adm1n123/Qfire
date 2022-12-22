

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class DeleteAnswerBackend
 */
@WebServlet("/DeleteAnswerBackend")
public class DeleteAnswerBackend extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DeleteAnswerBackend() {
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
		
		String answerid,questionid="",Q,who="",id="";
		boolean valid=true;
		
		try{
			try{
				id=ses.getAttribute("user_id").toString();who="user";
			}catch(Exception p){
				id=ses.getAttribute("admin_id").toString();who="admin";
			}
		}catch(Exception e){ response.sendRedirect("Login");return;}
		
		answerid=request.getParameter("id");
		if(fob.validNumber(answerid)==false) valid=false;
		if(valid==false) return;
		
		if(who.equals("user")){
			try{
				Class.forName("com.mysql.jdbc.Driver").newInstance();
				Connection cn=DriverManager.getConnection("jdbc:mysql://localhost:3306/Qfire","root","qazzaq");
				Statement smt=cn.createStatement();
			
				Q="select * from answers where id="+answerid+" and author='"+id+"' limit 1";
				ResultSet rs=smt.executeQuery(Q);
				if(rs.next()==true) who="admin"; // answer owner
				rs.close();smt.close();cn.close();
			}catch(Exception e){
				return;
			}
		}
		if(who.equals("admin")){
			try{
				Class.forName("com.mysql.jdbc.Driver").newInstance();
				Connection cn=DriverManager.getConnection("jdbc:mysql://localhost:3306/Qfire","root","qazzaq");
				Statement smt=cn.createStatement();
				PreparedStatement psmt;
				ResultSet rs;
				cn.setAutoCommit(false);
				
				try{ /////////////// moving to recycle bin //////////////
					Q="select * from answers where id="+answerid+" limit 1";
					rs=smt.executeQuery(Q);
					if(rs.next()==true){
						psmt=cn.prepareStatement("insert into recycle_bin values (null,'answer','"+rs.getString("id")+"','"+rs.getString("author")+"','"+rs.getString("datetime")+"',?,now())");
						psmt.setString(1,"question_id:"+rs.getString("question_id")+",upvote:"+rs.getString("upvote")+", downvote:"+rs.getString("downvote")+", comment count:"+rs.getString("commentcount")+"answer:-"+rs.getString("answer"));
						psmt.executeUpdate();
						psmt.close();
					}
				}catch(Exception e){}
				
				try{
					Q="select * from answers where id="+answerid+" limit 1";
					rs=smt.executeQuery(Q);
					if(rs.next()==true) questionid=rs.getString("question_id");
					Q="delete from answer_upvote where id="+answerid;
					smt.executeUpdate(Q);
					Q="delete from answer_downvote where id="+answerid;
					smt.executeUpdate(Q);
					Q="delete from answers where id="+answerid+" limit 1";
					smt.executeUpdate(Q);
					Q="delete from suspicious where id="+answerid+" and type='answer'";
					smt.executeUpdate(Q);
					Q="delete from comments where typeid="+answerid+" and type='answer'";
					smt.executeUpdate(Q);
					Q="update questions set answercount=answercount-1 where id="+questionid+" limit 1";
					smt.executeUpdate(Q);
					pr.println("Deleted");
					cn.commit();rs.close();
				}catch(Exception e){}
				cn.rollback();smt.close();cn.close();
			}catch(Exception e){
				pr.println("failed");
			}
		}
		else{
			response.sendRedirect("Login");return;
		}
	}
}
