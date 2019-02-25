package com.github.core.common;

import com.github.core.model.FileType;
import com.github.core.model.Thing;

import java.io.File;

/**
 * 将文件对象转换成Thing对象
 * Thing对象包含
 * 文件名
 * 文件路径
 * 文件深度
 * 文件类型
 */
public class FileCovertThing {


    public static Thing Covert(File file)
    {
        Thing thing=new Thing();
        thing.setName(file.getName());
        thing.setPath(file.getPath());
        thing.setDepth(computeDepth(file));
        thing.setFileType(computeType(file));
        return thing;
    }

    //通过具体文件计算该文件的深度
    private static int computeDepth(File file)
    {
        String[] Depth=file.getAbsolutePath().split("\\\\"); // "\\"是转义字符
        return Depth.length;
    }

    private static FileType computeType(File file)
    {
        if(file.isDirectory()) //如果是文件夹
            return FileType.OTHER;
        String fileName=file.getName();
        int index=fileName.lastIndexOf("."); //找到最后一个以.结束的位置
        if(index!=-1 && index!=fileName.length()) //可能有a.后面没有后缀名
        {
            String extend=fileName.substring(index+1);
            return FileType.lookUp(extend);  //根据扩展名找文件类型
        }
        return FileType.OTHER;
    }
}
