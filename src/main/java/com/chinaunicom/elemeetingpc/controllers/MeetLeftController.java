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
import com.chinaunicom.elemeetingpc.utils.LoadingPage;
import com.chinaunicom.elemeetingpc.utils.MQPlugin;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
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

/**
 * 会议左侧列表controller
 *
 * @author zhaojunfeng, chenxi
 */
public class MeetLeftController {

    private static final Logger logger = LoggerFactory.getLogger(MeetLeftController.class);

    @FXML
    private ListView<MeetInfoFx> meetCurrentListView;

    @FXML
    private ListView<MeetInfoFx> meetFutureListView;

    @FXML
    private ListView<MeetInfoFx> meetHistoryListView;

    @FXML
    private TextField textFieldUsername;

    @FXML
    private VBox leftMenu;

    @FXML
    private VBox scrollableArea; //滚动条区域

    private MeetInfoModel meetInfoModel;

    private MeetUserRelationModel meetUserRelationModel;

    private BorderPane borderPaneMain;
    
    private MQPlugin mQPlugin;

    //会议界面中部数据
    public static final String FXML_INDEX = "/fxml/fxml_index.fxml";

    public void initialize() {
        List<ListView> listViewCollection = new ArrayList<>();//用于存放3个ListView
        //设置Vertical ScrollPane
        ScrollPane scroll = new ScrollPane(scrollableArea);
        scroll.setPannable(true);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background-insets:0.0px;-fx-border-color:transparent;");
        leftMenu.getChildren().addAll(scroll);

        textFieldUsername.setText(GlobalStaticConstant.GLOBAL_USERINFO_USERNAME);
        meetInfoModel = new MeetInfoModel();
        MeetService meetService = new MeetService();
        try {
            List<String> parentMeetIdList = this.getParentMeetList(GlobalStaticConstant.GLOBAL_ORGANINFO_OWNER_USERID);
            //设置3个ListView
            ObservableList<MeetInfoFx> fxlist1 = meetInfoModel.getCurrentMeetInfoFxs(parentMeetIdList);
            meetCurrentListView.setItems(fxlist1);
            listViewCollection.add(meetCurrentListView);
            meetCurrentListView.setPrefHeight(fxlist1.size() * 80.0 + 5.0);
            meetCurrentListView.setMinHeight(fxlist1.size() * 80.0 + 5.0);

            ObservableList<MeetInfoFx> fxlist2 = meetInfoModel.getFutureMeetInfoFxs(parentMeetIdList);
            meetFutureListView.setItems(fxlist2);
            listViewCollection.add(meetFutureListView);
            meetFutureListView.setPrefHeight(fxlist2.size() * 80.0 + 5.0);
            meetFutureListView.setMinHeight(fxlist2.size() * 80.0 + 5.0);

            ObservableList<MeetInfoFx> fxlist3 = meetInfoModel.getHistoryMeetInfoFxs(parentMeetIdList);
            meetHistoryListView.setItems(fxlist3);
            listViewCollection.add(meetHistoryListView);
            meetHistoryListView.setPrefHeight(fxlist3.size() * 80.0 + 5.0);
            meetHistoryListView.setMinHeight(fxlist3.size() * 80.0 + 5.0);

            //循环并逐个重新自定义每个ListView中cell为Label
            for (int i = 0; i < listViewCollection.size(); i++) {
                listViewCollection.get(i).setCellFactory(e -> new ListCell<MeetInfoFx>() {
                    {
                        setStyle("-fx-padding: 0px"); //删除cell周边的填充
                    }

                    @Override
                    public void updateItem(MeetInfoFx item, boolean empty) {
                        if (!empty && item != null) {
                            //自定义cell样式
                            Label labelCell = new Label(item.getMeetingName());
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
                    }
                });
            }
            //为3个ListView添加监听事件
            meetCurrentListView.getSelectionModel().selectedItemProperty().addListener(
                    (ObservableValue<? extends MeetInfoFx> observable, MeetInfoFx oldValue, MeetInfoFx newValue) -> {
                        GlobalStaticConstant.GLOBAL_SELECTED_MEETID = observable.getValue().getMeetingId();
                        //创建数据加载界面
                        LoadingPage loadingPage = new LoadingPage(borderPaneMain.getScene().getWindow());
                        loadingPage.showLoadingPage();
                        final Task task = new Task<Void>() {
                            @Override
                            protected Void call() throws InterruptedException, ExecutionException {
                                //调用接口，从远程服务器上获取会议相关信息
                                meetService.getMeetInfosFromRemote();
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
                            protected Void call() throws InterruptedException, ExecutionException {
                                //调用接口，从远程服务器上获取会议相关信息
                                meetService.getMeetInfosFromRemote();
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
                            protected Void call() throws InterruptedException, ExecutionException {
                                //调用接口，从远程服务器上获取会议相关信息
                                meetService.getMeetInfosFromRemote();
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
                        new Thread(task).start();
                    }
            );
        } catch (Exception ex) {
            logger.error(ex.getCause().getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * 退出
     */
    @FXML
    public void handExit() throws IOException, TimeoutException {
        boolean result = false;
        if (result = DialogsUtils.confirmationAlert()) {
            mQPlugin.closeConnection(); //关闭mq
            Platform.exit();
        }
    }

    /**
     * 跳转会议界面
     */
    public void showFxmlMeet() {
        try {
            FXMLLoader loader = FxmlUtils.getFXMLLoader(FXML_INDEX);
            borderPaneMain.getChildren().remove(borderPaneMain.getCenter());//清除当前BorderPane内中间区域的内容
            borderPaneMain.getChildren().remove(borderPaneMain.getLeft());//清除当前BorderPane内左侧区域的内容
            borderPaneMain.setCenter(loader.load()); //将当前BorderPane中间区域加载为机构选择界面
            MeetController meetController = loader.getController(); //从loader中获取MeetController
            meetController.setBorderPane(borderPaneMain);//设置传参当前的borderPane，以便在MeetController中获取到当前BorderPane
        } catch (IOException e) {
            logger.error(e.getCause().getMessage());
        }
    }

    /**
     * 根据用户id获取其所在的父会议id列表.
     *
     * @param userId
     * @return parentMeetIdList 父会议id列表
     * @throws ApplicationException
     * @throws java.sql.SQLException
     */
    public List<String> getParentMeetList(String userId) throws ApplicationException, SQLException {
        List<String> parentMeetIdList = new ArrayList<>();
        this.meetUserRelationModel = new MeetUserRelationModel();
        List<MeetUserRelation> meetUserRelationList = new ArrayList<>();
        //根据用户id在会议用户关系表里查询其对应的所有子会议
        meetUserRelationList = meetUserRelationModel.queryMeetUserRelationByUserId(userId);
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
     * @throws java.sql.SQLException
     */
    public String getParentMeetIdByChildMeetId(String childMeetId) throws ApplicationException, SQLException {
        String parentMeetId = "";
        this.meetInfoModel = new MeetInfoModel();
        List<MeetInfo> meetInfoList = meetInfoModel.queryMeetInfoByChildMeetId(childMeetId);
        if (!meetInfoList.isEmpty()) {
            parentMeetId = meetInfoList.get(0).getParentMeetingId();
        }
        return parentMeetId;
    }
    
    public void setBorderPane(BorderPane borderPaneMain) {
        this.borderPaneMain = borderPaneMain;
    }
    
    public void setMQPlugin(MQPlugin mQPlugin) {
        this.mQPlugin = mQPlugin;
    }
}
