package com.chinaunicom.elemeetingpc.service;

import com.chinaunicom.elemeetingpc.constant.ServeIpConstant;
import com.chinaunicom.elemeetingpc.utils.GsonUtil;
import com.chinaunicom.elemeetingpc.utils.HttpClientUtil;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

/**
 * 负责与后台修改密码接口进行数据交互处理（mUser.do?action=resetPassword），主要包括以下操作： 1、接口请求参数封装；
 * 2、接口请求； 3、接口数据解析.
 *
 * @author zhaojunfeng, chenxi
 */
public class UserInfoServiceController {

    private static final Logger logger = LoggerFactory.getLogger(UserInfoServiceController.class);

    /**
     * 封装参数为json字符串.
     *
     * @param userId
     * @param old_password
     * @param new_password
     * @return resultString
     */
    public String fzParam(String userId, String old_password, String new_password) {
        String resultString = "{userId:'" + userId + "',oldPassword:'" + old_password + "',newPassword:'" + new_password + "'}";
        return resultString;
    }

    /**
     * 解析接口返回的数据.
     *
     * @param resString 接口返回的提示语
     */
    public String resetPassword(String userId, String old_password, String new_password) {
        String resString = "";
        try {
            //封装参数
            String param = this.fzParam(userId, old_password, new_password);
            //访问接口
            String result = HttpClientUtil.getInstance().getResponseBodyAsString(ServeIpConstant.resetPasswordServicePath(), param);
            if (StringUtils.isNotBlank(result)) {
                //把json字符串转成map
                Map temp_map = GsonUtil.getMap(result);
                resString = String.valueOf(temp_map.get("resultDesc"));
            }
        } catch (Exception e) {
            logger.error(e.getCause().getMessage());
            e.printStackTrace();
        }
        return resString;
    }
}
