package com.jiangfendou.mall.order.dao;

import com.jiangfendou.mall.order.entity.PaymentInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 支付信息表
 *
 * @author jiangfendou
 * @email 49323245@qq.com
 * @date 2022-05-09 21:39:35
 */
@Mapper
public interface PaymentInfoDao extends BaseMapper<PaymentInfoEntity> {

}
