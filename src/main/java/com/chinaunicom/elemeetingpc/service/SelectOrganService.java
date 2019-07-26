
package com.chinaunicom.elemeetingpc.service;

import com.chinaunicom.elemeetingpc.constant.GlobalStaticConstant;
import com.chinaunicom.elemeetingpc.constant.ServeIpConstant;
import com.chinaunicom.elemeetingpc.constant.StatusConstant;
import com.chinaunicom.elemeetingpc.database.models.DictionaryInfo;
import com.chinaunicom.elemeetingpc.database.models.FileResource;
import com.chinaunicom.elemeetingpc.database.models.FileUserRelation;
import com.chinaunicom.elemeetingpc.database.models.IssueFileRelation;
import com.chinaunicom.elemeetingpc.database.models.IssueInfo;
import com.chinaunicom.elemeetingpc.database.models.MeetInfo;
import com.chinaunicom.elemeetingpc.database.models.MeetIssueRelation;
import com.chinaunicom.elemeetingpc.database.models.MeetUserRelation;
import com.chinaunicom.elemeetingpc.modelFx.DictionaryModel;
import com.chinaunicom.elemeetingpc.modelFx.FileResourceModel;
import com.chinaunicom.elemeetingpc.modelFx.FileUserRelationModel;
import com.chinaunicom.elemeetingpc.modelFx.IssueFileRelationModel;
import com.chinaunicom.elemeetingpc.modelFx.IssueInfoModel;
import com.chinaunicom.elemeetingpc.modelFx.MeetInfoModel;
import com.chinaunicom.elemeetingpc.modelFx.MeetIssueRelationModel;
import com.chinaunicom.elemeetingpc.modelFx.MeetUserRelationModel;
import com.chinaunicom.elemeetingpc.utils.GsonUtil;
import com.chinaunicom.elemeetingpc.utils.HttpClientUtil;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;
import java.util.HashMap;
import java.util.List;
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
    
    private MeetIssueRelationModel meetIssueRelationModel;
    
    private IssueFileRelationModel issueFileRelationModel;
    
    private DictionaryModel dictionaryModel;
    
    private MeetUserRelationModel meetUserRelationModel;
    
    private FileUserRelationModel fileUserRelationModel;
    
    /**
     * 获取远程服务器上的最新会议相关信息
     */
    public void getMeetInfosFromRemote(){        
        try {
            //封装参数
            String param = this.fzParam(GlobalStaticConstant.GLOBAL_ORGANINFO_OWNER_USERID, GlobalStaticConstant.GLOBAL_ORGANINFO_ORGANIZATIONID, "zh_CN", GlobalStaticConstant.GLOBAL_UPDATEDATE);
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
            }else{
                System.out.println("--没有获取远程服务器上的最新会议相关信息");
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
        
        //处理会议信息        
        List<Map> meetInfoListMap = (List<Map>) dataMap.get("meetings");
        if(meetInfoListMap!=null && !meetInfoListMap.isEmpty()){
            this.meetInfoModel = new MeetInfoModel();
            Map meetInfoMap = new HashMap();
            for(int i=0;i<meetInfoListMap.size();i++){
                meetInfoMap = meetInfoListMap.get(i);
                MeetInfo meetInfo = new MeetInfo();
                this.meetInfoModel.saveOrUpdateMeetInfo(this.setPropertiesMeetInfo(meetInfo, meetInfoMap));
            }
        }
        
        //处理议题信息
        List<Map> issueInfoListMap = (List<Map>) dataMap.get("issues");
        if(issueInfoListMap!=null && !issueInfoListMap.isEmpty()){
            this.issueInfoModel = new IssueInfoModel();
            Map issueInfoMap = new HashMap();
            for(int i=0;i<issueInfoListMap.size();i++){
                issueInfoMap = issueInfoListMap.get(i);
                IssueInfo issueInfo = new IssueInfo();
                this.issueInfoModel.saveOrUpdateIssueInfo(this.setPropertiesIssueInfo(issueInfo, issueInfoMap));
            }
        }
        
        //处理文件信息
        List<Map> fileResourceListMap = (List<Map>) dataMap.get("files");
        if(fileResourceListMap!=null && !fileResourceListMap.isEmpty()){
            this.fileResourceModel = new FileResourceModel();
            Map fileMap = new HashMap();
            for(int i=0;i<fileResourceListMap.size();i++){
                fileMap = fileResourceListMap.get(i);
                FileResource fr = new FileResource();
                this.fileResourceModel.saveOrUpdateFileResource(this.setPropertiesFileResource(fr, fileMap));
            }
        }
        
        //处理会议-议题关联信息
        List<Map> meetIssueRelationListMap = (List<Map>) dataMap.get("meetingIssueInfos");
        if(meetIssueRelationListMap!=null && !meetIssueRelationListMap.isEmpty()){
            this.meetIssueRelationModel = new MeetIssueRelationModel();
            Map map = new HashMap();
            for(int i=0;i<meetIssueRelationListMap.size();i++){
                map = meetIssueRelationListMap.get(i);
                MeetIssueRelation mir = new MeetIssueRelation();
                this.meetIssueRelationModel.saveOrUpdate(this.setPropertiesMeetIssueRelation(mir, map));
            }
        }
        
        //处理议题-文件关联信息
        List<Map> issueFileRelationListMap = (List<Map>) dataMap.get("issueFileInfos");
        if(issueFileRelationListMap!=null && !issueFileRelationListMap.isEmpty()){
            this.issueFileRelationModel = new IssueFileRelationModel();
            Map map = new HashMap();
            for(int i=0;i<issueFileRelationListMap.size();i++){
                map = issueFileRelationListMap.get(i);
                IssueFileRelation mir = new IssueFileRelation();
                this.issueFileRelationModel.saveOrUpdate(this.setPropertiesIssueFileRelation(mir, map));
            }
        }
        
        //当前更新时间updateDate
        String updateDate = String.valueOf(dataMap.get("updateDate"));
        this.dictionaryModel = new DictionaryModel();
        DictionaryInfo dictionaryInfo = new DictionaryInfo();
        dictionaryInfo.setUpdateDate(updateDate);
        this.dictionaryModel.saveOrUpdateOrganInfo(dictionaryInfo);
        
        //处理会议-用户关联信息
        List<Map> meetUserRelationListMap = (List<Map>) dataMap.get("meetingUserInfos");
        if(meetUserRelationListMap!=null && !meetUserRelationListMap.isEmpty()){
            this.meetUserRelationModel = new MeetUserRelationModel();
            Map map = new HashMap();
            for(int i=0;i<meetUserRelationListMap.size();i++){
                map = meetUserRelationListMap.get(i);
                MeetUserRelation mir = new MeetUserRelation();
                this.meetUserRelationModel.saveOrUpdate(this.setPropertiesMeetUserRelation(mir, map));
            }
        }
        
        //处理文件-用户关联信息
        List<Map> fileUserRelationListMap = (List<Map>) dataMap.get("fileUserInfos");
        if(fileUserRelationListMap!=null && !fileUserRelationListMap.isEmpty()){
            this.fileUserRelationModel = new FileUserRelationModel();
            Map map = new HashMap();
            for(int i=0;i<fileUserRelationListMap.size();i++){
                map = fileUserRelationListMap.get(i);
                FileUserRelation mir = new FileUserRelation();
                this.fileUserRelationModel.saveOrUpdate(this.setPropertiesFileUserRelation(mir, map));
            }
        }
        
        //处理投票信息
        
        //处理签到信息
        
        //处理批注信息        
        
    }
    
    /**
     * 解析数据并封装MeetInfo对象.
     * @param info
     * @param meetInfoMap
     * @return 
     */
    public MeetInfo setPropertiesMeetInfo(MeetInfo info,Map meetInfoMap){
        String meetingId=String.valueOf(meetInfoMap.get("meetingId"));
        String meetingName=String.valueOf(meetInfoMap.get("meetingName"));
        String startDateTime=String.valueOf(meetInfoMap.get("startDateTime"));
        String endDateTime=String.valueOf(meetInfoMap.get("endDateTime"));
        String state=String.valueOf(meetInfoMap.get("state"));
        String createTime=String.valueOf(meetInfoMap.get("createTime"));
        String parentMeetingId=String.valueOf(meetInfoMap.get("parentMeetingId"));
        String sort=String.valueOf(meetInfoMap.get("sort"));
        String englishName=String.valueOf(meetInfoMap.get("englishName"));
        String isEng=String.valueOf(meetInfoMap.get("isEng"));
        info.setMeetingId(meetingId);
        info.setMeetingName(meetingName);
        info.setStartDateTime(startDateTime);
        info.setEndDateTime(endDateTime);
        info.setState(state);
        info.setCreateTime(createTime);
        info.setParentMeetingId(parentMeetingId);
        info.setSort(Double.valueOf(sort).intValue());
        info.setEnglishName(englishName);
        info.setIsEng(isEng);
        info.setOrganizationId(GlobalStaticConstant.GLOBAL_ORGANINFO_ORGANIZATIONID);
        
        return info;
    }
    
    /**
     * 解析数据并封装IssueInfo对象.
     * @param info
     * @param infoMap
     * @return 
     */
    public IssueInfo setPropertiesIssueInfo(IssueInfo info,Map infoMap){
        String issueId=String.valueOf(infoMap.get("issueId"));
        String issueName=String.valueOf(infoMap.get("issueName"));
        String state=String.valueOf(infoMap.get("state"));
        String sort=String.valueOf(infoMap.get("sort"));
        String englishName=String.valueOf(infoMap.get("englishName"));
        info.setIssueId(issueId);
        info.setIssueName(issueName);
        info.setState(state);
        info.setSort(Double.valueOf(sort).intValue());
        info.setEnglishName(englishName);
        
        return info;
    }
    
    /**
     * 解析数据并封装FileResource对象.
     * @param info
     * @param infoMap
     * @return 
     */
    public FileResource setPropertiesFileResource(FileResource info,Map infoMap){
        String fileId=String.valueOf(infoMap.get("fileId"));
        String fileName=String.valueOf(infoMap.get("fileName"));
        String filePath=String.valueOf(infoMap.get("filePath"));
        String fileSize=String.valueOf(infoMap.get("fileSize"));
        String password=String.valueOf(infoMap.get("password"));
        String state=String.valueOf(infoMap.get("state"));
        String sort=String.valueOf(infoMap.get("sort"));
        info.setFileId(fileId);
        info.setFileName(fileName);
        info.setFilePath(filePath);
        info.setFileSize(fileSize);
        info.setPassword(password);
        info.setState(state);
        info.setSort(Double.valueOf(sort).intValue());
        
        return info;
    }
    
    /**
     * 解析数据并封装MeetIssueRelation对象.
     * @param info
     * @param infoMap
     * @return 
     */
    public MeetIssueRelation setPropertiesMeetIssueRelation(MeetIssueRelation info,Map infoMap){
        String meettingIssueId=String.valueOf(infoMap.get("meettingIssueId"));
        String issueId=String.valueOf(infoMap.get("issueId"));
        String meetingId=String.valueOf(infoMap.get("meetingId"));
        String state=String.valueOf(infoMap.get("state"));
        String sort=String.valueOf(infoMap.get("sort"));
        info.setMeettingIssueId(meettingIssueId);
        info.setIssueId(issueId);
        info.setMeetingId(meetingId);
        info.setSort(Double.valueOf(sort).intValue());
        info.setState(state);
        
        return info;
    }
    
    /**
     * 解析数据并封装IssueFileRelation对象.
     * @param info
     * @param infoMap
     * @return 
     */
    public IssueFileRelation setPropertiesIssueFileRelation(IssueFileRelation info,Map infoMap){
        String issueFileInfo=String.valueOf(infoMap.get("issueFileInfo"));
        String issueId=String.valueOf(infoMap.get("issueId"));
        String fileId=String.valueOf(infoMap.get("fileId"));
        String fileName=String.valueOf(infoMap.get("fileName"));
        String state=String.valueOf(infoMap.get("state"));
        String sort=String.valueOf(infoMap.get("sort"));
        info.setIssueFileInfo(issueFileInfo);
        info.setIssueId(issueId);
        info.setFileId(fileId);
        info.setFileName(fileName);
        info.setSort(Double.valueOf(sort).intValue());
        info.setState(state);
        
        return info;
    }
    
    /**
     * 解析数据并封装MeetUserRelation对象.
     * @param info
     * @param infoMap
     * @return 
     */
    public MeetUserRelation setPropertiesMeetUserRelation(MeetUserRelation info,Map infoMap){
        String meetingUserId=String.valueOf(infoMap.get("meetingUserId"));
        String userId=String.valueOf(infoMap.get("userId"));
        String meetingId=String.valueOf(infoMap.get("meetingId"));
        String state=String.valueOf(infoMap.get("state"));
        info.setMeetingUserId(meetingUserId);
        info.setMeetingId(meetingId);
        info.setUserId(userId);
        info.setState(state);
        
        return info;
    }
    
    /**
     * 解析数据并封装FileUserRelation对象.
     * @param info
     * @param infoMap
     * @return 
     */
    public FileUserRelation setPropertiesFileUserRelation(FileUserRelation info,Map infoMap){
        String userFileId=String.valueOf(infoMap.get("userFileId"));
        String userId=String.valueOf(infoMap.get("userId"));
        String fileId=String.valueOf(infoMap.get("fileId"));
        String state=String.valueOf(infoMap.get("state"));
        info.setUserFileId(userFileId);
        info.setFileId(fileId);
        info.setUserId(userId);
        info.setState(state);
        
        return info;
    }
    
    
    
}
