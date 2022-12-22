
// Qfire
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class Logout
 */
@WebServlet("/Logout")
public class Logout extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Logout() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		Functions fob=new Functions(); fob.noCache(response);
		boolean valid=true;
		String id,who,ext;
		HttpSession ses=request.getSession(false);
		try{
			try{
				id=ses.getAttribute("user_id").toString();who="user";ext="s";
			}catch(Exception p){
				id=ses.getAttribute("admin_id").toString();who="admin";ext="";
			}
			ses.removeAttribute("user_id");
			ses.removeAttribute("user_fname");
			ses.removeAttribute("Time");
			ses.removeAttribute("emailhash");
			ses.removeAttribute("key");
			ses.invalidate();
			if(fob.validId(id)==false) valid=false;
			if(valid==true){
				try{
		    		Class.forName("com.mysql.jdbc.Driver").newInstance();
					Connection cn=DriverManager.getConnection("jdbc:mysql://localhost:3306/Qfire","root","qazzaq");
					Statement smt=cn.createStatement();
					smt.executeUpdate("delete from active_"+who+ext+" where "+who+"_id='"+id+"'");
					smt.close();cn.close();
				}catch(Exception p){
				}
			}
			
		}catch(Exception e){
			
		}
		response.sendRedirect("Login");
	}
}
