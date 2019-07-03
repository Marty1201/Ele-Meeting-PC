package com.chinaunicom.elemeetingpc.modelFx;

import com.chinaunicom.elemeetingpc.constant.GlobalStaticConstant;
import com.chinaunicom.elemeetingpc.database.dao.OrganInfoDao;
import com.chinaunicom.elemeetingpc.database.models.OrganInfo;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * This class provides various methods implementation for OrganInfo, mainly
 * focus on the logic for the Dao operation.
 *
 * @author chenxi 创建时间：2019-6-26 14:56:15
 */
public class OrganInfoModel {
    
    String userId = GlobalStaticConstant.SESSION_USERINFO_ID;//to do, 应动态获取当前登录用户的id
    private ObservableList<String> organInfoNameObservableList = FXCollections.observableArrayList();
    private List<String> organInfoNameList = new ArrayList<>();

    /**
     * Initialize the OrganInfoFxsObservableList and populate with String
     * objects.
     *
     * @throws ApplicationException.
     */
    public void init() throws ApplicationException {
        OrganInfoDao organInfoDao = new OrganInfoDao();
        //根据用户id从数据库里查到对应的组织机构信息
        List<OrganInfo> OrganInfoList = organInfoDao.findByFieldNameAndValue(OrganInfo.class, "USERINFO_ID", userId);
        organInfoNameList.clear();
        OrganInfoList.forEach(organInfo -> {
            this.organInfoNameList.add(organInfo.getOrganizationName());
        });
        organInfoNameObservableList.setAll(organInfoNameList);
    }

    /**
     * Return a OrganInfoFxsObservableList which contains String objects.
     *
     * @return organInfoFxsObservableLis.
     */
    public ObservableList<String> getOrganInfoNameObservableList() {
        return organInfoNameObservableList;
    }
}
