
package com.chinaunicom.elemeetingpc.controllers;

import com.chinaunicom.elemeetingpc.constant.GlobalStaticConstant;
import com.chinaunicom.elemeetingpc.modelFx.MeetInfoFx;
import com.chinaunicom.elemeetingpc.modelFx.MeetInfoModel;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

/**
 * 会议左侧列表controller
 * @author zhaojunfeng
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
    public void initialize(){
        textFieldUsername.setText(GlobalStaticConstant.GLOBAL_USERINFO_LOGINNAME);
        meetInfoModel = new MeetInfoModel();
        meetCurrentListView.setItems(meetInfoModel.getCurrentMeetInfoFxs());
        //添加监听事件
        meetCurrentListView.getSelectionModel().selectedItemProperty().addListener(
                (ObservableValue<? extends MeetInfoFx > observable,MeetInfoFx oldValue,MeetInfoFx newValue) -> {
                    System.out.println("newValue: " + newValue);
                    System.out.println("oldValue: " + oldValue);
                    System.out.println("observable-meetId: " + observable.getValue().getMeetingId());
                    System.out.println("observable-meetName: " + observable.getValue().getMeetingName()); 
                }
        );
        
        meetFutureListView.setItems(meetInfoModel.getFutureMeetInfoFxs());
        //添加监听事件
        meetFutureListView.getSelectionModel().selectedItemProperty().addListener(
                (ObservableValue<? extends MeetInfoFx > observable,MeetInfoFx oldValue,MeetInfoFx newValue) -> {
                    System.out.println("newValue: " + newValue);
                    System.out.println("oldValue: " + oldValue);
                    System.out.println("observable-meetId: " + observable.getValue().getMeetingId());
                    System.out.println("observable-meetName: " + observable.getValue().getMeetingName()); 
                }
        );
        
        meetHistoryListView.setItems(meetInfoModel.getHistoryMeetInfoFxs());
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
}
