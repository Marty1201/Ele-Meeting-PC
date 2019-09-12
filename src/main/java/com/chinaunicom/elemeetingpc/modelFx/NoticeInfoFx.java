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
public class NoticeInfoFx {
    //
    private final StringProperty noticeId = new SimpleStringProperty();
    //
    private final StringProperty noticeTitle = new SimpleStringProperty();
    //
    private final StringProperty createTime = new SimpleStringProperty();
    //
    private final StringProperty noticeTypeName = new SimpleStringProperty();
    //
    private final StringProperty noticeTypeEnglishName = new SimpleStringProperty();
    //单位管理员
    private final StringProperty userName = new SimpleStringProperty();
    //单位管理员英文
    private final StringProperty englishName = new SimpleStringProperty();
    //
    private final IntegerProperty sort = new SimpleIntegerProperty();
     //
    private final StringProperty noticeContent = new SimpleStringProperty();
     //
    private final StringProperty state = new SimpleStringProperty();

    public NoticeInfoFx() {
    }

    public StringProperty getNoticeIdProperty() {
        return noticeId;
    }
    public String getNoticeId() {
        return noticeId.get();
    }
    
    public void setNoticeId(String noticeId){
        this.noticeId.set(noticeId);
    }

    public StringProperty getNoticeTitleProperty() {
        return noticeTitle;
    }
    public String getNoticeTitle() {
        return noticeTitle.get();
    }
    public void setNoticeTitle(String noticeTitle){
        this.noticeTitle.set(noticeTitle);
    }

    public StringProperty getCreateTimeProperty() {
        return createTime;
    }
    public String getCreateTime() {
        return createTime.get();
    }
    public void setCreateTime(String createTime){
        this.createTime.set(createTime);
    }

    public StringProperty getNoticeTypeNameProperty() {
        return noticeTypeName;
    }
    public String getNoticeTypeName() {
        return noticeTypeName.get();
    }
    public void setNoticeTypeName(String typeName){
        this.noticeTypeName.set(typeName);
    }

    public StringProperty getNoticeTypeEnglishNameProperty() {
        return noticeTypeEnglishName;
    }
    public String getNoticeTypeEnglishName() {
        return noticeTypeEnglishName.get();
    }
    public void setNoticeTypeEnglishName(String typeName){
        this.noticeTypeEnglishName.set(typeName);
    }

    public StringProperty getUserNameProperty() {
        return userName;
    }
    public String getUserName() {
        return userName.get();
    }
    public void setUserName(String userName){
        this.userName.set(userName);
    }

    public StringProperty getEnglishNameProperty() {
        return englishName;
    }
    public String getEnglishName() {
        return englishName.get();
    }
    public void setEnglishName(String englishName){
        this.englishName.set(englishName);
    }

    public IntegerProperty getSortProperty() {
        return sort;
    }
    public int getSort() {
        return sort.get();
    }
    public void setSort(int sort){
        this.sort.set(sort);
    }

    public StringProperty getNoticeContentProperty() {
        return noticeContent;
    }
    public String getNoticeContent() {
        return noticeContent.get();
    }
    public void setNoticeContent(String content){
        this.noticeContent.set(content);
    }

    public StringProperty getStateProperty() {
        return state;
    }
    public String getState() {
        return state.get();
    }
    public void setState(String state){
        this.state.set(state);
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
        return this.getNoticeTitle();
    }
    
}
