/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cs.ers.server;

import cs.ers.Card;
import cs.ers.Deck;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @author s506571
 */
public class ServerThread extends Thread {
    Socket sock;
    Server serv;
    BufferedReader in;
    PrintWriter out;
    String pname;
    Deck deck;
    int penalty;
    int SLAP_DELAY_NS = (500000000);
    public ServerThread(Socket sock, Server serv) {
        this.sock = sock;
        this.serv = serv;
        try {
            in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(sock.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        penalty = 0;
        deck = new Deck();
    }
    public String getPlayerName() {
        return pname;
    }
    public int getNumCards() {
        return deck.cards.size();
    }
    public void send(String data) {
        if (!sock.isClosed()) {
            out.print(data);
            out.flush();
        }
    }
    @Override
    public void run() {
        if (serv == null || sock == null || in == null || out == null) return;
        try {
            try {
                pname = in.readLine();
                if (pname == null || pname.equals(""))
                    throw new IOException("Invalid name");
                synchronized (serv) {
                    serv.register(pname, this);
                }
            } catch (Server.NameTakenException e) {
                out.println("ERROR name taken");
                out.flush();
                return;
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            out.println("ACK");
            out.flush();
            serv.broadcast();
            try {
                // read, write
                while (true) {
                    String cmd = in.readLine();
                    if (cmd == null) break;
                    if (cmd.equals("DEAL")) {
                        // TODO: make face cards work
                        synchronized (serv) {
                            if (deck.cards.size() > 0) {
                                if (serv.turn == serv.getIndex(this)) {
                                    serv.claim = -1;
                                    Card dealt = deck.deal(serv.middle);
                                    boolean next = true;
                                    switch (dealt.getValue()) {
                                        case Card.TEN:
                                            // reset counter
                                            serv.counter = -1;
                                            serv.counterplayer = -1;
                                            break;
                                        case Card.JACK:
                                            serv.counter = 1;
                                            serv.counterplayer = serv.getIndex(this);
                                            break;
                                        case Card.QUEEN:
                                            serv.counter = 2;
                                            serv.counterplayer = serv.getIndex(this);
                                            break;
                                        case Card.KING:
                                            serv.counter = 3;
                                            serv.counterplayer = serv.getIndex(this);
                                            break;
                                        case Card.ACE:
                                            serv.counter = 4;
                                            serv.counterplayer = serv.getIndex(this);
                                            break;
                                        default:
                                            if (serv.counter != -1) {
                                                serv.counter--;
                                                if (serv.counter == 0) {
                                                    // person before you
                                                    // can take cards
                                                    System.out.println("Setting claim");
                                                    serv.claim = serv.counterplayer;
                                                    System.out.println("Claim is " + serv.claim);
                                                    System.out.println("We are " + serv.getIndex(this));
                                                    serv.counter = -1;
                                                    serv.counterplayer = -1;
                                                } else {
                                                    next = false;
                                                }
                                            }
                                    }
                                    if (next)
                                        serv.nextTurn();
                                    serv.validateTurn();
                                } else {
                                    deck.burn(serv.middle);
                                    serv.validateTurn();
                                }
                            }
                        }
                    } else if (cmd.equals("CLAIM")) {
                        synchronized (serv) {
                            if (serv.middle.canSlap() || serv.claim == serv.getIndex(this)) {
                                if (penalty == 0) {
                                    serv.claim = -1;
                                    serv.counter = -1;
                                    serv.middle.dealAll(deck);
                                    serv.turn = serv.getIndex(this);
                                } else {
                                    penalty--;
                                }
                            } else {
                                if (serv.lastSlap < System.nanoTime() - SLAP_DELAY_NS) {
                                    if (deck.cards.size() > 0) {
                                        deck.burn(serv.middle);
                                        serv.validateTurn();
                                    } else {
                                        penalty++;
                                    }
                                }
                                serv.lastSlap = System.nanoTime();
                            }
                        }
                    }
                    serv.broadcast();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } finally {
            try {
                synchronized (serv) {
                    deck.burnAll(serv.middle);
                    serv.deregister(pname, this);
                }
                sock.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            serv.broadcast();
        }
    }
}
