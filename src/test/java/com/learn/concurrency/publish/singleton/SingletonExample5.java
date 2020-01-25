package com.learn.concurrency.publish.singleton;

import com.learn.concurrency.annotation.ThreadSafe;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 懒汉模式 -> 双重同步锁单例模式
 * 单例实例在第一次使用时进行创建
 */
@Slf4j
@ThreadSafe
public class SingletonExample5 {

    private static AtomicInteger count = new AtomicInteger(0);

    // 私有构造函数
    private SingletonExample5() {
        count.incrementAndGet();
    }

    // 单例对象
    private volatile static SingletonExample5 instance = null;

    /**
     * 使用双重检测机制实例化对象
     *
     * @return 实例对象
     */
    public static SingletonExample5 getInstance() {
        // 双重检测机制
        if (instance == null) {
            //new对象并非原子操作
            synchronized (SingletonExample5.class){ // 同步锁
                if(instance == null){
                    // 使用volatile限制指令重排序
                    instance = new SingletonExample5();
                }
            }

        }
        return instance;
    }
}