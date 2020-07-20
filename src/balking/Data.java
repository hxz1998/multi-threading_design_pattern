/*
 * Copyright (c) 2020.
 */
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
