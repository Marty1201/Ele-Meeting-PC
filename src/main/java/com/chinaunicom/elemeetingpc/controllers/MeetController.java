
package com.chinaunicom.elemeetingpc.controllers;

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
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
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
    
    private static final Logger logger = LoggerFactory.getLogger(MeetController.class);
    
    private BorderPane borderPaneMain;

    private MeetInfoModel meetInfoModel;

    private MeetIssueRelationModel meetIssueRelationModel;

    private IssueInfoModel issueInfoModel;
    
    private MeetUserRelationModel meetUserRelationModel;

    @FXML
    private VBox subMeetingSection;
    
    @FXML
    private TextField indexMeetTitle;

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
        indexMeetTitle.clear();
        //存放子会议区域控件的list
        List<Node> childMeetingSectionList = new ArrayList<>();
        String parentMeetId = "";
        try {
            //获取默认会议的父会议id
            parentMeetId = getDefaultMeetingId();
            //获取父会议名称
            List<MeetInfo> parentMeetList = meetInfoModel.queryMeetInfoByParentMeetId(parentMeetId);
            if (!parentMeetList.isEmpty()) {
                indexMeetTitle.setText(parentMeetList.get(0).getMeetingName());
            }
            //获取当前登录用户所属子会议信息
            List<String> childMeetIdList = getChildMeetList(GlobalStaticConstant.GLOBAL_ORGANINFO_OWNER_USERID);
            List<MeetInfo> childMeetList = meetInfoModel.queryChildMeetInfosByParentId(parentMeetId, childMeetIdList);
            if (!childMeetList.isEmpty()) {
                for (int i = 0; i < childMeetList.size(); i++) {
                    //子会议名称区域
                    TextField childMeetingName = new TextField(childMeetList.get(i).getMeetingName());
                    childMeetingName.setAlignment(Pos.CENTER);
                    childMeetingName.setPrefSize(1300.0, 60.0);
                    childMeetingName.setEditable(false);
                    childMeetingName.setStyle("-fx-text-fill:#ffffff;-fx-background-color:#4581bf;-fx-font-size:20px;-fx-pref-height: 40px;");
                    childMeetingSectionList.add(childMeetingName);
                    //子会议议题区域
                    FlowPane childMeetingFlowPane = new FlowPane(Orientation.HORIZONTAL, 10.0, 10.0);
                    childMeetingFlowPane.setPadding(new Insets(5.0, 3.0, 5.0, 3.0));
                    //childMeetingFlowPane.setPrefWrapLength(600);//1900
                    //childMeetingFlowPane.setPrefSize(1900.0, 200.0);
                    childMeetingFlowPane.setStyle("-fx-font-size: 16px;-fx-font-size-family: Arial;-fx-background-color: #ffffff;");
                    //VBox.setVgrow(childMeetingFlowPane, Priority.ALWAYS);

                    //在MeetIssueRelation表里根据子会议id获取对应的会议和议题关系
                    meetIssueRelationList = meetIssueRelationModel.queryMeetIssueRelation(childMeetList.get(i).getMeetingId());
                    //获取所有议题ids
                    for (int j = 0; j < meetIssueRelationList.size(); j++) {
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
                        //issueLabel.setPrefSize(161, 185);
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
            }
            //VBox.setVgrow(subMeetingSection, Priority.ALWAYS);
            subMeetingSection.getChildren().addAll(childMeetingSectionList);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    /**
     * 跳转左侧会议列表界面.
     */
    @FXML
    public void showFxmlLeftNavigation() {
        try {
            FXMLLoader loader = FxmlUtils.getFXMLLoader(FXML_LEFT_NAVIGATION);
            borderPaneMain.getChildren().remove(borderPaneMain.getCenter());//清除当前BorderPane内中间区域的内容
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
    public void showFxmlOrg() {
        try {
            FXMLLoader loader = FxmlUtils.getFXMLLoader(FXML_ORG_FXML);
            borderPaneMain.getChildren().remove(borderPaneMain.getCenter());//清除当前BorderPane内中间区域的内容
            borderPaneMain.setCenter(loader.load()); //将当前BorderPane中间区域加载为机构选择界面
            OrganInfoController organInfoController = loader.getController(); //从loader中获取OrganInfoController
            organInfoController.setBorderPane(borderPaneMain);//设置传参当前的borderPaneMain，以便在OrganInfoController中获取到当前BorderPane
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getCause().getMessage());
        }
    }
    
    public void setBorderPane(BorderPane borderPaneMain) {
        this.borderPaneMain = borderPaneMain;
    }
    
    /**
     * 获取默认父会议id，存在两种情况：
     * 1、未选择左侧会议列表中的会议，则获取默认父会议id的顺序优先级为当前会议 > 即将召开会议 > 历史会议
     * 2、已选择左侧会议列表中的会议，则从全局常量中的GLOBAL_SELECTED_MEETID获取默认父会议id.
     * 
     * @return parentMeetingId 父会议id
     * @throws ApplicationException
     * @throws java.sql.SQLException
     */
    public String getDefaultMeetingId() throws ApplicationException, SQLException{
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
    public List<String> getParentMeetList(String userId) throws ApplicationException, SQLException{
        List<String> parentMeetIdList = new ArrayList<>();
        this.meetUserRelationModel = new MeetUserRelationModel();
        List<MeetUserRelation> meetUserRelationList = new ArrayList<>();
        //根据用户id在会议用户关系表里查询其对应的所有子会议
        meetUserRelationList = meetUserRelationModel.queryMeetUserRelationByUserId(userId);
        if (!meetUserRelationList.isEmpty()) {
            for (MeetUserRelation meetUserRelation : meetUserRelationList) {
                //根据子会议获取其父会议
                parentMeetIdList.add(getParentMeetIdByChildMeetId(meetUserRelation.getMeetingId()));
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
    public String getParentMeetIdByChildMeetId(String childMeetId) throws ApplicationException, SQLException{
        String parentMeetId = "";
        this.meetInfoModel = new MeetInfoModel();
        List<MeetInfo> meetInfoList = meetInfoModel.queryMeetInfoByChildMeetId(childMeetId);
        if(!meetInfoList.isEmpty()){
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
}