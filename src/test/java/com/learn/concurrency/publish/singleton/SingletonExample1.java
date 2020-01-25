package com.learn.concurrency.publish.singleton;

import com.learn.concurrency.annotation.NotThreadSafe;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 懒汉模式
 * 单例实例在第一次使用时进行创建
 */
@Slf4j
@NotThreadSafe
public class SingletonExample1 {

    private static AtomicInteger count = new AtomicInteger(0);

    // 私有构造函数
    private SingletonExample1() {
        count.incrementAndGet();
    }

    // 单例对象
    private static SingletonExample1 instance = null;

    // 静态的工厂方法
    public static SingletonExample1 getInstance() {
        if (instance == null) {
            //new对象并非原子操作
            instance = new SingletonExample1();
        }
        return instance;
    }
}