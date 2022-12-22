//Qfire

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
 * Servlet implementation class SearchUserBackend
 */
@WebServlet("/SearchUserBackend")
public class SearchUserBackend extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchUserBackend() {
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
		
		String s="",t="",who="",userid,id,name[],fname="",lname="",Q,all="false";
		int c=0;
		boolean valid=true;
		
		try{
			try{
				id=ses.getAttribute("user_id").toString();who="user";
			}catch(Exception p){
				id=ses.getAttribute("admin_id").toString();who="admin";
			}
		}catch(Exception e){ response.sendRedirect("Login");return;}

		if(fob.validId(id)==false) valid=false;
		if(fob.validName(request.getParameter("By"))==false) valid=false;
		if(valid==false){
			response.sendRedirect("Login");return;
		}
		
		if(request.getParameter("By").equals("id")){
			userid=request.getParameter("userid");
			try{
				if(userid.equals("*")==true) all="true";
			}catch(Exception e){}
			 ///////////////// by id all //////////////////////////
			if(all.equals("true")==true){
				try{
					Class.forName("com.mysql.jdbc.Driver").newInstance();
					Connection cn=DriverManager.getConnection("jdbc:mysql://localhost:3306/Qfire","root","qazzaq");
					Statement smt=cn.createStatement();
					Q="select * from users order by user_id asc";
					ResultSet rs=smt.executeQuery(Q);
					c=0;
					if(rs.next()==true){
						do{
							s+="<tr>"+
									"<td><a href=\"Profile?id="+rs.getString("user_id")+"\">"+rs.getString("user_id")+"</a></td>"+
									"<td>"+rs.getString("user_fname")+" "+rs.getString("user_lname")+"</td>"+
									"<td>"+rs.getString("user_country")+"</td>"+
							   "</tr>";
							c++;
						}while(rs.next()==true);
					}
						s="<span style=\"font-size:12pt;color:green;\">"+c+" results found</font></span>"+
								"<table class=\"table table-striped table-hover\"><tr><th> User Id </th><th> User Name </th><th> Country </th></tr>"+s+"</table>";
					rs.close();smt.close();cn.close();
				}catch(Exception e){
					pr.println("<font color=\"red\">Error try again</font>");
				}
				pr.println(s);
				pr.flush();
				return;
			}
			
			///////////////// by id only one ///////////////////
			
			if(fob.validId(userid)==false||userid.length()>45) valid=false;
			if(valid==false){
				pr.print("<font color=\"red\">Invalid user id</font>");
				pr.flush();
				return;
			}
			userid=userid.toLowerCase();
			try{
				Class.forName("com.mysql.jdbc.Driver").newInstance();
				Connection cn=DriverManager.getConnection("jdbc:mysql://localhost:3306/Qfire","root","qazzaq");
				Statement smt=cn.createStatement();
				Q="select * from users where user_id='"+userid+"' limit 1";
				ResultSet rs=smt.executeQuery(Q);
				if(rs.next()==true){
					s="<table class=\"table table-striped table-hover\"><tr><th> User Id </th><th> User Name </th><th> Country </th></tr>";
					s+="<tr>"+
							"<td><a href=\"Profile?id="+rs.getString("user_id")+"\">"+rs.getString("user_id")+"</a></td>"+
							"<td>"+rs.getString("user_fname")+" "+rs.getString("user_lname")+"</td>"+
							"<td>"+rs.getString("user_country")+"</td>"+
					   "</tr>"+
					   "</table>";
				} else{
					Q="select * from admin where admin_id='"+userid+"' limit 1";
					rs=smt.executeQuery(Q);
					if(rs.next()==true){
						s="<table class=\"table table-striped table-hover\"><tr><th> Admin Id </th><th> Admin Name </th><th> Country </th></tr>";
						s+="<tr>"+
								"<td><a href=\"Profile?id="+rs.getString("admin_id")+"\">"+rs.getString("admin_id")+"</a></td>"+
								"<td>"+rs.getString("admin_fname")+" "+rs.getString("admin_lname")+"</td>"+
								"<td>NA</td>"+
						   "</tr>"+
						   "</table>";
					} else s="<span style=\"font-size:12pt;color:orange;\">0 results found</font></span>";
				}
				rs.close();smt.close();cn.close();
			}catch(Exception e){
				pr.println("<font color=\"red\">Error try again</font>");
			}
		} else{
			/////////////////////// by name all /////////////////////////////////////
			try{
				if(request.getParameter("name").equals("*")==true) all="true";
			}catch(Exception e){}
			if(all.equals("true")==true){
				try{
					Class.forName("com.mysql.jdbc.Driver").newInstance();
					Connection cn=DriverManager.getConnection("jdbc:mysql://localhost:3306/Qfire","root","qazzaq");
					Statement smt=cn.createStatement();
					Q="select * from users order by user_fname asc, user_lname desc";
					ResultSet rs=smt.executeQuery(Q);
					c=0;
					if(rs.next()==true){
						do{
							s+="<tr>"+
									"<td><a href=\"Profile?id="+rs.getString("user_id")+"\">"+rs.getString("user_id")+"</a></td>"+
									"<td>"+rs.getString("user_fname")+" "+rs.getString("user_lname")+"</td>"+
									"<td>"+rs.getString("user_country")+"</td>"+
							   "</tr>";
							c++;
						}while(rs.next()==true);
						s="<span style=\"font-size:12pt;color:green;\">"+c+" results found</font></span>"+
								"<table class=\"table table-striped table-hover\"><tr><th> User Id </th><th> User Name </th><th> Country </th></tr>"+s+"</table>";
					}
					rs.close();smt.close();cn.close();
				}catch(Exception e){
					pr.println("<font color=\"red\">Error try again</font>");
				}
				pr.println(s);
				pr.flush();
				return;
			}
			
			/////////////////////// by name /////////////////////////////////////
			name=request.getParameter("name").toLowerCase().split(" ");
			fname=name[0];
			if(name.length==1) lname=name[0];
			else if(name.length==2) lname=name[1];
			
			if(fob.validName(fname)==false) valid=false;
			if(fob.validName(lname)==false) valid=false;
			if(valid==false){
				pr.print("<font color=\"red\">Invalid user name</font>");
				pr.flush();
				return;
			}
			
			try{
				Class.forName("com.mysql.jdbc.Driver").newInstance();
				Connection cn=DriverManager.getConnection("jdbc:mysql://localhost:3306/Qfire","root","qazzaq");
				Statement smt=cn.createStatement();
				Q="select * from users where user_fname='"+fname+"' and user_lname='"+lname+"'";
				ResultSet rs=smt.executeQuery(Q);
				c=0;
				if(rs.next()==true){
					do{
						s+="<tr>"+
								"<td><a href=\"Profile?id="+rs.getString("user_id")+"\">"+rs.getString("user_id")+"</a></td>"+
								"<td>"+rs.getString("user_fname")+" "+rs.getString("user_lname")+"</td>"+
								"<td>"+rs.getString("user_country")+"</td>"+
						   "</tr>";
						c++;
					}while(rs.next()==true);
					s="<span style=\"font-size:12pt;color:green;\">"+c+" results found</font></span>"+
							"<table class=\"table table-striped table-hover\"><tr><th> User Id </th><th> User Name </th><th> Country </th></tr>"+s+"</table>";
				} else{
					Q="select * from users where user_fname='"+fname+"'";
					rs=smt.executeQuery(Q);
					if(rs.next()==true){
						s="";
						do{
							s+="<tr>"+
									"<td><a href=\"Profile?id="+rs.getString("user_id")+"\">"+rs.getString("user_id")+"</a></td>"+
									"<td>"+rs.getString("user_fname")+" "+rs.getString("user_lname")+"</td>"+
									"<td>"+rs.getString("user_country")+"</td>"+
							   "</tr>";
							c++;
						}while(rs.next()==true);
					}
					Q="select * from users where user_lname='"+lname+"'";
					rs=smt.executeQuery(Q);
					if(rs.next()==true){
						do{
							s+="<tr>"+
									"<td><a href=\"UserProfile?id="+rs.getString("user_id")+"\">"+rs.getString("user_id")+"</a></td>"+
									"<td>"+rs.getString("user_fname")+" "+rs.getString("user_lname")+"</td>"+
									"<td>"+rs.getString("user_country")+"</td>"+
							   "</tr>";
								c++;
						}while(rs.next()==true);
					}
					if(s.length()==0){
						s="<span style=\"font-size:12pt;color:orange;\">0 results found</font></span>";
					}
					else{
							s="<span style=\"font-size:12pt;color:green;\">"+c+" results found</font></span>"+
								"<table class=\"table table-striped table-hover\"><tr><th> User Id </th><th> User Name </th><th> Country </th></tr>"+s+"</table>";
					}
				}
				rs.close();smt.close();cn.close();
			}catch(Exception e){
				pr.println("<font color=\"red\">Error try again</font>");
			}
		}
		pr.println(s);
		pr.flush();
	}
}
