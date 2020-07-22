/*
 * Copyright (c) 2020.
 */
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
