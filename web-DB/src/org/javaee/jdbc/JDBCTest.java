package org.javaee.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author lailulu
 * 2015-7-24 下午01:45:44
 */
public class JDBCTest {
	
	/*
	 * 使用预编译语句PreparedStatement（可以解决SQL注入攻击），java会在语句正式执行之前对它进行一个编译
	 * 
	 * mysql的驱动 ： Class.forName("com.mysql.jdbc.Driver");
	 * mysql连接创建：	conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbname" +
	 * 					"?useUnicode=true&characterEncoding=utf8",
	 * 					"username","password");
	 */
	
	/*
	 //连接oracle数据库，获取连接操作，使用statement语句
	 public static void main(String[] args) throws Exception {
		//注册驱动
		Class.forName("oracle.jdbc.driver.OracleDdiver");
		//创建连接
		Connection conn = DriverManager.getConnection("jdbc:oracle:thin:@192.168.1.110:8080:dbname",
				"username","password");
		//获取statement对象（数据库端）
		Statement stat = conn.createStatement();
		String sql = "select * from lll_emp";
		//发送SQL，获取结果集
		ResultSet rs = stat.executeQuery(sql);
		//从结果集获取数据
		while (rs.next()) {
			//下标从1开始，rs.get**参数为列的索引（查询结果中的索引而不是原来表中的索引）
			// 获取结果集的值也可以通过对于的字段名来获取（推荐使用，如rs.getInt("id")）
			//rs.getIng(0)出现异常：SQLException无效的列索引
			System.out.println(rs.getInt(1)+"\t"+rs.getString(2)+"\t"+rs.getDouble(3));
		}
		//关闭连接
		rs.close();
		stat.close();
		conn.close();
	}
	*/
	
	//使用PreparedStatement
	public static void main(String[] args) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conn = DriverManager.getConnection("jdbc:oracle:@localhost:1521:dbname","username","password");
			pstmt = conn.prepareStatement("select score.* from score,student" +
					"  where score.stuId=student.id and student.name= ?");
			pstmt.setString(1, "studenName");//参数1表示第一个“?”,参数2表示传入的值
			rs = pstmt.executeQuery();
			while (rs.next()) {
				System.out.println(rs.getInt("subject")+" "+ rs.getFloat("score"));
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{//关闭连接
			if(rs != null){
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(pstmt != null){
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(conn != null){
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
