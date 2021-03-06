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
 * Server thread (one for each player)
 */
public class ServerThread extends Thread {
    Socket sock;
    Server serv;
    BufferedReader in;
    PrintWriter out;
    // basically Player's properties
    String pname;
    Deck deck;
    int penalty;
    // half a second
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
                // read the player's name
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
                    // player sent us a command?
                    String cmd = in.readLine();
                    if (cmd == null) break;
                    if (cmd.equals("DEAL")) {
                        synchronized (serv) {
                            if (deck.cards.size() > 0) {
                                // if it's our turn
                                if (serv.turn == serv.getIndex(this)) {
                                    serv.claim = -1; // no one can claim now
                                    Card dealt = deck.deal(serv.middle); // deal a card
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
                                                // if we're out of cards so counterplayer can claim
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
                                                    next = false; // don't go to next turn
                                                }
                                            }
                                    }
                                    if (next)
                                        serv.nextTurn();
                                    serv.validateTurn();
                                } else { // invalid move; burn
                                    deck.burn(serv.middle);
                                    serv.validateTurn();
                                }
                            }
                        }
                    } else if (cmd.equals("CLAIM")) {
                        synchronized (serv) {
                            // if we can slap/claim
                            if (serv.middle.canSlap() || serv.claim == serv.getIndex(this)) {
                                if (penalty == 0) {
                                    // reset
                                    serv.claim = -1;
                                    serv.counter = -1;
                                    // get all the cards!
                                    serv.middle.dealAll(deck);
                                    // it's our turn
                                    serv.turn = serv.getIndex(this);
                                } else {
                                    penalty--;
                                }
                            } else {
                                // don't penalize late slaps
                                if (serv.lastSlap < System.nanoTime() - SLAP_DELAY_NS) {
                                    // burn or penalize
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
                // give the server all our cards and deregister name
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
