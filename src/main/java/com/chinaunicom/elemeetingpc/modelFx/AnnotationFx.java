
package com.chinaunicom.elemeetingpc.modelFx;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * This is a JavaFx wrapper class for Annotation, a JavaFx wrap class
 * encapsulates a Java primitive and adds some extra functionality 
 * (the classes under javafx.beans.property all contain built-in support 
 * for observability and binding as part of their design).
 * 
 * @author zhaojunfeng
 */
public class AnnotationFx {
    
    //id
    private final StringProperty annoId = new SimpleStringProperty();
    //批注时间
    private final StringProperty annoDate = new SimpleStringProperty();
    //xPoint
    private final StringProperty xPoint = new SimpleStringProperty();
    //yPoint
    private final StringProperty yPoint = new SimpleStringProperty();
    //批注内容
    private final StringProperty content = new SimpleStringProperty();
    //height
    private final StringProperty height = new SimpleStringProperty();
    //width
    private final StringProperty width = new SimpleStringProperty();
    //文件Id
    private final StringProperty fileId = new SimpleStringProperty();
    //批注类型
    private final StringProperty annoType = new SimpleStringProperty();
    //状态
    private final StringProperty state = new SimpleStringProperty();
    //userId
    private final StringProperty userId = new SimpleStringProperty();
    //批注页码
    private final IntegerProperty pageNum = new SimpleIntegerProperty();

    public AnnotationFx() {
    }

    public StringProperty getAnnoIdProperty() {
        return annoId;
    }

    public StringProperty getAnnoDateProperty() {
        return annoDate;
    }

    public StringProperty getxPointProperty() {
        return xPoint;
    }

    public StringProperty getyPointProperty() {
        return yPoint;
    }

    public StringProperty getContentProperty() {
        return content;
    }

    public StringProperty getHeightProperty() {
        return height;
    }

    public StringProperty getWidthProperty() {
        return width;
    }

    public StringProperty getFileIdProperty() {
        return fileId;
    }

    public StringProperty getAnnoTypeProperty() {
        return annoType;
    }

    public StringProperty getStateProperty() {
        return state;
    }

    public StringProperty getUserIdProperty() {
        return userId;
    }

    public IntegerProperty getPageNumProperty() {
        return pageNum;
    }
    
    public String getAnnoId() {
        return annoId.get();
    }

    public void setAnnoId(String annoId) {
        this.annoId.set(annoId);
    }

    public String getAnnoDate() {
        return annoDate.get();
    }

    public void setAnnoDate(String annoDate) {
        this.annoDate.set(annoDate);
    }

    public String getxPoint() {
        return xPoint.get();
    }

    public void setxPoint(String xPoint) {
        this.xPoint.set(xPoint);
    }

    public String getyPoint() {
        return yPoint.get();
    }

    public void setyPoint(String yPoint) {
        this.yPoint.set(yPoint);
    }

    public String getContent() {
        return content.get();
    }

    public void setContent(String content) {
        this.content.set(content);
    }

    public String getHeight() {
        return height.get();
    }

    public void setHeight(String height) {
        this.height.set(height);
    }

    public String getWidth() {
        return width.get();
    }

    public void setWidth(String width) {
        this.width.set(width);
    }

    public int getPageNum() {
        return pageNum.get();
    }

    public void setPageNum(int pageNum) {
        this.pageNum.set(pageNum);
    }

    public String getFileId() {
        return fileId.get();
    }

    public void setFileId(String fileId) {
        this.fileId.set(fileId);
    }

    public String getAnnoType() {
        return annoType.get();
    }

    public void setAnnoType(String annoType) {
        this.annoType.set(annoType);
    }

    public String getState() {
        return state.get();
    }

    public void setState(String state) {
        this.state.set(state);
    }

    public String getUserId() {
        return userId.get();
    }

    public void setUserId(String userId) {
        this.userId.set(userId);
    }
    
}
