/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package components.preprocessing.combiner;

import annotation.component.Component;
import annotation.component.method.Method;
import enumeration.component.ComponentType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Meng
 */
@Component(type=ComponentType.COMBINER)
public class Combine {
    
    private static List<Map<String, Object>> combined = new ArrayList<>();
    
    @Method(name="COMBINE")
    public List<Map<String, Object>> combine(List<Map<String, Object>> data){
        combined.addAll(data);
        return combined;
    }
}
