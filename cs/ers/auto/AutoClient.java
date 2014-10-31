/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cs.ers.auto;

import cs.ers.*;
import cs.ers.server.Server;
import java.io.*;
import java.net.*;
import java.util.Scanner;
import javax.swing.*;

/**
 *
 * @author s506571
 */
public class AutoClient {
    Socket sock = null;
    PrintWriter out;
    BufferedReader in;
    String name;
    ActionTimer timer;
    public static void main(String argv[]) {
        new AutoClient().run();
    }
    public int getSleep() {
        return 500 + (int)(500 * Math.random());
    }
    public void run() {
        JFrame lol = new JFrame("Close this window to exit autoplay");
        lol.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        lol.setVisible(true);
        timer = new ActionTimer(0, 0, out);
        while (sock == null) {
            String addr = JOptionPane.showInputDialog("Enter IP address of server");
            if (addr == null || addr.equals("")) return;
            try {
                InetAddress ia = InetAddress.getByName(addr);
                sock = new Socket(ia, Server.PORT);
                out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(sock.getOutputStream())));
                in = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            } catch (UnknownHostException e) {
                JOptionPane.showMessageDialog(null, "Unknown host");
                continue;
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "IO exception");
                return;
            }
        }
        out.println(name=JOptionPane.showInputDialog("Enter your name"));
        out.flush();
        lol.setTitle(lol.getTitle() + ": " + name);
        try {
            String line = in.readLine();
            if (!line.equals("ACK")) {
                JOptionPane.showMessageDialog(null, line);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "IO exception");
            return;
        }
        // read forever
        Scanner s = new Scanner(in);
        while (true) {
            int np = s.nextInt();
            s.nextLine();
            int action = -1;
            for (int i = 0; i < np; i++) {
                String pn = s.nextLine();
                int nc = s.nextInt();
                s.nextLine();
                boolean pturn = s.nextBoolean();
                s.nextLine();
                boolean pclaim = s.nextBoolean();
                s.nextLine();
                int ppen = s.nextInt();
                s.nextLine();
                if (pn.equals(name) && nc > 0) {
                    if (pclaim) {
                        action = 1;
                    } else if (pturn) {
                        action = 0;
                    }
                }
            }
            int nc = s.nextInt();
            s.nextLine();
            Deck middle = new Deck(false);
            for (int i = 0; i < nc; i++) {
                middle.cards.add(new Card(s.nextInt()));
                if (s.hasNextLine())
                    s.nextLine();
            }
            if (middle.canSlap())
                action = 1;
            if (timer.isAlive()) {
                if (action != timer.do_action) {
                    timer.interrupt();
                    if (action != -1) {
                        timer = new ActionTimer(action, getSleep(), out);
                        timer.start();
                    }
                }
            } else {
                if (action != -1) {
                    timer = new ActionTimer(action, getSleep(), out);
                    timer.start();
                }
            }
        }
    }
}
