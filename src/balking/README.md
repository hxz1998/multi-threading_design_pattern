# Balking 模式

## 0. 描述了什么事？
当发现文件没有修改时，直接返回不用保存。

## 1. 类的说明，怎么抽象？
|类名|说明|
|---|---|
|`Main`|启动类|
|`Data`|数据的抽象类，提供了两个线程同步的方法，分别是修改文件内容和保存。|
|`ChangerThread`|定时修改文件内容的类|
|`SaverThread`|定时保存文件内容的类|

## 2. 示例程序

### 1. Main.java
```java
package balking;

public class Main {
    public static void main(String[] args) {
        Data data = new Data("data.txt", "(empty)");
        ChangerThread changer = new ChangerThread("ChangerThread", data);
        SaverThread saver = new SaverThread("SaverThread", data);
        changer.start();
        saver.start();
    }
}
```

### 2. Data.java
```java
package balking;

import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

public class Data {
    private final String filename;
    private String content;
    private boolean changed;

    public Data(String filename, String content) {
        this.filename = filename;
        this.content = content;
        this.changed = true;
    }

    public synchronized void change(String newContent) {
        this.content = newContent;
        this.changed = true;
    }

    public synchronized void save() throws IOException {
        // 守护条件，当条件不满足时，就直接返回去，不再重复保存
        if (!changed) {
            System.out.println("\t\t内容一样，不需要保存！");
            return;
        }
        doSave();
        // 保存结束后记得要修改回来守护条件
        changed = false;
    }

    private void doSave() throws IOException {
        System.out.println(Thread.currentThread().getName() + " calls doSave, content = " + content);
        Writer writer = new FileWriter(filename);
        writer.write(content);
        writer.close();
    }

}
```

### 3. ChangerThread.java
```java
package balking;

import java.io.IOException;
import java.util.Random;

public class ChangerThread extends Thread {
    private final Data data;
    private final Random random = new Random();

    public ChangerThread(String name, Data data) {
        super(name);
        this.data = data;
    }

    @Override
    public void run() {
        try {
            for (int i = 0; i < 1000; i++) {
                // 先修改文件内容
                data.change("No. " + i);
                // 然后“装模做样”去做点别的
                Thread.sleep(random.nextInt(1000));
                // 最后回来触发保存
                data.save();
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
}
```

### 4. SaverThread.java
```java
package balking;

import java.io.IOException;

public class SaverThread extends Thread {
    private final Data data;

    public SaverThread(String name, Data data) {
        super(name);
        this.data = data;
    }

    @Override
    public void run() {
        while (true) {
            try {
                // 每过1秒，自动保存一次文件内容
                data.save();
                Thread.sleep(1000);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
```