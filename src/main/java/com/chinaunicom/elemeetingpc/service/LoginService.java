package com.chinaunicom.elemeetingpc.service;

import com.chinaunicom.elemeetingpc.constant.GlobalStaticConstant;
import com.chinaunicom.elemeetingpc.constant.ServeIpConstant;
import com.chinaunicom.elemeetingpc.constant.StatusConstant;
import com.chinaunicom.elemeetingpc.database.models.IdentityInfo;
import com.chinaunicom.elemeetingpc.database.models.OrganInfo;
import com.chinaunicom.elemeetingpc.database.models.UserInfo;
import com.chinaunicom.elemeetingpc.modelFx.IdentityInfoModel;
import com.chinaunicom.elemeetingpc.modelFx.OrganInfoModel;
import com.chinaunicom.elemeetingpc.modelFx.UserInfoModel;
import com.chinaunicom.elemeetingpc.utils.GsonUtil;
import com.chinaunicom.elemeetingpc.utils.HashUtil;
import com.chinaunicom.elemeetingpc.utils.HttpClientUtil;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;

/**
 * 登录业务逻辑service.
 *
 * @author zhaojunfeng, chenxi
 */
public class LoginService {

    private UserInfoModel userInfoModel;

    private OrganInfoModel organInfoModel;

    private IdentityInfoModel identityInfoModel;

    /**
     * 登录
     *
     * @param loginName
     * @param password
     * @param regiCode
     * @return
     */
    public Map<String, String> login(String loginName, String password, String regiCode) {
        Map<String, String> resultMap = new HashMap();
        try {
            //封装参数
            String param = this.fzParam(loginName, password, regiCode);
            //访问接口
            String result = HttpClientUtil.getInstance().getResponseBodyAsString(ServeIpConstant.loginServicePath(), param);
            if(StringUtils.isNotBlank(result)){
                //把json字符串转成map
                Map temp_map = GsonUtil.getMap(result);
                String result_code = String.valueOf(temp_map.get("resultCode"));
                String resultDesc = String.valueOf(temp_map.get("resultDesc"));
                resultMap.put("code", result_code);
                resultMap.put("desc", resultDesc);
                if (StatusConstant.RESULT_CODE_SUCCESS.equals(result_code)) {
                    String resultData = String.valueOf(temp_map.get("resultData"));
                    Map dataMap = GsonUtil.getMap(resultData);;
                    parseFzDataMap(dataMap);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(LoginService.class.getName()).log(Level.SEVERE, null, ex);
            resultMap.put("code", StatusConstant.RESULT_CODE_FAIL);
            resultMap.put("desc", "登录异常");
        }
        return resultMap;
    }

    /**
     * 封装参数为json字符串
     *
     * @param loginName
     * @param password
     * @param regiCode
     * @param updateDate
     * 示例：{loginName:'changlu',password:'757f7fc9ad2ec1a2951fbf3a7bbc2144',deviceToken:'60393D7BF54A',updateDate:''}
     * @return
     */
    private String fzParam(String loginName, String password, String regiCode) {
        String md5password = HashUtil.toMD5(password);
        String resultString = "{loginName:'" + loginName + "',password:'" + md5password + "',deviceToken:'"+ regiCode +"',updateDate:''}";
        return resultString;
    }

    /**
     * 解析数据，封装userInfo对象、organInfo对象、identityInfo对象，并调Model层(保存/修改/删除)的数据库操作.
     *
     * @param dataMap 接口返回的数据对象
     */
    private void parseFzDataMap(Map dataMap) throws ApplicationException {
        //用于存放接口解析对象
        Map userInfoMap = new HashMap();
        Map organInfoMap = new HashMap();
        Map identityInfoMap = new HashMap();
        List<OrganInfo> remoteOrganList = new ArrayList<>();//用来记录接口返回的组织机构信息

        //1、处理用户信息
        UserInfo userInfo = new UserInfo();
        userInfoMap = (Map) dataMap.get("userInfo");//解析userInfo对象
        if (userInfoMap != null) {
            this.userInfoModel = new UserInfoModel();
            List<UserInfo> userList = userInfoModel.queryUserInfos("loginName", String.valueOf(userInfoMap.get("loginName")));//查询登陆用户是否已在数据库里存在
            if (!userList.isEmpty()) {
                userInfo = userList.get(0);
                userInfoModel.saveOrUpdateUserInfo(setUserInfoProperties(userInfo, userInfoMap));//解析并封装用户信息,更新对象信息
                //在全局常量里记录当前登录人的id
                GlobalStaticConstant.GLOBAL_USERINFO_ID = userInfo.getId();
            } else {
                userInfoModel.saveOrUpdateUserInfo(setUserInfoProperties(userInfo, userInfoMap));//解析并封装用户信息,新建对象信息
                //在全局常量里记录当前登录人的id
                GlobalStaticConstant.GLOBAL_USERINFO_ID = userInfo.getId();
            }
        }
        //2、处理机构信息
        List<Map> organListMap = (List<Map>) userInfoMap.get("organs");
        if (!organListMap.isEmpty()) {
            this.organInfoModel = new OrganInfoModel();
            for (int i = 0; i < organListMap.size(); i++) {
                organInfoMap = organListMap.get(i);
                OrganInfo organInfo = new OrganInfo();
                Map fieldValues = new HashMap<>();
                fieldValues.put("organizationId", String.valueOf(organInfoMap.get("organizationId")));
                fieldValues.put("userId", String.valueOf(organInfoMap.get("userId")));
                List<OrganInfo> organList = organInfoModel.queryOrganInfosByMap(fieldValues);//根据机构id和用户id查询数据库里是否已存在同样的数据
                if (!organList.isEmpty()) {
                    organInfo = organList.get(0);
                    organInfoModel.saveOrUpdateOrganInfo(setOrganInfoProperties(organInfo, organInfoMap, userInfo));//解析并封装机构信息,更新对象信息
                    remoteOrganList.add(setOrganInfoProperties(organInfo, organInfoMap, userInfo));
                } else {
                    organInfoModel.saveOrUpdateOrganInfo(setOrganInfoProperties(organInfo, organInfoMap, userInfo));//解析并封装机构信息,新建对象信息
                    remoteOrganList.add(setOrganInfoProperties(organInfo, organInfoMap, userInfo));
                }
                //3、处理身份信息
                List<Map> identityListMap = (List<Map>) organInfoMap.get("identityList");
                if (!identityListMap.isEmpty()) {
                    this.identityInfoModel = new IdentityInfoModel();
                    List<IdentityInfo> identityList = identityInfoModel.queryIdentityInfos("ORGANINFOR_ID", organInfo.getId());//首先判断当前机构下是否存在身份信息
                    if (!identityList.isEmpty()) {
                        identityInfoModel.deleteAllIdentityInfos(identityList);//如存在身份信息，则全部删除（因为接口没有返回身份的state，所以只能物理删除）
                    }
                    for (int k = 0; k < identityListMap.size(); k++) {
                        identityInfoMap = identityListMap.get(k);
                        IdentityInfo identityInfo = new IdentityInfo();
                        identityInfoModel.saveOrUpdateIdentityInfo(setIdentityInfoProperties(identityInfo, identityInfoMap, organInfo));//解析并封装身份信息,新建对象信息
                    }
                }
            }
        }
        //4、最后需要处理删除本地机构表和身份表与接口不同步的数据
        List<OrganInfo> localOrganList = organInfoModel.queryOrganInfosByUserId("USERINFO_ID", userInfo.getId());//查出用户所属的所有机构
        if (remoteOrganList.size() < localOrganList.size()) { //如果接口返回的机构数小于本地存储的机构数，则服务端用户所属机构减少了，本地也要删除对应的机构和身份
            localOrganList.removeAll(remoteOrganList);//机构去重
            for (OrganInfo localOrganInfo : localOrganList) {
                List<IdentityInfo> localIdentityList = identityInfoModel.queryIdentityInfos("ORGANINFOR_ID", localOrganInfo.getId());//首先判断当前机构下是否存在身份信息
                if (!localIdentityList.isEmpty()) {
                    identityInfoModel.deleteAllIdentityInfos(localIdentityList);//需要先删除机构下的身份
                }
            }
            organInfoModel.deleteAllOrganInfos(localOrganList);//最后再删除不同步的机构（因为接口没有按照state规则返回数据，只能物理删除）
        }
    }

    /**
     * 解析数据并封装userInfo对象.
     *
     * @param userInfoMap 接口返回的数据对象
     * @param userInfo 用户对象
     * @return UserInfo 封装好的用户对象
     */
    public UserInfo setUserInfoProperties(UserInfo userInfo, Map userInfoMap) {
        String loginName = String.valueOf(userInfoMap.get("loginName"));
        String password = String.valueOf(userInfoMap.get("password"));
        String userName = String.valueOf(userInfoMap.get("userName"));
        String englishName = String.valueOf(userInfoMap.get("englishName"));
        String phone = String.valueOf(userInfoMap.get("phone"));
        String state = String.valueOf(userInfoMap.get("state"));
        String sexName = String.valueOf(userInfoMap.get("sexName"));
        String sexEnglishName = String.valueOf(userInfoMap.get("sexEnglishName"));
        String sort = String.valueOf(userInfoMap.get("sort"));
        userInfo.setLoginName(loginName);
        userInfo.setPassword(password);
        userInfo.setUserName(userName);
        userInfo.setEnglishName(englishName);
        userInfo.setPhone(phone);
        userInfo.setState(state);
        userInfo.setSexName(sexName);
        userInfo.setSexEnglishName(sexEnglishName);
        userInfo.setSort(sort);
        return userInfo;
    }

    /**
     * 解析数据并封装organInfo对象.
     *
     * @param organInfoMap 接口返回的数据对象
     * @param organInfo 机构对象
     * @param userInfo 用户对象
     * @return OrganInfo 封装好的机构对象
     */
    public OrganInfo setOrganInfoProperties(OrganInfo organInfo, Map organInfoMap, UserInfo userInfo) {
        String organizationName = String.valueOf(organInfoMap.get("organizationName"));
        String userId = String.valueOf(organInfoMap.get("userId"));
        String state = String.valueOf(organInfoMap.get("state"));
        String organizationId = String.valueOf(organInfoMap.get("organizationId"));
        String organizationEnglishName = String.valueOf(organInfoMap.get("organizationEnglishName"));
        organInfo.setOrganizationName(organizationName);
        organInfo.setUserId(userId);
        organInfo.setState(state);
        organInfo.setOrganizationId(organizationId);
        organInfo.setOrganizationEnglishName(organizationEnglishName);
        organInfo.setUserInfo(userInfo);
        return organInfo;
    }

    /**
     * 解析数据并封装identityInfo对象.
     *
     * @param identityInfoMap 接口返回的数据对象
     * @param identityInfo 身份对象
     * @param organInfo 机构对象
     * @return OrganInfo 封装好的身份对象
     */
    public IdentityInfo setIdentityInfoProperties(IdentityInfo identityInfo, Map identityInfoMap, OrganInfo organInfo) {
        String identityName = String.valueOf(identityInfoMap.get("identityName"));
        String identityEnglishName = String.valueOf(identityInfoMap.get("identityEnglishName"));
        identityInfo.setIdentityName(identityName);
        identityInfo.setIdentityEnglishName(identityEnglishName);
        identityInfo.setOrganInfo(organInfo);
        return identityInfo;
    }
}
