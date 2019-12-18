
package com.chinaunicom.elemeetingpc.service;

import com.chinaunicom.elemeetingpc.database.dao.IssueInfoDao;
import com.chinaunicom.elemeetingpc.database.dao.MeetIssueRelationDao;
import com.chinaunicom.elemeetingpc.database.models.IssueInfo;
import com.chinaunicom.elemeetingpc.database.models.MeetIssueRelation;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;
import java.util.ArrayList;
import java.util.List;

/**
 * The IssueInfoService class serves as a service layer between Controller and
 * Dao, it provides variouse database operation methods on the IssueInfo
 * table.
 * 
 * @author zhaojunfeng
 */
public class IssueInfoService {
    
    /**
     * 保存或修改.
     * 
     * @param issueInfo a IssueInfo object
     * @throws ApplicationException 
     */
    public void saveOrUpdateIssueInfo(IssueInfo issueInfo) throws ApplicationException{
        IssueInfoDao issueInfoDao = new IssueInfoDao();
        issueInfoDao.saveOrUpdate(issueInfo);
    }

    /**
     * 根据会议ID获取议题集合.
     * 
     * @param meetId meeting id
     * @return a list of IssueInfo
     * @throws ApplicationException
     */
    public List<IssueInfo> queryIssueInfosByMeetId(String meetId) throws ApplicationException {
        List<IssueInfo> list = new ArrayList<>();
        List<MeetIssueRelation> tempList = new ArrayList<>();
        MeetIssueRelationDao rdao = new MeetIssueRelationDao();
        tempList = rdao.findByFieldNameAndValue(MeetIssueRelation.class, "meetingId", meetId);
        List<String> ids = new ArrayList<>();
        for (MeetIssueRelation mr : tempList) {
            ids.add(mr.getIssueId());
        }
        IssueInfoDao issueInfoDao = new IssueInfoDao();
        list = issueInfoDao.findIssueInfosByIds(ids);
        return list;
    }

    /**
     * 根据议题ids批量查询所有议题信息，增加state=0和sort条件.
     *
     * @param issueIdList a list of issue ids
     * @return a list of IssueInfo
     * @throws ApplicationException
     */
    public List<IssueInfo> queryIssueByIds(List<String> issueIdList) throws ApplicationException {
        IssueInfoDao issueInfoDao = new IssueInfoDao();
        List<IssueInfo> issueList = new ArrayList<>();
        issueList = issueInfoDao.findIssueInfosByIds(issueIdList);
        return issueList;
    }
}
