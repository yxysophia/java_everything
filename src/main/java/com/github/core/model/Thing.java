package com.github.core.model;

import lombok.Data;

/**
 * 文件信息类
 * 文件名
 * 文件路径
 * 文件深度
 * 文件类型
 */

@Data  //注解：生成每个成员变量的getter和setter
public class Thing {

    //在用户检索时，只需要输文件名
    //File D:/a/b/hello.txt  ->hello.txt（文件名）

    private String name; //文件名
    private String path;  //文件路径
    private Integer depth;  //文件深度
    private FileType fileType;  //文件类型

}
