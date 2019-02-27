package com.github.core.monitor.impl;

import com.github.config.EverythingConfig;
import com.github.core.DAO.impl.FileIndexDao;
import com.github.core.common.FileCovertThing;
import com.github.core.monitor.FileWatch;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;
import java.io.FileFilter;

/**
 * 文件监听
 * 在Apache的Commons-IO中有关于文件的监控功能的代码. 文件监控的原理如下：
 * 由文件监控类FileAlterationMonitor中的线程不停的扫描文件观察器FileAlterationObserver，
 * 如果有文件的变化，则根据相关的文件比较器FileAlterationListener的实现类来判断文件时新增，还是删除，还是更改。
 * FileAlterationListener专门用来接收文件系统的通知
 *
 */
public class FileWatchImpl implements FileWatch ,FileAlterationListener {

    private FileAlterationMonitor monitor;
    private FileIndexDao fileIndexDao;

    public FileWatchImpl(FileIndexDao fileIndexDao) {
        this.fileIndexDao = fileIndexDao;
        this.monitor=new FileAlterationMonitor(10);
        //10ms监听一次
    }

    @Override
    public void start() {
        try {
            monitor.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void monitor(EverythingConfig config) {
        //监听includePath
        for(String path :config.getIncludePath())
        {
            this.monitor.addObserver(new FileAlterationObserver(path,
                            new FileFilter() {
                                @Override
                                public boolean accept(File pathname) {
                                    String currentPath=pathname.getAbsolutePath();
                                    for(String exclude:config.getExcludePath())
                                    {
                                        //当前文件是要过滤文件，返回false
                                        if(exclude.startsWith(currentPath))
                                            return false;
                                    }
                                    return true; //不包含在过滤文件
                                }
                            })
                    );
        }
    }

    @Override
    public void stop() {
        try {
            monitor.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStart(FileAlterationObserver fileAlterationObserver) {
        fileAlterationObserver.addListener(this);
    }

    @Override
    public void onDirectoryCreate(File file) {

    }

    @Override
    public void onDirectoryChange(File file) {

    }

    @Override
    public void onDirectoryDelete(File file) {

    }

    @Override
    public void onFileCreate(File file) {
        System.out.println("onFileCreate:"+file);
        fileIndexDao.insert(FileCovertThing.Covert(file));
    }

    @Override
    public void onFileChange(File file) {

    }

    @Override
    public void onFileDelete(File file) {
        fileIndexDao.delete(FileCovertThing.Covert(file));
    }

    @Override
    public void onStop(FileAlterationObserver fileAlterationObserver) {
        fileAlterationObserver.addListener(this);
    }
}
