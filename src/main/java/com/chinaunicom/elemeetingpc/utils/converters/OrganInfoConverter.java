
package com.chinaunicom.elemeetingpc.utils.converters;

import com.chinaunicom.elemeetingpc.database.models.OrganInfo;
import com.chinaunicom.elemeetingpc.modelFx.OrganInfoFx;


/**
 * This class convert an OrganInfo object to OrganInfoFx object and vice versa.
 * @author chenxi
 * 创建时间：2019-6-27 11:10:14
 */
public class OrganInfoConverter {
    
    public OrganInfoConverter(){
        
    }
    
    /**
     * Change an OrganInfo object to OrganInfoFx object.
     * @param organInfo
     * @return organInfoFx
     */
    public static OrganInfoFx convertToOrganInfoFx(OrganInfo organInfo){
        OrganInfoFx organInfoFx = new OrganInfoFx();
        organInfoFx.setId(organInfo.getId());
        organInfoFx.setUserInfoFx(UserInfoConverter.convertToUserInfoFx(organInfo.getUserInfo()));
        organInfoFx.setOrganizationName(organInfo.getOrganizationName());
        organInfoFx.setOrganizationId(organInfo.getOrganizationId());
        organInfoFx.setOrganizationEnglishName(organInfo.getOrganizationEnglishName());
        organInfoFx.setState(organInfo.getState());
        organInfoFx.setUserId(organInfo.getUserId());
        return organInfoFx;
    }
    
    /**
     * Change an OrganInfoFx object to OrganInfo object.
     * @param organInfoFx
     * @return organInfo
     */
    public static OrganInfo convertToOrganInfo(OrganInfoFx organInfoFx) {
        OrganInfo organInfo = new OrganInfo();
        organInfo.setId(organInfoFx.getId());
        organInfo.setUserInfo(UserInfoConverter.convertToUserInfo(organInfoFx.getUserInfoFx()));
        organInfo.setOrganizationName(organInfoFx.getOrganizationName());
        organInfo.setOrganizationId(organInfoFx.getOrganizationId());
        organInfo.setOrganizationEnglishName(organInfoFx.getOrganizationEnglishName());
        organInfo.setState(organInfoFx.getState());
        organInfo.setUserId(organInfoFx.getUserId());
        return organInfo;
    }
}