/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package gui.util;

import javax.swing.JPanel;

/**
 *
 * @author Meng
 */
public class ObjectHoldingPanel extends JPanel{
    private Object userObject;
    
    public ObjectHoldingPanel(){
        super();
    }
    
    public Object getUserObject(){
        return userObject;
    }
    
    public void setUserObject(Object obj){
        userObject = obj;
    }
}
