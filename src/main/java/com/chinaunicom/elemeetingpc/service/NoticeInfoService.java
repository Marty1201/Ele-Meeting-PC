/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chinaunicom.elemeetingpc.service;

import com.chinaunicom.elemeetingpc.constant.GlobalStaticConstant;
import com.chinaunicom.elemeetingpc.constant.ServeIpConstant;
import com.chinaunicom.elemeetingpc.constant.StatusConstant;
import com.chinaunicom.elemeetingpc.modelFx.NoticeInfoFx;
import com.chinaunicom.elemeetingpc.modelFx.OrganInfoFx;
import com.chinaunicom.elemeetingpc.utils.GsonUtil;
import com.chinaunicom.elemeetingpc.utils.HashUtil;
import com.chinaunicom.elemeetingpc.utils.HttpClientUtil;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author zhaojunfeng
 */
public class NoticeInfoService {
    private ObservableList<NoticeInfoFx> noticeInfoListObservableList = FXCollections.observableArrayList();
    private List<NoticeInfoFx> noticeInfoFxList = new ArrayList<>();
    
    /**
     * 获取通知信息列表
     * @return 
     */
    public ObservableList<NoticeInfoFx> getNoticeInfoListObservableList() {
        this.getNoticeInfoListFromRemote(GlobalStaticConstant.GLOBAL_ORGANINFO_OWNER_USERID, GlobalStaticConstant.GLOBAL_SELECTED_MEETID);
        return noticeInfoListObservableList;
    }
    
    /**
     * 封装参数
     * @param userId
     * @param meetId
     * @return 
     */
    private String fzParam(String userId, String meetId) {
        String resultString = "{userId:'" + userId + "',meetingId:'" + meetId + "',pageSize:'10',pageIndex:'1'}";
        return resultString;
    }
    
    /**
     * 访问服务器，获取通知信息列表
     * @param userId
     * @param meetId
     * @return 
     */
    public Map<String, String> getNoticeInfoListFromRemote(String userId, String meetId) {
        Map<String, String> resultMap = new HashMap();
        try {
            //封装参数
            String param = this.fzParam(userId, meetId);
            //访问接口
            String result = HttpClientUtil.getInstance().getResponseBodyAsString(ServeIpConstant.noticeListServicePath(), param);
            if(StringUtils.isNotBlank(result)){
                //把json字符串转成map
                Map temp_map = GsonUtil.getMap(result);
                String result_code = String.valueOf(temp_map.get("resultCode"));
                String resultDesc = String.valueOf(temp_map.get("resultDesc"));
                resultMap.put("code", result_code);
                resultMap.put("desc", resultDesc);
                if (StatusConstant.RESULT_CODE_SUCCESS.equals(result_code)) {
                    String resultData = String.valueOf(temp_map.get("resultData"));
                    Map dataMap = GsonUtil.getMap(resultData);;
                    parseFzDataMap(dataMap);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(NoticeInfoService.class.getName()).log(Level.SEVERE, null, ex);
            resultMap.put("code", StatusConstant.RESULT_CODE_FAIL);
            resultMap.put("desc", "获取通知信息列表异常");
        }
        return resultMap;
    }
    
    /**
     * 解析接口返回的数据
     * @param dataMap
     * @throws ApplicationException 
     */
    private void parseFzDataMap(Map dataMap) throws ApplicationException {
        Map noticeInfoMap = new HashMap();
        List<Map> noticeListMap = (List<Map>) dataMap.get("notices");//解析notices对象
        if (!noticeListMap.isEmpty()) {
            for (int i = 0; i < noticeListMap.size(); i++) {
                noticeInfoMap = noticeListMap.get(i);
                NoticeInfoFx infofx = new NoticeInfoFx();
                this.setNoticeInfoFxProperties(infofx, noticeInfoMap);
                noticeInfoFxList.add(infofx);
            }
            noticeInfoListObservableList.setAll(noticeInfoFxList);
        }
    }
    
    /**
     * 封装NoticeInfoFx
     * @param fx
     * @param organInfoMap
     * @return 
     */
    public NoticeInfoFx setNoticeInfoFxProperties(NoticeInfoFx fx,Map noticeInfoMap){
        String noticeId = String.valueOf(noticeInfoMap.get("noticeId"));
        String noticeTitle = String.valueOf(noticeInfoMap.get("noticeTitle"));
        String createTime = String.valueOf(noticeInfoMap.get("createTime"));
        String noticeTypeName = String.valueOf(noticeInfoMap.get("noticeTypeName"));
        String noticeTypeEnglishName = String.valueOf(noticeInfoMap.get("noticeTypeEnglishName"));
        String sort = String.valueOf(noticeInfoMap.get("sort"));
        fx.setNoticeId(noticeId);
        fx.setNoticeTitle(noticeTitle);
        fx.setCreateTime(createTime);
        fx.setNoticeTypeName(noticeTypeName);
        fx.setNoticeTypeEnglishName(noticeTypeEnglishName);
        fx.setSort(Double.valueOf(sort).intValue());
        return fx;
    }
    
    
}
