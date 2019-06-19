
package com.chinaunicom.elemeetingpc.database.dao;

import com.chinaunicom.elemeetingpc.database.dutils.DbManager;
import com.chinaunicom.elemeetingpc.database.models.BaseModel;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import java.io.IOException;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Common methods for handling all database operations for persisted 
 * class.
 * @author chenxi
 * 创建时间：2019-6-18 17:57:16
 */
public class CommonDao {
	private static final Logger logger = LoggerFactory.getLogger(CommonDao.class);
    
    protected final ConnectionSource connectionSource;
    
    public CommonDao(){
        this.connectionSource = DbManager.getConnectionSource();
    }
    //to do throws ApplicationException
    public <T extends BaseModel, I> Dao<T, I> getDao(Class<T> cls) throws Exception{
        try{
            return DaoManager.createDao(connectionSource, cls);
        } catch(SQLException e){
            logger.warn(e.getCause().getMessage());
            //internationalize
            //throw new ApplicationException(FxmlUtils.getResourceBundle().getString("error.get.dao"));
        } finally{
            this.closeDbConnection();
        }
    }
    
    // to do throws ApplicationException
    public void closeDbConnection() throws Exception{
        try{
            this.connectionSource.close();
        } catch(IOException e){
            logger.warn(e.getCause().getMessage());
            //throw new ApplicationException(FxmlUtils.getResourceBundle().getString("error.get.dao"));
        }
    }
}
