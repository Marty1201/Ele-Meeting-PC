package com.chinaunicom.elemeetingpc.database.dao;

import com.chinaunicom.elemeetingpc.database.models.DictionaryInfo;
import com.chinaunicom.elemeetingpc.utils.FxmlUtils;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.stmt.QueryBuilder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DictionaryInfoDao
 *
 * @author chenxi 创建时间：2019-7-12 18:19:34
 */
public class DictionaryInfoDao extends CommonDao {

    private static final Logger logger = LoggerFactory.getLogger(DictionaryInfoDao.class);

    public DictionaryInfoDao() {
        super();
    }
    
    /**
     * Find field's value by queryBuilder, return a list of DictionaryInfo, 
     * this method is exclusively used in query registerCode value in the
     * dictionaryInfo table.
     *
     * @param fieldName the field name.
     * @return A list of DictionaryInfo.
     * @throws ApplicationException,SQLException.
     */
    public List<DictionaryInfo> findByFieldIsNotNull(String fieldName) throws ApplicationException, SQLException {
        List<DictionaryInfo> dicList = new ArrayList<DictionaryInfo>();
        try {
            Dao<DictionaryInfo, Object> dao = getDao(DictionaryInfo.class);
            QueryBuilder<DictionaryInfo, Object> queryBuilder = dao.queryBuilder();
            queryBuilder.where().isNotNull(fieldName);
            dicList = dao.query(queryBuilder.prepare());
        } catch (SQLException e) {
            logger.warn(e.getCause().getMessage());
            throw new ApplicationException(FxmlUtils.getResourceBundle().getString("error.not.found.all"));
        } finally {
            this.closeDbConnection();
        }
        return dicList;
    }

    /**
     * THIS METHOD IS NO LONGER USED!!!(which has been replaced by findByFieldIsNotNull method)
     * Find field's value given a raw sql, return a list of String[], 
     * this method is exclusively used in get registerCode value in the
     * dictionaryInfo table.
     *
     * @param cls the object class to be queried.
     * @param tableName the name of the table.
     * @param fieldName the field name.
     * @return A list of String[].
     * @throws ApplicationException, SQLException.
     */
    public List<String[]> findByFieldValueNotNull(String fieldName) throws ApplicationException, SQLException {
        List<String[]> list = new ArrayList<String[]>();
        Dao<DictionaryInfo, Object> dao = getDao(DictionaryInfo.class);
        try {
            GenericRawResults<String[]> rawResults = dao.queryRaw(
                    "select " + fieldName + " from dictionaryinfo where " + fieldName + " is not null");
            for (String[] resultArray : rawResults) {
                list.add(resultArray);
            }
            return list;
        } catch (SQLException e) {
            logger.warn(e.getCause().getMessage());
            throw new ApplicationException(FxmlUtils.getResourceBundle().getString("error.not.found.all"));
        } finally {
            this.closeDbConnection();
        }
    }
}
