package com.jyb.miaosha.rabbitmq;

import com.jyb.miaosha.redis.RedisService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author jyb
 * @since 2021-05-27 14:49
 */
@Service
public class MQSender {

    private static Logger log= LoggerFactory.getLogger(MQReceiver.class);
    @Autowired
    AmqpTemplate amqpTemplate;

//    发送秒杀请求
    public void sendMiaoshaMessage(MiaoshaMessage miaoshaMessage) {
        String msg=RedisService.beanToString(miaoshaMessage);
        log.info("send message"+msg);
        amqpTemplate.convertAndSend(MQConfig.MIAOSHA_QUEUE,msg);
    }

//    public void send(Object message){
//        //将消息转化为String类型
//        String msg= RedisService.beanToString(message);
//        log.info("send messages:"+msg);
//        //转发并发送消息
//        amqpTemplate.convertAndSend(MQConfig.QUEUE,msg);
//    }
//
//
//    public void sendTopic(Object message){
//        String msg=RedisService.beanToString(message);
//        log.info("send topic message:"+msg);
//        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE,"topic.key1",msg+"1");
//        amqpTemplate.convertAndSend(MQConfig.TOPIC_EXCHANGE,"topic.key2",msg+"2");
//    }
//
//
//    public void sendFanout(Object message){
//        String msg=RedisService.beanToString(message);
//        log.info("send fanout message:"+msg);
//        amqpTemplate.convertAndSend(MQConfig.FANOUT_EXCHANGE,"",msg);
//    }
//
//
//    public void sendHeaders(Object message){
//        String msg=RedisService.beanToString(message);
//        log.info("send headers message:"+msg);
//        MessageProperties properties=new MessageProperties();
//        properties.setHeader("header1","value1");
//        properties.setHeader("header2","value2");
//        Message obj=new Message(msg.getBytes(),properties);
//        amqpTemplate.convertAndSend(MQConfig.HEADERS_EXCHANGE,"",obj);
//    }


}
