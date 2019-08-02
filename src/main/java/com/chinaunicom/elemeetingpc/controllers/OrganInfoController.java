
package com.chinaunicom.elemeetingpc.controllers;

import com.chinaunicom.elemeetingpc.constant.GlobalStaticConstant;
import com.chinaunicom.elemeetingpc.modelFx.OrganInfoFx;
import com.chinaunicom.elemeetingpc.modelFx.OrganInfoModel;
import com.chinaunicom.elemeetingpc.service.SelectOrganService;
import com.chinaunicom.elemeetingpc.utils.DialogsUtils;
import com.chinaunicom.elemeetingpc.utils.FxmlUtils;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import java.io.IOException;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;



/**
 * 组织机构控制器，在界面的列表上加载登陆人所属机构名称，选择机构后进入会议
 * 主界面.
 * @author chenxi
 * 创建时间：2019-6-27 14:20:17
 */
public class OrganInfoController {
    
//    @FXML
//    private ListView<String> organListView;
    
    @FXML
    private ListView<OrganInfoFx> organListView;
    
    private OrganInfoModel organInfoModel;
    
    private static final Logger logger = LoggerFactory.getLogger(OrganInfoController.class);
    
    //会议界面左侧列表
    public static final String FXML_LEFT_NAVIGATION = "/fxml/fxml_left_navigation.fxml";
    //会议界面顶部数据
    public static final String FXML_INDEX_TOP = "/fxml/fxml_index_top.fxml";
    //会议界面中部数据
    public static final String FXML_INDEX_CENTER = "/fxml/fxml_index_center.fxml";
    
    private BorderPane borderPane;

    public void setBorderPane(BorderPane borderPane) {
        this.borderPane = borderPane;
    }
    
    //初始化
    public void initialize(){
        this.organInfoModel = new OrganInfoModel();
        try{
            organInfoModel.init();
        }catch(ApplicationException e){
            DialogsUtils.errorAlert(e.getMessage());
        }
        int listSize = this.organInfoModel.getOrganInfoNameObservableList().size();
        organListView.setItems(this.organInfoModel.getOrganInfoNameObservableList());
        
        //ListView的高=list的size X 每条list-cell的size + 5(间距)
        organListView.setPrefHeight(listSize * 40 + 5);
        //添加事件监听器
        organListView.getSelectionModel().selectedItemProperty().addListener(
                (ObservableValue<? extends OrganInfoFx> observable, OrganInfoFx oldValue, OrganInfoFx newValue) -> {
                    //在全局常量里记录当前选择的机构id和用户id（服务器端的id）
                    GlobalStaticConstant.GLOBAL_ORGANINFO_ORGANIZATIONID = observable.getValue().getOrganizationId();
                    GlobalStaticConstant.GLOBAL_ORGANINFO_OWNER_USERID = observable.getValue().getUserId();
                    SelectOrganService service = new SelectOrganService();
                    //解析数据
                    service.getMeetInfosFromRemote();
                    //跳转界面
                    showFxmlMeet();
        });
    }
    
    /**
     * 跳转会议界面
     */
    private void showFxmlMeet(){  
        //加载顶部
        //borderPane.setTop(FxmlUtils.fxmlLoader(FXML_INDEX_TOP));
        //加载左侧
        //borderPane.setLeft(FxmlUtils.fxmlLoader(FXML_LEFT_NAVIGATION));
        //加载中部
        //borderPane.setCenter(FxmlUtils.fxmlLoader(FXML_LEFT_NAVIGATION));
        
        try {            
            FXMLLoader loader = FxmlUtils.getFXMLLoader(FXML_LEFT_NAVIGATION);
            borderPane.setCenter(loader.load()); //将当前BorderPane中间区域加载为机构选择界面
            MeetLeftController meetLeftController = loader.getController(); //从loader中获取MeetLeftController
            meetLeftController.setBorderPane(borderPane);//设置传参当前的borderPane，以便在MeetLeftController中获取到当前BorderPane
        } catch (IOException e) {
            logger.error(e.getCause().getMessage());
        }
    }
}
