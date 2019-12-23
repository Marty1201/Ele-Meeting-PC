
package com.chinaunicom.elemeetingpc.service;

import com.chinaunicom.elemeetingpc.database.dao.IdentityInfoDao;
import com.chinaunicom.elemeetingpc.database.models.IdentityInfo;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;
import java.util.ArrayList;
import java.util.List;


/**
 * The IdentityInfoService class serves as a service layer between Controller and
 * Dao, it provides variouse database operation methods on the IdentityInfo
 * table.
 *
 * @author chenxi 创建时间：2019-7-9 11:36:27
 */
public class IdentityInfoService {
    
    private IdentityInfoDao identityInfoDao;
    
    public IdentityInfoService() {
        
        identityInfoDao = new IdentityInfoDao();
        
    }
    
    /**
     * Query existing identityInfo from the table by given the ORGANINFOR_ID & its
     * value.
     *
     * @param ORGANINFOR_ID not null
     * @param value not null
     * @return a list of IdentityInfo
     * @throws ApplicationException
     */
    public List<IdentityInfo> queryIdentityInfos(String ORGANINFOR_ID, int value) throws ApplicationException {
        List<IdentityInfo> identityList = new ArrayList<>();
        identityList = identityInfoDao.findByFieldNameAndValue(IdentityInfo.class, ORGANINFOR_ID, value);
        return identityList;
    }
    
    /**
     * Save or update userInfo in to table.
     *
     * @param identityInfo not null
     * @throws ApplicationException
     */
    public void saveOrUpdateIdentityInfo(IdentityInfo identityInfo) throws ApplicationException {
        identityInfoDao.saveOrUpdate(identityInfo);
    }
    
    /**
     * Delete a list of identityInfos from the table.
     *
     * @param identityList not null
     * @throws ApplicationException
     */
    public void deleteAllIdentityInfos(List<IdentityInfo> identityList) throws ApplicationException {
        identityInfoDao.deleteByCollection(IdentityInfo.class, identityList);
    }
}
