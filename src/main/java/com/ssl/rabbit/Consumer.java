package com.ssl.rabbit;

import com.alibaba.fastjson.JSON;
import com.rabbitmq.client.*;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * 接收消息
 * Created by ssl on 2017/6/30.
 */
@RestController
public class Consumer {

    @RequestMapping("/consumer")
    public String consumer(HttpServletResponse response) throws Exception {
        Connection connection = CommonUtils.getConnection();
        //获取通道
        final Channel channel = connection.createChannel();
        PassObject passObject = new PassObject();
        //获取队列
        Map<String, Object> args = new HashMap<String, Object>();
        args.put("x-max-length", 3000000);//队列最大消息为2
        channel.queueDeclare(CommonUtils.QUEUE_NAME, true, false, false, args);
        //从队列中获取一条消息
        channel.basicQos(1);

        final CountDownLatch countDownLatch = new CountDownLatch(1);

        com.rabbitmq.client.Consumer consumer = new DefaultConsumer(channel) {
            @Override
            public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)
                    throws IOException {
                String message = new String(body, "UTF-8");

                PassObject pj = JSON.toJavaObject(JSON.parseObject(message), PassObject.class);
                passObject.setKeyPair(pj.getKeyPair());
                passObject.setId(pj.getId());
                //通知RabbitMQ，已消费该条消息
                channel.basicAck(envelope.getDeliveryTag(), false);

                //response.setCharacterEncoding("UTF-8");
                // response.getWriter().print("接收到密钥对：" + passObject[0].getKeyPair() + "'");
                System.out.println("接收到密钥对：'" + pj.getKeyPair() + "'");
                countDownLatch.countDown();
            }
        };
        channel.basicConsume(CommonUtils.QUEUE_NAME, false, consumer);
        countDownLatch.await(1, TimeUnit.SECONDS);//最长等到1秒（1秒内没有收到消息，则关闭通道，主线程结束）
        try {
            //关闭通道
            channel.close();
            connection.close();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
        if (StringUtils.isNotBlank(passObject.getKeyPair())) {
            CommonUtils.KEYPAIR_SIZE.add(passObject.getKeyPair());
            CommonUtils.ID_SIZE.add(passObject.getId());
            return passObject.getKeyPair();
        } else {
            return "未获取到keypair";
        }
    }

    @RequestMapping("/getKeyPairSize")
    public String getKeyPairSize(){
        return "size:"+CommonUtils.KEYPAIR_SIZE.size();
    }
    @RequestMapping("/getIdSize")
    public String getIdSize(){
        return "size:"+CommonUtils.ID_SIZE.size();
    }


}
