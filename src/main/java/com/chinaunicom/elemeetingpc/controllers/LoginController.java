/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chinaunicom.elemeetingpc.controllers;

import com.chinaunicom.elemeetingpc.constant.StatusConstant;
import com.chinaunicom.elemeetingpc.service.LoginService;
import com.chinaunicom.elemeetingpc.utils.DialogsUtils;
import com.chinaunicom.elemeetingpc.utils.FxmlUtils;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import org.apache.commons.lang3.StringUtils;

/**
 *
 * @author zhaojunfeng
 */
public class LoginController {
    
    @FXML
    private TextField loginNameField;
    
    @FXML
    private TextField tpasswordfField;
    
    @FXML
    private PasswordField mpasswordField;
    
    @FXML
    private TextField validaNumField;
    
    @FXML
    private BorderPane borderPaneMain;
    
    public static final String FXML_ORG_FXML = "/fxml/fxml_org.fxml";
    
    private boolean okLogin=false;

    public boolean isOkLogin() {
        return okLogin;
    }
    
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
    public void login(){
        String loginName = loginNameField.getText();
        String password = tpasswordfField.getText();
        //String validaNum = validaNumField.getText();
        String validaNum="123456";
        if(StringUtils.isBlank(loginName)){
            DialogsUtils.infoAlert("loginController.loginName.not.empty");
            return;
        }
        if(StringUtils.isBlank(password)){
            DialogsUtils.infoAlert("loginController.password.not.empty");
            return;
        }
        if(StringUtils.isBlank(validaNum)){
            DialogsUtils.infoAlert("loginController.validaNum.not.empty");
            return;
        }
        
        LoginService service = new LoginService();
        Map<String,String> map  = service.login(loginName, password, validaNum);
        if(StatusConstant.RESULT_CODE_SUCCESS.endsWith(map.get("code"))){
            //登录成功
            okLogin=true;
            //DialogsUtils.infoAlert("loginController.success");
            showFxmlOrg();
        }else{
            okLogin=false;
            DialogsUtils.infoAlert("loginController.fail");
        }
    }
    
    /**
     * 获取验证码
     */
    @FXML
    public void validaNum(){
        System.out.println("获取验证码");
    }
    
    /**
     * 展示选择机构列表
     */
    private void showFxmlOrg(){ 
        try {
            //setCenter(FXML_ORG_FXML);
            FXMLLoader loader = FxmlUtils.getFXMLLoader(FXML_ORG_FXML);            
            borderPaneMain.setCenter(loader.load());
            
            OrganInfoController organInfoController = loader.getController();
            //设置传参loginController
            organInfoController.setLoginController(this);
        } catch (IOException ex) {
            Logger.getLogger(LoginController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void setCenter(String fxmlPath){
        borderPaneMain.setCenter(FxmlUtils.fxmlLoader(fxmlPath));
    }
    
    public BorderPane getBorderPane(){
        return borderPaneMain;
    }
            
    
}
