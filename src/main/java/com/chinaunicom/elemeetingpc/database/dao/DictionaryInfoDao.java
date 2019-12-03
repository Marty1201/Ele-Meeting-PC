package com.chinaunicom.elemeetingpc.database.dao;

import com.chinaunicom.elemeetingpc.database.models.DictionaryInfo;
import com.chinaunicom.elemeetingpc.utils.FxmlUtils;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.stmt.QueryBuilder;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 * This class provides various methods implementation for DictionaryInfo, mainly
 * focus on the logic for the Dao operation.
 *
 * @author chenxi 创建时间：2019-7-12 18:19:34
 */
public class DictionaryInfoDao extends CommonDao {

    public DictionaryInfoDao() {
        super();
    }

    /**
     * Find field's value by queryBuilder, return a list of DictionaryInfo, this
     * method is exclusively used in query registerCode value in the
     * dictionaryInfo table.
     *
     * @param fieldName the field name.
     * @return A list of DictionaryInfo.
     * @throws ApplicationException
     */
    public List<DictionaryInfo> findByFieldIsNotNull(String fieldName) throws ApplicationException {
        List<DictionaryInfo> dicList = new ArrayList<DictionaryInfo>();
        if (StringUtils.isNotBlank(fieldName)) {
            try {
                Dao<DictionaryInfo, Object> dao = getDao(DictionaryInfo.class);
                QueryBuilder<DictionaryInfo, Object> queryBuilder = dao.queryBuilder();
                queryBuilder.where().isNotNull(fieldName);
                dicList = dao.query(queryBuilder.prepare());
            } catch (Exception e) {
                throw new ApplicationException(FxmlUtils.getResourceBundle().getString("error.findByFieldIsNotNull"));
            } finally {
                this.closeDbConnection();
            }
        }
        return dicList;
    }

    /**
     * THIS METHOD IS NO LONGER USED!!!(which has been replaced by
     * findByFieldIsNotNull method) Find field's value given a raw sql, return a
     * list of String[], this method is exclusively used in get registerCode
     * value in the dictionaryInfo table.
     *
     * @param fieldName the field name.
     * @return A list of String[].
     * @throws ApplicationException
     */
    public List<String[]> findByFieldValueNotNull(String fieldName) throws ApplicationException {
        List<String[]> list = new ArrayList<String[]>();
        if (StringUtils.isNotBlank(fieldName)) {
            Dao<DictionaryInfo, Object> dao = getDao(DictionaryInfo.class);
            try {
                GenericRawResults<String[]> rawResults = dao.queryRaw(
                        "select " + fieldName + " from dictionaryinfo where " + fieldName + " is not null");
                for (String[] resultArray : rawResults) {
                    list.add(resultArray);
                }
                return list;
            } catch (Exception e) {
                throw new ApplicationException(FxmlUtils.getResourceBundle().getString("error.findByFieldValueNotNull"));
            } finally {
                this.closeDbConnection();
            }
        }
        return list;
    }
}
