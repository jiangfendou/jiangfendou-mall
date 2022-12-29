package com.jiangfendou.mall.ware.config;

import java.util.HashMap;
import javax.annotation.PostConstruct;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Exchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.ReturnedMessage;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MyRabbitConfig {

    @Autowired
    RabbitTemplate rabbitTemplate;

    /**
     * 定制RabbitTemplate
     * 1、服务收到消息就会回调
     *      1、spring.rabbitmq.publisher-confirms: true
     *      2、设置确认回调
     * 2、消息正确抵达队列就会进行回调
     *      1、spring.rabbitmq.publisher-returns: true
     *         spring.rabbitmq.template.mandatory: true
     *      2、设置确认回调ReturnCallback
     *
     * 3、消费端确认(保证每个消息都被正确消费，此时才可以broker删除这个消息)
     *      1、默认是自动确认的，只要消息接收到，客户端会自动确认，服务端就会移除这个消息。（自动ACK机制）
     *      问题：
     *          我们收到很多消息，自动回复给服务器ack，只有一个消息处理成功，宕机了。发生消息丢失现象。
     *      2、手动确认模式，只要我们没有明确MQ，货物被签收。没有ACK，消息就直接是unacked状态。即使Consumer宕机消息也不会丢失。
     *      问题：如何签收
     *          channel.basicAck(deliveryTag, false); 签收：业务成功完成就应该签收
     *          channel.basicNack(deliveryTag, false, true); 拒签：业务失败，拒签
     */
    @PostConstruct
    public void initRabbitTemplate() {
        rabbitTemplate.setConfirmCallback(new RabbitTemplate.ConfirmCallback() {
            /**
            * 1、只要消息抵达Broker就ack=true
            * correlationData：当前消息的唯一关联数据(这个是消息的唯一id)
            * ack：消息是否成功收到
            * cause：失败的原因
            */
            //设置确认回调
            @Override
            public void confirm(CorrelationData correlationData, boolean ack, String cause) {
                System.out.println("confirm...correlationData["+correlationData+"]==>" +
                    "ack:["+ack+"]==>cause:["+cause+"]");
            }
        });

        /**
        * 只要消息没有投递给指定的队列，就触发这个失败回调
        * message：投递失败的消息详细信息
        * replyCode：回复的状态码
        * replyText：回复的文本内容
        * exchange：当时这个消息发给哪个交换机
        * routingKey：当时这个消息用哪个路邮键
        */
        rabbitTemplate.setReturnsCallback(new RabbitTemplate.ReturnsCallback() {
            @Override
            public void returnedMessage(ReturnedMessage returnedMessage) {
                System.out.println("Fail Message["+returnedMessage.getMessage()+"]==>replyCode["
                        +returnedMessage.getReplyCode()+"]" +
                    "==>replyText["+returnedMessage.getReplyText()+"]==>exchange["+returnedMessage.getExchange()+
                    "]==>routingKey["+returnedMessage.getRoutingKey()+"]");
            }
        });
    }
//
//    @Primary
//    @Bean
//    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
//        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
//        this.rabbitTemplate = rabbitTemplate;
//        rabbitTemplate.setMessageConverter(messageConverter());
//        initRabbitTemplate();
//        return rabbitTemplate;
//    }

    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public Exchange stockEventExchange() {
        return new TopicExchange("stock-event-exchange", true, false);
    }

    @Bean
    public Queue stockReleaseQueue() {
        //String name, boolean durable, boolean exclusive, boolean autoDelete, Map<String, Object> arguments
        Queue queue = new Queue("stock.release.stock.queue", true, false, false);
        return queue;
    }

    /**
     * 延迟队列
     * @return
     */
    @Bean
    public Queue stockDelay() {

        HashMap<String, Object> arguments = new HashMap<>();
        arguments.put("x-dead-letter-exchange", "stock-event-exchange");
        arguments.put("x-dead-letter-routing-key", "stock.release");
        // 消息过期时间 2分钟
        arguments.put("x-message-ttl", 120000);

        Queue queue = new Queue("stock.delay.queue", true, false, false,arguments);
        return queue;
    }

    /**
     * 交换机与普通队列绑定
     * @return
     */
    @Bean
    public Binding stockReleaseBinding() {
        //String destination, DestinationType destinationType, String exchange, String routingKey,
        // 			Map<String, Object> arguments
        Binding binding = new Binding("stock.release.stock.queue",
            Binding.DestinationType.QUEUE,
            "stock-event-exchange",
            "stock.release.#",
            null);

        return binding;
    }


    /**
     * 交换机与延迟队列绑定
     * @return
     */
    @Bean
    public Binding stockLockedBinding() {
        return new Binding("stock.delay.queue",
            Binding.DestinationType.QUEUE,
            "stock-event-exchange",
            "stock.locked",
            null);
    }

}
