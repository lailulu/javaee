package org.javaee.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class RegisterServlet extends HttpServlet{
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		process(req,resp);
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		process(req,resp);
	}
	
	private void process(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String username = req.getParameter("username");
		String password = req.getParameter("password");
		String repassword = req.getParameter("repassword");
		int age = Integer.parseInt(req.getParameter("age"));
		
		resp.setContentType("text/html");
		PrintWriter out = resp.getWriter();
		
		String result="";
		if(password.equals(repassword)&&age>18){
			result +="success";
		}
		if(!password.equals(repassword)){
		    result +="password != repassword";	
		}
		if(age<=18){
			result +="age <= 18";
		}
        out.println("<html><head><title>registeraction result</title></head>");
        out.println("<body><h1>"+result+"</h1></body></html>");
        out.flush();
	}
	

}
