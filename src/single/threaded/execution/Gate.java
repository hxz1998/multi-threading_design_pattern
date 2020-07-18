/**
 * Created by Xiaozhong on 2020/7/18.
 * Copyright (c) 2020/7/18 Xiaozhong. All rights reserved.
 */
package single.threaded.execution;

public class Gate {
    private int counter = 0;
    private String name = "Nobody";
    private String address = "Nowhere";

    public synchronized void pass(String name, String address) {
        this.counter++;
        this.name = name;
        this.address = address;
        check();
    }

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
