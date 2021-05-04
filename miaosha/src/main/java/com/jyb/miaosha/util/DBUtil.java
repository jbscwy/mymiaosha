package com.jyb.miaosha.util;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

/**
 * @author jyb
 * @since 2021-04-27 22:20
 */
public class DBUtil {

    private static Properties props;

    static {
        try{
            //读取配置文件
            InputStream in=DBUtil.class.getClassLoader().getResourceAsStream("application.properties");
            //创建对象
            props=new Properties();
            //加载配置文件
            props.load(in);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public static Connection getConn() throws Exception{
        String url=props.getProperty("spring.datasource.url");
        String username = props.getProperty("spring.datasource.username");
        String password = props.getProperty("spring.datasource.password");
        String driver = props.getProperty("spring.datasource.driver-class-name");
        Class.forName(driver);
        return DriverManager.getConnection(url,username, password);
    }

}
