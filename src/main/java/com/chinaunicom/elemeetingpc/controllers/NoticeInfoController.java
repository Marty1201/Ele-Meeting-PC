/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chinaunicom.elemeetingpc.controllers;

import com.chinaunicom.elemeetingpc.constant.GlobalStaticConstant;
import com.chinaunicom.elemeetingpc.modelFx.NoticeInfoFx;
import com.chinaunicom.elemeetingpc.service.NoticeInfoService;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

/**
 *通知信息controller
 * @author zhaojunfeng
 */
public class NoticeInfoController {
    
    @FXML
    private ListView<NoticeInfoFx> noticeListView;
    
    private Stage dialogStage;
    
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    
    private NoticeInfoService noticeInfoService;
    
    @FXML
    private void initialize(){
        System.out.println("通知列表界面");
        System.out.println("userId:"+GlobalStaticConstant.GLOBAL_ORGANINFO_OWNER_USERID);
        System.out.println("meetId:"+GlobalStaticConstant.GLOBAL_SELECTED_MEETID);
        noticeInfoService = new NoticeInfoService();
        noticeListView.setItems(noticeInfoService.getNoticeInfoListObservableList());
        
        noticeListView.getSelectionModel().selectedItemProperty().addListener(
                (ObservableValue<? extends NoticeInfoFx> observable, NoticeInfoFx oldValue, NoticeInfoFx newValue) -> {
                    //在全局常量里记录当前选择的机构id和用户id（服务器端的id）
                    System.out.println("title:"+newValue.getNoticeTitle());
                    System.out.println("noticeId:"+newValue.getNoticeId());
                    
                    //解析数据
                    
                    //跳转界面
                    
                }
        );
    }
    
}
