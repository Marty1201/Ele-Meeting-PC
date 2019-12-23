package com.chinaunicom.elemeetingpc.controllers;

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
import com.chinaunicom.elemeetingpc.service.AnnotationService;
import com.chinaunicom.elemeetingpc.service.FileResourceService;
import com.chinaunicom.elemeetingpc.service.FileUserRelationService;
import com.chinaunicom.elemeetingpc.service.IssueFileRelationService;
import com.chinaunicom.elemeetingpc.service.IssueInfoService;
import com.chinaunicom.elemeetingpc.service.MeetInfoService;
import com.chinaunicom.elemeetingpc.service.MeetIssueRelationService;
import com.chinaunicom.elemeetingpc.service.MeetUserRelationService;
import com.chinaunicom.elemeetingpc.utils.DateUtil;
import com.chinaunicom.elemeetingpc.utils.FileDownloader;
import com.chinaunicom.elemeetingpc.utils.FileUtil;
import com.chinaunicom.elemeetingpc.utils.GsonUtil;
import com.chinaunicom.elemeetingpc.utils.HttpClientUtil;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 当用户在左侧会议列表中选择会议后将触发MeetInfoServiceController，此控制器负责与后台选择会议接口进行数据交互处理（mMeet.do?action=getMeeting），
 * 主要包括以下操作： 1、接口请求参数封装； 2、接口请求； 3、接口数据解析； 4、调用service层对象进行数据本地保存.
 *
 * @author zhaojunfeng, chenxi
 */
public class MeetInfoServiceController {

    private static final Logger logger = LoggerFactory.getLogger(MeetInfoServiceController.class);

    private MeetInfoService meetInfoService;

    private IssueInfoService issueInfoService;

    private FileResourceService fileResourceService;

    private MeetIssueRelationService meetIssueRelationService;

    private IssueFileRelationService issueFileRelationService;

    private MeetUserRelationService meetUserRelationService;

    private FileUserRelationService fileUserRelationService;

    private AnnotationService annotationService;

    /**
     * 调用会议接口逻辑处理,根据会议ID查询会议信息.
     */
    public void getMeetInfosFromRemote() throws ApplicationException, Exception {
        //初始化
        meetInfoService = new MeetInfoService();
        issueInfoService = new IssueInfoService();
        fileResourceService = new FileResourceService();
        meetIssueRelationService = new MeetIssueRelationService();
        issueFileRelationService = new IssueFileRelationService();
        meetUserRelationService = new MeetUserRelationService();
        fileUserRelationService = new FileUserRelationService();
        annotationService = new AnnotationService();
        //封装参数
        String jsonRequestString = buildJsonRequestString(GlobalStaticConstant.GLOBAL_ORGANINFO_OWNER_USERID, GlobalStaticConstant.GLOBAL_SELECTED_MEETID, getUpdateDate());
        //访问接口
        logger.debug("调用getMeeting接口开始时间：{}", DateUtil.formatFullDateTime(new Date()));
        String response = HttpClientUtil.getInstance().getResponseBodyAsString(ServeIpConstant.meetingOfOrganServicePath(), jsonRequestString);
        logger.debug("调用getMeeting接口结束时间：{}", DateUtil.formatFullDateTime(new Date()));
        if (StringUtils.isNotBlank(response)) {
            //把json字符串转成map
            Map temp_map = GsonUtil.getMap(response);
            String resultCode = String.valueOf(temp_map.get("resultCode"));
            if (StatusConstant.RESULT_CODE_SUCCESS.equals(resultCode)) {//查询成功
                String resultData = String.valueOf(temp_map.get("resultData"));
                Map dataMap = GsonUtil.getMap(resultData);
                logger.debug("处理getMeeting接口返回数据开始时间：{}", DateUtil.formatFullDateTime(new Date()));
                parseDataMap(dataMap);
                logger.debug("处理getMeeting接口返回数据结束时间：{}", DateUtil.formatFullDateTime(new Date()));
            }
        }
    }

    /**
     * 从MeetInfo中获取updateDate字段值.
     *
     * @return updateDate 更新时间
     * @throws ApplicationException
     */
    public String getUpdateDate() throws ApplicationException {
        String updateDate = "";
        Map<String, String> queryMap = new HashMap<>();
        MeetInfo info = meetInfoService.getMeetInfoByMeetId(GlobalStaticConstant.GLOBAL_SELECTED_MEETID);
        if (info != null) {
            String existUpdateDate = info.getUpdateDate();
            if (StringUtils.isNotBlank(existUpdateDate)) {
                updateDate = existUpdateDate;
            }
        }
        return updateDate;
    }

    /**
     * 封装参数为json字符串，示例：{userId:'20190724085930588875478137127383',meetingId:'20190701145559610372604487039259',organizationId:'20190109175459084818173570055782',updateDate:''}.
     *
     * @param userId 用户id
     * @param meetingId 会议id
     * @param updateDate 数据更新时间
     * @return 请求主体
     */
    private String buildJsonRequestString(String userId, String meetingId, String updateDate) {
        String resultString = "{userId:'" + userId + "',meetingId:'" + meetingId + "',updateDate:'" + updateDate + "'}";
        return resultString;
    }

    /**
     * 解析接口返回的Map数据集合，需要处理的数据类型主要包括以下几种： 1、子会议信息 2、议题信息 3、文件信息（线程池下载文件）
     * 4、会议议题关联信息 5、议题文件关联信息 6、会议用户关联信息 7、文件用户关联信息 8、rabbitMQ相关信息 9、misc.
     *
     * @param dataMap
     * @throws ApplicationException
     */
    private void parseDataMap(Map dataMap) throws ApplicationException {

        //处理子会议信息
        handleChildMeetingInfo(dataMap);
        //处理议题信息
        handleIssueInfo(dataMap);
        //处理文件信息
        handleFileResource(dataMap);
        //处理会议议题关联信息
        hanleMeetIssueRelation(dataMap);
        //处理议题文件关联信息
        hanleIssueFileRelation(dataMap);
        //处理会议用户关联信息
        handleMeetUserRelation(dataMap);
        //处理文件用户关联信息
        handleFileUserRelation(dataMap);
        //处理批注信息
        handleAnnotation(dataMap);
        //处理更新时间updateDate
        handleMisc(dataMap);
    }

    /**
     * 从接口返回的Map集合中获取MeetInfo对象（一个Map的列表集合），循环该集合，获取每条MeetInfo对象的属性，
     * 创建新的MeetInfo对象，对其属性进行赋值并保存到数据库里.
     *
     * @param dataMap 服务器返回的数据集合
     * @throws ApplicationException
     */
    public void handleChildMeetingInfo(Map dataMap) throws ApplicationException {
        //处理子会议信息        
        List<Map> childMeetingsListMap = (List<Map>) dataMap.get("childMeetings");
        if (childMeetingsListMap != null && !childMeetingsListMap.isEmpty()) {
            Map meetInfoMap = new HashMap();
            for (int i = 0; i < childMeetingsListMap.size(); i++) {
                meetInfoMap = childMeetingsListMap.get(i);
                meetInfoService.saveOrUpdateMeetInfo(setMeetInfoProperties(meetInfoMap));
            }
        }
    }

    /**
     * MeetInfo对象赋值，获取接口返回的MeetInfo对象的值，创建一个空白的MeetInfo对象并对其进行赋值然后保存到数据库里.
     *
     * @param meetInfoMap 接口返回的一个MeetInfo对象
     * @return 一个被赋值的MeetInfo对象
     */
    public MeetInfo setMeetInfoProperties(Map meetInfoMap) {
        MeetInfo meetInfo = new MeetInfo();
        meetInfo.setMeetingId(String.valueOf(meetInfoMap.get("meetingId")));
        meetInfo.setMeetingName(String.valueOf(meetInfoMap.get("meetingName")));
        meetInfo.setStartDateTime(String.valueOf(meetInfoMap.get("startDateTime")));
        meetInfo.setEndDateTime(String.valueOf(meetInfoMap.get("endDateTime")));
        meetInfo.setState(String.valueOf(meetInfoMap.get("state")));
        meetInfo.setCreateTime(String.valueOf(meetInfoMap.get("createTime")));
        meetInfo.setParentMeetingId(String.valueOf(meetInfoMap.get("parentMeetingId")));
        meetInfo.setSort(Double.valueOf(String.valueOf(meetInfoMap.get("sort"))).intValue());
        meetInfo.setEnglishName(String.valueOf(meetInfoMap.get("englishName")));
        meetInfo.setIsEng(String.valueOf(meetInfoMap.get("isEng")));
        meetInfo.setOrganizationId(GlobalStaticConstant.GLOBAL_ORGANINFO_ORGANIZATIONID);
        return meetInfo;
    }

    /**
     * 从接口返回的Map集合中获取IssueInfo对象（一个Map的列表集合），循环该集合，获取每条IssueInfo对象的属性，
     * 创建新的IssueInfo对象，对其属性进行赋值并保存到数据库里.
     *
     * @param dataMap 服务器返回的数据集合
     * @throws ApplicationException
     */
    public void handleIssueInfo(Map dataMap) throws ApplicationException {
        //获取议题信息
        List<Map> issueInfoListMap = (List<Map>) dataMap.get("issues");
        if (issueInfoListMap != null && !issueInfoListMap.isEmpty()) {
            Map issueInfoMap = new HashMap();
            for (int i = 0; i < issueInfoListMap.size(); i++) {
                issueInfoMap = issueInfoListMap.get(i);
                issueInfoService.saveOrUpdateIssueInfo(setIssueInfoProperties(issueInfoMap));
            }
        }
    }

    /**
     * IssueInfo对象赋值，获取接口返回的IssueInfo对象的值，创建一个空白的IssueInfo对象并对其进行赋值然后保存到数据库里.
     *
     * @param issueInfoMap 接口返回的一个IssueInfo对象
     * @return 一个被赋值的IssueInfo对象
     */
    public IssueInfo setIssueInfoProperties(Map issueInfoMap) {
        IssueInfo issueInfo = new IssueInfo();
        issueInfo.setIssueId(String.valueOf(issueInfoMap.get("issueId")));
        issueInfo.setIssueName(String.valueOf(issueInfoMap.get("issueName")));
        issueInfo.setState(String.valueOf(issueInfoMap.get("state")));
        issueInfo.setSort(Double.valueOf(String.valueOf(issueInfoMap.get("sort"))).intValue());
        issueInfo.setEnglishName(String.valueOf(issueInfoMap.get("englishName")));
        return issueInfo;
    }

    /**
     * 从接口返回的Map集合中获取FileResource对象（一个Map的列表集合），循环该集合，获取每条FileResource对象的属性，
     * 创建新的FileResource对象，对其属性进行赋值并保存到数据库里，同时使用线程池对服务器端的文件进行下载的处理.
     *
     * @param dataMap 服务器返回的数据集合
     * @throws ApplicationException
     */
    public void handleFileResource(Map dataMap) throws ApplicationException {
        //获取文件信息
        List<Map> fileResourceListMap = (List<Map>) dataMap.get("files");
        if (fileResourceListMap != null && !fileResourceListMap.isEmpty()) {
            Map fileResourceMap = new HashMap();
            //在用户的主文件夹目录下创建下载文件存放目录，对Windows和Mac操作系统进行区分
            if (StringUtils.containsIgnoreCase(System.getProperty("os.name"), GlobalStaticConstant.GLOBAL_WINDOWS_SYSTEM)) {
                FileUtil.createFolder(GlobalStaticConstant.GLOBAL_FILE_DISK + "\\" + GlobalStaticConstant.GLOBAL_FILE_FOLDER);
            }
            if (StringUtils.containsIgnoreCase(System.getProperty("os.name"), GlobalStaticConstant.GLOBAL_MAC_SYSTEM)) {
                FileUtil.createFolder(GlobalStaticConstant.GLOBAL_FILE_DISK + "/" + GlobalStaticConstant.GLOBAL_FILE_FOLDER);
            }
            //使用CachedThreadPool管理文件下载线程
            ExecutorService service = Executors.newCachedThreadPool();
            for (int i = 0; i < fileResourceListMap.size(); i++) {
                fileResourceMap = fileResourceListMap.get(i);
                //根据接口返回的文件id，查询数据库中是否已存在此条数据并对已存在的文件进行相应的处理
                List<FileResource> oldFile = fileResourceService.queryFilesById(String.valueOf(fileResourceMap.get("fileId")));
                if (!oldFile.isEmpty()) { //如果文件已存在，检查是否需要对已存在的文件进行更新
                    String fileName = StringUtils.substringAfterLast(oldFile.get(0).getFilePath(), "/");
                    //1.根据状态state检查文件是否需要删除
                    if (StringUtils.equals(oldFile.get(0).getState(), "0") && StringUtils.equals(String.valueOf(fileResourceMap.get("state")), "2")) {
                        //在本地磁盘内删除此文件, 对Windows和Mac操作系统进行区分
                        if (StringUtils.containsIgnoreCase(System.getProperty("os.name"), GlobalStaticConstant.GLOBAL_WINDOWS_SYSTEM)) {
                            FileUtil.deleteFile(GlobalStaticConstant.GLOBAL_FILE_DISK + "\\" + GlobalStaticConstant.GLOBAL_FILE_FOLDER + "\\" + fileName);
                        }
                        if (StringUtils.containsIgnoreCase(System.getProperty("os.name"), GlobalStaticConstant.GLOBAL_MAC_SYSTEM)) {
                            FileUtil.deleteFile(GlobalStaticConstant.GLOBAL_FILE_DISK + "/" + GlobalStaticConstant.GLOBAL_FILE_FOLDER + "/" + fileName);
                        }
                    }
                    //2.根据filePath（里面最后文件id）来检查文件是否需要更新
                    if (!StringUtils.equals(oldFile.get(0).getFilePath(), String.valueOf(fileResourceMap.get("filePath")))) {
                        //先把本地的旧文件删除, 对Windows和Mac操作系统进行区分
                        if (StringUtils.containsIgnoreCase(System.getProperty("os.name"), GlobalStaticConstant.GLOBAL_WINDOWS_SYSTEM)) {
                            FileUtil.deleteFile(GlobalStaticConstant.GLOBAL_FILE_DISK + "\\" + GlobalStaticConstant.GLOBAL_FILE_FOLDER + "\\" + fileName);
                        }
                        if (StringUtils.containsIgnoreCase(System.getProperty("os.name"), GlobalStaticConstant.GLOBAL_MAC_SYSTEM)) {
                            FileUtil.deleteFile(GlobalStaticConstant.GLOBAL_FILE_DISK + "/" + GlobalStaticConstant.GLOBAL_FILE_FOLDER + "/" + fileName);
                        }
                        //开启多线程下载新文件，FileDownloader实现Runnable，重写run方法
                        service.execute(new FileDownloader(ServeIpConstant.IP + "/" + ServeIpConstant.FILE_FOLDER + "/" + String.valueOf(fileResourceMap.get("filePath"))));
                    }
                    //更新文件信息
                    fileResourceService.saveOrUpdateFileResource(setFileResourceProperties(fileResourceMap));
                } else { //如果文件不存在，开启线程下载文件
                    //开启多线程下载新文件，FileDownloader实现Runnable，重写run方法
                    service.execute(new FileDownloader(ServeIpConstant.IP + "/" + ServeIpConstant.FILE_FOLDER + String.valueOf(fileResourceMap.get("filePath"))));
                    //新增文件信息
                    fileResourceService.saveOrUpdateFileResource(setFileResourceProperties(fileResourceMap));
                }
            }
            service.shutdown();//关闭线程
        }
    }

    /**
     * FileResource对象赋值，获取接口返回的FileResource对象的值，创建一个空白的FileResource对象并对其进行赋值然后保存到数据库里.
     *
     * @param fileResourceMap 接口返回的一个FileResource对象
     * @return 一个被赋值的FileResource对象
     */
    public FileResource setFileResourceProperties(Map fileResourceMap) {
        FileResource fileResource = new FileResource();
        fileResource.setFileId(String.valueOf(fileResourceMap.get("fileId")));
        fileResource.setFileName(String.valueOf(fileResourceMap.get("fileName")));
        fileResource.setFilePath(String.valueOf(fileResourceMap.get("filePath")));
        fileResource.setFileSize(String.valueOf(fileResourceMap.get("fileSize")));
        fileResource.setPassword(String.valueOf(fileResourceMap.get("password")));
        fileResource.setState(String.valueOf(fileResourceMap.get("state")));
        fileResource.setSort(Double.valueOf(String.valueOf(fileResourceMap.get("sort"))).intValue());
        return fileResource;
    }

    /**
     * 从接口返回的Map集合中获取MeetIssueRelation对象（一个Map的列表集合），循环该集合，获取每条MeetIssueRelation对象的属性，
     * 创建新的MeetIssueRelation对象，对其属性进行赋值并保存到数据库里.
     *
     * @param dataMap 服务器返回的数据集合
     * @throws ApplicationException
     */
    public void hanleMeetIssueRelation(Map dataMap) throws ApplicationException {
        //处理会议议题关联信息
        List<Map> meetIssueRelationListMap = (List<Map>) dataMap.get("meetingIssueInfos");
        if (meetIssueRelationListMap != null && !meetIssueRelationListMap.isEmpty()) {
            Map meetIssueRelationMap = new HashMap();
            for (int i = 0; i < meetIssueRelationListMap.size(); i++) {
                meetIssueRelationMap = meetIssueRelationListMap.get(i);
                meetIssueRelationService.saveOrUpdateMeetIssueRelation(setMeetIssueRelationProperties(meetIssueRelationMap));
            }
        }
    }

    /**
     * MeetIssueRelation对象赋值，获取接口返回的MeetIssueRelation对象的值，创建一个空白的MeetIssueRelation对象并对其进行赋值然后保存到数据库里.
     *
     * @param meetIssueRelationMap 接口返回的一个MeetIssueRelation对象
     * @return 一个被赋值的MeetIssueRelation对象
     */
    public MeetIssueRelation setMeetIssueRelationProperties(Map meetIssueRelationMap) {
        MeetIssueRelation meetIssueRelation = new MeetIssueRelation();
        meetIssueRelation.setMeettingIssueId(String.valueOf(meetIssueRelationMap.get("meettingIssueId")));
        meetIssueRelation.setIssueId(String.valueOf(meetIssueRelationMap.get("issueId")));
        meetIssueRelation.setMeetingId(String.valueOf(meetIssueRelationMap.get("meetingId")));
        meetIssueRelation.setSort(Double.valueOf(String.valueOf(meetIssueRelationMap.get("sort"))).intValue());
        meetIssueRelation.setState(String.valueOf(meetIssueRelationMap.get("state")));
        return meetIssueRelation;
    }

    /**
     * 从接口返回的Map集合中获取IssueFileRelation对象（一个Map的列表集合），循环该集合，获取每条IssueFileRelation对象的属性，
     * 创建新的IssueFileRelation对象，对其属性进行赋值并保存到数据库里.
     *
     * @param dataMap 服务器返回的数据集合
     * @throws ApplicationException
     */
    public void hanleIssueFileRelation(Map dataMap) throws ApplicationException {
        //处理议题文件关联信息
        List<Map> issueFileRelationListMap = (List<Map>) dataMap.get("issueFileInfos");
        if (issueFileRelationListMap != null && !issueFileRelationListMap.isEmpty()) {
            Map issueFileRelationMap = new HashMap();
            for (int i = 0; i < issueFileRelationListMap.size(); i++) {
                issueFileRelationMap = issueFileRelationListMap.get(i);
                issueFileRelationService.saveOrUpdateIssueFileRelation(setIssueFileRelationProperties(issueFileRelationMap));
            }
        }
    }

    /**
     * IssueFileRelation对象赋值，获取接口返回的IssueFileRelation对象的值，创建一个空白的IssueFileRelation对象并对其进行赋值然后保存到数据库里.
     *
     * @param issueFileRelationMap 接口返回的一个IssueFileRelation对象
     * @return 一个被赋值的IssueFileRelation对象
     */
    public IssueFileRelation setIssueFileRelationProperties(Map issueFileRelationMap) {
        IssueFileRelation issueFileRelation = new IssueFileRelation();
        issueFileRelation.setIssueFileInfo(String.valueOf(issueFileRelationMap.get("issueFileInfo")));
        issueFileRelation.setIssueId(String.valueOf(issueFileRelationMap.get("issueId")));
        issueFileRelation.setFileId(String.valueOf(issueFileRelationMap.get("fileId")));
        issueFileRelation.setFileName(String.valueOf(issueFileRelationMap.get("fileName")));
        issueFileRelation.setSort(Double.valueOf(String.valueOf(issueFileRelationMap.get("sort"))).intValue());
        issueFileRelation.setState(String.valueOf(issueFileRelationMap.get("state")));
        return issueFileRelation;
    }

    /**
     * 从接口返回的Map集合中获取MeetUserRelation对象（一个Map的列表集合），循环该集合，获取每条MeetUserRelation对象的属性，
     * 创建新的MeetUserRelation对象，对其属性进行赋值并保存到数据库里.
     *
     * @param dataMap 服务器返回的数据集合
     * @throws ApplicationException
     */
    public void handleMeetUserRelation(Map dataMap) throws ApplicationException {
        //处理会议用户关联信息
        List<Map> meetUserRelationListMap = (List<Map>) dataMap.get("meetingUserInfos");
        if (meetUserRelationListMap != null && !meetUserRelationListMap.isEmpty()) {
            Map meetUserRelationMap = new HashMap();
            for (int i = 0; i < meetUserRelationListMap.size(); i++) {
                meetUserRelationMap = meetUserRelationListMap.get(i);
                meetUserRelationService.saveOrUpdateMeetUserRelation(setMeetUserRelationProperties(meetUserRelationMap));
            }
        }
    }

    /**
     * MeetUserRelation对象赋值，获取接口返回的MeetUserRelation对象的值，创建一个空白的MeetUserRelation对象并对其进行赋值然后保存到数据库里.
     *
     * @param meetUserRelationMap 接口返回的一个MeetUserRelation对象
     * @return 一个被赋值的MeetUserRelation对象
     */
    public MeetUserRelation setMeetUserRelationProperties(Map meetUserRelationMap) {
        MeetUserRelation meetUserRelation = new MeetUserRelation();
        meetUserRelation.setMeetingUserId(String.valueOf(meetUserRelationMap.get("meetingUserId")));
        meetUserRelation.setMeetingId(String.valueOf(meetUserRelationMap.get("meetingId")));
        meetUserRelation.setUserId(String.valueOf(meetUserRelationMap.get("userId")));
        meetUserRelation.setState(String.valueOf(meetUserRelationMap.get("state")));
        return meetUserRelation;
    }

    /**
     * 从接口返回的Map集合中获取FileUserRelation对象（一个Map的列表集合），循环该集合，获取每条FileUserRelation对象的属性，
     * 创建新的FileUserRelation对象，对其属性进行赋值并保存到数据库里.
     *
     * @param dataMap 服务器返回的数据集合
     * @throws ApplicationException
     */
    public void handleFileUserRelation(Map dataMap) throws ApplicationException {
        //处理文件用户关联信息
        List<Map> fileUserRelationListMap = (List<Map>) dataMap.get("fileUserInfos");
        if (fileUserRelationListMap != null && !fileUserRelationListMap.isEmpty()) {
            Map fileUserRelationMap = new HashMap();
            for (int i = 0; i < fileUserRelationListMap.size(); i++) {
                fileUserRelationMap = fileUserRelationListMap.get(i);
                fileUserRelationService.saveOrUpdateFileUserRelation(setFileUserRelationProperties(fileUserRelationMap));
            }
        }
    }

    /**
     * FileUserRelation对象赋值，获取接口返回的FileUserRelation对象的值，创建一个空白的FileUserRelation对象并对其进行赋值然后保存到数据库里.
     *
     * @param fileUserRelationMap 接口返回的一个FileUserRelation对象
     * @return 一个被赋值的FileUserRelation对象
     */
    public FileUserRelation setFileUserRelationProperties(Map fileUserRelationMap) {
        FileUserRelation fileUserRelation = new FileUserRelation();
        fileUserRelation.setUserFileId(String.valueOf(fileUserRelationMap.get("userFileId")));
        fileUserRelation.setFileId(String.valueOf(fileUserRelationMap.get("fileId")));
        fileUserRelation.setUserId(String.valueOf(fileUserRelationMap.get("userId")));
        fileUserRelation.setState(String.valueOf(fileUserRelationMap.get("state")));
        return fileUserRelation;
    }

    /**
     * 从接口返回的Map集合中获取Annotation对象（一个Map的列表集合），循环该集合，获取每条Annotation对象的属性，
     * 创建新的Annotation对象，对其属性进行赋值并保存到数据库里.
     *
     * @param dataMap 服务器返回的数据集合
     * @throws ApplicationException
     */
    public void handleAnnotation(Map dataMap) throws ApplicationException {
        //处理批注信息       
        List<Map> annotationListMap = (List<Map>) dataMap.get("annotations");
        if (annotationListMap != null && !annotationListMap.isEmpty()) {
            Map annotationMap = new HashMap();
            for (int i = 0; i < annotationListMap.size(); i++) {
                annotationMap = annotationListMap.get(i);
                annotationService.saveOrUpdateAnnotation(setAnnotationProperties(annotationMap));
            }
        }
    }

    /**
     * Annotation对象赋值，获取接口返回的Annotation对象的值，创建一个空白的Annotation对象并对其进行赋值然后保存到数据库里.
     *
     * @param annotationMap 接口返回的一个Annotation对象
     * @return 一个被赋值的Annotation对象
     */
    public Annotation setAnnotationProperties(Map annotationMap) {
        Annotation annotation = new Annotation();
        annotation.setAnnoId(String.valueOf(annotationMap.get("annoId")));
        annotation.setAnnoDate(String.valueOf(annotationMap.get("annoDate")));
        annotation.setAnnoType(String.valueOf(annotationMap.get("annoType")));
        annotation.setContent(String.valueOf(annotationMap.get("content")));
        annotation.setFileId(String.valueOf(annotationMap.get("fileId")));
        annotation.setHeight(String.valueOf(annotationMap.get("height")));
        annotation.setPageNum(Double.valueOf(String.valueOf(annotationMap.get("pageNum"))).intValue());
        annotation.setState(String.valueOf(annotationMap.get("state")));
        annotation.setUserId(String.valueOf(annotationMap.get("userId")));
        annotation.setWidth(String.valueOf(annotationMap.get("width")));
        annotation.setxPoint(String.valueOf(annotationMap.get("xPoint")));
        annotation.setyPoint(String.valueOf(annotationMap.get("yPoint")));
        return annotation;
    }

    /**
     * 从接口返回的Map集合中获取updateDate的值，并将这两个值存入MeetInfo表中.
     *
     * @param dataMap 服务器返回的数据集合
     * @throws ApplicationException
     */
    public void handleMisc(Map dataMap) throws ApplicationException {
        //处理更新时间
        String updateDate = String.valueOf(dataMap.get("updateDate"));
        if (updateDate != null) {
            MeetInfo info = meetInfoService.getMeetInfoByMeetId(GlobalStaticConstant.GLOBAL_SELECTED_MEETID);
            if (info != null) {
                info.setUpdateDate(updateDate);
                meetInfoService.saveOrUpdateMeetInfo(info);
            }
        }
    }
}
