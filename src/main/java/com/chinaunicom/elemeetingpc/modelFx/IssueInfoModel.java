
package com.chinaunicom.elemeetingpc.modelFx;

import com.chinaunicom.elemeetingpc.database.dao.IssueInfoDao;
import com.chinaunicom.elemeetingpc.database.dao.MeetIssueRelationDao;
import com.chinaunicom.elemeetingpc.database.models.IssueInfo;
import com.chinaunicom.elemeetingpc.database.models.MeetIssueRelation;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author zhaojunfeng
 */
public class IssueInfoModel {
    
    /**
     * 保存或修改
     * @param issueInfo
     * @throws ApplicationException 
     */
    public void saveOrUpdateIssueInfo(IssueInfo issueInfo) throws ApplicationException{
        IssueInfoDao issueInfoDao = new IssueInfoDao();
        issueInfoDao.saveOrUpdate(issueInfo);
    }

    /**
     * 根据会议ID获取议题集合 
     * @param meetId
     * @return 
     */
    public List<IssueInfo> queryIssueInfosByMeetId(String meetId){
        List<IssueInfo> list = new ArrayList<>();
        try {
            List<MeetIssueRelation> tempList = new ArrayList<>();
            MeetIssueRelationDao rdao = new MeetIssueRelationDao();
            tempList = rdao.findByFieldNameAndValue(MeetIssueRelation.class, "meetingId", meetId);
            List<String> ids = new ArrayList<>();
            for(MeetIssueRelation mr : tempList){
                ids.add(mr.getIssueId());
            } 
            
            IssueInfoDao issueInfoDao = new IssueInfoDao();
            list = issueInfoDao.findIssueInfosByIds(ids);
            
        } catch (ApplicationException ex) {
            Logger.getLogger(IssueInfoModel.class.getName()).log(Level.SEVERE, null, ex);
        }
        return list;
    }
    
    public List<IssueInfo> queryIssueByIds(List<String> issueIdList) throws ApplicationException {
        IssueInfoDao issueInfoDao = new IssueInfoDao();
        List<IssueInfo> issueList = new ArrayList<>();
        issueList = issueInfoDao.findIssueInfosByIds(issueIdList);
        return issueList;
    }
}
