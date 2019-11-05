package com.chinaunicom.elemeetingpc.controllers;

import com.chinaunicom.elemeetingpc.constant.GlobalStaticConstant;
import com.chinaunicom.elemeetingpc.database.models.FileResource;
import com.chinaunicom.elemeetingpc.database.models.IssueInfo;
import com.chinaunicom.elemeetingpc.utils.DialogsUtils;
import com.chinaunicom.elemeetingpc.utils.FileUtil;
import com.chinaunicom.elemeetingpc.utils.FxmlUtils;
import com.chinaunicom.elemeetingpc.utils.MQPlugin;
import com.chinaunicom.elemeetingpc.utils.OpenPdfViewer;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import java.io.IOException;
import java.sql.SQLException;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import org.apache.commons.lang3.StringUtils;

/**
 * 文件详情控制器，实现一个文件容器.
 *
 * @author chenxi 创建时间：2019-9-24 17:35:06
 */
public class FileDetailController {

    private static final Logger logger = LoggerFactory.getLogger(FileDetailController.class);

    //文件列表界面
    public static final String FXML_FILE = "/fxml/fxml_file.fxml";

    @FXML
    private VBox mainView;

    @FXML
    private AnchorPane pdfArea;

    @FXML
    private Label fileTitle;

    @FXML
    private OpenPdfViewer openPdfViewer;

    @FXML
    private AnchorPane topMenu;

    @FXML
    private ImageView followingState;

    @FXML
    private ImageView speakerState;

    @FXML
    private ImageView backState;

    //文件所属议题，从MeetController里传过来的，在“返回”功能里回传给FileController
    private IssueInfo issueInfo;

    //同步阅读时主讲切换文件，跟读需返回文件列表然后重新打开主讲浏览的文件，需要用到FileController
    private FileController fileController;

    //文件路径
    private String filePath;

    //加载的文件实体
    private FileResource fileInfo;

    private String fileName;

    private BorderPane borderPaneMain;

    private MQPlugin mQPlugin;

    /**
     * 默认文件页面布局初始化.
     *
     * @param fileInfo 文件
     * @param fileName 文件名称
     * @param issueInfo 文件所属议题
     * @param fileController 文件列表控制器
     * @param pageIndex 当前页码
     */
    public void initialize(FileResource fileInfo, String fileName, IssueInfo issueInfo, FileController fileController, int pageIndex) {
        this.fileInfo = fileInfo;
        this.fileName = fileName;
        this.issueInfo = issueInfo;
        this.fileController = fileController;

        pdfArea.prefHeightProperty().bind(mainView.heightProperty());//AnchorPane的高度绑定为VBox高度，让openPdfViewer高度撑满
        createFileTitle(this.fileName);
        //获取文件路径
        filePath = GlobalStaticConstant.GLOBAL_FILE_DISK + "\\" + GlobalStaticConstant.GLOBAL_FILE_FOLDER + "\\" + StringUtils.substringAfterLast(fileInfo.getFilePath(), "/");
        //如果文件存在，打开文件
        if (FileUtil.isFileExist(filePath)) {
            //支持打开加密的PDF文件
            if (StringUtils.isNotBlank(fileInfo.getPassword())) {
                openPdfViewer.loadPdf(filePath, fileInfo.getPassword(), pageIndex);
            } else {
                openPdfViewer.loadPdf(filePath, pageIndex);
            }
        } else {
            DialogsUtils.infoAlert("FileDetailController.fileNotExist");
        }
        //如果在跟读情况下还需按照正在跟读的状态来重载文件阅读界面
        if (GlobalStaticConstant.GLOBAL_ISFOLLOWINGCLICKED == true) {
            try {
                GlobalStaticConstant.GLOBAL_ISFOLLOWINGCLICKED = false;
                updateFileDetailView();
            } catch (IOException ex) {
                logger.error(ex.getCause().getMessage());
            }
        }
        openPdfViewer.setFileDetaiController(this);//把FileDetailController传到OpenPdfViewer里面使用
    }

    /**
     * 创建文件名称.
     *
     * @param fileName 议题文件表中文件名称
     */
    public void createFileTitle(String fileName) {
        fileTitle.setText(fileName);
        fileTitle.setTextAlignment(TextAlignment.CENTER);
        fileTitle.setTextOverrun(OverrunStyle.ELLIPSIS);
        fileTitle.setWrapText(true);
        fileTitle.setAlignment(Pos.CENTER);
    }

    /**
     * 返回按钮，跳转文件列表界面，回传议题信息用于文件列表界面数据加载，同时关闭当前打开的PDF文件.
     *
     * @throws
     * com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException
     * @throws java.sql.SQLException
     */
    @FXML
    public void showFxmlFileList() throws ApplicationException, SQLException {
        try {
            FXMLLoader loader = FxmlUtils.getFXMLLoader(FXML_FILE);
            borderPaneMain.getChildren().remove(borderPaneMain.getCenter());//清除当前BorderPane内中间区域的内容
            borderPaneMain.setCenter(loader.load()); //将当前BorderPane中间区域加载为文件列表界面
            FileController localFileController = loader.getController(); //从loader中获取FileController
            localFileController.setBorderPane(borderPaneMain);//设置传参当前的borderPane，以便在FileController中获取到当前BorderPane
            localFileController.initData(issueInfo);//初始化文件列表，回传议题信息
            localFileController.setMQPlugin(mQPlugin);//回传mq信息
            openPdfViewer.closePdf();//关闭当前打开的pdf文件
        } catch (IOException e) {
            logger.error(e.getCause().getMessage());
        }
    }

    /**
     * 处理主讲操作.
     */
    @FXML
    public void handleSpeaking() {
        // to be implemented
    }

    /**
     * 处理跟读操作, 跟读分为两种状态：1、未跟读，2、跟读，系统常量GLOBAL_ISFOLLOWINGCLICKED=false代表未跟读
     * GLOBAL_ISFOLLOWINGCLICKED=true代表跟读，使用GLOBAL_ISFOLLOWINGCLICKED来对文件详情界面进行更新并判断是否
     * 需要处理消息。系统常量GLOBAL_ISSTARTCONSUMING代表是否开启了消费消息功能（默认是false），也就是说一旦开启
     * 消费消息GLOBAL_ISSTARTCONSUMING=true（程序运行时只开启一次），客户端将一直接收消息，需要通过代码逻辑来判断
     * 是否需要处理消息.
     * @throws java.io.IOException
     */
    @FXML
    public void handleFollowing() throws IOException {
        //如果用户点击了跟读按钮（按钮当前状态是false，未开始跟读）
        if (GlobalStaticConstant.GLOBAL_ISFOLLOWINGCLICKED == false) {
            updateFileDetailView();
            //如果第一次点击跟读按钮，开启消费消息，避免重复开启消息而导致报错
            if (GlobalStaticConstant.GLOBAL_ISSTARTCONSUMING == false) {
                if (mQPlugin != null) {
                    mQPlugin.consumeMessage();
                } else {
                    //创建mq失败
                    DialogsUtils.errorAlert("server.connection.error");
                }
            }
        } else { //如果用户点击了取消跟读按钮（按钮当前状态是true，正在跟读）
            updateFileDetailView();
        }
    }

    /**
     * 根据跟读状态（开始跟读/取消跟读）来更新文件阅读界面.
     * @throws java.io.IOException
     */
    public void updateFileDetailView() throws IOException {
        //用户开始跟读，更新文件阅读界面
        if (GlobalStaticConstant.GLOBAL_ISFOLLOWINGCLICKED == false) {
            //改变跟读图标
            Image followerIcon = new Image(getClass().getResourceAsStream("/images/stop_following.png"));
            followingState.setImage(followerIcon);
            followingState.setFitHeight(100.0);
            followingState.setFitWidth(120.0);
            //disable主讲图标
            Image speakerIcon = new Image(getClass().getResourceAsStream("/images/apply_speaker_disable.png"));
            speakerState.setImage(speakerIcon);
            speakerState.setFitHeight(100.0);
            speakerState.setFitWidth(120.0);
            speakerState.setDisable(true);
            //disable返回图标
            Image backIcon = new Image(getClass().getResourceAsStream("/images/back_disable.png"));
            backState.setImage(backIcon);
            backState.setFitHeight(54.0);
            backState.setFitWidth(70.0);
            backState.setDisable(true);
            //主要问题出现在当主讲和跟读不在同一个文件，当updateFileDetailView()被call的时候，OpenPdfViewer里面的createPdfImage还未生成当前页面，
            //如果此时对pagination和scroller进行操作，得到的对象就是空的，因此需要等当前线程结束后，再延迟执行以下两个任务
            //另外好的习惯是当一个JavaFX线程尝试去更新另一个FX线程的UI时, 必须要用Platform.runLater，效果是当前线程执行完以后再执行另一个线程
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    //隐藏OpenPdfViewer工具栏
                    openPdfViewer.setZoomOptions(false);
                    //disable手动翻页
                    openPdfViewer.isLockPagination(true);
                    //disable滚动条
                    openPdfViewer.isLockScroller(true);
                }
            });
            //用户已经点击了跟读按钮，设置按钮状态为true
            GlobalStaticConstant.GLOBAL_ISFOLLOWINGCLICKED = true;
        } else { //用户取消跟读，更新文件阅读界面
            //改变跟读图标
            Image followerIcon = new Image(getClass().getResourceAsStream("/images/follow.png"));
            followingState.setImage(followerIcon);
            followingState.setFitHeight(100.0);
            followingState.setFitWidth(120.0);
            //开启主讲图标
            Image speakerIcon = new Image(getClass().getResourceAsStream("/images/apply_speaker.png"));
            speakerState.setImage(speakerIcon);
            speakerState.setFitHeight(100.0);
            speakerState.setFitWidth(120.0);
            speakerState.setDisable(false);
            //开启返回图标
            Image backIcon = new Image(getClass().getResourceAsStream("/images/back.png"));
            backState.setImage(backIcon);
            backState.setFitHeight(54.0);
            backState.setFitWidth(70.0);
            backState.setDisable(false);
            //主要问题出现在当主讲和跟读不在同一个文件，当updateFileDetailView()被call的时候，OpenPdfViewer里面的createPdfImage还未生成当前页面，
            //如果此时对pagination和scroller进行操作，得到的对象就是空的，因此需要等当前线程结束后，再延迟执行以下两个任务
            //另外好的习惯是当一个JavaFX线程尝试去更新另一个FX线程的UI时, 必须要用Platform.runLater，效果是当前线程执行完以后再执行另一个线程
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    //开启OpenPdfViewer工具栏
                    openPdfViewer.setZoomOptions(true);
                    //disable手动翻页
                    openPdfViewer.isLockPagination(false);
                    //disable滚动条
                    openPdfViewer.isLockScroller(false);
                }
            });
            //用户已经点击了取消跟读按钮，设置按钮状态为false
            GlobalStaticConstant.GLOBAL_ISFOLLOWINGCLICKED = false;
        }
    }

    public FileResource getFileInfo() {
        return fileInfo;
    }

    public OpenPdfViewer getOpenPdfViewer() {
        return openPdfViewer;
    }

    public FileController getFileController() {
        return fileController;
    }

    public AnchorPane getTopMenu() {
        return topMenu;
    }

    public VBox getMainView() {
        return mainView;
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
