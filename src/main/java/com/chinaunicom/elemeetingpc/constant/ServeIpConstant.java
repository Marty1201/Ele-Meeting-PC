package com.chinaunicom.elemeetingpc.constant;

/**
 * 服务信息和接口地址常量
 *
 * @author zhaojunfeng, chenxi
 */
public class ServeIpConstant {

    //服务器ip
    public static final String IP = "http://192.168.1.162";

    //服务器端口
    public static final String PORT = "80";

    //项目名称
    public static final String PROJECT_NAME = "UnicomGlobalEleMeetingCloud";

    //服务器存放文件的文件夹
    public static final String FILE_FOLDER = "fileInfo";

    //服务器ip地址（完整）
    public static final String SERVICE_IP = "http://192.168.1.162:80/UnicomGlobalEleMeetingCloud";

    //登录接口
    public static final String LOGIN_INTERFACE = "/mLogin.do";

    //选择机构接口
    public static final String SELECT_ORGAN_INTERFACE = "/mLogin.do?action=defaultMeeting";

    //修改密码接口
    public static final String RESET_PASSWORD_INTERFACE = "/mUser.do?action=resetPassword";

    //根据会议ID获取子会议、议题、文件等信息
    public static final String MEETING_OF_ORGAN_INTEGERFACE = "/mMeet.do?action=getMeeting";
    
    //会议通知列表接口
    public static final String MEETING_NOTICE_LIST_INTERFACE = "/mNoti.do";
    
    //通知详情接口
    public static final String MEETING_NOTICE_DETAIL_INTERFACE = "/mNoti.do?action=read";

    /**
     * 获取服务接口地址全路径
     *
     * @param interface_constant接口地址常量
     * @return
     */
    public static String getServeInterfaceFullAddress(String interface_constant) {
        return SERVICE_IP + interface_constant;
    }

    /**
     * 登录服务接口
     *
     * @return
     */
    public static String loginServicePath() {
        return getServeInterfaceFullAddress(LOGIN_INTERFACE);
    }

    /**
     * 选择机构接口
     *
     * @return
     */
    public static String selectOrganServicePath() {
        return getServeInterfaceFullAddress(SELECT_ORGAN_INTERFACE);
    }

    /**
     * 修改密码接口
     *
     * @return
     */
    public static String resetPasswordServicePath() {
        return getServeInterfaceFullAddress(RESET_PASSWORD_INTERFACE);
    }

    /**
     * 获取机构的会议列表信息
     *
     * @return
     */
    public static String meetingOfOrganServicePath() {
        return getServeInterfaceFullAddress(MEETING_OF_ORGAN_INTEGERFACE);
    }
    
    /**
     * 会议通知列表接口
     * @return 
     */
    public static String noticeListServicePath(){
        return getServeInterfaceFullAddress(MEETING_NOTICE_LIST_INTERFACE);
    }
    
    /**
     * 获取通知详情接口
     * @return 
     */
    public static String noticeDetailServicePath(){
        return getServeInterfaceFullAddress(MEETING_NOTICE_DETAIL_INTERFACE);
    }
}
