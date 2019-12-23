package com.chinaunicom.elemeetingpc.controllers;

import com.chinaunicom.elemeetingpc.constant.GlobalStaticConstant;
import com.chinaunicom.elemeetingpc.modelFx.OrganInfoFx;
import com.chinaunicom.elemeetingpc.service.OrganInfoService;
import com.chinaunicom.elemeetingpc.utils.DialogsUtils;
import com.chinaunicom.elemeetingpc.utils.FxmlUtils;
import com.chinaunicom.elemeetingpc.utils.LoadingPage;
import com.chinaunicom.elemeetingpc.utils.MQPlugin;
import java.util.concurrent.ExecutionException;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 组织机构控制器，在界面的列表上加载登陆人所属机构名称，选择机构后进入会议主界面.
 *
 * @author chenxi 创建时间：2019-6-27 14:20:17
 */
public class OrganInfoController {

    private static final Logger logger = LoggerFactory.getLogger(OrganInfoController.class);

    //会议首界面
    public static final String FXML_INDEX = "/fxml/fxml_index.fxml";

    @FXML
    private ListView<OrganInfoFx> organListView;

    private OrganInfoService organInfoService;

    private BorderPane borderPaneMain;

    private MQPlugin mQPlugin;

    /**
     * 初始化组织机构列表，加载数据、添加事件监听、使用Task实现读取界面加载等相关业务逻辑.
     *
     * @author chenxi 创建时间：2019-6-27 14:20:17
     */
    public void initialize() {
        try {
            organInfoService = new OrganInfoService();
            organInfoService.init();
            int listSize = organInfoService.getOrganInfoNameObservableList().size();
            organListView.setItems(organInfoService.getOrganInfoNameObservableList());
            //ListView的高=list的size X 每条list-cell的size + 4
            organListView.setFixedCellSize(40.0);
            organListView.setPrefHeight(listSize * 40.0 + 4.0);
            //添加事件监听器
            organListView.getSelectionModel().selectedItemProperty().addListener ((ObservableValue<? extends OrganInfoFx> observable, OrganInfoFx oldValue, OrganInfoFx newValue) -> {
                //在全局常量里记录当前选择的机构id，机构名称和用户id
                GlobalStaticConstant.GLOBAL_ORGANINFO_ORGANIZATIONID = observable.getValue().getOrganizationId();
                GlobalStaticConstant.GLOBAL_ORGANINFO_ORGANIZATIONNAME = observable.getValue().getOrganizationName();
                GlobalStaticConstant.GLOBAL_ORGANINFO_OWNER_USERID = observable.getValue().getUserId();
                //创建数据加载界面
                LoadingPage loadingPage = new LoadingPage(borderPaneMain.getScene().getWindow());
                loadingPage.showLoadingPage();
                final Task task = new Task<Void>() {
                    @Override
                    protected Void call() throws InterruptedException, ExecutionException, Exception {
                        OrganInfoServiceController organInfoServiceController = new OrganInfoServiceController();
                        //调接口解析数据
                        organInfoServiceController.initialize();
                        return null;
                    }
                };
                task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent event) {
                        //如果mq连接不存在，先创建mq连接
                        if (mQPlugin == null) {
                            mQPlugin = new MQPlugin();
                            //添加窗口关闭事件监听器，因为此时mq已经创建，需要在关闭程序前关闭mq线程
                            handleWindowCloseEvent(mQPlugin);
                        }
                        //加载跳转界面
                        showFxmlIndex(mQPlugin);
                        loadingPage.closeLoadingPage();
                    }
                });
                task.setOnFailed(new EventHandler<WorkerStateEvent>() {
                    @Override
                    public void handle(WorkerStateEvent event) {
                        //弹窗提示错误并记录到日志里面
                        DialogsUtils.errorAlert("system.malfunction");
                        loadingPage.closeLoadingPage();
                        logger.error(FxmlUtils.getResourceBundle().getString("error.OrganInfoController.initialize.Task.call"), task.getException());
                    }
                });
                new Thread(task).start();
            });
        } catch (Exception ex) {
            DialogsUtils.errorAlert("system.malfunction");
            logger.error(FxmlUtils.getResourceBundle().getString("error.OrganInfoController.initialize"), ex);
        }
    }

    /**
     * 跳转会议首界面.
     * 
     * @param mQPlugin a mQPlugin object
     */
    public void showFxmlIndex(MQPlugin mQPlugin) {
        try {
            FXMLLoader loader = FxmlUtils.getFXMLLoader(FXML_INDEX);
            borderPaneMain.getChildren().remove(borderPaneMain.getCenter());//清除当前BorderPane内中间区域的内容
            borderPaneMain.setCenter(loader.load()); //将当前BorderPane中间区域加载为会议首界面
            MeetInfoController meetInfoController = loader.getController(); //从loader中获取meetInfoController
            meetInfoController.setBorderPane(borderPaneMain);//把borderPane设置为参数继续往下传，以便在meetInfoController中获取到当前BorderPane
            meetInfoController.setMQPlugin(mQPlugin);//把MQPlugin往下传
        } catch (Exception ex) {
            DialogsUtils.errorAlert("system.malfunction");
            logger.error(FxmlUtils.getResourceBundle().getString("error.OrganInfoController.showFxmlIndex"), ex);
        }
    }

    /**
     * Windows 窗口关闭监听器，用于监听窗口右上角小红叉关闭按钮，之所以在此添加监听是因为此时mq对象已经创建，必须在程序
     * 退出前正确关闭mq线程，否则会造因mq线程没有正确关闭而导致程序hang.
     * 
     * @param mQPlugin a mQPlugin object
     */
    public void handleWindowCloseEvent(MQPlugin mQPlugin) {
        try {
            Stage stage = (Stage) borderPaneMain.getScene().getWindow();
            if (stage != null) {
                //monitor stage close event, close RabbitMQ connection
                stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                    @Override
                    public void handle(WindowEvent event) {
                        try {
                            if (mQPlugin != null) {
                                mQPlugin.closeConnection(); //close mq gracefully
                            }
                        } catch (Exception ex) {
                            logger.error(FxmlUtils.getResourceBundle().getString("error.OrganInfoController.handleWindowCloseEvent"), ex);
                        } finally {
                            Platform.exit();
                        }
                    }
                });
            }
        } catch (Exception ex) {
            logger.error(FxmlUtils.getResourceBundle().getString("error.OrganInfoController.handleWindowCloseEvent"), ex);
        }
    }

    public void setBorderPane(BorderPane borderPaneMain) {
        this.borderPaneMain = borderPaneMain;
    }

    public void setMQPlugin(MQPlugin mQPlugin) {
        if (mQPlugin != null) {
            this.mQPlugin = mQPlugin;
        }
    }
}
