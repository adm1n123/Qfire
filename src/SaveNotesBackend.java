

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
 * Servlet implementation class SaveNotesBackend
 */
@WebServlet("/SaveNotesBackend")
public class SaveNotesBackend extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SaveNotesBackend() {
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
		
		String userid,id,notes,Q,who="",s="Failed";
		boolean valid=true;
		
		try{
			try{
				id=ses.getAttribute("user_id").toString();who="user";
			}catch(Exception p){
				id=ses.getAttribute("admin_id").toString();who="admin";
			}
		}catch(Exception e){ response.sendRedirect("Login");return;}


		userid=request.getParameter("id");
		notes=request.getParameter("notes");
		if(fob.validId(userid)==false) valid=false;
		if(fob.validText(notes)==false) valid=false;
		if(userid.equals(id)==false) valid=false;
		if(valid==false) { response.sendRedirect("Error");return;}
		
		try{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection cn=DriverManager.getConnection("jdbc:mysql://localhost:3306/Qfire","root","qazzaq");
			Statement smt=cn.createStatement();
			PreparedStatement psmt;
			Q="select * from notes where user_id='"+id+"' limit 1";
			ResultSet rs=smt.executeQuery(Q);
			if(rs.next()) psmt=cn.prepareStatement("update notes set notes=?, datetime=now() where user_id='"+id+"' limit 1");
			else psmt=cn.prepareStatement("insert into notes (user_id,notes,datetime) values ('"+id+"',?,now())");
			psmt.setString(1,notes);
			psmt.executeUpdate();
			psmt.close();
			Q="select datetime from notes where user_id='"+id+"' limit 1";
			rs=smt.executeQuery(Q);
			if(rs.next()==true) s="Success "+fob.date(rs.getString("datetime"));
			else s="Success ";
			rs.close();smt.close();cn.close();
		}catch(Exception e){
			response.sendRedirect("Error");
		}
		pr.println(s);
		pr.flush();
	}
}
