# Future 模式

## 0. 描述了什么事？
与其原地等待结果，不如先拿一个提货单，过一会儿再来康康

## 1. 类的说明，怎么抽象？
|类名|说明|
|---|---|
|`Main`|启动类|
|`Host`|接受请求，并且向请求方返回提货单 `FutureData` 的实例|
|`Data`|表示访问数据的方法的接口，由 `FutureData` 和 `RealData` 实现该接口|
|`FutureData`|提货单的抽象类，里面有 `RealData` 的实例，会创建额外的线程来完成处理|
|`RealData`|表示实际数据的类，构造函数会花费很长时间|

## 2. 程序示例

### 1. Main.java
```java
package future;

public class Main {
    public static void main(String[] args) {
        System.out.println("Main Begin");
        Host host = new Host();
        // 发出三个请求，并且拿到提货单
        Data data1 = host.request('A', 10);
        Data data2 = host.request('B', 20);
        Data data3 = host.request('C', 30);
        // 假装去干一会儿别的事
        System.out.println("去干别的事儿啦~");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("干别的事回来惹，康康之前的请求完成了咩");
        // 使用提货单，来获取真正的数据
        System.out.println("data1 = " + data1.getContent());
        System.out.println("data2 = " + data2.getContent());
        System.out.println("data3 = " + data3.getContent());
        System.out.println("Main End");
    }
}
```

### 2. Host.java
```java
package future;

public class Host {
    public Data request(char c, int count) {
        System.out.println("\t Request : " + count + " " + c + " BEGIN");
        // 先构造一个提货单
        FutureData futureData = new FutureData();
        // 创建新的线程，并且在线程里面完成获取真正的数据
        new Thread() {
            @Override
            public void run() {
                // 构造数据
                RealData realData = new RealData(c, count);
                // 获取数据
                futureData.setRealData(realData);
            }
        }.start();
        System.out.println("\t Request : " + count + " " + c + " END");
        return futureData;
    }
}
```

### 3. Data.java
```java
package future;

public interface Data {
    String getContent();
}
```

### 4. FutureData.java
```java
package future;

public class FutureData implements Data {
    private RealData realData;
    private boolean ready = false;

    // 设置真正的数据
    public synchronized void setRealData(RealData realData) {
        if (ready) return;          // 如果已经设置过了，那就直接返回
        this.realData = realData;
        ready = true;
        notifyAll();                // 唤醒其他等待获取数据的线程
    }

    @Override
    public synchronized String getContent() {
        while (!ready) {    // 如果没有准备好数据，那就在这里等
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        return realData.getContent();
    }
}
```

### 5. RealData.java
```java
package future;

public class RealData implements Data {
    private final String content;

    public RealData(char c, int count) {
        System.out.println("Making String...: " + count + " * " + c + " BEGIN...");
        char[] buffer = new char[count];
        for (int i = 0; i < count; i++) {
            buffer[i] = c;
            try {
                Thread.sleep(100);  // 模拟费时操作
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        content = String.valueOf(buffer);
        System.out.println("Making String : " + content + " END...");
    }

    @Override
    public String getContent() {
        return content;
    }
}
```