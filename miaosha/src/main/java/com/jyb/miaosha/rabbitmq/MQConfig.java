package com.jyb.miaosha.rabbitmq;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;


/**
 * 配置bean
 * @author jyb
 * @since 2021-05-27 14:50
 */
@Configuration
public class MQConfig {

    public static final String MIAOSHA_QUEUE="miaosha.queue";
    public static final String QUEUE="queue";
    public static final String TOPIC_QUEUE1="topic.queue1";
    public static final String TOPIC_QUEUE2="topic.queue2";
    public static final String HEADERS_QUEUE="headers.queue";
    public static final String TOPIC_EXCHANGE="topic.exchange";
    public static final String ROUNTING_KEY1="topic.key1";
    public static final String ROUNTING_KEY2="topic.#";
    public static final String FANOUT_EXCHANGE="fanout.exchange";
    public static final String HEADERS_EXCHANGE="headers.exchange";


    /**
     * Direct模式 交换机Exchange
     */
//    配置队列
    @Bean
    public Queue queue(){
        return new Queue(QUEUE,true);
    }

    @Bean
    public Queue miaoshaQueue()
    {
        return new Queue(MIAOSHA_QUEUE,true);
    }


    /**
     * Topic模式 交换机Exchange
     */
    @Bean
    public Queue topicQueue1(){
        return new Queue(TOPIC_QUEUE1,true);
    }
    @Bean
    public Queue topicQueue2(){
        return new Queue(TOPIC_QUEUE2,true);
    }
    @Bean
    public TopicExchange topicExchange(){
        return new TopicExchange(TOPIC_EXCHANGE);
    }
    @Bean
    public Binding topicBinding1(){
        return BindingBuilder.bind(topicQueue1()).to(topicExchange()).with(ROUNTING_KEY1);
    }
    @Bean
    public Binding topicBinding2(){
        return BindingBuilder.bind(topicQueue2()).to(topicExchange()).with(ROUNTING_KEY2);
    }


    /**
     * Fanout模式，交换机exchange
     */
    @Bean
    public FanoutExchange fanoutExchange(){
        return new FanoutExchange(FANOUT_EXCHANGE);
    }
    @Bean
    public Binding fanoutBinding1(){
        return BindingBuilder.bind(topicQueue1()).to(fanoutExchange());
    }
    @Bean
    public Binding fanoutBinding2(){
        return BindingBuilder.bind(topicQueue2()).to(fanoutExchange());
    }

    /**
     * Header模式，交换机exchange
     */
    @Bean
    public HeadersExchange headersExchange(){
        return new HeadersExchange(HEADERS_EXCHANGE);
    }
    @Bean
    public Queue headersQueue(){
        return new Queue(HEADERS_QUEUE,true);
    }
    @Bean
    public Binding headerBinding(){
        Map<String,Object> map=new HashMap<>();
        map.put("header1","value1");
        map.put("header2","value2");
        return BindingBuilder.bind(headersQueue()).to(headersExchange()).whereAll(map).match();
    }

}
