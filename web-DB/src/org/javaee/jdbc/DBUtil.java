package org.javaee.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author lailulu
 * 2015-7-24 下午02:20:58
 * jdbc connection工具类，提供了连接的获得与关闭功能
 */
public class DBUtil {
	
	//获取连接
	public static Connection getConnection(){
		Connection conn = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbname" +
					  					"?useUnicode=true&characterEncoding=utf8",
					 					"username","password");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return conn;
	}
	
	//释放连接
	//(JDBC中的mysql数据库连接Connection和Statement或PreparedStatement、ResultSet使用完毕后一定要关闭，否则会占用大量内存资源，导致内存溢出)
	public static void close(Connection conn){
		if(conn != null){
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	public static void close(PreparedStatement pstma){
		if(pstma != null){
			try {
				pstma.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	public static void close(ResultSet rs){
		if(rs != null){
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

}
