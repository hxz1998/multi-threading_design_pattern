# Work Thread 模式

## 0. 描述了什么事？
工作没来就一直等，来了就忙活。

## 1. 类的说明，怎么抽象？
|类名|说明|
|---|---|
|`Main`|启动类|
|`Request`|请求抽象类，有一个固定方法叫 `execute` |
|`Channel`|拥有一个 `WorkerThread` 线程池，负责接收 `Request` 并调度 `WorkerThread` 完成任务|
|`ClientThread`|不断产生 `Request` 请求，然后交给 `Channel` 管理|
|`WorkerThread`|工人线程，负责从 `Channel` 中取出来 `Request` 并且执行其 `execute` 方法|

## 2. 程序示例

### 1. Main.java
```java
package worker.thread;

public class Main {
    public static void main(String[] args) {
        Channel channel = new Channel(5);
        channel.startWorkers();
        new ClientThread("Alice", channel).start();
        new ClientThread("Bob", channel).start();
        new ClientThread("Chris", channel).start();
    }
}
```

### 2. Request.java
```java
package worker.thread;

import java.util.Random;

public class Request {
    private final String name;
    private final int num;
    private static final Random random = new Random();

    public Request(String name, int num) {
        this.name = name;
        this.num = num;
    }

    public void execute() {
        System.out.println(Thread.currentThread().getName() + " execute " + this);
        try {
            Thread.sleep(random.nextInt(1000));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "[ Request from " + name + " No. " + num + " ]";
    }
}
```

### 3. Channel.java
```java
package worker.thread;

public class Channel {
    private static final int MAX_REQUEST = 100; // 请求队列容量
    private final Request[] requestQueue;       // 请求队列
    private int tail;       // 尾部指针，用来添加元素
    private int head;       // 头部指针，用来取出元素
    private int count;      // 记录当前队列有的请求数量

    private final WorkerThread[] threadPool;    // 工人线程池

    public Channel(int threads) {
        this.threadPool = new WorkerThread[threads];
        for (int i = 0; i < threads; i++)
            threadPool[i] = new WorkerThread("Worker-" + i, this);
        requestQueue = new Request[MAX_REQUEST];
        this.tail = 0;
        this.head = 0;
        this.count = 0;
    }

    public void startWorkers() {
        // 启动方法，把所有的工人线程都启动了
        for (WorkerThread worker : threadPool) {
            worker.start();
        }
    }

    public synchronized void putRequest(Request request) {
        try {
            while (count >= MAX_REQUEST)    // 如果队列已经满了，那就在这里一直等着吧
                wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        requestQueue[tail] = request;
        tail = (tail + 1) % MAX_REQUEST;
        count++;
        notifyAll();
    }

    public synchronized Request getRequest() {
        try {
            while (count <= 0)              // 如果队列是空的，那就等着放进来请求之后再处理吧
                wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Request request = requestQueue[head];
        head = (head + 1) % MAX_REQUEST;
        count--;
        notifyAll();
        return request;
    }
}
```

### 4. ClientThread.java
```java
package worker.thread;

import java.util.Random;

public class ClientThread extends Thread {
    private final Channel channel;
    private static final Random random = new Random();

    public ClientThread(String name, Channel channel) {
        super(name);
        this.channel = channel;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; true; i++) {
                Request request = new Request(getName(), i);
                channel.putRequest(request);
                Thread.sleep(random.nextInt(1000));
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```

### 5. WorkerThread.java
```java
package worker.thread;

public class WorkerThread extends Thread {
    private final Channel channel;

    public WorkerThread(String name, Channel channel) {
        super(name);
        this.channel = channel;
    }

    @Override
    public void run() {
        // 对于 Worker 来说，只知道 Request 有一个 execute 方法而已
        while (true) {
            Request request = channel.getRequest();
            request.execute();
        }
    }
}
```
