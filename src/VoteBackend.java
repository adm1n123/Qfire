

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
 * Servlet implementation class VoteBackend
 */
@WebServlet("/VoteBackend")
public class VoteBackend extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public VoteBackend() {
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
		
		String s="",id="",who,type,typeid,operation,user_id,Q,upvote="<i class=\"fa fa-thumbs-o-up fa-lg\"></i> upvote",downvote="<i class=\"fa fa-thumbs-o-down fa-lg\"></i>";
		boolean valid=true;
		int totalupvote,totaldownvote;
		
		try{
			try{
				id=ses.getAttribute("user_id").toString();who="user";
			}catch(Exception p){
				id=ses.getAttribute("admin_id").toString();who="admin";
			}
		}catch(Exception e){ response.sendRedirect("Login");return;}
		
		type=request.getParameter("type");
		operation=request.getParameter("operation");
		user_id=request.getParameter("user_id");
		typeid=request.getParameter("id");
		
		if(fob.validName(type)==false) valid=false;
		if(fob.validName(operation)==false) valid=false;
		if(fob.validId(user_id)==false||user_id.equals(id)==false) valid=false;
		if(fob.validNumber(typeid)==false) valid=false;
		if(valid==false) return;
		
		try{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection cn=DriverManager.getConnection("jdbc:mysql://localhost:3306/Qfire","root","qazzaq");
			Statement smt=cn.createStatement();
			ResultSet rs;
			cn.setAutoCommit(false);
			try{
				if(operation.equals("upvote")==true){
					Q="select * from "+type+"_upvote where (id="+typeid+" and user_id='"+user_id+"')";
					rs=smt.executeQuery(Q);
					if(rs.next()){
						Q="delete from "+type+"_upvote where (id="+typeid+" and user_id='"+user_id+"')";
						smt.executeUpdate(Q);
						Q="update "+type+"s set upvote=upvote-1 where id="+typeid;
						smt.executeUpdate(Q);
					} else{
						upvote="<i class=\"fa fa-thumbs-up fa-lg i-link\"></i><span class=\"t-link\"> upvote</span>";
						Q="insert into "+type+"_upvote (id,user_id) values ("+typeid+",'"+user_id+"')";
						smt.executeUpdate(Q);
						Q="update "+type+"s set upvote=upvote+1 where id="+typeid;
						smt.executeUpdate(Q);
						Q="select * from "+type+"_downvote where (id="+typeid+" and user_id='"+user_id+"')";
						rs=smt.executeQuery(Q);
						if(rs.next()){
							Q="delete from "+type+"_downvote where (id="+typeid+" and user_id='"+user_id+"')";
							smt.executeUpdate(Q);
							Q="update "+type+"s set downvote=downvote-1 where id="+typeid;
							smt.executeUpdate(Q);
						}
					}
				}
				else{
					Q="select * from "+type+"_downvote where (id="+typeid+" and user_id='"+user_id+"')";
					rs=smt.executeQuery(Q);
					if(rs.next()){
						Q="delete from "+type+"_downvote where (id="+typeid+" and user_id='"+user_id+"')";
						smt.executeUpdate(Q);
						Q="update "+type+"s set downvote=downvote-1 where id="+typeid;
						smt.executeUpdate(Q);
					} else{
						downvote="<i class=\"fa fa-thumbs-down fa-lg i-red\"></i>";
						Q="insert into "+type+"_downvote (id,user_id) values ("+typeid+",'"+user_id+"')";
						smt.executeUpdate(Q);
						Q="update "+type+"s set downvote=downvote+1 where id="+typeid;
						smt.executeUpdate(Q);
						Q="select * from "+type+"_upvote where (id="+typeid+" and user_id='"+user_id+"')";
						rs=smt.executeQuery(Q);
						if(rs.next()){
							Q="delete from "+type+"_upvote where (id="+typeid+" and user_id='"+user_id+"')";
							smt.executeUpdate(Q);
							Q="update "+type+"s set upvote=upvote-1 where id="+typeid;
							smt.executeUpdate(Q);
						}
					}
				}
				cn.commit();rs.close();
			}catch(Exception e){}
			cn.rollback();
			
			Q="select * from "+type+"s where id="+typeid+" limit 1";
			rs=smt.executeQuery(Q);
			if(rs.next()){
				totalupvote=Integer.parseInt(rs.getString("upvote"));
				totaldownvote=Integer.parseInt(rs.getString("downvote"));
				s="Vote#"+totalupvote+"#"+totaldownvote+"#"+upvote+"#"+downvote;
			}
			else s="Not found";
			pr.println(s);
			smt.close();cn.close();
		}catch(Exception e){
		}
		pr.flush();
		
	}

}
