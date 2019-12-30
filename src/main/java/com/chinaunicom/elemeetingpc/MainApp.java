package com.chinaunicom.elemeetingpc;

import com.chinaunicom.elemeetingpc.database.dutils.DbManager;
import com.chinaunicom.elemeetingpc.database.models.DictionaryInfo;
import com.chinaunicom.elemeetingpc.service.DictionaryInfoService;
import com.chinaunicom.elemeetingpc.utils.DialogsUtils;
import com.chinaunicom.elemeetingpc.utils.FxmlUtils;
import java.util.Locale;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MainApp extends Application {
    
    private static final Logger logger = LoggerFactory.getLogger(MainApp.class);
    
    public static final String FXML_LOGIN_FXML = "/fxml/fxml_login.fxml";
    
    public static final String STYLES = "/styles/Styles.css";
    
    private DictionaryInfoService dictionaryInfoService;

    @Override
    public void start(Stage stage) throws Exception {
        try {
            //国际化
            //Locale.setDefault(new Locale("en"));
            Locale.setDefault(new Locale("zh"));
            Pane root = FxmlUtils.fxmlLoader(FXML_LOGIN_FXML);
            Scene scene = new Scene(root);
            scene.getStylesheets().add(STYLES);
            stage.setTitle(FxmlUtils.getResourceBundle().getString("title.application"));
            stage.setScene(scene);
            //stage.setFullScreen(true); //窗口最大化，但是会弹出ESC提示
            stage.setMaximized(true); //窗口最大化
            stage.show();
            //database initialization
            DbManager.initDatabase();
            //Query registerCode from the database, if it doesn't exist, create a new one, else do nothing
            dictionaryInfoService = new DictionaryInfoService();
            if (StringUtils.isBlank(dictionaryInfoService.queryRegiCode())) {
                DictionaryInfo dic = new DictionaryInfo();
                dic.setRegisterCode(RandomStringUtils.randomAlphanumeric(6));
                dictionaryInfoService.saveOrUpdateDictionaryInfo(dic);//create a new register code when the app run the first time
            }
        } catch (Exception ex) {
            DialogsUtils.errorAlert("system.malfunction");
            logger.error(FxmlUtils.getResourceBundle().getString("error.MainApp.start"), ex);
        }
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
