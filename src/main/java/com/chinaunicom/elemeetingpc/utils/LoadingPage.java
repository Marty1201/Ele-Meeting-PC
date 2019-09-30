package com.chinaunicom.elemeetingpc.utils;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.Background;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

/**
 * A custom loading page writen by pure java which is used wherever there is a heavy
 * computation occured, usually happen in database operation or server interface calls etc.
 *
 * @author chenxi 创建时间：2019-9-29 10:16:16
 */
public class LoadingPage {

    private Stage dialogStage;
    private ProgressIndicator progressIndicator;

    /**
     * Construct a Modal window.
     *
     * @param primaryStage the parent window/stage of this modal window
     */
    public LoadingPage(Window primaryStage) {
        dialogStage = new Stage();
        progressIndicator = new ProgressIndicator();
        //set parent ownership
        dialogStage.initOwner(primaryStage);
        dialogStage.initStyle(StageStyle.UNDECORATED);
        dialogStage.initStyle(StageStyle.TRANSPARENT);
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        progressIndicator.setProgress(-1.0f);
        VBox vBox = new VBox();
        vBox.setBackground(Background.EMPTY);
        vBox.getChildren().add(progressIndicator);
        vBox.setAlignment(Pos.CENTER);
        Scene scene = new Scene(vBox);
        scene.setFill(null);
        dialogStage.setScene(scene);
    }

    /**
     * Show loading page.
     *
     */
    public void showLoadingPage() {
        dialogStage.show();
    }

    /**
     * Close loading page.
     *
     */
    public void closeLoadingPage() {
        dialogStage.close();
    }

    public Stage getDialogStage() {
        return dialogStage;
    }
}
