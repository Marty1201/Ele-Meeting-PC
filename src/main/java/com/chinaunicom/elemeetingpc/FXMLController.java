package com.chinaunicom.elemeetingpc;

import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

public class FXMLController implements Initializable {
    
    @FXML
    private Label label;
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
        System.out.println("You clicked me!");
        label.setText("Hello World!");
    }
    
    private void doPostRequestWithMap(String targetUrl,String responseType,Map params)throws Exception{
        if (StringUtils.isBlank(targetUrl)) {
            throw new IllegalArgumentException("targetUrl不能为空!");
        }
        CloseableHttpClient httpClient = HttpClients.createDefault();;

        CloseableHttpResponse response = null;
    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }    
}
