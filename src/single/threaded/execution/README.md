# Single Threaded Execution 模式

> 参考书目[《图解Java多线程设计模式》](https://book.douban.com/subject/27116724/) [日] 结城浩 著

## 0. 描述了什么事？

三个人（线程）通过一扇门（资源）。

## 1. 类的说明，如何抽象？

|类名|说明|
|---|---|
|`Main`|创建 `Gate` 类，并让三个 `UserThread` 不断去通过门，启动类|
|`Gate`|表示门的类。在人通过时会打印其名称和地址|
|`UserThread`|表示人的类。有 `name` 和 `address` 两个字段|

## 2. 程序示例

### 1. Main.java

```java
package single.threaded.execution;

public class Main {
    public static void main(String[] args) {
        System.out.println("Testing Gate, hit Ctrl + C to exit.");
        Gate gate = new Gate();
        new UserThread("Alice", "Australia", gate).start();
        new UserThread("Bob", "Brazil", gate).start();
        new UserThread("Chris", "China", gate).start();
    }
}
```

### 2. Gate.java

```java
package single.threaded.execution;

public class Gate {
    private int counter = 0;
    private String name = "Nobody";
    private String address = "Nowhere";

    // 如果这里不添加 synchronized 关键字来保护，那么可能会导致 name 和 address 混乱，例如 Bob - Australia
    public synchronized void pass(String name, String address) {
        this.counter++;
        this.name = name;
        this.address = address;
        check();
    }

    // 不添加 synchronized 之后的结果和上面的方法一样
    public synchronized String toString() {
        return "No." + counter + ": " + name + " - " + address;
    }

    private void check() {
        if (name.charAt(0) != address.charAt(0)) {
            System.exit(0);
            System.out.println("************** ERROR ****************" + toString());
        } else {
            System.out.println(toString());
        }
    }
}
```

### 3. UserThread.java

```java
package single.threaded.execution;

public class UserThread extends Thread {
    private final String name;
    private final String address;
    private final Gate gate;

    public UserThread(String name, String address, Gate gate) {
        this.name = name;
        this.address = address;
        this.gate = gate;
    }

    public void run() {
        System.out.println(name + " BEGIN");
        while (true) {
            gate.pass(name, address);
        }
    }
}
```