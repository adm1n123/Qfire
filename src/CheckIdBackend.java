

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
 * Servlet implementation class CheckIdBackend
 */
@WebServlet("/CheckIdBackend")
public class CheckIdBackend extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CheckIdBackend() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		PrintWriter pr=response.getWriter();
		Functions fob=new Functions();
		
		String id,Q,s="Error";
		boolean valid=true;
		
		id=request.getParameter("id");
		if(fob.validId(id)==false) valid=false;
		if(valid==false) return;
		
		try{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection cn=DriverManager.getConnection("jdbc:mysql://localhost:3306/Qfire","root","qazzaq");
			Statement smt=cn.createStatement();
			Q="select * from admin where admin_id='"+id+"'";
			ResultSet rs=smt.executeQuery(Q);
			if(rs.next()){
				s="A Exist"; //Admin Exist
			}
			else{
				try{
					Q="select * from users where user_id='"+id+"'";
					rs=smt.executeQuery(Q);
					if(rs.next()){
						s="U Exist"; //User Exist
					}
					else s="Not exist";
				}catch(Exception e){
				}
			}
			rs.close();smt.close();cn.close();
		}catch(Exception e){
			pr.println("failed");
		}
		if(id.toLowerCase().equals("admin")==true) s="U Exist"; // not really :)
		pr.println(s);
		pr.flush();
	}
}
