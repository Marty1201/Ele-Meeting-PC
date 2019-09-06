package com.chinaunicom.elemeetingpc.service;

import com.chinaunicom.elemeetingpc.constant.GlobalStaticConstant;
import com.chinaunicom.elemeetingpc.constant.ServeIpConstant;
import com.chinaunicom.elemeetingpc.constant.StatusConstant;
import com.chinaunicom.elemeetingpc.database.models.Annotation;
import com.chinaunicom.elemeetingpc.database.models.FileResource;
import com.chinaunicom.elemeetingpc.database.models.FileUserRelation;
import com.chinaunicom.elemeetingpc.database.models.IssueFileRelation;
import com.chinaunicom.elemeetingpc.database.models.IssueInfo;
import com.chinaunicom.elemeetingpc.database.models.MeetInfo;
import com.chinaunicom.elemeetingpc.database.models.MeetIssueRelation;
import com.chinaunicom.elemeetingpc.database.models.MeetUserRelation;
import com.chinaunicom.elemeetingpc.modelFx.AnnotationModel;
import com.chinaunicom.elemeetingpc.modelFx.FileResourceModel;
import com.chinaunicom.elemeetingpc.modelFx.FileUserRelationModel;
import com.chinaunicom.elemeetingpc.modelFx.IssueFileRelationModel;
import com.chinaunicom.elemeetingpc.modelFx.IssueInfoModel;
import com.chinaunicom.elemeetingpc.modelFx.MeetInfoModel;
import com.chinaunicom.elemeetingpc.modelFx.MeetIssueRelationModel;
import com.chinaunicom.elemeetingpc.modelFx.MeetUserRelationModel;
import com.chinaunicom.elemeetingpc.modelFx.OrganInfoModel;
import com.chinaunicom.elemeetingpc.utils.FileDownloader;
import com.chinaunicom.elemeetingpc.utils.GsonUtil;
import com.chinaunicom.elemeetingpc.utils.HttpClientUtil;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;

/**
 * 负责与后台选择会议接口进行数据交互处理（mMeet.do?action=getMeeting），主要包括以下操作： 1、接口请求参数封装； 2、接口请求；
 * 3、接口数据解析； 4、调用models层对象进行数据本地保存.
 *
 * @author zhaojunfeng, chenxi
 */
public class MeetService {

    private MeetInfoModel meetInfoModel;

    private IssueInfoModel issueInfoModel;

    private FileResourceModel fileResourceModel;

    private MeetIssueRelationModel meetIssueRelationModel;

    private IssueFileRelationModel issueFileRelationModel;

    private OrganInfoModel organInfoModel;

    private MeetUserRelationModel meetUserRelationModel;

    private FileUserRelationModel fileUserRelationModel;

    private AnnotationModel annotationModel;

    /**
     * 调用会议接口逻辑处理,根据会议ID查询会议信息.
     */
    public void getMeetInfosFromRemote() {
        try {
            //封装参数
            String updateDate = "";
            Map<String, String> queryMap = new HashMap<>();
            this.meetInfoModel = new MeetInfoModel();
            MeetInfo info = meetInfoModel.getMeetInfoByMeetId(GlobalStaticConstant.GLOBAL_SELECTED_MEETID);
            if (info != null) {
                String update = info.getUpdateDate();
                if (StringUtils.isNotBlank(update)) {
                    updateDate = update;
                }
            }
            //queryMap.put("organizationId", GlobalStaticConstant.GLOBAL_ORGANINFO_ORGANIZATIONID);
            //queryMap.put("userId", GlobalStaticConstant.GLOBAL_ORGANINFO_OWNER_USERID);
            //List<OrganInfo> organInfoList = organInfoModel.queryOrganInfosByMap(queryMap);//根据用户id和机构id查出对应的机构
            //if (!organInfoList.isEmpty()) {
            //    updateDate = organInfoList.get(0).getUpdateDate();
            //}
            String param = this.fzParam(GlobalStaticConstant.GLOBAL_ORGANINFO_OWNER_USERID, GlobalStaticConstant.GLOBAL_SELECTED_MEETID, updateDate);
            //访问接口
            String result = HttpClientUtil.getInstance().getResponseBodyAsString(ServeIpConstant.meetingOfOrganServicePath(), param);
            if (StringUtils.isNotBlank(result)) {
                //把json字符串转成map
                Map temp_map = GsonUtil.getMap(result);
                String result_code = String.valueOf(temp_map.get("resultCode"));
                String resultDesc = String.valueOf(temp_map.get("resultDesc"));
                if (StatusConstant.RESULT_CODE_SUCCESS.equals(result_code)) {//查询成功
                    String resultData = String.valueOf(temp_map.get("resultData"));
                    Map dataMap = GsonUtil.getMap(resultData);;
                    parseFzDataMap(dataMap);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(SelectOrganService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * 封装参数为json字符串
     *
     * @param userId
     * @param meetingId
     * @param updateDate
     * 示例：{userId:'20190724085930588875478137127383',meetingId:'20190701145559610372604487039259',organizationId:'20190109175459084818173570055782',updateDate:''}
     * @return
     */
    private String fzParam(String userId, String meetingId, String updateDate) {
        String resultString = "{userId:'" + userId + "',meetingId:'" + meetingId + "',updateDate:'" + updateDate + "'}";
        return resultString;
    }

    /**
     * 解析数据
     *
     * @param dataMap
     * @throws ApplicationException
     */
    private void parseFzDataMap(Map dataMap) throws ApplicationException {

        //处理子会议信息        
        List<Map> meetInfoListMap = (List<Map>) dataMap.get("childMeetings");
        if (meetInfoListMap != null && !meetInfoListMap.isEmpty()) {
            this.meetInfoModel = new MeetInfoModel();
            Map meetInfoMap = new HashMap();
            for (int i = 0; i < meetInfoListMap.size(); i++) {
                meetInfoMap = meetInfoListMap.get(i);
                MeetInfo meetInfo = new MeetInfo();
                this.meetInfoModel.saveOrUpdateMeetInfo(this.setPropertiesMeetInfo(meetInfo, meetInfoMap));
            }
        }

        //处理议题信息
        List<Map> issueInfoListMap = (List<Map>) dataMap.get("issues");
        if (issueInfoListMap != null && !issueInfoListMap.isEmpty()) {
            this.issueInfoModel = new IssueInfoModel();
            Map issueInfoMap = new HashMap();
            for (int i = 0; i < issueInfoListMap.size(); i++) {
                issueInfoMap = issueInfoListMap.get(i);
                IssueInfo issueInfo = new IssueInfo();
                this.issueInfoModel.saveOrUpdateIssueInfo(this.setPropertiesIssueInfo(issueInfo, issueInfoMap));
            }
        }

        //处理文件信息
        List<Map> fileResourceListMap = (List<Map>) dataMap.get("files");
        if (fileResourceListMap != null && !fileResourceListMap.isEmpty()) {
            this.fileResourceModel = new FileResourceModel();
            FileResource fr = new FileResource();
            Map fileMap = new HashMap();
            //使用CachedThreadPool管理文件下载线程
            ExecutorService service = Executors.newCachedThreadPool();
            for (int i = 0; i < fileResourceListMap.size(); i++) {
                fileMap = fileResourceListMap.get(i);
                //根据接口返回的文件id，查询数据库中是否已存在此条数据并对已存在的文件进行相应的处理
                List<FileResource> oldFile = fileResourceModel.queryFilesById(String.valueOf(fileMap.get("fileId")));
                if (!oldFile.isEmpty()) {
                    //1.根据状态state检查文件是否需要删除
                    if (StringUtils.equals(oldFile.get(0).getState(), "0") && StringUtils.equals(String.valueOf(fileMap.get("state")), "2")) {
                        //在本地磁盘内删除此文件
                        deleteFile(StringUtils.substringAfterLast(oldFile.get(0).getFilePath(), "/"));
                    }
                    //2.根据filePath（里面最后文件id）来检查文件是否需要更新
                    if (!StringUtils.equals(oldFile.get(0).getFilePath(), String.valueOf(fileMap.get("filePath")))) {
                        //先把本地的旧文件删除
                        deleteFile(StringUtils.substringAfterLast(oldFile.get(0).getFilePath(), "/"));
                        //开启多线程下载新文件，FileDownloader实现Runnable，重写run方法
                        String url = ServeIpConstant.IP + "/" + ServeIpConstant.FILE_FOLDER + "/" + String.valueOf(fileMap.get("filePath"));
                        service.execute(new FileDownloader(url));
                    }
                    //更新文件信息
                    this.fileResourceModel.saveOrUpdateFileResource(this.setPropertiesFileResource(fr, fileMap));
                } else {
                    //开启多线程下载新文件，FileDownloader实现Runnable，重写run方法
                    String url = ServeIpConstant.IP + "/" + ServeIpConstant.FILE_FOLDER + String.valueOf(fileMap.get("filePath"));
                    service.execute(new FileDownloader(url));
                    //新增文件信息
                    this.fileResourceModel.saveOrUpdateFileResource(this.setPropertiesFileResource(fr, fileMap));
                }
            }
            service.shutdown();//关闭线程
        }

        //处理会议-议题关联信息
        List<Map> meetIssueRelationListMap = (List<Map>) dataMap.get("meetingIssueInfos");
        if (meetIssueRelationListMap != null && !meetIssueRelationListMap.isEmpty()) {
            this.meetIssueRelationModel = new MeetIssueRelationModel();
            Map map = new HashMap();
            for (int i = 0; i < meetIssueRelationListMap.size(); i++) {
                map = meetIssueRelationListMap.get(i);
                MeetIssueRelation mir = new MeetIssueRelation();
                this.meetIssueRelationModel.saveOrUpdate(this.setPropertiesMeetIssueRelation(mir, map));
            }
        }

        //处理议题-文件关联信息
        List<Map> issueFileRelationListMap = (List<Map>) dataMap.get("issueFileInfos");
        if (issueFileRelationListMap != null && !issueFileRelationListMap.isEmpty()) {
            this.issueFileRelationModel = new IssueFileRelationModel();
            Map map = new HashMap();
            for (int i = 0; i < issueFileRelationListMap.size(); i++) {
                map = issueFileRelationListMap.get(i);
                IssueFileRelation mir = new IssueFileRelation();
                this.issueFileRelationModel.saveOrUpdate(this.setPropertiesIssueFileRelation(mir, map));
            }
        }

        //处理会议-用户关联信息
        List<Map> meetUserRelationListMap = (List<Map>) dataMap.get("meetingUserInfos");
        if (meetUserRelationListMap != null && !meetUserRelationListMap.isEmpty()) {
            this.meetUserRelationModel = new MeetUserRelationModel();
            Map map = new HashMap();
            for (int i = 0; i < meetUserRelationListMap.size(); i++) {
                map = meetUserRelationListMap.get(i);
                MeetUserRelation mir = new MeetUserRelation();
                this.meetUserRelationModel.saveOrUpdate(this.setPropertiesMeetUserRelation(mir, map));
            }
        }

        //处理文件-用户关联信息
        List<Map> fileUserRelationListMap = (List<Map>) dataMap.get("fileUserInfos");
        if (fileUserRelationListMap != null && !fileUserRelationListMap.isEmpty()) {
            this.fileUserRelationModel = new FileUserRelationModel();
            Map map = new HashMap();
            for (int i = 0; i < fileUserRelationListMap.size(); i++) {
                map = fileUserRelationListMap.get(i);
                FileUserRelation mir = new FileUserRelation();
                this.fileUserRelationModel.saveOrUpdate(this.setPropertiesFileUserRelation(mir, map));
            }
        }

        //处理批注信息       
        List<Map> annotationListMap = (List<Map>) dataMap.get("annotations");
        if (annotationListMap != null && !annotationListMap.isEmpty()) {
            this.annotationModel = new AnnotationModel();
            Map map = new HashMap();
            for (int i = 0; i < annotationListMap.size(); i++) {
                map = annotationListMap.get(i);
                Annotation mir = new Annotation();
                this.annotationModel.saveOrUpdate(this.setPropertiesAnnotation(mir, map));
            }
        }

        //处理更新时间的问题updateDate
        Object _tempupdate = dataMap.get("updateDate");
        if (_tempupdate != null) {
            String temp_updateDate = String.valueOf(_tempupdate);
            meetInfoModel = new MeetInfoModel();
            MeetInfo info = meetInfoModel.getMeetInfoByMeetId(GlobalStaticConstant.GLOBAL_SELECTED_MEETID);
            if (info != null) {
                info.setUpdateDate(temp_updateDate);
                meetInfoModel.saveOrUpdateMeetInfo(info);
            }
        }

    }

    /**
     * 解析数据并封装MeetInfo对象.
     *
     * @param info
     * @param meetInfoMap
     * @return
     */
    public MeetInfo setPropertiesMeetInfo(MeetInfo info, Map meetInfoMap) {
        String meetingId = String.valueOf(meetInfoMap.get("meetingId"));
        String meetingName = String.valueOf(meetInfoMap.get("meetingName"));
        String startDateTime = String.valueOf(meetInfoMap.get("startDateTime"));
        String endDateTime = String.valueOf(meetInfoMap.get("endDateTime"));
        String state = String.valueOf(meetInfoMap.get("state"));
        String createTime = String.valueOf(meetInfoMap.get("createTime"));
        String parentMeetingId = String.valueOf(meetInfoMap.get("parentMeetingId"));
        String sort = String.valueOf(meetInfoMap.get("sort"));
        String englishName = String.valueOf(meetInfoMap.get("englishName"));
        String isEng = String.valueOf(meetInfoMap.get("isEng"));
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
     *
     * @param info
     * @param infoMap
     * @return
     */
    public IssueInfo setPropertiesIssueInfo(IssueInfo info, Map infoMap) {
        String issueId = String.valueOf(infoMap.get("issueId"));
        String issueName = String.valueOf(infoMap.get("issueName"));
        String state = String.valueOf(infoMap.get("state"));
        String sort = String.valueOf(infoMap.get("sort"));
        String englishName = String.valueOf(infoMap.get("englishName"));
        info.setIssueId(issueId);
        info.setIssueName(issueName);
        info.setState(state);
        info.setSort(Double.valueOf(sort).intValue());
        info.setEnglishName(englishName);

        return info;
    }

    /**
     * 解析数据并封装FileResource对象.
     *
     * @param info
     * @param infoMap
     * @return
     */
    public FileResource setPropertiesFileResource(FileResource info, Map infoMap) {
        String fileId = String.valueOf(infoMap.get("fileId"));
        String fileName = String.valueOf(infoMap.get("fileName"));
        String filePath = String.valueOf(infoMap.get("filePath"));
        String fileSize = String.valueOf(infoMap.get("fileSize"));
        String password = String.valueOf(infoMap.get("password"));
        String state = String.valueOf(infoMap.get("state"));
        String sort = String.valueOf(infoMap.get("sort"));
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
     *
     * @param info
     * @param infoMap
     * @return
     */
    public MeetIssueRelation setPropertiesMeetIssueRelation(MeetIssueRelation info, Map infoMap) {
        String meettingIssueId = String.valueOf(infoMap.get("meettingIssueId"));
        String issueId = String.valueOf(infoMap.get("issueId"));
        String meetingId = String.valueOf(infoMap.get("meetingId"));
        String state = String.valueOf(infoMap.get("state"));
        String sort = String.valueOf(infoMap.get("sort"));
        info.setMeettingIssueId(meettingIssueId);
        info.setIssueId(issueId);
        info.setMeetingId(meetingId);
        info.setSort(Double.valueOf(sort).intValue());
        info.setState(state);

        return info;
    }

    /**
     * 解析数据并封装IssueFileRelation对象.
     *
     * @param info
     * @param infoMap
     * @return
     */
    public IssueFileRelation setPropertiesIssueFileRelation(IssueFileRelation info, Map infoMap) {
        String issueFileInfo = String.valueOf(infoMap.get("issueFileInfo"));
        String issueId = String.valueOf(infoMap.get("issueId"));
        String fileId = String.valueOf(infoMap.get("fileId"));
        String fileName = String.valueOf(infoMap.get("fileName"));
        String state = String.valueOf(infoMap.get("state"));
        String sort = String.valueOf(infoMap.get("sort"));
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
     *
     * @param info
     * @param infoMap
     * @return
     */
    public MeetUserRelation setPropertiesMeetUserRelation(MeetUserRelation info, Map infoMap) {
        String meetingUserId = String.valueOf(infoMap.get("meetingUserId"));
        String userId = String.valueOf(infoMap.get("userId"));
        String meetingId = String.valueOf(infoMap.get("meetingId"));
        String state = String.valueOf(infoMap.get("state"));
        info.setMeetingUserId(meetingUserId);
        info.setMeetingId(meetingId);
        info.setUserId(userId);
        info.setState(state);

        return info;
    }

    /**
     * 解析数据并封装FileUserRelation对象.
     *
     * @param info
     * @param infoMap
     * @return
     */
    public FileUserRelation setPropertiesFileUserRelation(FileUserRelation info, Map infoMap) {
        String userFileId = String.valueOf(infoMap.get("userFileId"));
        String userId = String.valueOf(infoMap.get("userId"));
        String fileId = String.valueOf(infoMap.get("fileId"));
        String state = String.valueOf(infoMap.get("state"));
        info.setUserFileId(userFileId);
        info.setFileId(fileId);
        info.setUserId(userId);
        info.setState(state);

        return info;
    }

    /**
     * 解析数据并封装Annotation对象.
     *
     * @param info
     * @param infoMap
     * @return
     */
    public Annotation setPropertiesAnnotation(Annotation info, Map infoMap) {
        String annoId = String.valueOf(infoMap.get("annoId"));
        String annoDate = String.valueOf(infoMap.get("annoDate"));
        String xPoint = String.valueOf(infoMap.get("xPoint"));
        String yPoint = String.valueOf(infoMap.get("yPoint"));
        String content = String.valueOf(infoMap.get("content"));
        String height = String.valueOf(infoMap.get("height"));
        String width = String.valueOf(infoMap.get("width"));
        String pageNum = String.valueOf(infoMap.get("pageNum"));
        String fileId = String.valueOf(infoMap.get("fileId"));
        String annoType = String.valueOf(infoMap.get("annoType"));
        String state = String.valueOf(infoMap.get("state"));
        String userId = String.valueOf(infoMap.get("userId"));

        info.setAnnoId(annoId);
        info.setAnnoDate(annoDate);
        info.setAnnoType(annoType);
        info.setContent(content);
        info.setFileId(fileId);
        info.setHeight(height);
        info.setPageNum(Double.valueOf(pageNum).intValue());
        info.setState(state);
        info.setUserId(userId);
        info.setWidth(width);
        info.setxPoint(xPoint);
        info.setyPoint(yPoint);

        return info;
    }

    /**
     * 删除本地磁盘上的文件.
     *
     * @param fileName
     */
    public void deleteFile(String fileName) {
        File deleteFile = new File(GlobalStaticConstant.GLOBAL_FILE_DISK + "\\" + GlobalStaticConstant.GLOBAL_FILE_FOLDER + "\\" + fileName);
        if (deleteFile.exists() && deleteFile.isFile()) {
            deleteFile.delete();
        }
    }
}
