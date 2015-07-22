package org.javaee.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author lailulu
 * 2015-7-22 下午02:49:18
 */
public class HelloWorld extends HttpServlet{
	
	/**
	 *  我们可以将Servlet看作是嵌套了HTML代码的Java类；
	 *  可以将JSP看做是嵌套了Java代码的HTML页面。
	 *  注jsp执行过程：容器先将.jsp文件转换成一个.java文件(其实就是将jsp转换一个对应的servlet（含有html代码的）)
	 *  可以看做，JSP就是Servlet，只是需要用容器将jsp转换成servlet。
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.setContentType("text/html");
		PrintWriter out=resp.getWriter();
		out.println("<html><head><title>Hello LuLu </title></head>");
		out.println("<body><h1>Hello fLULUhahh</h1></body></html>");
		out.flush();
	}

}
