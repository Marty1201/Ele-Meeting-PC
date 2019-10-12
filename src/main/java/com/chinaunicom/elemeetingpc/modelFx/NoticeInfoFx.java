
package com.chinaunicom.elemeetingpc.modelFx;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * 会议通知实体类，会议通知信息并未保存到数据库表中，会议通知非持久化，即只有在项目启动的情况下，通过网络访问
 * 服务器接口实时获取数据并进行数据展示.
 * @author zhaojunfeng, chenxi
 */
public class NoticeInfoFx {

    private final StringProperty noticeId = new SimpleStringProperty();

    private final StringProperty noticeTitle = new SimpleStringProperty();

    private final StringProperty createTime = new SimpleStringProperty();

    private final StringProperty noticeTypeName = new SimpleStringProperty();

    private final StringProperty noticeTypeEnglishName = new SimpleStringProperty();

    private final StringProperty userName = new SimpleStringProperty();

    private final StringProperty englishName = new SimpleStringProperty();

    private final IntegerProperty sort = new SimpleIntegerProperty();

    private final StringProperty noticeContent = new SimpleStringProperty();

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
}
