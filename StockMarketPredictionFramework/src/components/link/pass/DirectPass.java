/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package components.link.pass;

import abstraction.component.EdgeComponent;
import annotation.component.Component;
import annotation.component.method.Method;
import enumeration.component.ComponentType;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Meng
 */
@Component(type=ComponentType.LINKER)
public class DirectPass extends EdgeComponent{

    @Method(name="TEST")
    public boolean test(List<Map<String, Object>> data){
        return true;
    }
}
