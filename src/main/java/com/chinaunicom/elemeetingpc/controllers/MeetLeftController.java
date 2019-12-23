package com.chinaunicom.elemeetingpc.controllers;

import com.chinaunicom.elemeetingpc.constant.GlobalStaticConstant;
import com.chinaunicom.elemeetingpc.database.models.MeetInfo;
import com.chinaunicom.elemeetingpc.database.models.MeetUserRelation;
import com.chinaunicom.elemeetingpc.modelFx.MeetInfoFx;
import com.chinaunicom.elemeetingpc.service.MeetInfoService;
import com.chinaunicom.elemeetingpc.service.MeetUserRelationService;
import com.chinaunicom.elemeetingpc.utils.DialogsUtils;
import com.chinaunicom.elemeetingpc.utils.FxmlUtils;
import com.chinaunicom.elemeetingpc.utils.LoadingPage;
import com.chinaunicom.elemeetingpc.utils.MQPlugin;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 左侧会议列表控制器，使用三个自定义的ListView来展示正在召开，即将召开和历史会议等信息.
 *
 * @author zhaojunfeng, chenxi
 */
public class MeetLeftController {

    private static final Logger logger = LoggerFactory.getLogger(MeetLeftController.class);

    private static ResourceBundle bundle = FxmlUtils.getResourceBundle();

    @FXML
    private ListView<MeetInfoFx> meetCurrentListView;

    @FXML
    private ListView<MeetInfoFx> meetFutureListView;

    @FXML
    private ListView<MeetInfoFx> meetHistoryListView;

    @FXML
    private TextField textFieldUsername; //当前登录人姓名区域

    @FXML
    private VBox leftMenu; //左侧会议列表主体

    @FXML
    private VBox scrollableArea; //可滚动条区域

    private MeetInfoServiceController meetInfoServiceController;

    private MeetInfoService meetInfoService;

    private MeetUserRelationService meetUserRelationService;

    //用于存放meetCurrentListView, meetFutureListView, meetHistoryListView这3个ListView
    private List<ListView> listViewCollection;

    private BorderPane borderPaneMain;

    private MQPlugin mQPlugin;

    //会议首页面
    public static final String FXML_INDEX = "/fxml/fxml_index.fxml";

    public void initialize() {
        try {
            meetInfoServiceController = new MeetInfoServiceController();
            meetInfoService = new MeetInfoService();
            meetUserRelationService = new MeetUserRelationService();
            listViewCollection = new ArrayList<>();
            //设置Vertical ScrollPane
            initVerticalScroll();
            //当前登录人姓名区域
            textFieldUsername.setText(GlobalStaticConstant.GLOBAL_USERINFO_USERNAME);
            //设置3个ListView的样式并填充数据
            initLeftMeetingLists();
            //为3个ListView添加事件监听
            addLeftMeetingListsListener();
        } catch (Exception ex) {
            DialogsUtils.errorAlert("system.malfunction");
            logger.error(FxmlUtils.getResourceBundle().getString("error.MeetLeftController.initialize"), ex);
        }
    }

    /**
     * 设置Vertical ScrollPane属性.
     */
    public void initVerticalScroll() {
        ScrollPane scroll = new ScrollPane(scrollableArea);
        scroll.setPannable(true);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background-insets:0.0px;-fx-border-color:transparent;");
        leftMenu.getChildren().addAll(scroll);
    }

    /**
     * 设置3个ListView用来存放正在召开，即将召开和历史会议3类会议列表，填充数据并自定义每个listView中cell的样式.
     */
    public void initLeftMeetingLists() throws ApplicationException {
        List<String> parentMeetIdList = getParentMeetList(GlobalStaticConstant.GLOBAL_ORGANINFO_OWNER_USERID);
        //正在召开的会议列表
        ObservableList<MeetInfoFx> currentFxList = meetInfoService.getCurrentMeetInfoFxs(parentMeetIdList);
        meetCurrentListView.setItems(currentFxList);
        meetCurrentListView.setPrefHeight(currentFxList.size() * 80.0 + 5.0);
        meetCurrentListView.setMinHeight(currentFxList.size() * 80.0 + 5.0);
        listViewCollection.add(meetCurrentListView);
        //即将召开的会议列表
        ObservableList<MeetInfoFx> fxlist2 = meetInfoService.getFutureMeetInfoFxs(parentMeetIdList);
        meetFutureListView.setItems(fxlist2);
        meetFutureListView.setPrefHeight(fxlist2.size() * 80.0 + 5.0);
        meetFutureListView.setMinHeight(fxlist2.size() * 80.0 + 5.0);
        listViewCollection.add(meetFutureListView);
        //历史会议列表
        ObservableList<MeetInfoFx> fxlist3 = meetInfoService.getHistoryMeetInfoFxs(parentMeetIdList);
        meetHistoryListView.setItems(fxlist3);
        meetHistoryListView.setPrefHeight(fxlist3.size() * 80.0 + 5.0);
        meetHistoryListView.setMinHeight(fxlist3.size() * 80.0 + 5.0);
        listViewCollection.add(meetHistoryListView);
        //逐个循环每个ListView并重新自定义每个ListView中cell为Label
        for (int i = 0; i < listViewCollection.size(); i++) {
            listViewCollection.get(i).setCellFactory(e -> new ListCell<MeetInfoFx>() {
                {
                    setStyle("-fx-padding: 0px"); //删除cell周边的填充
                }

                @Override
                public void updateItem(MeetInfoFx item, boolean empty) {
                    try {
                        if (!empty && item != null) {
                            //自定义cell样式
                            Label labelCell = new Label(item.getMeetingName());
                            //添加tooltip
                            labelCell.setTooltip(new Tooltip(item.getMeetingName()));
                            labelCell.setPrefSize(400.0, 80.0);
                            labelCell.setPadding(new Insets(5.0, 20.0, 5.0, 10.0));
                            labelCell.setBackground(new Background(new BackgroundFill(Color.valueOf("#f0eff5"), CornerRadii.EMPTY, Insets.EMPTY)));
                            labelCell.setBorder(new Border(new BorderStroke(Color.valueOf("#f0eff5"), Color.valueOf("#f0eff5"), Color.valueOf("#dfdee4"), Color.valueOf("#f0eff5"), BorderStrokeStyle.NONE, BorderStrokeStyle.NONE, BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE, CornerRadii.EMPTY, BorderWidths.DEFAULT, Insets.EMPTY)));
                            labelCell.setFont(Font.font("Arial", 18.0));
                            labelCell.setTextFill(Color.valueOf("#000000"));
                            labelCell.setWrapText(true);
                            labelCell.setTextOverrun(OverrunStyle.ELLIPSIS);
                            labelCell.setTextAlignment(TextAlignment.LEFT);
                            labelCell.setAlignment(Pos.CENTER_LEFT);
                            labelCell.setCursor(Cursor.HAND);
                            setGraphic(labelCell);
                        }
                        super.updateItem(item, empty);
                    } catch (Exception ex) {
                        DialogsUtils.errorAlert("system.malfunction");
                        logger.error(FxmlUtils.getResourceBundle().getString("error.MeetLeftController.initLeftMeetingList.updateItem"), ex);
                    }
                }
            });
        }
    }

    /**
     * 给3个ListView添加事件监听.
     */
    public void addLeftMeetingListsListener() {
        try {
            meetCurrentListView.getSelectionModel().selectedItemProperty().addListener(
                    (ObservableValue<? extends MeetInfoFx> observable, MeetInfoFx oldValue, MeetInfoFx newValue) -> {
                        GlobalStaticConstant.GLOBAL_SELECTED_MEETID = observable.getValue().getMeetingId();
                        //创建数据加载界面
                        LoadingPage loadingPage = new LoadingPage(borderPaneMain.getScene().getWindow());
                        loadingPage.showLoadingPage();
                        final Task task = new Task<Void>() {
                    @Override
                    protected Void call() throws InterruptedException, ExecutionException, Exception {
                        //调用接口，从远程服务器上获取会议相关信息
                        meetInfoServiceController.getMeetInfosFromRemote();
                        return null;
                    }
                };
                        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                            @Override
                            public void handle(WorkerStateEvent event) {
                                //跳转界面
                                showFxmlMeet();
                                loadingPage.closeLoadingPage();
                            }
                        });
                        task.setOnFailed(new EventHandler<WorkerStateEvent>() {
                            @Override
                            public void handle(WorkerStateEvent event) {
                                //弹窗提示错误并记录到日志里面
                                DialogsUtils.errorAlert("system.malfunction");
                                loadingPage.closeLoadingPage();
                                logger.error(FxmlUtils.getResourceBundle().getString("error.MeetLeftController.addLeftMeetingListListener.Task.call"), task.getException());
                            }
                        });
                        new Thread(task).start();
                    }
            );
            meetFutureListView.getSelectionModel().selectedItemProperty().addListener(
                    (ObservableValue<? extends MeetInfoFx> observable, MeetInfoFx oldValue, MeetInfoFx newValue) -> {
                        GlobalStaticConstant.GLOBAL_SELECTED_MEETID = observable.getValue().getMeetingId();
                        //创建数据加载界面
                        LoadingPage loadingPage = new LoadingPage(borderPaneMain.getScene().getWindow());
                        loadingPage.showLoadingPage();
                        final Task task = new Task<Void>() {
                    @Override
                    protected Void call() throws InterruptedException, ExecutionException, Exception {
                        //调用接口，从远程服务器上获取会议相关信息
                        meetInfoServiceController.getMeetInfosFromRemote();
                        return null;
                    }
                };
                        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                            @Override
                            public void handle(WorkerStateEvent event) {
                                //跳转界面
                                showFxmlMeet();
                                loadingPage.closeLoadingPage();
                            }
                        });
                        task.setOnFailed(new EventHandler<WorkerStateEvent>() {
                            @Override
                            public void handle(WorkerStateEvent event) {
                                //弹窗提示错误并记录到日志里面
                                DialogsUtils.errorAlert("system.malfunction");
                                loadingPage.closeLoadingPage();
                                logger.error(FxmlUtils.getResourceBundle().getString("error.MeetLeftController.addLeftMeetingListListener.Task.call"), task.getException());
                            }
                        });
                        new Thread(task).start();
                    }
            );
            meetHistoryListView.getSelectionModel().selectedItemProperty().addListener(
                    (ObservableValue<? extends MeetInfoFx> observable, MeetInfoFx oldValue, MeetInfoFx newValue) -> {
                        GlobalStaticConstant.GLOBAL_SELECTED_MEETID = observable.getValue().getMeetingId();
                        //创建数据加载界面
                        LoadingPage loadingPage = new LoadingPage(borderPaneMain.getScene().getWindow());
                        loadingPage.showLoadingPage();
                        final Task task = new Task<Void>() {
                    @Override
                    protected Void call() throws InterruptedException, ExecutionException, Exception {
                        //调用接口，从远程服务器上获取会议相关信息
                        meetInfoServiceController.getMeetInfosFromRemote();
                        return null;
                    }
                };
                        task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                            @Override
                            public void handle(WorkerStateEvent event) {
                                //跳转界面
                                showFxmlMeet();
                                loadingPage.closeLoadingPage();
                            }
                        });
                        task.setOnFailed(new EventHandler<WorkerStateEvent>() {
                            @Override
                            public void handle(WorkerStateEvent event) {
                                //弹窗提示错误并记录到日志里面
                                DialogsUtils.errorAlert("system.malfunction");
                                loadingPage.closeLoadingPage();
                                logger.error(FxmlUtils.getResourceBundle().getString("error.MeetLeftController.addLeftMeetingListListener.Task.call"), task.getException());
                            }
                        });
                        new Thread(task).start();
                    }
            );
        } catch (Exception ex) {
            DialogsUtils.errorAlert("system.malfunction");
            logger.error(FxmlUtils.getResourceBundle().getString("error.MeetLeftController.addLeftMeetingListListener"), ex);
        }
    }

    /**
     * 退出.
     */
    @FXML
    public void handleExit() {
        try {
            if (DialogsUtils.confirmationAlert(bundle.getString("exitConfirm.header"), bundle.getString("exitConfirm.content"))) {
                if (mQPlugin != null) {
                    mQPlugin.closeConnection(); //关闭mq
                }
                Platform.exit();
            }
        } catch (Exception ex) {
            DialogsUtils.errorAlert("system.malfunction");
            logger.error(FxmlUtils.getResourceBundle().getString("error.MeetLeftController.handleExit"), ex);
        }
    }

    /**
     * 跳转会议界面.
     */
    public void showFxmlMeet() {
        try {
            FXMLLoader loader = FxmlUtils.getFXMLLoader(FXML_INDEX);
            borderPaneMain.getChildren().remove(borderPaneMain.getCenter());//清除当前BorderPane内中间区域的内容
            borderPaneMain.getChildren().remove(borderPaneMain.getLeft());//清除当前BorderPane内左侧区域的内容
            borderPaneMain.setCenter(loader.load()); //将当前BorderPane中间区域加载为机构选择界面
            MeetInfoController meetController = loader.getController(); //从loader中获取MeetController
            meetController.setBorderPane(borderPaneMain);//设置传参当前的borderPane，以便在MeetController中获取到当前BorderPane
            meetController.setMQPlugin(mQPlugin);//把MQPlugin往下传
        } catch (Exception ex) {
            DialogsUtils.errorAlert("system.malfunction");
            logger.error(FxmlUtils.getResourceBundle().getString("error.MeetLeftController.showFxmlMeet"), ex);
        }
    }

    /**
     * 根据用户id获取其所在的父会议id列表.
     *
     * @param userId
     * @return parentMeetIdList 父会议id列表
     * @throws ApplicationException
     */
    public List<String> getParentMeetList(String userId) throws ApplicationException {
        List<String> parentMeetIdList = new ArrayList<>();
        List<MeetUserRelation> meetUserRelationList = new ArrayList<>();
        //根据用户id在会议用户关系表里查询其对应的所有子会议
        meetUserRelationList = meetUserRelationService.queryMeetUserRelationByUserId(userId);
        for (MeetUserRelation meetUserRelation : meetUserRelationList) {
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
     */
    public String getParentMeetIdByChildMeetId(String childMeetId) throws ApplicationException {
        String parentMeetId = "";
        List<MeetInfo> meetInfoList = meetInfoService.queryMeetInfoByChildMeetId(childMeetId);
        if (!meetInfoList.isEmpty()) {
            parentMeetId = meetInfoList.get(0).getParentMeetingId();
        }
        return parentMeetId;
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
