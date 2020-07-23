/*
 * Copyright (c) 2020.
 */
package future;

public class Main {
    public static void main(String[] args) {
        System.out.println("Main Begin");
        Host host = new Host();
        // 发出三个请求，并且拿到提货单
        Data data1 = host.request('A', 10);
        Data data2 = host.request('B', 20);
        Data data3 = host.request('C', 30);
        // 假装去干一会儿别的事
        System.out.println("去干别的事儿啦~");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("干别的事回来惹，康康之前的请求完成了咩");
        // 使用提货单，来获取真正的数据
        System.out.println("data1 = " + data1.getContent());
        System.out.println("data2 = " + data2.getContent());
        System.out.println("data3 = " + data3.getContent());
        System.out.println("Main End");
    }
}
