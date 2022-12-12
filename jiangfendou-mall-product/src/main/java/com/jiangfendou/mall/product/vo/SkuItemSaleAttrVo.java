package com.jiangfendou.mall.product.vo;

import java.util.List;
import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SkuItemSaleAttrVo {

    private Long attrId;

    private String attrName;

    private List<AttrValueWithSkuIdVo> attrValues;

}
