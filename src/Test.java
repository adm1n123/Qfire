

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class Test
 */
@WebServlet("/Test")
public class Test extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Test() {
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
		String s="<!DOCTYPE html>" + 
				"<html lang=\"en\">" + 
					"<head>" + 
						"<title>Qfire</title>"+
						"<meta charset=\"utf-8\">"+
						"<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">" + 
						"<link rel=\"stylesheet\" href=\"font-awesome-4.7.0/css/font-awesome.min.css\">" + 
						"<link rel=\"stylesheet\" href=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/css/bootstrap.min.css\">" +
						
						"<script src=\"https://ajax.googleapis.com/ajax/libs/jquery/3.1.1/jquery.min.js\"></script>" + // this must be a less prior than bootstrapcdn
						"<script src=\"https://maxcdn.bootstrapcdn.com/bootstrap/3.3.7/js/bootstrap.min.js\"></script>" + // must be after jquery.min
						
				//		"<link rel=\"stylesheet\" href=\"https://ajax.googleapis.com/ajax/libs/jqueryui/1.12.1/themes/smoothness/jquery-ui.css\">"+
				//		"<script src=\"https://ajax.googleapis.com/ajax/libs/jqueryui/1.12.1/jquery-ui.min.js\"></script>"+
						
						"<script src=\"script.js\"></script>"+
						"<link rel=\"stylesheet\" href=\"style.css\" type=\"text/css\" media=\"all\"/>"+
				"</head>" +
				"<body>";
		 s+=		"<div class=\"container\">"+
				 		"<input class=\"login-field login-field-password\" id=\"searchaskinput\" onfocus=yes(); placeholder=\"Password\">"+
				 		"<div class=\"modal fade\" id=\"modal\" role=\"dialog\">"+
				 		"<div class=\"modal-dialog\">"+
				 		"<div class=\"modal-content\">"+
				 		"<div class=\"modal-header\">"+
						"<button type=\"button\" class=\"close\" data-dismiss=\"modal\">&times;</button>"+
						"<h4 class=\"modal-title\">Modal Header</h4>"+
						"</div>"+
						"<div class=\"modal-body\">"+
						"<p>Some text in the modal.</p>"+
						"</div>"+
						"<div class=\"modal-footer\">"+
						"<button type=\"button\" class=\"btn btn-default\" data-dismiss=\"modal\">Close</button>"+
					"</div>"+
				"</div>"+
			"</div>"+
		"</div>"+
					"</div>"+
				"</body>"+
		"</html>";
		pr.println(s);
		pr.flush();
		
		
		
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
