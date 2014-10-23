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

    Deck p1, p2, p3, p4;
    Deck middle;
    Deck[] pdecks;

    public GamePanel() {
        p1 = new Deck();
        p2 = new Deck();
        p3 = new Deck();
        p4 = new Deck();
        Deck start = new Deck(true);
        start.shuffle();
        for (int i = 0; i < 13; i++) {
            start.deal(p1);
            start.deal(p2);
            start.deal(p3);
            start.deal(p4);
        }
        assert start.cards.isEmpty();
        middle = new Deck();
        pdecks = new Deck[]{p1, p2, p3, p4};
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
        for (int i = (num & ~1) - 1; i >= 0; i -= 2) {
            int dx = x - i;
            int dy = y;
            g.drawImage(backImg, dx, dy, this);
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
        int dsy = 50;
        int dey = (GameFrame.WINDOW_HEIGHT - dsy - Card.HEIGHT);
        // since 3 decks other than us, draw at
        // (dsx, dsy), ((dsx + dex) / 2, dsy), (dex, dsy)
        drawDeck(g, p2, dsx, dsy);
        drawDeck(g, p3, (dsx + dex) / 2, dsy);
        drawDeck(g, p4, dex, dsy);
        // draw us
        drawDeck(g, p1, (dsx + dex) / 2, dey);
        // draw middle
        drawFront(g, middle.getFront(5), (dsx + dex) / 2, (dsy + dey) / 2);
    }
    int cp = 0;
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_D) {
            cp++;
            cp %= pdecks.length;
            pdecks[cp].deal(middle);
        }
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            middle.dealAll(pdecks[0]);
            cp = 0;
        }
        repaint();
    }
}
