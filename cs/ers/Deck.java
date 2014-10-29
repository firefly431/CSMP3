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
        Collections.sort(cards);
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
        int top = cards.get(0).getValue();
        int second = cards.get(1).getValue();
        int third = cards.get(2).getValue();

        if(top == second) { //double
            return true;
        }
        if(top == third) { //sandwich
            return true;
        }
        if((top == 12 && second == 13) || (top == 13 && second == 12)) { //marriage
            return true;
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
