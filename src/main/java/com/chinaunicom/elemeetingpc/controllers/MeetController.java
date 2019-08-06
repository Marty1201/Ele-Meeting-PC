
package com.chinaunicom.elemeetingpc.controllers;

import com.chinaunicom.elemeetingpc.database.models.IssueInfo;
import com.chinaunicom.elemeetingpc.database.models.MeetInfo;
import com.chinaunicom.elemeetingpc.database.models.MeetIssueRelation;
import com.chinaunicom.elemeetingpc.modelFx.IssueInfoModel;
import com.chinaunicom.elemeetingpc.modelFx.MeetInfoModel;
import com.chinaunicom.elemeetingpc.modelFx.MeetIssueRelationModel;
import com.chinaunicom.elemeetingpc.utils.FxmlUtils;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import java.io.IOException;
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
 * 会议控制器
 *
 * @author zhaojunfeng, chenxi
 */
public class MeetController {
    
    //会议界面左侧列表
    public static final String FXML_LEFT_NAVIGATION = "/fxml/fxml_left_navigation.fxml";
    
    private static final Logger logger = LoggerFactory.getLogger(MeetController.class);
    
    private BorderPane borderPane;

    private MeetInfoModel meetInfoModel;

    private MeetIssueRelationModel meetIssueRelationModel;

    private IssueInfoModel issueInfoModel;

//    @FXML
//    private FlowPane meetFlowPane;
    @FXML
    private VBox subMeetingSection;

    public void initialize() {

        //业务逻辑
        this.meetInfoModel = new MeetInfoModel();
        this.meetIssueRelationModel = new MeetIssueRelationModel();
        this.issueInfoModel = new IssueInfoModel();
        List<MeetIssueRelation> meetIssueRelationList = new ArrayList<>();
        List<IssueInfo> meetIssueList = new ArrayList<>();
        try {
            //在MeetInfo表里根据默认会议的父会议id获取子会议id
            List<MeetInfo> childMeetList = meetInfoModel.queryChildMeetInfosByParentId("parentMeetingId", "20190701145559610372604487039259");
            //在MeetIssueRelation表里跟换子会议id获取对应的议题id
            for (MeetInfo meetInfo : childMeetList) {
                meetIssueRelationList.addAll(meetIssueRelationModel.queryIssueIdByMeetId("meetingId", meetInfo.getMeetingId()));
            }
            //在MeetIssueRelation表里获取议题id，并根据议题id在issueInfo表里查到对应的议题
            for (MeetIssueRelation meetIssue : meetIssueRelationList) {
                meetIssueList.addAll(issueInfoModel.queryIssueById("issueId", meetIssue.getIssueId()));
            }

            //界面逻辑
            List<Node> childMeetingSectionList = new ArrayList<>();
            int childMeetingSize = childMeetList.size();
            int issueSize = meetIssueList.size();

            //子会议
            for (int i = 0; i < childMeetingSize; i++) {
                //子会议名称区域
                TextField childMeetingName = new TextField(childMeetList.get(i).getMeetingName());
                childMeetingName.setAlignment(Pos.CENTER);
                childMeetingName.setPrefSize(1920.0, 68.0);
                childMeetingName.setStyle("-fx-text-fill:#ffffff;-fx-background-color:#4581bf;-fx-font-size:20px;-fx-pref-height: 40px;");
                childMeetingSectionList.add(childMeetingName);
                
                //议题
                FlowPane childMeetingFlowPane = new FlowPane(Orientation.HORIZONTAL, 2, 4);
                childMeetingFlowPane.setPrefWrapLength(240);
                childMeetingFlowPane.setPrefSize(1920.0, 363.0);
                childMeetingFlowPane.setStyle("-fx-font-size: 16px;-fx-font-size-family: Arial;-fx-background-color: #ffffff;");

                Image icon = new Image(getClass().getResourceAsStream("/images/icon-book.jpg"));
                ImageView[] imageViews = new ImageView[issueSize];
                for (int k = 0; k < issueSize; k++) {
                    imageViews[i] = new ImageView(icon);
                    Label issueLabel = new Label(meetIssueList.get(i).getIssueName(), imageViews[i]);
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
     * 首页（回退）
     */
    @FXML
    private void shouye() {
        try {
            FXMLLoader loader = FxmlUtils.getFXMLLoader(FXML_LEFT_NAVIGATION);
            borderPane.setCenter(loader.load()); //将当前BorderPane中间区域加载为机构选择界面
            MeetLeftController meetLeftController = loader.getController(); //从loader中获取MeetLeftController
            meetLeftController.setBorderPane(borderPane);//设置传参当前的borderPane，以便在MeetLeftController中获取到当前BorderPane
        } catch (IOException e) {
            logger.error(e.getCause().getMessage());
        }
    }
    
    public void setBorderPane(BorderPane borderPane) {
        this.borderPane = borderPane;
    }
}
