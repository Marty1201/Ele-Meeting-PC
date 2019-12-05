package com.chinaunicom.elemeetingpc.database.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 *  文件实体类.
 * @author zhaojunfeng
 */
@DatabaseTable(tableName = "FileResource")
public class FileResource implements BaseModel{
    
    //id
    @DatabaseField(columnName = "fileId",id = true)
    private String fileId;
    
    //文件名称
    @DatabaseField(columnName = "fileName")
    private String fileName;
    
    //文件路径
    @DatabaseField(columnName = "filePath")
    private String filePath;
    
    //文件大小
    @DatabaseField(columnName = "fileSize")
    private String fileSize;
    
    //文件密码
    @DatabaseField(columnName = "password")
    private String password;
    
    //文件状态
    @DatabaseField(columnName = "state")
    private String state;
    
    //文件排序
    @DatabaseField(columnName = "sort")
    private int sort;
    
    
    public FileResource() {
    }

    public FileResource(String fileId, String fileName, String filePath, String fileSize, String password, String state, int sort) {
        this.fileId = fileId;
        this.fileName = fileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.password = password;
        this.state = state;
        this.sort = sort;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }
}
