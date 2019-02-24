package com.github.core;

import com.github.core.Index.FileScan;
import com.github.core.model.Condition;
import com.github.core.model.Thing;
import com.github.core.search.FileSearch;

import java.util.List;

/**
 *是为了统一调度
 * 1.创建索引，将每个盘符下的文件添加都数据库
 * 2.检索，根据用户要求进行查找
 *
 */
public class EverythingManger {

    private FileSearch fileSearch;
    private FileScan fileScan;

    public EverythingManger(FileSearch fileSearch, FileScan fileScan) {
        this.fileSearch = fileSearch;
        this.fileScan = fileScan;
    }

    /**
     * 按照用户需求进行检索
     * @param condition
     * @return
     */
    public List<Thing> search(Condition condition)
    {
        return this.fileSearch.search(condition);
    }

    /**
     * 遍历所有盘符，将文件写入数据库
     */
    public void BuildIndex()
    {

    }
}
