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
    public Player(String n, int nc) {
        name = n;
        ncards = nc;
    }
    @Override
    public String toString() {
        return name + " " + ncards;
    }
    public String name;
    public int ncards;
}
