package com.chinaunicom.elemeetingpc.database.dao;

import com.chinaunicom.elemeetingpc.database.models.MeetInfo;
import com.chinaunicom.elemeetingpc.utils.FxmlUtils;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

/**
 * MeetInfoDao, 自定义数据库操作方法实现.
 *
 * @author zhaojunfeng, chenxi
 */
public class MeetInfoDao extends CommonDao {

    public MeetInfoDao() {
        super();
    }

    /**
     * 获取正在进行的会议列表.
     *
     * @param parentMeetIdList 父会议id列表
     * @param dateTimeString 时间字符串yyyy-MM-dd hh:mm:ss
     * @return meetInfoList
     * @throws ApplicationException
     */
    public List<MeetInfo> findCurrentMeetInfoList(List<String> parentMeetIdList, String dateTimeString) throws ApplicationException {
        List<MeetInfo> meetInfoList = new ArrayList<>();
        if (!parentMeetIdList.isEmpty() && StringUtils.isNotBlank(dateTimeString)) {
            try {
                Dao<MeetInfo, Object> dao = getDao(MeetInfo.class);
                QueryBuilder<MeetInfo, Object> queryBuilder = dao.queryBuilder();
                queryBuilder.where().in("meetingId", parentMeetIdList).and().eq("state", "0").and().eq("parentMeetingId", "00000000000000000000000000000000")
                        .and().le("startDateTime", dateTimeString).and().ge("endDateTime", dateTimeString);
                queryBuilder.orderBy("sort", true);
                meetInfoList = dao.query(queryBuilder.prepare());
            } catch (Exception e) {
                throw new ApplicationException(FxmlUtils.getResourceBundle().getString("error.findCurrentMeetInfoList"));
            } finally {
                this.closeDbConnection();
            }
        }
        return meetInfoList;
    }

    /**
     * 获取即将进行的会议列表。
     *
     * @param parentMeetIdList 父会议id列表
     * @param dateTimeString 时间字符串yyyy-MM-dd hh:mm:ss
     * @return meetInfoList
     * @throws ApplicationException
     */
    public List<MeetInfo> findFutureMeetInfoList(List<String> parentMeetIdList, String dateTimeString) throws ApplicationException {
        List<MeetInfo> meetInfoList = new ArrayList<>();
        if (!parentMeetIdList.isEmpty() && StringUtils.isNotBlank(dateTimeString)) {
            try {
                Dao<MeetInfo, Object> dao = getDao(MeetInfo.class);
                QueryBuilder<MeetInfo, Object> queryBuilder = dao.queryBuilder();
                queryBuilder.where().in("meetingId", parentMeetIdList).and().eq("state", "0").and().eq("parentMeetingId", "00000000000000000000000000000000")
                        .and().gt("startDateTime", dateTimeString);
                queryBuilder.orderBy("sort", true);
                meetInfoList = dao.query(queryBuilder.prepare());
            } catch (Exception e) {
                throw new ApplicationException(FxmlUtils.getResourceBundle().getString("error.findFutureMeetInfoList"));
            } finally {
                this.closeDbConnection();
            }
        }
        return meetInfoList;
    }

    /**
     * 获取会议的历史记录列表.
     *
     * @param parentMeetIdList 父会议id列表
     * @param dateTimeString 时间字符串yyyy-MM-dd hh:mm:ss
     * @return meetInfoList
     * @throws ApplicationException
     */
    public List<MeetInfo> findHistoryMeetInfoList(List<String> parentMeetIdList, String dateTimeString) throws ApplicationException {
        List<MeetInfo> meetInfoList = new ArrayList<>();
        if (!parentMeetIdList.isEmpty() && StringUtils.isNotBlank(dateTimeString)) {
            try {
                Dao<MeetInfo, Object> dao = getDao(MeetInfo.class);
                QueryBuilder<MeetInfo, Object> queryBuilder = dao.queryBuilder();
                queryBuilder.where().in("meetingId", parentMeetIdList).and().eq("state", "0").and().eq("parentMeetingId", "00000000000000000000000000000000")
                        .and().lt("endDateTime", dateTimeString);
                queryBuilder.orderBy("sort", true);
                meetInfoList = dao.query(queryBuilder.prepare());
            } catch (Exception e) {
                throw new ApplicationException(FxmlUtils.getResourceBundle().getString("error.findHistoryMeetInfoList"));
            } finally {
                this.closeDbConnection();
            }
        }
        return meetInfoList;
    }

    /**
     * 根据父会议id和子会议id列表获取子会议信息，增加state=0和sort条件.
     *
     * @param parentMeetingId 父会议id
     * @param childMeetIdList 子会议id列表
     * @return childMeetInfoList 子会议信息列表
     */
    public List<MeetInfo> findChildMeetInfos(String parentMeetingId, List<String> childMeetIdList) throws ApplicationException {
        List<MeetInfo> childMeetInfoList = new ArrayList<>();
        if (StringUtils.isNotBlank(parentMeetingId) && !childMeetIdList.isEmpty()) {
            try {
                Dao<MeetInfo, Object> dao = getDao(MeetInfo.class);
                QueryBuilder<MeetInfo, Object> queryBuilder = dao.queryBuilder();
                queryBuilder.orderBy("sort", true).where().in("meetingId", childMeetIdList).and().eq("parentMeetingId", parentMeetingId).and().eq("state", "0");
                childMeetInfoList = dao.query(queryBuilder.prepare());
            } catch (Exception e) {
                throw new ApplicationException(FxmlUtils.getResourceBundle().getString("error.findChildMeetInfos"));
            } finally {
                this.closeDbConnection();
            }
        }
        return childMeetInfoList;
    }

    /**
     * 根据会议id获取会议信息，增加state=0和sort条件.
     *
     * @param meetingId 会议id
     * @return meetInfoList 会议信息列表
     */
    public List<MeetInfo> findMeetInfosById(String meetingId) throws ApplicationException {
        List<MeetInfo> meetInfoList = new ArrayList<>();
        if (StringUtils.isNotBlank(meetingId)) {
            try {
                Dao<MeetInfo, Object> dao = getDao(MeetInfo.class);
                QueryBuilder<MeetInfo, Object> queryBuilder = dao.queryBuilder();
                queryBuilder.orderBy("sort", true).where().eq("meetingId", meetingId).and().eq("state", "0");
                meetInfoList = dao.query(queryBuilder.prepare());
            } catch (Exception e) {
                throw new ApplicationException(FxmlUtils.getResourceBundle().getString("error.findMeetInfosById"));
            } finally {
                this.closeDbConnection();
            }
        }
        return meetInfoList;
    }
}
