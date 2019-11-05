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
     * This method initialize the connectionFactory.
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
            //there is a whole section on connection recovery. make sure you read them
            // connection that will recover automatically
            factory.setAutomaticRecoveryEnabled(true);
            // attempt recovery every 10 seconds
            factory.setNetworkRecoveryInterval(10000);
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
        System.out.println(" [x] Sent '" + message + "'");
    }

    /**
     * This method consume a message from the exchange, note once start
     * consuming, it will continue consuming message only when the the app exit
     * or close, that is all the incoming message will be auto handled by the
     * handleDelivery method, therefore two constants are used here to determine
     * whether the message will be handled or discard, understand this mechanism
     * is vital to understand how the consuming message works here.
     *
     * @throws java.io.IOException
     */
    public void consumeMessage() throws IOException {
        //only the state of following is true, then we start to handle the message, otherwise do nothing and ignore any message
        if (GlobalStaticConstant.GLOBAL_ISFOLLOWINGCLICKED) {
            //System.out.println(" [*] Waiting for messages.");
            DialogsUtils.infoAlert("MQPlugin.followingStart");
            boolean autoAck = false; //set false to manual ack, set true to auto ack
            GlobalStaticConstant.GLOBAL_ISSTARTCONSUMING = true; //set start consuming flag true to avoid the error of redeploy this method
            //channel.basicConsume(queueName, autoAck, "consumerTag", new DefaultConsumer(channel) {
            channel.basicConsume(queueName, autoAck, new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag,
                        Envelope envelope,
                        AMQP.BasicProperties properties,
                        byte[] body)
                        throws IOException {
                    String routingKey = envelope.getRoutingKey();
                    String contentType = properties.getContentType();
                    long deliveryTag = envelope.getDeliveryTag();
                    //message content
                    String message = new String(body, "UTF-8");
                    //System.out.println(" [x] Received '" + routingKey + "':'" + message + "'");
                    channel.basicAck(deliveryTag, false);
                    try {
                        handleMessage(message);
                    } catch (ApplicationException | SQLException ex) {
                        logger.error(ex.getCause().getMessage());
                    }
                }
            });
        }
    }

    /**
     * This method handles the particular message content, it's done by first
     * checking if user indeed choose to start following and the consumer had
     * really started consuming message, then it comparing organization name,
     * command, userId, fileId from the message content with the login user info
     * in a orderly fashion to determine what action should be taken to respond
     * the message.
     *
     * @param message
     * @throws
     * com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException
     * @throws java.sql.SQLException
     */
    public void handleMessage(String message) throws ApplicationException, SQLException {
        //only when user choosed start following and the consumer started to consume, then we start to handle the message, otherwise do nothing and ignore any message
        if (GlobalStaticConstant.GLOBAL_ISFOLLOWINGCLICKED && GlobalStaticConstant.GLOBAL_ISSTARTCONSUMING) {
            //handle JSONObject
            JSONObject jSONObject = JSONObject.fromObject(message);
            //1st check whether the user should handle the message by comparing organization names
            if (StringUtils.equalsIgnoreCase(jSONObject.getString("PAMQOrganizationIDName"), GlobalStaticConstant.GLOBAL_ORGANINFO_ORGANIZATIONNAME)) {
                //2nd check if the message contain the key word "scale", if it does, discard this message(sync zooming ability won't be implemented)
                if (!jSONObject.containsKey("scale")) {
                    //3rd check what the user should do with the message by checking the command field
                    if (StringUtils.equalsIgnoreCase(jSONObject.getString("command"), "turnPage")) {
                        //4th check whether the user has the right to act on the file by querying FileUserRelationModel using fileId and
                        //comparing userIds
                        if (StringUtils.equalsIgnoreCase(getUserIdByFileId(jSONObject.getString("bookid")), GlobalStaticConstant.GLOBAL_ORGANINFO_OWNER_USERID)) {
                            //5th check if same file, turn page straight away
                            if (StringUtils.equalsIgnoreCase(jSONObject.getString("bookid"), fileDetailController.getFileInfo().getFileId())) {
                                //Important note：in javafx, when a FX thread try to modify UI component other than the application thread, must alway use Platform.runLater
                                //eg. here the RabbitMQ thread try to modify the application UI, need to wait for RabbitMQ thread finish, than start application's thread
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        fileDetailController.getOpenPdfViewer().goToCurrentPage(jSONObject.getInt("page"));
                                    }
                                });
                            } else { //method handles speaker and follower are reading different files
                                handleDiffFile(jSONObject);
                            }
                            //System.out.println("Receive turnPage command and turn page successfully!");
                        }
                    }
                    if (StringUtils.equalsIgnoreCase(jSONObject.getString("command"), "applyForPresenter")) {
                        //do nothing
                        //System.out.println("Receive applyForPresenter command!");
                    }
                    //and so on, eg vote, sign etc...
                }
            }
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
        //Important note：in javafx, when a FX thread try to modify UI component other than the application thread, must alway use Platform.runLater
        //eg. here the RabbitMQ thread try to modify the application UI, need to wait for RabbitMQ thread finish, than start application's thread
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
     * Query fileUserRelationModel with fileId and get the corresponding userId.
     *
     * @param fileId
     * @return userId
     */
    public String getUserIdByFileId(String fileId) {
        String userId = "";
        try {
            FileUserRelationModel fileUserRelationModel = new FileUserRelationModel();
            List<FileUserRelation> fileUserRelationList = fileUserRelationModel.queryFileUserRelationByFileId(fileId);
            userId = fileUserRelationList.get(0).getUserId();
            return userId;
        } catch (ApplicationException | SQLException ex) {
            logger.error(ex.getCause().getMessage());
        }
        return userId;
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
        channel.close();
        connection.close();
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
