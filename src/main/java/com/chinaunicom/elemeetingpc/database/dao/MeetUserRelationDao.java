package com.chinaunicom.elemeetingpc.database.dao;

import com.chinaunicom.elemeetingpc.database.models.MeetUserRelation;
import com.chinaunicom.elemeetingpc.utils.FxmlUtils;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 * This class provides advanced database operation on MeetUserRelation table, all
 * methods in this class handle exceptions by try and catch, then throw the
 * approperate error message to the caller, it's the caller's responsibility to
 * catch and process the error message.
 *
 * @author zhaojunfeng, chenxi
 */
public class MeetUserRelationDao extends CommonDao {

    public MeetUserRelationDao() {
        super();
    }

    /**
     * 在会议用户关系表中根据当前登录用户id获取用户与子会议的关系（子会议id），增加state=0条件.
     *
     * @param userId 用户id
     * @return meetUserRelationList 用户与子会议对应关系列表
     * @throws ApplicationException
     */
    public List<MeetUserRelation> findMeetUserRelationByUserId(String userId) throws ApplicationException {
        List<MeetUserRelation> meetUserRelationList = new ArrayList<>();
        if (StringUtils.isNotBlank(userId)) {
            try {
                Dao<MeetUserRelation, Object> dao = getDao(MeetUserRelation.class);
                QueryBuilder<MeetUserRelation, Object> queryBuilder = dao.queryBuilder();
                queryBuilder.where().eq("userId", userId).and().eq("state", "0");
                meetUserRelationList = dao.query(queryBuilder.prepare());
            } catch (Exception e) {
                throw new ApplicationException(FxmlUtils.getResourceBundle().getString("error.findMeetUserRelationByUserId"));
            } finally {
                this.closeDbConnection();
            }
        }
        return meetUserRelationList;
    }
}
