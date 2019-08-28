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
 * 组织机构控制器，在界面的列表上加载登陆人所属机构名称，选择机构后进入会议 主界面.
 *
 * @author chenxi 创建时间：2019-6-27 14:20:17
 */
public class OrganInfoController {

    @FXML
    private ListView<OrganInfoFx> organListView;

    private OrganInfoModel organInfoModel;

    private static final Logger logger = LoggerFactory.getLogger(OrganInfoController.class);

    //会议首界面
    public static final String FXML_INDEX = "/fxml/fxml_index.fxml";

    private BorderPane borderPaneMain;

    //初始化
    public void initialize() {
        this.organInfoModel = new OrganInfoModel();
        try {
            organInfoModel.init();
        } catch (ApplicationException e) {
            DialogsUtils.errorAlert(e.getMessage());
        }
        int listSize = this.organInfoModel.getOrganInfoNameObservableList().size();
        organListView.setItems(this.organInfoModel.getOrganInfoNameObservableList());

        //ListView的高=list的size X 每条list-cell的size + 5(padding)
        organListView.setFixedCellSize(40.0);
        organListView.setPrefHeight(listSize * 40.0 + 5.0);
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
                    showFxmlIndex();
                });
    }

    /**
     * 跳转会议首界面
     */
    public void showFxmlIndex() {
        try {
            FXMLLoader loader = FxmlUtils.getFXMLLoader(FXML_INDEX);
            borderPaneMain.getChildren().remove(borderPaneMain.getCenter());//清除当前BorderPane内中间区域的内容
            borderPaneMain.setCenter(loader.load()); //将当前BorderPane中间区域加载为会议首界面
            MeetController meetController = loader.getController(); //从loader中获取MeetController
            meetController.setBorderPane(borderPaneMain);//把borderPane设置为参数继续往下传，以便在MeetController中获取到当前BorderPane
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getCause().getMessage());
        }
    }

    public void setBorderPane(BorderPane borderPaneMain) {
        this.borderPaneMain = borderPaneMain;
    }
}
