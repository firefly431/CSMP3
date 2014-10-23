/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cs.ers.server;

import java.io.IOException;
import java.net.*;
import java.util.TreeMap;

/**
 *
 * @author s506571
 */
public class Server {
    private ServerSocket ss;
    private TreeMap<String, ServerThread> players;

    public static class NameTakenException extends Exception {}

    public void init(int port) {
        try {
            ss = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void register(String name, ServerThread sock) throws NameTakenException {
        if (players.containsKey(name))
            throw new NameTakenException();
        players.put(name, sock);
    }

    public void deregister(String name, ServerThread sock) {
        if (players.get(name) != sock)
            throw new RuntimeException("Name registered to wrong thread");
        players.remove(name);
    }

    public void run() {
        try {
            while (true) {
                Socket s = ss.accept();
                new ServerThread(s, this).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
