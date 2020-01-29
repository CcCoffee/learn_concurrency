## AQS介绍
AQS是JUC的核心。而JUC大大提高了JAVA并发性能。

* 底层数据结构是一个FIFO队列。
* 使用Node实现FIFO队列，可以用于构建锁或者其他同步装置的基础框架。
* 利用了一个int类型表示状态。
* 使用方法是继承：子类通过继承并通过实现它的方法管理其状态。

可以同时实现排他锁和共享锁模式。在使用者的角度, AQS 的功能分为两类, 独占功能 和共享功能. 
它的所有子类中要么实现并使用了它的独占功能API , 要么使用了共享锁的功能 , 不会同时使用两套API
> 它底层使用的是双向列表 ,是队列的一种实现 , 因此也可以将它当成一种队列 . 其中 Sync queue 
是同步列表 ,它是双向列表 , 包括 head ,tail 节点. 其中head 节点主要用来后续的调度 ; 
Condition queue 是单向链表 , 不是必须的 , 只有当程序中需要Condition 的时候 ,才会存在这个
单向链表 , 并且可能会有多个 Condition queue
## 实现思路
AbstractQueuedSynchronizer内部维护了一个 CLH队列来管理锁 , 线程首先会尝试获取锁 , 如果失败, 
就将当前线程及等待状态等信息包成一个NODE 节点 加入到 同步队列 (Sync queue)里 , 接着会不断循环
尝试获取锁, 它的条件是当前节点为head 的直接后继才会尝试 , 如果失败就会阻塞自己, 直到自己被唤醒,
而当持有锁的线程释放锁的时候会唤醒队列中的后继线程。

基于这些基础的设计和思路，JDK提供了许多基于AQS的子类，如CountDownLatch、Semaphore

## AQS同步组件
* CountDownLatch
* Semaphore
* CyclicBarrier
* ReentrantLock
* Condition
* FutureTask
