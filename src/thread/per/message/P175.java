/*
 * Copyright (c) 2020.
 */
package thread.per.message;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class P175Host {
    private final Helper helper = new Helper();
    private final Executor executor;

    public P175Host(Executor executor) {
        this.executor = executor;
    }

    public void request(int count, char c) {
        // 使用 executor 接口来完成线程操作
        executor.execute(new Runnable() {
            @Override
            public void run() {
                helper.handle(c, count);
            }
        });
    }
}

public class P175 {
    public static void main(String[] args) {
        P175Host host = new P175Host(new Executor() {
            @Override
            // 当其他地方调用 executor.execute(Runnable command) 时，在该处就可以直接传入该对象并启动线程开始运行
            public void execute(Runnable command) {
                new Thread(command).start();
            }
        });
        host.request(10, 'A');
        host.request(20, 'B');
        host.request(30, 'C');

        System.out.println();

        // 也可以使用 ExecutorService 来管理服务
        ExecutorService service = Executors.newCachedThreadPool();
        P175Host host1 = new P175Host(service);
        try {
            host1.request(10, 'D');
            host1.request(20, 'E');
            host1.request(30, 'F');
        } finally {
            service.shutdown();
        }

    }
}
