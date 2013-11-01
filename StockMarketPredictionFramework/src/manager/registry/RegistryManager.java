/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package manager.registry;

import enumeration.registry.RegistryItemType;
import application.Background;
import database.DatabaseManager;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import persistence.registry.RegistryItem;

/**
 *
 * @author Meng
 */
public class RegistryManager {

    private EntityManager entityManager;
    
    private DatabaseManager dbManager;
    
    public RegistryManager(){
        entityManager = Background.getInstance().getEntityManager();
        dbManager = Background.getInstance().getDatabaseManager();
    }
    
    private void clearRegistry(){
        entityManager.getTransaction().begin();
        entityManager.remove(getRoot());
        entityManager.getTransaction().commit();        
    }
    
    public void initRegistry(){
        clearRegistry();
        establishRoot();
    }
    
    private void establishRoot(){
        
        RegistryItem root = new RegistryItem();
        root.setName("Stock Market Prediction Framework");
        root.setType(RegistryItemType.CATEGORY);
        
        entityManager.getTransaction().begin();
        entityManager.persist(root);
        entityManager.getTransaction().commit();        
    }
    
    public void saveRegistry(){
        dbManager.saveTable(RegistryItem.class.getSimpleName());
    }
    
    public void loadRegistry(){
        clearRegistry();
        dbManager.loadTable(RegistryItem.class.getSimpleName());
    }

    public RegistryItem getRoot(){
        Query query = entityManager.createQuery("SELECT item FROM "+RegistryItem.class.getSimpleName()+" item WHERE item.parent=null");
        if(query.getResultList().isEmpty()){
            establishRoot();
            return getRoot();
        } else{
            return (RegistryItem) query.getResultList().get(0);
        }
    }
    
    public void changeRegistryItemName(RegistryItem target, String name){
        entityManager.getTransaction().begin();
        target.setName(name);
        entityManager.getTransaction().commit();
    }

    public RegistryItem addRegistryItem(RegistryItem parent, String name, RegistryItemType type){
        RegistryItem item = new RegistryItem();
        item.setName(name);
        item.setType(type);
        item.registerParent(parent);
        
        entityManager.getTransaction().begin();
        entityManager.persist(item);
        entityManager.getTransaction().commit();       
        
        return item;
    }

    public void removeRegistryItem(RegistryItem item){
        entityManager.getTransaction().begin();
        entityManager.remove(item);
        entityManager.getTransaction().commit();          
    }
}
