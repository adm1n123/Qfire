

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
 * Servlet implementation class Edit
 */
@WebServlet("/Edit")
public class Edit extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Edit() {
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
		
		String id="",who="",editor,typeid,type,text,Q,tag,tags[],questiontags[],s="Error",fname="",lname="",notification,date,authorq,questionid,typeof,typeofid,typeofauthor,Typeof;
		boolean valid=true;
		int tl,qtl;

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

		editor=request.getParameter("userid");
		typeid=request.getParameter("typeid");
		type=request.getParameter("type");
		text=request.getParameter("text");
		tag=request.getParameter("tag");
		
		if(fob.validId(editor)==false) valid=false;
		if(fob.validNumber(typeid)==false) valid=false;
		if(fob.validName(type)==false) valid=false;
		if(fob.validText(text)==false) valid=false;
		if(fob.validTag(tag)==false) tag="";
		if(editor.equals(id)==false) valid=false;
		if(type.equals("question")==false&&type.equals("answer")==false&&type.equals("comment")==false) valid=false;
		if(valid==false) { 
			request.setAttribute("error", "<font color=\"red\">Error occured try again</font>");
			request.getRequestDispatcher("Error").forward(request, response);
			return;
		}
		if(type.equals("question")==true&&text.length()>200) text=text.substring(0, 200);
		if(type.equals("answer")==true&&text.length()>2000) text=text.substring(0, 2000);
		if(type.equals("comment")==true&&text.length()>200) text=text.substring(0, 200);
		
		try{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection cn=DriverManager.getConnection("jdbc:mysql://localhost:3306/Qfire","root","qazzaq");
			Statement smt=cn.createStatement();
			PreparedStatement psmt;
			cn.setAutoCommit(false);
			try{
				Q="select * from "+type+"s where id="+typeid+" and author='"+id+"' limit 1";
				ResultSet rs=smt.executeQuery(Q);
				if(who.equals("user")==true&&rs.next()==true||who.equals("admin")==true){
					psmt=cn.prepareStatement("update "+type+"s set "+type+"=? where id="+typeid+" limit 1");
					psmt.setString(1,text);
					psmt.executeUpdate();
					psmt.close();
					if(type.equals("question")==true){
						
						//////// updating tags ////////////////
						tags=fob.parseTag(tag); tl=tags.length;
						questiontags=fob.getTags(typeid); qtl=questiontags.length;
						
						for(int i=0;i<tl;i++){
							for(int j=0;j<qtl;j++){
								if(tags[i].equals(questiontags[j])==true) tags[i]=questiontags[j]="";
							}
						}
						for(int i=0;i<qtl;i++){
							if(questiontags[i].length()>0) smt.executeUpdate("delete from tag_map where tag_id=(select id from tags where tag='"+questiontags[i]+"' limit 1) and question_id="+typeid);
						}
						
						psmt=cn.prepareStatement("insert into tag_map values(?,"+typeid+",now())");
						for(int j=0;j<tl;j++){
							if(tags[j].length()==0) continue;
							rs=smt.executeQuery("select id from tags where tag='"+tags[j]+"'");
							if(rs.next()==true){
								psmt.setInt(1, rs.getInt("id"));
							} else{
								smt.executeUpdate("insert into tags values (null,'"+id+"','"+tags[j]+"',1,now())");
								rs=smt.executeQuery("select id from tags where tag='"+tags[j]+"'");
								if(rs.next()==true) psmt.setInt(1, rs.getInt("id"));
							}
							smt.executeUpdate("update tags set used=used+1 where tag='"+tags+"'");
							psmt.executeUpdate();
						}
						///\\\\\\\\\\\ updating tags /////\\\\\\//////\\\\\\\\\\////////\\\\\\\
						
						psmt=cn.prepareStatement("update readinglist set question=? where question_id="+typeid);
						psmt.setString(1,text);
						psmt.executeUpdate();
						psmt.close();
						////////// sending notification //////////////////////
						HashSet<String> idset=new HashSet<String>();
						date=fob.date();
						notification="<span><a href=\"Profile?id="+id+"\">"+fname+" "+lname+"</a> edited <a href=\"Question?id="+typeid+"\">question</a> you have answered. <span class=\"t-grey t-sx\"> "+date+"</span></span>";
						Q="select author from answers where question_id="+typeid;
						rs=smt.executeQuery(Q);
						while(rs.next()==true) idset.add(rs.getString("author"));
						idset.remove(id);
						fob.sendNotification(idset, notification);
						idset.clear();
						notification="<span><a href=\"Profile?id="+id+"\">"+fname+" "+lname+"</a> edited <a href=\"Question?id="+typeid+"\">question</a> you commented on. <span class=\"t-grey t-sx\"> "+date+"</span></span>";
						Q="select author from comments where type='question' and typeid="+typeid;
						rs=smt.executeQuery(Q);
						while(rs.next()==true) idset.add(rs.getString("author"));
						idset.remove(id);
						fob.sendNotification(idset, notification);
						idset.clear();rs.close();
					} else if(type.equals("answer")==true){  ////////// sending notification //////////////////////
						Q="select question_id from answers where id="+typeid+" limit 1";
						rs.close(); rs=smt.executeQuery(Q);rs.next();
						questionid=rs.getString("question_id");
						HashSet<String> idset=new HashSet<String>();
						Q="select author from questions where id="+questionid+" limit 1";
						rs=smt.executeQuery(Q);
						if(rs.next()==true){
							authorq=rs.getString("author");
							date=fob.date();
							notification="<span><a href=\"Profile?id="+id+"\">"+fname+" "+lname+"</a> edited <a href=\"Answer?id="+typeid+"\">answer</a> you commented on. <span class=\"t-grey t-sx\"> "+date+"</span></span>";
							Q="select author from comments where type='answer' and typeid="+typeid;
							rs=smt.executeQuery(Q);
							while(rs.next()==true) idset.add(rs.getString("author"));
							idset.remove(authorq);idset.remove(id);
							fob.sendNotification(idset, notification);
							idset.clear();idset.add(authorq);idset.remove(id);
							notification="<span><a href=\"Profile?id="+id+"\">"+fname+" "+lname+"</a> edited answer of your <a href=\"Question?id="+questionid+"\">question</a>. <span class=\"t-grey t-sx\"> "+date+"</span><span>";
							fob.sendNotification(idset, notification);
							idset.clear();
						}
						rs.close();
					} else if(type.equals("comment")==true){  ////////// sending notification //////////////////////
						Q="select * from comments where id="+typeid+" limit 1";
						rs.close(); rs=smt.executeQuery(Q);rs.next();
						typeof=rs.getString("type");
						typeofid=rs.getString("typeid");
						if(type.equals("question")==true) Typeof="Question";else Typeof="Answer";
						HashSet<String> idset=new HashSet<String>();
						Q="select author from "+typeof+"s where id="+typeofid+" limit 1";
						rs=smt.executeQuery(Q);
						if(rs.next()==true){
							typeofauthor=rs.getString("author");
							date=fob.date();
							notification="<span><a href=\"Profile?id="+id+"\">"+fname+" "+lname+"</a> edited comment on <a href=\""+Typeof+"?id="+typeofid+"\">"+typeof+"</a>. <span class=\"t-grey t-sx\"> "+date+"</span></span>";
							Q="select author from comments where type='"+typeof+"' and typeid="+typeofid;
							rs=smt.executeQuery(Q);
							while(rs.next()==true) idset.add(rs.getString("author"));
							idset.remove(typeofauthor);idset.remove(id);
							fob.sendNotification(idset, notification);
							idset.clear();idset.add(typeofauthor);idset.remove(id);
							notification="<span><a href=\"Profile?id="+id+"\">"+fname+" "+lname+"</a> edited comment on your <a href=\""+Typeof+"?id="+typeofid+"\">"+typeof+"</a>. <span class=\"t-grey t-sx\"> "+date+"</span><span>";
							fob.sendNotification(idset, notification);
							idset.clear();
						}
						rs.close();
					}
					cn.commit();rs.close();
					s="Success";
				}
			}catch(Exception e){}
			cn.rollback();smt.close();cn.close();
		}catch(Exception e){
		}
		try{
		}catch(Exception e){}
		
		pr.println(s);
		pr.flush();
	}
}
