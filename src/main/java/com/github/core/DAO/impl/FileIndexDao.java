package com.github.core.DAO.impl;

import com.github.core.DAO.DataSourceFactory;
import com.github.core.DAO.FileIndex;
import com.github.core.model.FileType;
import com.github.core.model.Thing;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import com.github.core.model.Condition;

import javax.sql.DataSource;


public class FileIndexDao implements FileIndex {

    private  final DataSource dataSource;

    public FileIndexDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public List<Thing> search(Condition condition) {
        List<Thing> things=new ArrayList<>();
        Thing thing=new Thing();
        //先获取到数据源
        Connection connection=null;  //连接对象
        PreparedStatement preStatement=null;  //执行sql语句对象
        ResultSet resultSet=null;
        StringBuilder sqlString=new StringBuilder();
        try {
            connection=dataSource.getConnection();

            /**name  文件名是模糊查询 like
             * fileType 是要相等
             * limit 最大查询深度 limit offset
             * orderByAsc  -->order by
             *
            */
            sqlString.append(" select name,path,depth,file_type from file_thing where ");
            sqlString.append(" name like '%")
                    .append(condition.getName())
                    .append("%' and file_type ='")
                    .append(condition.getFileType().name().toUpperCase())
                    .append("' order by depth ")
                    .append(condition.getOrderByAsc()? " asc ":" desc ")
                    .append(" limit ")
                    .append(condition.getLimit())
                    .append(" offset 0");
            System.out.println(sqlString);
            preStatement=connection.prepareStatement(sqlString.toString());
            resultSet=preStatement.executeQuery();
            while(resultSet.next()) //结果集还有元素
            {
                thing.setName(resultSet.getString("name"));
                thing.setPath(resultSet.getString("path"));
                thing.setDepth(resultSet.getInt("depth"));
                thing.setFileType(FileType.lookUpByName
                        (resultSet.getString("file_type"))); //比如通过字符串DOC找到文件类型DOC
                things.add(thing);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            releaseSource(resultSet,preStatement,connection);
        }
        return things;
    }

    @Override
    public void insert(Thing thing) {
        //先获取到数据源
        Connection connection=null;  //连接对象
        PreparedStatement preStatement=null;  //执行sql语句对象
        StringBuilder sqlString=new StringBuilder();
        //sql语句
        sqlString.append("insert  into file_thing " +
                "(name, path, depth, file_type)" +
                " VALUES (?,?,?,?)");  //占位参数依次是1,2,3,4
        //设置sql语句的参数
        try {
            connection=dataSource.getConnection();
            preStatement=connection.prepareStatement(sqlString.toString());
            preStatement.setString(1,thing.getName()); //设置sql语句的name属性值
            preStatement.setString(2,thing.getPath());//设置sql语句的path属性值
            preStatement.setInt(3,thing.getDepth()); //设置sql语句的路径长度值
            preStatement.setString(4,thing.getFileType().name()); //返回的枚举对象，用name() 设置sql语句的文件类型

            //执行Sql语句
            preStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        finally {
            //调用方法关闭资源
            releaseSource(null,preStatement,connection);
        }

    }

    //因为插入查询都需要close资源，为了代码的不重复，采用重构，利用一个专门的方法释放资源
    private void releaseSource(ResultSet resultSet,PreparedStatement preStatement,Connection connection)
    {
        //关闭结果集
        if(resultSet!=null)
        {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        //关闭执行对象
        if(preStatement !=null)
        {
            try {
                preStatement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        //关闭连接
        if(connection !=null)
        {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {

        FileIndexDao fileIndexDao=new FileIndexDao(DataSourceFactory.getDataSource());

        Thing thing=new Thing();
        thing.setName("简历2");
        thing.setPath("D:\\a\\简历2.ppt");
        thing.setDepth(3);
        thing.setFileType(FileType.DOC);
        //fileIndexDao.insert(thing);
        List<Thing> things=new ArrayList<>();
        Condition condition=new Condition();
        condition.setName("简历");
        condition.setFileType(FileType.DOC);
        condition.setLimit(4);
        condition.setOrderByAsc(true);

        things=fileIndexDao.search(condition);
        for(Thing thing1:things)
        {
            System.out.println(thing1);
        }
    }
}
