# Thread-Specific Storage 模式

## 0. 描述了什么事？
在内部为每一个线程分配专有的存储空间。

## 1. 类的说明，怎么抽象？
|类名|说明|
|---|---|
|`Main`|启动类|
|`Log`|创建日志的类（负责分配各个线程的专有空间）|
|`ThreadSpecificLog`|负责写入文件的日志抽象类|
|`java.lang.ThreadLocal`|实际上负责管理线程专有空间的类|
|`ClientThread`|调用 `Log` 的线程类|

## 2. 程序示例

### 1. Main.java
```java
package thread.specific.storage;

public class Main {
    public static void main(String[] args) {
        new ClientThread("Alice").start();
        new ClientThread("Bob").start();
        new ClientThread("Chris").start();
    }
}
```

### 2. Log.java
```java
package thread.specific.storage;

public class Log {
    private static final ThreadLocal<ThreadSpecificLog> logCollection = new ThreadLocal<>();

    // 打印日志
    public static void println(String content) {
        getThreadSpecificLog().println(content);
    }

    public static void close() {
        getThreadSpecificLog().close();
    }

    private static ThreadSpecificLog getThreadSpecificLog() {
        ThreadSpecificLog threadSpecificLog = logCollection.get();
        if (threadSpecificLog == null) {
            threadSpecificLog = new ThreadSpecificLog(Thread.currentThread().getName() + "-log.txt");
            logCollection.set(threadSpecificLog);
        }
        return threadSpecificLog;
    }
}
```

### 3. ThreadSpecificLog.java
```java
package thread.specific.storage;

import java.io.IOException;
import java.io.PrintWriter;

public class ThreadSpecificLog {
    private PrintWriter writer;
    public ThreadSpecificLog (String filename) {
        try {
            writer = new PrintWriter(filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void println(String content) {
        writer.println(content);
    }

    public void close() {
        writer.print("====== End of Log ======");
        writer.close();
    }
}
```

### 4. ClientThread.java
```java
package thread.specific.storage;

public class ClientThread extends Thread {
    public ClientThread(String name) {
        super(name);
    }

    @Override
    public void run() {
        System.out.println(getName() + " BEGIN");
        for (int i = 0; i < 10; i++) {
            Log.println("i = " + i);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        }
        Log.close();
        System.out.println(getName() + " END!");
    }
}
```