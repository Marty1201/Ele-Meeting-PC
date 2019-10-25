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
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;
import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

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
    
    //文件所属议题，从MeetController里传过来的，在“返回”功能里回传给FileController
    private IssueInfo issueInfo;
    
    //同步阅读时主讲切换文件，跟读需返回文件列表然后重新打开主讲浏览的文件
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
     * @param fileController
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
     * @throws com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException
     * @throws java.sql.SQLException
     */
    @FXML
    public void showFxmlFileList() throws ApplicationException, SQLException {
        try {
            FXMLLoader loader = FxmlUtils.getFXMLLoader(FXML_FILE);
            borderPaneMain.getChildren().remove(borderPaneMain.getCenter());//清除当前BorderPane内中间区域的内容
            borderPaneMain.setCenter(loader.load()); //将当前BorderPane中间区域加载为文件列表界面
            FileController fileController = loader.getController(); //从loader中获取FileController
            fileController.setBorderPane(borderPaneMain);//设置传参当前的borderPane，以便在FileController中获取到当前BorderPane
            fileController.initData(issueInfo);//初始化文件列表，回传议题信息
            //提取pdf内容
            PDDocument doc = openPdfViewer.getPdf();
            PDFTextStripper pdfStripper = new PDFTextStripper();
            String content = pdfStripper.getText(doc);
            System.out.println("doc is close = " + doc.getDocument().isClosed());
            openPdfViewer.closePdf();//关闭当前打开的pdf文件
            System.out.println("doc is close = " + doc.getDocument().isClosed());
        } catch (IOException e) {
            e.printStackTrace();
            logger.error(e.getCause().getMessage());
        }
    }
    
     /**
     * 开始主讲.
     */
    @FXML
    public void startSpeaking() {
        // to be implemented
    }
    
    /**
     * 结束主讲.
     */
    @FXML
    public void stopSpeaking() {
        // to be implemented
    }
    
    /**
     * 开始跟读.
     */
    @FXML
    public void startFollowing() throws IOException {
        mQPlugin.consumeMessage();
    }
    
    /**
     * 取消跟读.
     */
    @FXML
    public void stopFollowing() throws IOException {
        // to be implemented
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
    
    public PDDocument getPdf() throws IOException {
        return openPdfViewer.getPdf();
    }
    
    public void setBorderPane(BorderPane borderPaneMain) {
        this.borderPaneMain = borderPaneMain;
    }
    
    public void setMQPlugin(MQPlugin mQPlugin) {
        this.mQPlugin = mQPlugin;
    }
}
