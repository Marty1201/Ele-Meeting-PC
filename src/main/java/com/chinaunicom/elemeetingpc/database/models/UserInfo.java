package com.chinaunicom.elemeetingpc.database.models;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 用户实体类.
 *
 * @author zhaojunfeng, chenxi
 */
@DatabaseTable(tableName = "UserInfo")
public class UserInfo implements BaseModel {
    //id
    @DatabaseField(generatedId = true)
    private int id;
    
    //登录账号
    @DatabaseField(columnName = "loginName", canBeNull = false)
    private String loginName;
    
    //用户名称
    @DatabaseField(columnName = "userName", canBeNull = false)
    private String userName;
    
    //密码（加密后）
    @DatabaseField(columnName = "password", canBeNull = false)
    private String password;
    
    //用户英文名称
    @DatabaseField(columnName = "englishName")
    private String englishName;
    
    //手机号
    @DatabaseField(columnName = "phone")
    private String phone;
    
    //状态
    @DatabaseField(columnName = "state", defaultValue = "0")
    private String state;
    
    //性别
    @DatabaseField(columnName = "sexName")
    private String sexName;
    
    //性别英文
    @DatabaseField(columnName = "sexEnglishName")
    private String sexEnglishName;
    
    //排序号
    @DatabaseField(columnName = "sort")
    private String sort;
    
    //组织机构列表
    @ForeignCollectionField
    private ForeignCollection<OrganInfo> orglist;

    //构造方法
    public UserInfo() {
    }

    //构造方法
    public UserInfo(String loginName, String userName, String password, String englishName, String phone, String state, String sexName, String sexEnglishName, String sort) {
        this.loginName = loginName;
        this.userName = userName;
        this.password = password;
        this.englishName = englishName;
        this.phone = phone;
        this.state = state;
        this.sexName = sexName;
        this.sexEnglishName = sexEnglishName;
        this.sort = sort;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getSexName() {
        return sexName;
    }

    public void setSexName(String sexName) {
        this.sexName = sexName;
    }

    public String getSexEnglishName() {
        return sexEnglishName;
    }

    public void setSexEnglishName(String sexEnglishName) {
        this.sexEnglishName = sexEnglishName;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public ForeignCollection<OrganInfo> getOrglist() {
        return orglist;
    }

    public void setOrglist(ForeignCollection<OrganInfo> orglist) {
        this.orglist = orglist;
    }
}
