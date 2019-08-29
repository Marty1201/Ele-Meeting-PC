
package com.chinaunicom.elemeetingpc.controllers;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.OverrunStyle;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;


/**
 *
 * @author chenxi
 * 创建时间：2019-8-29 15:31:28
 */
public class FileController {
	    
    @FXML
    private TextField issueTitle;
    
    @FXML
    private VBox fileSection;
	
    /**
     * 文件页面布局初始化.
     *
     * @throws ApplicationException
     */
    public void initialize() {
        //main meeting name
        issueTitle.setText("Issue Title");
        issueTitle.setAlignment(Pos.CENTER);
        //flowpane
        FlowPane filesFlowPane = new FlowPane(Orientation.HORIZONTAL, 10.0, 10.0);
        filesFlowPane.setPadding(new Insets(5.0, 3.0, 5.0, 3.0));
        //childMeetingFlowPane.setPrefWrapLength(350.0);//it seems this method will not behave inside the vbox?
        //childMeetingFlowPane.setPrefSize(50.0, 50.0);
        filesFlowPane.setStyle("-fx-background-color: #ebebeb;");
        //VBox.setVgrow(childMeetingFlowPane, Priority.ALWAYS);
        //create flowpane contents
        createFiles(filesFlowPane);
        fileSection.getChildren().addAll(filesFlowPane);
    }
    
    public void createFiles(FlowPane filesFlowPane){
        Image image = new Image(getClass().getResourceAsStream("icon-book.jpg"));
        int fileListSize = 12;
        ImageView[] imageList = new ImageView[fileListSize];
        for (int k = 0; k < fileListSize; k++) {
            imageList[k] = new ImageView(image);
            Label fileLabel = new Label("第十三章关于动态计量经济模型-分布滞后模型与自回归第十三章关于动态计量经济模型-分布滞后模型与自回归第十三章关于动态计量经济模型-分布滞后模型与自回归");//issueList.get(k).getIssueName()
            //issueName.setPrefSize(120.0, 160.0);
            fileLabel.setPrefSize(161.0, 185.0);
            fileLabel.setTextOverrun(OverrunStyle.ELLIPSIS);
            fileLabel.setWrapText(true);
            fileLabel.setPadding(new Insets(15, 20, 35, 25));
            //issueName.setLineSpacing(1.0);
            StackPane fileStackPane = new StackPane();
            fileStackPane.setPrefSize(161.0, 185.0);
            fileStackPane.getChildren().addAll(imageList[k], fileLabel);
            fileStackPane.setAlignment(Pos.TOP_LEFT);
            //StackPane.setMargin(issueName, new Insets(15, 25, 50, 25));    
            filesFlowPane.getChildren().addAll(fileStackPane);
        }
    }
}
