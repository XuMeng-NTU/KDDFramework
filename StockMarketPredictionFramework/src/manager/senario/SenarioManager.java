/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package manager.senario;

import application.Background;
import database.DatabaseManager;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import persistence.flow.Flow;
import persistence.flow.node.Node;
import persistence.registry.RegistryItem;
import persistence.senario.Senario;
import persistence.senario.source.Source;

/**
 *
 * @author Meng
 */
public class SenarioManager {
    private EntityManager entityManager;
    private DatabaseManager dbManager;

    public SenarioManager(){
        entityManager = Background.getInstance().getEntityManager();
        dbManager = Background.getInstance().getDatabaseManager();
    }    
    
     
    public void createSenario(RegistryItem item, Flow flow){
        Senario senario = new Senario();
        senario.setRegistration(item);
        senario.setFlow(flow);
        
        List<Node> rootNodes = flow.getRootNodes();
        for(Node node : rootNodes){
            Source source = new Source();
            source.setNode(node);
            senario.addSource(source);
        }
        
        entityManager.getTransaction().begin();
        entityManager.persist(senario);
        entityManager.getTransaction().commit();
        
    }        
    
    public Senario getSenario(RegistryItem item){
        Query query = entityManager.createQuery("SELECT senario FROM Senario senario WHERE senario.registration = :registration");
        query.setParameter("registration", item);
        
        if(query.getResultList().isEmpty()){
            return null;
        } else{
            return (Senario) query.getResultList().get(0);
        }   
    }    
    
    public void removeSenario(Senario senario){
        entityManager.getTransaction().begin();
        entityManager.remove(senario);
        entityManager.getTransaction().commit();
    }
    
    public void addSource(Senario senario, Source source){
        entityManager.getTransaction().begin();
        senario.addSource(source);
        source.setSenario(senario);
        entityManager.getTransaction().commit();        
    }
    
    public void saveSource(Source source){
        entityManager.getTransaction().begin();
        entityManager.persist(source);
        entityManager.getTransaction().commit();      
    }
    
    public void removeAttribute(Senario senario, Source source){
        entityManager.getTransaction().begin();
        senario.removeSource(source);
        entityManager.remove(source);
        entityManager.getTransaction().commit();        
    }    
}
