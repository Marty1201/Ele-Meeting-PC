
package com.chinaunicom.elemeetingpc.database.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 *会议-议题关联类
 * @author zhaojunfeng
 */
@DatabaseTable(tableName = "MeetIssueRelation")
public class MeetIssueRelation implements BaseModel{
    //id
    @DatabaseField(columnName = "meettingIssueId",id = true)
    private String meettingIssueId;
    
    //议题Id
    @DatabaseField(columnName = "issueId")
    private String issueId;
    
    //会议Id
    @DatabaseField(columnName = "meetingId")
    private String meetingId;
           
    //状态
    @DatabaseField(columnName = "state")
    private String state;
           
    //排序
    @DatabaseField(columnName = "sort")
    private int sort;

    public MeetIssueRelation() {
    }

    public MeetIssueRelation(String meettingIssueId, String issueId, String meetingId, String state, int sort) {
        this.meettingIssueId = meettingIssueId;
        this.issueId = issueId;
        this.meetingId = meetingId;
        this.state = state;
        this.sort = sort;
    }

    public String getMeettingIssueId() {
        return meettingIssueId;
    }

    public void setMeettingIssueId(String meettingIssueId) {
        this.meettingIssueId = meettingIssueId;
    }

    public String getIssueId() {
        return issueId;
    }

    public void setIssueId(String issueId) {
        this.issueId = issueId;
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

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }
    
    
}
