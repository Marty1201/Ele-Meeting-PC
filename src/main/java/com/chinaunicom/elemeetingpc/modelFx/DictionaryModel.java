
package com.chinaunicom.elemeetingpc.modelFx;

import com.chinaunicom.elemeetingpc.database.dao.DictionaryInfoDao;
import com.chinaunicom.elemeetingpc.database.models.DictionaryInfo;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * This class provides various methods implementation for DictionaryInfo, mainly
 * focus on the logic for the Dao operation.
 * @author chenxi
 * 创建时间：2019-7-12 18:21:53
 */
public class DictionaryModel {
    
    /**
     * Save or update dictionaryInfo in to table.
     *
     * @param dictionaryInfo
     */
    public void saveOrUpdateOrganInfo(DictionaryInfo dictionaryInfo) throws ApplicationException {
        DictionaryInfoDao organDao = new DictionaryInfoDao();
        organDao.saveOrUpdate(dictionaryInfo);
    }
    
    /**
     * Query all items in the DictionaryInfo table.
     *
     * @param dicList
     * @return List<DictionaryInfo>
     */
    public List<DictionaryInfo> queryAllDicItems() throws ApplicationException {
        DictionaryInfoDao dicDao = new DictionaryInfoDao();
        List<DictionaryInfo> dicList = new ArrayList<>();
        dicList = dicDao.findAll(DictionaryInfo.class);
        return dicList;
    }
    
        /**
     * Query all items in the DictionaryInfo table.
     *
     * @param dicList
     * @return List<DictionaryInfo>
     */
    public DictionaryInfo queryById(int id) throws ApplicationException {
        DictionaryInfoDao dicDao = new DictionaryInfoDao();
        DictionaryInfo dicList = new DictionaryInfo();
        dicList = dicDao.findById(DictionaryInfo.class, id);
        return dicList;
    }
    
    /**
     * Query the registerCode's value in the DictionaryInfo table.
     *
     * @return result the registerCode
     */
    public String queryByFieldIsNotNull() throws ApplicationException, SQLException {
        String result = "";
        DictionaryInfoDao dicDao = new DictionaryInfoDao();
        List<DictionaryInfo> dicList = new ArrayList<DictionaryInfo>();
        //dicList = dicDao.findByFieldValueNotNull(DictionaryInfo.class, "dictionaryinfo", "registercode");
        dicList = dicDao.findByFieldIsNotNull("registercode");
        if (!dicList.isEmpty()) {
            for (DictionaryInfo dicInfo:dicList) {
                result = dicInfo.getRegisterCode();
            }
        }
        return result;
    }
}
