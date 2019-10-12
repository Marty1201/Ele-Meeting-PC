
package com.chinaunicom.elemeetingpc.constant;

/**
 * 全局静态常量.
 *
 * @author zhaojunfeng, chenxi
 */
public class GlobalStaticConstant {
    
    //当前登录用户Id（本地数据库用户的id）
    public static int GLOBAL_USERINFO_ID = 0;
    
    //当前登录用户登陆账号
    public static String GLOBAL_USERINFO_LOGINNAME = "1";
    
    //当前登录用户名称
    public static String GLOBAL_USERINFO_USERNAME = "1";
    
    //全局更新机构id（服务器端机构的id）
    public static String GLOBAL_ORGANINFO_ORGANIZATIONID = "1";
    
    //全局更新机构中对应的用户Id（服务器端用户的id）
    public static String GLOBAL_ORGANINFO_OWNER_USERID = "1";
    
    //当前选择的会议ID
    public static String GLOBAL_SELECTED_MEETID = "";
    
    //本地存放文件的磁盘
    public static String GLOBAL_FILE_DISK = "D:";

    //本地存放文件的文件夹
    public static String GLOBAL_FILE_FOLDER = "MeetingFiles";
    
    //会议通知每页条数
    public static String GLOBAL_NOTICE_PAGESIZE = "10";
}
