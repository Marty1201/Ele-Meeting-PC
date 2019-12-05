
package com.chinaunicom.elemeetingpc.database.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;


/**
 * 字典实体类，用来存放系统内需要持久化保存的数据.
 * @author chenxi
 * 创建时间：2019-7-12 18:12:26
 */
@DatabaseTable(tableName = "DictionaryInfo")
public class DictionaryInfo implements BaseModel {
    //id
    @DatabaseField(generatedId = true)
    private int id;
    //更新日期
    @DatabaseField(columnName = "updateDate")
    private String updateDate;
    //设备注册码
    @DatabaseField(columnName = "registerCode")
    private String registerCode;
    
	public DictionaryInfo(){
        
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
    
    public String getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(String updateDate) {
        this.updateDate = updateDate;
    }
    
    public String getRegisterCode() {
        return registerCode;
    }

    public void setRegisterCode(String registerCode) {
        this.registerCode = registerCode;
    }

}
