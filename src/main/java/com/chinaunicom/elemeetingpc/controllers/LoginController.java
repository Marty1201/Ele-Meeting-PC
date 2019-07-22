
package com.chinaunicom.elemeetingpc.controllers;

import com.chinaunicom.elemeetingpc.constant.StatusConstant;
import com.chinaunicom.elemeetingpc.modelFx.DictionaryModel;
import com.chinaunicom.elemeetingpc.service.LoginService;
import com.chinaunicom.elemeetingpc.utils.DialogsUtils;
import com.chinaunicom.elemeetingpc.utils.FxmlUtils;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import org.apache.commons.lang3.StringUtils;

/**
 * 登陆控制器
 * @author zhaojunfeng, chenxi
 */
public class LoginController {
    
    @FXML
    private TextField loginNameField;
    
    @FXML
    private TextField tpasswordfField;
    
    @FXML
    private BorderPane borderPaneMain;
    
    public static final String FXML_ORG_FXML = "/fxml/fxml_org.fxml";
    
    private DictionaryModel dictionaryModel;
    
    /**
     * 初始化controller
     */
    @FXML
    private void initialize(){
        
    }
    
    /**
     * 登录方法
     */
    @FXML
    public void login() throws ApplicationException, SQLException{
        String loginName = loginNameField.getText();
        String password = tpasswordfField.getText();
        String regiCode = dictionaryModel.queryByFieldIsNotNull();
        if(StringUtils.isBlank(loginName)){
            DialogsUtils.infoAlert("loginController.loginName.not.empty");
            return;
        }
        if(StringUtils.isBlank(password)){
            DialogsUtils.infoAlert("loginController.password.not.empty");
            return;
        }
        if(StringUtils.isBlank(regiCode)){
            DialogsUtils.infoAlert("loginController.regiCode.not.empty");
            return;
        }
        
        LoginService service = new LoginService();
        Map<String,String> map  = service.login(loginName, password, regiCode);
        if(StatusConstant.RESULT_CODE_SUCCESS.endsWith(map.get("code"))){
            //登录成功
            showFxmlOrg();
        }else{
            //登录失败
            DialogsUtils.loginAlert(map.get("desc"));
        }
    }
    
    /**
     * 展示注册码
     */
    @FXML
    public void showRegiCode() throws ApplicationException, SQLException{
        String regiCode = "";
        this.dictionaryModel = new DictionaryModel();
        regiCode = dictionaryModel.queryByFieldIsNotNull();
        DialogsUtils.registerCodeAlert(regiCode);
    }
    
    /**
     * 展示选择机构列表
     */
    private void showFxmlOrg() {
        try {
            FXMLLoader loader = FxmlUtils.getFXMLLoader(FXML_ORG_FXML);
            borderPaneMain.setCenter(loader.load());

            OrganInfoController organInfoController = loader.getController();
            //设置传参loginController
            organInfoController.setLoginController(this);
        } catch (IOException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * 替换BorderPane中间区域
     */
    public void setCenter(String fxmlPath){
        borderPaneMain.setCenter(FxmlUtils.fxmlLoader(fxmlPath));
    }
    
    public BorderPane getBorderPane(){
        return borderPaneMain;
    }
}
