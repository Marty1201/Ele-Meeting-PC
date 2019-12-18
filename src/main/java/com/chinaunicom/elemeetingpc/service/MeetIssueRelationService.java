
package com.chinaunicom.elemeetingpc.service;

import com.chinaunicom.elemeetingpc.database.dao.MeetIssueRelationDao;
import com.chinaunicom.elemeetingpc.database.models.MeetIssueRelation;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;
import java.util.ArrayList;
import java.util.List;

/**
 * The MeetIssueRelationService class serves as a service layer between Controller and
 * Dao, it provides variouse database operation methods on the MeetIssueRelation
 * table.
 * @author zhaojunfeng
 */
public class MeetIssueRelationService {
    
    /**
     * 保存或修改.
     * 
     * @param meetIssueRelation not null
     * @throws ApplicationException 
     */
    public void saveOrUpdateMeetIssueRelation(MeetIssueRelation meetIssueRelation) throws ApplicationException{
        MeetIssueRelationDao dao = new MeetIssueRelationDao();
        dao.saveOrUpdate(meetIssueRelation);
    }
    
    /**
     * 根据子会议id获取对应的会议和议题关系.
     *
     * @param meetIssueRelation not null
     * @throws ApplicationException
     */
    public List<MeetIssueRelation> queryMeetIssueRelation(String childMeetingId) throws ApplicationException {
        MeetIssueRelationDao meetIssueRelationDao = new MeetIssueRelationDao();
        List<MeetIssueRelation> meetIssueList = new ArrayList<>();
        meetIssueList = meetIssueRelationDao.findMeetIssueRelationByMeetId(childMeetingId);
        return meetIssueList;
    }
}
