
package com.chinaunicom.elemeetingpc.controllers;

import com.chinaunicom.elemeetingpc.constant.GlobalStaticConstant;
import com.chinaunicom.elemeetingpc.database.models.MeetInfo;
import com.chinaunicom.elemeetingpc.database.models.MeetUserRelation;
import com.chinaunicom.elemeetingpc.modelFx.MeetInfoFx;
import com.chinaunicom.elemeetingpc.modelFx.MeetInfoModel;
import com.chinaunicom.elemeetingpc.modelFx.MeetUserRelationModel;
import com.chinaunicom.elemeetingpc.service.MeetService;
import com.chinaunicom.elemeetingpc.utils.DialogsUtils;
import com.chinaunicom.elemeetingpc.utils.FxmlUtils;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;
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
    
    private MeetUserRelationModel meetUserRelationModel;
    
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
        try {
            List<String> parentMeetIdList = this.getParentMeetList(GlobalStaticConstant.GLOBAL_ORGANINFO_OWNER_USERID);//to do: zhaojunfeng
            ObservableList<MeetInfoFx> fxlist1 = meetInfoModel.getCurrentMeetInfoFxs(parentMeetIdList);
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
            
            ObservableList<MeetInfoFx> fxlist2 = meetInfoModel.getFutureMeetInfoFxs(parentMeetIdList);//to do: zhaojunfeng
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
            
            ObservableList<MeetInfoFx> fxlist3 = meetInfoModel.getHistoryMeetInfoFxs(parentMeetIdList);//to do: zhaojunfeng
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
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        
    }    
    
    /**
     * 退出
     */
    @FXML
    public void handExit(){
        boolean result = false;
        if(result = DialogsUtils.confirmationAlert()){
            Platform.exit();
        }
    }
    
     /**
     * 跳转会议界面
     */
    public void showFxmlMeet(){
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
    
    /**
     * 根据用户id获取其所在的父会议id列表.
     *
     * @param userId
     * @return parentMeetIdList 父会议id列表
     * @throws ApplicationException
     * @throws java.sql.SQLException
     */
    public List<String> getParentMeetList(String userId) throws ApplicationException, SQLException{
        List<String> parentMeetIdList = new ArrayList<>();
        this.meetUserRelationModel = new MeetUserRelationModel();
        List<MeetUserRelation> meetUserRelationList = new ArrayList<>();
        //根据用户id在会议用户关系表里查询其对应的所有子会议
        meetUserRelationList = meetUserRelationModel.queryMeetUserRelationByUserId(userId);
        for(MeetUserRelation meetUserRelation:meetUserRelationList){
            //根据子会议获取其父会议
            parentMeetIdList.add(getParentMeetIdByChildMeetId(meetUserRelation.getMeetingId()));
        }
        //去重
        parentMeetIdList = parentMeetIdList.stream().distinct().collect(Collectors.toList());
        return parentMeetIdList;
    }
    
    /**
     * 根据子会议id获取其父会议id.
     *
     * @param childMeetId
     * @return parentMeetId 父会议id
     * @throws ApplicationException
     * @throws java.sql.SQLException
     */
    public String getParentMeetIdByChildMeetId(String childMeetId) throws ApplicationException, SQLException{
        String parentMeetId = "";
        this.meetInfoModel = new MeetInfoModel();
        List<MeetInfo> meetInfoList = meetInfoModel.queryMeetInfoByChildMeetId(childMeetId);
        if(!meetInfoList.isEmpty()){
            parentMeetId = meetInfoList.get(0).getParentMeetingId();
        }
        return parentMeetId;
    }
    
}
