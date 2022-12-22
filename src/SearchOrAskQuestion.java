

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class SearchOrAskQuestion
 */
@WebServlet("/SearchOrAskQuestion")
public class SearchOrAskQuestion extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public SearchOrAskQuestion() {
        super();
        // TODO Auto-generated constructor stub
    }
    protected void swap(int a[],int b[],int l,int r){
    	int t;
    	while(l<r){
    		t=a[l];a[l]=a[r];a[r]=t;
    		t=b[l];b[l]=b[r];b[r]=t;
    		l++;r--;
    	}
    }
    
    
	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String search,s="",id="",qid="",Q,question,ask,tag,tags[];
		int i,n,count=0,index[],points[],idx,tl;
		boolean valid=true;
		long start,end,time;
		PrintWriter pr=response.getWriter();
		HttpSession ses=request.getSession(false);
		MergeSort ms=new MergeSort();
		Functions fob=new Functions(); fob.noCache(response);
		if(fob.validate(ses)==false) { response.sendRedirect("Login");return;}
		
		////////////////////////// Home ///////////////////////////////////////////////
		
		String who="",questions[],t,stime;
		try{
			try{
				id=ses.getAttribute("user_id").toString();who="user";
			}catch(Exception p){
				id=ses.getAttribute("admin_id").toString();who="admin";
			}
		}catch(Exception e){ response.sendRedirect("Login");return;}

		question=request.getParameter("question");
		search=request.getParameter("searchquestion");
		ask=request.getParameter("askquestion");
		tag=request.getParameter("tag");

		if(fob.validId(id)==false) valid=false;
		if(fob.validText(question)==false) valid=false;
		if(fob.validTag(tag)==false) tag="";
		if(valid==false) { response.sendRedirect("Error");return;}
		
		if(question.length()<10) return;
		if(question.length()>200) question=question.substring(0, 200);
		
		if(who.equals("user")) s=fob.head(id)+fob.navbar(ses);
		else if(who.equals("admin")==true) s=fob.head(id)+fob.navbarAdmin(ses);
		
		tags=fob.parseTag(tag); tl=tags.length;
		
		if(search!=null){
			
			s+=
					"<div class=\"container\" id=\"bodycontainer\">"+
						"<div class=\"row\">"+
							"<div class=\"col-md-6 col-md-offset-3\">"+
								"<div class=\"page-header\">"+
									"<h4 class=\"pageheading\">Search result</h4>"+
								"</div>"+
								"<div id=\"maindiv\">";
			
			try{
				Class.forName("com.mysql.jdbc.Driver").newInstance();
				Connection cn=DriverManager.getConnection("jdbc:mysql://localhost:3306/Qfire","root","qazzaq");
				Statement smt=cn.createStatement();
				Q="select * from questions limit 1000000";
				ResultSet rs=smt.executeQuery(Q);
				searchquestion=question.toLowerCase().split(" ");
				searchtags=tags;
				
				start=System.currentTimeMillis(); ///////////// start time 
				n=0;points=new int[1000000];index=new int[1000000];questions=new String[100];
				while(rs.next()==true){
					index[n]=n;
					points[n]=findPoints(rs.getString("question").toLowerCase(), rs.getString("id"));
					n++;
				}
				
				
				ms.sort(points, index, 0, n); // sorting the pair according to points in desc
				for(i=0;i<100;i++) if(points[i]==0) break;
				count=0;
				if(i>0){
					ms.sort(index, points, 0, i); // sorting the pair according to index in desc
					swap(index,points,0,i-1); // sort by asc
					rs.beforeFirst();
					n=0;idx=0;
					while(rs.next()==true){
						if(index[idx]==n){       // questionid stored in index[] is equal to n means this is that question 
							questions[idx]=fob.question(id, "searchoraskquestion", rs);
							index[idx]=idx;  // change index range to  0 - (i-1) show that it can be map to questions[]
							idx++;
							if(idx==i) break;
						}
						n++;
					}
					
					ms.sort(points, index, 0, i);count=0;t=""; // sort by points to display
					for(idx=0;idx<i;idx++){
						if(questions[idx].equals("Failed")==true||questions[idx].length()==0) continue;
						t+=				"<table id=\"searchquestionstable\" class=\"table table-hover\">"+
											"<tr>"+
												"<td>"+
													questions[index[idx]]+
												"</td>"+
											"</tr>"+
										"</table>";
						count++;
					}
					end=System.currentTimeMillis();
					time=(end-start)/1000;
					stime=time+" sec";
					if(time>60){
						stime=(int)((time/60.0)*100)/100.0+" min";
					}
					s+=					"<span class=\"t-blue\">"+count+" results</span><span class=\"t-grey t-sm\"> ("+stime+")</span></br>";
					s+=t;
				}
				if(count==0) s+=		"<span class=\"t-md t-red\">No questions found please try including some more words :(</span>";
				
				rs.close();smt.close();cn.close();
			}catch(Exception e){
			}
			s+=					"</div>"+
							"</div>"+
						"</div>"+
					"</div>"+
				"</body>"+
			"</html>";
			
			
			
			
		} else if(ask!=null){
			
			try{
				Class.forName("com.mysql.jdbc.Driver").newInstance();
				Connection cn=DriverManager.getConnection("jdbc:mysql://localhost:3306/Qfire","root","qazzaq");
				Statement smt=cn.createStatement();
				PreparedStatement psmt;
				cn.setAutoCommit(false);
				
				try{
					psmt=cn.prepareStatement("insert into questions (author,upvote,downvote,question,datetime,answercount,commentcount) values ('"+id+"',0,0,?,now(),0,0)");
					psmt.setString(1, question);
					psmt.executeUpdate();
					
					Q="select * from questions where author='"+id+"' order by datetime desc limit 1";
					ResultSet rs=smt.executeQuery(Q);
					while(rs.next()) qid=rs.getString("id");
					
					///////// inserting tags //////////////////////////////
					psmt=cn.prepareStatement("insert into tag_map values(?,"+qid+",now())");
					for(int j=0;j<tl;j++){
						if(tags[j].length()==0) continue;
						rs=smt.executeQuery("select id from tags where tag='"+tags[j]+"'");
						if(rs.next()==true){
							psmt.setInt(1, rs.getInt("id"));
						} else{
							smt.executeUpdate("insert into tags values (null,'"+id+"','"+tags[j]+"',1,now())");
							rs=smt.executeQuery("select id from tags where tag='"+tags[j]+"'");
							if(rs.next()==true) psmt.setInt(1, rs.getInt("id"));
						}
						smt.executeUpdate("update tags set used=used+1 where tag='"+tags+"'");
						psmt.executeUpdate();
					}
					cn.commit();rs.close();psmt.close();
				}catch(Exception e){}

				cn.rollback();smt.close();cn.close();
				response.sendRedirect("Question?id="+qid); return;
			}catch(Exception e){
			}
		} else s="error please try again";
		
		pr.println(s);
		pr.flush();
		
	}

	
	
	
	
	//// class methods //////////////////////////
	String searchquestion[],question[],searchtags[],questiontags[];
	int sql,ql,stl,qtl;
	
	int findPoints(String q,String questionid){
		Functions fob=new Functions();
		
		int points=0,i,j;
		
		questiontags=fob.getTags(questionid);
		question=q.split(" ");
		ql=question.length;
		
		qtl=questiontags.length; if(questiontags[0].length()==0) qtl=0;  // no tags
		sql=searchquestion.length;
		stl=searchtags.length; if(searchtags[0].length()==0) stl=0; // no tags
		/////////////////////// find points related to tag ///////////
		Arrays.sort(questiontags);Arrays.sort(searchtags);
		
		i=0;j=0;
		while(i<stl&&j<qtl){
			if(searchtags[i].equals(questiontags[j])==true){
				points+=50;i++;j++;
			} else if(searchtags[i].compareTo(questiontags[j])>0){
				j++;
			} else i++;
		}
		
		/////////////////////////////// end  //////////////////////
		
		i=0;j=sql<ql?sql:ql;
		while(i<j&&searchquestion[i].equals(question[i])) points+=i+++1<<1;
		i=1;
		while(i<=j&&searchquestion[sql-i].equals(question[ql-i])) points+=i++<<1;
		if(searchquestion.length==question.length&&searchquestion.toString().equals(question.toString())) points+=10;
		
		for(i=0;i<sql;i++){
			for(j=0;j<ql;j++){
				if(question[j].equals("")) continue;
				if(searchquestion[i].equals(question[j])){
					points++;question[j]="";break;
				}
			}
		}
		return points;
	}
}
