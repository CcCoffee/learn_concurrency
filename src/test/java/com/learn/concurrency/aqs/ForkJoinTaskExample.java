package com.learn.concurrency.aqs;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * 因为需要自己写算法，所以较少使用
 */
@Slf4j
public class ForkJoinTaskExample extends RecursiveTask<Integer> {

    private int start;
    private int end;

    public ForkJoinTaskExample(int start, int end) {
        this.start = start;
        this.end = end;
    }

    @Override
    protected Integer compute() {
        int sum = 0;
        int threshold = 2;
        boolean canCompute = (end - start) <= threshold;
        if (canCompute) {
            for (int i = start; i <= end; i++) {
                sum += i;
            }
        } else {
            int middle = (start + end)/2;
            ForkJoinTaskExample leftTask = new ForkJoinTaskExample(start, middle);
            ForkJoinTaskExample rightTask = new ForkJoinTaskExample(middle+1, end);
            leftTask.fork();
            rightTask.fork();

            Integer leftSum = leftTask.join();
            Integer rightSum = rightTask.join();
            sum = leftSum + rightSum;
        }
        return sum;
    }

    public static void main(String[] args) throws ExecutionException, InterruptedException {
        ForkJoinTaskExample task = new ForkJoinTaskExample(1, 100);
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        Future<Integer> result = forkJoinPool.submit(task);
        log.info("result = {}", result.get());
    }
}
