
package com.chinaunicom.elemeetingpc.modelFx;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
/**
 * This is a JavaFx wrapper class for FileResource, a JavaFx wrap class
 * encapsulates a Java primitive and adds some extra functionality 
 * (the classes under javafx.beans.property all contain built-in support 
 * for observability and binding as part of their design).
 * 
 * @author zhaojunfeng
 */
public class FileResourceFx {
    //id
    private final StringProperty fileId = new SimpleStringProperty();
    //名称
    private final StringProperty fileName = new SimpleStringProperty();
    //文件路径
    private final StringProperty filePath = new SimpleStringProperty();
    //文件大小
    private final StringProperty fileSize = new SimpleStringProperty();
    //加密密码
    private final StringProperty password = new SimpleStringProperty();
    //状态
    private final StringProperty state = new SimpleStringProperty();    
    //排序
    private final IntegerProperty sort = new SimpleIntegerProperty();

    public FileResourceFx() {
    }

    public StringProperty getFileIdProperty() {
        return fileId;
    }

    public StringProperty getFileNameProperty() {
        return fileName;
    }

    public StringProperty getFilePathProperty() {
        return filePath;
    }

    public StringProperty getFileSizeProperty() {
        return fileSize;
    }

    public StringProperty getPasswordProperty() {
        return password;
    }

    public StringProperty getStateProperty() {
        return state;
    }

    public IntegerProperty getSortProperty() {
        return sort;
    }
    
    public String getFileId() {
        return fileId.get();
    }

    public void setFileId(String fileId) {
        this.fileId.set(fileId);
    }

    public String getFileName() {
        return fileName.get();
    }

    public void setFileName(String fileName) {
        this.fileName.set(fileName);
    }

    public String getFilePath() {
        return filePath.get();
    }

    public void setFilePath(String filePath) {
        this.filePath.set(filePath);
    }

    public String getFileSize() {
        return fileSize.get();
    }

    public void setFileSize(String fileSize) {
        this.fileSize.set(fileSize);
    }

    public String getPassword() {
        return password.get();
    }

    public void setPassword(String password) {
        this.password.set(password);
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
    
}
