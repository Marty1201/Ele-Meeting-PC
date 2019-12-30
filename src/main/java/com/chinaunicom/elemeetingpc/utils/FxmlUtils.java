
package com.chinaunicom.elemeetingpc.utils;

import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;

/**
 * Provides common methods for loading and localizing project's fxml templates.
 * @author chenxi
 * 创建时间：2019-6-20 9:44:25
 */
public class FxmlUtils {
    
    /**
     * Load and open the project's fxml templates, set localization.
     * 
     * @param fxmlPath the path of the fxml template
     * @return Pane
     * @throws Exception
     */
    public static Pane fxmlLoader(String fxmlPath) throws Exception {
        FXMLLoader loader = new FXMLLoader(FxmlUtils.class.getResource(fxmlPath));
        loader.setResources(getResourceBundle());
        return loader.load();
    }
    
    /**
     * Load the locale language properties file.
     *
     * @return ResourceBundle
     * @throws Exception
     */
    public static ResourceBundle getResourceBundle() {
        return ResourceBundle.getBundle("bundles.messages");
    }
    
    /**
     * Return a FXMLLoader type loader with fxml templates and localization
     * set.
     *
     * @return FXMLLoader
     * @throws Exception
     */
    public static FXMLLoader getFXMLLoader(String fxmlPath) {
        FXMLLoader loader = new FXMLLoader(FxmlUtils.class.getResource(fxmlPath));
        loader.setResources(getResourceBundle());
        return loader;
    }
}
