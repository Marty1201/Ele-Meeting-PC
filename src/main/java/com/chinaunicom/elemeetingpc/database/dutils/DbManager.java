
package com.chinaunicom.elemeetingpc.database.dutils;

import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.support.ConnectionSource;
import java.io.IOException;
import java.sql.SQLException;


/**
 * Handle ORMLite ConnectionSource configuration, a ConnectionSource 
 * is a factory for connections to the physical SQL database.
 * @author chenxi
 * 创建时间：2019-6-18 16:40:10
 */
public class DbManager {
    private static final Logger logger = LoggerFactory.getLogger(DbManager.class);
    //database URL
    private static final String JDBC_DRIVER_HD = "jdbc:h2:./EleMeetingPCDB";
    //database accout & password
//    public static final String USER = "admin";
//    public static final String PASS = "2w3e$R%T";
    
    private static ConnectionSource connectionSource;
    
    /**
     * Initialize our database.
     */
    public static void initDatabase() {
        createConnectionSource();
        //to do....
        dropTable();
        createTable();
        closeConnectionSource();
    }
    
    /**
     * Create a connection source to our database.
     */
    public static void createConnectionSource(){
        try{
            connectionSource = new JdbcConnectionSource(JDBC_DRIVER_HD);
        } catch(SQLException e){
            logger.warn(e.getMessage());
        }
    }
    
    /**
     * Return the connection source, never null.
     * @return connectionSource
     */
    public static ConnectionSource getConnectionSource(){
        if(connectionSource == null) {
            createConnectionSource();
        }
        return connectionSource;
    }
    
    /**
     * Close the connection source.
     */
    public static void closeConnectionSource(){
        if(connectionSource != null){
            try{
                connectionSource.close();
            } catch(IOException e){
                logger.warn(e.getMessage());
            }
        }
    }
    
    /**
     * to do.
     */
    private static void dropTable(){
        
    }
    
    /**
     * to do.
     */
    private static void createTable(){
        
    }
}
