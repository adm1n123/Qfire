
// Insightful Thoughts

/*
function checkQuestion(){
	var question=document.getElementById("typedquestion").value;
	var typedquestionspan=document.getElementById("typedquestionspan");
	var searchquestion=document.getElementById("searchquestion");
	var askquestion=document.getElementById("askquestion");
	searchquestion.disabled=true;
	askquestion.disabled=true;
	if(noOfAlphabet(question)+noOfDigit(question)>(question.length/2)){
		searchquestion.disabled=false;
		askquestion.disabled=false;
		typedquestionspan.innerHTML="&nbsp;";
	}
	else if(question.length>0) typedquestionspan.innerHTML="<font color=orange>Please include some alphabets or digits</font>";
	else typedquestionspan.innerHTML="&nbsp;";
}
*/
function SearchById(){
	var id=document.getElementById("userid").value;
	var url="SearchUserBackend?By=id&userid="+id;
	if(id==""){
		$("#resultdiv").html("<font color=orange>Please enter user id or name</font>");
		return;
	}
	var req=new XMLHttpRequest();
	req.open("GET",url,true);
	req.send(null);
	req.onreadystatechange=function(){
		if(req.readyState==4){
			var result=req.responseText;
		//	document.getElementById("divid").style.overflow='scroll';
			$("#resultdiv").html(result);
		}
	};
}

function SearchByName(){
	var name=document.getElementById("userid").value;
	if(name==""){
		$("#resultdiv").html("<font color=orange>Please enter user id or name</font>");
		return;
	}
	var req=new XMLHttpRequest();
	var url="SearchUserBackend?By=name&name="+name;
	req.open("GET",url,true);
	req.send(null);
	req.onreadystatechange=function(){
		if(req.readyState==4){
			var result=req.responseText;
		//	document.getElementById("divid").style.overflow='scroll';
			$("#resultdiv").html(result);
		}
	};
}

function aboutMe(){
	var aboutinput=document.getElementById("about");
	var about=aboutinput.value;
	var l=about.length;
	if(l>200) aboutinput.style.borderColor="red";
	else aboutinput.style.borderColor="green";
	return 1;
}

function findMessageLength(){
	var l=document.getElementById("sendtextarea").value.length;
	var button=document.getElementById("sendbutton");
	var r=200-l;
	remainingCharactersSpan("sendtextarea","sendmessagespan",200);
}

function findAdminMessageLength(){
	remainingCharactersSpan("sendtextarea","sendadminmessagespan",5000);
}


function decreaseHeight(id){
	document.getElementById(id).rows="6";
}


function sendMessage(receiver,sender){
	var span=document.getElementById("sendmessagespan");
	var message=document.getElementById("sendtextarea").value;
	if(noOfAlphabet(message)<2){
		span.innerHTML="<span class=\"t-orange\">Please type message</span>"; return;
	}
    $.post("SendMessageBackend",
    	    {
    	    	receiver: receiver,
    			sender: sender,
    	    	message: message
    	    },
    	    function(data, status){
    	    	if(status=="success"){
    	    		if(data.substring(0,7)=="Success"){
    	    			span.innerHTML="<span class=\"t-green\">Sent successfully</span>";
    					document.getElementById("sendtextarea").value="";
    	    		} else span.innerHTML="<span class=\"t-red\">Sending failed</span>";
    	    	}
    	    });
    insertWords(sender,message);
}

function replyMessage(messageid, receiver, sender, element){
	
	if(element=="button"){
		$("#replydiv"+messageid).clearQueue();
		$("#replydiv"+messageid).slideToggle(500);
	} else if(element=="textarea"){
		remainingCharactersSpan("replytextarea"+messageid,"replyspan"+messageid,200);
	} else if(element=="reply"){
		var message=$("#replytextarea"+messageid).val();
		if(message.length<1){
			$("#replyspan"+messageid).html("<font color=\"orange\">please reply</font>"); return;
		}
	    $.post("SendMessageBackend",
	    	    {
	    	    	receiver: receiver,
	    			sender: sender,
	    	    	message: message
	    	    },
	    	    function(data, status){
	    	    	if(status=="success"){
	    	    		if(data.substring(0,7)=="Success"){
	    	    			$("#replyspan"+messageid).html("");
	    	    			$("#replytextarea"+messageid).val("");
	    	    			$("#replydiv"+messageid).hide("slow",function(){
	    	    			});
	    	    		} else $("#replyspan"+messageid).html("<font color=\"red\">Error while replying try again</font>");
	    	    	}
	    	    });
	}
}


function checkIdExist(){ // when someone search user
	var id=document.getElementById("userid").value;
	var span=document.getElementById("useridspan");
	var button=document.getElementById("deletebutton");
	button.disabled=true;
	var req=new XMLHttpRequest();
	var url="CheckIdBackend?id="+id;
	req.open("GET",url,true);
	req.send(null);
	req.onreadystatechange=function(){
		if(req.readyState==4){
			var result=req.responseText;
			var who="Admin";
			if(result.charAt(0)=='U') who="User";
			if(result.charAt(0)=='A'||result.charAt(0)=='U'){
				span.innerHTML="<font color=green>"+who+" Id exist</font>";
				if(result.charAt(0)=='U') button.disabled=false;
			}
			else span.innerHTML="<font color=red>Id does not exist</font>";
		}
		else span.innerHTML="<font color=red>Id does not exist</font>";
	};
}

function checkIdAvailable(){ // when submit account setting form
	var idinput=document.getElementById("id");
	var span=document.getElementById("idspan");
	var id=idinput.value;
	if(id.length==0){
		idinput.style.borderColor="red";
		span.innerHTML="&nbsp;";
		return;
	}
	if(noOfDigit(id)+noOfAlphabet(id)!=id.length||id.charCodeAt(0)>=48&&id.charCodeAt(0)<=57||id.length>20){
		idinput.style.borderColor="red";
		span.innerHTML="&nbsp;";
		return;
	}
	var req=new XMLHttpRequest();
	var url="CheckIdBackend?id="+id;
	req.open("GET",url,true);
	req.send(null);
	req.onreadystatechange=function(){
		if(req.readyState==4){
			var result=req.responseText;
			var c=result.charAt(0);
			if(c=='N'){
				span.innerHTML="<font color=green>available</font>";
				idinput.style.borderColor="green";
			}
			else{
				span.innerHTML="<font color=red>already exist</font>";
				idinput.style.borderColor="red";
			}
		}
	};
}

function checkName(who){
	var firstnameinput=document.getElementById(who+"firstname");
	var lastnameinput=document.getElementById(who+"lastname");
	firstname=firstnameinput.value;
	lastname=lastnameinput.value;
	
	if(lastname.length==0){
		lastnameinput.style.borderColor="red";
	}
	if(firstname.length>0&&firstname.length<=20&&onlyAlphabet(firstname)==1){
		firstnameinput.style.borderColor="green";
	} else firstnameinput.style.borderColor="red";
	if(lastname.length>0&&lastname.length<=20&&onlyAlphabet(lastname)==1){
		lastnameinput.style.borderColor="green";
	} else lastnameinput.style.borderColor="red";
	if(firstname.length>0&&lastname.length>0&&firstname.length<=20&&lastname.length<=20&&onlyAlphabet(firstname)==1&&onlyAlphabet(lastname)==1) return 1;
}

function checkChangePassword(who){
	var passwordinput=document.getElementById(who+"newpassword");
	var confirmpasswordinput=document.getElementById(who+"confirmpassword");
	var password=passwordinput.value;
	var confirmpassword=confirmpasswordinput.value;
	var l=password.length;
	var l2=confirmpassword.length;
	var a=isAlphabet(password);
	var d=isDigit(password);
	var c=isSpecialChar(password);
	var ac=noOfAlphabet(password);
	var dc=noOfDigit(password);
	var cc=noOfSpecialChar(password);
	if(password.length!=ac+dc+cc){
		passwordinput.style.borderColor="red";
		return;
	}
	if(l==0){
		passwordinput.style.borderColor="red";
	}
	else if(l>=1 && l<=4){
		passwordinput.style.borderColor="yellow";
	}
	else if(l>=5 && l<=7){
		if((a==1&&d==1)||(a==1&&c==1)||(d==1&&c==1)){
			passwordinput.style.borderColor="yellow";
		}
		else{
			passwordinput.style.borderColor="yellow";
		}

	}
	else if(l>=8){
		if(l>10&&a==1&&d==1&&c==1){
			passwordinput.style.borderColor="green";
		}
		else if(l>15&&c==1){
			passwordinput.style.borderColor="green";
		}
		else if((ac>2&&dc>2)||(ac>2&&cc>2)||(dc>2&&cc>2)){
			passwordinput.style.borderColor="green";
		}
		else if(l>10){
			passwordinput.style.borderColor="yellow";
		}
		else if((a==1&&d==1)||(a==1&&c==1)||(d==1&&c==1)){
			passwordinput.style.borderColor="yellow";
		}
		else{
			passwordinput.style.borderColor="yellow";
		}
	}
	if(l>45){
		passwordinput.style.borderColor="yellow";
	}
	else if(l==0&&l2==0){
		passwordinput.style.borderColor="red";
		confirmpasswordinput.style.borderColor="red";
	}
	else if(l2==0){
		confirmpasswordinput.style.borderColor="red";
	}
	else if(l2>l){
		confirmpasswordinput.style.borderColor="red";
	}
	else if(password==confirmpassword&&confirmpassword.length>0){
		passwordinput.style.borderColor="green";
		confirmpasswordinput.style.borderColor="green";
		return 1;
	}
	else if(confirmpassword.length>0){
		confirmpasswordinput.style.borderColor="red";
	}
}

function checkEmail(){
	var b=0,c=0;
	var emailinput=document.getElementById("email");
	var email=emailinput.value;
	var st=email.split("@");
	for(var i=0;i<email.length;i++){
		if(email.charAt(i)=='.') c=1;
		if(email.charAt(i)=='\''){
			c=0;break;
		}
	}
	if(email.length>4&&st.length==2&&st[0]!=""&&st[1]!=""&&email.length<=45){
		b=1;
	}
	if(c==0) b=0;
	if(b==1) emailinput.style.borderColor="green";
	else emailinput.style.borderColor="red";
	return b;
}
/*
function fillCountry(){
	var country=document.getElementById("country");
	var countryarray=Array("Not selected","India","Pakistan");
	var state=document.getElementById("state");
	var opt=document.createElement("option");
	opt.value="Not selected";
	opt.text="Not selected";
	state.options.add(opt);
	fill(countryarray,country);
}

function fillState(){
	var country=document.getElementById("country");
	var state=document.getElementById("state");
	removeOption(state);
	var i=country.selectedIndex;
	switch(i){
	case 1:
		var india=Array("Madhya Pradesh","Utter Pradesh","Tamilnadu");
		fill(india,state);
		break;
	case 2:
		var pakistan=Array("Karachi","Balochistan","Sindh");
		fill(pakistan,state);
		break;
	}
}

function fill(arr,combo){
	for(var i=0;i<arr.length;i++){
		opt=document.createElement("option");
		opt.value=arr[i];
		opt.text=arr[i];
		combo.options.add(opt);
	}
}

function removeOption(opt){				
	for(var i=1;i<opt.options.length;)
		opt.remove(i);
}
*/
function checkLogin(){
	var id=document.getElementById("id").value;
	var password=document.getElementById("password").value;
	var span=document.getElementById("span");
	if(id.length==0||password.length==0) return;
	var submit=document.getElementById("submit");
	var req=new XMLHttpRequest();
	var url="CheckLoginBackend?id="+id+"&password="+password;
	req.open("GET",url,true);
	req.send(null);
	req.onreadystatechange=function(){
		if(req.readyState==4){
			var result=req.responseText;
			var c=result.charAt(0);
			if(c=='A'||c=='U'){
				span.innerHTML="<span class=\"t-green t-bold\"><b>valid identity</span>";
				$("form").submit();
			}
			else span.innerHTML="&nbsp;";
		}
		else span.innerHTML="<font color=red>Error encountered</font>";
	};
}
function validId(id){
	if(id.length>0&&id.length<=20&&id.charCodeAt(0)>=65&&id.length==noOfAlphabet(id)+noOfDigit(id)) return 1;
	else return 0;
}

function confirmDetails(){
	var form=document.getElementById("signupform");
	var idinput=document.getElementById("id");
	var idspan=document.getElementById("idspan");
	var emailinput=document.getElementById("email");
	var email=emailinput.value;
	var id=idinput.value;
	var c=0;
	c+=checkName("user");
	c+=checkChangePassword("user");
	c+=checkEmail();
	c+=aboutMe();
	c+=validId(id);
	if(id.length==0) idinput.style.borderColor="red";
	
	if(c==5){
		var req=new XMLHttpRequest();
		var url="ConfirmDetailsBackend?id="+id+"&email="+email;//check id and email both
		req.open("GET",url,true);
		req.send(null);
		req.onreadystatechange=function(){
			if(req.readyState==4){
				var result=req.responseText;
				if(result.charAt(0)=='N'&&result.charAt(2)=='N'){
					form.submit();// or $(form).submit();
				} else if(result.charAt(0)=='N'){
					emailinput.style.borderColor="red";
					emailspan.innerHTML="<font color=red>Id Exist</font>"
				} else if(result.charAt(2)=='N'){
					idinput.style.borderColor="red";
					idspan.innerHTML="<font color=red>Id Exist</font>"
				}
			}
		};
	}
}



function accountSetting(who){
	var submitbuttonspan=document.getElementById("submitbuttonspan");
	var id=document.getElementById(who+"id").value;
	var firstname=document.getElementById(who+"firstname").value;
	var lastname=document.getElementById(who+"lastname").value;
	var password=document.getElementById(who+"newpassword").value;
	var confirmpassword=document.getElementById(who+"confirmpassword").value;
	var email=document.getElementById("email").value;
	var emailspan=document.getElementById("emailspan");
	var gender=document.querySelector('input[name="gender"]:checked').value;
	var country=document.getElementById("country").value;
	var about=document.getElementById("about").value;
	var facebook=document.getElementById("facebook").value;
	var google=document.getElementById("google").value;
	var github=document.getElementById("github").value;
	var quora=document.getElementById("quora").value;
	var aboutmespan=document.getElementById("aboutmespan");
	var c=0,flag=0;
	$("#useraccountsetting").html("<i class=\"fa fa-spinner fa-pulse\"></i> Saving");
	submitbuttonspan.innerHTML="";
	$("#submitbuttonspan").clearQueue();
	$("#emailspan").clearQueue();
	$("#submitbuttonspan").fadeIn();
	$("#emailspan").html("");
	$("#emailspan").fadeTo(1,1);
	c+=checkName(who);
	c+=checkChangePassword(who);
	c+=checkEmail();
	if(c==3){
		flag=1;
	}
	if(flag==1){
	    $.post("AccountSettingBackend",
	    	    {
	    	    	id: id,
	    	    	fname: firstname,
	    	    	lname: lastname,
	    	    	password: password,
	    	    	who: who,
	    	    	confirmpassword: confirmpassword,
	    	    	email: email,
	    	    	gender: gender,
	    	    	country: country,
	    	    	about: about,
	    	    	facebook:facebook,
	    	    	google: google,
	    	    	github: github,
	    	    	quora: quora
	    	    },
	    	    function(data, status){
	    	    	if(status=="success"){
	    				var result=data;
	    				if(result.charAt(0)=='S') submitbuttonspan.innerHTML="<font color=green>Changes saved</font>";
	    				else if(result.charAt(0)=='E'){
	    					emailspan.innerHTML="<font color=red>Email id exist</font>";
	    					$("#emailspan").fadeTo(7000,.001);
	    				}
	    				else if(result.charAt(0)=='I') submitbuttonspan.innerHTML="<font color=red>Invalid input</font>";
	    				else submitbuttonspan.innerHTML="<font color=red>failed</font>";
	    	    	} else submitbuttonspan.innerHTML="<font color=red>Error occured try again</font>";
	    			$("#useraccountsetting").html("<i class=\"glyphicon glyphicon-save\"></i> Save");
	    	    });
	
	}
	else{
		$("#useraccountsetting").html("<i class=\"glyphicon glyphicon-save\"></i> Save");
		submitbuttonspan.innerHTML="<font color=red>Please check information</font>";
	}
	$("#submitbuttonspan").fadeOut(1000);
}

function vote(type,operation,id,user_id){
	var totalupvotespan=document.getElementById("totalupvote"+type+id);
	var totaldownvotespan=document.getElementById("totaldownvote"+type+id);
	var upvotebutton=document.getElementById("upvote"+type+id);
	var downvotebutton=document.getElementById("downvote"+type+id);
	var totalupvote,upvotesign="",upvotehtml;
	var totaldownvote,downvotesign="",downvotehtml;
	var req=new XMLHttpRequest();
	var url="VoteBackend?id="+id+"&user_id="+user_id+"&type="+type+"&operation="+operation;
	req.open("GET",url,true);
	req.send(null);
	req.onreadystatechange=function(){
		if(req.readyState==4){
			var result=req.responseText;
			var st=result.split("#");
			if(st[0]=="Vote"){
				totalupvote=parseInt(st[1]); if(totalupvote>0) upvotesign="+";
				totaldownvote=parseInt(st[2]);if(totaldownvote>0) downvotesign="-";
				upvotehtml=st[3]+" | <span id=\"totalupvote"+type+id+"\"><span class=\"t-link\">"+upvotesign+totalupvote+"</span></span>";
				downvotehtml=st[4]+" | <span id=\"totaldownvote"+type+id+"\"><span class=\"t-red\">"+downvotesign+totaldownvote+"</span></span>";
				$(upvotebutton).html(upvotehtml);
				$(downvotebutton).html(downvotehtml);
/*				if($(upvotebutton).html()!=upvotehtml){
					$(upvotebutton).fadeTo(0,.01,function(){
						$(upvotebutton).html(upvotehtml);
						$(upvotebutton).fadeTo(500,1);
					});
				}
				if($(downvotebutton).html()!=downvotehtml){
					$(downvotebutton).fadeTo(100,0,function(){
						$(downvotebutton).html(downvotehtml);
						$(downvotebutton).fadeTo(500,1);
					});
				}
*/				
			}
		}
	};
}

function deleteUser(){
	var id=document.getElementById("userid").value;
	var span=document.getElementById("useridspan");
	var req=new XMLHttpRequest();
	var url="DeleteUserBackend?id="+id;
	req.open("GET",url,true);
	req.send(null);
	req.onreadystatechange=function(){
		if(req.readyState==4){
			var result=req.responseText;
			if(result.charAt(0)=='d'){
				span.innerHTML="<font color=green>User deleted successfully</font>";
			}
		} else span.innerHTML="<font color=red>error encountered</font>";
	};
}

function checkQuestionExist(){
	var id=document.getElementById("questionid").value;
	var span=document.getElementById("questionidspan");
	var button=document.getElementById("deletebutton");
	button.disabled=true;
	if(id.length==0){
		span.innerHTML="<font color=red>Enter question id</font>";
		return;
	}
	var req=new XMLHttpRequest();
	var url="CheckQuestionBackend?id="+id;
	req.open("GET",url,true);
	req.send(null);
	req.onreadystatechange=function(){
		if(req.readyState==4){
			var result=req.responseText;
			if(result.charAt(0)=='E'){
				span.innerHTML="<font color=green>Question id exist</font>";
				button.disabled=false;
			}
			else span.innerHTML="<font color=red>Question id does not exist</font>";
		}
		else span.innerHTML="<font color=red>error encountered !</font>";
	};
}

function deleteQuestion(){
	var id=document.getElementById("questionid").value;
	var span=document.getElementById("questionidspan");
	var req=new XMLHttpRequest();
	var url="DeleteQuestionBackend?id="+id;
	req.open("GET",url,true);
	req.send(null);
	req.onreadystatechange=function(){
		if(req.readyState==4){
			var result=req.responseText;
			if(result.charAt(0)=='D'){
				span.innerHTML="<font color=green>Question deleted successfully</font>";
			}
			else span.innerHTML="<font color=red>Question not deleted</font>";
		}
		else span.innerHTML="<font color=red>error encountered !</font>";
	};
}

function deleteMyQuestion(questionid,page){
	var deletebutton=document.getElementById("deletequestion"+questionid);
	var req=new XMLHttpRequest();
	var url="DeleteQuestionBackend?id="+questionid;
	req.open("GET",url,true);
	req.send(null);
	req.onreadystatechange=function(){
		if(req.readyState==4){
			var result=req.responseText;
			if(result.charAt(0)=='D'){
				if(page=="question"){
					$("#maindiv").fadeOut("slow",function(){
						$("#maindiv").remove();
					});
				}
				$(deletebutton).closest("table").fadeOut("slow",function(){
					$(deletebutton).closest("table").remove();
					if(page=="askedquestions"){
						var count=$("#maindiv").children("table").length;
						$("#totalcountspan").html(count);
						if(count==0) $("#askedquestionsspan").show();
					} else if(page=="userhome"){
						var count=$("#maindiv").children("table").length;
						if(count==0) $("#recentspan").show();
					}
				});
			}
		}
	};
}

function deleteMyAnswer(answerid,questionid,page){
	var deletebutton=document.getElementById("deleteanswer"+answerid);
	var req=new XMLHttpRequest();
	var url="DeleteAnswerBackend?id="+answerid;
	req.open("GET",url,true);
	req.send(null);
	req.onreadystatechange=function(){
		if(req.readyState==4){
			var result=req.responseText;
			if(result.charAt(0)=='D'){
				$(deletebutton).closest("table").fadeOut("slow",function(){
					$(deletebutton).closest("table").remove();
					if(page=="question"){
						var count=$("#maindiv").children("table").length-1;
						$("#numberofanswer"+questionid).html(" "+count+" Answers");
					} else if(page=="myanswers"){
						var count=$("#maindiv").children("table").length;
						$("#totalcountspan").html(count);
						if(count==0) $("#myanswerspan").show();
					}
				});
			}
		}
	};
}

function checkAnswerExist(){
	var id=document.getElementById("answerid").value;
	var span=document.getElementById("answeridspan");
	var button=document.getElementById("deletebutton");
	button.disabled=true;
	if(id.length==0){
		span.innerHTML="<font color=red>Enter answer id</font>";
		return;
	}
	var req=new XMLHttpRequest();
	var url="CheckAnswerBackend?id="+id;
	req.open("GET",url,true);
	req.send(null);
	req.onreadystatechange=function(){
		if(req.readyState==4){
			var result=req.responseText;
			if(result.charAt(0)=='E'){
				span.innerHTML="<font color=green>Answer id exist</font>";
				button.disabled=false;
			}
			else span.innerHTML="<font color=red>Answer id does not exist</font>";
		}
		else span.innerHTML="<font color=red>error encountered !</font>";
	};
}

function deleteAnswer(){
	var id=document.getElementById("answerid").value;
	var span=document.getElementById("answeridspan");
	var req=new XMLHttpRequest();
	var url="DeleteAnswerBackend?id="+id;
	req.open("GET",url,true);
	req.send(null);
	req.onreadystatechange=function(){
		if(req.readyState==4){
			var result=req.responseText;
			if(result.charAt(0)=='D'){
				span.innerHTML="<font color=green>Answer deleted successfully</font>";
			}
			else span.innerHTML="<font color=red>Answer not deleted</font>";
		}
		else span.innerHTML="<font color=red>error encountered !</font>";
	};
}

function inboxMessagesCount(){
	var u=$("#unreadtable >tbody >tr").length;
	var r=$("#readtable >tbody >tr").length;
	$("#unreadcountspan").html(u);
	$("#readcountspan").html(r);
	$("#totalcountspan").html(u+r);
}

function markAsRead(messageid,userid){
	
    $.post("MarkAsReadMessageBackend",
    	    {
    	    	messageid: messageid,
    	    	userid: userid,
    	    	operation: "one"
    	    },
    	    function(data, status){
    	    	if(status=="success"){
    	    		if(data.charAt(0)=='S'&&data.charAt(1)=='u'){
    	    			data=data.substring(7);
    					$("#read"+messageid).closest("tr").fadeOut(500,function(){
    						$("#read"+messageid).closest("tr").remove();
        	    			$("#readpanelbody").html(data);
        	    			inboxMessagesCount();
    					});
    	    		}
    	    	}
    	    });
}

function markAllReadMessages(userid){
	
    $.post("MarkAsReadMessageBackend",
    	    {
    	    	userid: userid,
    	    	operation: "all"
    	    },
    	    function(data, status){
    	    	if(status=="success"){
    	    		if(data.charAt(0)=='S'&&data.charAt(1)=='u'){
    	    			data=data.substring(7);
    					$("#unreadtable tr").fadeOut(500,function(){
    						$("#unreadtable tr").remove();
        	    			$("#readpanelbody").html(data);
        	    			inboxMessagesCount();
    					});
    	    		}
    	    	}
    	    });
}

function deleteReadMessages(userid){
	
    $.post("DeleteMessageBackend",
    	    {	
    			userid: userid,
    	    	operation: "read",
    	    	page: "inbox"
    	    },
    	    function(data, status){
    	    	if(status=="success"){
    	    		if(data.charAt(0)=='S'&&data.charAt(1)=='u'){
    					$("#readtable tr").fadeOut(500,function(){
    						$("#readtable tr").remove();
    						inboxMessagesCount();
    					});
    	    		}
    	    	}
    	    });
}

function deleteMessage(messageid,userid,page){
	
    $.post("DeleteMessageBackend",
    	    {	
    	    	messageid: messageid,
    			userid: userid,
    			page: page,
    	    	operation: "one"
    	    },
    	    function(data, status){
    	    	if(status=="success"){
    	    		if(data.charAt(0)=='S'&&data.charAt(1)=='u'){
    					$("#delete"+messageid).closest("tr").fadeOut(500,function(){
    						$("#delete"+messageid).closest("tr").remove();
    						if(page=="outbox"){
    							var t=$("#outboxtable >tbody >tr").length;
    							$("#totalcountspan").html(t);
    						} else{
    							inboxMessagesCount();
    						}
    					});
    	    		}
    	    	}
    	    });
}

function deleteAllMessages(userid,page){
	
    $.post("DeleteMessageBackend",
    	    {	
    			userid: userid,
    			page: page,
    	    	operation: "all"
    	    },
    	    function(data, status){
    	    	if(status=="success"){
    	    		if(data.charAt(0)=='S'&&data.charAt(1)=='u'){
    					if(page=="outbox"){
    						$("#outboxtable tr").fadeOut(500,function(){
    							$("#outboxtable tr").remove();
    							$("#totalcountspan").html("0");
    						});
    					} else{
    						$("#unreadtable tr").fadeOut(500,function(){
    							$("#unreadtable tr").remove();
    							inboxMessagesCount();
    						});
    						$("#readtable tr").fadeOut(500,function(){
    							$("#readtable tr").remove();
    							inboxMessagesCount();
    						});
    					}
    	    		}
    	    	}
    	    });
}

function deleteReadingList(no,id,userid){
	var deletebutton=document.getElementById("delete"+no);
	var deletespan=document.getElementById("deletespan"+no);
	var req=new XMLHttpRequest();
	var url="DeleteReadingListBackend?id="+id+"&userid="+userid+"&operation=one";
	req.open("GET",url,true);
	req.send(null);
	req.onreadystatechange=function(){
		if(req.readyState==4){
			var result=req.responseText;
			if(result.charAt(0)=='D'){
				$("#delete"+no).closest("tr").fadeOut(500,function(){
					$("#delete"+no).closest("tr").remove();
					var t=$("#readinglisttable >tbody >tr").length-1;
					$("#totalcountspan").html(t);
				});
			}
		}
	};
}


function deleteAllReadingList(userid,totalcount){
	var req=new XMLHttpRequest();
	var url="DeleteReadingListBackend?userid="+userid+"&operation=all";
	req.open("GET",url,true);
	req.send(null);
	req.onreadystatechange=function(){
		if(req.readyState==4){
			var result=req.responseText;
			if(result.charAt(0)=='D'){
				$("#readinglisttable tr:gt(0)").fadeOut(500,function(){
					$("#readinglisttable tr:gt(0)").remove();
					$("#totalcountspan").html("0");
				});
			}
		}
	};
}

function deleteAllSuspicious(){
	var req=new XMLHttpRequest();
	var url="DeleteSuspiciousBackend?operation=all";
	req.open("GET",url,true);
	req.send(null);
	req.onreadystatechange=function(){
		if(req.readyState==4){
			var result=req.responseText;
			if(result.charAt(0)=='D'){
				$("button").closest("table").remove();
			}
		}
	};
}

function deleteSuspicious(type,id,reporter){
	var req=new XMLHttpRequest();
	var url="DeleteSuspiciousBackend?type="+type+"&id="+id+"&reporter="+reporter+"&operation=one";
	req.open("GET",url,true);
	req.send(null);
	req.onreadystatechange=function(){
		if(req.readyState==4){
			var result=req.responseText;
			if(result.charAt(0)=='D'){
				location.reload(false);
			}
		}
	};
}

function reportSuspicious(type,id,user){
	var reportbutton=document.getElementById("report"+type+id);
	var reportbuttonspan=document.getElementById("report"+type+"span"+id);
	var req=new XMLHttpRequest();
	var url="ReportSuspiciousBackend?user_id="+user+"&id="+id+"&type="+type;
	req.open("GET",url,true);
	req.send(null);
	req.onreadystatechange=function(){
		if(req.readyState==4){			// if readystate==4 means we have got some response no matter operation was successful or controlled by try catch in backend
			var result=req.responseText;
			if(result.charAt(0)=='R'){
				reportbutton.innerHTML="<i class=\"glyphicon glyphicon-alert\"></i>";
				reportbutton.disabled=true;
			}
		}
	};
}

function deleterecyclebin(id){
	
    $.post("RecycleBin",
    	    {
    	    	id: id,
    	    },
    	    function(data, status){
    	    	if(status=="success"){
    	    		if(data.substring(0,7)=="Success"){
    	    			if(id=="all") $("tr:gt(0)").remove();
    	    			else $("#"+id).closest("tr").remove();
    	    		} else alert("failed please try again");
    	    	}
    	    });
}

function deleteadminmessage(id){
	
    $.post("AdminMessage",
    	    {
    	    	id: id,
    	    },
    	    function(data, status){
    	    	if(status=="success"){
    	    		if(data.substring(0,7)=="Success"){
    	    			if(id=="all") $("tr:gt(0)").remove();
    	    			else $("#"+id).closest("tr").remove();
    	    		} else alert("failed please try again");
    	    	}
    	    });
}

function addReadingList(questionid,userid){
	var span=document.getElementById("addreadinglistspan"+questionid);
	var button=document.getElementById("addreadinglist"+questionid);
	
    $.post("ReadingList",
    	    {
    	    	questionid: questionid,
    	    	userid: userid
    	    },
    	    function(data, status){
    	    	if(status=="success"){
    	    		if(data.substring(0,5)=="Added"){
    	    			button.innerHTML="<i class=\"glyphicon glyphicon-ok\"></i>";
    	    		} else if(data.substring(0, 7)=="Removed"){
    	    			button.innerHTML="<i class=\"glyphicon glyphicon-plus\"></i>";
    	    		}
    	    	}
    	    });
}

function findNotesLength(){
	remainingCharactersSpan("notes","notesspan",2000);
}

function editNotes(element){
	if(element=="button"){
		$("#editnotesdiv").clearQueue();
		$("#editnotesdiv").slideToggle("slow");
	}
	if(element=="textarea"){
		findNotesLength();
		$("#notestext").html($("#notes").val());
	}
}

function saveNotes(id,button){
	var notes=document.getElementById("notes").value;
	var notesspan=document.getElementById("notesspan");
	var savebutton=document.getElementById("savenotes");
	if(button=="clear") notes="";

	var req=new XMLHttpRequest();
	var url="SaveNotesBackend?id="+id+"&notes="+notes;
	req.open("GET",url,true);
	req.send(null);
	req.onreadystatechange=function(){
		if(req.readyState==4){
			var result=req.responseText
			if(result.substring(0,7)=="Success"){
				$("#lastsaved").html("saved on "+result.substring(8));
				if(button=="clear"){
					$("#notes").val("");
					$("#notestext").html("");
					notesspan.innerHTML="<font color=green>Cleared</font>";
				}
				else notesspan.innerHTML="<font color=green>Saved</font>";
			}
			else notesspan.innerHTML="<font color=red>Saving failed</font>";
		}
		else notesspan.innerHTML="<font color=red>error encountered !</font>";
	};
}

function sendForgotPassword(){
	var email=document.getElementById("email").value;
	var span=document.getElementById("forgotspan");
	span.innerHTML="";
	$("button").html("<i class=\"fa fa-spinner fa-pulse fa-lg\"></i> Sending");
	var req=new XMLHttpRequest();
	var url="ResetPassword?email="+email+"&send=true";
	req.open("GET",url,true);
	req.send(null);
	req.onreadystatechange=function(){
		if(req.readyState==4){
			var result=req.responseText;
			if(result.charAt(0)=='S'){
				span.innerHTML="<font color=green>Mail sent</font>";
				alert("Password reset link has been sent successfully to address:"+email);
			}
			else if(result.charAt(0)=='N') span.innerHTML="<font color=red>Email not exist</font>";
			else span.innerHTML="<font color=red>Error try again</font>";
		}
		else notesspan.innerHTML="<font color=red>Error encountered !</font>";
		$("button").html("<i class=\"glyphicon glyphicon-send\"></i> Send");
	};
}

function answerButton(questionid){
	var type="question", typeid=questionid;
	$("#writeanswerdiv"+questionid).clearQueue();
	$("#writeanswerdiv"+questionid).slideToggle(700);
	
	if($("#edit"+type+"div"+typeid).is(":visible")==true){
		$("#edit"+type+"div"+typeid).slideToggle(700);
	}
	
	if($("#comment"+type+"div"+typeid).is(":visible")==true){
		$("#comment"+type+"div"+typeid).hide("slow");
		$("#comment"+type+"div"+typeid).slideUp("slow");
		$("#comment"+type+"div"+typeid).fadeOut("slow");
	}
}

function writeAnswer(questionid){
	var l=document.getElementById("writeanswertextarea"+questionid).value.length;
	var button=document.getElementById("sendbutton"+questionid);
	var limit=5000;
	var r=limit-l;
	if(l>0){
		$("#writeanswerpreview"+questionid).css('background-color','#e1f2ff');
	} else $("#writeanswerpreview"+questionid).css('background-color', 'Transparent');
	
	remainingCharactersSpan("writeanswertextarea"+questionid,"writeanswerspan"+questionid,limit);
	$("#writeanswerpreview"+questionid).html($("#writeanswertextarea"+questionid).val());
	button.disabled=false;
	if(r<0||l==0){
		button.disabled=true;
	}
}

function sendAnswer(questionid,userid,page){
	var textarea=document.getElementById("writeanswertextarea"+questionid);
	var sendbutton=document.getElementById("sendbutton"+questionid);
	var span=document.getElementById("writeanswerspan"+questionid);
	var answer=textarea.value;
	if(answer.length>5000) answer=answer.substring(0,5000);
	$.post("SendAnswerBackend",
    	    {
    	    	id: questionid,
    	    	user_id: userid,
    	    	answer: answer
    	    },
    	    function(data, status){
    	    	if(status=="success"){
    	    		if(data.substring(0,7)=="Success"){
    	    		//	location.reload(false);
    					sendbutton.disabled=true;
    					span.innerHTML="<font color=green>Thanks for contribution</font>";
    					
    					$("#writeanswerdiv"+questionid).slideUp(500,function(){
    						textarea.value="";
        					$("#writeanswerpreview"+questionid).html("");
        					$("#writeanswerpreview"+questionid).css('background-color', 'Transparent');
        					$(span).html("");
    					});
    					if(page=="question"){
    						$("a[href$='#myanswer'").click();
    					}
    			//		$.when($("#myanswer >table").length>0).done(function() {
    			//			$("#myanswer").html("fuck fuck"); alert($("#myanswer").html());//animate({backgroundColor: "#5bbaff"});
        		//			$("#myanswer table tr").animate({backgroundColor: "Transparent"}, "slow");
    			//		});
    	    		} else span.html("<font color=red>Error occured try again</font>");
    	    	}
    	    });
	insertWords(userid,answer);
    
}

function edit(typeid,userid,type,element){
	if(element=="button"){
		$("#edit"+type+"div"+typeid).clearQueue();
		
		if($("#edit"+type+"div"+typeid).is(":visible")==true){
			$("#edit"+type+"button"+typeid).css("color","#333333");
			$("#edit"+type+"div"+typeid).slideUp("slow");
			if(type=="question"){  ///// tag ///////
				$("#inputtag"+typeid).hide("slow");
				$("#tagdiv"+typeid).show("slow");
			}
		} else {
			$("#edit"+type+"button"+typeid).css("color","blue");
			$("#edit"+type+"textarea"+typeid).val($("#"+type+"text"+typeid).html());
			$("#edit"+type+"div"+typeid).slideDown("slow");
			if(type=="question"){  ///// tag ///////
				$("#tagdiv"+typeid).hide("slow");
				$("#inputtag"+typeid).show("slow");
			}
		}
		
		if($("#comment"+type+"div"+typeid).is(":visible")==true){
			$("#comment"+type+"div"+typeid).hide("slow");
			$("#comment"+type+"div"+typeid).slideUp("slow");
			$("#comment"+type+"div"+typeid).fadeOut("slow");
		}
		if($("#writeanswerdiv"+typeid).is(":visible")==true){
			$("#writeanswerdiv"+typeid).slideUp(700);
		}
		
	} else if(element=="textarea"){
		var text=$("#edit"+type+"textarea"+typeid).val();
		var span=$("#edit"+type+"span"+typeid);
		var limit=200;
		if(type=="answer") limit=2000;
		remainingCharactersSpan("edit"+type+"textarea"+typeid,"edit"+type+"span"+typeid,limit);
		$("#"+type+"text"+typeid).html(text);
		
	} else if(element=="save"){
		var text=$("#edit"+type+"textarea"+typeid).val();
		var span=$("#edit"+type+"span"+typeid);
		var tag=$("#inputtag"+typeid).val();
		
		if(type=="question"&&text.length<10){
			span.html("<font color=red>At least 10 characters</font>");return;
		} else if(type=="answer"&&text.length==0){
			return;
		}
		
	    $.post("Edit",
	    	    {
	    	    	typeid: typeid,
	    	    	userid: userid,
	    	    	type: type,
	    	    	text: text,
	    	    	tag: tag
	    	    },
	    	    function(data, status){
	    	    	if(status=="success"){
	    	    		if(data.charAt(0)=='S'){
	    	    			$("#edit"+type+"div"+typeid).slideUp(700);
	    	    			$("#edit"+type+"button"+typeid).css("color","#333333");
	    	    			span.html("<span class=\"t-green\">saved</span>");
	    	    			
	    	    			if(type=="question"){  ///// tag ///////
	    	    				var s=$("#inputtag"+typeid).val().toLowerCase().split(" ");
	    	    				var tags="";
	    	    				for(var i=0;i<s.length;i++){
	    	    					if(s[i].length==0) continue;
	    	    					tags+="<span class=\"taglabel\"><a href=\"Tag?name="+s[i]+"\">"+s[i]+"</a></span> ";
	    	    				}
	    	    				$("#tagdiv"+typeid).html(tags);
	    	    				$("#inputtag"+typeid).hide("slow");
	    	    				$("#tagdiv"+typeid).show("slow");
	    					}
	    	    			//\\//\\//\\ tag //\\//\\//\\//\\
	    	    		} else span.html("<span class=\"t-red\">error occured try again</span>");
	    	    	}
	    	    });
	    
	    insertWords(userid,text);
		
	}
}

function commentButton(type,typeid,userid){
	if($("#comment"+type+"div"+typeid).is(":visible")==true){
		$("#comment"+type+"div"+typeid).hide("slow");
		$("#comment"+type+"div"+typeid).slideUp("slow");
		$("#comment"+type+"div"+typeid).fadeOut("slow");
	} else{
		$("#comment"+type+"div"+typeid).fadeIn("slow");
		$("#commenttext"+type+"div"+typeid).html("<span style=\"padding-left:45%;color:#5bbaff;\"><i class=\"fa fa-spinner fa-pulse fa-3x\"></i></span>");
	    $.post("CommentBackend",
	    	    {
	    	    	operation: "show",
	    	    	type: type,
	    			typeid: typeid,
	    			userid: userid
	    	    },
	    	    function(data, status){
	    	    	if(status=="success"){
	    	    		if(data.charAt(0)=='S'&&data.charAt(1)=='u'){
	    	    			var s=data.substring(7);
	    	    			$("#commenttext"+type+"div"+typeid).fadeTo("slow",0.1,function(){
	    	    				$("#commenttext"+type+"div"+typeid).html(s);
	    	    				$("#commenttext"+type+"div"+typeid).fadeTo("slow",1);
	    	    				var count=$("#commenttable"+type+typeid+" >tbody >tr").length;
	    	    				$("#commentcount"+type+typeid).html(count);
	    	    				if(count==0) $("#commenttext"+type+"div"+typeid).html("<span style=\"color:blue;\">No comments be first to comment <i class=\"fa fa-smile-o fa-lg\"></i></span>");
	    	    			});
	    	    		} else $("#commenttext"+type+"div"+typeid).html("<font color=red>Error while loading comments try again</font>");
	    	    	}
	    	    });
	}
	
	if($("#writeanswerdiv"+typeid).is(":visible")==true){
		$("#writeanswerdiv"+typeid).hide();
	}
	if($("#edit"+type+"div"+typeid).is(":visible")==true){
		$("#edit"+type+"div"+typeid).hide();
	}
}

function commentType(type,typeid){
	var c=$("#comment"+type+"textarea"+typeid).val();
	if(c.length>0){
		$("#comment"+type+"preview"+typeid).css('background-color','#e1f2ff');
	} else $("#comment"+type+"preview"+typeid).css('background-color', 'Transparent');
	remainingCharactersSpan("comment"+type+"textarea"+typeid,"comment"+type+"span"+typeid,200);

	$("#comment"+type+"preview"+typeid).html(c);
}

function commentSave(type,typeid,userid){
	var c=$("#comment"+type+"textarea"+typeid).val();
	var span=$("#comment"+type+"span"+typeid);

	if(c.length<1){
		span.html("<font color=\"orange\">Please comment</font>");return;
	}
	$("#comment"+type+"preview"+typeid).css('background-color', 'Transparent');
	$("#comment"+type+"preview"+typeid).html("<span style=\"color: #5bbaff;\"><i class=\"fa fa-spinner fa-pulse fa-3x\"></i></span>");
	$.post("CommentBackend",
    	    {
    	    	operation: "save",
    	    	type: type,
    			typeid: typeid,
    	    	author: userid,
    	    	comment: c,
    	    },
    	    function(data, status){
    	    	if(status=="success"){
    	    		if(data.charAt(0)=='S'&&data.charAt(1)=='u'){
    	    			var s=data.substring(7);
    	    			$("#commenttext"+type+"div"+typeid).fadeTo("slow",0.1,function(){
    	    				$("#commenttext"+type+"div"+typeid).html(s);
    	    				$("#commenttext"+type+"div"+typeid).fadeTo("slow",1);
    	    				$("#comment"+type+"textarea"+typeid).val("");
    	    				$("#comment"+type+"preview"+typeid).html("");
    	    				var count=$("#commenttable"+type+typeid+" >tbody >tr").length;
    	    				$("#commentcount"+type+typeid).html(count);
    	    				span.html("");
    	    			});
    	    		} else{
    	    			$("#commenttext"+type+"div"+typeid).html("<font color=red>Error while loading comments try again</font>");
    	    			$("#comment"+type+"preview"+typeid).html("");
    	    		}
    	    	}
    	    });
	insertWords(userid,c);
	
}

function commentDelete(type,typeid,userid,cid){
	
    $.post("CommentBackend",
    	    {
    	    	operation: "delete",
    			cid: cid,
    	    	author: userid,
    	    },
    	    function(data, status){
    	    	if(status=="success"){
    	    		if(data.charAt(0)=='S'&&data.charAt(1)=='u'){
    	    			$("#commentrow"+cid).fadeOut("slow",function(){
    	    				$("#commentrow"+cid).remove();
    	    				$("#comment"+type+"span"+typeid).html("");
    	    				var count=$("#commenttable"+type+typeid+" >tbody >tr").length;
    	    				$("#commentcount"+type+typeid).html(count);
    	    				if(count==0) $("#commenttext"+type+"div"+typeid).html("<span style=\"color: blue;\">No comments be first to comment <i class=\"fa fa-smile-o fa-lg\"></i></span>");
    	    			});
    	    		} else $("#comment"+type+"span"+typeid).html("<font color=\"red\">Error while deleting comment try again</font>");
    	    	}
    	    });
}

function remainingCharactersSpan(textid,spanid,limit){
	
	var text=$("#"+textid).val();
	var span=$("#"+spanid);
	var r=limit-text.length;
	
	if(r<0){
		span.html("<font color=\"red\">length Limit exceeded "+text.length+"</font> /"+limit);
	} else if(r<20){
		span.html("<font color=\"orange\">"+r+"</font> /"+limit);
	} else{
		span.html("<font color=\"green\">"+r+"</font> /"+limit);
	}
}

function sortByAnswerTab(tab,questionid,userid,page){
	$("#popularity").html("");
	$("#oldest").html("");
	$("#newest").html("");
	$("#myanswer").html("");
	$("#"+tab).html("</br><div style=\"text-align:center;color:#5bbaff;\"><i class=\"fa fa-spinner fa-pulse fa-3x\"></i></div>");
	
    $.post("SortByAnswerTab",
    	    {
    	    	tab: tab,
    			questionid: questionid,
    			userid: userid,
    			page: page
    	    },
    	    function(data, status){
    	    	if(status=="success"){
    	    		if(data.substring(0,7)=="Success"){
    	    			data=data.substring(8);
    	    			if(data.substring(0,6)=="Failed"){
        	    			$("#"+tab).html("</br><font color=\"red\">Error while loading answers try again</font>");
        	    		} else{
        	    			$("#"+tab).html(data);
        	    		}
    	    			var count=$("#"+tab+" >table").length;
    	    			if(count==0) $("#"+tab).html("</br><font color=\"blue\">No answers be first to answer <i class=\"fa fa-smiley-o\"></i></font>");
    	    			$("#numberofanswer"+questionid).html(count+" Answers");
    	    			if(tab=="myanswer"&&count==0){
    	    				$("#myanswer").html("</br><font color=\"blue\">You haven't answered yet <i class=\"fa fa-smiley-o\"></i></font>");
    	    			}
    	    		} else $("#"+tab).html("</br><font color=\"red\">Error while loading answers try again</font>");
    	    	} else $("#"+tab).html("</br><font color=\"red\">Unable to connect to server try again</font>");
    	    });
}



function verifyEmail(userid){
	var emailinput=$("#verifyemail");
	var email=emailinput.val();
	
	var b=0,c=0;
	var st=email.split("@");
	for(var i=0;i<email.length;i++) if(email.charAt(i)=='.') c=1;
	if(email.length>4&&st.length==2&&st[0]!=""&&st[1]!=""&&email.length<=45){
		b=1;
	}
	if(c==0) b=0;
	if(b==0){
		$("#verifyemailspan").html("<span class=\"t-red\">Invalid email</span>"); return;
	}
	
	$("#verifyemailbutton").html("<i class=\"fa fa-spinner fa-pulse fa-lg\"></i> Sending");
	$("#verifyemailspan").html("");
    $.post("VerifyEmail",
    	    {
    	    	userid: userid,
    			email: email
    	    },
    	    function(data, status){
    	    	if(status=="success"){
    	    		if(data.substring(0,7)=="Success"){
    	    			alert("Verification mail has been sent to address: "+email);
    	    			$("#verifyemailspan").html("<span class=\"t-green\">Mail sent</span>");
    	    		} else $("#verifyemailspan").html("<span class=\"t-red\">Error sending mail try again</span>");
    	    		$("#verifyemailbutton").html("<i class=\"glyphicon glyphicon-ok\"></i> Verify");
    	    	}
    	    });
}


function notification(operation,userid,notificationid){
	if(operation=="show"){
		if($("#notificationdiv").is(":visible")==false){
			$("#notificationdiv").html("<div class=\"t-center\"><i class=\"fa fa-spinner fa-pulse fa-2x t-blue\"></i></div>");
		} else return;
	}
	
	$.post("Notification",
    	    {
    			operation: operation,
    			userid: userid,
    			notificationid: notificationid
    	    },
    	    function(data, status){
    	    	if(status=="success"){
    	    		data=data.split("#");
    	    		if(data[0]=="Success"){
    	    			$("#notificationdiv").fadeTo("slow",0.1,function(){
    	    				if(data[1]=="0") $("#notificationcount").hide("slow");
    	    				else{
    	    					$("#notificationcount").html(data[1]);
    	    					$("#notificationcount").show("slow");
    	    				}
    	    				$("#notificationdiv").html(data[2]);
    	    				$("#notificationdiv").fadeTo("slow",1);
    	    			});
    	    		} else{
    	    			$("#notificationdiv").html("<span class=\"t-red\">Error while fetching notifications try again</span>");
    	    		}
    	    	}
    	    });
}

function eye(password){
	if($("#"+password).attr("type")=="password"){
		$("#"+password).prop("type","text");
		$(".eye").html("<i class=\"fa fa-eye-slash fa-lg\" onclick=eye(\"password\");></i>");
	} else{
		$("#"+password).prop("type","password");
		$(".eye").html("<i class=\"fa fa-eye fa-lg i-link\" onclick=eye(\"password\");></i>")
	}
}

function searchOrAskQuestion(userid,button){
	text=$("#searchaskinput").val();
	insertWords(userid,text);
	$("#"+button).click();return;
}

function modal(){
	$("#modal").modal("show");
}

function predict(){
	
	var question=$("#searchaskinput").val();
	var s="";
	if($("#hiddendiv").html()==question) return;
	else $("#hiddendiv").html(question);
//	alert("called");
	$.get("TypeQuestion?question="+question,function(data, status){
		if(status=="success"){
			if(data.substring(0,7)=="Success"){
				s=data.split("%;%")[1];
		//		alert(s);
				$("#predictlist").html(s);
			}
		}
	});
}

function insertWords(userid, text){
	$.get("InsertWords?text="+text);
}

//////////////////////////////// jquery /////////////////////////////////

$(document).ready(function(){
	$("#forgot").click(function(){
		$("#loginpanel").slideUp("slow",function(){
			$("#resetpanel").slideDown("slow");
		});
	});

	$('[data-toggle="popover"]').popover().css(z-index,'2000'); // activating the
	
	$('[data-toggle="tooltip"]').tooltip();
	
	$('#modal-content').on('shown.bs.modal', function() {
	    $("#searchaskinput").focus();
	    $(this).find('[autofocus]').focus();
	});

	$(".btn").click(function(){ // to remove the focus after click
		$(this).blur();
	});
	
});

$(document).click(function(event) { // to hide the popover 
    if(!$(event.target).closest('#notificationtable').length) {
        if($('#notificationtable').is(":visible")) {
        	 $('.notificationpopover').popover('hide');
        }
    }
    if($(".messagediv").is(":visible")){
    	$(".messagediv").fadeOut(3000,function(){
    		$(".containerdiv").attr('id','bodycontainer');
    	});
    }
});



//\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\ jquery \\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\

function isAlphabet(a){
	var l=a.length;
	for(var i=0;i<l;i++){
		if((a.charCodeAt(i)>=97&&a.charCodeAt(i)<=122)||(a.charCodeAt(i)>=65&&a.charCodeAt(i)<=90)){
			return 1;
		}
	}
	return 0;
}
function noOfAlphabet(a){
	var l=a.length;
	var c=0;
	for(var i=0;i<l;i++)
		if((a.charCodeAt(i)>=97&&a.charCodeAt(i)<=122)||(a.charCodeAt(i)>=65&&a.charCodeAt(i)<=90))
			c++;
	return c;
}
function onlyAlphabet(a){
	if(a.length==noOfAlphabet(a)) return 1;
	else return 0;
}

function isDigit(a){
	var l=a.length;
	for(var i=0;i<l;i++){
		if(a.charCodeAt(i)>=48&&a.charCodeAt(i)<=57){
			return 1;
		}
	}
	return 0;
}
function noOfDigit(a){
	var l=a.length;
	var c=0;
	for(var i=0;i<l;i++)
		if(a.charCodeAt(i)>=48&&a.charCodeAt(i)<=57)
			c++;
	return c;
}
function onlyDigit(a){
	if(a.length==noOfDigit(a)) return 1;
	else return 0;
}

function isSpecialChar(a){
	var l=a.length;
	for(var i=0;i<l;i++){
		if((a.charCodeAt(i)>=35&&a.charCodeAt(i)<=38)||(a.charCodeAt(i)>=42&&a.charCodeAt(i)<=46)||(a.charCodeAt(i)>=58&&a.charCodeAt(i)<=64)||a.charCodeAt(i)==95){
			return 1;
		}
	}
	return 0;
}

function noOfSpecialChar(a){
	var l=a.length;
	var c=0;
	for(var i=0;i<l;i++)
		if((a.charCodeAt(i)>=35&&a.charCodeAt(i)<=38)||(a.charCodeAt(i)>=42&&a.charCodeAt(i)<=46)||(a.charCodeAt(i)>=58&&a.charCodeAt(i)<=64)||a.charCodeAt(i)==95)
			c++;
	return c;
}
function onlySpecialChar(a){
	if(a.length==noOfSpecialChar(a)) return 1;
	else return 0;
}
///////////////////////////////////////////////////////////////////////          insightful thoughts              //////////////////////////////////////////////////////////