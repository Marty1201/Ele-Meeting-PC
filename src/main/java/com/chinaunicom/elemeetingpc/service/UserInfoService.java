/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chinaunicom.elemeetingpc.service;

import com.chinaunicom.elemeetingpc.constant.ServeIpConstant;
import com.chinaunicom.elemeetingpc.constant.StatusConstant;
import com.chinaunicom.elemeetingpc.utils.GsonUtil;
import com.chinaunicom.elemeetingpc.utils.HashUtil;
import com.chinaunicom.elemeetingpc.utils.HttpClientUtil;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author zhaojunfeng
 */
public class UserInfoService {
    
    public String fzParam(String userId, String old_password, String new_password) {
        String resultString = "{userId:'" + userId + "',oldPassword:'" + old_password + "',newPassword:'"+ new_password +"'}";
        return resultString;
    }
    
    /**
     * 修改密码
     */
    public String resetPassword(String userId, String old_password, String new_password){
        String resString="";
        try {
            //封装参数
            String param = this.fzParam(userId, old_password, new_password);
            //访问接口
            String result = HttpClientUtil.getInstance().getResponseBodyAsString(ServeIpConstant.resetPasswordServicePath(), param);
            if(StringUtils.isNotBlank(result)){
                //把json字符串转成map
                Map temp_map = GsonUtil.getMap(result);
                String result_code = String.valueOf(temp_map.get("resultCode"));
                String resultDesc = String.valueOf(temp_map.get("resultDesc"));
                
                if (StatusConstant.RESULT_CODE_SUCCESS.equals(result_code)) {
                    resString = String.valueOf(temp_map.get("resultData"));
                }else{
                    resString = String.valueOf(temp_map.get("resultData"));
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(UserInfoService.class.getName()).log(Level.SEVERE, null, ex);
            resString = "修改密码时发生异常！";
        }
        
        return resString;
    }
     
    
}
