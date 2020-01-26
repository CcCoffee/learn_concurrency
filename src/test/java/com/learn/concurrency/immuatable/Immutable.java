package com.learn.concurrency.immuatable;

import com.learn.concurrency.annotation.NotThreadSafe;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@NotThreadSafe
public class Immutable {

    // 修饰的如果是基本类型变量，那么变量一旦在初始化之后就不能再修改了。
    private static final int COUNT = 0;
    private static final char A = 'A';
    // 如果是引用类型的变量，则在对其初始化之后便不能让它再指向另外一个对象。
    private static final Map<Integer, Integer> map = new HashMap<>();

    static {
        map.put(1,3);
        map.put(3,4);
    }

    public static void main(String[] args) {
//        COUNT = 4; //IDE报错
//        map = new HashMap<>(); //IDE报错

        // 其他线程可以随时修改里面的值，容易引发线程安全问题
        map.put(1,33);
        log.info("map get key 1 = {}", map.get(1));
    }

    private void test(final int a){
//        a = 22;
    }
}
