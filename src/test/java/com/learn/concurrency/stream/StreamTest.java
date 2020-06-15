package com.learn.concurrency.stream;

import com.learn.concurrency.annotation.NotThreadSafe;
import com.learn.concurrency.annotation.ThreadSafe;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.util.Lists;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
public class StreamTest {

    /**
     * 使用系统属性只能全局设置一个并行度，无法做到细粒度控制每个方法的并行度。
     */
    @Test
    public void testGlobalParallelismSetting() {
        log.info("当前 CPU 核心数(超线程)：{}", Runtime.getRuntime().availableProcessors());
        // final 属性，运行时只能设置一次
        // 由于 main 线程也算一个工作线程，所以实际是由 main 线程和一个 ForkJoinPool.commonPool-worker-1 线程执行任务。
        System.setProperty("java.util.concurrent.ForkJoinPool.common.parallelism", "2");
        log.info("当前的并行度为 ：{}",System.getProperty("java.util.concurrent.ForkJoinPool.common.parallelism"));
        IntStream range = IntStream.range(1, 100000);
        range.parallel().forEach(num-> log.info("{}",num));
    }

    /**
     * 显示使用 ForkJoinPool 可以细粒度指定并行度
     *
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Test
    public void testForkJoinPool() throws ExecutionException, InterruptedException {
        IntStream range = IntStream.range(1, 100000);
        // 显示使用 ForkJoinPool 时，main 线程不再参与工作，工作线程为 ForkJoinPool-1-worker-1
        new ForkJoinPool(1)
                .submit(()-> range.parallel().forEach(num->{
                    log.info("{}",num);
                }))
                .get();
    }


    @NotThreadSafe
    @Test(expected = ArrayIndexOutOfBoundsException.class)
    public void testNotThreadSafe(){
        List<Integer> values = new ArrayList<>();
        IntStream.range(1, 10000).parallel().forEach(values::add);
    }
}
