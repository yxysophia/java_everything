package com.github.cmd;

import com.github.config.EverythingConfig;
import com.github.core.EverythingManger;
import com.github.core.model.Condition;
import com.github.core.model.FileType;
import com.github.core.model.Thing;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Everything主程序
 * 基于字符界面进行交互式访问
 * 用户交互层
 * 用户的需求
 */
public class EverythingCmdApp {


    public static void main(String[] args) {

        //解析命令行参数
        praseArgs(args);

        //欢迎界面
        welcome();

        //everythingManger是统一调度对象
        EverythingManger everythingManger=EverythingManger.getEverythingManger();

        //启动后台清理线程
        everythingManger.startCleanThread();

        //交互式
        client(everythingManger);

    }

    private static  void welcome()
    {
        System.out.println("Welcome to Everything,you can search what u want~~");
    }

    //根据用户需求
    private static void client(EverythingManger everythingManger)
    {
        Scanner scanner=new Scanner(System.in);
        while(true)
        {
            System.out.println("please input your require:");
            String require=scanner.nextLine().trim();
            if(require.startsWith("search"))
            {
                //search name [fileType]
                Condition condition=new Condition();
                String[] re=require.split(" ");
                if(re.length>=2)
                {
                    condition.setName(re[1]);
                    if(re.length==3)
                    {
                        condition.setFileType(FileType.lookUpByName(re[2]));
                        System.out.println(condition.getFileType());
                    }
                  doSearch(condition,everythingManger);
                }else
                {
                    doHelp();
                    continue;
                }
            }else
            {
                switch (require)
                {
                    case "help":
                        doHelp();
                        break;
                    case "quit":
                        doQuit();
                        return ;
                    case "index":
                        doIndex(everythingManger);
                        break;
                        default:
                            doHelp();
                }
            }
        }
    }

    //检索功能
    private static void doSearch(Condition condition,EverythingManger everythingManger)
    {
        EverythingConfig config=EverythingConfig.getConfig();
        condition.setOrderByAsc(config.getOrderByAsc());
        condition.setLimit(config.getMaxReturn());
       List<Thing> things= everythingManger.search(condition);
       if(things.size()==0)
       {
           System.out.println("没有");
       }
       for(Thing thing :things)
       {
           System.out.println(thing.getPath());
       }
    }

    //帮助
    private static void doHelp()
    {
        System.out.println("命令列表:");
        System.out.println("帮助：help");
        System.out.println("退出：exit");
        System.out.println("索引:index");
        System.out.println("检索：search fileName [fileType: img doc bin other]");
    }

    //退出everything
    private static void doQuit()
    {
        System.out.println("感谢您的使用，再见");
        System.exit(0);
    }

    //建立索引，将文件放入数据库
    private static void doIndex(EverythingManger everythingManger)
    {
        //用统一调度器调用建立索引
        //由于建立锁引较慢 用线程
       new Thread(new Runnable() {
           @Override
           public void run() {
               everythingManger.BuildIndex();
           }
       }).start();
    }

    //对命令行参数的解析  返回检索文件最大数量  按照文件深度降序或者升序 检索的目录 不检索的目录
    //java -jar everything.jar --maxReturn=40
    //--depthAsc=false   --includePath=D:\  --excludePath=E:\work,F:me
    public static void praseArgs(String[] args)
    {
        EverythingConfig config=EverythingConfig.getConfig();
        for(String pram:args)
        {
            if(pram.startsWith("--maxReturn="))
            {
                int index=pram.indexOf("=");
                int maxReturn=0; //默认深度为30
                try {
                       maxReturn=Integer.parseInt(pram.substring(index+1));
                   }catch (NumberFormatException e)
                   {
                       System.out.println("Waring:String can not convert to int");
                   }
                config.setMaxReturn(maxReturn);
            }
            else if(pram.startsWith("--depthAsc"))
            {
                int index=pram.indexOf("=");
                Boolean depthAsc=true;
                depthAsc=Boolean.parseBoolean(pram.substring(index+1));
                config.setOrderByAsc(depthAsc);
            }else if(pram.startsWith("--includePath"))
            {
                int index=pram.indexOf("=");
                //需要先将默认的检索目录清除，然后添加用户指定目录
                    config.getIncludePath().clear();
                    String[] include=pram.substring(index+1).split(",");  //指定目录已,间隔
                    for(String includs:include)
                    {
                        config.getIncludePath().add(includs);
                    }

            }else if(pram.startsWith("--excludePath="))
            {
                int index=pram.indexOf("=");
                if(index+1<pram.length()) //证明后面有过滤掉的目录
                {
                    config.getExcludePath().clear();
                    String[] exclude=pram.substring(index+1).split(",");
                    config.getExcludePath().addAll(Arrays.asList(exclude));
                }
            }
        }
    }
}
