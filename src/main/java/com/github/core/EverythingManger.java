package com.github.core;

import com.github.config.EverythingConfig;
import com.github.core.DAO.DataSourceFactory;
import com.github.core.DAO.impl.FileIndexDao;
import com.github.core.Index.FileScan;
import com.github.core.Index.impl.FileScanImpl;
import com.github.core.interceptor.FileInter;
import com.github.core.interceptor.ThingInter;
import com.github.core.interceptor.impl.DeleteThingFromSqlInter;
import com.github.core.interceptor.impl.FileInsertSqlInter;
import com.github.core.model.Condition;
import com.github.core.model.Thing;
import com.github.core.search.impl.FileSearchImpl;
import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 *是为了统一调度
 * 1.创建索引，将每个盘符下的文件添加都数据库
 * 2.检索，根据用户要求进行查找
 * 采用单例模式
 */
public class EverythingManger {

    private static volatile EverythingManger everythingManger;
    private FileSearchImpl fileSearch;
    private FileScanImpl fileScan;
    private ExecutorService executorService;

    /**
     * 清理不存在的文件
     */
    private DeleteThingFromSqlInter deleteThingFromSqlInter;
    private Thread backgroundcleanThread;   //清理线程
    private AtomicBoolean cleanthreadStatus=new AtomicBoolean(false);//默认没有启动清理线程

    private EverythingManger(){

    }

    private void initScanSearch()
    {
        FileIndexDao fileIndexDao=new FileIndexDao(DataSourceFactory.getDataSource());
        fileSearch=new FileSearchImpl(fileIndexDao);
        fileScan=new FileScanImpl();
        FileInter fileInter=new FileInsertSqlInter(fileIndexDao);
        fileScan.addInterceptor(fileInter);
        deleteThingFromSqlInter=new DeleteThingFromSqlInter(fileIndexDao);
        backgroundcleanThread =new Thread(deleteThingFromSqlInter,"cleanThing-Thread");
        backgroundcleanThread.setDaemon(true); //设置为守护线程，当所有线程停止该线程也就停止

    }

    //初始化数据库
    private void InitSql()
    {
        /**
         * String workDir=System.getProperty("user.dir");
         *         String sqlPath=workDir+ File.separator+"java_everything.mv.db";
         *         File sqlFile=new File(sqlPath);
         *         if(! sqlFile.exists())  //数据库不存在，即没有初始化数据库
         *         {
         *             DataSourceFactory.initDatbase();
         *         }
         */
        //上述初始化数据库方式有问题，因为一旦数据库连接，就要mv.db文件，就不会初始化数据库，但是数据库中表并不存在
        //所以需要在第一次使用everything和重建索引(buildIndex)时初始化数据库
        System.out.println("初始化数据库");
        DataSourceFactory.initDatbase();
    }

    public static EverythingManger getEverythingManger()
    {
        if(everythingManger==null)
        {
            synchronized (EverythingManger.class)
            {
                if(everythingManger==null)
                {
                    everythingManger=new EverythingManger();
                    everythingManger.initScanSearch(); //需要遍历的
                    everythingManger.InitSql(); //初始化数据库
                }
            }
        }
        return everythingManger;
    }

    /**
     * 按照用户需求进行检索
     * @param condition
     * @return
     */
    public List<Thing> search(Condition condition)
    {
        //Stream流式处理 JDK8
        //文件不存在剔除掉

        return this.fileSearch.search(condition)
                .stream().filter((thing) -> {
                    String path=thing.getPath();
                    File file=new File(path);
                    if(file.exists())
                        return true;
                    else
                    {
                        //该文件不存在，将该文件删除
                        //由于删除的时候需要阻塞，只能等删除完才能搜索，就需要有一个删除线程，在后台删除
                        deleteThingFromSqlInter.apply(thing);//将该不存在文件添加到删除阻塞队列，如果队列满，会阻塞
                        return false;
                    }
                }).collect(Collectors.toList());
    }

    /**
     * 遍历所有盘符，将文件写入数据库
     */
    public void BuildIndex()
    {
        //将文件写入数据库时需要将数据库初始化
        InitSql(); //重建索引需要初始化数据库
        EverythingConfig config=EverythingConfig.getConfig();
        Set<String> includePath=config.getIncludePath();
        int pathCount=includePath.size();//需要遍历的盘符个数
        if(executorService==null)
        {
            executorService=Executors.newFixedThreadPool(pathCount, new ThreadFactory() {
                private final AtomicInteger count=new AtomicInteger(0);
                @Override
                public Thread newThread(Runnable r) {
                    Thread thread=new Thread
                            (r,"Thread-Scan"+count.getAndIncrement());
                    return thread;
                }
            });

            CountDownLatch countDownLatch=new CountDownLatch(pathCount);
            System.out.println("Scan begin...");
            //有多少个盘符，启动多少个线程，每个线程遍历自己的盘符
            for(String path:includePath)
            {
                executorService.submit(new Runnable() {
                    @Override
                    public void run() {
                        fileScan.index(path);
                    }
                });
                countDownLatch.countDown(); //一个线程结束值-1
            }
            try {
                countDownLatch.await(); //如果还有线程，将会一直阻塞，直至线程数为1
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Scan ending");
        }
    }


    public void startCleanThread()
    {
        //期望该清理线程没哟启动，然后改为true
        if(this.cleanthreadStatus.compareAndSet(false,true))
        {
            backgroundcleanThread.start();
        }
        else
        {
            System.out.println("cleanThread had started and would not be started again");
        }
    }
}
