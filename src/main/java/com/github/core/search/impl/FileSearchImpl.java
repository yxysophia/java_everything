package com.github.core.search.impl;

import com.github.core.DAO.DataSourceFactory;
import com.github.core.DAO.FileIndex;
import com.github.core.DAO.impl.FileIndexDao;
import com.github.core.model.Condition;
import com.github.core.model.Thing;
import com.github.core.search.FileSearch;

import java.util.ArrayList;
import java.util.List;

public class FileSearchImpl implements FileSearch {
    private final FileIndexDao fileIndex;

    public FileSearchImpl(FileIndexDao fileIndex) {
        this.fileIndex = fileIndex;
    }

    @Override
    public List<Thing> search(Condition condition) {
        if(condition==null)  //如果用户查找条件为空
        {
            System.out.println("条件为空");
            return new ArrayList<>();   //返回空
        }
        return fileIndex.search(condition);
    }
}
