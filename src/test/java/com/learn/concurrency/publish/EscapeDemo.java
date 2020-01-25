package com.learn.concurrency.publish;

import com.learn.concurrency.annotation.NotRecommend;
import com.learn.concurrency.annotation.NotThreadSafe;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NotThreadSafe
@NotRecommend
public class EscapeDemo {
    private int thisCanBeEscape = 1;

    public EscapeDemo() {
        // 相当于在构造函数的过程中启动了一个线程，无论是隐式的启动还是显式的启动都会造成这次引用的逸出。新线程总会在所属对象构造完毕之前
        // 就已经看到它了，所以如果要在构造函数中创建线程，那么不要启动它，而是采用一个专有的start或初始化的方法来统一启动线程，这里可以
        // 采用工厂方法和私有构造函数来完成对象创建和监听器的注册等等。
        InnerClass innerClass = new InnerClass();
        // 还有业务需要执行
        thisCanBeEscape++;
        log.info("innerClass获取的thisCanBeEscape = {}",innerClass.thisCanBeEscape);//innerClass获取的thisCanBeEscape = 1

    }

    /**
     * 内部类包含了对外部类实例属性的引用，这样当外部类完全构造完成之前该外部类实例引用就被发布给了内部类。
     * 这有可能产生不安全的因素，如果要使用thisCanBeEscape时，很有可能会出现对象不一致的状态。
     * 本例子中InnerClass.this.thisCanBeEscape最终值为1而不是2，没有能反应Escape.this.thisCanBeEscape++的影响。
     */
    private class InnerClass {
        private int thisCanBeEscape;
        public InnerClass() {
            thisCanBeEscape = EscapeDemo.this.thisCanBeEscape;
            log.info("{}", EscapeDemo.this.thisCanBeEscape);
        }

        public int getThisCanBeEscape(){
            return this.thisCanBeEscape;
        }
    }

    public static void main(String[] args) {
        new EscapeDemo();
    }
}