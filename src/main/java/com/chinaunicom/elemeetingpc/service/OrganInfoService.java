package com.chinaunicom.elemeetingpc.service;

import com.chinaunicom.elemeetingpc.constant.GlobalStaticConstant;
import com.chinaunicom.elemeetingpc.database.dao.OrganInfoDao;
import com.chinaunicom.elemeetingpc.database.models.OrganInfo;
import com.chinaunicom.elemeetingpc.modelFx.OrganInfoFx;
import com.chinaunicom.elemeetingpc.utils.converters.OrganInfoConverter;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * The OrganInfoService class serves as a service layer between Controller and
 * Dao, it provides variouse database operation methods on the OrganInfoService
 * table.
 *
 * @author chenxi 创建时间：2019-6-26 14:56:15
 */
public class OrganInfoService {

    private ObservableList<OrganInfoFx> organInfoNameObservableList = FXCollections.observableArrayList();

    private List<OrganInfoFx> organInfoNameList = new ArrayList<>();

    /**
     * Initialize the OrganInfoFxsObservableList and populate with OrganInfoFx
     * objects.
     *
     * @throws ApplicationException.
     */
    public void init() throws ApplicationException {
        OrganInfoDao organInfoDao = new OrganInfoDao();
        //query organizations by userId
        List<OrganInfo> OrganInfoList = organInfoDao.findByFieldNameAndValue(OrganInfo.class, "USERINFO_ID", GlobalStaticConstant.GLOBAL_USERINFO_ID);
        organInfoNameList.clear();
        OrganInfoList.forEach(organInfo -> {
            this.organInfoNameList.add(OrganInfoConverter.convertToOrganInfoFx(organInfo));
        });
        organInfoNameObservableList.setAll(organInfoNameList);
    }

    /**
     * Return a OrganInfoFxsObservableList which contains OrganInfoFx objects.
     *
     * @return organInfoFxsObservableList not null
     */
    public ObservableList<OrganInfoFx> getOrganInfoNameObservableList() {
        return organInfoNameObservableList;
    }

    /**
     * Save or update organInfo in to table.
     *
     * @param organInfo an OrganInfo object not null
     * @throws ApplicationException
     */
    public void saveOrUpdateOrganInfo(OrganInfo organInfo) throws ApplicationException {
        OrganInfoDao organDao = new OrganInfoDao();
        organDao.saveOrUpdate(organInfo);
    }

    /**
     * Query existing organInfo from the table by given the organizationId & its
     * value.
     *
     * @param organizationId the organizationId not null
     * @param value the value field nit null
     * @return A list of OrganInfo
     * @throws ApplicationException
     */
    public List<OrganInfo> queryOrganInfosByOrganId(String organizationId, String value) throws ApplicationException {
        OrganInfoDao organDao = new OrganInfoDao();
        List<OrganInfo> organList = new ArrayList<>();
        organList = organDao.findByFieldNameAndValue(OrganInfo.class, organizationId, value);
        return organList;
    }

    /**
     * Query existing organInfo object from the table by given a map of
     * organizationId and userId & its value.
     *
     * @param organizationId not null
     * @param userId not null
     * @return a list of OrganInfo
     * @throws ApplicationException
     */
    public List<OrganInfo> queryOrganInfosByMap(String organizationId, String userId) throws ApplicationException {
        OrganInfoDao organDao = new OrganInfoDao();
        List<OrganInfo> organList = new ArrayList<>();
        Map queryMap = new HashMap<>();
        queryMap.put("organizationId", organizationId);
        queryMap.put("userId", userId);
        organList = organDao.findByFieldNamesAndValues(OrganInfo.class, queryMap);
        return organList;
    }

    /**
     * Query existing organInfo from the table by given the userId & its value.
     *
     * @param userId user's id not null
     * @param value the value not null
     * @return a list of OrganInfo
     * @throws ApplicationException
     */
    public List<OrganInfo> queryOrganInfosByUserId(String userId, int value) throws ApplicationException {
        OrganInfoDao organDao = new OrganInfoDao();
        List<OrganInfo> organList = new ArrayList<>();
        organList = organDao.findByFieldNameAndValue(OrganInfo.class, userId, value);
        return organList;
    }

    /**
     * Delete a list of organInfos from the table.
     *
     * @param organList a list of OrganInfo not null
     * @throws ApplicationException
     */
    public void deleteAllOrganInfos(List<OrganInfo> organList) throws ApplicationException {
        OrganInfoDao organInfoDao = new OrganInfoDao();
        organInfoDao.deleteByCollection(OrganInfo.class, organList);
    }
}
