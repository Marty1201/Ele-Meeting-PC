/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chinaunicom.elemeetingpc.controllers;

import com.chinaunicom.elemeetingpc.modelFx.ResetPasswordModel;
import com.chinaunicom.elemeetingpc.modelFx.UserInfoModel;
import com.chinaunicom.elemeetingpc.utils.DialogsUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * 密码修改
 * @author zhaojunfeng
 */
public class ResetPasswordController {
    @FXML
    private TextField oldPasswordField;
    @FXML
    private TextField newPasswordField;
    @FXML
    private TextField reNewPasswordField;
    
    @FXML
    private void initialize(){
        System.out.println("密码修改界面");
    }
    
    private Stage dialogStage;
    private ResetPasswordModel resetPasswordModel;
    private UserInfoModel userInfoModel;
    
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
    
    @FXML
    private void handOK(){
        if(isInputValid()){
            String oldPassword = oldPasswordField.getText();
            String newPassword = newPasswordField.getText();
            userInfoModel = new UserInfoModel();
            String infoString = userInfoModel.resetPassword(oldPassword, newPassword);
            DialogsUtils.infoAlert(infoString);
            dialogStage.close();
        }
    }
    
    @FXML
    private void handCancel(){
        dialogStage.close();
    }
    
    private boolean isInputValid(){
        boolean flag=false;
        String errorMessage="";
        
        if(oldPasswordField.getText()==null || oldPasswordField.getText().length()==0){
            errorMessage+="旧密码不能为空！\n";
        }
        if(newPasswordField.getText()==null || newPasswordField.getText().length()==0){
            errorMessage+="新密码不能为空！\n";
        }else{
            
        }     
        if(reNewPasswordField.getText()==null || reNewPasswordField.getText().length()==0){
            errorMessage+="确认密码不能为空！\n";
        }else{
            if(!reNewPasswordField.getText().equals(newPasswordField.getText())){
                errorMessage+="新密码两次输入不一致！\n";
            }   
        }
                
        if(errorMessage.length()==0){
            flag=true;
        }else{
            Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
            infoAlert.setTitle("提示");
            TextArea textArea = new TextArea(errorMessage);
            infoAlert.getDialogPane().setContent(textArea);
            infoAlert.showAndWait();
        }
        return flag;
    }
    
}
