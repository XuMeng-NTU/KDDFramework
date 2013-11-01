/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package abstraction.component;

import components.preprocessing.cleaner.Date;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Meng
 */
public class NodeComponent {
    
    public List<Map<String, Object>> calculate(String methodName, List<Map<String, Object>> data){
        try {

            Method method = this.getClass().getDeclaredMethod(methodName, List.class);
            return (List<Map<String, Object>>) method.invoke(this, data);
            
        } catch (NoSuchMethodException | SecurityException ex) {
            Logger.getLogger(Date.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(NodeComponent.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(NodeComponent.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(NodeComponent.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
}
