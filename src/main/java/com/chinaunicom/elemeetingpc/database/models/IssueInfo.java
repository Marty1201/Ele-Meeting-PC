package com.chinaunicom.elemeetingpc.database.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 *议题类
 * @author zhaojunfeng
 */
@DatabaseTable(tableName = "issueInfo")
public class IssueInfo implements BaseModel{
    //id
    @DatabaseField(columnName = "issueId",id = true)
    private String issueId;
    
    //议题名称
    @DatabaseField(columnName = "issueName")
    private String issueName;
           
    //状态
    @DatabaseField(columnName = "state")
    private String state;
           
    //排序
    @DatabaseField(columnName = "sort")
    private int sort;
    
    //英文名称
    @DatabaseField(columnName = "englishName")
    private String englishName;

    public IssueInfo() {
    }

    public IssueInfo(String issueId, String issueName, String state, int sort, String englishName) {
        this.issueId = issueId;
        this.issueName = issueName;
        this.state = state;
        this.sort = sort;
        this.englishName = englishName;
    }

    public String getIssueId() {
        return issueId;
    }

    public void setIssueId(String issueId) {
        this.issueId = issueId;
    }

    public String getIssueName() {
        return issueName;
    }

    public void setIssueName(String issueName) {
        this.issueName = issueName;
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

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName(String englishName) {
        this.englishName = englishName;
    }
    
    
}
