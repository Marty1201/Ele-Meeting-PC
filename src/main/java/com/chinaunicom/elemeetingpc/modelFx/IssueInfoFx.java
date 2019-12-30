
package com.chinaunicom.elemeetingpc.modelFx;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * This is a JavaFx wrapper class for IssueInfo, a JavaFx wrap class
 * encapsulates a Java primitive and adds some extra functionality 
 * (the classes under javafx.beans.property all contain built-in support 
 * for observability and binding as part of their design).
 * @author zhaojunfeng
 */
public class IssueInfoFx {
    //id
    private final StringProperty issueId = new SimpleStringProperty();
    //议题名称
    private final StringProperty issueName = new SimpleStringProperty();
    //状态
    private final StringProperty state = new SimpleStringProperty();
    //英文名称
    private final StringProperty englishName = new SimpleStringProperty();
    //排序
    private final IntegerProperty sort = new SimpleIntegerProperty();

    public IssueInfoFx() {
    }

    public StringProperty getIssueIdProperty() {
        return issueId;
    }

    public StringProperty getIssueNameProperty() {
        return issueName;
    }

    public StringProperty getStateProperty() {
        return state;
    }

    public StringProperty getEnglishNameProperty() {
        return englishName;
    }

    public IntegerProperty getSortProperty() {
        return sort;
    }
    
    public String getIssueId() {
        return issueId.get();
    }

    public void setIssueId(String issueId) {
        this.issueId.set(issueId);
    }

    public String getIssueName() {
        return issueName.get();
    }

    public void setIssueName(String issueName) {
        this.issueName.set(issueName);
    }

    public String getState() {
        return state.get();
    }

    public void setState(String state) {
        this.state.set(state);
    }

    public int getSort() {
        return sort.get();
    }

    public void setSort(int sort) {
        this.sort.set(sort);
    }

    public String getEnglishName() {
        return englishName.get();
    }

    public void setEnglishName(String englishName) {
        this.englishName.set(englishName);
    }
    
    
}
