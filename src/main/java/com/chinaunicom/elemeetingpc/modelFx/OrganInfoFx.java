
package com.chinaunicom.elemeetingpc.modelFx;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * This is a JavaFx wrapper class for OrganInfo, a JavaFx wrap class
 * encapsulates a Java primitive and adds some extra functionality 
 * (the classes under javafx.beans.property all contain built-in support 
 * for observability and binding as part of their design).
 * @author chenxi
 * 创建时间：2019-6-26 11:19:42
 */
public class OrganInfoFx {
    //id
    private final IntegerProperty id = new SimpleIntegerProperty();
    //用户信息
    private final ObjectProperty<UserInfoFx> userInfoFx = new SimpleObjectProperty<>();
    //组织名称
    private final StringProperty organizationName = new SimpleStringProperty();
    //组织id
    private final StringProperty organizationId = new SimpleStringProperty();
    //组织英文名称
    private final StringProperty organizationEnglishName = new SimpleStringProperty();
    //状态
    private final StringProperty state = new SimpleStringProperty();
    //用户ID
    private final StringProperty userId = new SimpleStringProperty();
    //用户ID
    private final StringProperty updateDate = new SimpleStringProperty();
    //用户ID
    private final StringProperty countDownTime = new SimpleStringProperty();
    
    public OrganInfoFx() {
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty(){
        return id;
    } 
    
    public void setId(int id) {
        this.id.set(id);
    }
    
    public UserInfoFx getUserInfoFx() {
        return userInfoFx.get();
    }

    public ObjectProperty<UserInfoFx> userInfoFxProperty(){
        return userInfoFx;
    } 
    
    public void setUserInfoFx(UserInfoFx userInfoFx) {
        this.userInfoFx.set(userInfoFx);
    }
    
    public String getOrganizationName() {
        return organizationName.get();
    }
    
    public StringProperty organizationNameProperty(){
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName.set(organizationName);
    }
    
    public String getOrganizationId() {
        return organizationId.get();
    }

    public StringProperty organizationIdProperty() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId.set(organizationId);
    }

    public String getOrganizationEnglishName() {
        return organizationEnglishName.get();
    }
    
    public StringProperty organizationEnglishNameProperty() {
        return organizationEnglishName;
    }

    public void setOrganizationEnglishName(String organizationEnglishName) {
        this.organizationEnglishName.set(organizationEnglishName);
    }
    
     public String getState() {
        return state.get();
    }
    
    public StringProperty stateProperty() {
        return state;
    }

    public void setState(String state) {
        this.state.set(state);
    }
    
    public String getUserId() {
        return userId.get();
    }

    public StringProperty userIdProperty() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId.set(userId);
    }
    
    public String getUpdateDate() {
        return updateDate.get();
    }

    public StringProperty updateDateProperty() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate.set(updateDate);
    }
    
    public String getCountDownTime() {
        return countDownTime.get();
    }

    public StringProperty countDownTimeProperty() {
        return countDownTime;
    }

    public void setCountDownTime(String countDownTime) {
        this.countDownTime.set(countDownTime);
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
        return getOrganizationName();
    }
}