/*
 * Copyright (c) 2020.
 */
package thread.per.message;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

class P173Host {
    private final ThreadFactory factory;
    private final Helper helper = new Helper();

    public P173Host(ThreadFactory factory) {
        this.factory = factory;
    }

    public void request(int count, char c) {
        System.out.println("request: " + count + " - " + c);
        factory.newThread(new Runnable() {
            @Override
            public void run() {
                helper.handle(c, count);
            }
        }).start();
    }
}

public class P173 {
    public static void main(String[] args) {
//        P173Host host = new P173Host(new ThreadFactory() {
//            @Override
//            public Thread newThread(Runnable r) {
//                return new Thread(r);
//            }
//        });
        // 也可以像下面这样使用默认的线程工厂
        P173Host host = new P173Host(Executors.defaultThreadFactory());
        host.request(10, 'A');
        host.request(20, 'B');
        host.request(30, 'C');
    }
}
