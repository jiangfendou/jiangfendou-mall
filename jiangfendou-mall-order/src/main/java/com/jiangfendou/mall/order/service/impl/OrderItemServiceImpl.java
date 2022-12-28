package com.jiangfendou.mall.order.service.impl;

import com.jiangfendou.mall.order.entity.OrderEntity;
import com.jiangfendou.mall.order.entity.OrderReturnReasonEntity;
import com.rabbitmq.client.Channel;
import java.io.IOException;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import java.util.Map;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiangfendou.common.utils.PageUtils;
import com.jiangfendou.common.utils.Query;

import com.jiangfendou.mall.order.dao.OrderItemDao;
import com.jiangfendou.mall.order.entity.OrderItemEntity;
import com.jiangfendou.mall.order.service.OrderItemService;


@RabbitListener(queues = {"hello-java-queue"})
@Service("orderItemService")
public class OrderItemServiceImpl extends ServiceImpl<OrderItemDao, OrderItemEntity> implements OrderItemService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<OrderItemEntity> page = this.page(
            new Query<OrderItemEntity>().getPage(params),
            new QueryWrapper<OrderItemEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 参数可以写一下内容
     * 1、org.springframework.amqp.core.Message;
     * 2、T<消息类型> OrderReturnReasonEntity orderReturnReasonEntity
     * 3、channel 当前传输数据的通道
     *
     * Queue：可以很多人监听，但是只有一个人会得到消息，只要有人收到消息，就会删除消息。
     *
     * @RabbitListener
     * 可以标注在 类 + 方法上 （监听哪些队列）
     * @RabbitHandler
     * 可以标注在 方法上 （重载区分不同的消息）
     * */
    @RabbitHandler
    public void receiveMessage(Message message,
                               OrderReturnReasonEntity orderReturnReasonEntity,
                               Channel channel) throws IOException {
        System.out.println("接收到的消息1：" + message + "内容：" + orderReturnReasonEntity);

        // channel 内按顺序自增的
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        System.out.println(deliveryTag);
        channel.basicAck(deliveryTag, false);

        // 退货 requeue = false 丢弃; requeue = true 发回服务器，消息重新入对
        // long deliveryTag, boolean multiple, boolean requeue

//        channel.basicNack(deliveryTag, false, false);
    }

    @RabbitHandler
    public void receiveMessage(Message message,
                               OrderEntity orderEntity,
                               Channel channel) throws IOException {
        System.out.println("接收到的消息2：" + message + "内容：" + orderEntity);
        // channel 内按顺序自增的
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        System.out.println(deliveryTag);
        channel.basicAck(deliveryTag, false);
    }

}