package com.jiangfendou.mall.order.dao;

import com.jiangfendou.mall.order.entity.OrderItemEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单项信息
 *
 * @author jiangfendou
 * @email 49323245@qq.com
 * @date 2022-05-09 21:39:35
 */
@Mapper
public interface OrderItemDao extends BaseMapper<OrderItemEntity> {

}
