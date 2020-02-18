package com.learn.concurrency.aqs;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

/**
 * 自定义不可重入独占锁
 *
 * @author kevin
 */
public class Mutex implements Lock, java.io.Serializable{

    class Sync extends AbstractQueuedSynchronizer{

        // 判断是否锁定状态
        @Override
        protected boolean isHeldExclusively() {
            return getState() == 1;
        }

        @Override
        protected boolean tryAcquire(int acquires) {
            assert acquires == 1;
            if(compareAndSetState(0, 1)){
                setExclusiveOwnerThread(Thread.currentThread());
                return true;
            }
            return false;
        }

        @Override
        protected boolean tryRelease(int acquires) {
            assert acquires == 1;
            if(getState() == 0){
                throw new IllegalStateException("当前线程未持有锁！");
            }
            if(compareAndSetState(1, 0)){
                setExclusiveOwnerThread(null);
                return true;
            }
            return false;
        }
    }

    private final Sync sync = new Sync();

    @Override
    public void lock() {
        sync.acquire(1);
    }

    @Override
    public void lockInterruptibly() throws InterruptedException {
        sync.acquireInterruptibly(1);
    }

    @Override
    public boolean tryLock() {
        return sync.tryAcquire(1);
    }

    @Override
    public boolean tryLock(long timeout, TimeUnit unit) throws InterruptedException {
        return sync.tryAcquireNanos(1, unit.toNanos(timeout));
    }

    @Override
    public void unlock() {
        sync.release(1);
    }

    @Override
    public Condition newCondition() {
        throw new UnsupportedOperationException();
    }

    public boolean isLock(){
        return sync.isHeldExclusively();
    }
}
