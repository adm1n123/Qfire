

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class CommentBackend
 */
@WebServlet("/CommentBackend")
public class CommentBackend extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CommentBackend() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
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
		
		String id="",who="",Q,type="",typeid="",author,operation,comment,s="Failed",cid,userid,typeauthor,date,notification,fname,lname,Type="";
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
		
		operation=request.getParameter("operation");
		if(fob.validName(operation)==false) { response.sendRedirect("Login");return;}
		
	/*	try{
		TimeUnit.SECONDS.sleep(5);
		}catch(Exception e){}
	*/	
		if(operation.equals("save")==true){
			type=request.getParameter("type");
			typeid=request.getParameter("typeid");
			author=request.getParameter("author");
			comment=request.getParameter("comment");
			if(type.equals("question")==true) Type="Question";else Type="Answer";
			if(fob.validName(type)==false) valid=false;
			if(fob.validNumber(typeid)==false) valid=false;
			if(fob.validId(author)==false) valid=false;
			if(fob.validText(comment)==false) valid=false;
			if(id.equals(author)==false) valid=false;
			if(valid==false) return;
			try{
				Class.forName("com.mysql.jdbc.Driver").newInstance();
				Connection cn=DriverManager.getConnection("jdbc:mysql://localhost:3306/Qfire","root","qazzaq");
				Statement smt=cn.createStatement();
				PreparedStatement psmt;
				ResultSet rs;
				cn.setAutoCommit(false);
				
				
				try{
					psmt=cn.prepareStatement("insert into comments (author,type,typeid,comment,datetime) values ('"+author+"','"+type+"',"+typeid+",?,now())");
					psmt.setString(1, comment);
					int c=psmt.executeUpdate();
					if(c==1){
						Q="update "+type+"s set commentcount=commentcount+1 where id="+typeid;
						smt.executeUpdate(Q);
						cn.commit();
						//////// sending notification //////////////////////
						HashSet<String> idset=new HashSet<String>();
						Q="select author from "+type+"s where id="+typeid+" limit 1";
						rs=smt.executeQuery(Q);
						if(rs.next()==true){
							typeauthor=rs.getString("author");
							date=fob.date();
							notification="<span><a href=\"Profile?id="+id+"\">"+fname+" "+lname+"</a> also commented on <a href=\"Profile?id="+typeauthor+"\">"+typeauthor+"'s</a> <a href=\""+Type+"?id="+typeid+"\">"+type+"</a>. <span class=\"t-grey t-sx\"> "+date+"</span></span>";
							Q="select author from comments where type='"+type+"' and typeid="+typeid;
							rs=smt.executeQuery(Q);
							while(rs.next()==true) idset.add(rs.getString("author"));
							idset.remove(typeauthor);idset.remove(id);
							fob.sendNotification(idset, notification);
							idset.clear();idset.add(typeauthor);idset.remove(id);
							notification="<span><a href=\"Profile?id="+id+"\">"+fname+" "+lname+"</a> commented on your <a href=\""+Type+"?id="+typeid+"\">"+type+"</a>. <span class=\"t-grey t-sx\"> "+date+"</span><span>";
							fob.sendNotification(idset, notification);
							idset.clear();
						}
						s="Success "+fob.comments(type, typeid, author);
					}
					psmt.close();
				}catch(Exception e){}
				
				cn.rollback();smt.close();cn.close();
			}catch(Exception e){}
			
		} else if(operation.equals("delete")==true){
			cid=request.getParameter("cid");
			author=request.getParameter("author");
			if(id.equals(author)==false) valid=false;
			if(fob.validNumber(cid)==false) valid=false;
			if(valid==false) return;
			try{
				Class.forName("com.mysql.jdbc.Driver").newInstance();
				Connection cn=DriverManager.getConnection("jdbc:mysql://localhost:3306/Qfire","root","qazzaq");
				Statement smt=cn.createStatement();
				PreparedStatement psmt;
				ResultSet rs;
				cn.setAutoCommit(false);
				try{ /////////////// moving to recycle bin //////////////
					Q="select * from comments where id="+cid+" limit 1";
					rs=smt.executeQuery(Q);
					if(rs.next()==true){
						psmt=cn.prepareStatement("insert into recycle_bin values (null,'comment','"+rs.getString("id")+"','"+rs.getString("author")+"','"+rs.getString("datetime")+"',?,now())");
						psmt.setString(1, "type:"+rs.getString("type")+", typeid:"+rs.getString("typeid")+", comment: "+rs.getString("comment"));
						psmt.executeUpdate();
						psmt.close();
					}
				}catch(Exception e){}
				
				rs=smt.executeQuery("select * from comments where id="+cid+" limit 1");
				try{
					if(rs.next()==true&&rs.getString("author").equals(id)==true||who.equals("admin")==true){
						type=rs.getString("type");typeid=rs.getString("typeid");
						Q="delete from comments where id="+cid+" limit 1";
						int c=smt.executeUpdate(Q);
						if(c==1) {
							Q="update "+type+"s set commentcount=commentcount-1 where id="+typeid+" limit 1";
							smt.executeUpdate(Q);
							s="Success";
							cn.commit();
						}
					}
				}catch(Exception e){}
				cn.rollback();rs.close();smt.close();cn.close();
			}catch(Exception e){}
			
		} else if(operation.equals("show")==true){ // to display comment on button click
			type=request.getParameter("type");
			typeid=request.getParameter("typeid");
			userid=request.getParameter("userid");
			if(type.equals("question")==true) Type="Question";else Type="Answer";
			if(fob.validName(type)==false) valid=false;
			if(fob.validNumber(typeid)==false) valid=false;
			if(fob.validId(userid)==false) valid=false;
			if(id.equals(userid)==false) valid=false;
			if(valid==false) return;
			s="Success "+fob.comments(type, typeid, userid);
		}
		pr.println(s);
		pr.flush();
	}
}
