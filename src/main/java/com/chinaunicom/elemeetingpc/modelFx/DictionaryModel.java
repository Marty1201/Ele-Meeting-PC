
package com.chinaunicom.elemeetingpc.modelFx;

import com.chinaunicom.elemeetingpc.database.dao.DictionaryInfoDao;
import com.chinaunicom.elemeetingpc.database.models.DictionaryInfo;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;


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
}
