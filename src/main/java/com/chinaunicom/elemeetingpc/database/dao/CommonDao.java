package com.chinaunicom.elemeetingpc.database.dao;

import com.chinaunicom.elemeetingpc.database.dutils.DbManager;
import com.chinaunicom.elemeetingpc.database.models.BaseModel;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.support.ConnectionSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Provide common methods for database manipulation, all Dao methods are
 * encapsulated again.
 *
 * @author chenxi 创建时间：2019-6-18 17:57:16
 */
public abstract class CommonDao {

    private static final Logger logger = LoggerFactory.getLogger(CommonDao.class);

    protected final ConnectionSource connectionSource;

    public CommonDao() {
        //construct a connection source to our database
        this.connectionSource = DbManager.getConnectionSource();
    }

    /**
     * Create DAO a class which extend and implement BaseModel + Dao
     *
     * @param cls the class which is needed to be persisting with the DAO
     * @return a Dao class
     * @throws ApplicationException
     */
    public <T extends BaseModel, I> Dao<T, I> getDao(Class<T> cls) throws ApplicationException {
        try {
            return DaoManager.createDao(connectionSource, cls);
        } catch (SQLException e) {
            logger.warn(e.getCause().getMessage());
            // to do internationalize
            //throw new ApplicationException(FxmlUtils.getResourceBundle().getString("error.get.dao"));
            throw new ApplicationException("Error with database");
        } finally {
            this.closeDbConnection();
        }
    }

    /**
     * Creating an item in the database.
     *
     * @param baseModel the object class
     * @throws ApplicationException
     */
    public <T extends BaseModel, I> void createOrUpdate(BaseModel baseModel) throws ApplicationException {
        try {
            Dao<T, I> dao = getDao((Class<T>) baseModel.getClass());
            dao.createOrUpdate((T) baseModel);
        } catch (SQLException e) {
            logger.warn(e.getCause().getMessage());
            // to do internationalize
            //throw new ApplicationException(FxmlUtils.getResourceBundle().getString("error.get.dao"));
            throw new ApplicationException("Error with database");
        } finally {
            this.closeDbConnection();
        }
    }

    /**
     * Refresh the class field values and bring them up-to-date.
     *
     * @param baseModel the object class
     * @throws ApplicationException
     */
    public <T extends BaseModel, I> void refresh(BaseModel baseModel) throws ApplicationException {
        try {
            Dao<T, I> dao = getDao((Class<T>) baseModel.getClass());
            dao.refresh((T) baseModel);
        } catch (SQLException e) {
            logger.warn(e.getCause().getMessage());
            // to do internationalize
            //throw new ApplicationException(FxmlUtils.getResourceBundle().getString("error.get.dao"));
            throw new ApplicationException("Error with database");
        } finally {
            this.closeDbConnection();
        }
    }

    /**
     * Delete an entry item from the database.
     *
     * @param baseModel the object class
     * @throws ApplicationException
     */
    public <T extends BaseModel, I> void delete(BaseModel baseModel) throws ApplicationException {
        try {
            Dao<T, I> dao = getDao((Class<T>) baseModel.getClass());
            dao.delete((T) baseModel);
        } catch (SQLException e) {
            logger.warn(e.getCause().getMessage());
            // to do internationalize
            //throw new ApplicationException(FxmlUtils.getResourceBundle().getString("error.get.dao"));
            throw new ApplicationException("Error with database");
        } finally {
            this.closeDbConnection();
        }
    }

    /**
     * Delete an entry item from the database by id.
     *
     * @param cls the object class
     * @param id the id of the item
     * @throws ApplicationException
     */
    public <T extends BaseModel, I> void deleteById(Class<T> cls, Integer id) throws ApplicationException {
        try {
            Dao<T, I> dao = getDao(cls);
            dao.deleteById((I) id);
        } catch (SQLException e) {
            logger.warn(e.getCause().getMessage());
            // to do internationalize
            //throw new ApplicationException(FxmlUtils.getResourceBundle().getString("error.get.dao"));
            throw new ApplicationException("Error with database");
        } finally {
            this.closeDbConnection();
        }
    }

    /**
     * Find an entry item from the database by id.
     *
     * @param cls the object class
     * @param id the id of the item
     * @throws ApplicationException
     */
    public <T extends BaseModel, I> T findById(Class<T> cls, Integer id) throws ApplicationException {
        try {
            Dao<T, I> dao = getDao(cls);
            return dao.queryForId((I) id);
        } catch (SQLException e) {
            logger.warn(e.getCause().getMessage());
            // to do internationalize
            //throw new ApplicationException(FxmlUtils.getResourceBundle().getString("error.get.dao"));
            throw new ApplicationException("Error with database");
        } finally {
            this.closeDbConnection();
        }
    }
    
    /**
     * Find all entry items in the object table from the database.
     *
     * @param cls the object class
     * @throws ApplicationException
     */
    public <T extends BaseModel, I> List<T> findAll(Class<T> cls) throws ApplicationException {
        try{
            Dao<T,I> dao = getDao(cls);
            return dao.queryForAll();
        } catch(SQLException e){
            logger.warn(e.getCause().getMessage());
            // to do internationalize
            //throw new ApplicationException(FxmlUtils.getResourceBundle().getString("error.get.dao"));
            throw new ApplicationException("Error with database");
        } finally {
            this.closeDbConnection();
        }
    }

    /**
     * Close the connection source
     *
     * @throws ApplicationException
     */
    public void closeDbConnection() throws ApplicationException {
        try {
            this.connectionSource.close();
        } catch (IOException e) {
            logger.warn(e.getCause().getMessage());
            // to do internationalize
            //throw new ApplicationException(FxmlUtils.getResourceBundle().getString("error.get.dao"));
            throw new ApplicationException("Error with database");
        }
    }
}
