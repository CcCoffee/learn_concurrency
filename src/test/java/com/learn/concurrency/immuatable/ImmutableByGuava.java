package com.learn.concurrency.immuatable;

import com.google.common.collect.ImmutableMap;
import com.learn.concurrency.annotation.ThreadSafe;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ThreadSafe
public class ImmutableByGuava {

//    private final static ImmutableList list = ImmutableList.of(1,2,3,4);
//    private final static List list2 = ImmutableList.of(1,2,3,4);

//    private final static ImmutableSet set = ImmutableSet.copyOf(list);

    private final static ImmutableMap<Integer, Integer> map = ImmutableMap.of(1,2,3,4);
    private final static ImmutableMap<Integer, Integer> map2 = ImmutableMap.<Integer,Integer>builder().put(1,2).build();

    public static void main(String[] args) {
        //Exception in thread "main" java.lang.UnsupportedOperationException
//        list.add(43);
//        set.add(43);

        //使用List类型也可以，但是不会表明此方法已被废弃
//        list2.add(43);

        //Exception in thread "main" java.lang.UnsupportedOperationException
        map.put(9,9);
        map2.put(10,10);
    }
}
