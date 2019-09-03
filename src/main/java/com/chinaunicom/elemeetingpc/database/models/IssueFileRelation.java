
package com.chinaunicom.elemeetingpc.database.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 *议题-文件资源关联类
 * @author zhaojunfeng
 */
@DatabaseTable(tableName = "IssueFileRelation")
public class IssueFileRelation implements BaseModel{
    //id
    @DatabaseField(columnName = "issueFileInfo",id = true)
    private String issueFileInfo;
    
    //议题Id
    @DatabaseField(columnName = "issueId")
    private String issueId;
    
    //文件Id
    @DatabaseField(columnName = "fileId")
    private String fileId;
           
    //状态
    @DatabaseField(columnName = "state")
    private String state;
    
    //文件名称
    @DatabaseField(columnName = "fileName")
    private String fileName;
           
    //排序
    @DatabaseField(columnName = "sort")
    private int sort;

    public IssueFileRelation() {
    }

    public IssueFileRelation(String issueFileInfo, String issueId, String fileId, String state, String fileName, int sort) {
        this.issueFileInfo = issueFileInfo;
        this.issueId = issueId;
        this.fileId = fileId;
        this.state = state;
        this.fileName = fileName;
        this.sort = sort;
    }

    public String getIssueFileInfo() {
        return issueFileInfo;
    }

    public void setIssueFileInfo(String issueFileInfo) {
        this.issueFileInfo = issueFileInfo;
    }

    public String getIssueId() {
        return issueId;
    }

    public void setIssueId(String issueId) {
        this.issueId = issueId;
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

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }
    
    
}
