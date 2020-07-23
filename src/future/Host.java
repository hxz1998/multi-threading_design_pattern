/*
 * Copyright (c) 2020.
 */
package future;

public class Host {
    public Data request(char c, int count) {
        System.out.println("\t Request : " + count + " " + c + " BEGIN");
        // 先构造一个提货单
        FutureData futureData = new FutureData();
        // 创建新的线程，并且在线程里面完成获取真正的数据
        new Thread() {
            @Override
            public void run() {
                // 构造数据
                RealData realData = new RealData(c, count);
                // 获取数据
                futureData.setRealData(realData);
            }
        }.start();
        System.out.println("\t Request : " + count + " " + c + " END");
        return futureData;
    }
}
