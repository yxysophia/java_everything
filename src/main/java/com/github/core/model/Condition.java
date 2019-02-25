package com.github.core.model;

import lombok.Data;

/**
 * 检索的参数
 * 文件名
 * 文件类型
 */

@Data
public class Condition {

    private String name; //文件名

    private FileType fileType ; //文件类型

    private Boolean orderByAsc =true ; //根据文件深度的排序类型

    private Integer limit =100; //搜索文件的最大深度
}
