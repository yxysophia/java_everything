package com.github.core.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

//文件类型
public enum FileType {

    IMG("png","jpeg","jpe","gif"), //图片形式
    DOC("doc","docx","pdf","ppt"),  //文档形式
    BIN("exe","sh","jar","msi"), //二进制形式 可以是执行文件
    OTHER(".*") ;

    private Set<String> extendSet=new HashSet<String>();  //具体的文件类型 用set是元素不重复

    FileType(String... extend)  //构造方法
    {
        this.extendSet.addAll(Arrays.asList(extend));
    }

    //通过具体类型获取文件类型
    public static FileType lookUp(String extend)
    {
        for(FileType fileType:FileType.values())  //FileType.values()所欲的枚举对象
        {
            if(fileType.extendSet.contains(extend))  //如果文件类型对象的扩展名里包含
            {
                return fileType;
            }
        }
        return FileType.OTHER;  //没有返回其他文件类型
    }
}
