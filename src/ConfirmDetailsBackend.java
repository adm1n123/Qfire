

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
 * Servlet implementation class ConfirmDetailsBackend
 */
@WebServlet("/ConfirmDetailsBackend")
public class ConfirmDetailsBackend extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public ConfirmDetailsBackend() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String id="",email="",Q,s="Error";
		boolean valid=true;
		PrintWriter pr=response.getWriter();
		Functions fob=new Functions();
		id=request.getParameter("id");
		email=request.getParameter("email");
		if(id==null||email==null) { response.sendRedirect("Login");return;}
		
		if(fob.validId(id)==false) valid=false;
		if(fob.validEmail(email)==false) valid=false;
		if(fob.validText(email)==false) valid=false;
		if(valid==false) return;
		
		id=id.toLowerCase(); email=email.toLowerCase();
		
		try{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection cn=DriverManager.getConnection("jdbc:mysql://localhost:3306/Qfire","root","qazzaq");
			Statement smt=cn.createStatement();
			Q="select * from admin where admin_id='"+id+"' limit 1";
			ResultSet rs=smt.executeQuery(Q);
			if(rs.next()){
				s="A"; //Admin Exist
			}
			else{
				try{
					Q="select * from users where user_id='"+id+"' limit 1";
					rs=smt.executeQuery(Q);
					if(rs.next()){
						s="U"; //User Exist
					}
					else s="N"; // not exist
				}catch(Exception e){
				}
			}
			Q="select * from users where user_email='"+email+"' limit 1";
			rs=smt.executeQuery(Q);
			if(rs.next()){
				s+=" E";// email exist
			} else {
				rs=smt.executeQuery("select * from admin where admin_email='"+email+"' limit 1");
				if(rs.next()==true) s+=" E";
				else s+=" N";// not exist
			}
			rs.close();smt.close();cn.close();
		}catch(Exception e){
			pr.println("failed");
		}
		pr.println(s);
		pr.flush();
	}
}
