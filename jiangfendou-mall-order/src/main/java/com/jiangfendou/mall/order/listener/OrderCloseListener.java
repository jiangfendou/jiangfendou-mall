package com.jiangfendou.mall.order.listener;

import com.jiangfendou.mall.order.entity.OrderEntity;
import com.jiangfendou.mall.order.service.OrderService;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;


@RabbitListener(queues = "order.release.order.queue")
@Service
public class OrderCloseListener {

    @Autowired
    private OrderService orderService;

    @RabbitHandler
    public void listener(OrderEntity orderEntity, Channel channel, Message message) throws IOException {
        try {
            orderService.closeOrder(orderEntity);
            channel.basicAck(message.getMessageProperties().getDeliveryTag(),false);
        } catch (Exception e) {
            e.printStackTrace();
            channel.basicReject(message.getMessageProperties().getDeliveryTag(),true);
        }
        System.out.println("就这么就结束了？？？？？？");
    }

}
