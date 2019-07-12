
package com.chinaunicom.elemeetingpc.modelFx;

import com.chinaunicom.elemeetingpc.database.dao.IdentityInfoDao;
import com.chinaunicom.elemeetingpc.database.models.IdentityInfo;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;
import java.util.ArrayList;
import java.util.List;


/**
 * This class provides various methods implementation for IdentityInfo, mainly
 * focus on the logic for the Dao operation.
 * @author chenxi
 * 创建时间：2019-7-9 11:36:27
 */
public class IdentityInfoModel {
    
    /**
     * Query existing identityInfo from the table by given the ORGANINFOR_ID & its
     * value.
     *
     * @param ORGANINFOR_ID
     * @param value
     * @return List<IdentityInfo>
     */
    public List<IdentityInfo> queryIdentityInfos(String ORGANINFOR_ID, int value) throws ApplicationException {
        IdentityInfoDao identityDao = new IdentityInfoDao();
        List<IdentityInfo> identityList = new ArrayList<>();
        identityList = identityDao.findByFieldNameAndValue(IdentityInfo.class, ORGANINFOR_ID, value);
        return identityList;
    }
    
    /**
     * Save or update userInfo in to table.
     *
     * @param identityInfo
     */
    public void saveOrUpdateIdentityInfo(IdentityInfo identityInfo) throws ApplicationException {
        IdentityInfoDao identityDao = new IdentityInfoDao();
        identityDao.saveOrUpdate(identityInfo);
    }
    
    /**
     * Delete a list of identityInfos from the table.
     *
     * @param identityList
     */
    public void deleteAllIdentityInfos(List<IdentityInfo> identityList) throws ApplicationException {
        IdentityInfoDao identityDao = new IdentityInfoDao();
        identityDao.deleteByCollection(IdentityInfo.class, identityList);
    }
}
