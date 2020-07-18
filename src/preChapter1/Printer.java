/**
 * Created by Xiaozhong on 2020/7/18.
 * Copyright (c) 2020/7/18 Xiaozhong. All rights reserved.
 */
package preChapter1;

public class Printer implements Runnable {
    private String message;

    public Printer(String message) {
        this.message = message;
    }

    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            System.out.println(i + " " + message);
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
