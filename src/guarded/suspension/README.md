# Guarded Suspension 模式

## 0. 描述了什么事？
在没有满足守护条件的情况下，不允许继续执行程序。

## 1. 类的说明，怎么抽象？
|类名|说明|
|---|---|
|`Main`|启动类|
|`Request`|对请求的抽象类|
|`RequestQueue`|请求队列，其中有守护条件以及同步方法|
|`ClientThread`|模拟客户端请求的线程|
|`ServerThread`|模拟服务端处理的线程|

## 2. 示例程序

### 1. Main.java
```java
package guaraded.suspension;

public class Main {
    public static void main(String[] args) {
        RequestQueue requestQueue = new RequestQueue();
        new ClientThread(requestQueue, "ClientThread", 31415926L).start();
        new ServerThread(requestQueue, 926535L, "ServerThread").start();
    }
}
```

### 2. Request.java
```java
package guaraded.suspension;

public class Request {
    private final String name;

    public Request(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "[ Request: " + name + " ]";
    }
}
```

### 3. RequestQueue.java
```java
package guaraded.suspension;

import guarded.suspension.Request;import java.util.LinkedList;
import java.util.Queue;

public class RequestQueue {
    private final Queue<Request> queue = new LinkedList<>();

    public synchronized Request getRequest() {
        while (queue.peek() == null) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return queue.remove();
    }

    public synchronized void putRequest(Request request) {
        queue.offer(request);
//        notifyAll();
        notify();
    }
}
```

### 4. ClientThread.java
```java
package guaraded.suspension;

import guarded.suspension.Request;import guarded.suspension.RequestQueue;import java.util.Random;

public class ClientThread extends Thread {
    private final Random random;
    private final RequestQueue queue;

    public ClientThread(RequestQueue queue, String name, long seed) {
        super(name);
        this.random = new Random(seed);
        this.queue = queue;
    }

    @Override
    public void run() {
        for (int i = 0; i < 1000; i++) {
            Request request = new Request("No. " + i);
            System.out.println(Thread.currentThread().getName() + " requests " + request);
            queue.putRequest(request);
            try {
                Thread.sleep(random.nextInt(1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
```

### 5. ServerThread.java
```java
package guaraded.suspension;

import guarded.suspension.Request;import guarded.suspension.RequestQueue;import java.util.Random;

public class ServerThread extends Thread {
    private final RequestQueue requestQueue;
    private final Random random;

    public ServerThread(RequestQueue requestQueue, long seed, String name) {
        super(name);
        this.random = new Random(seed);
        this.requestQueue = requestQueue;
    }

    @Override
    public void run() {
        for (int i = 0; i < 1000; i++) {
            Request request = requestQueue.getRequest();
            System.out.println(Thread.currentThread().getName() + " handles " + request);
            try {
                Thread.sleep(random.nextInt(1000));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
```