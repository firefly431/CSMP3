/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cs.ers.server;

import cs.ers.Card;
import cs.ers.Deck;
import cs.ers.Player;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import javax.swing.JButton;
import javax.swing.JFrame;

/**
 *
 * @author s506571
 */
public class Server {
    private ServerSocket ss;
    private ArrayList<ServerThread> players;

    public Deck middle;

    public static final int PORT = 5760;

    public void setClaim() {
        boolean fail = false;
        do {
            claim--;
            if (claim < 0) {
                if (fail) return;
                claim = players.size() - 1;
                fail = true;
            }
        } while (players.get(claim).deck.cards.isEmpty());
    }

    public static class NameTakenException extends Exception {}

    public long lastSlap;
    public int turn;
    public int counter = -1;
    public int claim = -1;
    public int counterplayer = -1;

    public void init(int port) {
        middle = new Deck(true);
        players = new ArrayList<ServerThread>();
        try {
            ss = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public synchronized void register(String name, ServerThread sock) throws NameTakenException {
        for (ServerThread t : players) {
            if (t.pname.equals(name))
                throw new NameTakenException();
        }
        players.add(sock);
    }

    public synchronized void deregister(String name, ServerThread sock) {
        players.remove(sock);
    }

    public synchronized void nextTurn() {
        turn++;
        validateTurn();
    }

    public synchronized void validateTurn() {
        if (players.size() == 0) return;
        if (turn >= players.size())
            turn = 0;
        boolean reset = false;
        while (players.get(turn).deck.cards.size() == 0) {
            turn++;
            if (turn >= players.size()) {
                turn = 0;
                if (reset)
                    return; // everyone's out of cards!
                reset = true;
            }
        }
    }

    public synchronized int getIndex(ServerThread t) {
        return players.indexOf(t);
    }

    public void run() {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JButton button = new JButton("Distribute cards");
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (players.size() == 0) return;
                // redistribute cards
                for (ServerThread t : players) {
                    t.deck.burnAll(middle);
                }
                middle.shuffle();
                ServerThread threads[] = players.toArray(new ServerThread[players.size()]);
                int nc = middle.cards.size() / threads.length;
                for (int q = 0; q < nc; q++) {
                    for (int i = 0; i < threads.length; i++)
                        middle.deal(threads[i].deck);
                }
                int i = (int)(Math.random() * threads.length);
                while (!middle.cards.isEmpty()) {
                    // deal to a random player
                    middle.deal(threads[(i++) % threads.length].deck);
                }
                turn = 0;
                broadcast();
            }
        });
        frame.getContentPane().add(button);
        frame.pack();
        frame.setVisible(true);
        
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
        for (int i = 0; i < players.size(); i++) {
            ServerThread p = players.get(i);
            data.append(p.getPlayerName());
            data.append('\n');
            data.append(p.getNumCards());
            data.append('\n');
            data.append(turn == i);
            data.append('\n');
            data.append(claim == i);
            data.append('\n');
            data.append(p.penalty);
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
        for (ServerThread p : players) {
            p.send(sss);
        }
    }

    public static void main(String argv[]) {
        Server s = new Server();
        s.init(Server.PORT);
        s.run();
    }
}
