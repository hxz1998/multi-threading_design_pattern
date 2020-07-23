# Two-Phase Termination 模式

## 0. 描述了什么事？
终止一个线程时，需要先请求，再终止（温柔一点）

## 1. 类的说明，怎么抽象？
|类名|说明|
|---|---|
|`Main`|启动类|
|`CountUpThread`|计数线程，负责计数，提供一个终止线程的 `shutdownRequest()` 方法|

## 2. 程序示例

### 1. Main.java
```java
package two.phase.termination;

public class Main {
    public static void main(String[] args) {
        System.out.println("Main BEGIN : ");
        try {
            CountUpThread t = new CountUpThread();
            t.start();                                      // 启动计数线程
            Thread.sleep(10000);
            System.out.println("Main shutdownRequest...");
            t.shutdownRequest();                            // 中断计数线程

            System.out.println("Main Join...");
            t.join();                                       // 并入主线程，让主线程等待它的结束，然后大家一起结束
        } catch (InterruptedException e) {
        }
        System.out.println("Main End");
    }
}
```

### 2. CountUpThread.java
```java
package two.phase.termination;

public class CountUpThread extends Thread {
    // 用来判断是否开始了终止
    private volatile boolean shutdownRequested = false;

    // 用于计数
    private long count = 0;

    // 中断任务请求方法
    public void shutdownRequest() {
        shutdownRequested = true;
        interrupt();
    }

    // 是否终止线程
    public boolean isShutdownRequested() {
        return shutdownRequested;
    }

    @Override
    public final void run() {
        try {
            while (!shutdownRequested) {
                doWork();
            }
        } catch (InterruptedException e) {
        } finally {
            // 如果发生了异常，一定要完成清尾工作
            doShutdown();
        }
    }

    private void doWork() throws InterruptedException {
        count++;
        System.out.println("Working... count = " + count);
        Thread.sleep(500);
    }

    private void doShutdown() {
        System.out.println("Shutdown... count = " + count);
    }
}
```