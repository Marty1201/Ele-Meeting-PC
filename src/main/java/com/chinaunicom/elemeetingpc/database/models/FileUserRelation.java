
package com.chinaunicom.elemeetingpc.database.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 文件用户关系实体类.
 * @author zhaojunfeng
 */
@DatabaseTable(tableName = "FileUserRelation")
public class FileUserRelation implements BaseModel{
    //id
    @DatabaseField(columnName = "userFileId",id = true)
    private String userFileId;
    
    //用户Id
    @DatabaseField(columnName = "userId")
    private String userId;
    
    //文件Id
    @DatabaseField(columnName = "fileId")
    private String fileId;
           
    //状态
    @DatabaseField(columnName = "state")
    private String state;

    public FileUserRelation() {
    }

    public FileUserRelation(String userFileId, String userId, String fileId, String state) {
        this.userFileId = userFileId;
        this.userId = userId;
        this.fileId = fileId;
        this.state = state;
    }

    public String getUserFileId() {
        return userFileId;
    }

    public void setUserFileId(String userFileId) {
        this.userFileId = userFileId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
