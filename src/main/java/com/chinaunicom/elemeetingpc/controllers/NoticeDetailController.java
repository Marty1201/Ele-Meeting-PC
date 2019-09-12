/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chinaunicom.elemeetingpc.controllers;

import com.chinaunicom.elemeetingpc.modelFx.NoticeInfoFx;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

/**
 *
 * @author zhaojunfeng
 */
public class NoticeDetailController {
    @FXML
    private Label titlelLabel;
    @FXML
    private Label createTimelLabel;
    @FXML
    private Label usernameLabel;
    @FXML
    private Label noticeTypeLabel;
    @FXML
    private TextArea noticeContentArea;
    
    private NoticeInfoFx noticeInfoFx;
    private Stage dialogStage;
    
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setNoticeInfoFx(NoticeInfoFx noticeInfoFx) {
        this.noticeInfoFx = noticeInfoFx;
    }
    
    @FXML
    private void initialize(){
        System.out.println("通知详情界面");
        
    }
    
    public void showNoticeDetail(NoticeInfoFx noticeInfoFx){
        if(noticeInfoFx!=null){
            titlelLabel.setText(noticeInfoFx.getNoticeTitle());
            createTimelLabel.setText(noticeInfoFx.getCreateTime());
            usernameLabel.setText(noticeInfoFx.getUserName());
            noticeTypeLabel.setText(noticeInfoFx.getNoticeTypeName());
            noticeContentArea.setText(noticeInfoFx.getNoticeContent());
        }else{
            titlelLabel.setText("");
            createTimelLabel.setText("");
            usernameLabel.setText("");
            noticeTypeLabel.setText("");
            noticeContentArea.setText("");
        }
    }
    
}
