
package com.chinaunicom.elemeetingpc.controllers;

import com.chinaunicom.elemeetingpc.constant.GlobalStaticConstant;
import com.chinaunicom.elemeetingpc.modelFx.MeetInfoFx;
import com.chinaunicom.elemeetingpc.modelFx.MeetInfoModel;
import com.chinaunicom.elemeetingpc.service.MeetService;
import com.chinaunicom.elemeetingpc.utils.DialogsUtils;
import com.chinaunicom.elemeetingpc.utils.FxmlUtils;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import java.io.IOException;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;

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
    
    private static final Logger logger = LoggerFactory.getLogger(MeetLeftController.class);
    
    private BorderPane borderPaneMain;
    
    //会议界面中部数据
    public static final String FXML_INDEX = "/fxml/fxml_index.fxml";
    
    //初始化
    @FXML
    public void initialize(){
        textFieldUsername.setText(GlobalStaticConstant.GLOBAL_USERINFO_USERNAME);
        meetInfoModel = new MeetInfoModel();
        MeetService meetService = new MeetService();
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
                    
                    GlobalStaticConstant.GLOBAL_SELECTED_MEETID = observable.getValue().getMeetingId();
                    //调用接口，从远程服务器上获取会议相关信息
                    meetService.getMeetInfosFromRemote();
                    showFxmlMeet();
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
                    GlobalStaticConstant.GLOBAL_SELECTED_MEETID = observable.getValue().getMeetingId();
                    //调用接口，从远程服务器上获取会议相关信息
                    meetService.getMeetInfosFromRemote();
                    showFxmlMeet();
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
                    GlobalStaticConstant.GLOBAL_SELECTED_MEETID = observable.getValue().getMeetingId();
                    //调用接口，从远程服务器上获取会议相关信息
                    meetService.getMeetInfosFromRemote();
                    showFxmlMeet();
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
    
     /**
     * 跳转会议界面
     */
    private void showFxmlMeet(){
        try {            
            FXMLLoader loader = FxmlUtils.getFXMLLoader(FXML_INDEX);
            borderPaneMain.setCenter(loader.load()); //将当前BorderPane中间区域加载为机构选择界面
            MeetController meetController = loader.getController(); //从loader中获取MeetController
            meetController.setBorderPane(borderPaneMain);//设置传参当前的borderPane，以便在MeetController中获取到当前BorderPane
        } catch (IOException e) {
            logger.error(e.getCause().getMessage());
        }       
    }
    
    public void setBorderPane(BorderPane borderPaneMain) {
        this.borderPaneMain = borderPaneMain;
    }
}
