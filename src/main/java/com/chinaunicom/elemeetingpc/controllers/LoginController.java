package com.chinaunicom.elemeetingpc.controllers;

import com.chinaunicom.elemeetingpc.constant.StatusConstant;
import com.chinaunicom.elemeetingpc.modelFx.DictionaryModel;
import com.chinaunicom.elemeetingpc.service.LoginService;
import com.chinaunicom.elemeetingpc.utils.DialogsUtils;
import com.chinaunicom.elemeetingpc.utils.FxmlUtils;
import com.chinaunicom.elemeetingpc.utils.LoadingPage;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import java.io.IOException;
import java.sql.SQLException;
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
import javafx.scene.layout.BorderPane;
import org.apache.commons.lang3.StringUtils;

/**
 * 登陆控制器.
 *
 * @author zhaojunfeng, chenxi
 */
public class LoginController {

    @FXML
    private TextField loginNameField;

    @FXML
    private PasswordField tpasswordfField;

    @FXML
    private BorderPane borderPaneMain;

    //机构选择界面
    public static final String FXML_ORG_FXML = "/fxml/fxml_org.fxml";

    private DictionaryModel dictionaryModel;

    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);

    /**
     * 初始化LoginController，给loginNameField和tpasswordfField添加回车事件.
     */
    public void initialize() {
        loginNameField.setOnKeyPressed(new EventHandler<javafx.scene.input.KeyEvent>() {
            @Override
            public void handle(javafx.scene.input.KeyEvent ke) {
                if (ke.getCode() == KeyCode.ENTER) {
                    try {
                        login();
                    } catch (Exception ex) {
                        DialogsUtils.customErrorAlert(ex.getCause().getMessage());
                        logger.error(ex.getCause().getMessage());
                        ex.printStackTrace();
                    }
                }
            }
        });
        tpasswordfField.setOnKeyPressed(new EventHandler<javafx.scene.input.KeyEvent>() {
            @Override
            public void handle(javafx.scene.input.KeyEvent ke) {
                if (ke.getCode() == KeyCode.ENTER) {
                    try {
                        login();
                    } catch (Exception ex) {
                        DialogsUtils.customErrorAlert(ex.getCause().getMessage());
                        logger.error(ex.getCause().getMessage());
                        ex.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 登录方法.
     */
    @FXML
    public void login() throws ApplicationException, SQLException, InterruptedException, ExecutionException {
        String loginName = loginNameField.getText();
        String password = tpasswordfField.getText();
        this.dictionaryModel = new DictionaryModel();
        String regiCode = dictionaryModel.queryByFieldIsNotNull();
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
        //只能通过Task来管理业务逻辑和UI界面的关系，否则这个数据加载界面根本出不来
        final Task<String> task = new Task<String>() {
            @Override
            protected String call() throws InterruptedException, ExecutionException {
                LoginService service = new LoginService();
                Map<String, String> map = service.login(loginName, password, regiCode);
                String a = map.get("code");
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
                        DialogsUtils.customErrorAlert(task.get());
                        loadingPage.closeLoadingPage();
                    }
                } catch (InterruptedException | ExecutionException ex) {
                    logger.error(ex.getCause().getMessage());
                    ex.printStackTrace();
                }
            }
        });
        task.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent event) {
                //弹窗提示错误
                DialogsUtils.errorAlert("server.connection.error");
                loadingPage.closeLoadingPage();
            }
        });
        new Thread(task).start();
    }

    /**
     * 展示注册码
     */
    @FXML
    public void showRegiCode() throws ApplicationException, SQLException {
        String regiCode = "";
        this.dictionaryModel = new DictionaryModel();
        regiCode = dictionaryModel.queryByFieldIsNotNull();
        DialogsUtils.registerCodeAlert(regiCode);
    }

    /**
     * 跳转到机构选择界面.
     */
    public void showFxmlOrg() {
        try {
            FXMLLoader loader = FxmlUtils.getFXMLLoader(FXML_ORG_FXML);
            borderPaneMain.setCenter(loader.load()); //将当前BorderPane中间区域加载为机构选择界面
            OrganInfoController organInfoController = loader.getController(); //从loader中获取OrganInfoController
            organInfoController.setBorderPane(borderPaneMain);//设置传参当前的borderPaneMain，以便在OrganInfoController中获取到当前BorderPane
        } catch (IOException e) {
            logger.error(e.getCause().getMessage());
        }
    }
}
