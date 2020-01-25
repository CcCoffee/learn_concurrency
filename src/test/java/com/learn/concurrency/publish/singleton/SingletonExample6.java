package com.learn.concurrency.publish.singleton;

import com.learn.concurrency.annotation.ThreadSafe;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 饿汉模式
 * 单例实例在第一次使用时进行创建
 */
@Slf4j
@ThreadSafe
public class SingletonExample6 {

    private static AtomicInteger count = new AtomicInteger(0);

    // 私有构造函数
    private SingletonExample6() {
        count.incrementAndGet();
    }

    // 虽然可以写于变量声明前面，但是instance会被接下来的语句赋值为null
//    static {
//        instance= new SingletonExample6();
//    }

    // 单例对象
    // 线程安全，缺点如果只进行来加载而没有进行实际的调用的话会造成资源和内存的浪费
    private static SingletonExample6 instance;


    static {
        instance= new SingletonExample6();
    }

    // 静态的工厂方法
    public static SingletonExample6 getInstance() {
        return instance;
    }
}