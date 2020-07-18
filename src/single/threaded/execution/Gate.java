/**
 * Created by Xiaozhong on 2020/7/18.
 * Copyright (c) 2020/7/18 Xiaozhong. All rights reserved.
 */
package single.threaded.execution;

public class Gate {
    private int counter = 0;
    private String name = "Nobody";
    private String address = "Nowhere";

    // 如果这里不添加 synchronized 关键字来保护，那么可能会导致 name 和 address 混乱，例如 Bob - Australia
    public synchronized void pass(String name, String address) {
        this.counter++;
        this.name = name;
        this.address = address;
        check();
    }

    // 不添加 synchronized 之后的结果和上面的方法一样
    public synchronized String toString() {
        return "No." + counter + ": " + name + " - " + address;
    }

    private void check() {
        if (name.charAt(0) != address.charAt(0)) {
            System.exit(0);
            System.out.println("************** ERROR ****************" + toString());
        } else {
            System.out.println(toString());
        }
    }
}
