/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package manager.component;

import application.Background;
import database.DatabaseManager;
import enumeration.component.ComponentType;
import implementation.ImplementationManager;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import persistence.component.Component;
import persistence.registry.RegistryItem;

/**
 *
 * @author Meng
 */
public class ComponentsManager {

    private EntityManager entityManager;
    private DatabaseManager dbManager;
    private ImplementationManager implManager;

    public ComponentsManager(){
        entityManager = Background.getInstance().getEntityManager();
        dbManager = Background.getInstance().getDatabaseManager();
        implManager = Background.getInstance().getImplementationManager();
    }
    
    public Component getComponent(RegistryItem item){
        Query query = entityManager.createQuery("SELECT component FROM Component component WHERE component.registration = :registration");
        query.setParameter("registration", item);
        
        if(query.getResultList().isEmpty()){
            return null;
        } else{
            return (Component) query.getResultList().get(0);
        }
        
    }
    
//    public List<Component> getAllComponents(){
//        Query query = entityManager.createQuery("SELECT component FROM Component component");
//        
//        List<Component> result = new ArrayList();
//        result.addAll(query.getResultList());
//        
//        return result;
//    }
    
    public List<Component> getComponentsByType(ComponentType type){
        Query query = entityManager.createQuery("SELECT component FROM Component component WHERE component.type = :type");
        query.setParameter("type", type);
        
        List<Component> result = new ArrayList();
        result.addAll(query.getResultList());
        
        return result;        
    }
    
    public void createComponentFromClass(RegistryItem item, Class provider){
        Component component = implManager.createComponentFromClass(provider);
        component.setRegistration(item);
        
        entityManager.getTransaction().begin();
        entityManager.persist(component);
        entityManager.getTransaction().commit();
        
    }
    
    public List<Class> scanForComponent(){
        return implManager.scanForComponent();
    }
    
    public void removeComponent(Component component){
        entityManager.getTransaction().begin();
        entityManager.remove(component);
        entityManager.getTransaction().commit();
    }
    
}
