# Immutable 模式

> 参考书目[《图解Java多线程设计模式》](https://book.douban.com/subject/27116724/) [日] 结城浩 著

## 0. 描述了什么事？
一个实例产生后就不发生变化了。

## 1. 类的说明，如何抽象？
|类名|说明|
|---|---|
|`Main`|启动类|
|`Person`|描述人的类|
|`PrintPersonThread`|打印 `Person` 类的信息|

## 2. 程序示例

### 1. Main.java
```java
package immutable;

public class Main {
    public static void main(String[] args) {
        Person alice = new Person("Alice", "Alaska");
        new PrintPersonThread(alice).start();
        new PrintPersonThread(alice).start();
        new PrintPersonThread(alice).start();
    }
}
```

### 2. Person.java

```java
package immutable;

public final class Person {
    private final String name;
    private final String address;

    public Person(String name, String address) {
        this.name = name;
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}
```

### 3. PrintPersonThread.java
```java
package immutable;

public class PrintPersonThread extends Thread {
    private Person person;

    public PrintPersonThread(Person person) {
        this.person = person;
    }

    @Override
    public void run() {
        while (true) {
            System.out.println(Thread.currentThread().getName() + " Print " + person);
        }
    }
}
```