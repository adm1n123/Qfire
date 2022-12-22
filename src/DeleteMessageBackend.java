

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
 * Servlet implementation class DeleteMessageBackend
 */
@WebServlet("/DeleteMessageBackend")
public class DeleteMessageBackend extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DeleteMessageBackend() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	}
	
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
	
		PrintWriter pr=response.getWriter();
		HttpSession ses=request.getSession(false);
		Functions fob=new Functions();
		if(fob.validate(ses)==false) { response.sendRedirect("Login");return;}
		
		String s="Failed",who="",messageid,Q,id="",userid,operation,page;
		boolean valid=true;
		
		try{
			try{
				id=ses.getAttribute("user_id").toString();who="user";
			}catch(Exception p){
				id=ses.getAttribute("admin_id").toString();who="admin";
			}
		}catch(Exception e){ response.sendRedirect("Login");return;}
		
		operation = request.getParameter("operation");
		page=request.getParameter("page");
		userid=request.getParameter("userid");
		if(fob.validId(userid)==false) valid=false;
		if(id.equals(userid)==false) valid=false;
		if(fob.validName(page)==false) valid=false;
		if(fob.validName(operation)==false) valid=false;
		if(valid==false) return;
		
		try{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection cn=DriverManager.getConnection("jdbc:mysql://localhost:3306/Qfire","root","qazzaq");
			Statement smt=cn.createStatement();
			PreparedStatement psmt;
			ResultSet rs;
			cn.setAutoCommit(false);
			
			try{
				
				if(operation.equals("one")==true){
					messageid=request.getParameter("messageid");
					if(fob.validNumber(messageid)==false) valid=false;
					if(valid==false) return;
					
					if(page.equals("inbox")==true){
						try{ /////////////// moving to recycle bin //////////////
							Q="select * from inbox where id="+messageid+" limit 1";
							rs=smt.executeQuery(Q);
							if(rs.next()==true){
								psmt=cn.prepareStatement("insert into recycle_bin values (null,'message','"+rs.getString("id")+"','"+rs.getString("sender")+"','"+rs.getString("datetime")+"',?,now())");
								psmt.setString(1,"receiver:"+rs.getString("receiver")+", status:"+rs.getString("status")+", message:-"+rs.getString("message"));
								psmt.executeUpdate();
								psmt.close();
							}
						}catch(Exception e){}
					}

					
					
					Q="delete from "+page+" where id="+messageid+" limit 1";
					smt.executeUpdate(Q);
					s="Success";
				} else if(operation.equals("read")==true){
					Q="delete from inbox where (receiver='"+id+"' and status=1)";
					smt.executeUpdate(Q);
					s="Success";
				} else if(operation.equals("all")==true){
					
					if(page.equals("inbox")==true){
						try{ /////////////// moving to recycle bin //////////////
							Q="select * from inbox where sender='"+id+"'";
							rs=smt.executeQuery(Q);
							while(rs.next()==true){
								psmt=cn.prepareStatement("insert into recycle_bin values (null,'message','"+rs.getString("id")+"','"+rs.getString("sender")+"','"+rs.getString("datetime")+"','receiver:"+rs.getString("receiver")+", status:"+rs.getString("status")+",?,now())");
								psmt.setString(1," message:-"+rs.getString("message"));
								psmt.executeUpdate();
								psmt.close();
							}
						}catch(Exception e){}
					}
					
					Q="delete from "+page+" where ";
					if(page.equals("inbox")) Q+="receiver";
					else Q+="sender";
					Q+="='"+id+"'";
					smt.executeUpdate(Q);
					s="Success";
				}
				cn.commit();
			}catch(Exception e){}
			

			cn.rollback();smt.close();cn.close();
		}catch(Exception e){
		}
		pr.println(s);
		pr.flush();
	}
}
