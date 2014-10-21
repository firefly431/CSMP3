/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cs.ers;

import java.util.*;

/**
 *
 * @author s506571
 */
public class Deck {
    public LinkedList<Card> cards;
    public Deck() {
        this(false);
    }
    public Deck(boolean init) {
        cards = new LinkedList<Card>();
        if (init) {
            for (int i = 0; i < Card.VALUE_MAX * Card.SUIT_MAX; i++) {
                cards.add(new Card(i));
            }
        }
    }
}
