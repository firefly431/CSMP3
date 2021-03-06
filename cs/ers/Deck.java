package cs.ers;

import java.util.*;

/**
 * Deck class. Contains some cards.
 */
public class Deck {
    /**
     * List of cards.
     * cards[0] is the front, and cards[size - 1] is the back.
     */
    public LinkedList<Card> cards;
    public Deck() {
        this(false);
    }
    /**
     * Init this deck with the standard deck of cards
     * if init is true.
     */
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

    public Card deal(Deck to) {
        Card ret;
        to.cards.addFirst(ret = cards.removeLast());
        return ret;
    }
    public void dealAll(Deck to) {
        to.cards.addAll(0, cards);
        cards.clear();
    }
    public Card burn(Deck to) {
        Card ret;
        to.cards.addLast(ret = cards.removeLast());
        return ret;
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
    /**
     * Get the front num cards.
     */
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
