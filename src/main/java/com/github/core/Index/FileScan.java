package com.github.core.Index;


import com.github.core.interceptor.FileInter;

/**
 * 根据路径来遍历并且添加到数据库
 */
public interface FileScan {
    void index(String path);

    void addInterceptor(FileInter fileInter); //创建一个拦截器
}
