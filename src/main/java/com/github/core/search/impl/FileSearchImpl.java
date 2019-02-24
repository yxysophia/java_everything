package com.github.core.search.impl;

import com.github.core.DAO.DataSourceFactory;
import com.github.core.DAO.FileIndex;
import com.github.core.DAO.impl.FileIndexDao;
import com.github.core.model.Condition;
import com.github.core.model.Thing;
import com.github.core.search.FileSearch;

import java.util.List;

public class FileSearchImpl implements FileSearch {
    FileIndex fileIndex=new FileIndexDao(DataSourceFactory.getDataSource());

    @Override
    public List<Thing> search(Condition condition) {
        return fileIndex.search(condition);
    }
}
