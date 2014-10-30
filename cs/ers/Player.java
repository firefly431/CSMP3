/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cs.ers;

/**
 *
 * @author s506571
 */
public class Player {
    public Player(String n, int nc, boolean t, boolean c) {
        name = n;
        ncards = nc;
        turn = t;
        claim = c;
    }
    public Player(String n, int nc) {
        this(n, nc, false, false);
    }
    @Override
    public String toString() {
        return name + " " + ncards;
    }
    public String name;
    public int ncards;
    boolean turn, claim;
}
