/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package workbench.wrapper;

import java.util.ArrayList;
import java.util.List;
import persistence.flow.Flow;
import persistence.flow.node.Node;

/**
 *
 * @author Meng
 */
public class FlowWrapper {
    private Flow flow;
    
    public FlowWrapper(Flow flow){
        this.flow = flow;
    }

    
    
//    
//    private List<NodeWrapper> wrapNodes(){
//        List<NodeWrapper> result = new ArrayList<>();
//        
//        for(Node node : flow.getNodes()){
//            result.add(new NodeWrapper(node));
//        }
//        
//    }
    
}
