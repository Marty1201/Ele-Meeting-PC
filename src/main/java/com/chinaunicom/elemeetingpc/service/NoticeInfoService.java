
package com.chinaunicom.elemeetingpc.service;

import com.chinaunicom.elemeetingpc.constant.GlobalStaticConstant;
import com.chinaunicom.elemeetingpc.constant.ServeIpConstant;
import com.chinaunicom.elemeetingpc.constant.StatusConstant;
import com.chinaunicom.elemeetingpc.modelFx.NoticeAccessoriesFx;
import com.chinaunicom.elemeetingpc.modelFx.NoticeInfoFx;
import com.chinaunicom.elemeetingpc.utils.DateUtil;
import com.chinaunicom.elemeetingpc.utils.FxmlUtils;
import com.chinaunicom.elemeetingpc.utils.GsonUtil;
import com.chinaunicom.elemeetingpc.utils.HttpClientUtil;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
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
public class NoticeInfoService {
    
    private static final Logger logger = LoggerFactory.getLogger(NoticeInfoService.class);
    
    //会议通知列表
    private List<NoticeInfoFx> noticeInfoFxList;
    
    //会议通知附件列表
    private List<NoticeAccessoriesFx> noticeAccessoriesFxList;
    
    //会议通知详情
    private NoticeInfoFx noticeInfoFx;
    
    public NoticeInfoService() {
        
        noticeInfoFxList = new ArrayList<>();
        
        noticeAccessoriesFxList = new ArrayList<>();

        noticeInfoFx = new NoticeInfoFx();
    }
    
    /**
     * 获取会议通知信息列表.
     * @param pageIndex 当前页码
     * @return noticeInfoListObservableList
     */
    public List<NoticeInfoFx> getNoticeInfoList(int pageIndex) {
        this.getNoticeInfoListFromRemote(GlobalStaticConstant.GLOBAL_ORGANINFO_OWNER_USERID, GlobalStaticConstant.GLOBAL_SELECTED_MEETID, pageIndex);
        return noticeInfoFxList;
    }
    
    /**
     * 访问服务器，获取会议通知信息列表.
     * @param userId
     * @param meetId
     * @param pageIndex
     * @return resultMap
     */
    public Map<String, String> getNoticeInfoListFromRemote(String userId, String meetId, int pageIndex) {
        Map<String, String> resultMap = new HashMap();
        try {
            //封装参数
            String param = this.fzParamForNoticeList(userId, meetId, pageIndex+1);
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
                    Map dataMap = GsonUtil.getMap(resultData);
                    parseNoticeListData(dataMap);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex.getCause().getMessage());
            resultMap.put("code", StatusConstant.RESULT_CODE_FAIL);
            resultMap.put("desc", FxmlUtils.getResourceBundle().getString("server.malfunction"));
        }
        return resultMap;
    }
    
    /**
     * 封装会议通知列表请求参数.
     *
     * @param userId
     * @param meetId
     * @param pageIndex 当前页码
     * @return resultString
     */
    private String fzParamForNoticeList(String userId, String meetId, int pageIndex) {
        String resultString = "{userId:'" + userId + "',meetingId:'" + meetId + "',pageIndex:'" + pageIndex + "',pageSize:'" + GlobalStaticConstant.GLOBAL_NOTICE_PAGESIZE + "'}";
        return resultString;
    }
    
    /**
     * 解析接口返回的数据，并把数据添加到List<NoticeInfoFx>列表中返回.
     * @param dataMap
     * @throws ApplicationException 
     */
    private void parseNoticeListData(Map dataMap) throws ApplicationException {
        Map noticeInfoMap = new HashMap();
        List<Map> noticeListMap = (List<Map>) dataMap.get("notices");//解析notices对象
        if (!noticeListMap.isEmpty()) {
            for (int i = 0; i < noticeListMap.size(); i++) {
                noticeInfoMap = noticeListMap.get(i);
                NoticeInfoFx infofx = new NoticeInfoFx();
                this.setNoticeListInfoFxProperties(infofx, noticeInfoMap);
                noticeInfoFxList.add(infofx);
            }
        }
    }
    
    /**
     * 设置会议通知列表属性.
     * @param fx
     * @param noticeInfoMap
     * @return fx
     */
    public NoticeInfoFx setNoticeListInfoFxProperties(NoticeInfoFx fx, Map noticeInfoMap){
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
    
    /**
     * 根据noticeId获取会议通知信息详情.
     * @param noticeId
     * @return fx
     */
    public NoticeInfoFx getNoticeDeatilFXById(String noticeId){
        Map<String, String> resultMap = new HashMap();
        try {
            //封装参数
            String param = this.fzParamForNoticeDetail(GlobalStaticConstant.GLOBAL_ORGANINFO_OWNER_USERID, noticeId);
            //访问接口
            String result = HttpClientUtil.getInstance().getResponseBodyAsString(ServeIpConstant.noticeDetailServicePath(), param);
            if(StringUtils.isNotBlank(result)){
                //把json字符串转成map
                Map temp_map = GsonUtil.getMap(result);
                String result_code = String.valueOf(temp_map.get("resultCode"));
                String resultDesc = String.valueOf(temp_map.get("resultDesc"));
                resultMap.put("code", result_code);
                resultMap.put("desc", resultDesc);
                if (StatusConstant.RESULT_CODE_SUCCESS.equals(result_code)) {
                    String resultData = String.valueOf(temp_map.get("resultData"));
                    Map dataMap = GsonUtil.getMap(resultData);
                    parseNoticeDetailData(dataMap);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex.getCause().getMessage());
            resultMap.put("code", StatusConstant.RESULT_CODE_FAIL);
            resultMap.put("desc", FxmlUtils.getResourceBundle().getString("server.malfunction"));
        }
        return noticeInfoFx;
    }
    
     /**
     * 封装会议通知详情请求参数.
     * @param userId
     * @param noticeId
     * @return 
     */
    private String fzParamForNoticeDetail(String userId,String noticeId) {
        String timeString = DateUtil.formatFullDateTime(new Date());
        String resultString = "{userId:'" + userId + "',noticeId:'" + noticeId + "',readTime:'" + timeString + "'}";
        return resultString;
    }
    
    /**
     * 解析接口返回的数据，分别对会议通知详情信息和会议通知附件信息进行解析.
     *
     * @param dataMap
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
     * @param fx
     * @param noticeInfoMap
     * @return fx
     */
    public NoticeInfoFx setNoticeDetailInfoFxProperties(NoticeInfoFx fx, Map noticeInfoMap){
        String noticeId = String.valueOf(noticeInfoMap.get("noticeId"));
        String noticeTitle = String.valueOf(noticeInfoMap.get("noticeTitle"));
        String createTime = String.valueOf(noticeInfoMap.get("createTime"));
        String noticeTypeName = String.valueOf(noticeInfoMap.get("noticeTypeName"));
        String noticeTypeEnglishName = String.valueOf(noticeInfoMap.get("noticeTypeEnglishName"));
        String userName = String.valueOf(noticeInfoMap.get("userName"));
        String englishName = String.valueOf(noticeInfoMap.get("englishName"));
        String noticeContent = String.valueOf(noticeInfoMap.get("noticeContent"));
        String state = String.valueOf(noticeInfoMap.get("state"));
        fx.setNoticeId(noticeId);
        fx.setNoticeTitle(noticeTitle);
        fx.setCreateTime(createTime);
        fx.setNoticeTypeName(noticeTypeName);
        fx.setNoticeTypeEnglishName(noticeTypeEnglishName);
        fx.setUserName(userName);
        fx.setEnglishName(englishName);
        fx.setNoticeContent(noticeContent);
        fx.setState(state);
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
        String id = String.valueOf(noticeAccessoriesMap.get("id"));
        String fileName = String.valueOf(noticeAccessoriesMap.get("fileName"));
        String filePath = String.valueOf(noticeAccessoriesMap.get("filePath"));
        filePath = ServeIpConstant.IP + "/" + ServeIpConstant.FILE_FOLDER + filePath; //http://127.0.0.1/fileInfo/20190724092135139547139719039984/accessory/20190830160228421206554937947717.pdf
        String fileSize = String.valueOf(noticeAccessoriesMap.get("fileSize"));
        fx.setId(id);
        fx.setFileName(fileName);
        fx.setFilePath(filePath);
        fx.setFileSize(fileSize);
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