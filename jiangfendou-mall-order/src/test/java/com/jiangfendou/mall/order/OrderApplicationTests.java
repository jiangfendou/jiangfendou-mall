package com.jiangfendou.mall.order;

import com.jiangfendou.mall.order.entity.OrderReturnReasonEntity;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
class OrderApplicationTests {

    @Autowired
    AmqpAdmin amqpAdmin;

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Test
    void createExchange() {

        amqpAdmin.declareExchange(new DirectExchange("hello-java-exchange", true, false));
        log.info("交换机创建完成");

    }

    @Test
    void createQueue() {

        amqpAdmin.declareQueue(new Queue("hello-java-queue", true, false, false));
        log.info("队列创建完成");
    }

    @Test
    void createBinding() {

        amqpAdmin.declareBinding(new Binding("hello-java-queue",
            Binding.DestinationType.QUEUE,
            "hello-java-exchange", "hello.java", null));
        log.info("绑定创建完成");
    }

    @Test
    void sendMessageTest() {
        OrderReturnReasonEntity orderReturnReasonEntity = new OrderReturnReasonEntity();
        orderReturnReasonEntity.setId(1L);
        orderReturnReasonEntity.setName("2222");
        orderReturnReasonEntity.setSort(2);
        rabbitTemplate.convertAndSend("hello-java-exchange", "hello.java", orderReturnReasonEntity);
        log.info("消息发送完成");
    }

}
