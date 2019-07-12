package com.chinaunicom.elemeetingpc.constant;

/**
 *服务接口地址常量
 * @author zhaojunfeng
 */
public class ServeIpConstant {
    
    //服务器ip地址
    private static final String SERVICE_IP = "http://192.168.1.162:80/UnicomGlobalEleMeetingCloud";
    
    //登录接口
    private static final String LOGIN_INTERFACE = "/mLogin.do";
    
    //选择机构接口
    private static final String SELECT_ORGAN_INTERFACE= "/mLogin.do?action=defaultMeeting";
    
    //修改密码接口
    private static final String RESET_PASSWORD_INTERFACE="/mUser.do?action=resetPassword";
    
    //获取机构的会议列表信息
    private static final String MEETING_OF_ORGAN_INTEGERFACE="/mMeet.do";
    
    /**
     * 获取服务接口地址全路径
     * @param interface_constant接口地址常量
     * @return 
     */
    private static String getServeInterfaceFullAddress(String interface_constant){
        return SERVICE_IP+interface_constant;
    }
    
    /**
     * 登录服务接口
     * @return 
     */
    public static String loginServicePath(){
        return getServeInterfaceFullAddress(LOGIN_INTERFACE);
    }
    
    /**
     * 选择机构接口
     * @return 
     */
    public static String selectOrganServicePath(){
        return getServeInterfaceFullAddress(SELECT_ORGAN_INTERFACE);
    }
    
    /**
     * 修改密码接口
     * @return 
     */
    public static String resetPasswordServicePath(){
        return getServeInterfaceFullAddress(RESET_PASSWORD_INTERFACE);
    }
    
    /**
     * 获取机构的会议列表信息
     * @return 
     */
    public static String meetingOfOrganServicePath(){
        return getServeInterfaceFullAddress(MEETING_OF_ORGAN_INTEGERFACE);
    }
    
    
    
    
    
}
