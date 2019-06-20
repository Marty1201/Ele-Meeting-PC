
package com.chinaunicom.elemeetingpc.utils;

import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

/**
 * Initialize and load the project's first fxml template, set localization
 * for the project.
 * @author chenxi
 * 创建时间：2019-6-20 9:44:25
 */
public class FxmlUtils {
    
    /**
     * Load the project's first fxml template, set localization
     * 
     * @param fxmlPath the path of the fxml template
     * @return Pane
     */
    public static Pane fxmlLoader(String fxmlPath) {
        FXMLLoader loader = new FXMLLoader(FxmlUtils.class.getResource(fxmlPath));
        loader.setResources(getResourceBundle());
        try{
            return loader.load();
        } catch(Exception e){
            DialogsUtils.errorAlert(e.getMessage());
        }
        return null;
    }
    
    /**
     * Load the locale language properties file
     *
     * @return ResourceBundle
     */
    public static ResourceBundle getResourceBundle() {
        return ResourceBundle.getBundle("bundles.messages");
    }
    
    /**
     * Return the FXMLLoader
     *
     * @return FXMLLoader
     */
    public static FXMLLoader getFXMLLoader(String fxmlPath) {
        FXMLLoader loader = new FXMLLoader(FxmlUtils.class.getResource(fxmlPath));
        loader.setResources(getResourceBundle());
        return loader;
    }
}
