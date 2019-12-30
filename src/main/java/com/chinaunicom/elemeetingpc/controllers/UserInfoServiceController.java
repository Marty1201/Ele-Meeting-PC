package com.chinaunicom.elemeetingpc.controllers;

import com.chinaunicom.elemeetingpc.constant.ServeIpConstant;
import com.chinaunicom.elemeetingpc.utils.GsonUtil;
import com.chinaunicom.elemeetingpc.utils.HttpClientUtil;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

/**
 * 当用户点击确认修改密码时触发UserInfoServiceController,
 * 此负责与后台修改密码接口进行数据交互处理（mUser.do?action=resetPassword），主要包括以下操作： 1、接口请求参数封装；
 * 2、接口请求； 3、接口数据解析.
 *
 * @author zhaojunfeng, chenxi
 */
public class UserInfoServiceController {

    /**
     * 封装请求参数为json字符串.
     *
     * @param userId 用户id
     * @param oldPassword 老密码
     * @param newPassword 新密码
     * @return requestString 请求主体
     */
    public String buildJsonRequestString(String userId, String oldPassword, String newPassword) {
        String requestString = "{userId:'" + userId + "',oldPassword:'" + oldPassword + "',newPassword:'" + newPassword + "'}";
        return requestString;
    }

    /**
     * 修改密码接口逻辑处理.
     *
     * @param userId 用户id
     * @param oldPassword 老密码
     * @param newPassword 新密码
     * @return resultString 接口返回的提示语
     */
    public String resetPassword(String userId, String oldPassword, String newPassword) throws Exception {
        String resultString = "";
        //封装参数
        String param = buildJsonRequestString(userId, oldPassword, newPassword);
        //访问接口
        String result = HttpClientUtil.getInstance().getResponseBodyAsString(ServeIpConstant.resetPasswordServicePath(), param);
        if (StringUtils.isNotBlank(result)) {
            //把json字符串转成map
            Map temp_map = GsonUtil.getMap(result);
            resultString = String.valueOf(temp_map.get("resultDesc"));
        }
        return resultString;
    }
}
