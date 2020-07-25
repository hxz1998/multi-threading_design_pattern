# Active Object 模式

## 0. 描述了什么事？
该章节为前几章节的综合运用。

## 1. 程序示例

### 1. Main.java
```java
package active.object;

public class Main {
    public static void main(String[] args) {
        ActiveObject activeObject = ActiveObjectFactory.createActiveObject();
        new MakerClientThread("Alice", activeObject).start();
        new MakerClientThread("Bob", activeObject).start();
        new DisplayClientThread("Chris", activeObject).start();
    }
}
```

### 2. ActivationQueue.java
```java
package active.object;

public class ActivationQueue {
    private static final int MAX_COUNT = 100;
    private final MethodRequest[] requests = new MethodRequest[MAX_COUNT];
    private int tail, head, count;

    public ActivationQueue() {
        tail = 0;
        head = 0;
        count = 0;
    }

    public synchronized void putRequest(MethodRequest request) {
        while (count >= MAX_COUNT) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        requests[tail] = request;
        tail = (tail + 1) % MAX_COUNT;
        count++;
        notifyAll();
    }

    public synchronized MethodRequest takeRequest() {
        while (count <= 0) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        MethodRequest request = requests[head];
        head = (head + 1) % MAX_COUNT;
        count--;
        notifyAll();
        return request;
    }
}
```

### 3. ActiveObject.java
```java
package active.object;

public interface ActiveObject {
    Result<String> makeString(int count, char fillchar);
    void displayString(String content);
}
```

### 4. ActiveObjectFactory.java
```java
package active.object;

public class ActiveObjectFactory {
    public static ActiveObject createActiveObject() {
        Servant servant = new Servant();
        ActivationQueue queue = new ActivationQueue();
        SchedulerThread scheduler = new SchedulerThread(queue);
        Proxy proxy = new Proxy(scheduler, servant);
        scheduler.start();
        return proxy;
    }
}
```

### 5. DisplayClientThread.java
```java
package active.object;

public class DisplayClientThread extends Thread {
    private final ActiveObject activeObject;

    public DisplayClientThread(String name, ActiveObject activeObject) {
        super(name);
        this.activeObject = activeObject;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; true; i++) {
                // 没有返回值的调用
                String string = Thread.currentThread().getName() + " " + i;
                activeObject.displayString(string);
                Thread.sleep(200);
            }
        } catch (InterruptedException e) {

        }
    }
}
```

### 6. DisplayStringRequest.java
```java
package active.object;

public class DisplayStringRequest<T> extends MethodRequest<Object> {

    private final String string;

    protected DisplayStringRequest(Servant servant, String string) {
        super(servant, null);
        this.string = string;
    }

    @Override
    public void execute() {
        servant.displayString(string);
    }
}
```

### 7. FutureResult.java
```java
package active.object;

public class FutureResult<T> extends Result<T> {
    private Result<T> result;
    private boolean ready;

    public synchronized void setResult(Result<T> result) {
        if (ready) return;
        this.result = result;
        ready = true;
        notifyAll();
    }

    @Override
    public synchronized T getResultValue() {
        while (!ready) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return result.getResultValue();
    }
}
```

### 8. MakerClientThread.java
```java
package active.object;

public class MakerClientThread extends Thread {
    private final ActiveObject activeObject;
    private final char fillchar;

    public MakerClientThread(String name, ActiveObject activeObject) {
        super(name);
        this.activeObject = activeObject;
        this.fillchar = name.charAt(0);
    }

    @Override
    public void run() {
        try {
            for (int i = 0; true; i++) {
                // 有返回值的调用
                Result<String> result = activeObject.makeString(i, fillchar);
                Thread.sleep(10);
                String value = result.getResultValue();
                System.out.println(Thread.currentThread().getName() + " : value = " + value);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```

### 9. MakeStringRequest.java
```java
package active.object;

public class MakeStringRequest<T> extends MethodRequest<String> {

    private final int count;
    private final char fillchar;

    protected MakeStringRequest(Servant servant, FutureResult<String> futureResult, int count, char fillchar) {
        super(servant, futureResult);
        this.count = count;
        this.fillchar = fillchar;
    }

    @Override
    public void execute() {
        Result<String> result = servant.makeString(count, fillchar);
        futureResult.setResult(result);
    }
}
```

### 10. MethodRequest.java
```java
package active.object;

public abstract class MethodRequest<T> {
    protected final Servant servant;
    protected final FutureResult<T> futureResult;

    protected MethodRequest(Servant servant, FutureResult<T> futureResult) {
        this.futureResult = futureResult;
        this.servant = servant;
    }

    public abstract void execute();
}
```

### 11. Proxy.java
```java
package active.object;

public class Proxy implements ActiveObject {
    private final SchedulerThread scheduler;
    private final Servant servant;

    public Proxy(SchedulerThread scheduler, Servant servant) {
        this.scheduler = scheduler;
        this.servant = servant;
    }

    public Result<String> makeString(int count, char fillchar) {
        FutureResult<String> future = new FutureResult<>();
        scheduler.invoke(new MakeStringRequest(servant, future, count, fillchar));
        return future;
    }

    public void displayString(String string) {
        scheduler.invoke(new DisplayStringRequest(servant, string));
    }
}
```

### 12. RealResult.java
```java
package active.object;

public class RealResult<T> extends Result<T> {
    private final T resultValue;

    public RealResult(T resultValue) {
        this.resultValue = resultValue;
    }

    @Override
    public T getResultValue() {
        return this.resultValue;
    }
}
```

### 13. Result.java
```java
package active.object;

public abstract class Result<T> {
    public abstract T getResultValue();
}
```

### 14. SchedulerThread.java
```java
package active.object;

public class SchedulerThread extends Thread {
    private final ActivationQueue queue;

    public SchedulerThread(ActivationQueue queue) {
        this.queue = queue;
    }

    public void invoke(MethodRequest request) {
        queue.putRequest(request);
    }

    public void run() {
        while (true) {
            MethodRequest request = queue.takeRequest();
            request.execute();
        }
    }
}
```

### 15 Servant.java
```java
package active.object;

public class Servant implements ActiveObject {

    @Override
    public Result<String> makeString(int count, char fillchar) {
        char[] buffer = new char[count];
        for (int i = 0; i < count; i++) {
            buffer[i] = fillchar;
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return new RealResult<>(new String(buffer));
    }

    @Override
    public void displayString(String content) {
        try {
            System.out.println("Display String : " + content);
            Thread.sleep(10);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```