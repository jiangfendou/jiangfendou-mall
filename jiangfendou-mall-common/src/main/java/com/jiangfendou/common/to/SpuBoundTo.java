package com.jiangfendou.common.to;

import java.math.BigDecimal;
import lombok.Data;

/**
 * @author jiangmh
 */
@Data
public class SpuBoundTo {

    private Long spuId;
    private BigDecimal buyBounds;
    private BigDecimal growBounds;
}
