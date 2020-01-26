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


# 线程安全策略
## 不可变对象
不可变对象只要发布了就是安全的。
### 不可变对象需要满足的条件
* 对象创建以后其状态就不能修改
* 对象所有域都是final类型
* 对象是正确创建的（在对象创建期间，this引用没有逸出）
### 手段 - 参考String类型
* 将类声明为final，不能被继承
* 将所有成员设置为私有的，不允许直接访问成员
* 对变量不提供set方法
* 将所有可变的成员声明为final，这样只能为他们赋值一次
* 通过构造器初始化所有成员
* 进行深度拷贝
* 在get方法中不直接返回对象的本身，而是克隆对象

### final关键字 : 类、方法、变量
* 修饰类 : 不能被继承
  直接看String的实现
* 修饰方法 : 
    1. 锁定方法不被继承类修改含义； 
    2. 效率
    > 在早期的java版本中会将final方法转为内嵌调用，但是如果方法过于庞大，可能看不到
    内嵌调用带来的任何性能提升。在最近的java版本中不需要再使用final方法进行这些优化了。
    因此只有在想明确禁止该方法被子类覆盖的情况下，才将方法设置为final
    
    注意 : 一个类的private方法会被隐式的指定为final方法。
* 修饰变量 : 基本数据类型变量、引用类型变量
  - 修饰的如果是基本类型变量，那么变量一旦在初始化之后就不能再修改了。
    * 重新赋值IDE编译时就出错
  - 如果是引用类型的变量，则在对其初始化之后便不能让它再指向另外一个对象。

### 其他创建不可变对象的方法
* 使用Collections以unmodifiableXXX : Collection、List、Set、Map...
* Guava : ImmutableXXX : Collection、List、Set、Map...
  - 提供了带初始化数据的初始化方法，因此只需要调用初始化方法，初始化完成就不允许修改了





参考: https://www.jianshu.com/p/a3fc770d11b9