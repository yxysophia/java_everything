package com.github.core.monitor;

import com.github.config.EverythingConfig;

/**
 * 监听文件的增加和删除
 */
public interface FileWatch {
    void start(); //开启
    void monitor(EverythingConfig config);  //监听目录及文件
    void stop() ; //停止
}

