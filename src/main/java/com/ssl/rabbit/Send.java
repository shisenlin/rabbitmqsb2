package com.ssl.rabbit;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.MessageProperties;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.KeyPair;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by ssl on 2017/6/30.
 */
@RestController
public class Send {

    /**
     * 向RabbitMQ发送序列化后对象
     *
     * @return
     * @throws Exception
     */
    @RequestMapping("/send")
    public String send() throws Exception {
        Connection connection = CommonUtils.getConnection();
        //获取通道
        Channel channel = connection.createChannel();
        //:创建一个可持久化消息的队列
        Map<String, Object> args = new HashMap<String, Object>();
        args.put("x-max-length", 3000000);//队列最大消息为2
        channel.queueDeclare(CommonUtils.QUEUE_NAME, true, false, false, args);
        String passObject = getPassObject();
        channel.basicPublish("", CommonUtils.QUEUE_NAME,
                MessageProperties.PERSISTENT_TEXT_PLAIN,
                passObject.getBytes("UTF-8"));
        channel.close();
        connection.close();
        return " [x] Send: '" + passObject + "'";
    }

    /**
     * 将对象转JSON
     *
     * @return
     */
    private String getPassObject() {
        PassObject passObject = new PassObject();
        passObject.setId("this is id");
        KeyPair keyPair = Pkcs10Generator.generateKeyPair();
        String csr = Pkcs10Generator.generateCertificationRequest(keyPair);
        passObject.setKeyPair(csr);
        return JSON.toJSONString(passObject);
    }
}
