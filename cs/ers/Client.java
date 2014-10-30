/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cs.ers;

import cs.ers.server.Server;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.*;
import java.util.Scanner;
import javax.swing.JOptionPane;

/**
 *
 * @author s506571
 */
public class Client {
    GameFrame gf;
    GamePanel gp;
    Socket sock = null;
    PrintWriter out;
    BufferedReader in;
    String name;
    public static void main(String argv[]) {
        new Client().run();
    }

    public void run() {
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
        try {
            String line = in.readLine();
            if (!line.equals("ACK")) {
                JOptionPane.showMessageDialog(null, line);
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "IO exception");
            return;
        }
        gf = new GameFrame();
        gf.init();
        gp = (GamePanel)gf.getCurrentPanel();
        gp.setSender(new MessageSender() {
            public void send(String data) {
                out.println(data);
                out.flush();
            }
        });
        gf.setVisible(true);
        // read forever
        Scanner s = new Scanner(in);
        while (true) {
            int np = s.nextInt();
            s.nextLine();
            Player[] players = new Player[np];
            int me = -1;
            for (int i = 0; i < np; i++) {
                String pn = s.nextLine();
                if (pn.equals(name)) me = i;
                int nc = s.nextInt();
                s.nextLine();
                boolean pturn = s.nextBoolean();
                s.nextLine();
                boolean pclaim = s.nextBoolean();
                s.nextLine();
                int ppen = s.nextInt();
                s.nextLine();
                Player p = new Player(pn, nc, pturn, pclaim, ppen);
                players[i] = p;
            }
            int nc = s.nextInt();
            s.nextLine();
            int[] cards = new int[nc];
            for (int i = 0; i < nc; i++) {
                cards[i] = s.nextInt();
                if (s.hasNextLine())
                    s.nextLine();
            }
            gp.updatePlayers(players, me);
            gp.updateMiddle(cards);
        }
    }
}
