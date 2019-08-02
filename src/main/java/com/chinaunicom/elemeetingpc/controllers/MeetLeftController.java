
package com.chinaunicom.elemeetingpc.controllers;

import com.chinaunicom.elemeetingpc.constant.GlobalStaticConstant;
import com.chinaunicom.elemeetingpc.modelFx.MeetInfoFx;
import com.chinaunicom.elemeetingpc.modelFx.MeetInfoModel;
import com.chinaunicom.elemeetingpc.utils.DialogsUtils;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

/**
 * 会议左侧列表controller
 * @author zhaojunfeng, chenxi
 */
public class MeetLeftController {
    
    @FXML
    private ListView<MeetInfoFx> meetCurrentListView;
    
    @FXML
    private ListView<MeetInfoFx> meetFutureListView;
    
    @FXML
    private ListView<MeetInfoFx> meetHistoryListView;
    
    @FXML
    private TextField textFieldUsername;
    
    private MeetInfoModel meetInfoModel;
    
    //初始化
    @FXML
    public void initialize(){
        textFieldUsername.setText(GlobalStaticConstant.GLOBAL_USERINFO_USERNAME);
        meetInfoModel = new MeetInfoModel();
        ObservableList<MeetInfoFx> fxlist1 = meetInfoModel.getCurrentMeetInfoFxs();
        int fxlist1_size = fxlist1.size();
        meetCurrentListView.setItems(fxlist1);
        meetCurrentListView.setPrefHeight(fxlist1_size*40+5);
        //添加监听事件
        meetCurrentListView.getSelectionModel().selectedItemProperty().addListener(
                (ObservableValue<? extends MeetInfoFx > observable,MeetInfoFx oldValue,MeetInfoFx newValue) -> {
                    System.out.println("newValue: " + newValue);
                    System.out.println("oldValue: " + oldValue);
                    System.out.println("observable-meetId: " + observable.getValue().getMeetingId());
                    System.out.println("observable-meetName: " + observable.getValue().getMeetingName()); 
                }
        );
        
        ObservableList<MeetInfoFx> fxlist2 = meetInfoModel.getFutureMeetInfoFxs();
        int fxlist2_size = fxlist2.size();        
        meetFutureListView.setItems(fxlist2);
        meetFutureListView.setPrefHeight(fxlist2_size*40+5);
        //添加监听事件
        meetFutureListView.getSelectionModel().selectedItemProperty().addListener(
                (ObservableValue<? extends MeetInfoFx > observable,MeetInfoFx oldValue,MeetInfoFx newValue) -> {
                    System.out.println("newValue: " + newValue);
                    System.out.println("oldValue: " + oldValue);
                    System.out.println("observable-meetId: " + observable.getValue().getMeetingId());
                    System.out.println("observable-meetName: " + observable.getValue().getMeetingName()); 
                }
        );
        
        ObservableList<MeetInfoFx> fxlist3 = meetInfoModel.getHistoryMeetInfoFxs();
        int fxlist3_size = fxlist3.size();
        meetHistoryListView.setItems(fxlist3);
        meetHistoryListView.setPrefHeight(fxlist3_size*40+5);
        //添加监听事件
        meetHistoryListView.getSelectionModel().selectedItemProperty().addListener(
                (ObservableValue<? extends MeetInfoFx > observable,MeetInfoFx oldValue,MeetInfoFx newValue) -> {
                    System.out.println("newValue: " + newValue);
                    System.out.println("oldValue: " + oldValue);
                    System.out.println("observable-meetId: " + observable.getValue().getMeetingId());
                    System.out.println("observable-meetName: " + observable.getValue().getMeetingName()); 
                }
        );
    }    
    
    /**
     * 退出
     */
    @FXML
    private void handExit(){
        boolean result = false;
        if(result = DialogsUtils.confirmationAlert()){
            Platform.exit();
        }
    }
}
