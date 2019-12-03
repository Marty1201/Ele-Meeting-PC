package com.chinaunicom.elemeetingpc.modelFx;

import com.chinaunicom.elemeetingpc.constant.GlobalStaticConstant;
import com.chinaunicom.elemeetingpc.database.dao.OrganInfoDao;
import com.chinaunicom.elemeetingpc.database.models.OrganInfo;
import com.chinaunicom.elemeetingpc.utils.converters.OrganInfoConverter;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * This class provides various methods implementation for OrganInfo, mainly
 * focus on the logic for the Dao operation.
 *
 * @author chenxi 创建时间：2019-6-26 14:56:15
 */
public class OrganInfoModel {
    
    private static final Logger logger = LoggerFactory.getLogger(OrganInfoModel.class);
    
    private ObservableList<OrganInfoFx> organInfoNameObservableList = FXCollections.observableArrayList();
    
    private List<OrganInfoFx> organInfoNameList = new ArrayList<>();

    /**
     * Initialize the OrganInfoFxsObservableList and populate with OrganInfoFx
     * objects.
     *
     * @throws ApplicationException.
     */
    public void init() {
        try {
            OrganInfoDao organInfoDao = new OrganInfoDao();
            //根据用户id从数据库里查到对应的组织机构信息
            List<OrganInfo> OrganInfoList = organInfoDao.findByFieldNameAndValue(OrganInfo.class, "USERINFO_ID", GlobalStaticConstant.GLOBAL_USERINFO_ID);
            organInfoNameList.clear();
            OrganInfoList.forEach(organInfo -> {
                this.organInfoNameList.add(OrganInfoConverter.convertToOrganInfoFx(organInfo));
            });
            organInfoNameObservableList.setAll(organInfoNameList);
        } catch (ApplicationException ex) {
            ex.printStackTrace();
            logger.error(ex.getCause().getMessage());
        }
    }

    /**
     * Return a OrganInfoFxsObservableList which contains OrganInfoFx objects.
     *
     * @return organInfoFxsObservableLis.
     */
    public ObservableList<OrganInfoFx> getOrganInfoNameObservableList() {
        return organInfoNameObservableList;
    }
    
    /**
     * Save or update organInfo in to table.
     *
     * @param organInfo
     */
    public void saveOrUpdateOrganInfo(OrganInfo organInfo) {
        try {
            OrganInfoDao organDao = new OrganInfoDao();
            organDao.saveOrUpdate(organInfo);
        } catch (ApplicationException ex) {
            ex.printStackTrace();
            logger.error(ex.getCause().getMessage());
        }
    }
    
    /**
     * Query existing organInfo from the table by given the organizationId & its
     * value.
     *
     * @param organizationId
     * @param value
     * @return List<OrganInfo>
     */
    public List<OrganInfo> queryOrganInfosByOrganId(String organizationId, String value) {
        OrganInfoDao organDao = new OrganInfoDao();
        List<OrganInfo> organList = new ArrayList<>();
        try {
            organList = organDao.findByFieldNameAndValue(OrganInfo.class, organizationId, value);
        } catch (ApplicationException ex) {
            ex.printStackTrace();
            logger.error(ex.getCause().getMessage());
        }
        return organList;
    }
    
    /**
     * Query existing organInfo from the table by given a map of organizationId 
     * and userId & its value.
     *
     * @param fieldValues
     * @return List<OrganInfo>
     */
    public List<OrganInfo> queryOrganInfosByMap(Map fieldValues) throws ApplicationException {
        OrganInfoDao organDao = new OrganInfoDao();
        List<OrganInfo> organList = new ArrayList<>();
        try {
            organList = organDao.findByFieldNamesAndValues(OrganInfo.class, fieldValues);
        } catch (ApplicationException ex) {
            ex.printStackTrace();
            logger.error(ex.getCause().getMessage());
        }
        return organList;
    }
    
    /**
     * Query existing organInfo from the table by given the userId & its
     * value.
     *
     * @param userId
     * @param value
     * @return List<OrganInfo>
     */
    public List<OrganInfo> queryOrganInfosByUserId(String userId, int value) throws ApplicationException {
        OrganInfoDao organDao = new OrganInfoDao();
        List<OrganInfo> organList = new ArrayList<>();
        try {
            organList = organDao.findByFieldNameAndValue(OrganInfo.class, userId, value);
        } catch (ApplicationException ex) {
            ex.printStackTrace();
            logger.error(ex.getCause().getMessage());
        }
        return organList;
    }
    
    /**
     * Delete a list of organInfos from the table.
     *
     * @param organList
     */
    public void deleteAllOrganInfos(List<OrganInfo> organList) throws ApplicationException {
        OrganInfoDao identityDao = new OrganInfoDao();
        try {
            identityDao.deleteByCollection(OrganInfo.class, organList);
        } catch (ApplicationException ex) {
            ex.printStackTrace();
            logger.error(ex.getCause().getMessage());
        }
    }
}
