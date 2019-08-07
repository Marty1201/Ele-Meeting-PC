
package com.chinaunicom.elemeetingpc.database.dao;

import com.chinaunicom.elemeetingpc.database.models.MeetIssueRelation;
import com.chinaunicom.elemeetingpc.utils.FxmlUtils;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import com.j256.ormlite.stmt.QueryBuilder;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * MeetIssueRelationDao, 自定义数据库操作方法实现.
 * 
 * @author zhaojunfeng, chenxi
 */
public class MeetIssueRelationDao extends CommonDao{
    
    private static final Logger logger = LoggerFactory.getLogger(MeetIssueRelationDao.class);

    public MeetIssueRelationDao() {
        super();
    }
    
    /**
     * 根据子会议id查询子会议和议题的关系（子会议下的议题），增加state=0和sort条件.
     *
     * @param childMeetingId
     * @return meetIssueList 会议议题列表
     */
    public List<MeetIssueRelation> findMeetIssueRelationByMeetId(String childMeetingId) throws ApplicationException{
        List<MeetIssueRelation> meetIssueList = new ArrayList<>();
        try{
            Dao<MeetIssueRelation, Object> dao = getDao(MeetIssueRelation.class);
            QueryBuilder<MeetIssueRelation, Object> queryBuilder = dao.queryBuilder();
            queryBuilder.orderBy("sort", true).where().eq("meetingId", childMeetingId).and().eq("state", "0");
            meetIssueList = dao.query(queryBuilder.prepare());
        } catch (SQLException e) {
            logger.warn(e.getCause().getMessage());
            throw new ApplicationException(FxmlUtils.getResourceBundle().getString("error.not.found.all"));
        } finally {
            this.closeDbConnection();
        }
        return meetIssueList;
    }
    
}
