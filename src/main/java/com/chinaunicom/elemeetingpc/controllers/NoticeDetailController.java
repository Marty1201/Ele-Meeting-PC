package com.chinaunicom.elemeetingpc.controllers;

import com.chinaunicom.elemeetingpc.modelFx.NoticeAccessoriesFx;
import com.chinaunicom.elemeetingpc.modelFx.NoticeInfoFx;
import com.chinaunicom.elemeetingpc.service.NoticeInfoService;
import com.chinaunicom.elemeetingpc.utils.FileDownloader;
import com.chinaunicom.elemeetingpc.utils.FxmlUtils;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Orientation;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.layout.FlowPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * 通知详情控制器.
 *
 * @author zhaojunfeng, chenxi
 */
public class NoticeDetailController {

    private static final Logger logger = LoggerFactory.getLogger(NoticeDetailController.class);

    @FXML
    private Label noticeTitle;

    @FXML
    private Label createTime;

    @FXML
    private Label author;

    @FXML
    private Label noticeType;

    @FXML
    private WebView noticeContent;

    @FXML
    private Label attachment;

    @FXML
    private FlowPane files;
    
    //通知详情界面
    private Stage noticeDetailDialogStage;

    public void initialize() {

    }

    /**
     * 重写初始化方法.
     *
     * @param notice 通知信息
     */
    public void initData(NoticeInfoFx notice) {
        //根据当前选择的会议通知id，调接口获取会议通知详情
        NoticeInfoService noticeInfoService = new NoticeInfoService();
        NoticeInfoFx noticeInfoFx = noticeInfoService.getNoticeDeatilFXById(notice.getNoticeId());
        List<NoticeAccessoriesFx> noticeAccessoriesFxList = noticeInfoService.getNoticeAccessoriesList();
        if (noticeInfoFx != null) {
            showNoticeDetail(noticeInfoFx, noticeAccessoriesFxList);
        }
    }

    /**
     * 会议通知界面布局.
     *
     * @param noticeInfoFx 会议通知信息
     * @param noticeAccessoriesFxList 会议通知附件列表
     */
    public void showNoticeDetail(NoticeInfoFx noticeInfoFx, List<NoticeAccessoriesFx> noticeAccessoriesFxList) {
        //通知标题
        noticeTitle.setText(noticeInfoFx.getNoticeTitle());
        noticeTitle.setTextOverrun(OverrunStyle.ELLIPSIS);
        //创建时间
        createTime.setText(noticeInfoFx.getCreateTime());
        //作者
        author.setText(noticeInfoFx.getUserName());
        //通知类型
        noticeType.setText(noticeInfoFx.getNoticeTypeName());
        //通知内容
        final WebEngine webEngine = noticeContent.getEngine();
        webEngine.loadContent(noticeInfoFx.getNoticeContent());
        //附件标题
        attachment.setText(FxmlUtils.getResourceBundle().getString("NoticeInfoController.accessories"));
        //通知附件
        files.setOrientation(Orientation.HORIZONTAL);
        files.setVgap(5.0);
        files.setHgap(5.0);
        for (int i = 0; i < noticeAccessoriesFxList.size(); i++) {
            Hyperlink link = new Hyperlink();
            link.setText(noticeAccessoriesFxList.get(i).getFileName());
            final String fileUrl = noticeAccessoriesFxList.get(i).getFilePath();
            final String fileName = noticeAccessoriesFxList.get(i).getFileName();
            link.setOnAction((ActionEvent e) -> {
                try {
                    //javafx webview 不支持读取pdf，因此实现文件展示只有两种途径：1、把文件下载到本地，2、用OpenPdfViewer打开，OpenPdfViewer相对麻烦，因此选用第一种方式
                    //配置FileChooser
                    FileChooser fileChooser = new FileChooser();
                    FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("PDF Files", "*.pdf", "*.PDF");
                    fileChooser.getExtensionFilters().add(extFilter);
                    fileChooser.setInitialFileName(fileName);
                    File dest = fileChooser.showSaveDialog(noticeDetailDialogStage);
                    //下载会议通知附件
                    FileDownloader fileDownloader = new FileDownloader(fileUrl);
                    URL url = new URL(fileUrl);
                    fileDownloader.downloadFile(url, new FileOutputStream(dest), 1024);
                } catch (IOException ex) {
                    ex.printStackTrace();
                    logger.error(ex.getCause().getMessage());
                }
            });
            files.getChildren().addAll(link);
        }
    }
    
    public void setDialogStage(Stage noticeDetailDialogStage) {
        this.noticeDetailDialogStage = noticeDetailDialogStage;
    }
}
