

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class SortByAnswerTab
 */
@WebServlet("/SortByAnswerTab")
public class SortByAnswerTab extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SortByAnswerTab() {
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
		
		String id="",s="Failed",userid,tab,questionid,page,who;
		boolean valid=true;
		
		try{
			try{
				id=ses.getAttribute("user_id").toString();who="user";
			}catch(Exception p){
				id=ses.getAttribute("admin_id").toString();who="admin";
			}
		}catch(Exception e){ response.sendRedirect("Login");return;}

		tab=request.getParameter("tab");
		userid=request.getParameter("userid");
		questionid=request.getParameter("questionid");
		page=request.getParameter("page");
	
		if(fob.validName(tab)==false) valid=false;
		if(fob.validId(userid)==false) valid=false;
		if(fob.validNumber(questionid)==false) valid=false;
		if(fob.validName(page)==false) valid=false;
		if(userid.equals(id)==false) valid=false;
		if(valid==false) { response.sendRedirect("Error");return;}
		
		if(tab.equals("popularity")==true){
			s=fob.popularAnswers(questionid, id, page);
		} else if(tab.equals("oldest")==true){
			s=fob.oldAnswers(questionid, id, page);
		} else if(tab.equals("newest")==true){
			s=fob.newAnswers(questionid, id, page);
		} else if(tab.equals("myanswer")==true){
			s=fob.myAnswers(questionid, id, page);
		}
		pr.println("Success "+s);
		pr.flush();
		
	}

}
