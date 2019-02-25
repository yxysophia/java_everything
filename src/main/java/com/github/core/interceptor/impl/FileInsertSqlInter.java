package com.github.core.interceptor.impl;


import com.github.core.DAO.impl.FileIndexDao;
import com.github.core.common.FileCovertThing;
import com.github.core.interceptor.FileInter;
import com.github.core.model.Thing;

import java.io.File;

//将文件对象进行插入到数据库
public class FileInsertSqlInter implements FileInter {

    private  FileIndexDao fileIndexDao;

    public FileInsertSqlInter(FileIndexDao fileIndexDao) {
        this.fileIndexDao = fileIndexDao;
    }

    @Override
    public void apply(File file) {
        //首先需要将文件对象转换为Thing对象
        Thing thing=FileCovertThing.Covert(file);
        //将thing对象写入数据库
            fileIndexDao.insert(thing);
    }
}
