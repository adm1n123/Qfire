

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
 * Servlet implementation class TypeQuestion
 */
@WebServlet("/TypeQuestion")
public class TypeQuestion extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public TypeQuestion() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String id="",s="",who="",question[],words[],count[],pre="",second;
		boolean valid=true,found=false;
		int ql,len,wl,listlength=10;
		char c;
		PrintWriter pr=response.getWriter();
		HttpSession ses=request.getSession(false);
		Functions fob=new Functions(); fob.noCache(response);
		if(fob.validate(ses)==false) { response.sendRedirect("Login");return;}
		
		try{
			try{
				id=ses.getAttribute("user_id").toString();who="user";
			}catch(Exception p){
				id=ses.getAttribute("admin_id").toString();who="admin";
			}
		}catch(Exception e){ response.sendRedirect("Login");return;}
		
		question=request.getParameter("question").split(" "); ql=question.length-1;
		
		if(fob.validId(id)==false) valid=false;
		if(valid==false) { response.sendRedirect("Error");return;}
		for(int i=0;i<=ql;i++){
			len=question[i].length();
			if(len==0||len>45||len==1&&fob.onlyAlphabet(question[i])==0){
				question[i]="";continue;
			}
			c=question[i].charAt(len-1);
			if(c=='.'||c==','||c=='?') question[i]=question[i].substring(0, len-1);
		}
		
		try{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			Connection cn=DriverManager.getConnection("jdbc:mysql://localhost:3306/Qfire","root","qazzaq");
			PreparedStatement psmt;
			ResultSet rs;
			psmt=cn.prepareStatement("select * from words where word like ? order by used desc");
			while(found==false&&ql>=0){
				if(question[ql].length()==0){
					ql--;continue;
				}
				found=true; // last word found
			
				for(int i=0;i<ql;i++) pre+=question[i]+" "; // rest question words
				
				psmt.setString(1, question[ql]+"%");
				rs=psmt.executeQuery();
				
				if(rs.next()==true){ // if exist then 
					words=new String[10]; wl=0;
					do{
						words[wl]=rs.getString("word");
						if(wl<2) s+="<option class=\"t-md\" value=\""+pre+words[wl]+"\">";		// max 2 auto complete words
						wl++;
					}while(rs.next()==true&&wl<10);
					psmt.close();
					psmt=cn.prepareStatement("select * from word_predict where first=? order by used desc");
					for(int i=0,j=0;i<wl&&j<2;i++){		// max 2 predicted words
						psmt.setString(1, words[i]);
						rs=psmt.executeQuery();
						if(rs.next()==true){
							second=" "+rs.getString("second");
							s+="<option class=\"t-md\" value=\""+pre+words[i]+second+" \">";
							j++;
						}
					}
				}
				rs.close();
			}
			s="Success%;%"+s+"%;%";// separator to find out result;
			psmt.close();cn.close();
		}catch(Exception e){}
		pr.println(s);
		pr.flush();
	}
}
