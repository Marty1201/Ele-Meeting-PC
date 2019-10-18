package com.chinaunicom.elemeetingpc.utils;

import com.chinaunicom.elemeetingpc.constant.GlobalStaticConstant;
import com.chinaunicom.elemeetingpc.database.models.SyncParams;
import com.chinaunicom.elemeetingpc.modelFx.SyncParamsModel;
import com.chinaunicom.elemeetingpc.utils.exceptions.ApplicationException;
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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

/**
 * A RabbitMQ plugin object take care of syn between clients and server.
 *
 * @author chenxi 创建时间：2019-10-16 16:40:57
 */
public class MQPlugin {

    private static final String EXCHANGE_NAME = "topic";

    private static Connection connection;

    private static Channel channel;

    private static ConnectionFactory factory;

    private static String routingKey;

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
        SyncParams syncParams = new SyncParams();
        syncParams = getSyncParams();
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
     * This method consume a message from the exchange.
     *
     * @throws java.io.IOException
     */
    public void consumeMessage() throws IOException {
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, EXCHANGE_NAME, routingKey);
        System.out.println(" [*] Waiting for messages.");
        boolean autoAck = false; //set false to manual ack, set true to auto ack
        channel.basicConsume(queueName, autoAck, "consumerTag", new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag,
                    Envelope envelope,
                    AMQP.BasicProperties properties,
                    byte[] body)
                    throws IOException {
                String routingKey = envelope.getRoutingKey();
                String contentType = properties.getContentType();
                long deliveryTag = envelope.getDeliveryTag();
                //do stuff here
                String message = new String(body, "UTF-8");
                System.out.println(" [x] Received '" + routingKey + "':'" + message + "'");
                channel.basicAck(deliveryTag, false);
            }
        });
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
        List<SyncParams> syncParamsList = new ArrayList<>();
        SyncParams syncParams = new SyncParams();
        SyncParamsModel syncParamsModel = new SyncParamsModel();
        //get RabbitMQ server infos from the database
        syncParamsList = syncParamsModel.querySyncParamsByOrganId("organizationId", GlobalStaticConstant.GLOBAL_ORGANINFO_ORGANIZATIONID);
        syncParams = syncParamsList.get(0);
        return syncParams;
    }
}
