
package com.chinaunicom.elemeetingpc.modelFx;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;



/**
 * This is a JavaFx wrapper class for UserInfo, a JavaFx wrap class
 * encapsulates a Java primitive and adds some extra functionality 
 * (the classes under javafx.beans.property all contain built-in support 
 * for observability and binding as part of their design).
 * @author chenxi
 * 创建时间：2019-6-26 13:06:18
 */
public class UserInfoFx {
    //id
    private final IntegerProperty id = new SimpleIntegerProperty();
    //登录账号
    private final StringProperty loginName = new SimpleStringProperty();
    //用户名称
    private final StringProperty userName = new SimpleStringProperty();
    //密码（加密后）
    private final StringProperty password = new SimpleStringProperty();
    //用户英文名称
    private final StringProperty englishName = new SimpleStringProperty();
    //手机号
    private final StringProperty phone = new SimpleStringProperty();
    //状态
    private final StringProperty state = new SimpleStringProperty();
    //性别（'男','女'）
    private final StringProperty sexName = new SimpleStringProperty();
    //性别英文（'man','feman'）
    private final StringProperty sexEnglishName = new SimpleStringProperty();
    //排序号
    private final StringProperty sort = new SimpleStringProperty();
    
    public UserInfoFx(){
        
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
    
    public String getLoginName() {
        return loginName.get();
    }
    
    public StringProperty loginNameProperty(){
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName.set(loginName);
    }
    
    public String getUserName() {
        return userName.get();
    }
    
    public StringProperty userNameProperty(){
        return userName;
    }

    public void setUserName(String userName) {
        this.userName.set(userName);
    }
    
    public String getPassword() {
        return password.get();
    }
    
    public StringProperty passwordProperty(){
        return password;
    }

    public void setPassword(String password) {
        this.password.set(password);
    }
    
    public String getEnglishName() {
        return englishName.get();
    }
    
    public StringProperty englishNameProperty(){
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName.set(englishName);
    }
    
    public String getPhone() {
        return phone.get();
    }
    
    public StringProperty phoneProperty(){
        return phone;
    }

    public void setPhone(String phone) {
        this.phone.set(phone);
    }
    
    public String getState() {
        return state.get();
    }
    
    public StringProperty stateProperty(){
        return state;
    }

    public void setState(String state) {
        this.state.set(state);
    }
    
    public String getSexName() {
        return sexName.get();
    }
    
    public StringProperty sexNameProperty(){
        return sexName;
    }

    public void setSexName(String sexName) {
        this.sexName.set(sexName);
    }
    
    public String getSexEnglishName() {
        return sexEnglishName.get();
    }
    
    public StringProperty sexEnglishNameProperty(){
        return sexEnglishName;
    }

    public void setSexEnglishName(String sexEnglishName) {
        this.sexEnglishName.set(sexEnglishName);
    }
    
    public String getSort() {
        return sort.get();
    }
    
    public StringProperty sortProperty(){
        return sort;
    }

    public void setSort(String sort) {
        this.sort.set(sort);
    }

    @Override
    public String toString() {
        return "UserInfoFx{"
                + "id=" + id
                + ", loginName" + loginName
                + ", userName=" + userName
                + ", password" + password
                + ", englishName=" + englishName
                + ", phone=" + phone
                + ", state=" + state
                + ", sexName=" + sexName
                + ", sexEnglishName=" + sexEnglishName
                + ", sort=" + sort
                + '}';
    }
}
