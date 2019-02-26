package com.github.config;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

/**
 * 需要遍历的盘符
 * 不需要遍历的文件夹或文件
 *
 * 配置类需要是单例，配置一次就好
 */

@Getter
@ToString
//includePath excludePath只能够获取，不能修改
public class EverythingConfig {

    private Set<String> includePath=new HashSet<>();
    private Set<String> excludePath=new HashSet<>();

    @Setter
    private Integer maxReturn=30; //返回检索文件的最大深度
    @Setter
    private Boolean orderByAsc=true;  //是否以深度升序返回检索文件

    private static volatile EverythingConfig config;
    private EverythingConfig(){

    }


    //初始化浏览目录和不需要浏览目录
    private void initPath()
    {
        //获取文件系统:设置需要查询的盘符
        FileSystem fileSystem= FileSystems.getDefault();
        Iterable<Path> iterable=fileSystem.getRootDirectories();//获取文件系统的根目录
        iterable.forEach(new Consumer<Path>() {
            @Override
            public void accept(Path path) {
                config.getIncludePath().add(path.toString());//[C:\, E:\, D:\, F:\]
            }
        });

        //设置不需要查询的文件或文件夹
        String os=System.getProperty("os.name");  //当前操作系统
        if(os.startsWith("Windows"))
        {
            config.excludePath.add("C:\\Users");
            config.excludePath.add("C:\\Windows");
            config.excludePath.add("C:\\Program Files");
            config.excludePath.add("C:\\Program Files (x86)");
        }else  //在这里只判断linux系统
        {
            config.excludePath.add("/tmp");
            config.excludePath.add("/etc");
            config.excludePath.add("/root");
        }
    }

    public static  EverythingConfig getConfig()
    {
        //double check
        if(config==null)
        {
            synchronized (EverythingConfig.class)
            {
                if(config==null)
                {
                    config=new EverythingConfig();
                    config.initPath();
                }
            }
        }
        return config;
    }
}
