package com.chinaunicom.elemeetingpc.database.dao;

import com.chinaunicom.elemeetingpc.database.models.MeetInfo;
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
 *
 * @author zhaojunfeng
 */
public class MeetInfoDao extends CommonDao{
    private static final Logger logger = LoggerFactory.getLogger(MeetInfoDao.class);

    public MeetInfoDao() {
        super();
    }
    
    
    /**
     * 获取正在进行的会议列表
     * @param organationId 机构ID
     * @param dateTimeString 时间字符串yyyy-MM-dd hh:mm:ss
     * @return
     * @throws ApplicationException
     * @throws SQLException 
     */
    public List<MeetInfo> findCurrentMeetInfoList(String organationId,String dateTimeString)  throws ApplicationException, SQLException {
        List<MeetInfo> meetInfoList = new ArrayList<>();
        try {
            Dao<MeetInfo, Object> dao = getDao(MeetInfo.class);
            QueryBuilder<MeetInfo, Object> queryBuilder = dao.queryBuilder();
            //机构ID
            queryBuilder.where().eq("organizationId", organationId).and().eq("state", "0").and().eq("parentMeetingId", "00000000000000000000000000000000")
                    .and().le("startDateTime", dateTimeString).and().ge("endDateTime", dateTimeString);
            //状态
            //queryBuilder.where().eq("state", "0");
            //父会议ID
            //queryBuilder.where().eq("parentMeetingId", "00000000000000000000000000000000");
            //会议开始时间
            //queryBuilder.where().ge("startDateTime", dateTimeString);
            //会议结束时间
            //queryBuilder.where().le("endDateTime", dateTimeString);
            queryBuilder.orderBy("sort", true);
            meetInfoList = dao.query(queryBuilder.prepare());
        } catch (SQLException e) {
            logger.warn(e.getCause().getMessage());
            throw new ApplicationException(FxmlUtils.getResourceBundle().getString("error.not.found.all"));
        } finally {
            this.closeDbConnection();
        }
        return meetInfoList;
    }
    
    /**
     * 获取即将进行的会议列表
     * @param organationId 机构ID
     * @param dateTimeString 时间字符串yyyy-MM-dd hh:mm:ss
     * @return
     * @throws ApplicationException
     * @throws SQLException 
     */
    public List<MeetInfo> findFutureMeetInfoList(String organationId,String dateTimeString)  throws ApplicationException, SQLException {
        List<MeetInfo> meetInfoList = new ArrayList<>();
        try {
            Dao<MeetInfo, Object> dao = getDao(MeetInfo.class);
            QueryBuilder<MeetInfo, Object> queryBuilder = dao.queryBuilder();
            //机构ID
            queryBuilder.where().eq("organizationId", organationId).and().eq("state", "0").and().eq("parentMeetingId", "00000000000000000000000000000000")
                    .and().gt("startDateTime", dateTimeString);
            //状态
            //queryBuilder.where().eq("state", "0");
            //父会议ID
            //queryBuilder.where().eq("parentMeetingId", "00000000000000000000000000000000");
            //会议开始时间
            //queryBuilder.where().gt("startDateTime", dateTimeString);            
            queryBuilder.orderBy("sort", true);
            meetInfoList = dao.query(queryBuilder.prepare());
        } catch (SQLException e) {
            logger.warn(e.getCause().getMessage());
            throw new ApplicationException(FxmlUtils.getResourceBundle().getString("error.not.found.all"));
        } finally {
            this.closeDbConnection();
        }
        return meetInfoList;
    }
    
    /**
     * 获取会议的历史记录列表
     * @param organationId 机构ID
     * @param dateTimeString 时间字符串yyyy-MM-dd hh:mm:ss
     * @return
     * @throws ApplicationException
     * @throws SQLException 
     */
    public List<MeetInfo> findHistoryMeetInfoList(String organationId,String dateTimeString)  throws ApplicationException, SQLException {
        List<MeetInfo> meetInfoList = new ArrayList<>();
        try {
            Dao<MeetInfo, Object> dao = getDao(MeetInfo.class);
            QueryBuilder<MeetInfo, Object> queryBuilder = dao.queryBuilder();
            //机构ID
            queryBuilder.where().eq("organizationId", organationId).and().eq("state", "0").and().eq("parentMeetingId", "00000000000000000000000000000000")
                    .and().lt("endDateTime", dateTimeString);
            //状态
            //queryBuilder.where().eq("state", "0");
            //父会议ID
            //queryBuilder.where().eq("parentMeetingId", "00000000000000000000000000000000");
            //会议结束时间
            //queryBuilder.where().lt("endDateTime", dateTimeString);
            queryBuilder.orderBy("sort", true);
            meetInfoList = dao.query(queryBuilder.prepare());
        } catch (SQLException e) {
            logger.warn(e.getCause().getMessage());
            throw new ApplicationException(FxmlUtils.getResourceBundle().getString("error.not.found.all"));
        } finally {
            this.closeDbConnection();
        }
        return meetInfoList;
    }
    
}
