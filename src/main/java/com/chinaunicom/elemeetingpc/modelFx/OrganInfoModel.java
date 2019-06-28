
package com.chinaunicom.elemeetingpc.modelFx;

import com.chinaunicom.elemeetingpc.database.dao.OrganInfoDao;
import com.chinaunicom.elemeetingpc.database.models.OrganInfo;
import com.chinaunicom.elemeetingpc.utils.converters.OrganInfoConverter;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


/**
 * This class provides various methods implementation for OrganInfo, mainly
 * focus on the logic for the Dao and JavaFX object operation. 
 * @author chenxi
 * 创建时间：2019-6-26 14:56:15
 */
public class OrganInfoModel {
    
    private ObservableList<OrganInfoFx> organInfoFxsObservableList = FXCollections.observableArrayList();
    Integer userId = 1;//to do, 应动态获取当前登录用户的id
    
    private List<OrganInfoFx> organInfoFxList = new ArrayList<>();
    
    /**
     * Initialize the OrganInfoFxsObservableList and populate with OrganInfoFx objects.
     * @throws ApplicationException.
     */
    public void init() throws ApplicationException{
        OrganInfoDao organInfoDao = new OrganInfoDao();
        //根据用户id从数据库里查到对应的组织机构信息
        List<OrganInfo> OrganInfoList = organInfoDao.findByFieldNameAndValue(OrganInfo.class, "USERINFO_ID", userId);
        organInfoFxList.clear();
        OrganInfoList.forEach(organInfo->{
            this.organInfoFxList.add(OrganInfoConverter.convertToOrganInfoFx(organInfo));
        });
        organInfoFxsObservableList.setAll(organInfoFxList);
    }
    
    /**
     * Return a OrganInfoFxsObservableList which contains OrganInfoFx objects.
     * @return organInfoFxsObservableLis.
     */
    public ObservableList<OrganInfoFx> getOrganInfoFxsObservableList(){
        return organInfoFxsObservableList;
    }
}
