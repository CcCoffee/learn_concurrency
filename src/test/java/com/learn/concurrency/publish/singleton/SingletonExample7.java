package com.learn.concurrency.publish.singleton;

import com.learn.concurrency.annotation.Recommend;
import com.learn.concurrency.annotation.ThreadSafe;

/**
 * 使用枚举实例化，最安全
 */
@ThreadSafe
@Recommend
public class SingletonExample7 {

    private SingletonExample7(){

    }

    public static SingletonExample7 getInstance() {
        return Singleton.INSTANCE.getInstance();
    }

    private enum Singleton {
        INSTANCE;

        private SingletonExample7 singleton;

        /**
         * JVM保证这个方法只调用一次
         */
        Singleton(){
            singleton = new SingletonExample7();
        }

        public SingletonExample7 getInstance(){
            return singleton;
        }
    }
}
