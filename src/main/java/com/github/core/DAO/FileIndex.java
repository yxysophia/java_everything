package com.github.core.DAO;

import com.github.core.model.Thing;

import java.util.List;
import com.github.core.model.Condition;

/**
 * 因为查询和插入都需要将数据库初始化，即获取数据源，所以插入和查询放在一个接口中，使用一个变量数据源
 *
 * 查询：1.数据库的初始化
 * 2.数据库的访问工作
 * 3.实现检索（查询）业务
 *
 * 插入：1.数据库的初始化
 * 2.数据库的访问工作
 * 3.实现索引（插入）业务
 *
 */
public interface FileIndex {


    /**
     * @param condition  根据用户的参数进行查询
     * @return  所有符合条件的文件对象
     */
    List<Thing>  search(Condition condition);

    /**
     *
     * @param thing  将文件对象进行插入
     */
    void insert(Thing thing);

}
