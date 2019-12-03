package com.chinaunicom.elemeetingpc.utils;

import com.chinaunicom.elemeetingpc.constant.GlobalStaticConstant;
import com.chinaunicom.elemeetingpc.controllers.FileDetailController;
import com.chinaunicom.elemeetingpc.database.models.FileResource;
import com.chinaunicom.elemeetingpc.database.models.FileUserRelation;
import com.chinaunicom.elemeetingpc.database.models.SyncParams;
import com.chinaunicom.elemeetingpc.modelFx.FileResourceModel;
import com.chinaunicom.elemeetingpc.modelFx.FileUserRelationModel;
import com.chinaunicom.elemeetingpc.modelFx.SyncParamsModel;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;
import com.j256.ormlite.logger.Logger;
import com.j256.ormlite.logger.LoggerFactory;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.ShutdownListener;
import com.rabbitmq.client.ShutdownSignalException;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.TimeoutException;
import javafx.application.Platform;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;

/**
 * A RabbitMQ plugin object take care of syn between clients and server plus a
 * lot of over things :) .
 *
 * @author chenxi 创建时间：2019-10-16 16:40:57
 */
public class MQPlugin {

    private static final Logger logger = LoggerFactory.getLogger(MQPlugin.class);

    private static final String EXCHANGE_NAME = "topic";

    private static Connection connection;

    private static Channel channel;

    private static ConnectionFactory factory;

    private static String routingKey;

    private static String queueName;

    private FileDetailController fileDetailController;

    public MQPlugin() {
        try {
            //init connectionFactory
            factory = initConnectionFactory();
            //create connection with a given name
            connection = factory.newConnection(GlobalStaticConstant.GLOBAL_USERINFO_USERNAME);
            //monitoring connection
            connectionShutdownMonitor();
            //create channel
            channel = connection.createChannel();
            //a fixed routingKey act like fanout exchange
            routingKey = "#.syncKey.#";
            //Note: the exchange must be exactly same as the exchange android and ios app declared, that is it must be a
            //non-durable, non-autodelete, no arguments exchange
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
            //declare a queue and bind the queue to exchange
            queueName = channel.queueDeclare().getQueue();
            channel.queueBind(queueName, EXCHANGE_NAME, routingKey);
        } catch (IOException | TimeoutException ex) {
            ex.printStackTrace();
            logger.error(ex.getCause().getMessage());
        }
    }

    /**
     * This method initialize the connectionFactory, note: in java client 5.7.3
     * the auto recovery is enabled by default.
     *
     * @return factory ConnectionFactory
     */
    public final ConnectionFactory initConnectionFactory() {
        try {
            SyncParams syncParams = getSyncParams();
            factory = new ConnectionFactory();
            if (syncParams != null) {
                //initialize ConnectionFactory
                factory.setUsername(syncParams.getUserName());
                factory.setPassword(syncParams.getPassword());
                factory.setVirtualHost("/");
                factory.setHost(syncParams.getIp());
                factory.setPort(Integer.parseInt(syncParams.getPort()));
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex.getCause().getMessage());
        }
        return factory;
    }

    /**
     * This method publish a message to exchange.
     *
     * @param message the message to be sent
     */
    public void publishMessage(String message) {
        try {
            channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes("UTF-8"));
        } catch (IOException ex) {
            ex.printStackTrace();
            logger.error(ex.getCause().getMessage());
        }
    }

    /**
     * This method consume a message from the exchange, note once start
     * consuming, it will continue consuming message only when the the app exit
     * or close(refer to as "Push API"/Receiving Messages by Subscription), that
     * is all the incoming message will be auto handled by the handleDelivery
     * method, within the handleDelivery method, operations such as update app's
     * GUI or database inqueries must be made after RabbitMq has completed its
     * operation first(we use Platform.runLater method to achieve this)
     * otherwise it will cause threading errors and unpredictable behavior from
     * the system, understand this mechanism is vital to understand how the
     * consuming message works here.
     */
    public void consumeMessage() {
        try {
            boolean autoAck = false; //set false to manual ack, set true to auto ack
            channel.basicConsume(queueName, autoAck, new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag,
                        Envelope envelope,
                        AMQP.BasicProperties properties,
                        byte[] body)
                        throws IOException {
                    //String routingKey = envelope.getRoutingKey();
                    //String contentType = properties.getContentType();
                    long deliveryTag = envelope.getDeliveryTag();
                    //message content
                    String message = new String(body, "UTF-8");
                    handleMessage(message);
                    channel.basicAck(deliveryTag, false);
                }
            });
        } catch (IOException ex) {
            ex.printStackTrace();
            logger.error(ex.getCause().getMessage());
        }
    }

    /**
     * This method handles the particular message content, it's done by first
     * checking if the consumer had really started consuming message, then it
     * comparing organization name, command, platformType from the message
     * content to determine what action should be taken to respond the message,
     * all operations other than MQ itself is delayed by using the
     * Platform.runLater to avoid threads conflict.
     *
     * @param message the message to be handled
     */
    public void handleMessage(String message) {
        try {
            //only when the consumer started to consume, then we start to handle the message, otherwise do nothing and ignore any message
            if (GlobalStaticConstant.GLOBAL_ISSTARTCONSUMING) {
                //handle JSONObject
                JSONObject jSONObject = JSONObject.fromObject(message);
                //if in the following state
                if (GlobalStaticConstant.GLOBAL_ISFOLLOWINGCLICKED == true) {
                    //check whether the user should handle the message by comparing organization names
                    if (StringUtils.equalsIgnoreCase(jSONObject.getString("PAMQOrganizationIDName"), GlobalStaticConstant.GLOBAL_ORGANINFO_ORGANIZATIONNAME)) {
                        //here handle all the messages which contain "command" key
                        if (jSONObject.containsKey("command")) {
                            //check what the user should do with the message by checking the command field(Universal)
                            if (StringUtils.equalsIgnoreCase(jSONObject.getString("command"), GlobalStaticConstant.GLOBAL_TURNPAGE)) {
                                handleTurnPage(jSONObject);
                            }
                            //if receiving command is giveupPresenter, then popup a dialog window to inform followers(Universal)
                            if (StringUtils.equalsIgnoreCase(jSONObject.getString("command"), GlobalStaticConstant.GLOBAL_GIVEUPPRESENTER)) {
                                handleGiveupSpeaker();
                            }
                            //if receiving command is zoomIn/Out, then sync with the speaker by zooming in/out(PC client only)
                            if (StringUtils.equalsIgnoreCase(jSONObject.getString("command"), GlobalStaticConstant.GLOBAL_ZOOMIN) || StringUtils.equalsIgnoreCase(jSONObject.getString("command"), GlobalStaticConstant.GLOBAL_ZOOMOUT)) {
                                handleZoom(jSONObject);
                            }
                            //if receiving command is fitHeight/Width, then sync with the speaker by fit height/width(PC client only)
                            if (StringUtils.equalsIgnoreCase(jSONObject.getString("command"), GlobalStaticConstant.GLOBAL_FITHEIGHT) || StringUtils.equalsIgnoreCase(jSONObject.getString("command"), GlobalStaticConstant.GLOBAL_FITWIDTH)) {
                                handleFitSize(jSONObject);
                            }
                        }
                        //the full blown version of other sync abilities include: zoom in/out. vertical/horizontal scroll, but at the moment only we implemented vertical scrolling sync
                        //here handle all the messages which contain "platformType" key(Universal)
                        if (jSONObject.containsKey("platformType")) {
                            //handle message from pc client
                            if (StringUtils.equalsIgnoreCase(jSONObject.getString("platformType"), GlobalStaticConstant.GLOBAL_PCSYNCFLAG)) {
                                handlePCScroll(jSONObject);
                            }
                            //handle message from ios client
                            if (StringUtils.equalsIgnoreCase(jSONObject.getString("platformType"), GlobalStaticConstant.GLOBAL_IOSSYNCFLAG)) {
                                    handleIosScroll(jSONObject);
                            }
                        }
                    }
                }
                //if in the speaking state
                if (GlobalStaticConstant.GLOBAL_ISSPEAKINGCLICKED == true) {
                    //if receive applyForPresenter message
                    if (StringUtils.equalsIgnoreCase(jSONObject.getString("command"), GlobalStaticConstant.GLOBAL_APPLYFORPRESENTER)) {
                        //check if applyForPresenter message is send by another user(not by itself!), if true then give up speaking
                        if (!StringUtils.equals(jSONObject.getString("userid"), GlobalStaticConstant.GLOBAL_ORGANINFO_OWNER_USERID)) {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    DialogsUtils.infoAlert("MQPlugin.replaceSpeaker");
                                    fileDetailController.updateFileDetailView(GlobalStaticConstant.GLOBAL_SPEAKING);
                                }
                            });
                        }
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex.getCause().getMessage());
        }
    }

    /**
     * Handle turn page command, 2 cases need to be considered: 1, speaker and
     * follower are looking at the same file, 2, speaker and follower are
     * looking at diff file.
     *
     * @param jSONObject the Json object
     */
    public void handleTurnPage(JSONObject jSONObject) {
        try {
            //Important note：in javafx, when a FX thread try to modify UI component other than the application thread, must alway use Platform.runLater
            //eg. here the RabbitMQ thread(handleDelivery method) try to modify the application UI(fileDetailController), need to wait for RabbitMQ thread finish, than start application's thread
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    //check if the user own this file
                    if (isOwnedByLoginUser(jSONObject.getString("bookid"))) {
                        //check if it's same file, if true then turn page straight away
                        if (StringUtils.equalsIgnoreCase(jSONObject.getString("bookid"), fileDetailController.getFileInfo().getFileId())) {
                            fileDetailController.getOpenPdfViewer().goToCurrentPage(jSONObject.getInt("page"));
                        } else {
                            //go to the method handles speaker and follower are reading different files
                            handleDiffFile(jSONObject);
                        }
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex.getCause().getMessage());
        }
    }

    /**
     * Handle when speaker and follower are reading different files when sync,
     * query to the database must be placed inside the Platform.runLater method.
     *
     * @param jSONObject the Json object
     */
    public void handleDiffFile(JSONObject jSONObject) {
        try {
            //query the speaker's file
            FileResourceModel fileResourceModel = new FileResourceModel();
            FileResource fileInfo = fileResourceModel.queryFilesById(jSONObject.getString("bookid")).get(0);
            //close the currently opened view and go back to file list view(close the currently opened file)
            fileDetailController.showFxmlFileList();
            //open the same file detail view as the speaker(open the speaker's file) and go to the right page
            fileDetailController.getFileController().showFxmlFileDetail(fileInfo, jSONObject.getString("fileName"), jSONObject.getInt("page"));
        } catch (ApplicationException | SQLException ex) {
            ex.printStackTrace();
            logger.error(ex.getCause().getMessage());
        }
    }

    /**
     * Handle Giveup speaker command.
     *
     */
    public void handleGiveupSpeaker() {
        try {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    DialogsUtils.infoAlert("MQPlugin.giveupSpeaker");
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex.getCause().getMessage());
        }
    }

    /**
     * Handle zoom in/out, 2 cases which needed to be handled: 1.same file same
     * page/diff page, 2.diff files.
     *
     * @param jSONObject the Json object
     */
    public void handleZoom(JSONObject jSONObject) {
        try {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    //check if the user own this file
                    if (isOwnedByLoginUser(jSONObject.getString("bookid"))) {
                        //if it's same file and on the same page/diff page, if true then set the zoomType, zoomFactor and updateImage with the pageIndex
                        if (StringUtils.equalsIgnoreCase(jSONObject.getString("bookid"), fileDetailController.getFileInfo().getFileId())) {
                            fileDetailController.getOpenPdfViewer().setZoomType(OpenPdfViewer.ZoomType.CUSTOM);
                            fileDetailController.getOpenPdfViewer().setZoomFactor(Float.valueOf(jSONObject.get("zoomFactor").toString()));
                            fileDetailController.getOpenPdfViewer().updateImage(jSONObject.getInt("page"), 0, true);
                        }
                        //diff files need to close currently opened file, go back to file list, open new file, go to the page and finally set the zoomType, zoomFactor and updateImage with the pageIndex
                        if (!StringUtils.equalsIgnoreCase(jSONObject.getString("bookid"), fileDetailController.getFileInfo().getFileId())) {
                            handleDiffFile(jSONObject);
                            //set zoomType
                            fileDetailController.getOpenPdfViewer().setZoomType(OpenPdfViewer.ZoomType.CUSTOM);
                            //set zoomFactor
                            fileDetailController.getOpenPdfViewer().setZoomFactor(Float.valueOf(jSONObject.get("zoomFactor").toString()));
                            //update page image
                            fileDetailController.getOpenPdfViewer().updateImage(jSONObject.getInt("page"), 0, true);
                        }
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex.getCause().getMessage());
        }
    }

    /**
     * Handle fit height/width, 2 cases which needed to be handled: 1.same file
     * same page/diff page, 2.diff files.
     *
     * @param jSONObject the Json object
     */
    public void handleFitSize(JSONObject jSONObject) {
        try {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    //check if the user own this file
                    if (isOwnedByLoginUser(jSONObject.getString("bookid"))) {
                        //if it's same file and on the same page/diff page, if true then set the zoomType, zoomFactor and updateImage with the pageIndex
                        if (StringUtils.equalsIgnoreCase(jSONObject.getString("bookid"), fileDetailController.getFileInfo().getFileId())) {
                            if (StringUtils.equalsIgnoreCase(jSONObject.getString("command"), GlobalStaticConstant.GLOBAL_FITHEIGHT)) {
                                fileDetailController.getOpenPdfViewer().setZoomType(OpenPdfViewer.ZoomType.HEIGHT);
                                fileDetailController.getOpenPdfViewer().updateImage(jSONObject.getInt("page"), 0, true);
                            }
                            if (StringUtils.equalsIgnoreCase(jSONObject.getString("command"), GlobalStaticConstant.GLOBAL_FITWIDTH)) {
                                fileDetailController.getOpenPdfViewer().setZoomType(OpenPdfViewer.ZoomType.WIDTH);
                                fileDetailController.getOpenPdfViewer().updateImage(jSONObject.getInt("page"), 0, true);
                            }
                        }
                        //diff files need to close currently opened file, go back to file list, open new file, go to the page and finally set the zoomType, zoomFactor and updateImage with the pageIndex
                        if (!StringUtils.equalsIgnoreCase(jSONObject.getString("bookid"), fileDetailController.getFileInfo().getFileId())) {
                            if (StringUtils.equalsIgnoreCase(jSONObject.getString("command"), GlobalStaticConstant.GLOBAL_FITHEIGHT)) {
                                handleDiffFile(jSONObject);
                                //set zoomType
                                fileDetailController.getOpenPdfViewer().setZoomType(OpenPdfViewer.ZoomType.HEIGHT);
                                //update page image
                                fileDetailController.getOpenPdfViewer().updateImage(jSONObject.getInt("page"), 0, true);
                            }
                            if (StringUtils.equalsIgnoreCase(jSONObject.getString("command"), GlobalStaticConstant.GLOBAL_FITWIDTH)) {
                                handleDiffFile(jSONObject);
                                //set zoomType
                                fileDetailController.getOpenPdfViewer().setZoomType(OpenPdfViewer.ZoomType.WIDTH);
                                //update page image
                                fileDetailController.getOpenPdfViewer().updateImage(jSONObject.getInt("page"), 0, true);
                            }
                        }
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex.getCause().getMessage());
        }
    }

    /**
     * Handle PC vertical and horizontal scrolling, there 3 cases which needed
     * to be handled : 1.same file and same page, 2.same file but diff page,
     * 3.diff files.
     *
     * @param jSONObject the Json object
     */
    public void handlePCScroll(JSONObject jSONObject) {
        try {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    //check if the user own this file
                    if (isOwnedByLoginUser(jSONObject.getString("bookid"))) {
                        if (StringUtils.equalsIgnoreCase(jSONObject.getString("command"), GlobalStaticConstant.GLOBAL_VSCROLL)) {
                            double vValue = jSONObject.getDouble("vValue");
                            //if it's same file and on the same page, if true then set the vValue straight away
                            if (StringUtils.equalsIgnoreCase(jSONObject.getString("bookid"), fileDetailController.getFileInfo().getFileId()) && jSONObject.getInt("page") == fileDetailController.getOpenPdfViewer().getCurrentPage()) {
                                fileDetailController.getOpenPdfViewer().setVvalue(vValue);
                            }
                            //if it's same file but diff page, go to that page and set the vValue
                            if (StringUtils.equalsIgnoreCase(jSONObject.getString("bookid"), fileDetailController.getFileInfo().getFileId()) && jSONObject.getInt("page") != fileDetailController.getOpenPdfViewer().getCurrentPage()) {
                                fileDetailController.getOpenPdfViewer().goToCurrentPage(jSONObject.getInt("page"));
                                fileDetailController.getOpenPdfViewer().setVvalue(vValue);
                            }
                            //diff files need to close currently opened file, go back to file list, open new file, go to the page and finally set the vValue
                            if (!StringUtils.equalsIgnoreCase(jSONObject.getString("bookid"), fileDetailController.getFileInfo().getFileId())) {
                                handleDiffFile(jSONObject);
                                fileDetailController.getOpenPdfViewer().setVvalue(vValue);
                            }
                        }
                        if (StringUtils.equalsIgnoreCase(jSONObject.getString("command"), GlobalStaticConstant.GLOBAL_HSCROLL)) {
                            double hValue = jSONObject.getDouble("hValue");
                            //if it's same file and on the same page, if true then set the vValue straight away
                            if (StringUtils.equalsIgnoreCase(jSONObject.getString("bookid"), fileDetailController.getFileInfo().getFileId()) && jSONObject.getInt("page") == fileDetailController.getOpenPdfViewer().getCurrentPage()) {
                                fileDetailController.getOpenPdfViewer().setHvalue(hValue);
                            }
                            //if it's same file but diff page, go to that page and set the vValue
                            if (StringUtils.equalsIgnoreCase(jSONObject.getString("bookid"), fileDetailController.getFileInfo().getFileId()) && jSONObject.getInt("page") != fileDetailController.getOpenPdfViewer().getCurrentPage()) {
                                fileDetailController.getOpenPdfViewer().goToCurrentPage(jSONObject.getInt("page"));
                                fileDetailController.getOpenPdfViewer().setHvalue(hValue);
                            }
                            //diff files need to close currently opened file, go back to file list, open new file, go to the page and finally set the vValue
                            if (!StringUtils.equalsIgnoreCase(jSONObject.getString("bookid"), fileDetailController.getFileInfo().getFileId())) {
                                handleDiffFile(jSONObject);
                                fileDetailController.getOpenPdfViewer().setHvalue(hValue);
                            }
                        }
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex.getCause().getMessage());
        }
    }

    /**
     * Handle ios vertical, horizontal and zooming scrolling, note: by default,
     * the ios app use the width of the file to fit the width of the screen, 
     * therefore there will be very limited case where we need to scroll a file
     * horizontally without zooming in first, that is when a file is opened, 
     * it should always be vertical scrolling and the offsetX is always set to 0,
     * only if we zoom in on the file, the offsetX will be a positive value > 0,
     * we use this change in offsetX to determine when to apply ios scale, there
     * also 3 cases which needed to be handled: 1.same file and same page, 2.same
     * file but diff page, 3.diff files.
     *
     * @param jSONObject the Json object
     */
    public void handleIosScroll(JSONObject jSONObject) {
        try {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    //check if the user own this file
                    if (isOwnedByLoginUser(jSONObject.getString("bookid"))) {
                        double offsetY = jSONObject.getDouble("offsetY");
                        double height = jSONObject.getDouble("height");
                        double offsetX = jSONObject.getDouble("offsetX");
                        double width = jSONObject.getDouble("width");
                        //apply scale if offsetX > 0
                        if (offsetX > 0) {
                            //the scale of ios is same as the pc's zoomfactor, when scale is in use, when need to update the file image with scale rather than zoomfactor
                            //note: there is a quite large deviation between ios scale and pc zoomfactor, but since it's too difficult to convert between scale and zoomfactor
                            //also the deviation is tolerable, therefore we just use the raw ios scale straight away for the sake of simplicity
                            fileDetailController.getOpenPdfViewer().updateImage(jSONObject.getInt("page"), Float.valueOf(jSONObject.get("scale").toString()), false);
                        }
                        //if it's same file and on the same page, if true then set the vValue straight away
                        if (StringUtils.equalsIgnoreCase(jSONObject.getString("bookid"), fileDetailController.getFileInfo().getFileId()) && jSONObject.getInt("page") == fileDetailController.getOpenPdfViewer().getCurrentPage()) {
                            convertVValue(offsetY, height);
                            convertHValue(offsetX, width);
                        }
                        //if it's same file but diff page, go to that page and set the vValue
                        if (StringUtils.equalsIgnoreCase(jSONObject.getString("bookid"), fileDetailController.getFileInfo().getFileId()) && jSONObject.getInt("page") != fileDetailController.getOpenPdfViewer().getCurrentPage()) {
                            fileDetailController.getOpenPdfViewer().goToCurrentPage(jSONObject.getInt("page"));
                            convertVValue(offsetY, height);
                            convertHValue(offsetX, width);
                        }
                        //diff files need to close currently opened file, go back to file list, open new file, go to the page and finally set the vValue
                        if (!StringUtils.equalsIgnoreCase(jSONObject.getString("bookid"), fileDetailController.getFileInfo().getFileId())) {
                            handleDiffFile(jSONObject);
                            convertVValue(offsetY, height);
                            convertHValue(offsetX, width);
                        }
                    }
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex.getCause().getMessage());
        }
    }

    /**
     * Convert and Set the vertical value for the scrollPane of the
     * OpenPdfViewer(handle ios vertical scrolling).
     *
     * @param offsetY
     * @param height
     */
    public void convertVValue(double offsetY, double height) {
        try {
            double vValue = offsetY / (height - 70);
            fileDetailController.getOpenPdfViewer().setVvalue(vValue);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex.getCause().getMessage());
        }
    }

    /**
     * Convert and Set the horizontal value for the scrollPane of the
     * OpenPdfViewer(handle ios horizontal scrolling).
     *
     * @param offsetX
     * @param width
     */
    public void convertHValue(double offsetX, double width) {
        try {
            double hValue = offsetX / (width); //-20 ?
            fileDetailController.getOpenPdfViewer().setHvalue(hValue);
        } catch (Exception ex) {
            ex.printStackTrace();
            logger.error(ex.getCause().getMessage());
        }
    }

    /**
     * Return a Channel.
     *
     * @return channel
     */
    public Channel getChannel() {
        return channel;
    }

    /**
     * Close the connection and channel.
     *
     */
    public void closeConnection() {
        try {
            if (connection.isOpen() && channel.isOpen()) {
                channel.close();
                connection.close();
            }
        } catch (ShutdownSignalException | IOException | TimeoutException ex) {
            ex.printStackTrace();
            logger.error(ex.getCause().getMessage());
        }
    }

    /**
     * This is a debug method which monitor possible
     * connection/channel/application/broker error which will cause the MQ
     * connectivity failure, note: regardless of the reason that caused the
     * closure(network failure/internal failure/explicit local shutdown.) it
     * will always end up here.
     */
    public final void connectionShutdownMonitor() {
        connection.addShutdownListener(new ShutdownListener() {
            @Override
            public void shutdownCompleted(ShutdownSignalException cause) {
                String hardError = "";
                String applInit = "";
                if (cause.isHardError()) {
                    hardError = "connection";
                } else {
                    hardError = "channel";
                }
                if (cause.isInitiatedByApplication()) {
                    applInit = "application";
                } else {
                    applInit = "broker";
                }
                System.out.println("Connectivity to MQ has stopped. It was caused by "
                        + applInit + " at the " + hardError
                        + " level. Reason received " + cause.getReason());
            }
        });
    }

    /**
     * Check whether the login user own this file and hence has the right to
     * operate the file, it's done by querying FileUserRelationModel using
     * fileId, find the corresponding userIds, then comparing userIds with the
     * currently login user's id.
     *
     * @param fileId file id
     * @return hasRight whether the user own the file or not
     */
    public boolean isOwnedByLoginUser(String fileId) {
        boolean hasRight = false;
        try {
            FileUserRelationModel fileUserRelationModel = new FileUserRelationModel();
            List<FileUserRelation> fileUserRelationList = fileUserRelationModel.queryFileUserRelationByFileId(fileId);
            if (!fileUserRelationList.isEmpty()) {
                for (int i = 0; i < fileUserRelationList.size(); i++) {
                    if (StringUtils.equals(fileUserRelationList.get(i).getUserId(), GlobalStaticConstant.GLOBAL_ORGANINFO_OWNER_USERID)) {
                        hasRight = true;
                        return hasRight;
                    }
                }
            }
        } catch (ApplicationException | SQLException ex) {
            ex.printStackTrace();
            logger.error(ex.getCause().getMessage());
        }
        return hasRight;
    }

    /**
     * Get sync params from the database.
     *
     * @return syncParams synchronization parameters
     */
    public SyncParams getSyncParams() {
        SyncParams syncParams = new SyncParams();
        try {
            SyncParamsModel syncParamsModel = new SyncParamsModel();
            //get RabbitMQ server infos from the database
            List<SyncParams> syncParamsList = syncParamsModel.querySyncParamsByOrganId("organizationId", GlobalStaticConstant.GLOBAL_ORGANINFO_ORGANIZATIONID);
            syncParams = syncParamsList.get(0);
        } catch (ApplicationException ex) {
            ex.printStackTrace();
            logger.error(ex.getCause().getMessage());
        }
        return syncParams;
    }

    public void setFileDetailController(FileDetailController fileDetailController) {
        this.fileDetailController = fileDetailController;
    }
}
