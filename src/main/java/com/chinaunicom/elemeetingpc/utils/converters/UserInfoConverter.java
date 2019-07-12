
package com.chinaunicom.elemeetingpc.utils.converters;

import com.chinaunicom.elemeetingpc.database.models.UserInfo;
import com.chinaunicom.elemeetingpc.modelFx.UserInfoFx;


/**
 * This class convert an UserInfo object to UserInfoFx object and vice versa.
 * @author chenxi
 * 创建时间：2019-6-28 8:51:48
 */
public class UserInfoConverter {
    
	public UserInfoConverter(){
        
    }
    
    /**
     * Change an UserInfo object to UserInfoFx object.
     * @param userInfo
     * @return userInfoFx
     */
    public static UserInfoFx convertToUserInfoFx(UserInfo userInfo){
        UserInfoFx userInfoFx = new UserInfoFx();
        userInfoFx.setId(userInfo.getId());
        userInfoFx.setLoginName(userInfo.getLoginName());
        userInfoFx.setUserName(userInfo.getUserName());
        userInfoFx.setPassword(userInfo.getPassword());
        userInfoFx.setEnglishName(userInfo.getEnglishName());
        userInfoFx.setPhone(userInfo.getPhone());
        userInfoFx.setState(userInfo.getState());
        userInfoFx.setSexName(userInfo.getSexName());
        userInfoFx.setSexEnglishName(userInfo.getSexEnglishName());
        userInfoFx.setSort(userInfo.getSort());
        return userInfoFx;
    }
    
    /**
     * Change an UserInfoFx object to UserInfo object.
     * @param userInfoFx
     * @return userInfo
     */
    public static UserInfo convertToUserInfo(UserInfoFx userInfoFx) {
        UserInfo userInfo = new UserInfo();
        userInfo.setId(userInfoFx.getId());
        userInfo.setLoginName(userInfoFx.getLoginName());
        userInfo.setUserName(userInfoFx.getUserName());
        userInfo.setPassword(userInfoFx.getPassword());
        userInfo.setEnglishName(userInfoFx.getEnglishName());
        userInfo.setPhone(userInfoFx.getPhone());
        userInfo.setState(userInfoFx.getState());
        userInfo.setSexName(userInfoFx.getSexName());
        userInfo.setSexEnglishName(userInfoFx.getSexEnglishName());
        userInfo.setSort(userInfoFx.getSort());
        return userInfo;
    }
}
