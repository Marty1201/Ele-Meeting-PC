
package com.chinaunicom.elemeetingpc.controllers;

import com.chinaunicom.elemeetingpc.modelFx.OrganInfoModel;
import com.chinaunicom.elemeetingpc.utils.DialogsUtils;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;



/**
 * 组织机构控制器，在界面的列表上加载登陆人所属机构名称，选择机构后进入会议
 * 主界面.
 * @author chenxi
 * 创建时间：2019-6-27 14:20:17
 */
public class OrganInfoController {
    
    @FXML
    private ListView<String> organListView;
    
    private OrganInfoModel organInfoModel;
    
    //初始化
    public void initialize(){
        this.organInfoModel = new OrganInfoModel();
        try{
            organInfoModel.init();
        }catch(ApplicationException e){
            DialogsUtils.errorAlert(e.getMessage());
        }
        int listSize = this.organInfoModel.getOrganInfoNameObservableList().size();
        organListView.setItems(this.organInfoModel.getOrganInfoNameObservableList());
        //ListView的高=list的size X 每条list-cell的size + 5(间距)
        organListView.setPrefHeight(listSize * 40 + 5);
        //添加事件监听器
        organListView.getSelectionModel().selectedItemProperty().addListener(
                            (ObservableValue<? extends String> ov, String old_val, String new_val) ->{
                    System.out.println(new_val + " Clicked!");
                    //to do
                });
    }
}
