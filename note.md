# 1. 并发测试
并发测试可以使用工具或者使用代码
## 使用工具并发测试
常见的测试工具有Postman, AB, jMeter
### Apache bench
`ab -n 1000 -c 50 http://localhost:8040/hello`
- 请求参数
    * -n : 请求次数
    * -c : 并发线程数
- 请求结果
    ```bash
    Document Path:          /hello
    Document Length:        11 bytes
    
    Concurrency Level:      50 # 并发量
    Time taken for tests:   0.186 seconds # 整个测试所用时间
    Complete requests:      1000 # 完成的请求数
    Failed requests:        0
    Total transferred:      144000 bytes # 所有请求的响应数据的长度总和(header + 正文)
    HTML transferred:       11000 bytes # 所有请求的响应数据的正文数据长度总和
    # 吞吐率，它与并发数相关，即使请求总数相同，但如果并发数不一样，吞吐率也有很大差异
    # Complete requests / Time taken for tests
    Requests per second:    5365.35 [#/sec] (mean)
    Time per request:       9.319 [ms] (mean) # 用户平均请求等待时间
    # 服务器平均请求等待时间
    Time per request:       0.186 [ms] (mean, across all concurrent requests)
    # 请求单位时间内从服务器获取的数据长度，Total transferred / Time taken for tests
    Transfer rate:          754.50 [Kbytes/sec] received
    
    Connection Times (ms)
                  min  mean[+/-sd] median   max
    Connect:        0    3   3.3      2      24
    Processing:     1    6   4.9      4      29
    Waiting:        1    5   4.0      4      25
    Total:          3    9   6.4      6      36
    
    Percentage of the requests served within a certain time (ms)
      50%      6
      66%      7
      75%      8
      80%     12
      90%     19
      95%     23
      98%     31
      99%     32
     100%     36 (longest request)
    ```
## 使用代码进行并发测试
### CountDownLatch
阻塞线程，并且在达到某种条件下恢复线程。
* 应用场景
  - 可用于限制请求总数，并在所有请求执行完后再进行其他操作。
### Semaphore
适合于控制同时并发的线程数。就像高速路上的通道数，通道越多能同时通过的🚗越多
* 应用场景
  - 可用于模拟同时请求的用户数
### CountDownLatch与Semaphore结合使用
在模拟并发测试的时候并在所有线程执行完输出一些结果，使用CountDownLatch与Semaphore结合起来使用。

# 2. 线程安全性
当多个线程访问某个类时，不管运行时环境采用**何种调度方式**或者这些进程将如何交替执行，
并且在主调代码中**不需要任何额外的同步或协同**，这个类都能表现出**正确的行为**，那么就称这个类时线程安全的
* 原子性 : 提供了**互斥访问**，同一时刻只能有一个线程来对它进行操作
* 可见性 : 一个线程对主内存的修改可以及时的被其他线程观察到
* 有序性 : 一个线程观察其他线程中的指令执行顺序，由于指令重排序的存在，该观察结果一般杂乱无序
## 原子性
### atomic包
#### AtomicInteger源码分析
AtomicInteger类中提供了incrementAndGet方法;
```java
class AtomicInteger{
    public final int incrementAndGet() {
        return unsafe.getAndAddInt(this, valueOffset, 1) + 1;
    }
}
```
incrementAndGet方法又调用了Unsafe类的getAndAddInt方法
```java
class Unsafe{
    public final int getAndAddInt(Object var1, long var2, int var4) {
        int var5;
        do {
            var5 = this.getIntVolatile(var1, var2);
            // 问题: 这里如果其他线程改变类var5的值不会发生并发问题吗？
            // 分析: 假设执行2+1案例，对象var1在工作内存中的var2为被加数2，而var4为加数1。
            // var5为var1对象在主内存中的值2。如果此时其他线程改变了主存中的var5变为3，
            // 在执行compareAndSwapInt方法时首先判断var2==var5，然后猜测应该还会判断var5
            // 是否被改变，如果有则会因为数据不一致返回false，从而再次进入此循环。
            // 结论: CAS保证了不会有并发问题
        } while(!this.compareAndSwapInt(var1, var2, var5, var5 + var4));
        return var5;
    }
}
```
* 参数：
    - Object var1：传进来的AtomicInteger对象
    - long var2：是传进来的值，当前要进行加一的值 (比如要进行2+1的操作, var2就是2)
    - int var4：是传进来的值，进行自增要加上的值 (比如要进行2+1的操作, var4就是1)
    - int var5:是通过调用底层的方法this.getIntVolatile(var1, var2);得到的底层当前的值
* 分析：
    ```bash
    while(!this.compareAndSwapInt(var1, var2, var5, var5 + var4))：
    通过do{} while()不停的将当前对象的传进来的值和底层的值进行比较,
    如果相同就将底层的值更新为：var5+var4(加一的操作),
    如果不相同,就重新再从底层取一次值,然后再进行比较，这就是CAS的核心。
    ```
* 帮助理解：把AtomicInteger里面存的值看成是工作内存中的值.
把底层的值看成是主内存中的值。在多线程中，工作内存中的值和主内存中的值会出现不一样的情况。

#### AtomicLong与LongAdder
* AtomicLong与AtomicInteger基本相同
  - 缺点 : 对于高并发场景会造成大量失败重新while循环,效率不高。使用LongAdder代替
* LongAdder : 在低并发时效率与AtomicLong基本相同，高并发时由于原理不同性能也很高
  - 缺点 : 数据可能有些偏差
  - 适用场景 : 网站访问计数等不需要太精确的业务 
