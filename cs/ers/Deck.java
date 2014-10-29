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

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public void deal(Deck to) {
        to.cards.addFirst(cards.removeLast());
    }
    public void dealAll(Deck to) {
        to.cards.addAll(0, cards);
        cards.clear();
    }
    public void burn(Deck to) {
        to.cards.addLast(cards.removeLast());
    }
    public void burnAll(Deck to) {
        to.cards.addAll(cards);
        cards.clear();
    }

    public boolean canSlap() {
        try {
            if ((cards.get(0).getValue() == Card.KING  && cards.get(1).getValue() == Card.QUEEN)
             || (cards.get(0).getValue() == Card.QUEEN && cards.get(1).getValue() == Card.KING)) { //marriage
                return true;
            }
            if (cards.get(0).getValue() == cards.get(1).getValue()) { //double
                return true;
            }
            if (cards.get(0).getValue() == cards.get(2).getValue()) { //sandwich
                return true;
            }
        } catch (IndexOutOfBoundsException e) {
            return false;
        }
        return false;
    }


    // return.length <= num
    public Card getFront(int num)[] {
        Card[] ret = new Card[cards.size() < num ? cards.size() : num];
        int i = 0;
        for (Card c : cards) {
            ret[i++] = c;
            if (i >= ret.length) break;
        }
        return ret;
    }
}
