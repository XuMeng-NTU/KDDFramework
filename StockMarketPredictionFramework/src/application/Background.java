/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package application;

import database.DatabaseManager;
import implementation.ImplementationManager;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author Meng
 */
public class Background {
    
    private EntityManagerFactory entityManagerFactory;
    private EntityManager entityManager;
    private DatabaseManager databaseManager;
    private ImplementationManager implementationManager;
    
    private Background() {
        entityManagerFactory = Persistence.createEntityManagerFactory("PersistenceUnit"); 
        entityManager = entityManagerFactory.createEntityManager();
        databaseManager = new DatabaseManager();
        implementationManager = new ImplementationManager();
    }
    
    public EntityManager getEntityManager(){
        return entityManager;
    }
    
    public DatabaseManager getDatabaseManager(){
        return databaseManager;
    }
    
    public ImplementationManager getImplementationManager(){
        return implementationManager;
    }
    
    public void close(){
        entityManager.close();
        entityManagerFactory.close();
        databaseManager.close();
    }
    
    public static Background getInstance() {
        return BackgroundHolder.INSTANCE;
    }
    
    private static class BackgroundHolder {

        private static final Background INSTANCE = new Background();
    }
}
