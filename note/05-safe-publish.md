# 安全发布对象
## 概念
* 发布对象 : 使一个对象能够被当前范围之外的代码所引用
* 对象逸出 : 一种错误的发布对象的表现。当一个对象还没有构造完成时，就使发布线程意外以外的线程
都可以看到过期的值；线程看到的被发布的对象的引用是最新的，然而被发布的对象的状态却是过期的，
如果一个对象是可变对象，那么它就要被安全发布才可以
## 对象逸出
### 1. 显式引用逸出
见EscapeDemo案例
### 2. 隐式地使this引用逸出
```java
public class ThisEscape{
    private int count;
    public ThisEscape(EventSource source){
        source.registerListener(
            new EventListener(){
                public void onEvent(Event e){
                    doSomething(e);
                }
            });
        //在这里count初始化为100
        count = 100;
    }
    
    private doSomething(Event e){
        ThisEscape.this.count++;
    }
}
```
这里是需要发布内部类实例，而这样的做法导致当ThisEscape发布EventListener时，
也隐含地发布了ThisEscape实例本身。因为在这个内部类的实例中包含了对ThisEscape实例的隐含引用。
如上，我们知道this逸出会导致ThisEscape也发布出去，也就是ThisEscape还没有构建完成就发布出去，
也就是count=1;这一句还没执行就发布了ThisEscape对象，如果要使用count时，
很有可能会出现对象不一致的状态。
> 无法用代码实现上述隐式使this逸出的例子，例子也许是错的
## 安全发布对象 - 单例模式
避免对象逸出的发布对象，就实现了安全发布对象

### 安全发布对象有以下四种方法：
1. 在静态初始化函数中初始化一个对象引用
   * 参考单例案例
2. 将对象的引用保存到volatile类型域或者AtomicReference对象中
   * 参考单例案例
3. 将对象的引用保存到某个正确构造的final类型域中
4. 将对象的引用保存到一个由锁保护的域中
   * 参考单例案例

### 使用工厂方法来防止this引用在构造函数过程中逸出

```java
public class SafeListener{
    private final EventListener listener;

    private SafeListener(){
        listener = new EventListener(){
            public void onEvent(Event e){
                dosomething(e);
            }
        };
    }

    public static SafeListener newInstance(EventSource source){
        // 1. 完成实例对象的构造
        SafeListener safe = new SafeListener();
        // 2. 
        source.registerListener(safe.listener);
        return safe;
    }
}
```
如此，便能保证在对象为构造完成之前，是不会发布该对象。






参考: https://www.jianshu.com/p/a3fc770d11b9