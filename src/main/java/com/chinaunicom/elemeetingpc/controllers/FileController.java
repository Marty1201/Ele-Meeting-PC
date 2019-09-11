package com.chinaunicom.elemeetingpc.controllers;

import com.chinaunicom.elemeetingpc.constant.GlobalStaticConstant;
import com.chinaunicom.elemeetingpc.database.models.FileUserRelation;
import com.chinaunicom.elemeetingpc.database.models.IssueFileRelation;
import com.chinaunicom.elemeetingpc.database.models.IssueInfo;
import com.chinaunicom.elemeetingpc.modelFx.FileUserRelationModel;
import com.chinaunicom.elemeetingpc.modelFx.IssueFileRelationModel;
import com.chinaunicom.elemeetingpc.utils.FxmlUtils;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

/**
 * 文件控制器.
 *
 * @author chenxi 创建时间：2019-8-29 15:31:28
 */
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    //左侧会议列表界面
    public static final String FXML_LEFT_NAVIGATION = "/fxml/fxml_left_navigation.fxml";

    //机构选择界面
    public static final String FXML_ORG_FXML = "/fxml/fxml_org.fxml";

    //会议主界面
    public static final String FXML_INDEX = "/fxml/fxml_index.fxml";

    //左侧会议列表默认收起状态
    private boolean isFolded = true;

    @FXML
    private Label issueTitle;

    @FXML
    private VBox fileSection;

    @FXML
    private VBox fileIndex;

    private BorderPane borderPaneMain;

    private IssueFileRelationModel issueFileRelationModel;

    private FileUserRelationModel FileUserRelationModel;

    /**
     * 默认文件页面布局初始化.
     *
     * @throws ApplicationException
     */
    public void initialize() throws ApplicationException {

    }

    /**
     * 重写文件页面布局初始化.
     *
     * @param issueInfo
     * @throws ApplicationException
     */
    public void initData(IssueInfo issueInfo) throws ApplicationException, SQLException {
        this.issueFileRelationModel = new IssueFileRelationModel();
        this.FileUserRelationModel = new FileUserRelationModel();
        List<String> issueFileIdList = new ArrayList<>();//议题文件关系表文件id列表
        List<String> fileUserFileList = new ArrayList<>();//文件人员关系表文件id列表
        try {
            //议题名称区域
            createIssueTitle(issueInfo);
            //根据议题id查询议题文件关系表对应的文件id
            String issueId = issueInfo.getIssueId();
            List<IssueFileRelation> issueFileRelationList = issueFileRelationModel.queryIssueFileRelationByIssueId(issueId);
            if (!issueFileRelationList.isEmpty()) {
                for (IssueFileRelation issueFileRelation : issueFileRelationList) {
                    issueFileIdList.add(issueFileRelation.getFileId());
                }
            }
            //根据当前登录用户id查询文件人员关系表对应的文件id
            List<FileUserRelation> fileUserRelationList = FileUserRelationModel.queryFileUserRelationByUserId(GlobalStaticConstant.GLOBAL_ORGANINFO_OWNER_USERID);
            if (!fileUserRelationList.isEmpty()) {
                for (FileUserRelation fileUserRelation : fileUserRelationList) {
                    fileUserFileList.add(fileUserRelation.getFileId());
                }
            }
            //比较两个列表并获取两个列表中重合的数据
            issueFileIdList.retainAll(fileUserFileList);
            //根据筛选过的文件id列表和议题id获取IssueFileRelation表里的数据
            List<IssueFileRelation> issueFileList = issueFileRelationModel.queryIssueFileRelationByFileIds(issueFileIdList, issueId);
            if (!issueFileList.isEmpty()) {
                //文件区域，注意：文件名称是从议题文件关系表里获取的，而不是从文件表里获取的，故此处传入的是IssueFileRelation列表
                createFiles(issueFileList, fileSection);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 创建文件，从低层到顶层所用的UI控件分别是：FlowPane -> StackPane -> ImageView -> Label.
     *
     * @param issueFileList
     * @param fileSection
     */
    public void createFiles(List<IssueFileRelation> issueFileList, VBox fileSection) {
        //设置FlowPane样式
        FlowPane filesFlowPane = new FlowPane(Orientation.HORIZONTAL, 10.0, 10.0);
        filesFlowPane.setPadding(new Insets(5.0, 3.0, 5.0, 3.0));
        //childMeetingFlowPane.setPrefWrapLength(1300);//内容换行使用默认宽度
        //childMeetingFlowPane.setPrefSize(50.0, 50.0);
        filesFlowPane.setStyle("-fx-background-color: #f4f4f4;");
        //文件区域（单个文件实现方式：StackPane + ImageView + Label）
        Image image = new Image(getClass().getResourceAsStream("/images/icon-book.jpg"));
        int issueFileListSize = issueFileList.size();
        ImageView[] imageList = new ImageView[issueFileListSize];
        for (int k = 0; k < issueFileListSize; k++) {
            imageList[k] = new ImageView(image);
            Label issueFileLabel = new Label(issueFileList.get(k).getFileName());
            StackPane fileStackPane = new StackPane();
            //todo:根据文件id列表查询文件，用于下面鼠标点击事件传参
            //设置Label样式
            issueFileLabel.setStyle("-fx-font-size:16.0px;-fx-font-size-family:Arial;-fx-cursor:hand;");
            issueFileLabel.setPrefSize(161.0, 185.0);
            issueFileLabel.setTextOverrun(OverrunStyle.ELLIPSIS);
            issueFileLabel.setWrapText(true);
            issueFileLabel.setPadding(new Insets(15, 20, 35, 25)); //通过内间距padding控制文字内容的位置
            //fileLabel.setLineSpacing(1.0);
            //todo:给Label添加鼠标点击事件
            //addMouseClickEvent(issueNameLabel, issue);
            //设置StackPane样式
            fileStackPane.setPrefSize(161.0, 185.0);
            fileStackPane.getChildren().addAll(imageList[k], issueFileLabel);
            fileStackPane.setAlignment(Pos.TOP_LEFT);
            //StackPane.setMargin(issueName, new Insets(15, 25, 50, 25));//或通过外间距margin控制文字内容的位置  
            filesFlowPane.getChildren().addAll(fileStackPane);
        }
        fileSection.getChildren().addAll(filesFlowPane);
        //设置Vertical ScrollPane
        ScrollPane scroll = new ScrollPane(fileSection);
        scroll.setPannable(true);
        scroll.setFitToWidth(true);
        scroll.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scroll.setStyle("-fx-background-insets:0.0px;-fx-border-color:transparent;");
        fileIndex.getChildren().addAll(scroll);
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
     * 跳转会议界面.
     */
    @FXML
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

    public void setBorderPane(BorderPane borderPaneMain) {
        this.borderPaneMain = borderPaneMain;
    }

    /**
     * 创建议题名称.
     *
     * @param issueInfo
     */
    public void createIssueTitle(IssueInfo issueInfo) {
        issueTitle.setText(issueInfo.getIssueName());
        issueTitle.setTextAlignment(TextAlignment.CENTER);
        issueTitle.setTextOverrun(OverrunStyle.ELLIPSIS);
        issueTitle.setWrapText(true);
        issueTitle.setPadding(new Insets(5, 15, 5, 15));
        issueTitle.setAlignment(Pos.CENTER);
    }
}