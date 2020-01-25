package com.learn.concurrency.publish.singleton;

import com.learn.concurrency.annotation.NotRecommend;
import com.learn.concurrency.annotation.ThreadSafe;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * 懒汉模式
 * 单例实例在第一次使用时进行创建
 */
@Slf4j
@ThreadSafe
@NotRecommend
public class SingletonExample3 {

    private static AtomicInteger count = new AtomicInteger(0);

    // 私有构造函数
    private SingletonExample3() {
        count.incrementAndGet();
    }

    // 单例对象
    private static SingletonExample3 instance = null;

    /**
     * 使用synchronized性能开销大
     * @return 实例对象
     */
    public synchronized static SingletonExample3 getInstance() {
        if (instance == null) {
            //new对象并非原子操作
            instance = new SingletonExample3();
        }
        return instance;
    }
}