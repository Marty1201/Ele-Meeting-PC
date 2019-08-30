package com.chinaunicom.elemeetingpc.controllers;

import com.chinaunicom.elemeetingpc.database.models.IssueInfo;
import com.chinaunicom.elemeetingpc.utils.FxmlUtils;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

/**
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

    private BorderPane borderPaneMain;

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
    public void initData(IssueInfo issueInfo) throws ApplicationException {
        //议题名称区域
        issueTitle.setText(issueInfo.getIssueName());
        issueTitle.setTextAlignment(TextAlignment.CENTER);
        issueTitle.setTextOverrun(OverrunStyle.ELLIPSIS);
        issueTitle.setWrapText(true);
        issueTitle.setPadding(new Insets(5, 15, 5, 15));
        issueTitle.setAlignment(Pos.CENTER);
        //flowpane
        FlowPane filesFlowPane = new FlowPane(Orientation.HORIZONTAL, 10.0, 10.0);
        filesFlowPane.setPadding(new Insets(5.0, 3.0, 5.0, 3.0));
        //childMeetingFlowPane.setPrefWrapLength(350.0);//it seems this method will not behave inside the vbox?
        //childMeetingFlowPane.setPrefSize(50.0, 50.0);
        filesFlowPane.setStyle("-fx-background-color: #ebebeb;");
        //VBox.setVgrow(childMeetingFlowPane, Priority.ALWAYS);
        //create flowpane contents
        createFiles(filesFlowPane);
        fileSection.getChildren().addAll(filesFlowPane);
    }

    public void createFiles(FlowPane filesFlowPane) {
        Image image = new Image(getClass().getResourceAsStream("/images/icon-book.jpg"));
        int fileListSize = 12;
        ImageView[] imageList = new ImageView[fileListSize];
        for (int k = 0; k < fileListSize; k++) {
            imageList[k] = new ImageView(image);
            Label fileLabel = new Label("第十三章关于动态计量经济模型-分布滞后模型与自回归第十三章关于动态计量经济模型-分布滞后模型与自回归第十三章关于动态计量经济模型-分布滞后模型与自回归");//issueList.get(k).getIssueName()
            //issueName.setPrefSize(120.0, 160.0);
            fileLabel.setPrefSize(161.0, 185.0);
            fileLabel.setTextOverrun(OverrunStyle.ELLIPSIS);
            fileLabel.setWrapText(true);
            fileLabel.setPadding(new Insets(15, 20, 35, 25));
            //issueName.setLineSpacing(1.0);
            StackPane fileStackPane = new StackPane();
            fileStackPane.setPrefSize(161.0, 185.0);
            fileStackPane.getChildren().addAll(imageList[k], fileLabel);
            fileStackPane.setAlignment(Pos.TOP_LEFT);
            //StackPane.setMargin(issueName, new Insets(15, 25, 50, 25));    
            filesFlowPane.getChildren().addAll(fileStackPane);
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
}
