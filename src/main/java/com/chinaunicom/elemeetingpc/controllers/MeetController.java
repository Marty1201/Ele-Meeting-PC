package com.chinaunicom.elemeetingpc.controllers;

import static com.chinaunicom.elemeetingpc.MainApp.STYLES;
import com.chinaunicom.elemeetingpc.constant.GlobalStaticConstant;
import com.chinaunicom.elemeetingpc.database.models.IssueInfo;
import com.chinaunicom.elemeetingpc.database.models.MeetInfo;
import com.chinaunicom.elemeetingpc.database.models.MeetIssueRelation;
import com.chinaunicom.elemeetingpc.database.models.MeetUserRelation;
import com.chinaunicom.elemeetingpc.modelFx.IssueInfoModel;
import com.chinaunicom.elemeetingpc.modelFx.MeetInfoModel;
import com.chinaunicom.elemeetingpc.modelFx.MeetIssueRelationModel;
import com.chinaunicom.elemeetingpc.modelFx.MeetUserRelationModel;
import com.chinaunicom.elemeetingpc.utils.FxmlUtils;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import java.io.IOException;
import java.sql.SQLException;
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

/**
 * 会议首页面控制器
 *
 * @author zhaojunfeng, chenxi
 */
public class MeetController {

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

    private static final Logger logger = LoggerFactory.getLogger(MeetController.class);

    private BorderPane borderPaneMain;

    private MeetInfoModel meetInfoModel;

    private MeetIssueRelationModel meetIssueRelationModel;

    private IssueInfoModel issueInfoModel;

    private MeetUserRelationModel meetUserRelationModel;

    @FXML
    private VBox subMeetingSection;

    @FXML
    private VBox mainIndex;

    @FXML
    private Label indexMeetTitle;

    /**
     * 会议首页面布局初始化.
     *
     * @throws ApplicationException
     */
    public void initialize() throws ApplicationException {
        this.meetInfoModel = new MeetInfoModel();
        this.meetIssueRelationModel = new MeetIssueRelationModel();
        this.issueInfoModel = new IssueInfoModel();
        List<MeetInfo> childMeetList = new ArrayList<>();//子会议列表
        List<String> childMeetIdList = new ArrayList<>();//子会议id列表
        List<MeetIssueRelation> meetIssueRelationList = new ArrayList<>();//子会议与议题关系列表
        List<IssueInfo> issueList = new ArrayList<>();//议题列表
        List<String> issueIdList = new ArrayList<>();//议题id列表
        List<Node> uiControlsList = new ArrayList<>();//存放界面UI控件的列表
        String parentMeetId = "";
        try {
            //获取默认会议的父会议id
            parentMeetId = getDefaultMeetingId();
            //父会议名称区域
            createParentMeetingTitle(parentMeetId);
            //获取当前登录用户所属子会议信息
            childMeetIdList = getChildMeetList(GlobalStaticConstant.GLOBAL_ORGANINFO_OWNER_USERID);
            childMeetList = meetInfoModel.queryChildMeetInfosByParentId(parentMeetId, childMeetIdList);
            if (!childMeetList.isEmpty()) {
                for (int i = 0; i < childMeetList.size(); i++) {
                    //子会议名称区域
                    createSubMeetingTitle(childMeetList.get(i).getMeetingName(), uiControlsList);
                    //在MeetIssueRelation表里根据子会议id获取对应的会议和议题关系
                    meetIssueRelationList = meetIssueRelationModel.queryMeetIssueRelation(childMeetList.get(i).getMeetingId());
                    if (!meetIssueRelationList.isEmpty()) {
                        //获取所有议题ids
                        for (int j = 0; j < meetIssueRelationList.size(); j++) {
                            issueIdList.add(meetIssueRelationList.get(j).getIssueId());
                        }
                        //根据议题ids获取议题信息
                        issueList = issueInfoModel.queryIssueByIds(issueIdList);
                        issueIdList.clear();
                        int issueSize = issueList.size(); //议题个数
                        //议题区域
                        createMeetingIssues(issueSize, issueList, uiControlsList);
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
            ex.printStackTrace();
        }
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
                isFolded = false;//设置为展开状态
            } else {
                borderPaneMain.getChildren().remove(borderPaneMain.getLeft());//清除当前BorderPane内左侧区域的内容
                isFolded = true;//设置为收起状态
            }
        } catch (IOException e) {
            logger.error(e.getCause().getMessage());
            e.printStackTrace();
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
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getCause().getMessage());
        }
    }

    /**
     * 跳转文件列表界面.
     */
    @FXML
    public void showFxmlFile(IssueInfo issueInfo) throws ApplicationException, SQLException {
        try {
            FXMLLoader loader = FxmlUtils.getFXMLLoader(FXML_FILE);
            borderPaneMain.getChildren().remove(borderPaneMain.getCenter());//清除当前BorderPane内中间区域的内容
            borderPaneMain.getChildren().remove(borderPaneMain.getLeft());//清除当前BorderPane内左侧区域的内容
            borderPaneMain.setCenter(loader.load()); //将当前BorderPane中间区域加载为文件列表界面
            FileController fileController = loader.getController(); //从loader中获取FileController
            fileController.setBorderPane(borderPaneMain);//设置传参当前的borderPaneMain，以便在FileController中获取到当前BorderPane
            fileController.initData(issueInfo);//把当前选择的议题传到FileController里，以便在下个控制器中使用，注意这里调用的是重写过的初始化方法
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getCause().getMessage());
        }
    }

    public void setBorderPane(BorderPane borderPaneMain) {
        this.borderPaneMain = borderPaneMain;
    }

    /**
     * 获取默认父会议id，存在两种情况： 1、未选择左侧会议列表中的会议，则获取默认父会议id的顺序优先级为当前会议 > 即将召开会议 > 历史会议
     * 2、已选择左侧会议列表中的会议，则从全局常量中的GLOBAL_SELECTED_MEETID获取默认父会议id.
     *
     * @return parentMeetingId 父会议id
     * @throws ApplicationException
     * @throws java.sql.SQLException
     */
    public String getDefaultMeetingId() throws ApplicationException, SQLException {
        String parentMeetingId = GlobalStaticConstant.GLOBAL_SELECTED_MEETID;
        if (StringUtils.isBlank(parentMeetingId)) {
            List<String> parentMeetIdList = new ArrayList<>();
            parentMeetIdList = getParentMeetList(GlobalStaticConstant.GLOBAL_ORGANINFO_OWNER_USERID);
            if (!parentMeetIdList.isEmpty()) {
                this.meetInfoModel = new MeetInfoModel();
                List<MeetInfo> currentMeetList = meetInfoModel.getCurrentMeetInfo(parentMeetIdList);
                List<MeetInfo> futureMeetList = meetInfoModel.getFutureMeetInfo(parentMeetIdList);
                List<MeetInfo> historyMeetList = meetInfoModel.getHistoryMeetInfo(parentMeetIdList);
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

    /**
     * 根据用户id获取其所在的子会议id列表.
     *
     * @param userId
     * @return childMeetIdList 子会议id列表
     * @throws ApplicationException
     * @throws java.sql.SQLException
     */
    public List<String> getChildMeetList(String userId) throws ApplicationException, SQLException {
        List<String> childMeetIdList = new ArrayList<>();
        this.meetUserRelationModel = new MeetUserRelationModel();
        List<MeetUserRelation> meetUserRelationList = new ArrayList<>();
        //根据用户id在会议用户关系表里查询其对应的所有子会议
        meetUserRelationList = meetUserRelationModel.queryMeetUserRelationByUserId(userId);
        if (!meetUserRelationList.isEmpty()) {
            for (MeetUserRelation meetUserRelation : meetUserRelationList) {
                childMeetIdList.add(meetUserRelation.getMeetingId());
            }
        }
        return childMeetIdList;
    }

    /**
     * 创建父会议标题.
     *
     * @param parentMeetId
     */
    public void createParentMeetingTitle(String parentMeetId) throws ApplicationException, SQLException {
        //获取父会议名称
        List<MeetInfo> parentMeetList = meetInfoModel.queryMeetInfoByParentMeetId(parentMeetId);
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
     * @param meetingName
     * @param uiControlsList
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
     * 创建议题，从低层到顶层所用的UI控件分别是：FlowPane -> StackPane -> ImageView -> Label.
     *
     * @param issueSize
     * @param issueList
     * @param uiControlsList
     */
    public void createMeetingIssues(int issueSize, List<IssueInfo> issueList, List<Node> uiControlsList) {
        //设置FlowPane样式
        FlowPane issueFlowPane = new FlowPane(Orientation.HORIZONTAL, 10.0, 10.0);
        issueFlowPane.setPadding(new Insets(5.0, 3.0, 5.0, 3.0));
        //childMeetingFlowPane.setPrefWrapLength(1300);//内容换行使用默认值
        issueFlowPane.setStyle("-fx-background-color:#ffffff;");
        //议题区域（单个议题实现方式：StackPane + ImageView + Label）
        Image icon = new Image(getClass().getResourceAsStream("/images/icon-book.jpg"));
        ImageView[] imageViews = new ImageView[issueSize];
        for (int k = 0; k < issueSize; k++) {
            imageViews[k] = new ImageView(icon);
            Label issueNameLabel = new Label(issueList.get(k).getIssueName());
            IssueInfo issue = issueList.get(k);
            StackPane issueStackPane = new StackPane();
            //设置Label样式
            issueNameLabel.setStyle("-fx-font-size:16.0px;-fx-font-size-family:Arial;-fx-cursor:hand;");
            issueNameLabel.setPrefSize(161, 185);
            issueNameLabel.setWrapText(true);
            issueNameLabel.setTextOverrun(OverrunStyle.ELLIPSIS);
            issueNameLabel.setPadding(new Insets(15, 20, 35, 25)); //通过内间距padding控制文字内容的位置
            //issueLabel.setLineSpacing(1.0);
            //给Label添加鼠标点击事件
            addMouseClickEvent(issueNameLabel, issue);
            //设置StackPane样式
            issueStackPane.setPrefSize(161.0, 185.0);
            issueStackPane.getChildren().addAll(imageViews[k], issueNameLabel);
            issueStackPane.setAlignment(Pos.TOP_LEFT);
            //StackPane.setMargin(issueName, new Insets(15, 25, 50, 25));//或通过外间距margin控制文字内容的位置
            issueFlowPane.getChildren().addAll(issueStackPane);
        }
        uiControlsList.add(issueFlowPane);
    }

    /**
     * Label点击事件，跳转到文件界面.
     *
     * @param issueNameLabel
     */
    public void addMouseClickEvent(Label issueNameLabel, IssueInfo issue) {
        issueNameLabel.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            public void handle(final MouseEvent event) {
                try {
                    //System.out.println("issueName is: " + issueNameLabel.getText());
                    showFxmlFile(issue);//跳转到文件列表界面
                } catch (ApplicationException ex) {
                    ex.printStackTrace();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    /**
     * 密码修改.
     */
    @FXML
    public void handleResetPassword() {
        try {
            FXMLLoader loader = FxmlUtils.getFXMLLoader(FXML_RESET_PASSWORD);
            AnchorPane resetPasswordDialog = loader.load();
            //创建一个弹窗
            Stage dialogStage = new Stage();
            dialogStage.setTitle("修改密码");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(borderPaneMain.getScene().getWindow());
            //or 直接一行就够了dialogStage.initModality(Modality.APPLICATION_MODAL);
            Scene scene = new Scene(resetPasswordDialog);
            scene.getStylesheets().add(STYLES);
            dialogStage.setScene(scene);
            ResetPasswordController controller = loader.getController();
            controller.setDialogStage(dialogStage);//把界面传递到下一个控制器里
            dialogStage.showAndWait();
        } catch (IOException ex) {
            logger.warn(ex.getCause().getMessage());
        }
    }
    
    /**
     * 通知列表信息
     */
    @FXML
    private void handNoticeList(){
        FXMLLoader loader = FxmlUtils.getFXMLLoader(FXML_NOTICE_LIST);
        AnchorPane noticeListDialog=new AnchorPane();
        try {
            noticeListDialog = loader.load();
        } catch (IOException ex) {
            java.util.logging.Logger.getLogger(MeetController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Stage dialogStage = new Stage();
        dialogStage.setTitle("通知信息列表");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(borderPaneMain.getScene().getWindow());
        Scene scene = new Scene(noticeListDialog);
        dialogStage.setScene(scene);
        
        NoticeInfoController controller = loader.getController();
        controller.setDialogStage(dialogStage);
        
        dialogStage.showAndWait(); 
    }
    
}
