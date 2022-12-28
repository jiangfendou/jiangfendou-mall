package com.jiangfendou.mall.order.vo;

import java.math.BigDecimal;
import lombok.Data;


@Data
public class FareVo {

    private MemberAddressVo address;

    private BigDecimal fare;

}
