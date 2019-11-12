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
import java.util.ResourceBundle;
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
 * 文件详情控制器，实现一个文件容器，FileDetailController，MQPlugin和OpenPdfViewer共同实现了
 * 文件的展示和同步阅读，FileDetailController面对的对象是用户，MQPlugin负责消息的发送与接收，
 * OpenPdfViewer面对的对象是文件本身.
 *
 * @author chenxi 创建时间：2019-9-24 17:35:06
 */
public class FileDetailController {

    private static final Logger logger = LoggerFactory.getLogger(FileDetailController.class);

    static ResourceBundle bundle = FxmlUtils.getResourceBundle();

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

    //文件的真实名称
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
            DialogsUtils.infoWarning("FileDetailController.fileNotExist");
        }
        //如果在同步阅读情况下还需按照正在跟读/主讲的状态来重载文件阅读界面
        if (GlobalStaticConstant.GLOBAL_ISFOLLOWINGCLICKED == true) {
            try {
                GlobalStaticConstant.GLOBAL_ISFOLLOWINGCLICKED = false;
                updateFileDetailView(GlobalStaticConstant.GLOBAL_FOLLOWING);
            } catch (IOException ex) {
                logger.error(ex.getCause().getMessage());
            }
        }
        if (GlobalStaticConstant.GLOBAL_ISSPEAKINGCLICKED == true) {
            try {
                GlobalStaticConstant.GLOBAL_ISSPEAKINGCLICKED = false;
                updateFileDetailView(GlobalStaticConstant.GLOBAL_SPEAKING);
            } catch (IOException ex) {
                logger.error(ex.getCause().getMessage());
            }
        }
        openPdfViewer.setFileDetaiController(this);//把FileDetailController传到OpenPdfViewer里面使用
        openPdfViewer.setMQPlugin(mQPlugin);//把MQPlugin传到OpenPdfViewer里面使用
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
     * 处理主讲操作，主讲分为两种状态：1、申请主讲，2、取消主讲，系统常量GLOBAL_ISSPEAKINGCLICKED=false代表还未主讲并准备申请主讲
     * GLOBAL_ISSPEAKINGCLICKED=true代表已经主讲并准备取消主讲，申请主讲前需要先开启consumer.
     *
     * @throws IOException
     * @throws Exception
     */
    @FXML
    public void handleSpeaking() throws IOException, Exception {
        ////如果consumer未开启，先开启consumer（只开一次 & 避免出现如果主讲人已存在，当前主讲人无法接收到新申请主讲人发出的applyForPresenter的消息）
        initConsumer();
        //用户点击申请主讲按钮
        if (GlobalStaticConstant.GLOBAL_ISSPEAKINGCLICKED == false) {
            if (DialogsUtils.confirmationAlert(bundle.getString("MQPlugin.applySpeaker.header"), bundle.getString("MQPlugin.applySpeaker.content"))) {
                //用户点击确认主讲按钮，对界面进行更新
                updateFileDetailView(GlobalStaticConstant.GLOBAL_SPEAKING);
                //发送申请主讲消息
                String command = "\"command\":\"" + GlobalStaticConstant.GLOBAL_APPLYFORPRESENTER;
                String userId = "\",\"userid\":\"" + GlobalStaticConstant.GLOBAL_ORGANINFO_OWNER_USERID;
                String organName = "\",\"PAMQOrganizationIDName\":\"" + GlobalStaticConstant.GLOBAL_ORGANINFO_ORGANIZATIONNAME;
                String message = "{" + command + userId + organName + "\"}";
                mQPlugin.publishMessage(message);
            }
        } else {
            //用户点击取消主讲按钮，对界面进行更新
            updateFileDetailView(GlobalStaticConstant.GLOBAL_SPEAKING);
            DialogsUtils.infoAlert("MQPlugin.speakingStop");
            //发送取消主讲消息
            String command = "\"command\":\"" + GlobalStaticConstant.GLOBAL_GIVEUPPRESENTER;
            String userId = "\",\"userid\":\"" + GlobalStaticConstant.GLOBAL_ORGANINFO_OWNER_USERID;
            String organName = "\",\"PAMQOrganizationIDName\":\"" + GlobalStaticConstant.GLOBAL_ORGANINFO_ORGANIZATIONNAME;
            String message = "{" + command + userId + organName + "\"}";
            mQPlugin.publishMessage(message);
        }
    }

    /**
     * 处理跟读操作, 跟读分为两种状态：1、未跟读，2、跟读，系统常量GLOBAL_ISFOLLOWINGCLICKED=false代表未跟读
     * GLOBAL_ISFOLLOWINGCLICKED=true代表跟读，使用GLOBAL_ISFOLLOWINGCLICKED来对文件详情界面进行更新并判断是否
     * 需要处理消息，跟读前需要先开启consumer.
     *
     * @throws java.io.IOException
     */
    @FXML
    public void handleFollowing() throws IOException {
        //如果consumer未开启，先开启consumer（只开一次）
        initConsumer();
        //用户选择跟读并对界面进行更新处理
        if (GlobalStaticConstant.GLOBAL_ISFOLLOWINGCLICKED == false) {
            DialogsUtils.infoAlert("MQPlugin.followingStart");
        } else {
            DialogsUtils.infoAlert("MQPlugin.followingStop");
        }
        updateFileDetailView(GlobalStaticConstant.GLOBAL_FOLLOWING);
    }

    /**
     * 启动consumer，开启消费消息，系统常量GLOBAL_ISSTARTCONSUMING代表是否开启了消费消息功能（默认是false），也就是说一旦开启
     * 消费消息GLOBAL_ISSTARTCONSUMING=true（程序运行时只开启一次），客户端将一直接收消息，需要通过代码逻辑来判断是否需要处理消息.
     */
    public void initConsumer() {
        if (GlobalStaticConstant.GLOBAL_ISSTARTCONSUMING == false) {
            if (mQPlugin != null) {
                try {
                    mQPlugin.consumeMessage();
                    GlobalStaticConstant.GLOBAL_ISSTARTCONSUMING = true; //set start consuming flag true to avoid the error of redeploy consumeMessage method
                } catch (IOException ex) {
                    logger.error(ex.getCause().getMessage());
                }
            } else {
                //创建mq失败
                DialogsUtils.errorAlert("server.connection.error");
            }
        }
    }

    /**
     * 根据用户的操作GLOBAL_FOLLOWING/GLOBAL_SPEAKING（跟读/主讲）和GLOBAL_ISFOLLOWINGCLICKED/GLOBAL_ISSPEAKINGCLICKED（跟读/主讲按钮状态）来更新文件阅读界面.
     *
     * @param action 根据用户操作对界面进行更新
     * @throws java.io.IOException
     */
    public void updateFileDetailView(String action) throws IOException {
        //用户选择跟读
        if (StringUtils.equalsIgnoreCase(action, GlobalStaticConstant.GLOBAL_FOLLOWING)) {
            //用户点击了跟读按钮（按钮当前状态是false，未开始跟读），更新文件阅读界面
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
            } else { //用户点击了取消跟读按钮（按钮当前状态是true，正在跟读），更新文件阅读界面
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
        //用户选择主讲
        if (StringUtils.equalsIgnoreCase(action, GlobalStaticConstant.GLOBAL_SPEAKING)) {
            //用户点击了主讲按钮（按钮当前状态是false，未开始主讲），更新文件阅读界面
            if (GlobalStaticConstant.GLOBAL_ISSPEAKINGCLICKED == false) {
                //改变主讲图标
                Image speakerIcon = new Image(getClass().getResourceAsStream("/images/stop_speaker.png"));
                speakerState.setImage(speakerIcon);
                speakerState.setFitHeight(100.0);
                speakerState.setFitWidth(120.0);
                //disable跟读图标
                Image followerIcon = new Image(getClass().getResourceAsStream("/images/follow_disable.png"));
                followingState.setImage(followerIcon);
                followingState.setFitHeight(100.0);
                followingState.setFitWidth(120.0);
                followingState.setDisable(true);
                //用户已经点击了开始主讲按钮，设置按钮状态为true
                GlobalStaticConstant.GLOBAL_ISSPEAKINGCLICKED = true;
            } else { //用户点击了取消主讲按钮（按钮当前状态是true，正在主讲），更新文件阅读界面
                //改变主讲图标
                Image speakerIcon = new Image(getClass().getResourceAsStream("/images/apply_speaker.png"));
                speakerState.setImage(speakerIcon);
                speakerState.setFitHeight(100.0);
                speakerState.setFitWidth(120.0);
                //开启跟读图标
                Image followerIcon = new Image(getClass().getResourceAsStream("/images/follow.png"));
                followingState.setImage(followerIcon);
                followingState.setFitHeight(100.0);
                followingState.setFitWidth(120.0);
                followingState.setDisable(false);
                //用户已经点击了取消主讲按钮，设置按钮状态为false
                GlobalStaticConstant.GLOBAL_ISSPEAKINGCLICKED = false;
            }
        }
    }

    public FileResource getFileInfo() {
        return fileInfo;
    }

    public String getFileName() {
        return fileName;
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
