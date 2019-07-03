package com.chinaunicom.elemeetingpc.modelFx;

import com.chinaunicom.elemeetingpc.constant.GlobalStaticConstant;
import com.chinaunicom.elemeetingpc.database.dao.OrganInfoDao;
import com.chinaunicom.elemeetingpc.database.dao.UserInfoDao;
import com.chinaunicom.elemeetingpc.database.models.OrganInfo;
import com.chinaunicom.elemeetingpc.database.models.UserInfo;
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
        //add some sample data
        //addSampleData();
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

    // to be deleted
    public void addSampleData() throws ApplicationException {
        //add a user
        UserInfoDao userInfoDao = new UserInfoDao();
        UserInfo userInfo = new UserInfo();
        userInfo.setLoginName("changlu");
        userInfo.setUserName("常璐");
        userInfo.setPassword("757f7fc9ad2ec1a2951fbf3a7bbc2144");
        userInfo.setEnglishName("");
        userInfo.setPhone("");
        userInfo.setState("0");
        userInfo.setSexName("女");
        userInfo.setSexEnglishName("Female");
        userInfo.setSort("65");
        userInfo.setUpdateDate("2019-06-28 09:03:56");
        userInfoDao.save(userInfo);
        System.out.println("finish adding user");
        //add organ
        OrganInfoDao organInfoDao = new OrganInfoDao();
        OrganInfo companyA = new OrganInfo();
        companyA.setOrganizationName("联通国际");
        companyA.setUserId("20190110142510922523284526669768");
        companyA.setState("0");
        companyA.setOrganizationId("20190109175459084818173570055782");
        companyA.setUserInfo(userInfo);
        organInfoDao.save(companyA);
        System.out.println("finish adding companyA");

        OrganInfo companyB = new OrganInfo();
        companyB.setOrganizationName("联通A股");
        companyB.setUserId("20190610100440876451901291839127");
        companyB.setState("0");
        companyB.setOrganizationId("20170526152646214211565279562682");
        companyB.setUserInfo(userInfo);
        organInfoDao.save(companyB);
        System.out.println("finish adding companyB");
        
    }
}
