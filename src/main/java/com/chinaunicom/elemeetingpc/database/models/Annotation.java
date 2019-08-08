/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chinaunicom.elemeetingpc.database.models;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * 批注类
 * @author zhaojunfeng
 */
@DatabaseTable(tableName = "meet_annotation")
public class Annotation  implements BaseModel{
    //id
    @DatabaseField(columnName = "annoId",id = true)
    private String annoId;
    
    //批注时间
    @DatabaseField(columnName = "annoDate")
    private String annoDate;
    
    //xPoint
    @DatabaseField(columnName = "xPoint")
    private String xPoint;
    
    //yPoint
    @DatabaseField(columnName = "yPoint")
    private String yPoint;
    
    //批注内容
    @DatabaseField(columnName = "content")
    private String content;
    
    //height
    @DatabaseField(columnName = "height")
    private String height;
    
    //width
    @DatabaseField(columnName = "width")
    private String width;    
    
    //批注页码
    @DatabaseField(columnName = "pageNum")
    private int pageNum;
    
    //文件Id
    @DatabaseField(columnName = "fileId")
    private String fileId;
    
    //批注类型
    @DatabaseField(columnName = "annoType")
    private String annoType;
           
    //状态
    @DatabaseField(columnName = "state")
    private String state;
   
    //
    @DatabaseField(columnName = "userId")
    private String userId;
    
    public Annotation() {
    }

    public Annotation(String annoId, String annoDate, String xPoint, String yPoint, String content, String height, String width, int pageNum, String fileId, String annoType, String state, String userId) {
        this.annoId = annoId;
        this.annoDate = annoDate;
        this.xPoint = xPoint;
        this.yPoint = yPoint;
        this.content = content;
        this.height = height;
        this.width = width;
        this.pageNum = pageNum;
        this.fileId = fileId;
        this.annoType = annoType;
        this.state = state;
        this.userId = userId;
    }

    public String getAnnoId() {
        return annoId;
    }

    public void setAnnoId(String annoId) {
        this.annoId = annoId;
    }

    public String getAnnoDate() {
        return annoDate;
    }

    public void setAnnoDate(String annoDate) {
        this.annoDate = annoDate;
    }

    public String getxPoint() {
        return xPoint;
    }

    public void setxPoint(String xPoint) {
        this.xPoint = xPoint;
    }

    public String getyPoint() {
        return yPoint;
    }

    public void setyPoint(String yPoint) {
        this.yPoint = yPoint;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getAnnoType() {
        return annoType;
    }

    public void setAnnoType(String annoType) {
        this.annoType = annoType;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    
    
}
