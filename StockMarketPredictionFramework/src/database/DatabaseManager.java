/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package database;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Meng
 */
public class DatabaseManager {
    private Connection connection;
    private Properties config;
    
    public static final String DB_CONFIG = "settings/database/config.properties";
    
    public DatabaseManager(){
        try {
            
            config = new Properties();
            config.load(new FileInputStream(DB_CONFIG));
            
            Properties connectionProps = new Properties();
            connectionProps.put("user", config.getProperty("user"));
            connectionProps.put("password", config.getProperty("password"));

            connection = DriverManager.getConnection("jdbc"+":"+config.getProperty("engine")+":"+"//"+config.getProperty("host")+":"+config.getProperty("port")+"/"+config.getProperty("database"),connectionProps);
            
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void saveTable(String tableName) {
        try {
            
            Files.deleteIfExists(Paths.get(config.getProperty("repository")+tableName+config.getProperty("postfix")));
            
            tableName = tableName.toUpperCase();
            
            PreparedStatement sql = connection.prepareStatement(
                    "CALL SYSCS_UTIL.SYSCS_EXPORT_TABLE (?,?,?,?,?,?)");
            sql.setString(1, null);
            sql.setString(2, tableName);
            sql.setString(3, config.getProperty("repository")+tableName+config.getProperty("postfix"));
            sql.setString(4, "%");
            sql.setString(5, null);
            sql.setString(6, null);
            sql.execute();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void loadTable(String tableName){
        
        tableName = tableName.toUpperCase();
        
        try {
            PreparedStatement sql = connection.prepareStatement(
                    "CALL SYSCS_UTIL.SYSCS_IMPORT_TABLE (?,?,?,?,?,?,?)");
            sql.setString(1, null);
            sql.setString(2, tableName);
            sql.setString(3, config.getProperty("repository")+tableName+config.getProperty("postfix"));
            sql.setString(4, "%");
            sql.setString(5, null);
            sql.setString(6, null);
            sql.setInt(7, 1);
            sql.execute();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
//    public void initTable(String tableName) {
//        
//        tableName = tableName.toUpperCase();
//        
//        if (tableName.equals("CATEGORY")) {
//            try {
//                PreparedStatement sql = connection.prepareStatement(
//                        "DELETE FROM " + tableName);
//                
//                sql.execute();
//                
//                sql = connection.prepareStatement(
//                        "ALTER TABLE " + tableName + " ALTER COLUMN ID RESTART WITH 1");
//                
//                sql.execute();
//                
//                sql = connection.prepareStatement(
//                        "INSERT INTO " + tableName + " (ID, NAME, PARENTCATEGORY_ID) VALUES (?,?,?)");
//                sql.setInt(1, 1);
//                sql.setString(2, "Stock Market Prediction Framework");
//                sql.setObject(3, null);
//                sql.execute();               
//            } catch (SQLException ex) {
//                Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//    }
    
    public void close(){
        try {
            connection.close();
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseManager.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
