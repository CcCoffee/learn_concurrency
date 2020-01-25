package com.learn.concurrency.publish.singleton;

import com.learn.concurrency.annotation.NotThreadSafe;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 懒汉模式 -> 双重同步锁单例模式
 * 单例实例在第一次使用时进行创建
 */
@Slf4j
@NotThreadSafe
public class SingletonExample4 {

    private static AtomicInteger count = new AtomicInteger(0);

    // 私有构造函数
    private SingletonExample4() {
        count.incrementAndGet();
    }

    // 单例对象
    private static SingletonExample4 instance = null;

    /**
     * 使用双重检测机制实例化对象
     *
     * @return 实例对象
     */
    public static SingletonExample4 getInstance() {
        // 双重检测机制，如果线程A进入同步锁块，并部分实例化了instance，线程B进入此行代码会直接返回instance，造成对象逸出
        if (instance == null) {
            //new对象并非原子操作
            synchronized (SingletonExample4.class){ // 同步锁
                if(instance == null){
                    //new非原子操作，可能发生指令重排，在单线程下没有影响，但是在多线程下不一定。
                    // 比如对于Thread A先执行了3，2还没有执行就线程中断，Thread B此时看到的instance不为空，直接返回对象
                    // 但instance还没有被初始化
                    //1. memory = allocate()分配对象的内存空间
                    //2. createInstance()初始化对象
                    //3. instance = memory 设置instance指向刚分配的内存
                    instance = new SingletonExample4();
                }
            }

        }
        return instance;
    }
}