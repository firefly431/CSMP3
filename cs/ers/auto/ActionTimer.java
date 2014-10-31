/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cs.ers.auto;

import java.io.PrintWriter;

/**
 *
 * @author s506571
 */
public class ActionTimer extends Thread {
    public int do_action;
    public long sleep;
    public PrintWriter out;
    @Override
    public void run() {
        try {
            Thread.sleep(sleep);
        } catch (InterruptedException e) {
            return;
        }
        if (do_action == 0) {
            out.println("DEAL");
            out.flush();
        } else {
            out.println("CLAIM");
            out.flush();
        }
    }
    public ActionTimer(int action, int amt, PrintWriter o) {
        do_action = action;
        sleep = amt;
        out = o;
    }
}
