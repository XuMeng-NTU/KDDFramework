/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package manager.flow;

import application.Background;
import database.DatabaseManager;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import persistence.flow.edge.Edge;
import persistence.flow.Flow;
import persistence.flow.node.Node;
import persistence.registry.RegistryItem;

/**
 *
 * @author Meng
 */
public class FlowsManager {
    private EntityManager entityManager;
    private DatabaseManager dbManager;

    public FlowsManager(){
        entityManager = Background.getInstance().getEntityManager();
        dbManager = Background.getInstance().getDatabaseManager();
    }
     
    public void createFlow(RegistryItem item){
        Flow flow = new Flow();
        flow.setRegistration(item);
        
        entityManager.getTransaction().begin();
        entityManager.persist(flow);
        entityManager.getTransaction().commit();
        
    }    
    public Flow getFlow(RegistryItem item){
        Query query = entityManager.createQuery("SELECT flow FROM Flow flow WHERE flow.registration = :registration");
        query.setParameter("registration", item);
        
        if(query.getResultList().isEmpty()){
            return null;
        } else{
            return (Flow) query.getResultList().get(0);
        }   
    }
    
    public List<Flow> getAllFlows(){
        Query query = entityManager.createQuery("SELECT flow FROM Flow flow");
        
        List<Flow> result = new ArrayList();
        result.addAll(query.getResultList());
        
        return result;
    }
    
    public void removeFlow(Flow flow){
        entityManager.getTransaction().begin();
        entityManager.remove(flow);
        entityManager.getTransaction().commit();
    }

    public void addNode(Flow flow, Node node) {
        entityManager.getTransaction().begin();
        flow.addNode(node);
        entityManager.getTransaction().commit();
    }
    
    public void saveNode(Node node){
        entityManager.getTransaction().begin();
        entityManager.persist(node);
        entityManager.getTransaction().commit();
    }
    
    public void removeNode(Node node){
        entityManager.getTransaction().begin();
        node.getFlow().removeNode(node);
        entityManager.remove(node);
        entityManager.getTransaction().commit();
    }

    public void addEdge(Flow flow, Edge edge) {
        entityManager.getTransaction().begin();
        flow.addEdge(edge);
        entityManager.getTransaction().commit();
    }
    
    public void saveEdge(Edge edge){
        entityManager.getTransaction().begin();
        entityManager.persist(edge);
        entityManager.getTransaction().commit();
    }
    
    public void removeEdge(Edge edge){
        entityManager.getTransaction().begin();
        edge.getFlow().removeEdge(edge);
        entityManager.remove(edge);
        entityManager.getTransaction().commit();
    }    
    
}
