package com.chinaunicom.elemeetingpc.database.dao;

import com.chinaunicom.elemeetingpc.database.dutils.DbManager;
import com.chinaunicom.elemeetingpc.database.models.BaseModel;
import com.chinaunicom.elemeetingpc.utils.FxmlUtils;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.support.ConnectionSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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
     * Create a DAO class which extend and implement BaseModel + Dao.
     *
     * @param cls the class which is needed to be persisting with the DAO.
     * @return a Dao class.
     * @throws ApplicationException.
     */
    public <T extends BaseModel, I> Dao<T, I> getDao(Class<T> cls) throws ApplicationException {
        try {
            return DaoManager.createDao(connectionSource, cls);
        } catch (SQLException e) {
            logger.warn(e.getCause().getMessage());
            throw new ApplicationException(FxmlUtils.getResourceBundle().getString("error.get.dao"));
        } finally {
            this.closeDbConnection();
        }
    }
    
    /**
     * Creating an item in the table.
     *
     * @param baseModel the object class to be saved in the table.
     * @throws ApplicationException.
     */
    public <T extends BaseModel, I> void save(BaseModel baseModel) throws ApplicationException {
        try {
            Dao<T, I> dao = getDao((Class<T>) baseModel.getClass());
            dao.create((T) baseModel);
        } catch (SQLException e) {
            logger.warn(e.getCause().getMessage());
            throw new ApplicationException(FxmlUtils.getResourceBundle().getString("error.create.update"));
        } finally {
            this.closeDbConnection();
        }
    }

    /**
     * Creating or update an item in the table depends on the existence
     * of the item in the table, the ID is extracted from the parameter
     * (Object's field) and is used to query the table.
     * 
     * @param baseModel the object class to be saved in the table.
     * @throws ApplicationException.
     */
    public <T extends BaseModel, I> void saveOrUpdate(BaseModel baseModel) throws ApplicationException {
        try {
            Dao<T, I> dao = getDao((Class<T>) baseModel.getClass());
            dao.createOrUpdate((T) baseModel);
        } catch (SQLException e) {
            logger.warn(e.getCause().getMessage());
            throw new ApplicationException(FxmlUtils.getResourceBundle().getString("error.create.update"));
        } finally {
            this.closeDbConnection();
        }
    }

    /**
     * Refresh the class field values and bring them up-to-date.
     *
     * @param baseModel the object class to be refreshed.
     * @throws ApplicationException.
     */
    public <T extends BaseModel, I> void refresh(BaseModel baseModel) throws ApplicationException {
        try {
            Dao<T, I> dao = getDao((Class<T>) baseModel.getClass());
            dao.refresh((T) baseModel);
        } catch (SQLException e) {
            logger.warn(e.getCause().getMessage());
            throw new ApplicationException(FxmlUtils.getResourceBundle().getString("error.refresh"));
        } finally {
            this.closeDbConnection();
        }
    }

    /**
     * Delete an item from the table which corresponding to the parameter's id.
     *
     * @param baseModel the object class to be deleted.
     * @throws ApplicationException.
     */
    public <T extends BaseModel, I> void delete(BaseModel baseModel) throws ApplicationException {
        try {
            Dao<T, I> dao = getDao((Class<T>) baseModel.getClass());
            dao.delete((T) baseModel);
        } catch (SQLException e) {
            logger.warn(e.getCause().getMessage());
            throw new ApplicationException(FxmlUtils.getResourceBundle().getString("error.delete"));
        } finally {
            this.closeDbConnection();
        }
    }

    /**
     * Delete an item from the table by id.
     *
     * @param cls the object class to be deleted.
     * @param id the id of the item.
     * @throws ApplicationException.
     */
    public <T extends BaseModel, I> void deleteById(Class<T> cls, Integer id) throws ApplicationException {
        try {
            Dao<T, I> dao = getDao(cls);
            dao.deleteById((I) id);
        } catch (SQLException e) {
            logger.warn(e.getCause().getMessage());
            throw new ApplicationException(FxmlUtils.getResourceBundle().getString("error.delete"));
        } finally {
            this.closeDbConnection();
        }
    }
    
    /**
     * Delete a collection of objects from the database.
     *
     * @param cls the object class to be deleted.
     * @param datas the collection of items.
     * @throws ApplicationException.
     */
    public <T extends BaseModel, I> void deleteByCollection(Class<T> cls, Collection<T> datas) throws ApplicationException {
        try {
            Dao<T, I> dao = getDao(cls);
            dao.delete(datas);
        } catch (SQLException e) {
            logger.warn(e.getCause().getMessage());
            throw new ApplicationException(FxmlUtils.getResourceBundle().getString("error.delete"));
        } finally {
            this.closeDbConnection();
        }
    }

    /**
     * Find an item from the table by id.
     *
     * @param cls the object class to be queried.
     * @param id the id of the item.
     * @return A single T object.
     * @throws ApplicationException.
     */
    public <T extends BaseModel, I> T findById(Class<T> cls, Integer id) throws ApplicationException {
        try {
            Dao<T, I> dao = getDao(cls);
            return dao.queryForId((I) id);
        } catch (SQLException e) {
            logger.warn(e.getCause().getMessage());
            throw new ApplicationException(FxmlUtils.getResourceBundle().getString("error.not.found"));
        } finally {
            this.closeDbConnection();
        }
    }
    
    /**
     * Find all items in the object table from the database.
     *
     * @param cls the object class to be queried.
     * @return A list of T objects.
     * @throws ApplicationException.
     */
    public <T extends BaseModel, I> List<T> findAll(Class<T> cls) throws ApplicationException {
        try{
            Dao<T,I> dao = getDao(cls);
            return dao.queryForAll();
        } catch(SQLException e){
            logger.warn(e.getCause().getMessage());
            throw new ApplicationException(FxmlUtils.getResourceBundle().getString("error.not.found.all"));
        } finally {
            this.closeDbConnection();
        }
    }
    
     /**
     * Find all items from the table by given the columName & its value.
     *
     * @param cls the object class to be queried.
     * @param fieldName the column name fo the table.
     * @param value the value of the column name holds.
     * @return A list of T objects.
     * @throws ApplicationException.
     */
    public <T extends BaseModel, I> List<T> findByFieldNameAndValue(Class<T> cls, String fieldName, Object value) throws ApplicationException {
        Dao<T, I> dao = getDao(cls);
        try {
            return dao.queryForEq(fieldName, value);
        } catch (SQLException e) {
            logger.warn(e.getCause().getMessage());
            throw new ApplicationException(FxmlUtils.getResourceBundle().getString("error.not.found.all"));
        } finally {
            this.closeDbConnection();
        }
    }
    
    /**
     * Find all items from the table by given a map of columNames & values.
     *
     * @param cls the object class to be queried.
     * @param fieldValues a map contains all the query values.
     * @return A list of T objects.
     * @throws ApplicationException.
     */
    public <T extends BaseModel, I> List<T> findByFieldNamesAndValues(Class<T> cls, Map<String, Object> fieldValues) throws ApplicationException {
        Dao<T, I> dao = getDao(cls);
        try {
            return dao.queryForFieldValues(fieldValues);
        } catch (SQLException e) {
            logger.warn(e.getCause().getMessage());
            throw new ApplicationException(FxmlUtils.getResourceBundle().getString("error.not.found.all"));
        } finally {
            this.closeDbConnection();
        }
    }

    /**
     * Close the connection source.
     * @throws ApplicationException.
     */
    public void closeDbConnection() throws ApplicationException {
        try {
            this.connectionSource.close();
        } catch (IOException e) {
            logger.warn(e.getCause().getMessage());
            throw new ApplicationException(FxmlUtils.getResourceBundle().getString("error.get.dao"));
        }
    }
}
