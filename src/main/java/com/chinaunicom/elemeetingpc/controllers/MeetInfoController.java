package com.chinaunicom.elemeetingpc.controllers;

import static com.chinaunicom.elemeetingpc.MainApp.STYLES;
import com.chinaunicom.elemeetingpc.constant.GlobalStaticConstant;
import com.chinaunicom.elemeetingpc.database.models.IssueInfo;
import com.chinaunicom.elemeetingpc.database.models.MeetInfo;
import com.chinaunicom.elemeetingpc.database.models.MeetIssueRelation;
import com.chinaunicom.elemeetingpc.database.models.MeetUserRelation;
import com.chinaunicom.elemeetingpc.service.IssueInfoService;
import com.chinaunicom.elemeetingpc.service.MeetInfoService;
import com.chinaunicom.elemeetingpc.service.MeetIssueRelationService;
import com.chinaunicom.elemeetingpc.service.MeetUserRelationService;
import com.chinaunicom.elemeetingpc.utils.DialogsUtils;
import com.chinaunicom.elemeetingpc.utils.FxmlUtils;
import com.chinaunicom.elemeetingpc.utils.MQPlugin;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 会议主界面控制器，展示会议，议题等信息.
 *
 * @author zhaojunfeng, chenxi
 */
public class MeetInfoController {

    private static final Logger logger = LoggerFactory.getLogger(MeetInfoController.class);

    @FXML
    private VBox subMeetingSection;

    @FXML
    private VBox mainIndex;

    @FXML
    private Label indexMeetTitle;

    //左侧会议列表界面
    public static final String FXML_LEFT_NAVIGATION = "/fxml/fxml_left_navigation.fxml";

    //机构选择界面
    public static final String FXML_ORG_FXML = "/fxml/fxml_org.fxml";

    //文件列表界面
    public static final String FXML_FILE = "/fxml/fxml_file.fxml";

    //修改密码界面
    public static final String FXML_RESET_PASSWORD = "/fxml/fxml_resetPassword.fxml";

    //通知列表界面
    public static final String FXML_NOTICE_LIST = "/fxml/fxml_notice_list.fxml";

    //左侧会议列表默认收起状态
    private boolean isFolded = true;

    private BorderPane borderPaneMain;

    private MQPlugin mQPlugin;

    private MeetInfoService meetInfoService;

    private MeetIssueRelationService meetIssueRelationService;

    private IssueInfoService issueInfoService;

    private MeetUserRelationService meetUserRelationService;

    public MeetInfoController() {

        meetInfoService = new MeetInfoService();

        meetIssueRelationService = new MeetIssueRelationService();

        issueInfoService = new IssueInfoService();
        
        meetUserRelationService = new MeetUserRelationService();
    }

    /**
     * 会议首页面布局初始化和数据载入.
     *
     */
    public void initialize() {
        //子会议列表
        List<MeetInfo> childMeetInfoList = new ArrayList<>();
        //子会议id列表
        List<String> childMeetInfoIdList = new ArrayList<>();
        //子会议与议题关系列表
        List<MeetIssueRelation> meetIssueRelationList = new ArrayList<>();
        //议题列表
        List<IssueInfo> issueInfoList = new ArrayList<>();
        //议题id列表
        List<String> issueInfoIdList = new ArrayList<>();
        //存放界面UI控件的列表
        List<Node> uiControlsList = new ArrayList<>();
        try {
            //获取默认会议的父会议id
            String parentMeetId = getDefaultMeetingId();
            GlobalStaticConstant.GLOBAL_SELECTED_MEETID = parentMeetId;
            //父会议名称区域
            createParentMeetingTitle(parentMeetId);
            //获取当前登录用户所属子会议信息
            childMeetInfoIdList = getChildMeetList(GlobalStaticConstant.GLOBAL_ORGANINFO_OWNER_USERID);
            childMeetInfoList = meetInfoService.queryChildMeetInfosByIds(parentMeetId, childMeetInfoIdList);
            if (!childMeetInfoList.isEmpty()) {
                for (int i = 0; i < childMeetInfoList.size(); i++) {
                    //子会议名称区域
                    createSubMeetingTitle(childMeetInfoList.get(i).getMeetingName(), uiControlsList);
                    //在MeetIssueRelation表里根据子会议id获取对应的会议和议题关系
                    meetIssueRelationList = meetIssueRelationService.queryMeetIssueRelation(childMeetInfoList.get(i).getMeetingId());
                    if (!meetIssueRelationList.isEmpty()) {
                        //获取所有议题ids
                        for (int j = 0; j < meetIssueRelationList.size(); j++) {
                            issueInfoIdList.add(meetIssueRelationList.get(j).getIssueId());
                        }
                        //根据议题ids获取议题信息
                        issueInfoList = issueInfoService.queryIssueByIds(issueInfoIdList);
                        issueInfoIdList.clear();
                        int issueSize = issueInfoList.size(); //议题个数
                        //议题区域
                        createMeetingIssues(issueSize, issueInfoList, uiControlsList);
                    }
                }
            }
            subMeetingSection.getChildren().addAll(uiControlsList);
            //设置Vertical ScrollPane
            ScrollPane scroll = new ScrollPane(subMeetingSection);
            scroll.setPannable(true);
            scroll.setFitToWidth(true);
            scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
            scroll.setStyle("-fx-background-insets:0.0px;-fx-border-color:transparent;");
            mainIndex.getChildren().addAll(scroll);
        } catch (Exception ex) {
            DialogsUtils.errorAlert("system.malfunction");
            logger.error(FxmlUtils.getResourceBundle().getString("error.MeetInfoController.initialize"), ex);
        }
    }

    /**
     * 获取默认父会议id，存在两种情况：1、未选择左侧会议列表中的会议，则获取默认父会议id的顺序优先级为当前会议 > 即将召开会议 > 历史会议
     * 2、已选择左侧会议列表中的会议，则从全局常量中的GLOBAL_SELECTED_MEETID获取默认父会议id.
     *
     * @return parentMeetingId 父会议id
     */
    public String getDefaultMeetingId() throws ApplicationException {
        String parentMeetingId = GlobalStaticConstant.GLOBAL_SELECTED_MEETID;
        if (StringUtils.isBlank(parentMeetingId)) {
            List<String> parentMeetIdList = new ArrayList<>();
            parentMeetIdList = getParentMeetList(GlobalStaticConstant.GLOBAL_ORGANINFO_OWNER_USERID);
            if (!parentMeetIdList.isEmpty()) {
                List<MeetInfo> currentMeetList = meetInfoService.getCurrentMeetInfo(parentMeetIdList);
                List<MeetInfo> futureMeetList = meetInfoService.getFutureMeetInfo(parentMeetIdList);
                List<MeetInfo> historyMeetList = meetInfoService.getHistoryMeetInfo(parentMeetIdList);
                if (!currentMeetList.isEmpty()) {
                    parentMeetingId = currentMeetList.get(0).getMeetingId();
                } else if (!futureMeetList.isEmpty()) {
                    parentMeetingId = futureMeetList.get(0).getMeetingId();
                } else if (!historyMeetList.isEmpty()) {
                    parentMeetingId = historyMeetList.get(0).getMeetingId();
                }
            }
        }
        return parentMeetingId;
    }

    /**
     * 根据用户id获取其所在的父会议id列表.
     *
     * @param userId 不为空
     * @return parentMeetIdList 父会议id列表
     * @throws ApplicationException
     */
    public List<String> getParentMeetList(String userId) throws ApplicationException {
        List<String> parentMeetIdList = new ArrayList<>();
        List<MeetUserRelation> meetUserRelationList = new ArrayList<>();
        //根据用户id在会议用户关系表里查询其对应的所有子会议
        meetUserRelationList = meetUserRelationService.queryMeetUserRelationByUserId(userId);
        if (!meetUserRelationList.isEmpty()) {
            for (MeetUserRelation meetUserRelation : meetUserRelationList) {
                //根据子会议获取其父会议
                String parentMeetId = getParentMeetIdByChildMeetId(meetUserRelation.getMeetingId());
                if (StringUtils.isNotBlank(parentMeetId)) {
                    parentMeetIdList.add(parentMeetId);
                }
            }
            //去重
            parentMeetIdList = parentMeetIdList.stream().distinct().collect(Collectors.toList());
        }
        return parentMeetIdList;
    }

    /**
     * 根据子会议id获取其父会议id.
     *
     * @param childMeetId not null
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

    /**
     * 根据用户id获取其所在的子会议id列表.
     *
     * @param userId not null
     * @return childMeetInfoIdList 子会议id列表
     * @throws ApplicationException
     */
    public List<String> getChildMeetList(String userId) throws ApplicationException {
        List<String> childMeetInfoIdList = new ArrayList<>();
        List<MeetUserRelation> meetUserRelationList = new ArrayList<>();
        //根据用户id在会议用户关系表里查询其对应的所有子会议
        meetUserRelationList = meetUserRelationService.queryMeetUserRelationByUserId(userId);
        if (!meetUserRelationList.isEmpty()) {
            for (MeetUserRelation meetUserRelation : meetUserRelationList) {
                childMeetInfoIdList.add(meetUserRelation.getMeetingId());
            }
        }
        return childMeetInfoIdList;
    }

    /**
     * 创建父会议标题.
     *
     * @param parentMeetId
     * @throws ApplicationException
     */
    public void createParentMeetingTitle(String parentMeetId) throws ApplicationException {
        //获取父会议名称
        List<MeetInfo> parentMeetList = meetInfoService.queryMeetInfoByParentMeetId(parentMeetId);
        if (!parentMeetList.isEmpty()) {
            //父会议名称区域赋值
            indexMeetTitle.setText(parentMeetList.get(0).getMeetingName());
            indexMeetTitle.setTextAlignment(TextAlignment.CENTER);
            indexMeetTitle.setTextOverrun(OverrunStyle.ELLIPSIS);
            indexMeetTitle.setWrapText(true);
            indexMeetTitle.setPadding(new Insets(5, 15, 5, 15));
            indexMeetTitle.setAlignment(Pos.CENTER);
        }
    }

    /**
     * 创建子会议标题, HBox包裹Label解决宽屏自适应问题.
     *
     * @param meetingName not null
     * @param uiControlsList UI容器list
     */
    public void createSubMeetingTitle(String meetingName, List<Node> uiControlsList) {
        Label childMeetingName = new Label(meetingName);
        childMeetingName.setAlignment(Pos.CENTER);
        childMeetingName.setTextAlignment(TextAlignment.CENTER);
        childMeetingName.setTextOverrun(OverrunStyle.ELLIPSIS);
        childMeetingName.setWrapText(true);
        childMeetingName.setPadding(new Insets(5, 15, 5, 15));
        childMeetingName.setPrefSize(1366.0, 60);
        childMeetingName.setMaxWidth(Double.MAX_VALUE);//label+HBox解决屏幕宽度自适应
        childMeetingName.setStyle("-fx-font-size-family:Arial;-fx-font-size:20.0px;-fx-text-fill:#ffffff;-fx-background-color:#4581bf;");
        //Label+HBox实现屏幕宽度自适应
        HBox childMeetingNameWrapper = new HBox(childMeetingName);
        childMeetingNameWrapper.setMaxWidth(Double.MAX_VALUE);
        childMeetingNameWrapper.setPrefSize(1366.0, 60.0);
        HBox.setHgrow(childMeetingName, Priority.ALWAYS);
        HBox.setMargin(childMeetingName, new Insets(10.0, 0.0, 10.0, 0.0));//上下预留间距，样式好看；）
        uiControlsList.add(childMeetingNameWrapper);
    }

    /**
     * 创建议题并添加事件处理，从低层到顶层所用的UI控件分别是：FlowPane -> StackPane -> ImageView -> Label.
     *
     * @param issueSize
     * @param issueInfoList
     * @param uiControlsList
     */
    public void createMeetingIssues(int issueSize, List<IssueInfo> issueInfoList, List<Node> uiControlsList) {
        //设置FlowPane样式
        FlowPane issueFlowPane = new FlowPane(Orientation.HORIZONTAL, 10.0, 10.0);
        issueFlowPane.setPadding(new Insets(5.0, 3.0, 5.0, 3.0));
        //childMeetingFlowPane.setPrefWrapLength(1300);//内容换行使用默认值
        issueFlowPane.setStyle("-fx-background-color:#ffffff;");
        //议题区域（单个议题实现方式：StackPane + ImageView + Label）
        Image icon = new Image(getClass().getResourceAsStream("/images/icon-book.jpg"));
        ImageView[] imageViews = new ImageView[issueSize];
        for (int k = 0; k < issueSize; k++) {
            IssueInfo issueInfo = issueInfoList.get(k);
            imageViews[k] = new ImageView(icon);
            //设置Label样式
            Label issueNameLabel = new Label(issueInfoList.get(k).getIssueName());
            //添加tooltip
            issueNameLabel.setTooltip(new Tooltip(issueInfoList.get(k).getIssueName()));
            issueNameLabel.setStyle("-fx-font-size:16.0px;-fx-font-size-family:Arial;-fx-cursor:hand;");
            issueNameLabel.setPrefSize(161, 185);
            issueNameLabel.setWrapText(true);
            issueNameLabel.setTextOverrun(OverrunStyle.ELLIPSIS);
            issueNameLabel.setPadding(new Insets(15, 20, 35, 25)); //通过内间距padding控制文字内容的位置
            //issueLabel.setLineSpacing(1.0);
            //设置StackPane样式
            StackPane issueStackPane = new StackPane();
            issueStackPane.setPrefSize(161.0, 185.0);
            issueStackPane.getChildren().addAll(imageViews[k], issueNameLabel);
            issueStackPane.setAlignment(Pos.TOP_LEFT);
            //StackPane.setMargin(issueName, new Insets(15, 25, 50, 25));//或通过外间距margin控制文字内容的位置
            issueStackPane.setUserData(issueInfo);//议题信息关联对应的StackPane
            addMouseClickEvent(issueStackPane);//给issueStackPane添加鼠标点击事件
            issueFlowPane.getChildren().addAll(issueStackPane);
        }
        uiControlsList.add(issueFlowPane);
    }

    /**
     * StackPane点击事件，跳转到文件列表界面并传入对应的议题信息.
     *
     * @param issueStackPane 代表议题
     */
    public void addMouseClickEvent(StackPane issueStackPane) {
        issueStackPane.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(final MouseEvent event) {
                try {
                    showFxmlFileList((IssueInfo) issueStackPane.getUserData());//跳转到文件列表界面，从StackPane获取到对应的议题信息
                } catch (Exception ex) {
                    DialogsUtils.errorAlert("system.malfunction");
                    logger.error(FxmlUtils.getResourceBundle().getString("error.MeetInfoController.addMouseClickEvent"), ex);
                }
            }
        });
    }

    /**
     * 跳转文件列表界面并传入对应的议题信息.
     *
     * @param issueInfo 议题信息
     * @throws ApplicationException
     */
    @FXML
    public void showFxmlFileList(IssueInfo issueInfo) throws ApplicationException, Exception {
        FXMLLoader loader = FxmlUtils.getFXMLLoader(FXML_FILE);
        borderPaneMain.getChildren().remove(borderPaneMain.getCenter());//清除当前BorderPane内中间区域的内容
        borderPaneMain.getChildren().remove(borderPaneMain.getLeft());//清除当前BorderPane内左侧区域的内容
        borderPaneMain.setCenter(loader.load()); //将当前BorderPane中间区域加载为文件列表界面
        FileController fileController = loader.getController(); //从loader中获取FileController
        fileController.setBorderPane(borderPaneMain);//设置传参当前的borderPaneMain，以便在FileController中使用
        fileController.initData(issueInfo);//把当前选择的议题传到FileController里，以便在下个控制器中使用，注意这里调用的是重写过的初始化方法
        fileController.setMQPlugin(mQPlugin);//把MQPlugin往下传
    }

    /**
     * 展开/收起左侧会议列表界面，默认isFolded为收起状态.
     */
    @FXML
    public void showFxmlLeftNavigation() {
        try {
            if (isFolded) {
                FXMLLoader loader = FxmlUtils.getFXMLLoader(FXML_LEFT_NAVIGATION);
                borderPaneMain.setLeft(loader.load()); //将当前BorderPane左侧区域加载为会议列表界面
                borderPaneMain.setCenter(borderPaneMain.getCenter());//重新加载中间区域
                MeetLeftController meetLeftController = loader.getController(); //从loader中获取MeetLeftController
                meetLeftController.setBorderPane(borderPaneMain);//设置传参当前的borderPane，以便在MeetLeftController中获取到当前BorderPane
                meetLeftController.setMQPlugin(mQPlugin);//把MQPlugin往下传
                isFolded = false;//设置为展开状态
            } else {
                borderPaneMain.getChildren().remove(borderPaneMain.getLeft());//清除当前BorderPane内左侧区域的内容
                isFolded = true;//设置为收起状态
            }
        } catch (Exception ex) {
            DialogsUtils.errorAlert("system.malfunction");
            logger.error(FxmlUtils.getResourceBundle().getString("error.MeetInfoController.showFxmlLeftNavigation"), ex);
        }
    }

    /**
     * 跳转机构选择界面.
     */
    @FXML
    public void showFxmlOrg() {
        try {
            FXMLLoader loader = FxmlUtils.getFXMLLoader(FXML_ORG_FXML);
            borderPaneMain.getChildren().remove(borderPaneMain.getCenter());//清除当前BorderPane内中间区域的内容
            borderPaneMain.getChildren().remove(borderPaneMain.getLeft());//清除当前BorderPane内左侧区域的内容
            borderPaneMain.setCenter(loader.load()); //将当前BorderPane中间区域加载为机构选择界面
            OrganInfoController organInfoController = loader.getController(); //从loader中获取OrganInfoController
            organInfoController.setBorderPane(borderPaneMain);//设置传参当前的borderPaneMain，以便在OrganInfoController中获取到当前BorderPane
            organInfoController.setMQPlugin(mQPlugin);//把mq回传给上个界面
        } catch (Exception ex) {
            DialogsUtils.errorAlert("system.malfunction");
            logger.error(FxmlUtils.getResourceBundle().getString("error.MeetInfoController.showFxmlOrg"), ex);
        }
    }

    /**
     * 跳转到密码修改界面，创建一个新的Modal window.
     */
    @FXML
    public void handleResetPassword() {
        try {
            FXMLLoader loader = FxmlUtils.getFXMLLoader(FXML_RESET_PASSWORD);
            AnchorPane resetPasswordDialog = loader.load();
            //创建一个弹窗
            Stage dialogStage = new Stage();
            dialogStage.setTitle(FxmlUtils.getResourceBundle().getString("MeetController.resetPassword"));
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(borderPaneMain.getScene().getWindow());
            //or 直接一行就够了dialogStage.initModality(Modality.APPLICATION_MODAL);
            Scene scene = new Scene(resetPasswordDialog);
            scene.getStylesheets().add(STYLES);
            dialogStage.setScene(scene);
            ResetPasswordController controller = loader.getController();
            controller.setDialogStage(dialogStage);//把界面传递到下一个控制器里
            dialogStage.showAndWait();
        } catch (Exception ex) {
            DialogsUtils.errorAlert("system.malfunction");
            logger.error(FxmlUtils.getResourceBundle().getString("error.MeetInfoController.handleResetPassword"), ex);
        }
    }

    /**
     * 通知列表信息.
     */
    @FXML
    public void handNoticeList() {
        try {
            //加载通知列表内容
            FXMLLoader loader = FxmlUtils.getFXMLLoader(FXML_NOTICE_LIST);
            AnchorPane noticeListContent = new AnchorPane();
            noticeListContent = loader.load();
            Scene scene = new Scene(noticeListContent);
            //创建弹窗
            Stage noticeListDialogStage = new Stage();
            noticeListDialogStage.setWidth(1300.0);
            noticeListDialogStage.setHeight(700.0);
            noticeListDialogStage.setTitle(FxmlUtils.getResourceBundle().getString("MeetController.noticeTitle"));
            noticeListDialogStage.initModality(Modality.WINDOW_MODAL);
            noticeListDialogStage.initOwner(borderPaneMain.getScene().getWindow());
            noticeListDialogStage.setScene(scene);
            //把当前stage作为下个界面的父界面使用（通知详情）
            NoticeInfoController controller = loader.getController();
            controller.setDialogStage(noticeListDialogStage);
            noticeListDialogStage.showAndWait();
        } catch (Exception ex) {
            DialogsUtils.errorAlert("system.malfunction");
            logger.error(FxmlUtils.getResourceBundle().getString("error.MeetInfoController.handNoticeList"), ex);
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
