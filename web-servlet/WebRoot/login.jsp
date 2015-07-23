<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
<%
String path = request.getContextPath();
String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/";
%>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
  <head>
    <base href="<%=basePath%>">
    
    <title>My JSP 'login.jsp' starting page</title>
    
	<meta http-equiv="pragma" content="no-cache">
	<meta http-equiv="cache-control" content="no-cache">
	<meta http-equiv="expires" content="0">    
	<meta http-equiv="keywords" content="keyword1,keyword2,keyword3">
	<meta http-equiv="description" content="This is my page">
	<!--
	<link rel="stylesheet" type="text/css" href="styles.css">
	-->

  </head>
  
  <body>
  	<!-- action表示你要提交的地址，可以是servlet的请求地址，也可以是jsp或者html页面地址 ；
  	默认的methos为get方式，此种方式为将信息添加到url后面，而指定为post则不会-->
    <form action="result.jsp" method="post">
    	username:<input type="text" name="username"/><br/>
    	password:<input type="password" name="password"/><br/>
    	
    	<input type="submit" value="submit" />&nbsp;&nbsp;
    	<input type="reset" value="reset"/>
    </form>
  </body>
</html>
