
package com.chinaunicom.elemeetingpc.controllers;

import com.chinaunicom.elemeetingpc.constant.GlobalStaticConstant;
import com.chinaunicom.elemeetingpc.constant.ServeIpConstant;
import com.chinaunicom.elemeetingpc.constant.StatusConstant;
import com.chinaunicom.elemeetingpc.modelFx.NoticeAccessoriesFx;
import com.chinaunicom.elemeetingpc.modelFx.NoticeInfoFx;
import com.chinaunicom.elemeetingpc.utils.DateUtil;
import com.chinaunicom.elemeetingpc.utils.GsonUtil;
import com.chinaunicom.elemeetingpc.utils.HttpClientUtil;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

/**
 * 负责与后台会议通知列表和会议通知详情接口进行数据交互处理（/mNoti.do和/mNoti.do?action=read），
 * 主要包括以下操作：
 * 1、接口请求参数封装；
 * 2、接口请求；
 * 3、接口数据解析；
 * 注：此处设计为不对会议通知信息进行持久化保存（不保存到数据库里），每次都要重新调接口获取数据.
 * @author zhaojunfeng, chenxi
 */
public class NoticeInfoServiceController {
    
    //会议通知列表
    private List<NoticeInfoFx> noticeInfoFxList;
    
    //会议通知附件列表
    private List<NoticeAccessoriesFx> noticeAccessoriesFxList;
    
    //会议通知详情
    private NoticeInfoFx noticeInfoFx;
    
    public NoticeInfoServiceController() {
        
        noticeInfoFxList = new ArrayList<>();
        
        noticeAccessoriesFxList = new ArrayList<>();

        noticeInfoFx = new NoticeInfoFx();
    }
    
    /**
     * 获取会议通知信息列表.
     * 
     * @param pageIndex 当前页码
     * @return noticeInfoListObservableList
     */
    public List<NoticeInfoFx> getNoticeInfoList(int pageIndex) throws Exception {
        getNoticeInfoListFromRemote(GlobalStaticConstant.GLOBAL_ORGANINFO_OWNER_USERID, GlobalStaticConstant.GLOBAL_SELECTED_MEETID, pageIndex);
        return noticeInfoFxList;
    }
    
    /**
     * 访问服务器，获取会议通知信息列表.
     * 
     * @param userId 用户id
     * @param meetId 当前会议id
     * @param pageIndex 页码
     */
    public void getNoticeInfoListFromRemote(String userId, String meetId, int pageIndex) throws Exception {
        //通知列表请求参数封装
        String param = buildJsonRequestStringForNoticeList(userId, meetId, pageIndex + 1);
        //访问接口
        String response = HttpClientUtil.getInstance().getResponseBodyAsString(ServeIpConstant.noticeListServicePath(), param);
        if (StringUtils.isNotBlank(response)) {
            //把json字符串转成map
            Map tempMap = GsonUtil.getMap(response);
            String resultCode = String.valueOf(tempMap.get("resultCode"));
            if (StatusConstant.RESULT_CODE_SUCCESS.equals(resultCode)) {
                String resultData = String.valueOf(tempMap.get("resultData"));
                Map dataMap = GsonUtil.getMap(resultData);
                parseNoticeListData(dataMap);
            }
        }
    }
    
    /**
     * 封装会议通知列表请求参数.
     *
     * @param userId 用户id
     * @param meetId 当前会议id
     * @param pageIndex 当前页码
     * @return resultString 请求主体
     */
    private String buildJsonRequestStringForNoticeList(String userId, String meetId, int pageIndex) {
        String requestString = "{userId:'" + userId + "',meetingId:'" + meetId + "',pageIndex:'" + pageIndex + "',pageSize:'" + GlobalStaticConstant.GLOBAL_NOTICE_PAGESIZE + "'}";
        return requestString;
    }
    
    /**
     * 解析接口返回的数据，并把数据添加到NoticeInfoFx列表中.
     * @param dataMap 服务器返回的数据集合
     * @throws ApplicationException 
     */
    private void parseNoticeListData(Map dataMap) throws ApplicationException {
        Map noticeInfoMap = new HashMap();
        List<Map> noticeListMap = (List<Map>) dataMap.get("notices");//解析notices对象
        if (!noticeListMap.isEmpty()) {
            for (int i = 0; i < noticeListMap.size(); i++) {
                noticeInfoMap = noticeListMap.get(i);
                NoticeInfoFx InfoFx = new NoticeInfoFx();
                setNoticeListInfoFxProperties(InfoFx, noticeInfoMap);
                noticeInfoFxList.add(InfoFx);
            }
        }
    }
    
    /**
     * 设置会议通知列表属性.
     * @param fx 列表中每条通知信息
     * @param noticeInfoMap 列表中每条通知map集合
     * @return NoticeInfoFx
     */
    public NoticeInfoFx setNoticeListInfoFxProperties(NoticeInfoFx fx, Map noticeInfoMap){
        fx.setNoticeId(String.valueOf(noticeInfoMap.get("noticeId")));
        fx.setNoticeTitle(String.valueOf(noticeInfoMap.get("noticeTitle")));
        fx.setCreateTime(String.valueOf(noticeInfoMap.get("createTime")));
        fx.setNoticeTypeName(String.valueOf(noticeInfoMap.get("noticeTypeName")));
        fx.setNoticeTypeEnglishName(String.valueOf(noticeInfoMap.get("noticeTypeEnglishName")));
        fx.setSort(Double.valueOf(String.valueOf(noticeInfoMap.get("sort"))).intValue());
        return fx;
    }
    
    /**
     * 根据noticeId获取会议通知信息详情.
     * @param noticeId 通知id
     * @return NoticeInfoFx
     */
    public NoticeInfoFx getNoticeDeatilFXById(String noticeId) throws Exception {
        //通知详情请求参数封装
        String param = buildJsonRequestStringForNoticeDetail(GlobalStaticConstant.GLOBAL_ORGANINFO_OWNER_USERID, noticeId);
        //访问接口
        String response = HttpClientUtil.getInstance().getResponseBodyAsString(ServeIpConstant.noticeDetailServicePath(), param);
        if (StringUtils.isNotBlank(response)) {
            //把json字符串转成map
            Map tempMap = GsonUtil.getMap(response);
            String resultCode = String.valueOf(tempMap.get("resultCode"));
            if (StatusConstant.RESULT_CODE_SUCCESS.equals(resultCode)) {
                String resultData = String.valueOf(tempMap.get("resultData"));
                Map dataMap = GsonUtil.getMap(resultData);
                parseNoticeDetailData(dataMap);
            }
        }
        return noticeInfoFx;
    }
    
     /**
     * 封装会议通知详情请求参数.
     * @param userId
     * @param noticeId
     * @return 
     */
    private String buildJsonRequestStringForNoticeDetail(String userId,String noticeId) {
        String timeString = DateUtil.formatFullDateTime(new Date());
        String resultString = "{userId:'" + userId + "',noticeId:'" + noticeId + "',readTime:'" + timeString + "'}";
        return resultString;
    }
    
    /**
     * 解析接口返回的数据，分别对会议通知详情信息和会议通知附件信息进行解析.
     *
     * @param dataMap 服务器返回的数据集合
     * @throws ApplicationException
     */
    private void parseNoticeDetailData(Map dataMap) throws ApplicationException {
        //会议通知信息
        Map noticeInfoMap = new HashMap();
        noticeInfoMap = (Map) dataMap.get("notice");//解析会议通知信息对象
        if (noticeInfoMap != null) {
            setNoticeDetailInfoFxProperties(noticeInfoFx, noticeInfoMap);
        }
        //会议通知附件
        List<Map> noticeAccessoriesListMap = (List<Map>) dataMap.get("accessories");//解析附件对象
        if (!noticeAccessoriesListMap.isEmpty()) {
            Map noticeAccessoriesMap = new HashMap();
            for (int i = 0; i < noticeAccessoriesListMap.size(); i++) {
                noticeAccessoriesMap = noticeAccessoriesListMap.get(i);
                NoticeAccessoriesFx noticeAccessoriesFx = new NoticeAccessoriesFx();
                setNoticeAccessoriesFxProperties(noticeAccessoriesFx, noticeAccessoriesMap);
                noticeAccessoriesFxList.add(noticeAccessoriesFx);
            }
        }
    }
    
    /**
     * 设置会议通知详情属性.
     * @param fx noticeInfoFx对象
     * @param noticeInfoMap noticeInfoFx对象集合
     * @return fx noticeInfoFx对象
     */
    public NoticeInfoFx setNoticeDetailInfoFxProperties(NoticeInfoFx fx, Map noticeInfoMap){
        fx.setNoticeId(String.valueOf(noticeInfoMap.get("noticeId")));
        fx.setNoticeTitle(String.valueOf(noticeInfoMap.get("noticeTitle")));
        fx.setCreateTime(String.valueOf(noticeInfoMap.get("createTime")));
        fx.setNoticeTypeName(String.valueOf(noticeInfoMap.get("noticeTypeName")));
        fx.setNoticeTypeEnglishName(String.valueOf(noticeInfoMap.get("noticeTypeEnglishName")));
        fx.setUserName(String.valueOf(noticeInfoMap.get("userName")));
        fx.setEnglishName(String.valueOf(noticeInfoMap.get("englishName")));
        fx.setNoticeContent(String.valueOf(noticeInfoMap.get("noticeContent")));
        fx.setState(String.valueOf(noticeInfoMap.get("state")));
        return fx;
    }
    
    /**
     * 设置会议通知详情附件属性.
     *
     * @param fx
     * @param noticeAccessoriesMap
     * @return fx
     */
    public NoticeAccessoriesFx setNoticeAccessoriesFxProperties(NoticeAccessoriesFx fx, Map noticeAccessoriesMap) {
        String filePath = String.valueOf(noticeAccessoriesMap.get("filePath"));
        filePath = ServeIpConstant.IP + "/" + ServeIpConstant.FILE_FOLDER + filePath; //http://127.0.0.1/fileInfo/20190724092135139547139719039984/accessory/20190830160228421206554937947717.pdf
        fx.setId(String.valueOf(noticeAccessoriesMap.get("id")));
        fx.setFileName(String.valueOf(noticeAccessoriesMap.get("fileName")));
        fx.setFilePath(filePath);
        fx.setFileSize(String.valueOf(noticeAccessoriesMap.get("fileSize")));
        return fx;
    }
    
    /**
     * 获取会议通知附件列表.
     *
     * @return noticeAccessoriesFxList
     */
    public List<NoticeAccessoriesFx> getNoticeAccessoriesList() {
        return noticeAccessoriesFxList;
    }
}