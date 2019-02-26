package com.github.core.DAO;

import com.alibaba.druid.pool.DruidDataSource;
import javax.sql.DataSource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Scanner;


/**
 * 创建数据源
 * 执行数据库脚本
 * 采用工厂模式：构建对象工厂
 * 单例模式：只有一个数据源
 */
public class DataSourceFactory {

    private static volatile  DruidDataSource dataSource;  //用static和volatile

    //构造方法私有
    private DataSourceFactory()
    {

    }
    public static DataSource getDataSource()  {
        if(dataSource==null)
        {
            synchronized (DataSourceFactory.class)
            {
                //再次判断是因为假如线程1和线程2都进入第一个if，
                // 线程1获得锁后new对象，释放锁后，线程2获得锁，如果没有if判断，线程2将会再次new一个数据源
                if(dataSource==null)
                {
                    //实例化一个数据源
                    dataSource=new DruidDataSource();
                    //设置数据源的驱动名称
                    dataSource.setDriverClassName("org.h2.Driver");


                    ///读Druid资源文件信息
                    Properties p = new Properties();
                    try {
                        p.load(DataSourceFactory.class.getClassLoader().getResourceAsStream("druid.properties"));
                        dataSource.setTestWhileIdle(Boolean.parseBoolean((String) p.get("testWhileIdle")));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    //正常需要url username password JDBC规范中关于MySQL jdbc:mysql://ip:port/databaseName
                    //由于采用的数据库是h2,h2是嵌入式数据库，
                    // 以本地文件形式（在本地磁盘）存储，不需要username password
                    //JDBC规范中关于H2  jdbc:h2:filepath --->存储到本地文件
                    //JDBC规范中关于H2  jdbc:h2:~filepath --->存储到当前用户的家目录
                    //JDBC规范中关于H2  jdbc:h2://ip:port/databaseName --->存储到服务器

                    //获取当前工作路径
                    //TODO 在config里获取
                    String workDir=System.getProperty("user.dir");//D:\VScode\Ideamavn\java_everything
                    dataSource.setUrl("jdbc:h2:"+workDir+ File.separator+"java_everything.sql");
                    //java_everthing是数据库名称
                }
            }
        }
        return dataSource;
    }

    //初始化数据库
    public static void initDatbase()
    {
        //1.获取数据源
        DataSource dataSource=DataSourceFactory.getDataSource();
        //2.获取Sql语句
        //获取数据库的输入流读取sql语句
        //不采取读取绝对路径的文件，采取读取classpath下的文件，当做一个类处理
        //把资源文件读取并且转为输入流

        InputStream inputStream=null;
        Scanner scanner=null;
        try {
             inputStream=DataSourceFactory.class.getClassLoader().
                    getResourceAsStream("java_everything.sql");
            if(inputStream==null)
            {
                throw new RuntimeException("initDatabases filed,please check dataName");
            }

            StringBuilder sbSQL=new StringBuilder(); //放置SQL语句
            scanner=new Scanner(inputStream);
            while(scanner.hasNext())
            {
                String line=scanner.nextLine();
                if(!line.startsWith("--"))
                {
                    sbSQL.append(line);
                }
            }
            //3.获取数据库连接和名称执行SQL
            String sql=sbSQL.toString(); //获取sql语句
            //JDBC编程
            //3.1加载数据库驱动---创建数据源时准备好
            try {
                //3.2获取数据库的连接，用的数据源---数据库连接，不需要
                Connection connection=dataSource.getConnection();
                //3.3创建命令
                PreparedStatement statement=connection.prepareStatement(sql);
                //3.4执行sql语句
                statement.execute();

                //3.5关闭资源 先关执行命令 再关连接
                statement.close();
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            scanner.close();
        }
    }

}
