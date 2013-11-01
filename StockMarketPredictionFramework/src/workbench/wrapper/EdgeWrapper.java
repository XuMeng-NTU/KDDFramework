/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package workbench.wrapper;

import abstraction.component.EdgeComponent;
import abstraction.component.NodeComponent;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import persistence.flow.edge.Edge;
import persistence.flow.node.Node;
import persistence.component.parameter.ParameterValue;

/**
 *
 * @author Meng
 */
public class EdgeWrapper {
    private Edge edge;
    private List<Map<String, Object>> input;
    private boolean output;
    private EdgeComponent worker;    
    
    
    public EdgeWrapper(Edge edge){
        this.edge = edge;
        worker = setupWorker(edge);
    }    
    
    private EdgeComponent setupWorker(Edge edge){
        try {
            Class provider = edge.getComponent().getProvider();
            EdgeComponent worker = (EdgeComponent) provider.newInstance();
            
            for(ParameterValue parameter : edge.getParameters()){
                Object value = Class.forName(parameter.getDefinition().getFormat()).getConstructor(String.class).newInstance(parameter.getValue());
                Field target = provider.getDeclaredField(parameter.getDefinition().getFieldName());
                target.setAccessible(true);
                target.set(worker, value);
                
            }
            
            return worker;
        } catch (InstantiationException ex) {
            Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchMethodException ex) {
            Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SecurityException ex) {
            Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvocationTargetException ex) {
            Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSuchFieldException ex) {
            Logger.getLogger(Node.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }        
    
    public void process(List<Map<String, Object>> data){
        input = data;
        output = worker.check(edge.getMethod().getMethodName(), data);
    }    
    
    public boolean getOutput(){
        return output;
    }
}
