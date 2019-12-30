package com.chinaunicom.elemeetingpc.service;

import com.chinaunicom.elemeetingpc.database.dao.DictionaryInfoDao;
import com.chinaunicom.elemeetingpc.database.models.DictionaryInfo;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;
import java.util.ArrayList;
import java.util.List;

/**
 * The DictionaryInfoService class serves as a service layer between Controller and
 * Dao, it provides variouse database operation methods on the DictionaryInfo
 * table.
 *
 * @author chenxi 创建时间：2019-7-12 18:21:53
 */
public class DictionaryInfoService {
    
    private DictionaryInfoDao dictionaryInfoDao;
    
    public DictionaryInfoService() {
        
        dictionaryInfoDao = new DictionaryInfoDao();
        
    }

    /**
     * Save or update dictionaryInfo in to table.
     *
     * @param dictionaryInfo not null
     * @throws ApplicationException
     */
    public void saveOrUpdateDictionaryInfo(DictionaryInfo dictionaryInfo) throws ApplicationException {
        dictionaryInfoDao.saveOrUpdate(dictionaryInfo);
    }

    /**
     * Query all items in the DictionaryInfo table.
     *
     * @return a list of DictionaryInfo
     * @throws ApplicationException
     */
    public List<DictionaryInfo> queryAllDictionaryInfoItems() throws ApplicationException {
        List<DictionaryInfo> dicList = new ArrayList<>();
        dicList = dictionaryInfoDao.findAll(DictionaryInfo.class);
        return dicList;
    }

    /**
     * Query a DictionaryInfo object from the table by dic id.
     *
     * @param id not null
     * @return a DictionaryInfo
     * @throws ApplicationException
     */
    public DictionaryInfo queryDictionaryInfoById(int id) throws ApplicationException {
        DictionaryInfo dicList = new DictionaryInfo();
        dicList = dictionaryInfoDao.findById(DictionaryInfo.class, id);
        return dicList;
    }

    /**
     * Query the registerCode's value in the DictionaryInfo table.
     *
     * @return result the registerCode
     * @throws ApplicationException
     */
    public String queryRegiCode() throws ApplicationException {
        String result = "";
        List<DictionaryInfo> dicList = new ArrayList<DictionaryInfo>();
        //dicList = dicDao.findByFieldValueNotNull(DictionaryInfo.class, "dictionaryinfo", "registercode");
        dicList = dictionaryInfoDao.findByFieldIsNotNull("registercode");
        if (!dicList.isEmpty()) {
            for (DictionaryInfo dicInfo : dicList) {
                result = dicInfo.getRegisterCode();
            }
        }
        return result;
    }
}