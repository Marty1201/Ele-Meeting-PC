
package com.chinaunicom.elemeetingpc.modelFx;

import com.chinaunicom.elemeetingpc.constant.GlobalStaticConstant;
import com.chinaunicom.elemeetingpc.database.dao.UserInfoDao;
import com.chinaunicom.elemeetingpc.database.models.UserInfo;
import com.chinaunicom.elemeetingpc.service.UserInfoService;
import com.chinaunicom.elemeetingpc.utils.HashUtil;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * This class provides various methods implementation for UserInfo, mainly
 * focus on the logic for the Dao operation.
 * @author chenxi, zhaojunfeng
 * 创建时间：2019-7-4 10:19:20
 */
public class UserInfoModel {
    
    /**
     * Query existing userInfo from the table by given the loginName & its value.
     * @param loginName
     * @param value
     * @return List<UserInfo>
     */
    public List<UserInfo> queryUserInfos(String loginName, String value) throws ApplicationException{
        UserInfoDao userDao = new UserInfoDao();
        List<UserInfo> userList = new ArrayList<>();
        userList = userDao.findByFieldNameAndValue(UserInfo.class, loginName, value);
        return userList;
    }
    
    /**
     * Save or update userInfo in to table.
     * @param userInfo
     */
    public void saveOrUpdateUserInfo(UserInfo userInfo) throws ApplicationException{
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
     * 修改密码
     * @param oldPassword
     * @param newPassword 
     */
    public String resetPassword(String oldPassword,String newPassword){
        String infoString="";
        try {            
            UserInfoDao userDao = new UserInfoDao();
            UserInfo userInfo = userDao.findById(UserInfo.class, GlobalStaticConstant.GLOBAL_USERINFO_ID);
            String md5_oldPassword = HashUtil.toMD5(oldPassword);
            //旧密码正确，可以修改为新密码
            if(md5_oldPassword.equals(userInfo.getPassword())){
                String md5_newPassword = HashUtil.toMD5(newPassword);
                UserInfoService service = new UserInfoService();
                infoString = service.resetPassword(GlobalStaticConstant.GLOBAL_ORGANINFO_OWNER_USERID, oldPassword, newPassword);
                
            }else{
                infoString="旧密码不正确！";
            } 
            
        } catch (ApplicationException ex) {
            Logger.getLogger(UserInfoModel.class.getName()).log(Level.SEVERE, null, ex);
            infoString="修改密码发生异常！";
        }
        return infoString;
    }
}
