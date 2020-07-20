/*
 * Copyright (c) 2020.
 */
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
