package com.chinaunicom.elemeetingpc.controllers;

import com.chinaunicom.elemeetingpc.constant.StatusConstant;
import com.chinaunicom.elemeetingpc.service.DictionaryInfoService;
import com.chinaunicom.elemeetingpc.utils.DialogsUtils;
import com.chinaunicom.elemeetingpc.utils.FxmlUtils;
import com.chinaunicom.elemeetingpc.utils.LoadingPage;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 登陆控制器, 初始化登陆界面UI事件，处理用户登录相关逻辑.
 *
 * @author zhaojunfeng, chenxi
 */
public class LoginController {

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    @FXML
    private TextField loginNameField;

    @FXML
    private PasswordField tpasswordfField;

    @FXML
    private BorderPane borderPaneMain;

    //机构选择界面
    public static final String FXML_ORG_FXML = "/fxml/fxml_org.fxml";

    private DictionaryInfoService dictionaryInfoService;

    /**
     * 初始化LoginController并给loginNameField和tpasswordfField添加回车事件.
     */
    public void initialize() {
        
        dictionaryInfoService = new DictionaryInfoService();
        
        loginNameField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(javafx.scene.input.KeyEvent ke) {
                if (ke.getCode() == KeyCode.ENTER) {
                    login();
                }
            }
        });
        tpasswordfField.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(javafx.scene.input.KeyEvent ke) {
                if (ke.getCode() == KeyCode.ENTER) {
                    login();
                }
            }
        });
    }

    /**
     * 登录方法，使用Task实现读取界面加载等相关业务逻辑.
     */
    @FXML
    public void login() {
        try {
            String loginName = loginNameField.getText();
            String password = tpasswordfField.getText();
            String regiCode = dictionaryInfoService.queryRegiCode();
            if (StringUtils.isBlank(loginName)) {
                DialogsUtils.infoAlert("loginController.loginName.not.empty");
                return;
            }
            if (StringUtils.isBlank(password)) {
                DialogsUtils.infoAlert("loginController.password.not.empty");
                return;
            }
            if (StringUtils.isBlank(regiCode)) {
                DialogsUtils.infoAlert("loginController.regiCode.not.empty");
                return;
            }
            //创建数据加载界面
            LoadingPage loadingPage = new LoadingPage(borderPaneMain.getScene().getWindow());
            loadingPage.showLoadingPage();
            //通过Task来管理业务逻辑和UI界面的关系
            final Task<String> task = new Task<String>() {
                @Override
                protected String call() throws InterruptedException, ExecutionException, Exception {
                    LoginServiceController loginServiceController = new LoginServiceController();
                    Map<String, String> map = loginServiceController.login(loginName, password, regiCode);
                    if (StringUtils.equals(StatusConstant.RESULT_CODE_SUCCESS, map.get("code"))) {
                        //登录成功
                        this.set(map.get("code"));
                    } else {
                        //登录失败
                        this.set(map.get("desc"));
                    }
                    return this.get();
                }
            };
            task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {
                    try {
                        if (StringUtils.equals(StatusConstant.RESULT_CODE_SUCCESS, task.get())) {
                            //登录成功
                            showFxmlOrg();
                            loadingPage.closeLoadingPage();
                        } else {
                            //登录失败
                            DialogsUtils.customInfoAlert(task.get());
                            loadingPage.closeLoadingPage();
                        }
                    } catch (Exception ex) {
                        DialogsUtils.errorAlert("system.malfunction");
                        loadingPage.closeLoadingPage();
                        logger.error(FxmlUtils.getResourceBundle().getString("error.LoginController.login.Task.call"), ex);
                    }
                }
            });
            task.setOnFailed(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {
                    //弹窗提示错误并记录到日志里面
                    DialogsUtils.errorAlert("system.malfunction");
                    loadingPage.closeLoadingPage();
                    logger.error(FxmlUtils.getResourceBundle().getString("error.LoginController.login.Task.call"), task.getException());
                }
            });
            new Thread(task).start();
        } catch (Exception ex) {
            DialogsUtils.errorAlert("system.malfunction");
            logger.error(FxmlUtils.getResourceBundle().getString("error.LoginController.login"), ex);
        }
    }

    /**
     * 展示注册码.
     */
    @FXML
    public void showRegiCode() {
        try {
            String regiCode = dictionaryInfoService.queryRegiCode();
            DialogsUtils.registerCodeAlert(regiCode);
        } catch (Exception ex) {
            DialogsUtils.errorAlert("system.malfunction");
            logger.error(FxmlUtils.getResourceBundle().getString("error.LoginController.showRegiCode"), ex);
        }
    }

    /**
     * 跳转到机构选择界面.
     */
    public void showFxmlOrg() throws Exception {
        FXMLLoader loader = FxmlUtils.getFXMLLoader(FXML_ORG_FXML);
        borderPaneMain.setCenter(loader.load()); //将当前BorderPane中间区域加载为机构选择界面
        OrganInfoController organInfoController = loader.getController(); //从loader中获取OrganInfoController
        organInfoController.setBorderPane(borderPaneMain);//设置传参当前的borderPaneMain，以便在OrganInfoController中获取到当前BorderPane
    }
}
