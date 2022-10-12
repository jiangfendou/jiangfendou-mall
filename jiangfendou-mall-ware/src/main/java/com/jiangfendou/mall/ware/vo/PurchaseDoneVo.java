package com.jiangfendou.mall.ware.vo;

import java.util.List;
import lombok.Data;

/**
 * @author jiangmh
 */
@Data
public class PurchaseDoneVo {

    private Long id;

    private List<PurchaseDoneItemVo> purchaseDoneItemVos;

}
