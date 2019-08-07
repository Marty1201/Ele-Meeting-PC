
package com.chinaunicom.elemeetingpc.service;

import com.chinaunicom.elemeetingpc.constant.GlobalStaticConstant;
import com.chinaunicom.elemeetingpc.constant.ServeIpConstant;
import com.chinaunicom.elemeetingpc.constant.StatusConstant;
import com.chinaunicom.elemeetingpc.database.models.IssueInfo;
import com.chinaunicom.elemeetingpc.database.models.OrganInfo;
import com.chinaunicom.elemeetingpc.modelFx.FileResourceModel;
import com.chinaunicom.elemeetingpc.modelFx.FileUserRelationModel;
import com.chinaunicom.elemeetingpc.modelFx.IssueFileRelationModel;
import com.chinaunicom.elemeetingpc.modelFx.IssueInfoModel;
import com.chinaunicom.elemeetingpc.modelFx.MeetInfoModel;
import com.chinaunicom.elemeetingpc.modelFx.MeetIssueRelationModel;
import com.chinaunicom.elemeetingpc.modelFx.MeetUserRelationModel;
import com.chinaunicom.elemeetingpc.modelFx.OrganInfoModel;
import com.chinaunicom.elemeetingpc.utils.GsonUtil;
import com.chinaunicom.elemeetingpc.utils.HttpClientUtil;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;

/**
 *会议主界面service
 * @author zhaojunfeng
 */
public class MeetService {
    
    private MeetInfoModel meetInfoModel;
    
    private IssueInfoModel issueInfoModel;
    
    private FileResourceModel fileResourceModel;
    
    private MeetIssueRelationModel meetIssueRelationModel;
    
    private IssueFileRelationModel issueFileRelationModel;
    
    private OrganInfoModel organInfoModel;
    
    private MeetUserRelationModel meetUserRelationModel;
    
    private FileUserRelationModel fileUserRelationModel;   

    
    /**
     * 调用会议接口逻辑处理,根据会议ID查询会议信息.
     */
    public void getMeetInfosFromRemote(){        
        try {
            //封装参数
            String updateDate = "";
            Map<String, String> queryMap = new HashMap<>();
            this.organInfoModel = new OrganInfoModel();
            queryMap.put("organizationId", GlobalStaticConstant.GLOBAL_ORGANINFO_ORGANIZATIONID);
            queryMap.put("userId", GlobalStaticConstant.GLOBAL_ORGANINFO_OWNER_USERID);
            List<OrganInfo> organInfoList = organInfoModel.queryOrganInfosByMap(queryMap);//根据用户id和机构id查出对应的机构
            if (!organInfoList.isEmpty()) {
                updateDate = organInfoList.get(0).getUpdateDate();
            }
            String param = this.fzParam(GlobalStaticConstant.GLOBAL_ORGANINFO_OWNER_USERID, GlobalStaticConstant.GLOBAL_ORGANINFO_ORGANIZATIONID, GlobalStaticConstant.GLOBAL_SELECTED_MEETID, updateDate);
            //访问接口
            String result = HttpClientUtil.getInstance().getResponseBodyAsString(ServeIpConstant.meetingOfOrganServicePath(), param);
            if(StringUtils.isNotBlank(result)){
                //把json字符串转成map
                Map temp_map = GsonUtil.getMap(result);
                String result_code = String.valueOf(temp_map.get("resultCode"));
                String resultDesc = String.valueOf(temp_map.get("resultDesc"));
                if (StatusConstant.RESULT_CODE_SUCCESS.equals(result_code)) {//查询成功
                    String resultData = String.valueOf(temp_map.get("resultData"));
                    Map dataMap = GsonUtil.getMap(resultData);;
                    parseFzDataMap(dataMap);
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(SelectOrganService.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
    /**
     * 封装参数为json字符串
     *
     * @param userId
     * @param orgId
     * @param meetingId
     * @param updateDate
     * 示例：{userId:'20190724085930588875478137127383',meetingId:'20190701145559610372604487039259',organizationId:'20190109175459084818173570055782',updateDate:''}
     * @return
     */
    private String fzParam(String userId, String orgId, String meetingId, String updateDate) {
        String resultString = "{userId:'"+userId+"',meetingId:'"+meetingId+"',orgId:'"+orgId+"',updateDate:'"+updateDate+"'}";
        return resultString;
    }
    
    /**
     * 解析数据
     * @param dataMap
     * @throws ApplicationException 
     */
    private void parseFzDataMap(Map dataMap) throws ApplicationException {
        //处理议题信息
        List<Map> issueInfoListMap = (List<Map>) dataMap.get("issues");
        if(issueInfoListMap!=null && !issueInfoListMap.isEmpty()){
            this.issueInfoModel = new IssueInfoModel();
            Map issueInfoMap = new HashMap();
            for(int i=0;i<issueInfoListMap.size();i++){
                issueInfoMap = issueInfoListMap.get(i);
                IssueInfo issueInfo = new IssueInfo();
                this.issueInfoModel.saveOrUpdateIssueInfo(this.setPropertiesIssueInfo(issueInfo, issueInfoMap));
            }
        }
    }
    
    /**
     * 解析数据并封装IssueInfo对象.
     * @param info
     * @param infoMap
     * @return 
     */
    public IssueInfo setPropertiesIssueInfo(IssueInfo info,Map infoMap){
        String issueId=String.valueOf(infoMap.get("issueId"));
        String issueName=String.valueOf(infoMap.get("issueName"));
        String state=String.valueOf(infoMap.get("state"));
        String sort=String.valueOf(infoMap.get("sort"));
        String englishName=String.valueOf(infoMap.get("englishName"));
        info.setIssueId(issueId);
        info.setIssueName(issueName);
        info.setState(state);
        info.setSort(Double.valueOf(sort).intValue());
        info.setEnglishName(englishName);
        
        return info;
    }
    
}
