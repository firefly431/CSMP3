/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cs.ers.auto;

import javax.swing.JOptionPane;

/**
 *
 * @author s506571
 */
public class AutoClientRunner implements Runnable {
    public static String addr;
    public static char foo = 'A';
    public char n;
    public static int base_delay, rand_delay;
    public AutoClientRunner() {
        n = foo++;
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
    public void run() {
        AutoClient a = new AutoClient();
        a.name = String.valueOf(n);
        a.base_delay = base_delay;
        a.rand_delay = rand_delay;
        a.run(addr);
    }
}
