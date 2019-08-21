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
 * MeetInfoDao, 自定义数据库操作方法实现.
 * @author zhaojunfeng, chenxi
 */
public class MeetInfoDao extends CommonDao{
    private static final Logger logger = LoggerFactory.getLogger(MeetInfoDao.class);

    public MeetInfoDao() {
        super();
    }
    
    
    /**
     * 获取正在进行的会议列表.
     * @param parentMeetIdList 父会议id列表
     * @param dateTimeString 时间字符串yyyy-MM-dd hh:mm:ss
     * @return meetInfoList
     * @throws ApplicationException
     * @throws SQLException 
     */
    public List<MeetInfo> findCurrentMeetInfoList(List<String> parentMeetIdList, String dateTimeString)  throws ApplicationException, SQLException {
        List<MeetInfo> meetInfoList = new ArrayList<>();
        try {
            Dao<MeetInfo, Object> dao = getDao(MeetInfo.class);
            QueryBuilder<MeetInfo, Object> queryBuilder = dao.queryBuilder();
            queryBuilder.where().in("meetingId", parentMeetIdList).and().eq("state", "0").and().eq("parentMeetingId", "00000000000000000000000000000000")
                    .and().le("startDateTime", dateTimeString).and().ge("endDateTime", dateTimeString);
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
     * 获取即将进行的会议列表。
     * @param parentMeetIdList 父会议id列表
     * @param dateTimeString 时间字符串yyyy-MM-dd hh:mm:ss
     * @return meetInfoList
     * @throws ApplicationException
     * @throws SQLException 
     */
    public List<MeetInfo> findFutureMeetInfoList(List<String> parentMeetIdList,String dateTimeString)  throws ApplicationException, SQLException {
        List<MeetInfo> meetInfoList = new ArrayList<>();
        try {
            Dao<MeetInfo, Object> dao = getDao(MeetInfo.class);
            QueryBuilder<MeetInfo, Object> queryBuilder = dao.queryBuilder();
            queryBuilder.where().in("meetingId", parentMeetIdList).and().eq("state", "0").and().eq("parentMeetingId", "00000000000000000000000000000000")
                    .and().gt("startDateTime", dateTimeString);           
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
     * 获取会议的历史记录列表.
     * @param parentMeetIdList 父会议id列表
     * @param dateTimeString 时间字符串yyyy-MM-dd hh:mm:ss
     * @return meetInfoList
     * @throws ApplicationException
     * @throws SQLException 
     */
    public List<MeetInfo> findHistoryMeetInfoList(List<String> parentMeetIdList,String dateTimeString)  throws ApplicationException, SQLException {
        List<MeetInfo> meetInfoList = new ArrayList<>();
        try {
            Dao<MeetInfo, Object> dao = getDao(MeetInfo.class);
            QueryBuilder<MeetInfo, Object> queryBuilder = dao.queryBuilder();
            queryBuilder.where().in("meetingId", parentMeetIdList).and().eq("state", "0").and().eq("parentMeetingId", "00000000000000000000000000000000")
                    .and().lt("endDateTime", dateTimeString);
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
     * 根据父会议id和子会议id列表获取子会议信息，增加state=0和sort条件.
     *
     * @param parentMeetingId
     * @param childMeetIdList
     * @return childMeetInfoList 子会议信息列表
     */
    public List<MeetInfo> findChildMeetInfos(String parentMeetingId, List<String> childMeetIdList) throws ApplicationException, SQLException {
        List<MeetInfo> childMeetInfoList = new ArrayList<>();
        try{
            Dao<MeetInfo, Object> dao = getDao(MeetInfo.class);
            QueryBuilder<MeetInfo, Object> queryBuilder = dao.queryBuilder();
            queryBuilder.orderBy("sort", true).where().in("meetingId", childMeetIdList).and().eq("parentMeetingId", parentMeetingId).and().eq("state", "0");
            childMeetInfoList = dao.query(queryBuilder.prepare());
        } catch (SQLException e) {
            logger.warn(e.getCause().getMessage());
            throw new ApplicationException(FxmlUtils.getResourceBundle().getString("error.not.found.all"));
        } finally {
            this.closeDbConnection();
        }
        return childMeetInfoList;
    }
    
    /**
     * 根据会议id获取会议信息，增加state=0和sort条件.
     *
     * @param meetingId
     * @return meetInfoList 会议信息列表
     */
    public List<MeetInfo> findMeetInfosById(String meetingId) throws ApplicationException, SQLException {
        List<MeetInfo> meetInfoList = new ArrayList<>();
        try {
            Dao<MeetInfo, Object> dao = getDao(MeetInfo.class);
            QueryBuilder<MeetInfo, Object> queryBuilder = dao.queryBuilder();
            queryBuilder.orderBy("sort", true).where().eq("meetingId", meetingId).and().eq("state", "0");
            meetInfoList = dao.query(queryBuilder.prepare());
        } catch (SQLException e) {
            e.printStackTrace();
            logger.warn(e.getCause().getMessage());
            throw new ApplicationException(FxmlUtils.getResourceBundle().getString("error.not.found.all"));
        } finally {
            this.closeDbConnection();
        }
        return meetInfoList;
    }
}
