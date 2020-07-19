/*
 * Copyright (c) 2020.
 */
package guaraded.suspension;

public class Main {
    public static void main(String[] args) {
        RequestQueue requestQueue = new RequestQueue();
        new ClientThread(requestQueue, "ClientThread", 31415926L).start();
        new ServerThread(requestQueue, 926535L, "ServerThread").start();
    }
}
