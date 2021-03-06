package cs.ers;

import java.awt.*;
import java.awt.event.KeyEvent;

import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 * Main game panel class.
 */
public class GamePanel extends StatePanel {
    // these are the card images.
    // cardImage actually has all the cards together.
    private static BufferedImage cardImg;
    private static BufferedImage backImg;
    // used to avoid typing
    private static final int CW = Card.WIDTH;
    private static final int CH = Card.HEIGHT;

    // used to delegate message sending (encapsulation)
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
        int tx = (index % Card.VALUE_MAX); // get the x component of the card
        int ty = (index / Card.VALUE_MAX); // y component
        int dx = tx * CW; // get the x in pixels
        int dy = ty * CH; // y
        // the below call is self-explanatory if you look at the Graphics.drawImage method
        g.drawImage(cardImg, x, y, x + CW, y + CH, dx, dy, dx + CW, dy + CH, this);
    }
    public void drawCard(Graphics g, Card card, int x, int y) {
        drawCard(g, card.getIndex(), x, y);
    }
    /**
     * Draw a deck face-down
     */
    public void drawDeck(Graphics g, int num, int x, int y) {
        for (int i = (num + 1 & ~1) - 1; i >= 0; i -= 2) {
            int dx = x - i;
            int dy = y;
            g.drawImage(backImg, dx, dy, this);
        }
    }
    /**
     * Draw a player's deck
     */
    public void drawDeck(Graphics g, Player p, int x, int y) {
        drawDeck(g, p.ncards, x, y); // draw the deck
        g.setColor(Color.BLACK);
        // calculate the center
        FontMetrics fm = g.getFontMetrics();
        int tx = x + Card.WIDTH / 2; // center of drawing
        int sw = fm.stringWidth(p.name + " " + p.ncards);
        tx -= sw / 2; // leftmost x coordinate of text
        g.drawString(p.name + " " + p.ncards, tx, y - 3);
        // we use sw and increment it to offset the circles drawn
        if (p.turn) { // if it's p's turn, draw a red circle
            g.setColor(Color.RED);
            g.fillOval(tx + (sw += 2), y - 14, 10, 10);
            sw += 10;
        }
        if (p.claim) { // if p can claim, draw a blue circle
            g.setColor(Color.BLUE);
            g.fillOval(tx + (sw += 2), y - 14, 10, 10);
            sw += 10;
        }
        if (p.penalty != 0) { // if p has a penalty, draw a green text
            g.setColor(Color.GREEN);
            g.drawString("Penalty: " + p.penalty, tx + sw + 2, y);
        }
    }
    /**
     * Draw a deck face-down
     */
    public void drawDeck(Graphics g, Deck d, int x, int y) {
        drawDeck(g, d.cards.size(), x, y);
    }
    /**
     * Draw some cards face-up
     */
    public void drawFront(Graphics g, int[] indices, int x, int y) {
         for (int i = indices.length - 1; i >= 0; i--) {
             drawCard(g, indices[i], x - i * 20, y);
         }
    }
    /**
     * Draw some cards face-up
     * We had to duplicate this because there's no easy way
     * to call the above with an array of Cards
     */
    public void drawFront(Graphics g, Card[] cards, int x, int y) {
        for (int i = cards.length - 1; i >= 0; i--) {
            drawCard(g, cards[i], x - i * 20, y);
        }
    }
    @Override
    public void paint(Graphics g) {
        // clear the paint area
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, GameFrame.WINDOW_WIDTH, GameFrame.WINDOW_HEIGHT);
        int dsx = 50; // deck start x (0 + border)
        int dex = (GameFrame.WINDOW_WIDTH - dsx - Card.WIDTH);
        int dfx = dex - dsx; // width of deck area
        int dsy = 50; // deck start y
        int dey = (GameFrame.WINDOW_HEIGHT - dsy - Card.HEIGHT);
        int ndecks = decks.length;
        if (index >= 0) // if we have a deck, we don't need to draw us
            --ndecks; // so decrease number of decks
        if (ndecks > 1) { // if there's more than one deck (must handle separately to avoid dividing by 0)
            int ei = 0; // temp variable, index of the deck (not including us)
            for (int i = 0; i < decks.length; i++) {
                if (i == index) continue;
                int dx = dsx + dfx * ei++ / (ndecks - 1); // compute position
                drawDeck(g, decks[i], dx, dsy);
            }
        }
        if (ndecks == 1) {
            // the ternary operator handles the case when there's one deck (and our index is -1)
            // the 1 - index is used as a quick way to determine the other index
            drawDeck(g, decks[decks.length == 1 ? 0 : (1 - index)], (dsx + dex) / 2, dsy);
        }
        // draw us
        if (index >= 0)
            drawDeck(g, decks[index], (dsx + dex) / 2, dey);
        if (middle != null) { // meaningless to synchronize on null
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
