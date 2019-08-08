
package com.chinaunicom.elemeetingpc.controllers;

import com.chinaunicom.elemeetingpc.database.models.IssueInfo;
import com.chinaunicom.elemeetingpc.database.models.MeetInfo;
import com.chinaunicom.elemeetingpc.database.models.MeetIssueRelation;
import com.chinaunicom.elemeetingpc.modelFx.IssueInfoModel;
import com.chinaunicom.elemeetingpc.modelFx.MeetInfoModel;
import com.chinaunicom.elemeetingpc.modelFx.MeetIssueRelationModel;
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
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

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
    
    private static final Logger logger = LoggerFactory.getLogger(MeetController.class);
    
    private BorderPane borderPaneMain;

    private MeetInfoModel meetInfoModel;

    private MeetIssueRelationModel meetIssueRelationModel;

    private IssueInfoModel issueInfoModel;

    @FXML
    private VBox subMeetingSection;

    /**
     * 会议首页面初始化.
     *
     * @throws ApplicationException
     */
    public void initialize() throws ApplicationException {
        this.meetInfoModel = new MeetInfoModel();
        this.meetIssueRelationModel = new MeetIssueRelationModel();
        this.issueInfoModel = new IssueInfoModel();
        List<MeetIssueRelation> meetIssueRelationList = new ArrayList<>();
        List<IssueInfo> issueList = new ArrayList<>();
        List<String> issueIdList = new ArrayList<>();
        try {
            //获取默认父会议id, 在MeetInfo表里根据默认父会议id查到其下子会议信息
            List<MeetInfo> childMeetList = meetInfoModel.queryChildMeetInfosByParentId(getDefaultMeetingId());
            //存放子会议区域控件的list
            List<Node> childMeetingSectionList = new ArrayList<>();
            //子会议
            for (int i = 0; i < childMeetList.size(); i++) {
                //子会议名称区域
                TextField childMeetingName = new TextField(childMeetList.get(i).getMeetingName());
                childMeetingName.setAlignment(Pos.CENTER);
                childMeetingName.setPrefSize(1920.0, 68.0);
                childMeetingName.setStyle("-fx-text-fill:#ffffff;-fx-background-color:#4581bf;-fx-font-size:20px;-fx-pref-height: 40px;");
                childMeetingSectionList.add(childMeetingName);
                //子会议议题区域
                FlowPane childMeetingFlowPane = new FlowPane(Orientation.HORIZONTAL, 2, 4);
                childMeetingFlowPane.setPrefWrapLength(240);
                childMeetingFlowPane.setPrefSize(1920.0, 363.0);
                childMeetingFlowPane.setStyle("-fx-font-size: 16px;-fx-font-size-family: Arial;-fx-background-color: #ffffff;");               
                //在MeetIssueRelation表里根据子会议id获取对应的会议和议题关系
                meetIssueRelationList = meetIssueRelationModel.queryMeetIssueRelation(childMeetList.get(i).getMeetingId());
                //获取所有议题ids
                for(int j = 0; j < meetIssueRelationList.size(); j++){
                    issueIdList.add(meetIssueRelationList.get(j).getIssueId());
                }
                //根据议题ids获取议题信息
                issueList = issueInfoModel.queryIssueByIds(issueIdList);
                issueIdList.clear();
                int issueSize = issueList.size(); //议题个数
                Image icon = new Image(getClass().getResourceAsStream("/images/icon-book.jpg"));
                ImageView[] imageViews = new ImageView[issueSize];
                for (int k = 0; k < issueSize; k++) {
                    imageViews[k] = new ImageView(icon);
                    Label issueLabel = new Label(issueList.get(k).getIssueName(), imageViews[k]);
                    issueLabel.setPrefSize(30, 30);
                    issueLabel.setWrapText(true);
                    //issueLabel.setTextAlignment(TextAlignment.LEFT);
                    //issueLabel.setAlignment(Pos.TOP_LEFT); //to do: issues title start from top
                    issueLabel.setContentDisplay(ContentDisplay.CENTER);
                    childMeetingFlowPane.getChildren().addAll(issueLabel);
                }
                //滚动条
//                ScrollBar sc = new ScrollBar();
//                sc.setOrientation(Orientation.HORIZONTAL);
//                sc.setMin(0);
//                sc.setMax(100);
//                //sc.setPrefHeight(180);
//                sc.setValue(50);
//                childMeetingFlowPane.getChildren().addAll(sc);

                //滚动pane
                //ScrollPane sp = new ScrollPane();
                //sp.setContent(childMeetingFlowPane);
                //childMeetingSectionList.add(sp);
                childMeetingSectionList.add(childMeetingFlowPane);
            }
            subMeetingSection.getChildren().addAll(childMeetingSectionList);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * 跳转左侧会议列表界面.
     */
    @FXML
    private void showFxmlLeftNavigation() {
        try {
            FXMLLoader loader = FxmlUtils.getFXMLLoader(FXML_LEFT_NAVIGATION);
            borderPaneMain.setCenter(loader.load()); //将当前BorderPane中间区域加载为机构选择界面
            MeetLeftController meetLeftController = loader.getController(); //从loader中获取MeetLeftController
            meetLeftController.setBorderPane(borderPaneMain);//设置传参当前的borderPane，以便在MeetLeftController中获取到当前BorderPane
        } catch (IOException e) {
            logger.error(e.getCause().getMessage());
        }
    }
    
    /**
     * 跳转机构选择界面.
     */
    @FXML
    private void showFxmlOrg() {
        try {
            FXMLLoader loader = FxmlUtils.getFXMLLoader(FXML_ORG_FXML);
            borderPaneMain.setCenter(loader.load()); //将当前BorderPane中间区域加载为机构选择界面
            OrganInfoController organInfoController = loader.getController(); //从loader中获取OrganInfoController
            organInfoController.setBorderPane(borderPaneMain);//设置传参当前的borderPaneMain，以便在OrganInfoController中获取到当前BorderPane
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getCause().getMessage());
        }
    }
    
    public void setBorderPane(BorderPane borderPane) {
        this.borderPaneMain = borderPaneMain;
    }
    
    /**
     * 获取默认父会议id，顺序优先级为当前会议 > 即将召开会议 > 历史会议.
     *
     * @return parentMeetingId 父会议id
     * @throws ApplicationException
     * @throws java.sql.SQLException
     */
    public String getDefaultMeetingId() throws ApplicationException, SQLException{
        String parentMeetingId = "";
        List<MeetInfo> currentMeetList = meetInfoModel.getCurrentMeetInfo();
        List<MeetInfo> futureMeetList = meetInfoModel.getFutureMeetInfo();
        List<MeetInfo> historyMeetList = meetInfoModel.getHistoryMeetInfo();
        if (!currentMeetList.isEmpty()) {
            parentMeetingId = currentMeetList.get(0).getMeetingId();
        } else if (!futureMeetList.isEmpty()) {
            parentMeetingId = futureMeetList.get(0).getMeetingId();
        } else if (!historyMeetList.isEmpty()) {
            parentMeetingId = historyMeetList.get(0).getMeetingId();
        }
        return parentMeetingId;
    }
}