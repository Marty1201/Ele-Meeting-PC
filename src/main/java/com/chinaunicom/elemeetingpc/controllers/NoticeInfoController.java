/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chinaunicom.elemeetingpc.controllers;

import com.chinaunicom.elemeetingpc.constant.GlobalStaticConstant;
import com.chinaunicom.elemeetingpc.modelFx.NoticeInfoFx;
import com.chinaunicom.elemeetingpc.service.NoticeInfoService;
import com.chinaunicom.elemeetingpc.utils.FxmlUtils;
import java.io.IOException;
import java.util.logging.Level;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 *通知信息controller
 * @author zhaojunfeng
 */
public class NoticeInfoController {
    
    @FXML
    private ListView<NoticeInfoFx> noticeListView;
    
     //通知详情界面
    public static final String FXML_NOTICE_DETATIL = "/fxml/fxml_notice_detail.fxml";    
    
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
                    showNoticeDetailList(newValue.getNoticeId());
                }
        );
    }
    
    /**
     * 通知详情
     */
    private void showNoticeDetailList(String noticeId){
        FXMLLoader loader = FxmlUtils.getFXMLLoader(FXML_NOTICE_DETATIL);
        AnchorPane noticeDetailDialog=new AnchorPane();
        try {
            noticeDetailDialog = loader.load();
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(NoticeInfoController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Stage detailStage = new Stage();
        detailStage.setTitle("通知信息详情");
        detailStage.initModality(Modality.WINDOW_MODAL);
        detailStage.initOwner(dialogStage);
        Scene scene = new Scene(noticeDetailDialog);
        detailStage.setScene(scene);
        
        NoticeDetailController controller = loader.getController();
        controller.setDialogStage(detailStage);
        
        noticeInfoService = new NoticeInfoService();
        NoticeInfoFx fx = noticeInfoService.getNoticeDeatilFXById(noticeId);
        controller.setNoticeInfoFx(fx);
        controller.showNoticeDetail(fx);
        
        detailStage.showAndWait(); 
    }
    
}
