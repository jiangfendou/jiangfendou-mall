package com.jiangfendou.mall.order.dao;

import com.jiangfendou.mall.order.entity.OrderEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 *
 * @author jiangfendou
 * @email 49323245@qq.com
 * @date 2022-05-09 21:39:35
 */
@Mapper
public interface OrderDao extends BaseMapper<OrderEntity> {

}
