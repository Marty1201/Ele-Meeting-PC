package com.chinaunicom.elemeetingpc.controllers;

import com.chinaunicom.elemeetingpc.constant.GlobalStaticConstant;
import com.chinaunicom.elemeetingpc.service.UserInfoService;
import com.chinaunicom.elemeetingpc.utils.DialogsUtils;
import com.chinaunicom.elemeetingpc.utils.HashUtil;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.apache.commons.lang3.StringUtils;

/**
 * 密码修改控制器.
 *
 * @author zhaojunfeng, chenxi
 */
public class ResetPasswordController {

    @FXML
    private TextField oldPasswordField;

    @FXML
    private TextField newPasswordField;

    @FXML
    private TextField reNewPasswordField;

    private Stage dialogStage;

    private UserInfoService userInfoService;

    @FXML
    public void initialize() {

    }

    /**
     * 确定提交.
     */
    @FXML
    public void handleOK() throws Exception {
        userInfoService = new UserInfoService();
        String errorMessage = isInputValid();
        //校验密码是否符合规则
        if (StringUtils.isBlank(errorMessage)) {
            String oldPassword = userInfoService.getOldPassword();
            String newPassword = HashUtil.toMD5(newPasswordField.getText());
            //调后台修改用户密码接口
            UserInfoServiceController service = new UserInfoServiceController();
            String resultString = service.resetPassword(GlobalStaticConstant.GLOBAL_ORGANINFO_OWNER_USERID, oldPassword, newPassword);
            DialogsUtils.customInfoAlert(resultString);
            dialogStage.close();//关闭上一个密码修改界面
        } else {
            DialogsUtils.infoAlert(errorMessage);
        }
    }

    /**
     * 确定关闭.
     */
    @FXML
    public void handleCancel() {
        dialogStage.close();
    }

    /**
     * 密码复杂度校验，规则如下：
     * 1、密码不为空；2、旧密码输入不正确；3、新密码与确认密码必须一致；4、密码必须是8-20位；5、密码必须包含字母和数字； 6、密码不能包含空格.
     *
     * @return errorMessage 校验不通过的错误信息，如果校验通过则错误信息为空
     */
    private String isInputValid() throws ApplicationException {
        String errorMessage = "";
        String regex = "^[A-Za-z0-9]+$";
        //密码不为空
        if (StringUtils.isBlank(oldPasswordField.getText())) {
            return errorMessage = "ResetPasswordController.oldPasswordField.empty";
        }
        if (StringUtils.isBlank(newPasswordField.getText())) {
            return errorMessage = "ResetPasswordController.newPasswordField.empty";
        }
        if (StringUtils.isBlank(reNewPasswordField.getText())) {
            return errorMessage = "ResetPasswordController.reNewPasswordField.empty";
        }
        //旧密码输入不正确
        String oldPassword = userInfoService.getOldPassword();
        String newOldPassword = HashUtil.toMD5(oldPasswordField.getText());
        if (!StringUtils.equals(oldPassword, newOldPassword)) {
            return errorMessage = "ResetPasswordController.passWordField.old";
        }
        //新密码与确认密码必须一致
        if (!StringUtils.equalsIgnoreCase(newPasswordField.getText(), reNewPasswordField.getText())) {
            return errorMessage = "ResetPasswordController.passWordField.different";
        }
        //密码必须是8-20位
        if (newPasswordField.getText().length() < 8 || newPasswordField.getText().length() > 20) {
            return errorMessage = "ResetPasswordController.passWordField.length";
        }
        if (reNewPasswordField.getText().length() < 8 || reNewPasswordField.getText().length() > 20) {
            return errorMessage = "ResetPasswordController.passWordField.length";
        }
        //密码必须包含字母和数字
        if (!newPasswordField.getText().matches(regex) || !reNewPasswordField.getText().matches(regex)) {
            return errorMessage = "ResetPasswordController.passWordField.lettersNumber";
        }
        //密码不能包含空格
        if (StringUtils.containsWhitespace(newPasswordField.getText()) || StringUtils.containsWhitespace(reNewPasswordField.getText())) {
            return errorMessage = "ResetPasswordController.passWordField.space";
        }
        return errorMessage;
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
}
