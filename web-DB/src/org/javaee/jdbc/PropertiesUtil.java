package org.javaee.jdbc;

import java.io.IOException;
import java.util.Properties;

/**
 * @author lailulu
 * 2015-7-24 下午03:27:42
 * 通过配置文件来获取数据的好处？ 
 * 	只需要更改配置文件（比如：用那个数据库实现JDBC/Oracle/Mysql），就可以获取不同数据库的应用。
 */
public class PropertiesUtil {
	
	private static Properties jdbcProp;
//	private static Properties daoProp;//比如注入不用的dao实现类也可以通过配置属性文件动态指定
	
	public static Properties getJdbcProp(){
		try {
			if(jdbcProp == null){
				jdbcProp = new Properties();
				jdbcProp.load(PropertiesUtil.class.getClassLoader().getResourceAsStream("jdbc.properties"));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jdbcProp;
	}
	
}
