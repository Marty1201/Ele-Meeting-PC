package com.chinaunicom.elemeetingpc;

import com.chinaunicom.elemeetingpc.utils.FxmlUtils;
import java.util.Locale;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;


public class MainApp extends Application {
    
    public static final String FXML_LOGIN_FXML = "/fxml/fxml_login.fxml";
    public static final String STYLES = "/styles/Styles.css";

    @Override
    public void start(Stage stage) throws Exception {
        
        //Locale.setDefault(new Locale("en"));
        Locale.setDefault(new Locale("zh"));
        
        Pane root = FxmlUtils.fxmlLoader(FXML_LOGIN_FXML);
        
        Scene scene = new Scene(root);
        scene.getStylesheets().add(STYLES);
        //applies styling globally to all scenes owned by an application
        //Application.setUserAgentStylesheet(getClass().getResource("/styles/Styles.css").toExternalForm());
        
        stage.setTitle(FxmlUtils.getResourceBundle().getString("title.application"));
        stage.setScene(scene);
        stage.show();
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
