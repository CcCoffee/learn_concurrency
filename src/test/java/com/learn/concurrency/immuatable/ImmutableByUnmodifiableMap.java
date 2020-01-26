package com.learn.concurrency.immuatable;

import com.learn.concurrency.annotation.ThreadSafe;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@ThreadSafe //只读所以线程安全
public class ImmutableByUnmodifiableMap {

    // 没有final
    private static Map<Integer, Integer> map = new HashMap<>();

    static {
        // 代码实现是通过装饰器模式禁用所有操作数据的方法了，直接抛出UnsupportedOperationException
        map = Collections.unmodifiableMap(map);
    }

    public static void main(String[] args) {
        // Exception in thread "main" java.lang.UnsupportedOperationException
        // 通过Collections处理过的map不可以再被修改
        map.put(1,33); // 不再会在IDE时出现编译异常，而是出现运行时异常
        log.info("map get key 1 = {}", map.get(1));
    }
}
