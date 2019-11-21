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
import java.io.UnsupportedEncodingException;
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

    public MQPlugin() throws IOException, TimeoutException, ApplicationException, SQLException {
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
    }

    /**
     * This method initialize the connectionFactory, note: in java client 5.7.3
     * the auto recovery is enabled by default.
     *
     * @return factory
     * @throws
     * com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException
     * @throws java.sql.SQLException
     * @throws java.io.IOException
     * @throws java.util.concurrent.TimeoutException
     */
    public final ConnectionFactory initConnectionFactory() throws ApplicationException, SQLException, IOException, TimeoutException {
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
        return factory;
    }

    /**
     * This method publish a message to exchange.
     *
     * @param message
     * @throws java.io.UnsupportedEncodingException
     * @throws java.io.IOException
     */
    public void publishMessage(String message) throws UnsupportedEncodingException, IOException {
        channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes("UTF-8"));
    }

    /**
     * This method consume a message from the exchange, note once start
     * consuming, it will continue consuming message only when the the app exit
     * or close(refer to as "Push API"/Receiving Messages by Subscription), that
     * is all the incoming message will be auto handled by the handleDelivery
     * method, understand this mechanism is vital to understand how the
     * consuming message works here.
     *
     * @throws java.io.IOException
     */
    public void consumeMessage() throws IOException {
        boolean autoAck = false; //set false to manual ack, set true to auto ack
        channel.basicConsume(queueName, autoAck, new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag,
                    Envelope envelope,
                    AMQP.BasicProperties properties,
                    byte[] body)
                    throws IOException {
//                    String routingKey = envelope.getRoutingKey();
//                    String contentType = properties.getContentType();
                long deliveryTag = envelope.getDeliveryTag();
                //message content
                String message = new String(body, "UTF-8");
                channel.basicAck(deliveryTag, false);
                try {
                    handleMessage(message);
                } catch (ApplicationException | SQLException | IOException ex) {
                    logger.error(ex.getCause().getMessage());
                }
            }
        });
    }

    /**
     * This method handles the particular message content,
     * it's done by first checking if the consumer had really started consuming
     * message, then it comparing organization name, command, platformType
     * from the message content to determine what action should be taken to respond
     * the message.
     *
     * @param message
     * @throws
     * com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException
     * @throws java.sql.SQLException
     * @throw IOException
     */
    public void handleMessage(String message) throws ApplicationException, SQLException, IOException {
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
                            //check if the user own this file
                            if (isOwnedByLoginUser(jSONObject.getString("bookid"))) {
                                handleTurnPage(jSONObject);
                            }
                        }
                        //if receiving command is giveupPresenter, then popup a dialog window to inform followers(Universal)
                        if (StringUtils.equalsIgnoreCase(jSONObject.getString("command"), GlobalStaticConstant.GLOBAL_GIVEUPPRESENTER)) {
                            handleGiveupSpeaker();
                        }
                        //if receiving command is zoomIn/Out, then sync with the speaker by zooming in/out(PC client only)
                        if (StringUtils.equalsIgnoreCase(jSONObject.getString("command"), GlobalStaticConstant.GLOBAL_ZOOMIN) || StringUtils.equalsIgnoreCase(jSONObject.getString("command"), GlobalStaticConstant.GLOBAL_ZOOMOUT)) {
                            //check if the user own this file
                            if (isOwnedByLoginUser(jSONObject.getString("bookid"))) {
                                handleZoom(jSONObject);
                            }
                        }
                        //if receiving command is fitHeight/Width, then sync with the speaker by fit height/width(PC client only)
                        if (StringUtils.equalsIgnoreCase(jSONObject.getString("command"), GlobalStaticConstant.GLOBAL_FITHEIGHT) || StringUtils.equalsIgnoreCase(jSONObject.getString("command"), GlobalStaticConstant.GLOBAL_FITWIDTH)) {
                            //check if the user own this file
                            if (isOwnedByLoginUser(jSONObject.getString("bookid"))) {
                                handleFitSize(jSONObject);
                            }
                        }
                    }
                    //the full blown version of other sync abilities include: zoom in/out. vertical/horizontal scroll, but at the moment only we implemented vertical scrolling sync
                    //here handle all the messages which contain "platformType" key(Universal)
                    if (jSONObject.containsKey("platformType")) {
                        //if the message is from ios/PC client， todo: handle android
                        if (StringUtils.equalsIgnoreCase(jSONObject.getString("platformType"), GlobalStaticConstant.GLOBAL_IOSSYNCFLAG) || StringUtils.equalsIgnoreCase(jSONObject.getString("platformType"), GlobalStaticConstant.GLOBAL_PCSYNCFLAG)) {
                            //check if the user own this file
                            if (isOwnedByLoginUser(jSONObject.getString("bookid"))) {
                                handleVerticalScroll(jSONObject);
                            }
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
                                try {
                                    fileDetailController.updateFileDetailView(GlobalStaticConstant.GLOBAL_SPEAKING);
                                } catch (IOException ex) {
                                    logger.error(ex.getCause().getMessage());
                                }
                            }
                        });
                    }
                }
            }
        }
    }

    /**
     * Handle turn page command.
     *
     * @param jSONObject
     * @throws
     * com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException
     */
    public void handleTurnPage(JSONObject jSONObject) throws ApplicationException {
        //check if it's same file, if true then turn page straight away
        if (StringUtils.equalsIgnoreCase(jSONObject.getString("bookid"), fileDetailController.getFileInfo().getFileId())) {
            //Important note：in javafx, when a FX thread try to modify UI component other than the application thread, must alway use Platform.runLater
            //eg. here the RabbitMQ thread(handleDelivery method) try to modify the application UI, need to wait for RabbitMQ thread finish, than start application's thread
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    fileDetailController.getOpenPdfViewer().goToCurrentPage(jSONObject.getInt("page"));
                }
            });
        } else { //go to the method handles speaker and follower are reading different files
            handleDiffFile(jSONObject);
        }
    }

    /**
     * Handle when speaker and follower are reading different files when sync.
     *
     * @param jSONObject
     * @throws
     * com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException
     */
    public void handleDiffFile(JSONObject jSONObject) throws ApplicationException {
        //query the speaker's file
        FileResourceModel fileResourceModel = new FileResourceModel();
        FileResource fileInfo = fileResourceModel.queryFilesById(jSONObject.getString("bookid")).get(0);
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    //close the currently opened view and go back to file list view(close the currently opened file)
                    fileDetailController.showFxmlFileList();
                    //open the same file detail view as the speaker(open the speaker's file) and go to the right page
                    fileDetailController.getFileController().showFxmlFileDetail(fileInfo, jSONObject.getString("fileName"), jSONObject.getInt("page"));
                } catch (ApplicationException | SQLException ex) {
                    logger.error(ex.getCause().getMessage());
                }
            }
        });
    }

    /**
     * Handle Giveup speaker command.
     *
     */
    public void handleGiveupSpeaker() {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                DialogsUtils.infoAlert("MQPlugin.giveupSpeaker");
            }
        });
    }

    /**
     * Handle vertical scrolling, there 3 cases which needed to be handled :
     * 1.same file and same page, 
     * 2.same file but diff page, 
     * 3.diff files.
     *
     * @param jSONObject
     */
    public void handleVerticalScroll(JSONObject jSONObject) {
        try {
            //handle ios vertical scrolling
            if (StringUtils.equalsIgnoreCase(jSONObject.getString("platformType"), GlobalStaticConstant.GLOBAL_IOSSYNCFLAG)) {
                double offsetY = jSONObject.getDouble("offsetY");
                double error = jSONObject.getDouble("error");
                double height = jSONObject.getDouble("height");
                //if it's same file and on the same page, if true then set the vValue straight away
                if (StringUtils.equalsIgnoreCase(jSONObject.getString("bookid"), fileDetailController.getFileInfo().getFileId()) && jSONObject.getInt("page") == fileDetailController.getOpenPdfViewer().getCurrentPage()) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            calculateVValue(offsetY, height);
                        }
                    });
                }
                //if it's same file but diff page, go to that page and set the vValue
                if (StringUtils.equalsIgnoreCase(jSONObject.getString("bookid"), fileDetailController.getFileInfo().getFileId()) && jSONObject.getInt("page") != fileDetailController.getOpenPdfViewer().getCurrentPage()) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            fileDetailController.getOpenPdfViewer().goToCurrentPage(jSONObject.getInt("page"));
                            calculateVValue(offsetY, height);
                        }
                    });
                }
                //diff files need to close currently opened file, go back to file list, open new file, go to the page and finally set the vValue
                if (!StringUtils.equalsIgnoreCase(jSONObject.getString("bookid"), fileDetailController.getFileInfo().getFileId())) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                //query the speaker's file
                                FileResourceModel fileResourceModel = new FileResourceModel();
                                FileResource fileInfo = fileResourceModel.queryFilesById(jSONObject.getString("bookid")).get(0);
                                //close the currently opened view and go back to file list view(close the currently opened file)
                                fileDetailController.showFxmlFileList();
                                //open the same file detail view as the speaker(open the speaker's file) and go to the right page
                                fileDetailController.getFileController().showFxmlFileDetail(fileInfo, jSONObject.getString("fileName"), jSONObject.getInt("page"));
                                //set vertical value
                                calculateVValue(offsetY, height);
                            } catch (ApplicationException | SQLException ex) {
                                logger.error(ex.getCause().getMessage());
                            }
                        }
                    });
                }
            }
            //handle pc client vertical scrolling
            if (StringUtils.equalsIgnoreCase(jSONObject.getString("platformType"), GlobalStaticConstant.GLOBAL_PCSYNCFLAG)) {
                double vValue = jSONObject.getDouble("vValue");
                //if it's same file and on the same page, if true then set the vValue straight away
                if (StringUtils.equalsIgnoreCase(jSONObject.getString("bookid"), fileDetailController.getFileInfo().getFileId()) && jSONObject.getInt("page") == fileDetailController.getOpenPdfViewer().getCurrentPage()) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            fileDetailController.getOpenPdfViewer().setVvalue(vValue);
                        }
                    });
                }
                //if it's same file but diff page, go to that page and set the vValue
                if (StringUtils.equalsIgnoreCase(jSONObject.getString("bookid"), fileDetailController.getFileInfo().getFileId()) && jSONObject.getInt("page") != fileDetailController.getOpenPdfViewer().getCurrentPage()) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            fileDetailController.getOpenPdfViewer().goToCurrentPage(jSONObject.getInt("page"));
                            fileDetailController.getOpenPdfViewer().setVvalue(vValue);
                        }
                    });
                }
                //diff files need to close currently opened file, go back to file list, open new file, go to the page and finally set the vValue
                if (!StringUtils.equalsIgnoreCase(jSONObject.getString("bookid"), fileDetailController.getFileInfo().getFileId())) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                //query the speaker's file
                                FileResourceModel fileResourceModel = new FileResourceModel();
                                FileResource fileInfo = fileResourceModel.queryFilesById(jSONObject.getString("bookid")).get(0);
                                //close the currently opened view and go back to file list view(close the currently opened file)
                                fileDetailController.showFxmlFileList();
                                //open the same file detail view as the speaker(open the speaker's file) and go to the right page
                                fileDetailController.getFileController().showFxmlFileDetail(fileInfo, jSONObject.getString("fileName"), jSONObject.getInt("page"));
                                //set vValue
                                fileDetailController.getOpenPdfViewer().setVvalue(vValue);
                            } catch (ApplicationException | SQLException ex) {
                                logger.error(ex.getCause().getMessage());
                            }
                        }
                    });
                }
            }
        } catch (Exception ex) {
            logger.error(ex.getCause().getMessage());
        }
    }

    /**
     * Calculate and Set the vertical value for the scrollPane of the OpenPdfViewer(handle ios vertical scrolling).
     *
     * @param offsetY
     * @param height
     */
    public void calculateVValue(double offsetY, double height) {
        double vValue = offsetY / (height - 70);
        fileDetailController.getOpenPdfViewer().setVvalue(vValue);
    }

    /**
     * Handle zoom in/out, 2 cases which needed to be handled: 
     * 1.same file same page/diff page, 
     * 2.diff files.
     *
     * @param jSONObject
     */
    public void handleZoom(JSONObject jSONObject) {
        //if it's same file and on the same page/diff page, if true then set the zoomType, zoomFactor and updateImage with the pageIndex
        if (StringUtils.equalsIgnoreCase(jSONObject.getString("bookid"), fileDetailController.getFileInfo().getFileId())) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    fileDetailController.getOpenPdfViewer().setZoomType(OpenPdfViewer.ZoomType.CUSTOM);
                    fileDetailController.getOpenPdfViewer().setZoomFactor(Float.valueOf(jSONObject.get("zoomFactor").toString()));
                    fileDetailController.getOpenPdfViewer().updateImage(jSONObject.getInt("page"));
                }
            });
        }
        //diff files need to close currently opened file, go back to file list, open new file, go to the page and finally set the zoomType, zoomFactor and updateImage with the pageIndex
        if (!StringUtils.equalsIgnoreCase(jSONObject.getString("bookid"), fileDetailController.getFileInfo().getFileId())) {
            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        //query the speaker's file
                        FileResourceModel fileResourceModel = new FileResourceModel();
                        FileResource fileInfo = fileResourceModel.queryFilesById(jSONObject.getString("bookid")).get(0);
                        //close the currently opened view and go back to file list view(close the currently opened file)
                        fileDetailController.showFxmlFileList();
                        //open the same file detail view as the speaker(open the speaker's file) and go to the right page
                        fileDetailController.getFileController().showFxmlFileDetail(fileInfo, jSONObject.getString("fileName"), jSONObject.getInt("page"));
                        //set zoomType
                        fileDetailController.getOpenPdfViewer().setZoomType(OpenPdfViewer.ZoomType.CUSTOM);
                        //set zoomFactor
                        fileDetailController.getOpenPdfViewer().setZoomFactor(Float.valueOf(jSONObject.get("zoomFactor").toString()));
                        //update page image
                        fileDetailController.getOpenPdfViewer().updateImage(jSONObject.getInt("page"));
                    } catch (ApplicationException | SQLException ex) {
                        logger.error(ex.getCause().getMessage());
                    }
                }
            });
        }
    }

    /**
     * Handle fit height/width, 2 cases which needed to be handled: 1.same file
     * same page/diff page, 2.diff files.
     *
     * @param jSONObject
     */
    public void handleFitSize(JSONObject jSONObject) {
        //if it's same file and on the same page/diff page, if true then set the zoomType, zoomFactor and updateImage with the pageIndex
        if (StringUtils.equalsIgnoreCase(jSONObject.getString("bookid"), fileDetailController.getFileInfo().getFileId())) {
            if (StringUtils.equalsIgnoreCase(jSONObject.getString("command"), GlobalStaticConstant.GLOBAL_FITHEIGHT)) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        fileDetailController.getOpenPdfViewer().setZoomType(OpenPdfViewer.ZoomType.HEIGHT);
                        fileDetailController.getOpenPdfViewer().updateImage(jSONObject.getInt("page"));
                    }
                });
            }
            if (StringUtils.equalsIgnoreCase(jSONObject.getString("command"), GlobalStaticConstant.GLOBAL_FITWIDTH)) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        fileDetailController.getOpenPdfViewer().setZoomType(OpenPdfViewer.ZoomType.WIDTH);
                        fileDetailController.getOpenPdfViewer().updateImage(jSONObject.getInt("page"));
                    }
                });
            }
        }
        //diff files need to close currently opened file, go back to file list, open new file, go to the page and finally set the zoomType, zoomFactor and updateImage with the pageIndex
        if (!StringUtils.equalsIgnoreCase(jSONObject.getString("bookid"), fileDetailController.getFileInfo().getFileId())) {
            if (StringUtils.equalsIgnoreCase(jSONObject.getString("command"), GlobalStaticConstant.GLOBAL_FITHEIGHT)) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //query the speaker's file
                            FileResourceModel fileResourceModel = new FileResourceModel();
                            FileResource fileInfo = fileResourceModel.queryFilesById(jSONObject.getString("bookid")).get(0);
                            //close the currently opened view and go back to file list view(close the currently opened file)
                            fileDetailController.showFxmlFileList();
                            //open the same file detail view as the speaker(open the speaker's file) and go to the right page
                            fileDetailController.getFileController().showFxmlFileDetail(fileInfo, jSONObject.getString("fileName"), jSONObject.getInt("page"));
                            //set zoomType
                            fileDetailController.getOpenPdfViewer().setZoomType(OpenPdfViewer.ZoomType.HEIGHT);
                            //update page image
                            fileDetailController.getOpenPdfViewer().updateImage(jSONObject.getInt("page"));
                        } catch (ApplicationException | SQLException ex) {
                            logger.error(ex.getCause().getMessage());
                        }
                    }
                });
            }
            if (StringUtils.equalsIgnoreCase(jSONObject.getString("command"), GlobalStaticConstant.GLOBAL_FITWIDTH)) {
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //query the speaker's file
                            FileResourceModel fileResourceModel = new FileResourceModel();
                            FileResource fileInfo = fileResourceModel.queryFilesById(jSONObject.getString("bookid")).get(0);
                            //close the currently opened view and go back to file list view(close the currently opened file)
                            fileDetailController.showFxmlFileList();
                            //open the same file detail view as the speaker(open the speaker's file) and go to the right page
                            fileDetailController.getFileController().showFxmlFileDetail(fileInfo, jSONObject.getString("fileName"), jSONObject.getInt("page"));
                            //set zoomType
                            fileDetailController.getOpenPdfViewer().setZoomType(OpenPdfViewer.ZoomType.WIDTH);
                            //update page image
                            fileDetailController.getOpenPdfViewer().updateImage(jSONObject.getInt("page"));
                        } catch (ApplicationException | SQLException ex) {
                            logger.error(ex.getCause().getMessage());
                        }
                    }
                });
            }
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
     * @throws java.io.IOException
     * @throws java.util.concurrent.TimeoutException
     */
    public void closeConnection() throws IOException, TimeoutException {
        try {
            if (connection.isOpen() && channel.isOpen()) {
                channel.close();
                connection.close();
            }
        } catch (ShutdownSignalException | IOException  ex) {
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
            for (int i = 0; i < fileUserRelationList.size(); i++) {
                if (StringUtils.equals(fileUserRelationList.get(i).getUserId(), GlobalStaticConstant.GLOBAL_ORGANINFO_OWNER_USERID)) {
                    hasRight = true;
                    return hasRight;
                }
            }
        } catch (ApplicationException | SQLException ex) {
            logger.error(ex.getCause().getMessage());
        }
        return hasRight;
    }

    /**
     * Get sync params from the database.
     *
     * @return syncParams
     * @throws ApplicationException
     */
    public SyncParams getSyncParams() throws ApplicationException {
        SyncParamsModel syncParamsModel = new SyncParamsModel();
        //get RabbitMQ server infos from the database
        List<SyncParams> syncParamsList = syncParamsModel.querySyncParamsByOrganId("organizationId", GlobalStaticConstant.GLOBAL_ORGANINFO_ORGANIZATIONID);
        SyncParams syncParams = syncParamsList.get(0);
        return syncParams;
    }

    public void setFileDetailController(FileDetailController fileDetailController) {
        this.fileDetailController = fileDetailController;
    }
}
