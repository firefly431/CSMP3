/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cs.ers;

import java.awt.*;
import java.awt.event.KeyEvent;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author s506571
 */
public class GamePanel extends StatePanel {
    private static BufferedImage cardImg;
    private static BufferedImage backImg;
    private static final int CW = Card.WIDTH;
    private static final int CH = Card.HEIGHT;

    private MessageSender sender;

    Player[] decks;
    int index; // index in decks[] of us
    Deck middle;

    public GamePanel() {
        decks = new Player[0];
        sender = null;
        index = -1;
    }

    public void setSender(MessageSender s) {
        sender = s;
    }

    public static void init() {
        try {
            cardImg = ImageIO.read(GamePanel.class.getClassLoader().getResourceAsStream("smallcards.png"));
            backImg = ImageIO.read(GamePanel.class.getClassLoader().getResourceAsStream("smallback.png"));
        } catch (IOException e) {
            // not good
            throw new RuntimeException("NO CARDS");
        }
    }
    public void drawCard(Graphics g, int index, int x, int y) {
        int tx = (index % Card.VALUE_MAX);
        int ty = (index / Card.VALUE_MAX);
        int dx = tx * CW;
        int dy = ty * CH;
        g.drawImage(cardImg, x, y, x + CW, y + CH, dx, dy, dx + CW, dy + CH, this);
    }
    public void drawCard(Graphics g, Card card, int x, int y) {
        drawCard(g, card.getIndex(), x, y);
    }
    public void drawDeck(Graphics g, int num, int x, int y) {
        for (int i = (num + 1 & ~1) - 1; i >= 0; i -= 2) {
            int dx = x - i;
            int dy = y;
            g.drawImage(backImg, dx, dy, this);
        }
        FontMetrics fm = g.getFontMetrics();
        //
    }
    public void drawDeck(Graphics g, Player p, int x, int y) {
        drawDeck(g, p.ncards, x, y);
        g.setColor(Color.BLACK);
        FontMetrics fm = g.getFontMetrics();
        int sw;
        int tx = x + Card.WIDTH / 2;
        tx -= (sw = fm.stringWidth(p.name + " " + p.ncards)) / 2;
        g.drawString(p.name + " " + p.ncards, tx, y - 3);
        if (p.turn) {
            g.setColor(Color.RED);
            g.fillOval(x + (sw += 2), y - 14, 10, 10);
            sw += 10;
        }
        if (p.claim) {
            g.setColor(Color.BLUE);
            g.fillOval(x + (sw += 2), y - 14, 10, 10);
            sw += 10;
        }
    }
    public void drawDeck(Graphics g, Deck d, int x, int y) {
        drawDeck(g, d.cards.size(), x, y);
    }
    public void drawFront(Graphics g, int[] indices, int x, int y) {
        for (int i = indices.length - 1; i >= 0; i--) {
            drawCard(g, indices[i], x - i * 20, y);
        }
         
    }
    public void drawFront(Graphics g, Card[] cards, int x, int y) {
        for (int i = cards.length - 1; i >= 0; i--) {
            drawCard(g, cards[i], x - i * 20, y);
        }
    }
    @Override
    public void paint(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, GameFrame.WINDOW_WIDTH, GameFrame.WINDOW_HEIGHT);
        // draw test stuff
        int dsx = 50;
        int dex = (GameFrame.WINDOW_WIDTH - dsx - Card.WIDTH);
        int dfx = dex - dsx;
        int dsy = 50;
        int dey = (GameFrame.WINDOW_HEIGHT - dsy - Card.HEIGHT);
        // since 3 decks other than us, draw at
        // (dsx, dsy), ((dsx + dex) / 2, dsy), (dex, dsy)
//        drawDeck(g, p2, dsx, dsy);
//        drawDeck(g, p3, (dsx + dex) / 2, dsy);
//        drawDeck(g, p4, dex, dsy);
        int ndecks = decks.length;
        if (index >= 0)
            --ndecks;
        if (ndecks > 1) {
            int ei = 0;
            for (int i = 0; i < decks.length; i++) {
                if (i == index) continue;
                int dx = dsx + dfx * ei++ / (ndecks - 1);
                drawDeck(g, decks[i], dx, dsy);
            }
        }
        if (ndecks == 1) {
            drawDeck(g, decks[decks.length == 1 ? 0 : (1 - index)], (dsx + dex) / 2, dsy);
        }
        // draw us
        if (index >= 0)
            drawDeck(g, decks[index], (dsx + dex) / 2, dey);
        if (middle != null) {
            synchronized (middle) {
                // draw middle
                drawFront(g, middle.getFront(5), (dsx + dex) / 2, (dsy + dey) / 2);
            }
        }
    }
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_D) {
            // send message
            sender.send("DEAL");
        }
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            // send message
            sender.send("CLAIM");
        }
    }
    // does not clone
    public void updatePlayers(Player[] players, int me) {
        decks = players;
        index = me;
        repaint();
    }
    // does clone
    public void updateMiddle(int[] m) {
        middle = new Deck();
        synchronized (middle) {
            for (int c : m) {
                middle.cards.add(new Card(c));
            }
        }
        repaint();
    }
}
