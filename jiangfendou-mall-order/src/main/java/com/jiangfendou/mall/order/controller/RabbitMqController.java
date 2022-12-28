package com.jiangfendou.mall.order.controller;

import com.jiangfendou.mall.order.entity.OrderEntity;
import com.jiangfendou.mall.order.entity.OrderReturnReasonEntity;
import java.util.UUID;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RabbitMqController {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @GetMapping("/sendMsg")
    public String sendMessage(@RequestParam(value = "num", defaultValue = "10") Integer num) {
        for(int i =0; i < num; i ++) {
            if(i % 2 == 0) {
                OrderReturnReasonEntity orderReturnReasonEntity = new OrderReturnReasonEntity();
                orderReturnReasonEntity.setId(1L);
                orderReturnReasonEntity.setName("2222");
                orderReturnReasonEntity.setSort(2);
                rabbitTemplate.convertAndSend("hello-java-exchange",
                    "hello.java",
                    orderReturnReasonEntity,
                    new CorrelationData(UUID.randomUUID().toString()));
            } else {
                OrderEntity orderEntity = new OrderEntity();
                orderEntity.setOrderSn(UUID.randomUUID().toString());
                rabbitTemplate.convertAndSend("hello-java-exchange",
                    "hello.java",
                    orderEntity,
                    new CorrelationData(UUID.randomUUID().toString()));
            }
        }
        return "ok";
    }
}
