package cs.ers;

/**
 * Basically a hidden deck
 * I think our model is really cool because the players don't know
 * any game-breaking stuff; basically what the players know in real life.
 *
 * This is pretty much just data.
 */
public class Player {
    public Player(String n, int nc, boolean t, boolean c, int p) {
        name = n;
        ncards = nc;
        turn = t;
        claim = c;
        penalty = p;
    }
    public Player(String n, int nc) {
        this(n, nc, false, false, 0);
    }
    @Override
    public String toString() {
        return name + " " + ncards;
    }
    public String name;
    public int ncards;
    public int penalty;
    public boolean turn, claim;
}
