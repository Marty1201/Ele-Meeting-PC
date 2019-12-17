package com.chinaunicom.elemeetingpc.controllers;

import com.chinaunicom.elemeetingpc.constant.GlobalStaticConstant;
import com.chinaunicom.elemeetingpc.constant.ServeIpConstant;
import com.chinaunicom.elemeetingpc.constant.StatusConstant;
import com.chinaunicom.elemeetingpc.database.models.IdentityInfo;
import com.chinaunicom.elemeetingpc.database.models.OrganInfo;
import com.chinaunicom.elemeetingpc.database.models.UserInfo;
import com.chinaunicom.elemeetingpc.service.IdentityInfoService;
import com.chinaunicom.elemeetingpc.service.OrganInfoService;
import com.chinaunicom.elemeetingpc.service.UserInfoService;
import com.chinaunicom.elemeetingpc.utils.GsonUtil;
import com.chinaunicom.elemeetingpc.utils.HashUtil;
import com.chinaunicom.elemeetingpc.utils.HttpClientUtil;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

/**
 * 当用户点击登录按钮时触发LoginServiceController，此控制器负责与后台的登录接口进行数据交互处理（mLogin.do），主要包括以下操作：
 * 1、接口请求参数封装； 2、接口请求； 3、接口数据解析； 4、接口数据保存本地数据库.
 *
 * @author zhaojunfeng, chenxi
 */
public class LoginServiceController {

    private UserInfoService userInfoService;

    private OrganInfoService organInfoService;

    private IdentityInfoService identityInfoService;

    private UserInfo userInfo;
    
    private OrganInfo organInfo;

    private Map userInfoMap;

    private Map organInfoMap;

    private List<OrganInfo> remoteOrganList;

    public LoginServiceController() {

        userInfoService = new UserInfoService();

        organInfoService = new OrganInfoService();

        identityInfoService = new IdentityInfoService();

        userInfo = new UserInfo();

        userInfoMap = new HashMap();

        organInfoMap = new HashMap();

        //用来记录接口返回的组织机构信息
        remoteOrganList = new ArrayList<>();
        
    }

    /**
     * 登录接口逻辑处理.
     *
     * @param loginName 账号 不为空
     * @param password 密码 不为空
     * @param regiCode 注册码 不为空
     * @return resultMap 结果map
     * @throws Exception
     */
    public Map<String, String> login(String loginName, String password, String regiCode) throws Exception {
        Map<String, String> resultMap = new HashMap();
        //封装参数
        String param = buildJsonRequestString(loginName, password, regiCode);
        //访问接口
        String response = HttpClientUtil.getInstance().getResponseBodyAsString(ServeIpConstant.loginServicePath(), param);
        if (StringUtils.isNotBlank(response)) {
            //把json字符串转成map
            Map tempMap = GsonUtil.getMap(response);
            String resultCode = String.valueOf(tempMap.get("resultCode"));
            String resultDesc = String.valueOf(tempMap.get("resultDesc"));
            resultMap.put("code", resultCode);
            resultMap.put("desc", resultDesc);
            if (StatusConstant.RESULT_CODE_SUCCESS.equals(resultCode)) {
                //取出真正有用的数据
                String resultData = String.valueOf(tempMap.get("resultData"));
                Map dataMap = GsonUtil.getMap(resultData);;
                parseDataMap(dataMap);
            }
        }
        return resultMap;
    }

    /**
     * 封装请求参数为json字符串，示例：{loginName:'changlu',password:'757f7fc9ad2ec1a2951fbf3a7bbc2144',deviceToken:'60393D7BF54A',updateDate:''}.
     *
     * @param loginName 登陆账号 不为空
     * @param password 登陆密码 不为空
     * @param regiCode 设备注册码 不为空
     * @param updateDate 空
     * @return requestString 请求主体
     */
    private String buildJsonRequestString(String loginName, String password, String regiCode) {
        String md5Password = HashUtil.toMD5(password);
        String requestString = "{loginName:'" + loginName + "',password:'" + md5Password + "',deviceToken:'" + regiCode + "',updateDate:''}";
        return requestString;
    }

    /**
     * 解析接口返回的Map数据集合，需要处理的数据类型主要包括以下几种： 1、用户信息 2、机构信息 3、身份信息
     * 此接口返回的数据是分层嵌套的，即机构套在用户里面，身份套到机构里面，且此接口返回的数据没有state状态，
     * 因此需要一些操作需要执行物理删除.
     *
     * @param dataMap 服务器返回的数据集合
     * @throws ApplicationException
     */
    private void parseDataMap(Map dataMap) throws ApplicationException {
        //1、处理用户信息
        handleUserInfo(dataMap);
    }

    /**
     * 从接口返回的Map集合中获取UserInfo对象（一个Map集合），更新或创建新的UserInfo对象，对其属性进行赋值并保存到数据库里.
     *
     * @param dataMap 服务器返回的数据集合
     * @throws ApplicationException
     */
    public void handleUserInfo(Map dataMap) throws ApplicationException {
        //从接口返回的数据中获取userInfo对象
        userInfoMap = (Map) dataMap.get("userInfo");
        if (userInfoMap != null) {
            //查询当前登陆用户是否已在本地数据库里存在
            List<UserInfo> userList = userInfoService.queryUserInfos("loginName", String.valueOf(userInfoMap.get("loginName")));
            //如果用户已存在，则更新用户信息
            if (!userList.isEmpty()) {
                userInfo = userList.get(0);
                userInfoService.saveOrUpdateUserInfo(setUserInfoProperties(userInfo, userInfoMap));
            } else {
                //如果用户不存在，则新建用户
                userInfoService.saveOrUpdateUserInfo(setUserInfoProperties(userInfo, userInfoMap));
            }
            //在全局常量里记录当前登录人的id和名称
            GlobalStaticConstant.GLOBAL_USERINFO_ID = userInfo.getId();
            GlobalStaticConstant.GLOBAL_USERINFO_USERNAME = userInfo.getUserName();
            //2、处理机构信息
            handleOrganInfo();
        }
    }

    /**
     * UserInfo对象赋值，获取接口返回的UserInfo对象的值，并对其进行赋值然后保存到数据库里，UserInfo对象可以是新建的或者已经存在的.
     *
     * @param userInfo 用户对象，可以是一个新的或已存在的对象
     * @param userInfoMap 接口返回的一个UserInfo对象
     * @return UserInfo 一个被赋值的UserInfo对象
     */
    public UserInfo setUserInfoProperties(UserInfo userInfo, Map userInfoMap) {
        userInfo.setLoginName(String.valueOf(userInfoMap.get("loginName")));
        userInfo.setPassword(String.valueOf(userInfoMap.get("password")));
        userInfo.setUserName(String.valueOf(userInfoMap.get("userName")));
        userInfo.setEnglishName(String.valueOf(userInfoMap.get("englishName")));
        userInfo.setPhone(String.valueOf(userInfoMap.get("phone")));
        userInfo.setState(String.valueOf(userInfoMap.get("state")));
        userInfo.setSexName(String.valueOf(userInfoMap.get("sexName")));
        userInfo.setSexEnglishName(String.valueOf(userInfoMap.get("sexEnglishName")));
        userInfo.setSort(String.valueOf(userInfoMap.get("sort")));
        return userInfo;
    }

    /**
     * 从接口返回的Map(注意是在userInfoMap里面)集合中获取OrganInfo对象（一个Map集合），更新或创建新的OrganInfo对象，对其属性进行赋值并保存到数据库里.
     *
     * @throws ApplicationException
     */
    public void handleOrganInfo() throws ApplicationException {
        List<Map> organInfoMapList = (List<Map>) userInfoMap.get("organs");
        if (!organInfoMapList.isEmpty()) {
            for (int i = 0; i < organInfoMapList.size(); i++) {
                //需要每次都新建一个机构对象
                organInfo = new OrganInfo();
                organInfoMap = organInfoMapList.get(i);
                //根据机构id和用户id查询数据库里是否已存在同样的数据
                List<OrganInfo> organList = organInfoService.queryOrganInfosByMap(String.valueOf(organInfoMap.get("organizationId")), String.valueOf(organInfoMap.get("userId")));
                //如果机构已存在，则更新机构信息
                if (!organList.isEmpty()) {
                    organInfo = organList.get(0);
                    organInfoService.saveOrUpdateOrganInfo(setOrganInfoProperties(organInfo, organInfoMap, userInfo));
                    remoteOrganList.add(setOrganInfoProperties(organInfo, organInfoMap, userInfo));
                } else { //如果机构不存在，则新建机构
                    organInfoService.saveOrUpdateOrganInfo(setOrganInfoProperties(organInfo, organInfoMap, userInfo));
                    remoteOrganList.add(setOrganInfoProperties(organInfo, organInfoMap, userInfo));
                }
                //3、处理身份信息
                handleIdentityInfo();
            }
        }
        //4、最后需要处理删除本地机构表和身份表与接口不同步的数据
        handleDiffOrganInfo();
    }

    /**
     * OrganInfo对象赋值，获取接口返回的OrganInfo对象的值，并对其进行赋值然后保存到数据库里，OrganInfo对象可以是新建的或者已经存在的.
     *
     * @param organInfo 机构对象，可以是一个新的或已存在的对象
     * @param organInfoMap 接口返回的一个OrganInfo对象
     * @param userInfo 用户对象，不为空
     * @return OrganInfo 一个被赋值的OrganInfo对象
     */
    public OrganInfo setOrganInfoProperties(OrganInfo organInfo, Map organInfoMap, UserInfo userInfo) {
        organInfo.setOrganizationName(String.valueOf(organInfoMap.get("organizationName")));
        organInfo.setUserId(String.valueOf(organInfoMap.get("userId")));
        organInfo.setState(String.valueOf(organInfoMap.get("state")));
        organInfo.setOrganizationId(String.valueOf(organInfoMap.get("organizationId")));
        organInfo.setOrganizationEnglishName(String.valueOf(organInfoMap.get("organizationEnglishName")));
        organInfo.setUserInfo(userInfo);
        return organInfo;
    }

    /**
     * 从接口返回的Map(注意是在organInfoMap里面)集合中获取IdentityInfo对象（一个Map集合），删除并创建新的IdentityInfo对象，对其属性进行赋值并保存到数据库里
     * 这里做物理删除会有一个小问题，就是每次登陆都会频繁的进行identity的删除的新建，因此identity表的id会变的非常的大，还有优化的空间.
     *
     * @throws ApplicationException
     */
    public void handleIdentityInfo() throws ApplicationException {
        Map identityInfoMap = new HashMap();
        List<Map> identityInfoListMap = (List<Map>) organInfoMap.get("identityList");
        if (!identityInfoListMap.isEmpty()) {
            //首先判断当前机构下是否存在身份信息
            List<IdentityInfo> identityList = identityInfoService.queryIdentityInfos("ORGANINFOR_ID", organInfo.getId());
            if (!identityList.isEmpty()) {
                //如存在身份信息，则全部删除（因为接口没有返回身份的state，所以只能物理删除）
                identityInfoService.deleteAllIdentityInfos(identityList);
            }
            for (int k = 0; k < identityInfoListMap.size(); k++) {
                identityInfoMap = identityInfoListMap.get(k);
                IdentityInfo identityInfo = new IdentityInfo();
                identityInfoService.saveOrUpdateIdentityInfo(setIdentityInfoProperties(identityInfo, identityInfoMap, organInfo));//新建对象信息
            }
        }
    }

    /**
     * IdentityInfo对象赋值，获取接口返回的IdentityInfo对象的值，并对新建的IdentityInfo对象进行赋值然后保存到数据库里.
     *
     * @param identityInfo 身份对象，一个新建的对象
     * @param identityInfoMap 接口返回的一个identityInfo对象
     * @param organInfo 机构对象，不为空
     * @return IdentityInfo 一个被赋值的IdentityInfo对象
     */
    public IdentityInfo setIdentityInfoProperties(IdentityInfo identityInfo, Map identityInfoMap, OrganInfo organInfo) {
        String identityName = String.valueOf(identityInfoMap.get("identityName"));
        String identityEnglishName = String.valueOf(identityInfoMap.get("identityEnglishName"));
        identityInfo.setIdentityName(identityName);
        identityInfo.setIdentityEnglishName(identityEnglishName);
        identityInfo.setOrganInfo(organInfo);
        return identityInfo;
    }

    /**
     * 此方法删除本地机构表和身份表与接口不同步的数据.
     *
     * @throws ApplicationException
     */
    public void handleDiffOrganInfo() throws ApplicationException {
        //查出用户所属的所有机构
        List<OrganInfo> localOrganList = organInfoService.queryOrganInfosByUserId("USERINFO_ID", userInfo.getId());
        //如果接口返回的机构数小于本地存储的机构数，则服务端用户所属机构减少了，本地也要删除对应的机构和身份
        if (remoteOrganList.size() < localOrganList.size()) {
            //机构去重
            localOrganList.removeAll(remoteOrganList);
            for (OrganInfo localOrganInfo : localOrganList) {
                //首先判断当前机构下是否存在身份信息
                List<IdentityInfo> localIdentityList = identityInfoService.queryIdentityInfos("ORGANINFOR_ID", localOrganInfo.getId());
                if (!localIdentityList.isEmpty()) {
                    //需要先删除机构下的身份
                    identityInfoService.deleteAllIdentityInfos(localIdentityList);
                }
            }
            //最后再删除不同步的机构（因为接口没有按照state规则返回数据，只能物理删除）
            organInfoService.deleteAllOrganInfos(localOrganList);
        }
    }
}
