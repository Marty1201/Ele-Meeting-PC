package com.chinaunicom.elemeetingpc.database.dao;

import com.chinaunicom.elemeetingpc.database.models.MeetIssueRelation;
import com.chinaunicom.elemeetingpc.utils.FxmlUtils;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 * This class provides advanced database operation on MeetIssueRelation table, all
 * methods in this class handle exceptions by try and catch, then throw the
 * approperate error message to the caller, it's the caller's responsibility to
 * catch and process the error message.
 *
 * @author zhaojunfeng, chenxi
 */
public class MeetIssueRelationDao extends CommonDao {

    public MeetIssueRelationDao() {
        super();
    }

    /**
     * 根据子会议id查询子会议和议题的关系（子会议下的议题），增加state=0和sort条件.
     *
     * @param childMeetingId 子会议id
     * @return meetIssueList 会议议题列表
     * @throws ApplicationException
     */
    public List<MeetIssueRelation> findMeetIssueRelationByMeetId(String childMeetingId) throws ApplicationException {
        List<MeetIssueRelation> meetIssueList = new ArrayList<>();
        if (StringUtils.isNotBlank(childMeetingId)) {
            try {
                Dao<MeetIssueRelation, Object> dao = getDao(MeetIssueRelation.class);
                QueryBuilder<MeetIssueRelation, Object> queryBuilder = dao.queryBuilder();
                queryBuilder.orderBy("sort", true).where().eq("meetingId", childMeetingId).and().eq("state", "0");
                meetIssueList = dao.query(queryBuilder.prepare());
            } catch (Exception e) {
                throw new ApplicationException(FxmlUtils.getResourceBundle().getString("error.findMeetIssueRelationByMeetId"));
            } finally {
                this.closeDbConnection();
            }
        }
        return meetIssueList;
    }
}
