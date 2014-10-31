/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cs.ers;

/**
 *
 * @author s506571
 */
public class Card implements Comparable<Card> {
    // suits
    public static final int SPADES = 0;
    public static final int HEARTS = 1;
    public static final int DIAMONDS = 2;
    public static final int CLUBS = 3;
    public static final int SUIT_MAX = 4;
    // values
    public static final int ACE = 1;
    public static final int TWO = 2;
    public static final int THREE = 3;
    public static final int FOUR = 4;
    public static final int FIVE = 5;
    public static final int SIX = 6;
    public static final int SEVEN = 7;
    public static final int EIGHT = 8;
    public static final int NINE = 9;
    public static final int TEN = 10;
    public static final int JACK = 11;
    public static final int QUEEN = 12;
    public static final int KING = 13;
    public static final int VALUE_MAX = 13;
    // constants
    public static final int WIDTH = 112;
    public static final int HEIGHT = 156;
    // variables
    private int suit, value;
    public Card(int v, int s) {
        suit = s;
        value = v;
    }

    public Card(int i) {
        suit = i / VALUE_MAX;
        value = i % VALUE_MAX + 1;
    }
    public int getSuit() {
        return suit;
    }
    public int getValue() {
        return value;
    }
    public int getValueIndex() {
        return value - 1;
    }
    public int getIndex() {
        return suit * VALUE_MAX + value - 1;
    }

    public int compareTo(Card o) {
        return getIndex() - o.getIndex();
    }
    @Override
    public String toString() {
        String ret = "";
        switch (value) {
            case ACE:
                ret = "ace";
                break;
            case JACK:
                ret = "jack";
                break;
            case QUEEN:
                ret = "queen";
                break;
            case KING:
                ret = "king";
                break;
            default:
                ret = Integer.toString(value);
                break;
        }
        ret += " of ";
        switch (suit) {
            case SPADES:
                ret += "spades";
                break;
            case HEARTS:
                ret += "hearts";
                break;
            case DIAMONDS:
                ret += "diamonds";
                break;
            case CLUBS:
                ret += "clubs";
                break;
        }
        return ret;
    }
}
