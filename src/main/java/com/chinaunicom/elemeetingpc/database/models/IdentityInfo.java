
package com.chinaunicom.elemeetingpc.database.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 身份
 * @author zhaojunfeng, chenxi
 */
@DatabaseTable(tableName = "IdentityInfo")
public class IdentityInfo implements BaseModel {
    
    //id
    @DatabaseField(generatedId = true)
    private int id;
    
    //身份中文名称
    @DatabaseField(columnName = "identityName")
    private String identityName;
    
    //身份英文名称
    @DatabaseField(columnName = "identityEnglishName")
    private String identityEnglishName;
    
    public IdentityInfo(){
        
    }
    
    public IdentityInfo(String identityName, String identityEnglishName){
        this.identityName = identityName;
        this.identityEnglishName = identityEnglishName;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public String getIdentityName() {
        return identityName;
    }

    public void setIdentityName(String identityName) {
        this.identityName = identityName;
    }
    
    public String getIdentityEnglishName() {
        return identityEnglishName;
    }

    public void setIdentityEnglishName(String identityEnglishName) {
        this.identityEnglishName = identityEnglishName;
    }
}
