package com.jiangfendou.common.to;

import java.math.BigDecimal;
import java.util.List;
import lombok.Data;

@Data
public class SkuReductionTo {

    private Long skuId;
    private BigDecimal discount;
    private int countStatus;
    private BigDecimal fullPrice;
    private int fullCount;
    private BigDecimal reducePrice;
    private int priceStatus;
    private List<MemberPrice> memberPrice;

}
