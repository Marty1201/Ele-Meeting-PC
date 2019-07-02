/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chinaunicom.elemeetingpc.controllers;

import com.chinaunicom.elemeetingpc.constant.StatusConstant;
import com.chinaunicom.elemeetingpc.service.LoginService;
import com.chinaunicom.elemeetingpc.utils.DialogsUtils;
import java.util.Map;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
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
            DialogsUtils.infoAlert("登录账号不能为空！");
            return;
        }
        if(StringUtils.isBlank(password)){
            DialogsUtils.infoAlert("密码不能为空！");
            return;
        }
        if(StringUtils.isBlank(validaNum)){
            DialogsUtils.infoAlert("验证码不能为空！");
            return;
        }
        
        LoginService service = new LoginService();
        Map<String,String> map  = service.login(loginName, password, validaNum);
        if(StatusConstant.RESULT_CODE_SUCCESS.endsWith(map.get("code"))){
            //登录成功
            okLogin=true;
        }else{
            okLogin=false;
            DialogsUtils.infoAlert(map.get("desc"));
        }
    }
    
    /**
     * 获取验证码
     */
    @FXML
    public void validaNum(){
        System.out.println("获取验证码");
    }
            
    
}
