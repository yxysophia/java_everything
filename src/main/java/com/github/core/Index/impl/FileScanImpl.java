package com.github.core.Index.impl;

import com.github.config.EverythingConfig;
import com.github.core.Index.FileScan;
import com.github.core.interceptor.FileInter;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * 遍历文件并且将文件写入数据库
 */
public class FileScanImpl implements FileScan {

    private List<FileInter> fileInters=new ArrayList<>();
    private EverythingConfig config=EverythingConfig.getConfig();
    @Override
    public void index(String path) {
        File file=new File(path);
        //如果是一个文件并且该文件的父目录是不需要遍历的，直接返回
        if(file.isFile())
        {
            if(config.getExcludePath().contains(file.getParentFile().toString()))
                 return ;
        }
        //如果是一个目录
        else
        {
            if( config.getExcludePath().contains(file.getName())) //如果该目录不需要遍历
                return ;
            File[] files=file.listFiles();  //该目录下所有文件
            if(files!=null)//该目录不为空目录
            {
                for(File file1:files)
                {
                    index(file1.getPath());//递归遍历该目录下文件
                }
            }
        }

        //是一个文件，将文件进行打印和写入数据库，这样就会将一个文件写入
        for(FileInter fileInter:fileInters)
        {
            fileInter.apply(file);
        }
    }

    public void addInterceptor(FileInter fileInter)
    {
        this.fileInters.add(fileInter);
    }
}
