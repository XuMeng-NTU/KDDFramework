/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package components.preprocessing.cleaner;

import abstraction.component.NodeComponent;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import annotation.component.Component;
import annotation.component.method.Method;
import annotation.component.input.RequiredInput;
import annotation.component.parameter.Parameter;
import enumeration.component.ComponentType;

/**
 *
 * @author Meng
 */
@Component(type=ComponentType.CALCULATOR)
public class Date extends NodeComponent{
    
    @Parameter(name="MAX_DAY_SPAN", format="java.lang.Integer")
    private int MAX_DAY_SPAN;
    
    @RequiredInput(name="DATE", format="java.util.Date")
    private String DATE = "DATE";
    
    @Method(name="CLEAN")
    public List<Map<String, Object>> clean(List<Map<String, Object>> data){
        int breakPoint = 0;
        int i;
        for (i = 1; i < data.size(); i++) {
            Map<String, Object> current = data.get(i);
            Map<String, Object> previous = data.get(i-1);

            if (((java.util.Date) current.get(DATE)).getTime() - ((java.util.Date) previous.get(DATE)).getTime() > (MAX_DAY_SPAN * (1000 * 60 * 60 * 24))) {
                breakPoint = i;
            }    
        }
        
        List<Map<String, Object>> result = new ArrayList<>();
        for(i=breakPoint;i<data.size();i++){
            result.add(data.get(i));
        }
        
        return result;
    }  
}
