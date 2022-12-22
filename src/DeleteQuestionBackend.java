

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
 * Servlet implementation class DeleteQuestionBackend
 */
@WebServlet("/DeleteQuestionBackend")
public class DeleteQuestionBackend extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DeleteQuestionBackend() {
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
		
		String questionid,who="",id="",Q,answerid;
		boolean valid=true;
		
		try{
			try{
				id=ses.getAttribute("user_id").toString();who="user";
			}catch(Exception p){
				id=ses.getAttribute("admin_id").toString();who="admin";
			}
		}catch(Exception e){ response.sendRedirect("Login");return;}
		
		questionid=request.getParameter("id");
		if(fob.validNumber(questionid)==false) valid=false;
		if(fob.validId(id)==false) valid=false;
		if(valid==false) return;
		
		if(who.equals("user")){
			try{
				Class.forName("com.mysql.jdbc.Driver").newInstance();
				Connection cn=DriverManager.getConnection("jdbc:mysql://localhost:3306/Qfire","root","qazzaq");
				Statement smt=cn.createStatement();
				Q="select * from questions where id="+questionid+" and author='"+id+"' limit 1";
				ResultSet rs=smt.executeQuery(Q);
				if(rs.next()==true) who="admin"; // question owner
				rs.close();smt.close();cn.close();
			}catch(Exception e){
				response.sendRedirect("Login");return;
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
					Q="select * from questions where id="+questionid+" limit 1";
					rs=smt.executeQuery(Q);
					if(rs.next()==true){
						psmt=cn.prepareStatement("insert into recycle_bin values (null,'question','"+rs.getString("id")+"','"+rs.getString("author")+"','"+rs.getString("datetime")+"',?,now())");
						psmt.setString(1,"answer count:"+rs.getString("answercount")+", upvote:"+rs.getString("upvote")+", downvote:"+rs.getString("downvote")+", comment count:"+rs.getString("commentcount")+"question:-"+rs.getString("question"));
						psmt.executeUpdate();
						psmt.close();
					}
				}catch(Exception e){
				}
				try{
					Q="delete from questions where id="+questionid+" limit 1";
					smt.executeUpdate(Q);
					Q="delete from question_upvote where id="+questionid;
					smt.executeUpdate(Q);
					Q="delete from question_downvote where id="+questionid;
					smt.executeUpdate(Q);
					Q="delete from comments where typeid="+questionid+" and type='question'";
					smt.executeUpdate(Q);
					Q="delete from readinglist where question_id="+questionid;
					smt.executeUpdate(Q);
					Q="delete from suspicious where id="+questionid+" and type='question'";
					smt.executeUpdate(Q);
					Q="select * from answers where question_id="+questionid;
					rs=smt.executeQuery(Q);
					while(rs.next()==true){
						answerid=rs.getString("id");
						Q="delete from answer_upvote where id="+answerid;
						smt.executeUpdate(Q);
						Q="delete from answer_downvote where id="+answerid;
						smt.executeUpdate(Q);
						Q="delete from comments where typeid="+answerid+" and type='answer'";
						smt.executeUpdate(Q);
						Q="delete from suspicious where id="+answerid+" and type='answer'";
						smt.executeUpdate(Q);
					}
					Q="delete from answers where question_id="+questionid;
					smt.executeUpdate(Q);
					Q="delete from tag_map where question_id="+questionid;
					smt.executeUpdate(Q);
					cn.commit();rs.close();
				}catch(Exception e){
				}
				pr.println("Deleted");
				cn.rollback();smt.close();cn.close();
			}catch(Exception e){
				pr.println("failed");
			}
		} else{
			response.sendRedirect("Login");return;
		}
	}
}
