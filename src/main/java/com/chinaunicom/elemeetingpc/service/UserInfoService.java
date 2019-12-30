package com.chinaunicom.elemeetingpc.service;

import com.chinaunicom.elemeetingpc.constant.GlobalStaticConstant;
import com.chinaunicom.elemeetingpc.database.dao.UserInfoDao;
import com.chinaunicom.elemeetingpc.database.models.UserInfo;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;
import java.util.ArrayList;
import java.util.List;

/**
 * The UserInfoService class serves as a service layer between Controller and
 * Dao, it provides variouse database operation methods on the UserInfo
 * table.
 *
 * @author chenxi, zhaojunfeng 创建时间：2019-7-4 10:19:20
 */
public class UserInfoService {

    /**
     * Query existing userInfo from the table by given the loginName & its
     * value.
     *
     * @param loginName not null
     * @param value not null
     * @return a list of UserInfo obj
     * @throws ApplicationException
     */
    public List<UserInfo> queryUserInfos(String loginName, String value) throws ApplicationException {
        UserInfoDao userDao = new UserInfoDao();
        List<UserInfo> userList = new ArrayList<>();
        userList = userDao.findByFieldNameAndValue(UserInfo.class, loginName, value);
        return userList;
    }

    /**
     * Save or update userInfo in to table.
     *
     * @param userInfo not null
     * @throws ApplicationException
     */
    public void saveOrUpdateUserInfo(UserInfo userInfo) throws ApplicationException {
        UserInfoDao userDao = new UserInfoDao();
        userDao.saveOrUpdate(userInfo);
    }

    /**
     * Delete a list of userInfos from the table.
     *
     * @param userList not null
     * @throws ApplicationException
     */
    public void deleteAllUserInfos(List<UserInfo> userList) throws ApplicationException {
        UserInfoDao userDao = new UserInfoDao();
        userDao.deleteByCollection(UserInfo.class, userList);
    }

    /**
     * 获取用户的旧密码.
     *
     * @return oldPassword
     * @throws ApplicationException
     */
    public String getOldPassword() throws ApplicationException {
        String oldPassword = null;
        UserInfoDao userDao = new UserInfoDao();
        UserInfo userInfo = userDao.findById(UserInfo.class, GlobalStaticConstant.GLOBAL_USERINFO_ID);
        oldPassword = userInfo.getPassword();
        return oldPassword;
    }
}
