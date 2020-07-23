# 多线程设计模式学习

> 参考书目[《图解Java多线程设计模式》](https://book.douban.com/subject/27116724/) [日] 结城浩 著

> 源程序：[https://github.com/hxz1998/multi-threading_design_pattern](https://github.com/hxz1998/multi-threading_design_pattern)

## 0. 关于UML

1. 类图
    1. 继承：实线 + 空心箭头 `-▷`
        1. 静态字段或者方法使用下划线表示
        2. 抽象类或者方法使用斜体
    2. 接口：虚线 + 空心箭头 `··▷`
    3. 聚合：菱形 + 实线 + 箭头 `◇->`
    4. 关联： 实线 + 箭头 `->`

2. 可见性
    1. `-` 表示私有 `private` 字段或者方法
    2. `#` 表示保护 `protected` 字段或者方法
    3. `+` 表示公有 `public` 字段或者方法
    
## 序章 1 Java线程

**Tips:** 
> `java.nio` 中包含兼具性能和可扩展性的 `I/O` 处理。即使不用线程，也可以很好地处理 `I/O` 请求。

1. 顺序、并行和并发：**顺序（Sequential）** 执行就是多个操作 **依次处理** ，比如十个操作交给一个人来做；
**并行（Parallel）** 表示多个操作 **同时处理** ，比如十个操作交给两个人来做；
**并发（Concurrent）** 表示将一个操作 **分割成多个部分，而且允许无序处理**。 

2. `synchronized` 方法：方法在声明时在前面加上 `synchronized` ，那么这个方法在同一个时间上，只能够被一个线程所放问。也被称为同步方法。
例如：

    ```java
    public synchronized void write(String message) {}
    ```
    这时，每一个实例都有一个锁，而不是该类的所有实例共用一把锁。可以使用 `assert Thread.holdsLock(obj);` 来判断当前线程是否已获取某一个对象 `obj` 的锁。
    
    当然，也可以使用 `synchronized` 来修饰代码块，例如：
    
    ```java
    synchronized (this) { 
        // 代码块 
    }
    ```
    
    此外，静态方法的锁和实例方法锁是不同的。一个是 `synchronized(this)` 一个是 `synchronized(Something.class)` 。

3. `wait` 、 `notify` 、 `notifyAll` 三个方法，`wait` 方法将当前线程 **（要求获得了对象锁）** 放到了当前实例的 **等待队列** 中，并且释放 **对象锁** 。
同样的， `notify` 和 `notifyAll` 也需要持有对象锁才可以（这是规则）。

**Tips:**
> 在一般情况下，要使用 `notifyAll` ，因为该方法更加稳妥。
    
## 序章 2 多线程程序的评价标准

|指标|含义|目标|
|---|---|---|
|**安全性**|不损坏对象|必要条件|
|**生存性**|必要的处理能够被执行|必要条件|
|性能|能被大批量、快速地执行|提高质量|
|可复用性|类可被重复使用|提高质量|

## 第一章 Single Threaded Execution 模式

1. 多线程设计难点所在
    1. 没检查出错误，也不能说明程序安全。测试次数、时间点都有可能导致错误无法重现。
    2. 操作测试只不过是增加“程序也许安全”的概率。
    3. 调试程序本身就不是线程安全的。
    
2. Single Threaded Execution 出现的角色
    1. `Shared Resource`（共享资源）：需要被多个线程所访问的类，对于其中的不安全的方法 `unsafeMethod` ，需要使用 `synchronized` 来保护。
    2. 只允许单个线程执行的程序部分称为 **临界区** 。
    
3. 什么时候使用？
    1. 多个线程放问同一个资源时，单线程中不需要考虑这些问题，就好比一个人独居在家，即便不锁厕所门，也不用担心有人突然闯进来。
    2. 状态有可能发生变化时，比如一个类的成员变量在创建后会被修改。
    3. 明确需要确保安全时。
    
4. **死锁** 满足以下条件时，就有可能会发生死锁
    1. 存在多个 `SharedResource` 角色。
    2. 线程持有某个 `SharedResource` 的锁同时想去获取另外一个 `SharedResource` 的锁。
    3. 获取 `SharedResource` 锁的步骤不固定。
    
5. 一般情况下，使用 `Single Threaded Execution` 模式会降低程序性能。因为：
    1. 频繁获取 `synchronized` 锁，这个步骤非常耗时。适当添加 `synchronized` 关键字出现的频率可以抑制效率的降低。
    2. 线程冲突会引起等待。尽可能缩小临界区的范围课以降低线程冲突的概率。
    
6. `Before/After` 处理模式：主要用于请求了资源后，必须要释放资源的场合下。例如：
    ```java
    void method() {
        lock();
        try {} finally {
           unlock(); // 不管怎样都要释放掉获取的资源
        }        
    }
    ```

## 第二章 Immutable 模式

1. 该模式下，存在可以确保 **实例状态不发生变化** 的类（Immutable）类。

2. 巧妙利用该模式，可以提高性能。

3. Immutable 出现的角色
    1. `Immutable`（不可变对象），该类中的字段，在创建完成以后，就不再改变。
    
3. 何时使用
    1. 实例创建后，状态不发生改变。
    2. 实例共享，而且频繁使用。
    
4. 集和类中的线程安全处理
    1. `ArrayList` 不是线程安全的，但是经过以下两种方法处理后，它可以变成安全的。
        ```
        List<Integer> list = new ArrayList<>();   // 一般的 ArrayList 是不安全的
        List<Integer> list = Collections.synchronizedList(new ArrayList<Integer>()); // 使用该方法后变成安全的
        List<Integer> list = new CopyOnWriteArrayList<>();  // 写时复制技术来避免冲突，该方法适用于频繁 读 操作，如果是频繁 写 ，那么就不合理
        ```
    2. 写时复制（copy-on-write）：对集和进行“写”操作时，内部会对确保安全的数组进行整体复制，这样就不用担心读取元素时内部元素被修改。
    
## 第三章 Guarded Suspension 模式

1. Guarded Suspension 模式通过 **让线程等待** 来保证实例的安全性。它类似于 **添加了条件** 的 `Single Thread Execution` 模式。
同时也是多线程版的 `if` 语句。
 
2. 在该模式中，线程之所以等待 `wait` ，是因为没有满足守护条件。也就是说该守护条件进行了保护，从而阻止了线程继续向前执行。

3. Guarded Suspension 模式中的角色
    1. `Guarded Object`（被守护对象）：持有被守护的方法，当守护条件成立，那么可以立即执行，否则就等待。守护条件的成立与否会随着 `Guarded Object` 对象的状态不同而发生改变。

4. 千万不可以忘记改变状态，否则会失去生存性。如果忘记改变守护条件的状态，那么不管多少次 `notifyAll/notify` 都不会继续执行。

5. `java.util.concurrent.LinkedBlockingQueue` 是一个线程安全的队列。其原理和该模式一样，保证线程安全。

## 第四章 Balking 模式

1. Balking 模式是通过 **直接返回函数调用** 来 **提高程序性能** 。它类似于前面的 Guarded Suspension 模式。

2. 在该模式中，对守护条件进行判断，如果发现不满足（没必要执行下面的操作或者不适合执行），那么就直接停止处理，直接返回。

3. Balking 模式中的角色
    1. Guarded Object （被守护的对象）：通过检查守护条件，来判断是否需要继续处理。

4. 何时使用
    1. 当后面的操作费时费力，不需要执行时
    2. 当守护条件仅在第一次成立时，例如实例的初始化操作

5. balk 的表现形式
    1. 忽略操作，例如 `return`
    2. 通过返回值来表示 `balk`
    3. 通过异常来表示操作
    
6. 超时操作： `wait()` 方法可以指定参数，例如 `obj.wait(1000)` ，即指定等待多久。

## 第五章 Producer-Consumer 模式

1. Producer-Consumer 模式完成的是：通过在两者之间建立 **“桥梁角色”** ，来安全的传送数据。
一般来说会有多个 `Producer` 也会有多个 `Consumer` ，当两者都只有一个时，也被称为 **Pipe** 模式。

2. Producer-Consumer 模式中的角色
    1. `Data` ：由 `Producer` 角色生成，由 `Consumer` 角色使用。
    2. `Producer`：负责生成 `Data` ，然后传送给 `Consumer`。
    3. `Consumer`：负责使用 `Data`。
    4. `Channel`：安全传输的通道，里面有线程安全的方法。会对 `Producer` 角色和 `Consumer` 角色的访问执行互斥处理。

3. `Producer` 和 `Consumer` 之间当然也可以直接传输数据，不通过 `Channel` ，不过这样一来，就成了简单的函数调用，
`Consumer` 对 `Data` 的处理耗时需要由 `Producer` 来承担。就好比餐厅大厨等着一位顾客吃完之后再去做下一锅。

4. **协调运行** 和 **互斥处理** 是内外统一的：
    * 线程的协调运行需要考虑 “放在中间的东西”
    * 线程的互斥处理需要考虑 ”应该保护的东西“
    
5. 当 `Consumer` 只有一个的时候，就不需要对 `Consumer` 的操作进行 `synchronized` 保护了。这样也可以提高程序性能。

6. `notify` 和 `interrupt` 方法
    * 相同点：
        1. 都可以让正在 `wait()` 的线程重新运行。
    * 不同点：
        1. `notify` 和 `notifyAll` 唤醒的是该实例等待队列中的线程，而不是指定的线程。 `notify/notifyAll` 唤醒线程后，会继续执行 `wait()` 后面的程序。 `notify/notifyAll` 要执行，必须获得实例的锁。
        2. `interrupt` 直接指定线程并且唤醒，当该线程处于 `wait()` 或者 `sleep()` 时，会抛出 `InterruptedException` 异常。但是不需要获得锁。
        
7. `isInterrupted` 、 `interrupt` 和 `Thread.interrupted` 方法
    
    |方法名|说明|
    |---|---|
    |`isInterrupted`|检查中断状态，`Thread` 类的实例方法，不会改变中断状态|
    |`interrupt`|改变中断状态，使得状态变为中断状态。并不会直接抛出异常|
    |`interrupted`|检查并且清除中断状态的方法|
    
8. 没事儿不要用 `stop()` ！！！

## 第六章 Read-Write Lock 模式

1. “读取”和“写入”从实例状态变化的角度来看，两者有本质区别。“读取”不会改变实例的状态，因此可以多个线程同时进行，而“写入”恰恰相反。

2. 把针对“写入”的互斥处理和针对“读取”的互斥处理分开来考虑时，可以一定程度提高程序性能。

3. `finally` 语句块中的内容无论如何都会被执行。

4. 读写冲突矩阵

||读取|写入|
|---|---|---|
|读取|无冲突|`read-write conflict`|
|写入|`read-write conflict`|`write-write conflict`|
    
5. Read-Write Lock 模式中的角色
    1. `SharedResource` ，使用 `ReadWriteLock` 来控制内部状态线程安全地改变
    2. `Reader` ，对 `SharedResource` 角色进行 `read` 操作
    3. `Writer` ，对 `SharedResource` 角色进行 `write` 操作 
    4. `ReadWriteLock` ，保证了两个基本操作 `read` 和 `write` 是线程安全的。
    
6. Read-Write Lock 模式利用了读取操作的线程之间不会引起冲突的特性，来提高性能。不过并不是绝对的提高，必须通过实际的测试来判断。

7. 什么时候用？
    1. 读取操作繁重时
    2. 读取频率比写入频率高时，反之写入会频繁打断读取操作，导致效率低下

8. **物理锁** 和 **逻辑锁** ：物理锁是由Java编程规范来产生的，Java程序并不能改变这种锁的运行。逻辑锁是开发人员自己实现的一种结构，可通过修改 `ReadWriteLock` 类来改变锁的运行。
`ReadWriteLock` 提供了 **用于读取的锁** 和 **用于写入的锁** 两个逻辑锁，但实际上只有一个 `ReadWriteLock` 实例所持有的物理锁。

9. 已有现成的工具包使用 `java.util.concurrent.locks`

## 第七章 Thread-Per-Message 模式

1. 为每个命令或请求都分配一个独立的线程，由这个线程来处理任务。

2. Thread-Per-Message 模式中的角色
    1. `Client` 委托人角色，该角色会向 `Host` 发出请求，但是并不晓得 `Host` 是如何完成该请求的。
    2. `Host` 该角色收到 `Client` 的请求 `request` ，并且创建新线程，新创建的线程会使用 `Helper` 角色来完成具体任务。
    3. `Helper` 小助手，为 `Host` 来提供实际的处理功能。

3. 何时使用？
    1. 当 `handle` 操作非常耗时，或者 `handle` 操作需要等待输入输出时。
    2. 平衡 **`handle` 操作花费的时间** 和 **线程启动花费的时间**。
    3. 应用于服务器，处理用户请求。
    
4. 可使用的接口
    1. `java.util.concurrent.ThreadFactory` 接口：将线程 **创建** 抽象化了
    2. `java.util.concurrent.Executor` 接口：将线程 **执行** 抽象化了
    3. `java.util.concurrent.ExecutorService` 接口：将 **被复用的线程** 抽象化了
    4. `java.util.concurrent.ScheduledExecutorService` 接口：将 **被调度的线程的执行** 抽象化了
    5. `java.util.concurrent.Executors` 接口：用于 **创建实例** 的工具类
    
## 第八章 Worker Thread 模式

1. 抽象出来“工人”这个概念，**工人线程（Worker Thread）** 会从 `Channel` 中取出来工作，并且完成后继续等待新的工作。

2. Worker Thread 模式中的角色
    1. `Client` 委托者，负责产生 `Request` ，并将其交给 `Channel` 角色
    2. `Channel` 通信线路，负责接收 `Request` 并且将其交给 `Worker` 角色完成
    3. `Worker` 工作者，完成 `Request` 中指定的方法
    4. `Request` 任务抽象

3. Worker Thread模式提高了系统的吞吐量，因为它可以连续重复使用同一个线程，省去了启动线程所用的时间。

4. 调用与执行的分离：`Client` 角色发出的 `Request` 相当于 **调用** 操作，而 `Worker` 角色相当于对请求的 **执行**，这样一来可以完成以下操作
    1. 提高响应速度：因为调用完之后，可以去继续执行其他处理，不需要等待执行结束
    2. 控制执行顺序：可以为 `Request` 来设置优先级，来控制顺序
    3. 取消和反复执行：即使调用了，只要没执行就可以取消，而且还可以保存 `Request` 来反复执行
    4. 分布式系统的基础：通过网络将 `Request` 传送 
    
## 第九章 Future 模式

1. 假设有一个方法需要花费很长时间，那么不如先拿一个 **“提货单”** ，完成其他处理后再来取结果。

2. Future 模式中的角色
    1. `Client` 请求者：负责向 `Host` 角色发出请求，并且可以立即接收到请求的处理结果（ `FutureData/VirtualData` ）
    2. `Host` 线程创建者：负责创建新的线程，并在新线程中完成 `RealData` 的获取，与此同时，会把 `FutureData` 返回给请求者
    3. `VirtualData` 虚拟数据：提货单和实际数据的接口抽象
    4. `FutureData` 提货单：能够根据该对象来获取到真实的数据
    5. `RealData` 实际数据：创建该对象或者进行处理需要花费很长时间

3. Future 模式实现了 **“准备返回值”** 和 **“使用返回值”** 的分离

4. Future 模式不让主线程久等，而且通常情况下，Future 角色只发生一次改变，不过也可能根据实际情况多次设置。

5. 如果想获取线程处理完之后的返回值，也可以使用 **回调（callback）** 的方式来实现，例如 `java.util.concurrent.Callable` 接口的使用。

## 第十章 Two-Phase Termination 模式

1. Two-Phase Termination 模式的要点：
    1. 安全地终止线程（安全性）
    2. 必定会进行终止处理（生存性）
    3. 发出终止请求后尽快进行终止处理（响应性）
    
2. Two-Phase Termination 模式中的角色
    1. `TerminationRequest` 终止请求发出者：负责发出终止请求
    2. `Terminator` 终止者：负责终止请求，提供了 `shutdownRequest()` 方法，而且呀，`shutdownRequest()` 方法不需要使用 `Single Threaded Execution` 模式.

3. 为了能够在接受到终止请求后立即开始终止处理，可以在执行长时间处理 **前** 检查 `shutdownRequested` 标志，或者调用 `isShutdownRequested` 方法。
     
        
        