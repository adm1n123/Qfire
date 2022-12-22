

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class DeleteAnswer
 */
@WebServlet("/DeleteAnswer")
public class DeleteAnswer extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public DeleteAnswer() {
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
		Functions fob=new Functions(); fob.noCache(response);
		if(fob.validate(ses)==false) { response.sendRedirect("Login");return;}
		
		String s="",id="",who="";
		boolean valid=true;
		
		try{
			try{
				id=ses.getAttribute("user_id").toString();who="user";
			}catch(Exception p){
				id=ses.getAttribute("admin_id").toString();who="admin";
			}
		}catch(Exception e){ response.sendRedirect("Login");return;}
		
		if(who.equals("admin")==false) valid=false;
		if(valid==false) return;
		
		s=fob.head(id)+fob.navbarAdmin(ses)+
				"<div class=\"container\" id=\"bodycontainer\">"+
						"<p class=pageheading>Delete answer</p></br>"+
						"<table>"+
							"<tr>"+
								"<td class=entity>Answer Id </td><td><input type=text id=answerid onkeyup=checkAnswerExist();> <span id=answeridspan></span></td>"+
							"</tr>"+
							"<tr><td></br></td><td></br></td></tr>"+
							"<tr>"+
								"<td></td><td  style=\"padding-left:100px;\"><button class=deletebutton disabled=true id=deletebutton onclick=deleteAnswer();>Delete</button></td>"+
							"</tr>"+
						"</table>"+
					"</div>"+
				"</body>"+
		"</html>";
		pr.println(s);
		pr.flush();
	}

}
