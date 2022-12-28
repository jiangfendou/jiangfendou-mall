package com.jiangfendou.mall.order.vo;

import com.jiangfendou.mall.order.entity.OrderEntity;
import lombok.Data;


@Data
public class SubmitOrderResponseVo {

    private OrderEntity order;

    /** 错误状态码 **/
    private Integer code;


}
