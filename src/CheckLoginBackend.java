

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
 * Servlet implementation class CheckLoginBackend
 */
@WebServlet("/CheckLoginBackend")
public class CheckLoginBackend extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public CheckLoginBackend() {
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
		
		String id,password,Q,s="failed";
		int c=0;
		boolean valid=true;

		id=request.getParameter("id");
		password=request.getParameter("password");
		if(fob.validId(id)==false) valid=false;
		if(fob.validPassword(password)==false) valid=false;
		if(valid==false) return;
		
		try{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection cn=DriverManager.getConnection("jdbc:mysql://localhost:3306/Qfire","root","qazzaq");
			Statement smt=cn.createStatement();
			Q="select * from users where user_id='"+id+"'";
			ResultSet rs=smt.executeQuery(Q);
			while(rs.next()) c++;
			rs.first();
			if(c==1&&rs.getString("user_id").equals(id)==true&&rs.getString("user_pass").equals(password)==true){
				s="User";
			} else if(c==0){
				Q="select * from admin where admin_id='"+id+"'";
				rs=smt.executeQuery(Q);
				while(rs.next()) c++;
				rs.first();
				if(c==1&&rs.getString("admin_id").equals(id)==true&&rs.getString("admin_pass").equals(password)==true){
					s="Admin";
				}
			}
			rs.close();smt.close();cn.close();
		}catch(Exception e){
			pr.println("error");
		}
		pr.println(s);
		pr.flush();
	}
}
