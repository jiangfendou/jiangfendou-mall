package com.jiangfendou.mall.ware.vo;

import java.util.List;
import lombok.Data;

@Data
public class WareSkuLockVo {

    private String orderSn;

    /** 需要锁住的所有库存信息 **/
    private List<OrderItemVo> locks;

}
