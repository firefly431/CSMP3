/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cs.ers.server;

import cs.ers.Card;
import cs.ers.Deck;
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

    private Deck middle;

    public static class NameTakenException extends Exception {}

    public void init(int port) {
        middle = new Deck(true);
        players = new TreeMap<String, ServerThread>();
        try {
            ss = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void register(String name, ServerThread sock) throws NameTakenException {
        if (players.containsKey(name))
            throw new NameTakenException();
        players.put(name, sock);
    }

    public synchronized void deregister(String name, ServerThread sock) {
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

    public synchronized void broadcast() {
        StringBuilder data = new StringBuilder();
        // broadcast the middle and the players
        // send # players
        data.append(players.size());
        data.append('\n');
        // send player name and deck size
        for (ServerThread p : players.values()) {
            data.append(p.getPlayerName());
            data.append(' ');
            data.append(p.getNumCards());
            data.append('\n');
        }
        // send middle
        data.append(middle.cards.size());
        data.append('\n');
        for (Card c : middle.cards) {
            data.append(c.getIndex());
            data.append('\n');
        }
        String sss = data.toString();
        for (ServerThread p : players.values()) {
            p.send(sss);
        }
    }

    public static void main(String argv[]) {
        Server s = new Server();
        s.init(5000);
        s.run();
    }
}
