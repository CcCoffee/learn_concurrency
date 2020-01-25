package com.learn.concurrency.publish;

import com.learn.concurrency.annotation.NotThreadSafe;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.Arrays;

@NotThreadSafe
@Slf4j
public class PublishDemo {

    private String[] states = {"a", "b", "c"};

    /**
     * 通过public访问级别发布了类的filed，在类的任何外部的线程都可以访问这些filed，
     * 这样发布对象其实是不安全的，因为无法假设其他线程会不会修改这个filed，从而造成
     * 类里面状态的错误
     *
     * @return
     */
    public String[] getStates() {
        return states;
    }

    /**
     * 不安全地发布对象
     */
    @Test
    public void unsafePublish() {
        // 线程A通过new获得Public的实例
        PublishDemo unsafePublish = new PublishDemo();
        // 通过Publish实例可以通过提供好的public方法直接得到私有filed states数组的引用
        log.info("{}", Arrays.toString(unsafePublish.getStates()));
        // 线程B可以通过实例的public方法修改私有filed，这样在任何一个线程里面真正想使用states里面的数据时
        // 数据是不完全确定的。因此这样发布的对象就是线程不安全的
        unsafePublish.getStates()[0] = "d";
        log.info("{}", Arrays.toString(unsafePublish.getStates()));
    }
}
