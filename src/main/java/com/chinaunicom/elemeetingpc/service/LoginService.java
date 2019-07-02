package com.chinaunicom.elemeetingpc.service;

import com.chinaunicom.elemeetingpc.constant.ServeIpConstant;
import com.chinaunicom.elemeetingpc.constant.StatusConstant;
import com.chinaunicom.elemeetingpc.database.dao.IdentityInfoDao;
import com.chinaunicom.elemeetingpc.database.dao.OrganInfoDao;
import com.chinaunicom.elemeetingpc.database.dao.UserInfoDao;
import com.chinaunicom.elemeetingpc.database.models.IdentityInfo;
import com.chinaunicom.elemeetingpc.database.models.OrganInfo;
import com.chinaunicom.elemeetingpc.database.models.UserInfo;
import com.chinaunicom.elemeetingpc.utils.GsonUtil;
import com.chinaunicom.elemeetingpc.utils.HashUtil;
import com.chinaunicom.elemeetingpc.utils.HttpClientUtil;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 登录业务逻辑service
 * @author zhaojunfeng
 */
public class LoginService {
    
    /**
     * 登录
     * @param loginName
     * @param password
     * @param validaNum 
     * @return  
     */
    public Map<String,String> login(String loginName,String password,String validaNum){
        Map<String,String> resultMap = new HashMap();
        try {            
            //封装参数
            String param = this.fzParam(loginName, password, validaNum, validaNum);
            //访问接口
            String result = HttpClientUtil.getInstance().getResponseBodyAsString(ServeIpConstant.loginServicePath(), param);
            //System.out.println(result);
            //把json字符串转成map
            Map temp_map = GsonUtil.getMap(result);
            String result_code = String.valueOf(temp_map.get("resultCode"));
            String resultDesc = String.valueOf(temp_map.get("resultDesc"));
            resultMap.put("code", result_code);
            resultMap.put("desc", resultDesc);
            
            Map dataMap = (Map) temp_map.get("resultData");
            parseFzDataMap(dataMap);
          
        } catch (Exception ex) {
            Logger.getLogger(LoginService.class.getName()).log(Level.SEVERE, null, ex);
            resultMap.put("code", StatusConstant.RESULT_CODE_FAIL);
            resultMap.put("desc", "发生异常");
        }
        return resultMap;
    }
    
    /**
     * 封装参数为json字符串
     * @param loginName
     * @param password
     * @param validaNum
     * @param updateDate
     * 示例：{loginName:'changlu',password:'757f7fc9ad2ec1a2951fbf3a7bbc2144',deviceToken:'60393D7BF54A',updateDate:''}
     * @return 
     */
    private String fzParam(String loginName,String password,String validaNum,String updateDate){
        String md5password = HashUtil.toMD5(password);
        md5password="757f7fc9ad2ec1a2951fbf3a7bbc2144";
        String resultString="{loginName:'"+loginName+"',password:'"+md5password+"',deviceToken:'60393D7BF54A',updateDate:''}";
        
        return resultString;
    }
    
    //解析数据，封装userInfo对象、organInfo对象、identityInfo对象，并(保存/修改)到数据库
    private void parseFzDataMap(Map dataMap) throws ApplicationException{
        //updateDate更新时间，作为参数传递给服务器，查询以此时间为起始至今天时间为止的时间段内服务器数据发生的变化
        String updateDate = String.valueOf(dataMap.get("updateDate"));
        Map userMap = (Map) dataMap.get("userInfo");
        
        //解析用户信息
        String loginName = String.valueOf(userMap.get("loginName"));
        String password = String.valueOf(userMap.get("password"));
        String userName = String.valueOf(userMap.get("userName"));
        String englishName = String.valueOf(userMap.get("englishName"));
        String phone = String.valueOf(userMap.get("phone"));
        String state = String.valueOf(userMap.get("state"));
        String sexName = String.valueOf(userMap.get("sexName"));
        String sexEnglishName = String.valueOf(userMap.get("sexEnglishName"));
        String sort = String.valueOf(userMap.get("sort"));
        //封装用户信息，保存至数据库
        UserInfo user = new UserInfo(loginName, userName, password, englishName,  phone,  state,  sexName,  sexEnglishName,  sort,  updateDate);
        UserInfoDao userDao = new UserInfoDao();
        //userDao.saveOrUpdate(user);
        userDao.save(user);
        
        //组织机构list
        List<Map> tempList = (List<Map>) userMap.get("organs");
        if(tempList!=null){
            for(Object obj : tempList){
                Map orgMap = (Map) obj;
                String organizationName = String.valueOf(orgMap.get("organizationName"));
                String userId = String.valueOf(orgMap.get("userId"));
                String state2 = String.valueOf(orgMap.get("state"));
                String organizationId = String.valueOf(orgMap.get("organizationId"));
                String organizationEnglishName = String.valueOf(orgMap.get("organizationEnglishName"));
                //封装机构信息，保存至数据库
                OrganInfo organInfo = new OrganInfo(user, organizationName, organizationId, organizationEnglishName, state2, userId);
                OrganInfoDao organDao = new OrganInfoDao();
                organDao.save(organInfo);
                
                 //身份list
                 List<Map> dataList = (List<Map>) orgMap.get("identityList");
                 if(dataList!=null){
                     for(Object t : dataList){
                        Map ideMap = (Map) t;
                        String identityName = String.valueOf(ideMap.get("identityName"));
                        String identityEnglishName = String.valueOf(ideMap.get("identityEnglishName"));
                        
                         IdentityInfo info = new IdentityInfo(organInfo, identityName, identityEnglishName);
                         IdentityInfoDao infoDao = new IdentityInfoDao();
                         infoDao.save(info);
                     }
                 }
            }
        }
    }
    
}
