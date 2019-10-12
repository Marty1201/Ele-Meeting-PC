package com.chinaunicom.elemeetingpc.modelFx;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * 会议通知附件实体类，与会议通知一样，不对该类进行持久化数据保存.
 *
 * @author chenxi 创建时间：2019-10-11 10:32:39
 */
public class NoticeAccessoriesFx {

    private final StringProperty id = new SimpleStringProperty();

    private final StringProperty fileName = new SimpleStringProperty();

    private final StringProperty filePath = new SimpleStringProperty();

    private final StringProperty fileSize = new SimpleStringProperty();

    public NoticeAccessoriesFx() {

    }

    public StringProperty getIdProperty() {
        return id;
    }

    public String getId() {
        return id.get();
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public StringProperty getFileNameProperty() {
        return fileName;
    }

    public String getFileName() {
        return fileName.get();
    }

    public void setFileName(String fileName) {
        this.fileName.set(fileName);
    }

    public StringProperty getFilePathProperty() {
        return filePath;
    }

    public String getFilePath() {
        return filePath.get();
    }

    public void setFilePath(String filePath) {
        this.filePath.set(filePath);
    }

    public StringProperty getFileSizeProperty() {
        return fileSize;
    }

    public String getFileSize() {
        return fileSize.get();
    }

    public void setFileSize(String fileSize) {
        this.fileSize.set(fileSize);
    }
}