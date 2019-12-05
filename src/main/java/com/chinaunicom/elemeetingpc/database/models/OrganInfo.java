
package com.chinaunicom.elemeetingpc.database.models;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import java.util.Objects;

/**
 * 组织机构实体类.
 *
 * @author zhaojunfeng, chenxi
 */
@DatabaseTable(tableName = "OrganInfo")
@SuppressWarnings("unchecked")
public class OrganInfo implements BaseModel {
    //id
    @DatabaseField(generatedId = true)
    private int id;
    
    //If UserInfo has a foreign collection of OrganInfos, then OrganInfo must have an UserInfo foreign field. It is required so ORMLite can find the orders that match a particular account.
    @DatabaseField(columnName = "USERINFO_ID", foreign = true, foreignAutoCreate = true, foreignAutoRefresh = true)
    private UserInfo userInfo;
    
    //组织名称
    @DatabaseField(columnName = "organizationName")
    private String organizationName;
    
    //组织id
    @DatabaseField(columnName = "organizationId", canBeNull = false)
    private String organizationId;
    
    //组织英文名称
    @DatabaseField(columnName = "organizationEnglishName")
    private String organizationEnglishName;
    
    //状态
    @DatabaseField(columnName = "state", defaultValue = "0")
    private String state;
    
    //用户ID
    @DatabaseField(columnName = "userId", canBeNull = false)
    private String userId;
    
    //更新时间updateDate
    @DatabaseField(columnName = "updateDate")
    private String updateDate;
    
    //投票倒计时countDownTime
    @DatabaseField(columnName = "countDownTime")
    private String countDownTime;
    
    //身份信息列表
    @ForeignCollectionField
    private ForeignCollection<IdentityInfo> identityList;

    public OrganInfo() {
    }

    public OrganInfo(UserInfo userInfo, String organizationName, String organizationId, String organizationEnglishName, String state, String userId, String updateDate, String countDownTime) {
        this.userInfo = userInfo;
        this.organizationName = organizationName;
        this.organizationId = organizationId;
        this.organizationEnglishName = organizationEnglishName;
        this.state = state;
        this.userId = userId;
        this.updateDate = updateDate;
        this.countDownTime = countDownTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
     public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
    
    public String getOrganizationName() {
        return organizationName;
    }

    public void setOrganizationName(String organizationName) {
        this.organizationName = organizationName;
    }

    public String getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }

    public String getOrganizationEnglishName() {
        return organizationEnglishName;
    }

    public void setOrganizationEnglishName(String organizationEnglishName) {
        this.organizationEnglishName = organizationEnglishName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }

    public String getCountDownTime() {
        return countDownTime;
    }

    public void setCountDownTime(String countDownTime) {
        this.countDownTime = countDownTime;
    }
    
    public ForeignCollection getIdentityList() {
        return identityList;
    }

    public void setIdentityList(ForeignCollection identityList) {
        this.identityList = identityList;
    }
    
    /**
     * 解决在List集合中使用removeAll对象去重的问题，重写OrganInfo类的equals方法，
     * 根据id，organizationId，organizationName三个字段判断对象是否相同。
     *
     * @return true if methods conditions are fullfilled
     */
    @Override
    public boolean equals(Object obj)  
   {  
       //任何对象不等于null，比较是否为同一类型
       if (!(obj instanceof OrganInfo)) return false;
       //强制类型转换
       OrganInfo organInfo = (OrganInfo)obj;
       //比较属性值
       return this.id == organInfo.id && 
               this.organizationId.equals(organInfo.organizationId) && 
               this.organizationName.equals(organInfo.organizationName);
   }

    /**
     * 重写hashCode方法,两个对象获取的HashCode也相等.
     *
     * @return hash
     */
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 17 * hash + this.id;
        hash = 17 * hash + Objects.hashCode(this.organizationName);
        hash = 17 * hash + Objects.hashCode(this.organizationId);
        return hash;
    }
}