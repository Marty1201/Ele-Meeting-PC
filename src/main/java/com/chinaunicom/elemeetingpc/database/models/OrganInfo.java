
package com.chinaunicom.elemeetingpc.database.models;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 组织机构
 *
 * @author zhaojunfeng, chenxi
 */
@DatabaseTable(tableName = "OrganInfo")
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
    //身份信息列表
    @ForeignCollectionField
    private ForeignCollection<IdentityInfo> identityList;

    public OrganInfo() {
    }

    public OrganInfo(UserInfo userInfo, String organizationName, String organizationId, String organizationEnglishName, String state, String userId) {
        this.userInfo = userInfo;
        this.organizationName = organizationName;
        this.organizationId = organizationId;
        this.organizationEnglishName = organizationEnglishName;
        this.state = state;
        this.userId = userId;
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

    public ForeignCollection getIdentityList() {
        return identityList;
    }

    public void setIdentityList(ForeignCollection identityList) {
        this.identityList = identityList;
    }
            
}
