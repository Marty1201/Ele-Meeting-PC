
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
    public static String GLOBAL_USERINFO_LOGINNAME = "";
    
    //当前登录用户名称
    public static String GLOBAL_USERINFO_USERNAME = "";
    
    //全局更新机构id（服务器端机构的id）
    public static String GLOBAL_ORGANINFO_ORGANIZATIONID = "";
    
    //全局更新机构名称（服务器端机构的名称）
    public static String GLOBAL_ORGANINFO_ORGANIZATIONNAME = "";
    
    //全局更新机构中对应的用户Id（服务器端用户的id）
    public static String GLOBAL_ORGANINFO_OWNER_USERID = "";
    
    //当前选择的会议ID
    public static String GLOBAL_SELECTED_MEETID = "";
    
    //本地存放文件的磁盘
    public static String GLOBAL_FILE_DISK = System.getProperty("user.home");
    //public static String GLOBAL_FILE_DISK = "D:";

    //本地存放文件的文件夹
    public static String GLOBAL_FILE_FOLDER = "MeetingFiles";
    
    //会议通知每页条数
    public static String GLOBAL_NOTICE_PAGESIZE = "10";
    
    //微软电脑操作系统版本
    public static String GLOBAL_WINDOWS_SYSTEM = "Windows";
    
    //苹果电脑操作系统版本
    public static String GLOBAL_MAC_SYSTEM = "Mac";
    
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
    
    //同步阅读文件垂直滑动
    public static String GLOBAL_VSCROLL = "verticalScroll";
    
    //同步阅读文件水平滑动
    public static String GLOBAL_HSCROLL = "horizontalScroll";
    
    //同步阅读文件放大
    public static String GLOBAL_ZOOMIN = "zoomIn";

    //同步阅读文件缩小
    public static String GLOBAL_ZOOMOUT = "zoomOut";
    
    //同步阅读文件适应竖屏
    public static String GLOBAL_FITHEIGHT = "fitHeight";
    
    //同步阅读文件适应横屏
    public static String GLOBAL_FITWIDTH = "fitWidth";
    
    //同步阅读PC标识(for ios and android)
    public static String GLOBAL_PCSYNCFLAG = "pcPlatform";
    
    //同步阅读ios标识
    public static String GLOBAL_IOSSYNCFLAG = "iosPlatform";
    
    //同步阅读android标识
    public static String GLOBAL_ANDROIDSYNCFLAG = "androidPlatform";
}
