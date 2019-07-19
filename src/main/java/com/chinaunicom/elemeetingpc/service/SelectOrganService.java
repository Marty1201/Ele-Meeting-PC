
package com.chinaunicom.elemeetingpc.service;

import com.chinaunicom.elemeetingpc.constant.GlobalStaticConstant;
import com.chinaunicom.elemeetingpc.constant.ServeIpConstant;
import com.chinaunicom.elemeetingpc.constant.StatusConstant;
import com.chinaunicom.elemeetingpc.modelFx.FileResourceModel;
import com.chinaunicom.elemeetingpc.modelFx.IssueInfoModel;
import com.chinaunicom.elemeetingpc.modelFx.MeetInfoModel;
import com.chinaunicom.elemeetingpc.utils.GsonUtil;
import com.chinaunicom.elemeetingpc.utils.HashUtil;
import com.chinaunicom.elemeetingpc.utils.HttpClientUtil;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *选择机构业务逻辑service
 * @author zhaojunfeng
 */
public class SelectOrganService {
    
    private MeetInfoModel meetInfoModel;
    
    private IssueInfoModel issueInfoModel;
    
    private FileResourceModel fileResourceModel;
    
    /**
     * 获取远程服务器上的最新会议相关信息
     */
    public void getMeetInfosFromRemote(){        
        try {
            //封装参数
            String param = this.fzParam(GlobalStaticConstant.GLOBAL_ORGANINFO_ORGANIZATIONID, GlobalStaticConstant.GLOBAL_ORGANINFO_OWNER_USERID, "zh_CN", GlobalStaticConstant.GLOBAL_UPDATEDATE);
            //访问接口
            String result = HttpClientUtil.getInstance().getResponseBodyAsString(ServeIpConstant.selectOrganServicePath(), param);
            System.out.println(result);
            //把json字符串转成map
            Map temp_map = GsonUtil.getMap(result);
            String result_code = String.valueOf(temp_map.get("resultCode"));
            String resultDesc = String.valueOf(temp_map.get("resultDesc"));
            if (StatusConstant.RESULT_CODE_SUCCESS.equals(result_code)) {//查询成功
                String resultData = String.valueOf(temp_map.get("resultData"));
                Map dataMap = GsonUtil.getMap(resultData);;
                parseFzDataMap(dataMap);
            }
        } catch (Exception ex) {
            Logger.getLogger(SelectOrganService.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("获取远程服务器上的最新会议相关信息--发生异常");
        }        
    }
    
    /**
     * 封装参数为json字符串
     *
     * @param userId
     * @param organizationId
     * @param locale
     * @param updateDate
     * 示例：{userId:'20190610100440876451901291839127',locale:'zh_CN',organizationId:'20170526152646214211565279562682',updateDate:''}
     * @return
     */
    private String fzParam(String userId, String organizationId, String locale, String updateDate) {
        String resultString = "{userId:'"+userId+"',locale:'zh_CN',organizationId:'"+organizationId+"',updateDate:'"+updateDate+"'}";
        return resultString;
    }
    
    /**
     * 解析数据
     * @param dataMap
     * @throws ApplicationException 
     */
    private void parseFzDataMap(Map dataMap) throws ApplicationException {
        
    }
    
}
