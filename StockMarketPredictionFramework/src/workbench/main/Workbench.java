/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package workbench.main;

import io.reader.DatasetReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import persistence.flow.edge.Edge;
import persistence.flow.node.Node;
import persistence.senario.Senario;
import persistence.senario.source.Source;
import workbench.wrapper.EdgeWrapper;
import workbench.wrapper.NodeWrapper;

/**
 *
 * @author Meng
 */
public class Workbench {
    
    private DatasetReader reader;
    private Senario senario;
    
    private Map<Node, NodeWrapper> nodeMapping;
    private Map<Edge, EdgeWrapper> edgeMapping;
    
    public Workbench(Senario senario){
        
        this.senario = senario;
        reader = new DatasetReader();
        
        nodeMapping = new HashMap<>();
        edgeMapping = new HashMap<>();
    }
    
    public void run(){
        List<Source> sources = senario.getSources();
        for(Source source : sources){
            List<Map<String, Object>> dataset = reader.read(source.getDataset(), source.getFilename());
            
            startChain(source.getNode(), dataset);
            
        }
    }
    
    private void startChain(Node node, List<Map<String, Object>> data){
        NodeWrapper nodeWrapper = getOrCreateNodeWrapper(node);
        nodeWrapper.process(data);
        
System.out.println(node.getName()+" Processing Result: ");      
for(Map<String, Object> entry : nodeWrapper.getOutput()){
    System.out.println(entry); 
}
       
        List<Edge> edges = senario.getFlow().getDownstreamEdges(node);
        
        for(Edge edge : edges){
            EdgeWrapper edgeWrapper = getOrCreateEdgeWrapper(edge);
            edgeWrapper.process(nodeWrapper.getOutput());
            
            if(edgeWrapper.getOutput()){
                startChain(edge.getDownstream(), nodeWrapper.getOutput());
            }

        }
        
    }
    
    private NodeWrapper getOrCreateNodeWrapper(Node node){
        if(nodeMapping.containsKey(node)){
            return nodeMapping.get(node);
        } else{
            NodeWrapper nodeWrapper = new NodeWrapper(node);
            nodeMapping.put(node, nodeWrapper);
            return nodeWrapper;
        }
    }
    
    private EdgeWrapper getOrCreateEdgeWrapper(Edge edge){
        if(edgeMapping.containsKey(edge)){
            return edgeMapping.get(edge);
        } else{
            EdgeWrapper edgeWrapper = new EdgeWrapper(edge);
            edgeMapping.put(edge, edgeWrapper);
            return edgeWrapper;
        }
    }
    
}
