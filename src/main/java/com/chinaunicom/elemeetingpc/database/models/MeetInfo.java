package com.chinaunicom.elemeetingpc.database.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 会议实体类.
 * @author zhaojunfeng
 */
@DatabaseTable(tableName = "meetInfo")
public class MeetInfo implements BaseModel{
    //id
    @DatabaseField(columnName = "meetingId",id = true)
    private String meetingId;
    
    //会议名称
    @DatabaseField(columnName = "meetingName")
    private String meetingName;
    
    //会议开始时间
    @DatabaseField(columnName = "startDateTime")
    private String startDateTime;
    
    //会议结束时间
    @DatabaseField(columnName = "endDateTime")
    private String endDateTime;
    
    //状态
    @DatabaseField(columnName = "state")
    private String state;
    
    //会议创建时间
    @DatabaseField(columnName = "createTime")
    private String createTime;
    
    //上级会议id
    @DatabaseField(columnName = "parentMeetingId")
    private String parentMeetingId;
    
    //排序
    @DatabaseField(columnName = "sort")
    private int sort;
    
    //英文名称
    @DatabaseField(columnName = "englishName")
    private String englishName;
    
    //是否英文
    @DatabaseField(columnName = "isEng")
    private String isEng;
    
    //所属机构ID
    @DatabaseField(columnName = "organizationId")
    private String organizationId;
    
    //更新时间
    @DatabaseField(columnName = "updateDate")
    private String updateDate;

    public MeetInfo() {
    }

    public MeetInfo(String meetingId, String meetingName, String startDateTime, String endDateTime, String state, String createTime, String parentMeetingId, int sort, String englishName, String isEng,String organizationId) {
        this.meetingId = meetingId;
        this.meetingName = meetingName;
        this.startDateTime = startDateTime;
        this.endDateTime = endDateTime;
        this.state = state;
        this.createTime = createTime;
        this.parentMeetingId = parentMeetingId;
        this.sort = sort;
        this.englishName = englishName;
        this.isEng = isEng;
        this.organizationId = organizationId;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public String getMeetingName() {
        return meetingName;
    }

    public void setMeetingName(String meetingName) {
        this.meetingName = meetingName;
    }

    public String getStartDateTime() {
        return startDateTime;
    }

    public void setStartDateTime(String startDateTime) {
        this.startDateTime = startDateTime;
    }

    public String getEndDateTime() {
        return endDateTime;
    }

    public void setEndDateTime(String endDateTime) {
        this.endDateTime = endDateTime;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getParentMeetingId() {
        return parentMeetingId;
    }

    public void setParentMeetingId(String parentMeetingId) {
        this.parentMeetingId = parentMeetingId;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public String getIsEng() {
        return isEng;
    }

    public void setIsEng(String isEng) {
        this.isEng = isEng;
    }
    
    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }     
}
