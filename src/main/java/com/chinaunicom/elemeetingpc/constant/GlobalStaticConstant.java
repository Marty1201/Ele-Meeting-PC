
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
    
    //全局更新机构名称（服务器端机构的名称）
    public static String GLOBAL_ORGANINFO_ORGANIZATIONNAME = "";
    
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
    
    //判断用户是否点击了跟读按钮并处于跟读/取消跟读状态（aka false=未跟读状态，true=跟读状态）
    public static boolean GLOBAL_ISFOLLOWINGCLICKED = false;
    
    //判断用户是否点击了主讲按钮并处于主讲/取消主讲状态（aka false=未主讲状态，true=主讲状态）
    public static boolean GLOBAL_ISSPEAKINGCLICKED = false;
    
    //判断是否开始消费消息（注意：消费消息在程序里只需要启动一次，然后通过代码逻辑判断是否需要处理消息）
    public static boolean GLOBAL_ISSTARTCONSUMING = false;
    
    //跟读操作
    public static String GLOBAL_FOLLOWING = "following";
    
    //主讲操作
    public static String GLOBAL_SPEAKING = "speaking";
    
    //翻页操作
    public static String GLOBAL_TURNPAGE = "turnPage";
    
    //发起同步操作
    public static String GLOBAL_APPLYFORPRESENTER = "applyForPresenter";
    
    //结束同步操作
    public static String GLOBAL_GIVEUPPRESENTER = "giveupPresenter";
}
