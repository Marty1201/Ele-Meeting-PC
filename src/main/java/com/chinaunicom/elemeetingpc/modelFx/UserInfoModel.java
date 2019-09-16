package com.chinaunicom.elemeetingpc.modelFx;

import com.chinaunicom.elemeetingpc.constant.GlobalStaticConstant;
import com.chinaunicom.elemeetingpc.database.dao.UserInfoDao;
import com.chinaunicom.elemeetingpc.database.models.UserInfo;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides various methods implementation for UserInfo, mainly focus
 * on the logic for the Dao operation.
 *
 * @author chenxi, zhaojunfeng 创建时间：2019-7-4 10:19:20
 */
public class UserInfoModel {

    /**
     * Query existing userInfo from the table by given the loginName & its
     * value.
     *
     * @param loginName
     * @param value
     * @return List<UserInfo>
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
     * @param userInfo
     */
    public void saveOrUpdateUserInfo(UserInfo userInfo) throws ApplicationException {
        UserInfoDao userDao = new UserInfoDao();
        userDao.saveOrUpdate(userInfo);
    }

    /**
     * Delete a list of userInfos from the table.
     *
     * @param userList
     */
    public void deleteAllUserInfos(List<UserInfo> userList) throws ApplicationException {
        UserInfoDao userDao = new UserInfoDao();
        userDao.deleteByCollection(UserInfo.class, userList);
    }

    /**
     * 获取用户的旧密码.
     *
     * @return oldPassword
     */
    public String getOldPassword() throws ApplicationException {
        String oldPassword = null;
        UserInfoDao userDao = new UserInfoDao();
        UserInfo userInfo = userDao.findById(UserInfo.class, GlobalStaticConstant.GLOBAL_USERINFO_ID);
        oldPassword = userInfo.getPassword();
        return oldPassword;
    }
}
