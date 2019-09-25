package com.chinaunicom.elemeetingpc.controllers;

import com.chinaunicom.elemeetingpc.database.models.FileResource;
import com.chinaunicom.elemeetingpc.utils.FxmlUtils;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

/**
 * 文件详情控制器.
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

    private BorderPane borderPaneMain;

    /**
     * 默认文件页面布局初始化.
     *
     * @throws ApplicationException
     */
    public void initialize(FileResource fileInfo, String fileName) throws ApplicationException {
        pdfArea.prefHeightProperty().bind(mainView.heightProperty());//AnchorPane的高度绑定为VBox高度

        createFileTitle(fileInfo, fileName);
    }

    public void setBorderPane(BorderPane borderPaneMain) {
        this.borderPaneMain = borderPaneMain;
    }

    /**
     * 创建文件名称.
     *
     * @param fileInfo
     */
    public void createFileTitle(FileResource file, String fileName) {
        fileTitle.setText(fileName);//应该从议题文件表里获取文件名称
        fileTitle.setTextAlignment(TextAlignment.CENTER);
        fileTitle.setTextOverrun(OverrunStyle.ELLIPSIS);
        fileTitle.setWrapText(true);
        fileTitle.setAlignment(Pos.CENTER);
    }

    /**
     * 跳转文件列表界面.
     */
    @FXML
    public void showFxmlFiles() {
        try {
            FXMLLoader loader = FxmlUtils.getFXMLLoader(FXML_FILE);
            //borderPaneMain.getChildren().remove(borderPaneMain.getCenter());//清除当前BorderPane内中间区域的内容
            //borderPaneMain.getChildren().remove(borderPaneMain.getLeft());//清除当前BorderPane内左侧区域的内容
            borderPaneMain.setCenter(loader.load()); //将当前BorderPane中间区域加载为机构选择界面
            FileController fileController = loader.getController(); //从loader中获取MeetController
            fileController.setBorderPane(borderPaneMain);//设置传参当前的borderPane，以便在MeetController中获取到当前BorderPane
        } catch (IOException e) {
            logger.error(e.getCause().getMessage());
        }
    }
}
