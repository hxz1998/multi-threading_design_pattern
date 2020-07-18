/**
 * Created by Xiaozhong on 2020/7/18.
 * Copyright (c) 2020/7/18 Xiaozhong. All rights reserved.
 */
package single.threaded.execution;

public class UserThread extends Thread {
    private final String name;
    private final String address;
    private final Gate gate;

    public UserThread(String name, String address, Gate gate) {
        this.name = name;
        this.address = address;
        this.gate = gate;
    }

    public void run() {
        System.out.println(name + " BEGIN");
        while (true) {
            gate.pass(name, address);
        }
    }
}
