package com.github.core.interceptor.impl;

import com.github.core.DAO.impl.FileIndexDao;
import com.github.core.interceptor.ThingInter;
import com.github.core.model.Thing;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;


/**
 * 从数据库删除数据
 */
public class DeleteThingFromSqlInter implements ThingInter,Runnable{

    private FileIndexDao fileIndexDao;

    //大小为1024的有界数组阻塞队列  当队列满时会阻塞，向空队列获取元素将会发生阻塞
    private Queue<Thing> thingQueue=new ArrayBlockingQueue<>(1024);



    public DeleteThingFromSqlInter(FileIndexDao fileIndexDao) {
        this.fileIndexDao = fileIndexDao;
    }

    @Override
    public void apply(Thing thing) {
        thingQueue.add(thing);
    }


    @Override
    public void run() {
        while(true)
        {
            Thing thing=thingQueue.poll(); //从该阻塞队列中获取元素，没有元素将会等待
            if(thing!=null)  //队列可能返回null
                fileIndexDao.delete(thing);
            try {
                Thread.sleep(100);  //100ms删除一个文件
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
