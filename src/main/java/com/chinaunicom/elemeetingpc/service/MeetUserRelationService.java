
package com.chinaunicom.elemeetingpc.service;

import com.chinaunicom.elemeetingpc.database.dao.MeetUserRelationDao;
import com.chinaunicom.elemeetingpc.database.models.MeetUserRelation;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;
import java.util.ArrayList;
import java.util.List;

/**
 * The MeetUserRelationService class serves as a service layer between Controller and
 * Dao, it provides variouse database operation methods on the MeetUserRelation
 * table.
 *
 * @author zhaojunfeng, chenxi
 */
public class MeetUserRelationService {
    
    /**
     * 保存或修改.
     * 
     * @param meetUserRelation 不为空
     * @throws ApplicationException 
     */
    public void saveOrUpdateMeetUserRelation(MeetUserRelation meetUserRelation) throws ApplicationException{
        MeetUserRelationDao dao = new MeetUserRelationDao();
        dao.saveOrUpdate(meetUserRelation);
    }
    
    /**
     * 在会议用户关系表中根据当前登录用户id获取用户与子会议的关系（子会议id），增加state=0条件.
     *
     * @param userId 不为空
     * @return meetUserRelationList 用户与子会议对应关系列表
     * @throws ApplicationException
     */
    public List<MeetUserRelation> queryMeetUserRelationByUserId(String userId) throws ApplicationException {
        MeetUserRelationDao meetUserRelationDao = new MeetUserRelationDao();
        List<MeetUserRelation> meetUserRelationList = new ArrayList<>();
        meetUserRelationList = meetUserRelationDao.findMeetUserRelationByUserId(userId);
        return meetUserRelationList;
    }
}
