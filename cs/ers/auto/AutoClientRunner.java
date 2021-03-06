package cs.ers.auto;

import javax.swing.JOptionPane;

/**
 * Utility program to run a bunch of AIs at the same time with the same parameters.
 * Also useful to run 1 AI with custom parameters.
 */
public class AutoClientRunner implements Runnable {
    public static String addr;
    // use a static variable so that the letters (for the name) change automatically
    public static char foo = 'A'; // kind of bad naming
    public char n;
    public static int base_delay, rand_delay;
    public AutoClientRunner() {
        n = foo++; // increment foo
    }
    public static void main(String argv[]) {
        int nclients = Integer.parseInt(JOptionPane.showInputDialog("How many players?"));
        addr = JOptionPane.showInputDialog("Enter IP Address");
        base_delay = Integer.parseInt(JOptionPane.showInputDialog("Enter base delay"));
        rand_delay = Integer.parseInt(JOptionPane.showInputDialog("Enter random delay"));
        for (int i = 0; i < nclients; i++) {
            new Thread(new AutoClientRunner()).start();
        }
    }
    /**
     * Run an instance of the client
     */
    public void run() {
        AutoClient a = new AutoClient();
        a.name = String.valueOf(n);
        a.base_delay = base_delay;
        a.rand_delay = rand_delay;
        a.run(addr);
    }
}
