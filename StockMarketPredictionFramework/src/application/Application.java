/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import gui.main.MainDisplay;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

/**
 *
 * @author Meng
 */
public class Application {
    public static void main(String[] args){
        
        final Background background = Background.getInstance();
        final MainDisplay gui = new MainDisplay();
        
        gui.addWindowListener(new WindowListener() {

            @Override
            public void windowOpened(WindowEvent we) {}

            @Override
            public void windowClosing(WindowEvent we) {}

            @Override
            public void windowClosed(WindowEvent we) {
                background.close();
            }

            @Override
            public void windowIconified(WindowEvent we) {}

            @Override
            public void windowDeiconified(WindowEvent we) {}

            @Override
            public void windowActivated(WindowEvent we) {}

            @Override
            public void windowDeactivated(WindowEvent we) {}
        });
        
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                gui.setVisible(true);
            }
        });      
    }
}
