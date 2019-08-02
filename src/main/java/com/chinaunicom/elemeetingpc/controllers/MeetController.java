
package com.chinaunicom.elemeetingpc.controllers;

import com.chinaunicom.elemeetingpc.utils.FxmlUtils;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;

/**
 *会议主界面控制器
 * @author zhaojunfeng
 */
public class MeetController {
    
    //会议界面左侧列表
    public static final String FXML_LEFT_NAVIGATION = "/fxml/fxml_left_navigation.fxml";
    private static final Logger logger = LoggerFactory.getLogger(MeetController.class);
    private BorderPane borderPane;

    public void setBorderPane(BorderPane borderPane) {
        this.borderPane = borderPane;
    }
    
    /**
     * 初始化
     */
    @FXML
    public void initialize(){
        
    }
    
    /**
     * 首页（回退）
     */
    @FXML
    private void shouye(){
        try {            
            FXMLLoader loader = FxmlUtils.getFXMLLoader(FXML_LEFT_NAVIGATION);
            borderPane.setCenter(loader.load()); //将当前BorderPane中间区域加载为机构选择界面
            MeetLeftController meetLeftController = loader.getController(); //从loader中获取MeetLeftController
            meetLeftController.setBorderPane(borderPane);//设置传参当前的borderPane，以便在MeetLeftController中获取到当前BorderPane
        } catch (IOException e) {
            logger.error(e.getCause().getMessage());
        }
    }
    
}
