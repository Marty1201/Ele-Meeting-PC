
package com.chinaunicom.elemeetingpc.modelFx;

import com.chinaunicom.elemeetingpc.database.dao.MeetUserRelationDao;
import com.chinaunicom.elemeetingpc.database.models.MeetUserRelation;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides various methods implementation for MeetUserRelation,
 * mainly focus on the logic for the Dao operation.
 *
 * @author zhaojunfeng, chenxi
 */
public class MeetUserRelationModel {
    
    /**
     * 保存或修改.
     * @param meetUserRelation
     * @throws ApplicationException 
     */
    public void saveOrUpdate(MeetUserRelation meetUserRelation) throws ApplicationException{
        MeetUserRelationDao dao = new MeetUserRelationDao();
        dao.saveOrUpdate(meetUserRelation);
    }
    
    /**
     * 在会议用户关系表中根据当前登录用户id获取用户与子会议的关系（子会议id），增加state=0条件.
     *
     * @param userId
     * @return meetUserRelationList 用户与子会议对应关系列表
     */
    public List<MeetUserRelation> queryMeetUserRelationByUserId(String userId) throws ApplicationException, SQLException {
        MeetUserRelationDao meetUserRelationDao = new MeetUserRelationDao();
        List<MeetUserRelation> meetUserRelationList = new ArrayList<>();
        meetUserRelationList = meetUserRelationDao.findMeetUserRelationByUserId(userId);
        return meetUserRelationList;
    }
    
}
