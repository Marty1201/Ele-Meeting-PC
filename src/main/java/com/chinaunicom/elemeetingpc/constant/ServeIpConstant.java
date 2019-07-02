package com.chinaunicom.elemeetingpc.constant;

/**
 *服务接口地址常量
 * @author zhaojunfeng
 */
public class ServeIpConstant {
    
    //服务器ip地址
    private static final String SERVICE_IP = "http://192.168.1.162:80/";
    
    //登录接口
    private static final String LOGIN_INTERFACE = "UnicomGlobalEleMeetingCloud/mLogin.do";
    
    //选择机构接口
    private static final String SELECT_ORGAN_INTERFACE= "EleMeetingCloudV3/mLogin.do?action=defaultMeeting";
    
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
    
    
    
    
    
}
