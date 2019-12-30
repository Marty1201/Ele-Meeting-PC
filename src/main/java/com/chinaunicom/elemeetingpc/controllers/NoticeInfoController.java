package com.chinaunicom.elemeetingpc.controllers;

import static com.chinaunicom.elemeetingpc.MainApp.STYLES;
import com.chinaunicom.elemeetingpc.modelFx.NoticeInfoFx;
import com.chinaunicom.elemeetingpc.utils.DialogsUtils;
import com.chinaunicom.elemeetingpc.utils.FxmlUtils;
import static com.chinaunicom.elemeetingpc.utils.FxmlUtils.getResourceBundle;
import java.util.List;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.Pagination;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
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
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 会议通知列表控制器.
 *
 * @author zhaojunfeng, chenxi
 */
public class NoticeInfoController {

    private static final Logger logger = LoggerFactory.getLogger(NoticeInfoController.class);

    @FXML
    private Pagination pagination;

    //通知详情界面
    public static final String FXML_NOTICE_DETATIL = "/fxml/fxml_notice_detail.fxml";

    //通知列表界面
    private Stage noticeListDialogStage;

    private NoticeInfoServiceController noticeInfoServiceController;

    /**
     * 初始化分页控件.
     *
     */
    public void initialize() {
        noticeInfoServiceController = new NoticeInfoServiceController();
        pagination.setPageFactory((Integer pageIndex) -> createPage(pageIndex));
    }

    /**
     * 创建通知列表界面， 该界面控件顺序从顶层到底层依次是：3个Label -> BorderPane -> VBox.
     *
     * @param pageIndex 当前页
     * @return noticeContentBox
     */
    public VBox createPage(int pageIndex) {
        VBox noticeContentBox = new VBox(5);
        try {
            //根据当前页码，调接口获取当前页码上的会议通知列表
            List<NoticeInfoFx> noticeList = noticeInfoServiceController.getNoticeInfoList(pageIndex);
            for (int i = 0; i < noticeList.size(); i++) {
                //通知标题
                Label noticeTitle = new Label(noticeList.get(i).getNoticeTitle());
                noticeTitle.setFont(Font.font("Arial", FontWeight.BOLD, 18.0));
                noticeTitle.setTextFill(Color.BLACK);
                noticeTitle.setPadding(new Insets(5, 0, 5, 5));
                noticeTitle.setTextOverrun(OverrunStyle.ELLIPSIS);
                noticeTitle.setWrapText(true);
                //通知类型
                Label noticeType = new Label(noticeList.get(i).getNoticeTypeName());
                noticeType.setFont(Font.font("Arial", 14.0));
                noticeType.setTextFill(Color.GREY);
                noticeType.setPadding(new Insets(0, 0, 5, 5));
                //通知日期
                Label noticeDate = new Label(noticeList.get(i).getCreateTime());
                noticeDate.setFont(Font.font("Arial", 14.0));
                noticeDate.setTextFill(Color.GREY);
                noticeDate.setPadding(new Insets(0, 5, 5, 0));
                //创建noticeCell容器
                BorderPane noticeCell = new BorderPane();
                noticeCell.setPrefHeight(50.0);
                noticeCell.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));
                noticeCell.setBorder(new Border(new BorderStroke(Color.valueOf("#f0eff5"), Color.valueOf("#f0eff5"), Color.valueOf("#dfdee4"), Color.valueOf("#f0eff5"), BorderStrokeStyle.NONE, BorderStrokeStyle.NONE, BorderStrokeStyle.SOLID, BorderStrokeStyle.NONE, CornerRadii.EMPTY, BorderWidths.DEFAULT, Insets.EMPTY)));
                noticeCell.setStyle("-fx-cursor:hand;");
                //增加hover效果
                noticeCell.setOnMouseEntered(e -> noticeCell.setBackground(new Background(new BackgroundFill(Color.valueOf("#ececec"), CornerRadii.EMPTY, Insets.EMPTY))));
                noticeCell.setOnMouseExited(e -> noticeCell.setBackground(new Background(new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY))));
                noticeCell.setTop(noticeTitle);
                noticeCell.setLeft(noticeType);
                noticeCell.setRight(noticeDate);
                noticeCell.setUserData(noticeList.get(i));//把会议通知信息添加到noticeCell上，用于在点击事件里获取会议通知id
                addMouseClickEvent(noticeCell);//给noticeCell添加鼠标点击事件
                noticeContentBox.getChildren().add(noticeCell);
            }
            noticeList.clear();
        } catch (Exception ex) {
            DialogsUtils.errorAlert("system.malfunction");
            logger.error(FxmlUtils.getResourceBundle().getString("error.NoticeInfoController.createPage"), ex);
        }
        return noticeContentBox;
    }

    /**
     * BorderPane点击事件，跳转到通知详情界面并传入对应的通知信息.
     *
     * @param noticeCell 代表一条通知
     */
    public void addMouseClickEvent(BorderPane noticeCell) {
        noticeCell.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(final MouseEvent event) {
                showNoticeDetail((NoticeInfoFx) noticeCell.getUserData());
            }
        });
    }

    /**
     * 通知详情.
     *
     * @param notice 会议通知
     */
    private void showNoticeDetail(NoticeInfoFx notice) {
        try {
            //加载通知详情界面
            FXMLLoader loader = FxmlUtils.getFXMLLoader(FXML_NOTICE_DETATIL);
            loader.setResources(getResourceBundle());
            AnchorPane noticeDetailContent = new AnchorPane();
            noticeDetailContent = loader.load();
            //加载通知详情界面数据
            NoticeDetailController controller = loader.getController();
            controller.initData(notice);
            //创建弹窗
            Stage noticeDetailDialogStage = new Stage();
            noticeDetailDialogStage.setWidth(1300.0);
            noticeDetailDialogStage.setHeight(700.0);
            noticeDetailDialogStage.setTitle(FxmlUtils.getResourceBundle().getString("NoticeInfoController.noticeDetail"));
            noticeDetailDialogStage.initModality(Modality.WINDOW_MODAL);
            noticeDetailDialogStage.initOwner(noticeListDialogStage);//把通知列表界面作为通知详情界面的父界面
            Scene scene = new Scene(noticeDetailContent);
            scene.getStylesheets().add(STYLES);
            noticeDetailDialogStage.setScene(scene);
            //把当前stage作为下个界面的父界面使用（通知详情附件）
            controller.setDialogStage(noticeDetailDialogStage);
            noticeDetailDialogStage.showAndWait();
        } catch (Exception ex) {
            DialogsUtils.errorAlert("system.malfunction");
            logger.error(FxmlUtils.getResourceBundle().getString("error.NoticeInfoController.showNoticeDetail"), ex);
        }
    }

    public void setDialogStage(Stage noticeListDialogStage) {
        this.noticeListDialogStage = noticeListDialogStage;
    }
}
