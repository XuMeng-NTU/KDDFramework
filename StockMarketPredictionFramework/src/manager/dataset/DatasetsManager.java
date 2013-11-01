/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package manager.dataset;

import application.Background;
import database.DatabaseManager;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import persistence.dataset.Dataset;
import persistence.dataset.attribute.Attribute;
import persistence.registry.RegistryItem;

/**
 *
 * @author Meng
 */
public class DatasetsManager {
    private EntityManager entityManager;
    private DatabaseManager dbManager;

    public DatasetsManager(){
        entityManager = Background.getInstance().getEntityManager();
        dbManager = Background.getInstance().getDatabaseManager();
    }    
    
     
    public void createDataset(RegistryItem item){
        Dataset dataset = new Dataset();
        dataset.setRegistration(item);
        
        entityManager.getTransaction().begin();
        entityManager.persist(dataset);
        entityManager.getTransaction().commit();
        
    }        
    
    public Dataset getDataset(RegistryItem item){
        Query query = entityManager.createQuery("SELECT dataset FROM Dataset dataset WHERE dataset.registration = :registration");
        query.setParameter("registration", item);
        
        if(query.getResultList().isEmpty()){
            return null;
        } else{
            return (Dataset) query.getResultList().get(0);
        }   
    }    
    
    public List<Dataset> getAllDatasets(){
        Query query = entityManager.createQuery("SELECT dataset FROM Dataset dataset");
        
        List<Dataset> result = new ArrayList();
        result.addAll(query.getResultList());
        
        return result;
    }
    
    public void removeDataset(Dataset dataset){
        entityManager.getTransaction().begin();
        entityManager.remove(dataset);
        entityManager.getTransaction().commit();
    }
    
    public void addAttribute(Dataset dataset, Attribute attribute){
        entityManager.getTransaction().begin();
        dataset.addAttribute(attribute);
        entityManager.getTransaction().commit();        
    }
    
    public void saveAttribute(Attribute attribute){
        entityManager.getTransaction().begin();
        entityManager.persist(attribute);
        entityManager.getTransaction().commit();      
    }
    
    public void removeAttribute(Dataset dataset, Attribute attribute){
        entityManager.getTransaction().begin();
        dataset.removeAttribute(attribute);
        entityManager.remove(attribute);
        entityManager.getTransaction().commit();        
    }    
}
