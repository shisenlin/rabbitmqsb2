package com.ssl.rabbit;

import com.rabbitmq.client.Connection;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeoutException;

/**
 * Created by ssl on 2017/6/30.
 */
public class CommonUtils {
    public static final String QUEUE_NAME = "object";

    public  static  final Set<String> KEYPAIR_SIZE = new HashSet<String>();
    public  static  final Set<String> ID_SIZE = new HashSet<String>();
    /**
     * 获取RabbitMQ服务连接
     *
     * @return
     */
    public static Connection getConnection() {
        com.rabbitmq.client.ConnectionFactory factory = new com.rabbitmq.client.ConnectionFactory();
        factory.setHost("192.168.1.98");
        factory.setPort(5672);
        factory.setUsername("ssl");
        factory.setPassword("123456");
        Connection connection = null;
        try {
            connection = factory.newConnection();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        return connection;
    }

}
