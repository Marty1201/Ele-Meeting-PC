package com.chinaunicom.elemeetingpc.database.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 会议用户关系实体类.
 * @author zhaojunfeng
 */
@DatabaseTable(tableName = "MeetUserRelation")
public class MeetUserRelation implements BaseModel{
    //id
    @DatabaseField(columnName = "meetingUserId",id = true)
    private String meetingUserId;
    
    //用户Id
    @DatabaseField(columnName = "userId")
    private String userId;
    
    //会议Id
    @DatabaseField(columnName = "meetingId")
    private String meetingId;
           
    //状态
    @DatabaseField(columnName = "state")
    private String state;

    public MeetUserRelation() {
    }

    public MeetUserRelation(String meetingUserId, String userId, String meetingId, String state) {
        this.meetingUserId = meetingUserId;
        this.userId = userId;
        this.meetingId = meetingId;
        this.state = state;
    }

    public String getMeetingUserId() {
        return meetingUserId;
    }

    public void setMeetingUserId(String meetingUserId) {
        this.meetingUserId = meetingUserId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMeetingId() {
        return meetingId;
    }

    public void setMeetingId(String meetingId) {
        this.meetingId = meetingId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }     
}
