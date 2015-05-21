/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cs.ers;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseListener;

/**
 * A StatePanel is a Panel that represents a game state.
 * A StatePanel can listen to events and process them accordingly.
 */
public class StatePanel extends JPanel implements KeyListener, MouseListener {
    /**
     * Constructor for a StatePanel
     */
    public StatePanel() {
        addMouseListener(this);
    }

    /**
     * @inherit
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(GameFrame.WINDOW_WIDTH, GameFrame.WINDOW_HEIGHT);
    }

    /**
     * @inherit
     */
    @Override
    public void paint(Graphics g) {}

    public void keyTyped(KeyEvent e) {}

    public void keyPressed(KeyEvent e) {}

    public void keyReleased(KeyEvent e) {}

    /**
     * Called when this panel is removed, panel should delete members
     * and stop processes
     */
    public void removed() {
        removeMouseListener(this);
    }

    public void mouseClicked(MouseEvent e) {}

    public void mousePressed(MouseEvent e) {}

    public void mouseReleased(MouseEvent e) {}

    public void mouseEntered(MouseEvent e) {}

    public void mouseExited(MouseEvent e) {}
}
