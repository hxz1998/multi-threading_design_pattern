# Thread-Per-Message 模式

## 0. 描述了什么事？
为每一个请求都分配一个线程。

## 1. 类的说明，怎么抽象？
|类名|说明|
|`Main`|启动类|
|`Host`|针对请求，来创建线程的类|
|`Helper`|实际的操作在这个实例中完成|

## 2. 程序示例

### 1. Main.java
```java
package thread.per.message;

public class Main {
    public static void main(String[] args) {
        System.out.println("main BEGIN");
        Host host = new Host();
        host.request(10, 'A');
        host.request(20, 'B');
        host.request(30, 'C');
        System.out.println("main END");
    }
}
```

### 2. Host.java
```java
package thread.per.message;

public class Host {
    private final Helper helper = new Helper();

    public void request(int count, char c) {
        System.out.println("\t request(" + count + ", " + c + ")BEGIN");
        // 匿名内部类，创建并启动线程，并且调用 helper 的处理方法
        new Thread() {
            @Override
            public void run() {
                helper.handle(c, count);
            }
        }.start();
        System.out.println("\t request(" + count + ", " + c + ")END");
    }
}
```

### 3. Helper.java
```java
package thread.per.message;

public class Helper {
    public void handle(char c, int count) {
        System.out.println("\t\t handle(" + count + ", " + c + ") BEGIN");
        // 进行一些输出操作吧~
        for (int i = 0; i < count; i++) {
            System.out.print(c + " ");
            slowly();
        }
        System.out.println("\t\t handle(" + count + ", " + c + ") END");
    }

    private void slowly() {
        // 用来模拟耗时的操作
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```