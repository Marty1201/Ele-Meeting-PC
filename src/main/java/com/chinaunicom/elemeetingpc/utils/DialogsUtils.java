
package com.chinaunicom.elemeetingpc.utils;

import java.util.Optional;
import java.util.ResourceBundle;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextArea;


/**
 * Dialog windows util.
 * @author chenxi
 * 创建时间：2019-6-20 18:06:06
 */
public class DialogsUtils {
    static ResourceBundle bundle = FxmlUtils.getResourceBundle();
    
    /**
     * Show about alert dialog windows.
     */
    public static void dialogAboutApp(){
        Alert informationAlert = new Alert(Alert.AlertType.INFORMATION);
        informationAlert.setTitle(bundle.getString("about.title"));
        informationAlert.setHeaderText(bundle.getString("about.header"));
        informationAlert.setContentText(bundle.getString("about.content"));
        informationAlert.showAndWait();
    }
    
    /**
     * Show error alert dialog windows.
     */
    public static void errorAlert(String error){
        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle(bundle.getString("error.title"));
        errorAlert.setHeaderText(bundle.getString("error.header"));
        TextArea textArea = new TextArea(error);
        errorAlert.getDialogPane().setContent(textArea);
        errorAlert.showAndWait();
    } 
    
    /**
     * Show info alert dialog windows.
     */
    public static void infoAlert(String infoString){
        Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
        infoAlert.setTitle(bundle.getString("info.title"));
        infoAlert.setHeaderText(bundle.getString("info.title"));
        TextArea textArea = new TextArea(bundle.getString(infoString));
        infoAlert.getDialogPane().setContent(textArea);
        infoAlert.showAndWait();
    }
    
    /**
     * Show custom info alert dialog windows.
     */
    public static void customInfoAlert(String infoString) {
        Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
        infoAlert.setTitle(bundle.getString("info.title"));
        infoAlert.setHeaderText(bundle.getString("info.title"));
        TextArea textArea = new TextArea(infoString);
        infoAlert.getDialogPane().setContent(textArea);
        infoAlert.showAndWait();
    }
    
    /**
     * Show confirmation alert dialog windows.
     * @return answer: click ok button return true, otherwise return false.
     */
    public static Boolean confirmationAlert() {
        boolean answer = false;
        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle(bundle.getString("info.title"));
        confirmationAlert.setHeaderText(bundle.getString("confirm.header"));
        confirmationAlert.setContentText(bundle.getString("confirm.content"));
        Optional<ButtonType> result = confirmationAlert.showAndWait();
        if(result.get() == ButtonType.OK){
            answer = true;
        }
        return answer;
    }
    
    /**
     * Show registerCode alert dialog windows.
     */
    public static void registerCodeAlert(String infoString) {
        Alert infoAlert = new Alert(Alert.AlertType.INFORMATION);
        infoAlert.setTitle(bundle.getString("info.title"));
        infoAlert.setHeaderText(bundle.getString("loginController.regiCode"));
        TextArea textArea = new TextArea(infoString);
        infoAlert.getDialogPane().setContent(textArea);
        infoAlert.showAndWait();
    }
    
    /**
     * Show login failed alert dialog windows.
     */
    public static void loginAlert(String infoString) {
        Alert infoAlert = new Alert(Alert.AlertType.ERROR);
        infoAlert.setTitle(bundle.getString("error.title"));
        infoAlert.setHeaderText(bundle.getString("error.title"));
        TextArea textArea = new TextArea(infoString);
        infoAlert.getDialogPane().setContent(textArea);
        infoAlert.showAndWait();
    }
}