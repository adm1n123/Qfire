

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class Notification
 */
@WebServlet("/Notification")
public class Notification extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Notification() {
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
		
		String id="",notificationid,s="Failed",userid,tab,questionid,page,who,operation,sql,notificationcount="0",fraction;
		int count,totalcount,unreadcount;
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
		notificationid=request.getParameter("notificationid");
		if(fob.validName(operation)==false) valid=false;
		if(operation.equals("one")==true&&fob.validNumber(notificationid)==false) valid=false;
		if(fob.validId(userid)==false) valid=false;
		if(userid.equals(id)==false) valid=false;
		if(valid==false) { response.sendRedirect("Error");return;}
		

		try{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection cn=DriverManager.getConnection("jdbc:mysql://localhost:3306/Qfire","root","qazzaq");
			Statement smt=cn.createStatement();
			PreparedStatement smt2=cn.prepareStatement("delete from notifications where id=?");
			ResultSet rs;
			cn.setAutoCommit(false);

			try{
				if(operation.equals("all")==true){
					sql="update notifications set status=0 where author='"+id+"' and status=1 order by status desc, datetime desc limit 5";
					smt.executeUpdate(sql);
				} else if(operation.equals("one")==true){
					sql="update notifications set status=0 where id="+notificationid+" and author='"+id+"' limit 1";
					smt.executeUpdate(sql);
				}
				sql="select id,status from notifications where author='"+id+"' order by status desc, datetime desc";
				rs=smt.executeQuery(sql);
				count=0;
				while(rs.next()==true){
					count++;
					if(count==5){
						while(rs.next()==true){
							if(rs.getInt("status")==0){
								smt2.setString(1, rs.getString("id"));
								smt2.executeUpdate();
							}
						}
						break;
					}
				}
				s="Success";
				cn.commit();
				//////////// showing notifications /////////////////
				rs=smt.executeQuery("select count(*) as ncount from notifications where status=1 and author='"+id+"'");
				if(rs.next()==true) notificationcount=rs.getString("ncount");
				ses.setAttribute("notificationcount", notificationcount);
				unreadcount=totalcount=Integer.parseInt(notificationcount);
				
				rs=smt.executeQuery("select count(*) as ncount from notifications where author='"+id+"'");
				if(rs.next()==true) totalcount=Integer.parseInt(rs.getString("ncount"));
				
				if(unreadcount>5) fraction="5/"+unreadcount; else fraction=totalcount+"/"+totalcount;
				
				s+="#"+notificationcount+"#";
				
				rs=smt.executeQuery("select * from notifications where author='"+id+"' order by status desc, datetime desc limit 5");
				if(rs.next()==true){
					s+=		"<table class=\"table table-hover\" id=\"notificationtable\">"+
								"<tr><th><button class=\"btn btn-success btn-sm f-right\" onclick=notification(\"all\",\""+id+"\",\"0\");><i class=\"fa fa-check\"></i> Mark all</button>Notifications &nbsp;<span class=\"t-grey\">"+fraction+"</span></th></tr>";
					do{
						if(rs.getInt("status")==1) s+=		
								"<tr><td><button class=\"btnCustom btn-sm b-s-mark f-right\" onclick=notification(\"one\",\""+id+"\",\""+rs.getString("id")+"\");><i class=\"fa fa-check\"></i></button>"+rs.getString("notification")+"</td></tr>";
						else s+="<tr><td>"+rs.getString("notification")+"</td></tr>";
					}while(rs.next()==true);
					s+=		"</table>";
				} else s+="<div class=\"t-blue t-center\">No notification</div>";
				rs.close();
			}catch(Exception e){
			}
			cn.rollback();smt.close();cn.close();
		}catch(Exception e){
		}
		pr.println(s);
		pr.flush();
	}
}
