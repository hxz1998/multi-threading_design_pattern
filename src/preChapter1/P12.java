/**
 * Created by Xiaozhong on 2020/7/18.
 * Copyright (c) 2020/7/18 Xiaozhong. All rights reserved.
 */
package preChapter1;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class P12 {
    public static void main(String[] args) throws InterruptedException {
        ThreadFactory factory = Executors.defaultThreadFactory();
        factory.newThread(new Printer("Nice!")).start();
        for (int i = 0; i < 100; i++) {
            System.out.println(i + " " + "Good!");
            Thread.sleep(1000);
        }
    }

}
