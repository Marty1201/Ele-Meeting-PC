/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chinaunicom.elemeetingpc.modelFx;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author zhaojunfeng
 */
public class MeetInfoFx {
    
    //id
    private final StringProperty meetingId = new SimpleStringProperty();
    //会议名称
    private final StringProperty meetingName = new SimpleStringProperty();
    //会议开始时间
    private final StringProperty startDateTime = new SimpleStringProperty();
    //会议结束时间
    private final StringProperty endDateTime = new SimpleStringProperty();
    //状态
    private final StringProperty state = new SimpleStringProperty();
    //会议创建时间
    private final StringProperty createTime = new SimpleStringProperty();
    //上级会议id
    private final StringProperty parentMeetingId = new SimpleStringProperty();
    //英文名称
    private final StringProperty englishName = new SimpleStringProperty();
    //排序
    private final IntegerProperty sort = new SimpleIntegerProperty();

    public MeetInfoFx() {
    }

    public StringProperty getMeetingIdProperty() {
        return meetingId;
    }

    public StringProperty getMeetingNameProperty() {
        return meetingName;
    }

    public StringProperty getStartDateTimeProperty() {
        return startDateTime;
    }

    public StringProperty getEndDateTimeProperty() {
        return endDateTime;
    }

    public StringProperty getStateProperty() {
        return state;
    }

    public StringProperty getCreateTimeProperty() {
        return createTime;
    }

    public StringProperty getParentMeetingIdProperty() {
        return parentMeetingId;
    }

    public StringProperty getEnglishNameProperty() {
        return englishName;
    }

    public IntegerProperty getSortProperty() {
        return sort;
    }
    
    public String getMeetingId() {
        return meetingId.get();
    }

    public void setMeetingId(String meetingId) {
        this.meetingId.set(meetingId);
    }

    public String getMeetingName() {
        return meetingName.get();
    }

    public void setMeetingName(String meetingName) {
        this.meetingName.set(meetingName);
    }

    public String getStartDateTime() {
        return startDateTime.get();
    }

    public void setStartDateTime(String startDateTime) {
        this.startDateTime.set(startDateTime);
    }

    public String getEndDateTime() {
        return endDateTime.get();
    }

    public void setEndDateTime(String endDateTime) {
        this.endDateTime.set(endDateTime);
    }

    public String getState() {
        return state.get();
    }

    public void setState(String state) {
        this.state.set(state);
    }

    public String getCreateTime() {
        return createTime.get();
    }

    public void setCreateTime(String createTime) {
        this.createTime.set(createTime);
    }

    public String getParentMeetingId() {
        return parentMeetingId.get();
    }

    public void setParentMeetingId(String parentMeetingId) {
        this.parentMeetingId.set(parentMeetingId);
    }

    public int getSort() {
        return sort.get();
    }

    public void setSort(int sort) {
        this.sort.set(sort);
    }

    public String getEnglishName() {
        return englishName.get();
    }

    public void setEnglishName(String englishName) {
        this.englishName.set(englishName);
    }

    /**
     * 解决当插入ListView中的集合是自定义类时，ListView不能正常显示的问题，
     * 通过重写自定义类toString方法将内容输出成一个字符串，可使ListView显示
     * toString方法的返回值。
     *
     * @return 要在ListView显示的String
     */
    @Override
    public String toString() {
        return this.getMeetingName(); //To change body of generated methods, choose Tools | Templates.
    }
    
    
}
