
package com.chinaunicom.elemeetingpc.database.dao;

import com.chinaunicom.elemeetingpc.database.models.MeetUserRelation;
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
 * MeetUserRelationDao, 自定义数据库操作方法实现（用户id对应其所在的子会议id）.
 * @author zhaojunfeng, chenxi
 */
public class MeetUserRelationDao extends CommonDao{
    
     private static final Logger logger = LoggerFactory.getLogger(MeetUserRelationDao.class);

    public MeetUserRelationDao() {
        super();
    }
    
    /**
     * 在会议用户关系表中根据当前登录用户id获取用户与子会议的关系（子会议id），增加state=0条件.
     *
     * @param userId
     * @return meetUserRelationList 用户与子会议对应关系列表
     */
    public List<MeetUserRelation> findMeetUserRelationByUserId(String userId) throws ApplicationException, SQLException{
        List<MeetUserRelation> meetUserRelationList = new ArrayList<>();
        try{
            Dao<MeetUserRelation, Object> dao = getDao(MeetUserRelation.class);
            QueryBuilder<MeetUserRelation, Object> queryBuilder = dao.queryBuilder();
            queryBuilder.where().eq("userId", userId).and().eq("state", "0");
            meetUserRelationList = dao.query(queryBuilder.prepare());
        } catch (SQLException e) {
            logger.warn(e.getCause().getMessage());
            throw new ApplicationException(FxmlUtils.getResourceBundle().getString("error.not.found.all"));
        }
        finally{
            this.closeDbConnection();
        }
        return meetUserRelationList;
    }
}
